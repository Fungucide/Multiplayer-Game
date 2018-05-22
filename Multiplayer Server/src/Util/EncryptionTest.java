package Util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionTest {

	public static byte[] getTimeDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).getBytes();
	}
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		byte[] pass = new String("Test").getBytes("UTF-8");
		byte[] dateTime = getTimeDate();
		byte[] encrypted = encrypt(dateTime, pass);
		System.out.println(new String(encrypted));
		byte[] decrypted = decrypt(encrypted, pass);
		System.out.println(new String(decrypted));
	}

	public static byte[] encrypt(byte[] pt, byte[] pass) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(pass);
		byte[] hash = Arrays.copyOf(md.digest(), 16);
		Key key = new SecretKeySpec(hash, "AES");
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		return c.doFinal(pt);
	}

	public static byte[] decrypt(byte[] ct, byte[] pass) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		System.out.println(pass.length);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(pass);
		byte[] hash = Arrays.copyOf(md.digest(), 16);
		Key key = new SecretKeySpec(hash, "AES");
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(ct);
	}
}
