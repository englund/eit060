package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import server.JournalEntry;

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
	
	private static final int TYPE_GOV 		= 0;
	private static final int TYPE_PATIENT 	= 1;
	private static final int TYPE_NURSE 	= 2;
	private static final int TYPE_DOCTOR 	= 3;

	public Client(String host, int port) {
		this.host	= host;
		this.port	= port;
		
		try {
			socket = new Socket(this.host, this.port);
			
			inputStream = socket.getInputStream();
			inputReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputReader);
			
			outputStream = socket.getOutputStream();
			outputWriter = new OutputStreamWriter(outputStream);
			writer = new BufferedWriter(outputWriter);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public String authenticate(int utype, String uid) {
		if (sendString("login:" + utype + ":" + uid)) { // login:type:id
			String s = waitForString();
			String[] cmd = parseCommand(s); // förväntar oss type:id
			String id = cmd[1];
			if (!id.equals("null")) { // användaren autentiserad och har behörighet!
				return id;
			}
		}
		return null;
	}

	public ArrayList<JournalEntry> getAll(String id) {
		if (sendString("getAll:"+id)) {
			String s = waitForString(); // (date:doctorId:nurseId:hospital:unit:content)+
			String[] j = parseCommand(s);
			
			ArrayList<JournalEntry> journals = new ArrayList<JournalEntry>();
			for (int i = 0; i < j.length; i += 6) {
				String date = j[i];
				String doctorId = j[i+1];
				String nurseId = j[i+2];
				String hospital = j[i+3];
				String unit = j[i+4];
				String content = j[i+5];
				journals.add(new JournalEntry(date, doctorId, nurseId, hospital, unit, content));
			}
			return journals;
		}
		return null;
	}
	
	public String[] parseCommand(String s) {
		return s.split(":");
	}
	
	public String waitForString() {
		while (!socket.isClosed()) {
			try {
				if (reader.ready()) {
					return reader.readLine().replace("\n", "");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public boolean sendString(String s) {
		try {
			writer.write(s +"\n");
			writer.flush();
			outputWriter.flush();
			outputStream.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void close() {
		try {
			reader.close();
			inputReader.close();
			inputStream.close();

			writer.close();
			outputWriter.close();
			outputStream.close();
			
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client("localhost", 10000);
		
		/*
		 * Logga in som Julia, patient.
		 */
		String id = client.authenticate(TYPE_PATIENT, "900401");
		if (id != null) { // sucessfull login!
			System.out.println(":>client User authenticated as " + id + ".");
			
			// Hämta alla hennes journalentries
			ArrayList<JournalEntry> journals = client.getAll(id);
			for (int i = 0; i < journals.size(); i++) {
				JournalEntry entry = journals.get(i);
				System.out.println(i + ". " + entry.getDate() + " " + entry.getHospital() + " " + entry.getDoctorId());
			}
		} else {
			System.out.println("Failed to authenticate the user!");
		}
		
		client.close();
	}
}
