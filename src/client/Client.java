package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	

	private InputStream inputStream;
	private InputStreamReader inputReader;
	
	private OutputStream outputStream;
	private OutputStreamWriter outputWriter;
	
	private String host;
	private int port;
	private String id;
	private boolean loggedIn;

	public Client(String host, int port, String id) {
		this.host = host;
		this.port = port;
		this.id	  = id;
		
		try {
			socket = new Socket(this.host, this.port);
			
			inputStream = socket.getInputStream();
			inputReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputReader);
			
			outputStream = socket.getOutputStream();
			outputWriter = new OutputStreamWriter(outputStream);
			writer = new BufferedWriter(outputWriter);

			sendString("login:"+this.id);
			while (!socket.isClosed()) {
				
				if (!loggedIn) {
					if (reader.ready()) {
						String s = reader.readLine();
						System.out.println(":>server " + s);
						if (s == "user:891121:doctor") {
							loggedIn = true;
							System.out.println(s);
						}
					}
				} else {
					if (reader.ready()) {
						String s = reader.readLine();
						System.out.println(":>server " + s);
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
	
	public static void main(String[] args) {
		Client t = new Client("localhost", 10000, "891121");
	}
}
