package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import request.Request;
import response.Response;

class ServerThread extends Thread{
	private Socket s=null;
	
	public ServerThread(Socket s){
		this.s=s;
	}
	
	@Override
	public void run(){
		BufferedReader br=null;
		PrintStream out=null;
		try{
			br=new BufferedReader(new InputStreamReader(s.getInputStream()));
			Request request=new Request(br);
			String url=request.getURL();
			out=new PrintStream(s.getOutputStream());
			Response response=new Response(out);
			response.sendMessage(url);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
