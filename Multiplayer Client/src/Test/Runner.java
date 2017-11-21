package Test;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import Client.RemoteProcessClient;

public class Runner {

	private final RemoteProcessClient remoteProcessClient;
	private final String token;

	public static void main(String[] args) throws IOException {
		new Runner(args.length == 3 ? args : new String[] { "127.0.0.1", "31001", "Token" }).run();
	}

	private Runner(String[] args) throws IOException {
		remoteProcessClient = new RemoteProcessClient(args[0], Integer.parseInt(args[1]));
		token = args[2];
	}

	public void run() throws IOException {
		try {
			remoteProcessClient.writeTokenMessage(token);
			remoteProcessClient.writeProtocolVersionMessage();
			Scanner s = new Scanner(System.in);

			while (true) {
				String pass = s.nextLine();
				byte[] bytes = pass.getBytes("UTF-8");
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] hash = md.digest(bytes);
				remoteProcessClient.loginRequest("Admin", hash);
				if (remoteProcessClient.loginStatus())
					break;
			}
			while (true) {
				remoteProcessClient.getCharacter();
				remoteProcessClient.moveCharacter(1, 0, 0, false);
				System.out.println(remoteProcessClient.c.getX()+" "+remoteProcessClient.c.getY());
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			remoteProcessClient.close();
		}
	}

}
