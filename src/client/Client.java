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
import server.Patient;

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

	public ArrayList<JournalEntry> getAllEntries(String id) {
		if (sendString("getAllEntries:"+id)) {
			String s = waitForString(); // (date:doctorId:nurseId:hospital:unit:content)+
			String[] j = parseCommand(s);
			
			if (!s.equals("null")) {
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
		}
		return null;
	}

	private ArrayList<Patient> getAllPatients(String id) {
		if (sendString("getAllPatients:"+id)) {
			String s = waitForString(); // (id:name)+
			String[] j = parseCommand(s);
			
			if (!s.equals("null")) {
				ArrayList<Patient> patients = new ArrayList<Patient>();
				for (int i = 0; i < j.length; i += 2) {
					String idStr = j[i];
					String nameStr = j[i+1];
					patients.add(new Patient(idStr, nameStr));
				}
				return patients;
			}
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
		 * Logga in som Victor, doktor.
		 */
		String id = client.authenticate(TYPE_DOCTOR, "d891121");
		if (id != null) { // sucessfull login!
			System.out.println(":>client User authenticated as " + id + ".");
			
			// Hämta alla mina journalentries
			System.out.println("Hämtar alla Victors entries!");
			ArrayList<JournalEntry> journals = client.getAllEntries(id);
			if (journals != null) {
				for (int i = 0; i < journals.size(); i++) {
					JournalEntry entry = journals.get(i);
					System.out.println(i + ". " + entry.getDate() + " " + entry.getHospital() + " " + entry.getDoctorId());
				}
			}

			System.out.println("Hämtar alla patienter jag kan se!");
			ArrayList<Patient> patients = client.getAllPatients(id);
			if (patients != null) {
				for (int i = 0; i < patients.size(); i++) {
					Patient p = patients.get(i);
					System.out.println(i + ". " + p.getId() + " " + p.getName());
				}
			}
		} else {
			System.out.println("Failed to authenticate the user!");
		}
		
		client.close();
	}
}
