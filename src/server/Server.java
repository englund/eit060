package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	
	private int port = 10000;
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	
	private InputStream inputStream;
	private InputStreamReader inputReader;
	
	private OutputStream outputStream;
	private OutputStreamWriter outputWriter;
	
	private Users users;

	private boolean loggedIn;
	
	private ACL acl;
	
	private static final int TYPE_GOV 		= 0;
	private static final int TYPE_PATIENT 	= 1;
	private static final int TYPE_NURSE 	= 2;
	private static final int TYPE_DOCTOR 	= 3;
	
	public Server() {
		users = new Users();
		users.fillTestUsers();
	}
	
	public void run() {
		try {
			acl = new ACL();
			
			ServerSocket server = new ServerSocket(port);
			
			System.out.println("Waiting for connection...");
			socket = server.accept();
			
			String client = socket.getInetAddress().getHostName();
			System.out.println("Connection received from " + client);
	
			inputStream = socket.getInputStream();
			inputReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputReader);
			
			outputStream = socket.getOutputStream();
			outputWriter = new OutputStreamWriter(outputStream);
			writer = new BufferedWriter(outputWriter);
			
			loggedIn = false;
			
			while (!socket.isClosed()) {
				String str = waitForString();
				System.out.println(":>server incoming " + str);
				
				String[] data = parseCommand(str);
				Person user = null;
				
				if (!loggedIn) {
					if (data[0].equals("login")) { // login:type:id
						String type = data[1];
						String id = data[2];
						user = users.getUser(id);
						
						if (acl.isType(user, Integer.parseInt(type))) {
							System.out.println(":>server Authenticated user " + id);
							sendString(type+":"+user.getId()); // type:id
						} else {
							System.out.println(":>server " + id + " tried to login but it was unsucessfull!");
							sendString("null:null"); // null:null
						}
					}
				} else { // logged in
					if (data[0].equals("other cmd")) { // cmd:id
						
					}
				}
			}
			
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public String[] parseCommand(String s) {
		return s.split(":");
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
	
	public String waitForString() {
		while (!socket.isClosed()) {
			try {
				if (reader.ready()) {
					return reader.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
    public static void main(String[] args){
    	Server a = new Server();
	    a.start();
    }
}
