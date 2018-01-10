package Server;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
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
import java.util.ArrayList;
import java.util.Base64;

import javax.imageio.ImageIO;

import Framework.Char;
import Framework.Displayable;
import Framework.Sprite;
import Framework.Terrain;
import Log.LogMessageType;

public class Functions implements Closeable {

	private final String TOKEN;
	private final int PROTOCOL_VERSION;
	private final ClientInteractions CI;

	private static final int BUFFER_SIZE_BYTES = 1 << 20;
	private static final ByteOrder PROTOCOL_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
	private static final int INTEGER_SIZE_BYTES = Integer.SIZE / Byte.SIZE;
	private static final int LONG_SIZE_BYTES = Long.SIZE / Byte.SIZE;

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	private final InputStream inputStream;
	private final OutputStream outputStream;
	private final ByteArrayOutputStream outputStreamBuffer;
	private final Socket SOCKET;

	public Functions(ClientInteractions ci, Socket socket, String t, int pv) throws IOException {
		CI = ci;
		SOCKET = socket;
		TOKEN = t;
		PROTOCOL_VERSION = pv;
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
		writeInt(CI.CHARACTER.w.TILE_SIZE);
		writeInt(CI.CHARACTER.w.COMPRESSION);
		writeInt(Char.getCharSize());
		flush();
	}

	public void dataUpdate(boolean update) throws IOException {
		writeEnum(MessageType.DATA_UPDATE);
		writeBoolean(update);
		flush();
	}

	public void writeResources(Sprite[] sprites) throws IOException {
		writeEnum(MessageType.RESOURCE_DATA);
		writeInt(sprites.length);
		for (int i = 0; i < sprites.length; i++) {
			BufferedImage img = toBufferedImage(ImageIO.read(new File(sprites[i].getPath())).getScaledInstance(sprites[i].getWidth(), sprites[i].getHeight(), Image.SCALE_SMOOTH));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			String base64String = new String(Base64.getEncoder().encode(baos.toByteArray()));
			writeString(base64String);
		}
		flush();
	}

	private static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage)
			return (BufferedImage) img;
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	@Deprecated
	public void charGraphicUpdate(boolean update) throws IOException {
		writeEnum(MessageType.CHAR_UPDATE);
		writeBoolean(update);
		flush();
	}

	public void charGraphics(String[] path) throws IOException {
		writeEnum(MessageType.RESOURCE_DATA);
		writeInt(Char.getCharSize());
		writeInt(path.length);
		for (int i = 0; i < path.length; i++) {
			BufferedImage img = toBufferedImage(ImageIO.read(new File(path[i])).getScaledInstance(Char.getCharSize(), Char.getCharSize(), Image.SCALE_SMOOTH));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			String base64String = new String(Base64.getEncoder().encode(baos.toByteArray()));
			writeString(base64String);
		}
		flush();
	}

	public void waitForLogin() throws IOException {
		while (true) {
			ensureMessageType(readEnum(MessageType.class), MessageType.LOGIN_REQUEST);
			String user = readString();
			String pass = readString();
			if (CI.SERVER.active.contains(user)) {
				CI.SERVER.log.log(LogMessageType.DATA, "Player already loged in with same username: " + user);
				writeLoginStatus(false);
				continue;
			}
			File f = new File(Char.PATH + user + "/Password.pass");
			if (!f.exists()) {
				CI.SERVER.log.log(LogMessageType.DATA, "Player does not exist: " + user);
				writeLoginStatus(false);
				continue;
			}
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			if (pass.equals(br.readLine())) {
				CI.SERVER.log.log(LogMessageType.DATA, "Player loged in successfully: " + user);
				CI.connection.USERNAME = user;
				CI.SERVER.active.add(user);
				CI.SERVER.connectionUpdate();
				writeLoginStatus(true);
				break;
			} else {
				CI.SERVER.log.log(LogMessageType.DATA, "Player login rejected: " + user);
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
		writeInt(CI.CHARACTER.getX());
		writeInt(CI.CHARACTER.getY());
		writeInt(CI.CHARACTER.getMaxHealth());
		writeInt(CI.CHARACTER.getHealth());
		writeInt(CI.CHARACTER.getAttack());
		writeInt(CI.CHARACTER.getMaxMana());
		writeInt(CI.CHARACTER.getMana());
		writeInt(CI.CHARACTER.getPower());
		writeInt(CI.CHARACTER.getSpeed());
		writeInt(CI.CHARACTER.getGraphics()[0]);
		writeInt(CI.CHARACTER.getGraphics()[1]);
		flush();
	}

	public void getCharacterMove() throws IOException {
		ensureMessageType(readEnum(MessageType.class), MessageType.CHARACTER_MOVE);
		CI.CHARACTER.move(readInt(), readInt());
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
		writeIntArray2D(displayableArray(CI.CHARACTER.w.getDisplay(x, y, width, height, CI.CHARACTER)));
		flush();
	}

	private int[][] displayableArray(ArrayList<Displayable> al) {
		int[][] array = new int[al.size()][8];// x,y,offset,passable
		for (int i = 0; i < al.size(); i++) {
			array[i][0] = al.get(i).getType();
			array[i][1] = al.get(i).getX();
			array[i][2] = al.get(i).getY();
			array[i][3] = al.get(i).getWidth();
			array[i][4] = al.get(i).getHeight();
			if (al.get(i) instanceof Terrain) {
				array[i][5] = ((Terrain) al.get(i)).getOffSet();
				array[i][6] = ((Terrain) al.get(i)).getGraphics()[0];
				array[i][7] = ((Terrain) al.get(i)).getGraphics()[1];
			} else {
				array[i][5] = 0;
				array[i][6] = al.get(i).getGraphics()[0];
				array[i][7] = al.get(i).getGraphics()[1];
			}
		}
		return array;
	}

	private static void ensureMessageType(MessageType actualType, MessageType expectedType) {
		if (actualType != expectedType) {
			throw new IllegalArgumentException(String.format("Received wrong message [actual=%s, expected=%s].", actualType, expectedType));
		}
	}

	@Deprecated
	public void characterDisplayRequest() throws IOException {
		ensureMessageType(readEnum(MessageType.class), MessageType.CHAR_DISPLAY_REQUEST);
		int tx = readInt();
		int ty = readInt();
		int bx = readInt();
		int by = readInt();
		ArrayList<int[]> al = CI.CHARACTER.w.getRenderData(tx, ty, bx, by, CI.CHARACTER);
		writeEnum(MessageType.CHAR_DISPLAY_DATA);
		writeInt(al.size());
		for (int[] a : al) {
			writeInt(a[0]);
			writeInt(a[1]);
			writeInt(a[2]);
		}
		flush();
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
		if (value.length == 0) {
			writeInt(0);
			return;
		}
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
