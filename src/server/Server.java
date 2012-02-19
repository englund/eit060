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
import java.util.ArrayList;

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

	private Person user;
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
			

			int type = -1;
			String id = null;
			
			while (!socket.isClosed()) {
				String str = waitForString();
				System.out.println(":>server incoming " + str);
				
				String[] data = parseCommand(str);
				
				if (!loggedIn) {
					if (data[0].equals("login")) { // login:type:id
						type = Integer.parseInt(data[1]);
						id = data[2];
						user = users.getUser(id);
						
						if (acl.isType(user, type)) {
							System.out.println(":>server Authenticated user " + id);
							sendString(type+":"+user.getId()); // type:id
							loggedIn = true;
						} else {
							System.out.println(":>server " + id + " tried to login but it was unsucessfull!");
							sendString("null:null"); // null:null
						}
					}
				} else { // logged in
					if (data[0].equals("getAll")) { // getAll:id
						if (type == TYPE_PATIENT) {
							Patient p = (Patient) user;
							
							p.getJournal().journalPrint();
							ArrayList<JournalEntry> entries = p.getJournal().getEntries();
							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < entries.size(); i++) {
								System.out.println(entries.get(i).toString());
								sb.append(entries.get(i).toString() + ":"); // date:doctorId:nurseId:hospital:unit:content:
							}
							sendString(sb.toString());
						}
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
