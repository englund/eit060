package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import server.JournalEntry;
import server.Patient;

public class Client {
	
	private SSLSocket socket;
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
		
		System.setProperty("javax.net.ssl.keyStore", "certificates/keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "eit060"); 
		System.setProperty("javax.net.ssl.trustStore", "certificates/truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "eit060");
		
		try {
			SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();

			System.out.println(":>client making contact with " + host + ":" + port);
		    socket = (SSLSocket)factory.createSocket(host, port);
			socket.setUseClientMode(true);
			socket.startHandshake();
			System.out.println(":>client handshake is done.");
		    
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
	
	public String authenticate(String uid) {
		if (sendString("login:" + uid)) { // login:id
			System.out.println(":>client waiting..");
			String s = waitForString();
			System.out.println(":>client ack=" + s);
			String[] cmd = parseCommand(s); // förväntar oss id
			String id = cmd[0];
			if (!id.equals("null")) { // användaren autentiserad och har behörighet!
				return id;
			}
		}
		return null;
	}

	public ArrayList<JournalEntry> getAllEntries(String id) {
		if (sendString("getAllEntries:"+id)) {
			String s = waitForString(); // (doctorId:nurseId:unit)+
			String[] j = parseCommand(s);
			
			if (!s.equals("null")) {
				ArrayList<JournalEntry> journals = new ArrayList<JournalEntry>();
				for (int i = 0; i < j.length; i += 4) {
					String doctorId = j[i];
					String nurseId = j[i+1];
					String unit = j[i+2];
					String notes = j[i+3];
					JournalEntry je = new JournalEntry(doctorId, nurseId, unit);
					je.addNote(notes);
					journals.add(je);
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
	
	public boolean createEntry(String id, String docId, String nurseId, String division) {
		if(sendString("createEntry:"+id +":" +docId +":" +nurseId +":" +division)){ //createEntry:id:docId:nurseId:division
			String s = waitForString();
			if(s.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteEntries(String id) {
		if (sendString("deleteEntries:" + id)) { // deleteEntries:id
			String s = waitForString();
			if(s.equals("true")) {
				return true;
			}
		}
		return false;
	}
	public boolean addNote(String id, int entryNo, String note) {
		if (sendString("addNote:" + id +":" +entryNo +":" +note)) { // addNote:id:entryNo:note
			String s = waitForString();
			if(s.equals("true")) {
				return true;
			}
		}
		return false;
		
	}
	
	public String[] parseCommand(String s) {
		return s.split(":");
	}
	
	public String waitForString() {
		while (!socket.isClosed()) {
			try {
				String s = null;
				if ((s = reader.readLine()) != null) {
					System.out.println(":>client recieveing " + s);
					return s;
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
			System.out.println(":>client sending " + s);
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
		String id = client.authenticate("d01");
		if (id != null) { // sucessfull login!
			System.out.println(":>client User authenticated as " + id + ".");
			
			// Hämta alla mina journalentries
			System.out.println("Hämtar alla Victors entries!");
			ArrayList<JournalEntry> journals = client.getAllEntries(id);
			if (journals != null) {
				for (int i = 0; i < journals.size(); i++) {
					JournalEntry entry = journals.get(i);
					System.out.println(i + ". " + entry);
				}
			}

			System.out.println("Hämtar alla patienter jag kan se!");
			ArrayList<Patient> patients = client.getAllPatients(id);
			if (patients != null) {
				for (int i = 0; i < patients.size(); i++) {
					Patient p = patients.get(i);
					System.out.println(i + ". " + p);
				}
			}
		} else {
			System.out.println("Failed to authenticate the user!");
		}
		
		client.close();
	}


}
