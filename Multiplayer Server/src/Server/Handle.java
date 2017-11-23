package Server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import Framework.Char;
import Framework.Terrain;
import Framework.World;

public class Handle implements Closeable {

	private final String TOKEN;
	private final int PROTOCOL_VERSION;
	private final int COMPRESSION;
	private final int TILE_SIZE;
	private final RemoteProcessServer RPS;

	private static final int BUFFER_SIZE_BYTES = 1 << 20;
	private static final ByteOrder PROTOCOL_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
	private static final int INTEGER_SIZE_BYTES = Integer.SIZE / Byte.SIZE;
	private static final int LONG_SIZE_BYTES = Long.SIZE / Byte.SIZE;

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	private final InputStream inputStream;
	private final OutputStream outputStream;
	private final ByteArrayOutputStream outputStreamBuffer;
	private final Socket SOCKET;

	public Handle(RemoteProcessServer rps, Socket socket, String t, int pv, int c, int ts) throws IOException {
		RPS = rps;
		SOCKET = socket;
		TOKEN = t;
		PROTOCOL_VERSION = pv;
		COMPRESSION = c;
		TILE_SIZE = ts;
		socket.setSendBufferSize(BUFFER_SIZE_BYTES);
		socket.setReceiveBufferSize(BUFFER_SIZE_BYTES);
		socket.setTcpNoDelay(true);

		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		outputStreamBuffer = new ByteArrayOutputStream(BUFFER_SIZE_BYTES);

	}

	public boolean verifyToken() throws IOException {
		ensureMessageType(readEnum(MessageType.class), MessageType.AUTHENTICATION_TOKEN);
		if (readString().equals(TOKEN))
			return true;
		else
			return false;
	}

	public boolean verifyProtocolVersion() throws IOException {
		ensureMessageType(readEnum(MessageType.class), MessageType.PROTOCOL_VERSION);
		if (readInt() == PROTOCOL_VERSION)
			return true;
		else
			return false;
	}

	public void writeGraphic() throws IOException {
		writeEnum(MessageType.GRAPHIC_DATA);
		writeInt(TILE_SIZE);
		writeInt(COMPRESSION);
		flush();
	}

	public void waitForLogin() throws IOException {
		while (true) {
			ensureMessageType(readEnum(MessageType.class), MessageType.LOGIN_REQUEST);
			String user = readString();
			byte[] pass = readByteArray(false);
			File f = new File("Data/Player/" + user + "/Password.pass");
			if (!f.exists()) {
				writeLoginStatus(false);
				continue;
			}
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			if (new String(pass).equals(br.readLine())) {
				RPS.connection.USERNAME = user;
				writeLoginStatus(true);
				break;
			} else {
				writeLoginStatus(false);
			}
		}
	}

	public void writeLoginStatus(boolean status) throws IOException {
		writeEnum(MessageType.LOGIN_REQUEST);
		writeBoolean(status);
		flush();
	}

	public void writeCharacter() throws IOException {
		writeEnum(MessageType.CHARACTER_DATA);
		writeInt(RPS.c.getX());
		writeInt(RPS.c.getY());
		writeInt(RPS.c.getMaxHealth());
		writeInt(RPS.c.getHealth());
		writeInt(RPS.c.getAttack());
		writeInt(RPS.c.getMaxMana());
		writeInt(RPS.c.getMana());
		writeInt(RPS.c.getPower());
		writeInt(RPS.c.getSpeed());
		flush();
	}

	public void getCharacterMove() throws IOException {
		ensureMessageType(readEnum(MessageType.class), MessageType.CHARACTER_MOVE);
		RPS.c.move(readInt(), readInt());
		readInt();
		readBoolean();
	}

	public void terraintRequest() throws IOException {
		ensureMessageType(readEnum(MessageType.class), MessageType.TERRAIN_REQUEST);
		int x = readInt();
		int y = readInt();
		int width = readInt();
		int height = readInt();
		writeEnum(MessageType.TERRAIN_REQUEST);
		writeInt(COMPRESSION);
		writeIntArray2D(RPS.c.w.getTerrain(x, y, width, height));
		flush();
	}

	private static void ensureMessageType(MessageType actualType, MessageType expectedType) {
		if (actualType != expectedType) {
			throw new IllegalArgumentException(String.format("Received wrong message [actual=%s, expected=%s].", actualType, expectedType));
		}
	}

	private <E> E[] readArray(Class<E> elementClass, ElementReader<E> elementReader) throws IOException {
		int length = readInt();
		if (length < 0) {
			return null;
		}

		@SuppressWarnings("unchecked")
		E[] array = (E[]) Array.newInstance(elementClass, length);

		for (int i = 0; i < length; ++i) {
			array[i] = elementReader.read();
		}

		return array;
	}

	private <E> void writeArray(E[] array, ElementWriter<E> elementWriter) throws IOException {
		if (array == null) {
			writeInt(-1);
		} else {
			int length = array.length;
			writeInt(length);

			for (int i = 0; i < length; ++i) {
				elementWriter.write(array[i]);
			}
		}
	}

	private byte[] readByteArray(boolean nullable) throws IOException {
		int count = readInt();

		if (count <= 0) {
			return nullable && count < 0 ? null : EMPTY_BYTE_ARRAY;
		}

		return readBytes(count);
	}

	private void writeByteArray(byte[] array) throws IOException {
		if (array == null) {
			writeInt(-1);
		} else {
			writeInt(array.length);
			writeBytes(array);
		}
	}

	private <E extends Enum> E readEnum(Class<E> enumClass) throws IOException {
		byte ordinal = readByte();

		E[] values = enumClass.getEnumConstants();
		return ordinal >= 0 && ordinal < values.length ? values[ordinal] : null;
	}

	@SuppressWarnings("SubtractionInCompareTo")
	private <E extends Enum> E[] readEnumArray(Class<E> enumClass, int count) throws IOException {
		byte[] bytes = readBytes(count);
		@SuppressWarnings("unchecked")
		E[] array = (E[]) Array.newInstance(enumClass, count);

		E[] values = enumClass.getEnumConstants();
		int valueCount = values.length;

		for (int i = 0; i < count; ++i) {
			byte ordinal = bytes[i];

			if (ordinal >= 0 && ordinal < valueCount) {
				array[i] = values[ordinal];
			}
		}

		return array;
	}

	private <E extends Enum> E[] readEnumArray(Class<E> enumClass) throws IOException {
		int count = readInt();
		if (count < 0) {
			return null;
		}

		return readEnumArray(enumClass, count);
	}

	@SuppressWarnings("unchecked")
	private <E extends Enum> E[][] readEnumArray2D(Class<E> enumClass) throws IOException {
		int count = readInt();
		if (count < 0) {
			return null;
		}

		E[][] array;
		try {
			array = (E[][]) Array.newInstance(Class.forName("[L" + enumClass.getName() + ';'), count);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Can't load array class for " + enumClass + '.', e);
		}

		for (int i = 0; i < count; ++i) {
			array[i] = readEnumArray(enumClass);
		}

		return array;
	}

	private <E extends Enum> void writeEnum(E value) throws IOException {
		writeByte(value == null ? -1 : value.ordinal());
	}

	private String readString() throws IOException {
		int length = readInt();
		if (length == -1) {
			return null;
		}

		return new String(readBytes(length), StandardCharsets.UTF_8);
	}

	private void writeString(String value) throws IOException {
		if (value == null) {
			writeInt(-1);
			return;
		}

		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

		writeInt(bytes.length);
		writeBytes(bytes);
	}

	private boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	private boolean[] readBooleanArray(int count) throws IOException {
		byte[] bytes = readBytes(count);
		boolean[] array = new boolean[count];

		for (int i = 0; i < count; ++i) {
			array[i] = bytes[i] != 0;
		}

		return array;
	}

	private boolean[] readBooleanArray() throws IOException {
		int count = readInt();
		if (count < 0) {
			return null;
		}

		return readBooleanArray(count);
	}

	private boolean[][] readBooleanArray2D() throws IOException {
		int count = readInt();
		if (count < 0) {
			return null;
		}

		boolean[][] array = new boolean[count][];

		for (int i = 0; i < count; ++i) {
			array[i] = readBooleanArray();
		}

		return array;
	}

	private void writeBoolean(boolean value) throws IOException {
		writeByte(value ? 1 : 0);
	}

	private int readInt() throws IOException {
		return ByteBuffer.wrap(readBytes(INTEGER_SIZE_BYTES)).order(PROTOCOL_BYTE_ORDER).getInt();
	}

	private int[] readIntArray(int count) throws IOException {
		byte[] bytes = readBytes(count * INTEGER_SIZE_BYTES);
		int[] array = new int[count];

		for (int i = 0; i < count; ++i) {
			array[i] = ByteBuffer.wrap(bytes, i * INTEGER_SIZE_BYTES, INTEGER_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).getInt();
		}

		return array;
	}

	private int[] readIntArray() throws IOException {
		int count = readInt();
		if (count < 0) {
			return null;
		}

		return readIntArray(count);
	}

	private int[][] readIntArray2D() throws IOException {
		int x = readInt();
		int y = readInt();

		int[][] array = new int[x][y];

		for (int i = 0; i < x; ++i) {
			for (int j = 0; j < y; j++) {
				array[i][j] = readInt();
			}
		}

		return array;
	}

	private void writeIntArray2D(int[][] value) throws IOException {
		writeInt(value.length);
		writeInt(value[0].length);
		for (int i = 0; i < value.length; i++) {
			for (int j = 0; j < value[0].length; j++) {
				writeInt(value[i][j]);
			}
		}
	}

	private void writeInt(int value) throws IOException {
		writeBytes(ByteBuffer.allocate(INTEGER_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).putInt(value).array());
	}

	private long readLong() throws IOException {
		return ByteBuffer.wrap(readBytes(LONG_SIZE_BYTES)).order(PROTOCOL_BYTE_ORDER).getLong();
	}

	private void writeLong(long value) throws IOException {
		writeBytes(ByteBuffer.allocate(LONG_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).putLong(value).array());
	}

	private double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	private void writeDouble(double value) throws IOException {
		writeLong(Double.doubleToLongBits(value));
	}

	private byte[] readBytes(int byteCount) throws IOException {
		byte[] bytes = new byte[byteCount];
		int offset = 0;
		int readByteCount;

		while (offset < byteCount && (readByteCount = inputStream.read(bytes, offset, byteCount - offset)) != -1) {
			offset += readByteCount;
		}

		if (offset != byteCount) {
			throw new IOException(String.format("Can't read %d bytes from input stream.", byteCount));
		}

		return bytes;
	}

	private void writeBytes(byte[] bytes) throws IOException {
		outputStreamBuffer.write(bytes);
	}

	private byte readByte() throws IOException {
		int value = inputStream.read();

		if (value == -1) {
			throw new IOException("Can't read a byte from input stream.");
		}

		return (byte) value;
	}

	private void writeByte(int value) throws IOException {
		try {
			outputStreamBuffer.write(value);
		} catch (RuntimeException e) {
			throw new IOException("Can't write a byte into output stream.", e);
		}
	}

	private void flush() throws IOException {
		outputStream.write(outputStreamBuffer.toByteArray());
		outputStreamBuffer.reset();
		outputStream.flush();
	}

	private interface ElementReader<E> {
		E read() throws IOException;
	}

	private interface ElementWriter<E> {
		void write(E element) throws IOException;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			SOCKET.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
