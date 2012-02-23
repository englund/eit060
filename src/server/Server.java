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

	private String uid;
	private String uunit;
	private boolean loggedIn;
	
	private ACL acl;

	private Logger audit;
	
	private static final int TYPE_GOV 		= 0;
	private static final int TYPE_PATIENT 	= 1;
	private static final int TYPE_NURSE 	= 2;
	private static final int TYPE_DOCTOR 	= 3;
	
	public Server() {
		audit = new Logger("audit.log");
		
		users = new Users();
		users.fillTestUsers();
		
		acl = new ACL();
		
		System.setProperty("javax.net.ssl.keyStore", "certificates/server.jks");
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
			
			
			uid = parseId(cert.getSubjectDN().getName());
			uunit = null;
			int type= acl.findType(uid);
			if (type == TYPE_DOCTOR || type == TYPE_NURSE) {
				Staff s = users.getStaff(uid);
				uunit = s.getUnit();
			} else if (type == TYPE_PATIENT) {
				uunit = "patient";
			}
			
			audit.log(uid, null, "authenticated user");
	
			inputStream = socket.getInputStream();
			inputReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputReader);
			
			outputStream = socket.getOutputStream();
			outputWriter = new OutputStreamWriter(outputStream);
			writer = new BufferedWriter(outputWriter);
		} catch (Exception e) {
			e.printStackTrace();
			loggedIn = false;
		}
		
		loggedIn = true;
	}
	
	private String parseId(String name) {
		String[] s = name.split(",");
		return s[0].split("=")[1];
	}

	public void run() {
		try {
			
			while (!socket.isClosed()) {
				if (loggedIn) { // logged in
					System.out.println(":>server waiting..");
					String str = waitForString();
					
					String[] data = parseCommand(str);
					
					if (data[0].equals("getAllEntries")) { // getAllEntries:id
						Patient p = users.getPatient(data[1]);
						
						if (p != null) {
							ArrayList<JournalEntry> entries = p.getJournal().getEntries();
							ArrayList<JournalEntry> returnEntries = new ArrayList<JournalEntry>();
							for (JournalEntry entry : entries) {
								if (acl.userCanRead(uid, uunit, p, entry)) {
									returnEntries.add(entry);
									
									audit.log(uid, p.getId(), "access granted for reading entry " + entry);
								} else {
									audit.log(uid, p.getId(), "access NOT granted for reading entry " + entry);
								}
							}
							String s = entriesToString(returnEntries);
							if(!s.isEmpty()){
								sendString(s);
							}else{
								sendString("null");
							}
							
						} else {
							sendString("null");
						}
					} else if (data[0].equals("createEntry")) { // createEntry:id:docId:nurseId:division
						String patId = data[1];
						String docId = data[2];
						String nurseId = data[3];
						String division = data[4];
						Patient p = users.getPatient(patId);
						
						if(acl.docCanWrite(docId, p) && uid.equals(docId)){ // En doktor, inte en apa
							p.addJournalEntry(docId, nurseId, division);
							
							audit.log(uid, p.getId(), "access granted to create new entry");
							
							sendString("true");
						}else{
							audit.log(uid, p.getId(), "access NOT granted to create new entry");
							
							sendString("false");
						}
					} else if (data[0].equals("deleteEntries")) { // deleteEntries:id
						String id = data[1];
						Patient p = users.getPatient(id);
						
						if (acl.isType(uid, TYPE_GOV)) {
							p.wipeJournals();

							audit.log(uid, p.getId(), "access granted to delete all entries");
							
							sendString("true");
						}else{
							audit.log(uid, p.getId(), "access NOT granted to delete all entries");
							
							sendString("false");
						}
					}else if(data[0].equals("addNote")){// addNote:id:entryNo:note
						String id = data[1];
						Patient p = users.getPatient(id);
						JournalEntry je = p.getJournal().getEntries().get(Integer.parseInt(data[2]));
						
						if(acl.canWriteToJournal(uid, je)){
							je.addNote(data[3]);
							
							audit.log(uid, p.getId(), "access granted to add note to entry " + je);
							
							sendString("true");
						}else{
							audit.log(uid, p.getId(), "access NOT granted to add note to entry " + je);
							
							sendString("false");
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
			sb.append(entries.get(i).toString() + ":"); // doctorId:nurseId:unit:notes
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