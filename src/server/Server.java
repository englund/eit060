package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.security.cert.X509Certificate;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class Server extends Thread {
	
	private int port = 10000;
	
	private SSLSocket socket;
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
		
		acl = new ACL();
		
		System.setProperty("javax.net.ssl.keyStore", "certificates/keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "eit060");
		System.setProperty("javax.net.ssl.trustStore", "certificates/truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "eit060");
		
		try {
			
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(port);
			server.setNeedClientAuth(true);
            
			System.out.println(":>server waiting for connection...");
            socket = (SSLSocket) server.accept();
			
			SSLSession session = socket.getSession();
			X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
			System.out.println(":>server recieved cert " + cert.getSubjectDN().getName());
			
			System.out.println(":>server connection received from " + socket.getInetAddress().getHostName());
	
			inputStream = socket.getInputStream();
			inputReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputReader);
			
			outputStream = socket.getOutputStream();
			outputWriter = new OutputStreamWriter(outputStream);
			writer = new BufferedWriter(outputWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		loggedIn = false;
	}
	
	public void run() {
		try {
			int type = -1;
			String id = null;
			
			while (!socket.isClosed()) {
				System.out.println(":>server waiting..");
				String str = waitForString();
				
				String[] data = parseCommand(str);
				
				if (!loggedIn) {
					if (data[0].equals("login")) { // login:type:id
						type = Integer.parseInt(data[1]);
						id = data[2];
						user = users.getPerson(id);
						
						if (acl.isType(user, type)) {
							System.out.println(":>server Authenticated user " + id);
							sendString(type+":"+user.getId()); // type:id
							loggedIn = true;
						} else {
							System.out.println(":>server " + id + " tried to login but it was unsucessfull!");
							sendString("null"); // null
						}
					}
				} else { // logged in
					if (data[0].equals("getAllEntries")) { // getAllEntries:id
						Patient p = users.getPatient(data[1]);
						if (p != null) {
							ArrayList<JournalEntry> entries = p.getJournal().getEntries();
							ArrayList<JournalEntry> returnEntries = p.getJournal().getEntries();
	
							for (JournalEntry entry : entries) {
								if (acl.personCanRead(user, p, entry)) {
									returnEntries.add(entry);
								}
							}
							sendString(entriesToString(returnEntries));
						} else {
							sendString("null");
						}
					} else if (data[0].equals("getAllPatients")) { // getAllPatients // som användaren har behörighet att se
						ArrayList<Patient> patients = users.getPatients();
						ArrayList<Patient> returnPatients = users.getPatients();
						for (Patient p : patients) {
							if (acl.canSeePatient(user, p) && !returnPatients.contains(p)) {
								returnPatients.add(p);
							}
						}
						if (returnPatients.size() > 0) {
							sendString(patientsToString(returnPatients));
						} else {
							sendString("null");
						}
					}
				}
			}
			
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private String entriesToString(ArrayList<JournalEntry> entries) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < entries.size(); i++) {
			sb.append(entries.get(i).toString() + ":"); // date:doctorId:nurseId:hospital:unit:content:
		}
		return sb.toString();
	}
	
	private String patientsToString(ArrayList<Patient> patients) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < patients.size(); i++) {
			sb.append(patients.get(i) + ":"); // id:name:
		}
		return sb.toString();
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
			System.out.println(":>server sending " + s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String waitForString() {
		while (!socket.isClosed()) {
			try {
				String s = null;
				if ((s = reader.readLine()) != null) {
					s = s.replace("\n", "");
					System.out.println(":>server recieveing " + s);
					return s;
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