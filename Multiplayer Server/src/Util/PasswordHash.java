package Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		FileWriter fw = new FileWriter(new File("Password.pass"));
		String pass = "Admin";
		byte[] bytes = pass.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] thedigest = md.digest(bytes);
		BigInteger foo = new BigInteger(thedigest);
		String hashed = foo.toString(16);
		System.out.println(hashed);
		fw.write(hashed);
		fw.close();
	}

}
