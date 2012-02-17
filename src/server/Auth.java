package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Auth extends Thread {
	
	private int port = 10000;
	
	private ServerSocket server;
	private BufferedReader reader;
	private BufferedWriter writer;
	

	private InputStream inputStream;
	private InputStreamReader inputReader;
	
	private OutputStream outputStream;
	private OutputStreamWriter outputWriter;
	
	public void run() {
		try {
			server = new ServerSocket(port);
			
			System.out.println("Waiting for connection...");
			Socket socket = server.accept();
			
			String client = socket.getInetAddress().getHostName();
			System.out.println("Connection received from " + client);
	
			inputStream = socket.getInputStream();
			inputReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputReader);
			
			outputStream = socket.getOutputStream();
			outputWriter = new OutputStreamWriter(outputStream);
			writer = new BufferedWriter(outputWriter);
			
			while (!socket.isClosed()) {
				if (reader.ready()) {
					String cmd = reader.readLine();
					System.out.println(":>client " + cmd);
					if (cmd.startsWith("login:891121")) {
						System.out.println(":>server sending..");
						sendString("user:891121:doctor");
					}
				}
			}
			
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void sendString(String s) {
		try {
			writer.write(s +"\n");
			writer.flush();
			outputWriter.flush();
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isCommand(byte[] data){
		if (data.length == 16){
			return true;
		}
		return false;	
		
		
	}
	
	private void commandParser(byte[] data) {
		char command = (char) (data[0]);
		System.out.println("command:"+command);
	}
	
    public static void main(String[] args){
	    Auth a = new Auth();
	    a.start();
    }
}
