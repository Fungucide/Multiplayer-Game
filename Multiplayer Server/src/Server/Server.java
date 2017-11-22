package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
@Deprecated
public class Server {

    private static final int MAXNCONNECTIONS=10;
	
	private static ServerSocket serverSocket;

	public static void main(String[] args) throws IOException {
		serverSocket = new ServerSocket(31001);
		int connections =0;
		while(connections<=MAXNCONNECTIONS){
			System.out.println("Waiting for connection....");
			Socket sock = serverSocket.accept();	
			System.out.println("Begining Conection to:"+sock.getInetAddress().getHostAddress());
			//RemoteProcessServer rps = new RemoteProcessServer(sock);
			System.out.println("Handle object created");
			//Thread t = new Thread(rps);
			//t.start();
			System.out.println("New Thread Created");
		}
	}

	
}
