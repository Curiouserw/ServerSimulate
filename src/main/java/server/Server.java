package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server{
	private static int PORT=9999;
	
	@SuppressWarnings("resource")
	public void receive(){
		try{
			ServerSocket ss=new ServerSocket(PORT);
			while(true){
				new ServerThread(ss.accept()).start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		new Server().receive();
	}
}
