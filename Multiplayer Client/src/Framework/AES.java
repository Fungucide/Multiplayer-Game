package Framework;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AES {
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
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(pass);
		byte[] hash = Arrays.copyOf(md.digest(), 16);
		Key key = new SecretKeySpec(hash, "AES");
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, key);
		return c.doFinal(ct);
	}
}
