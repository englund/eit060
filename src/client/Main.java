package client;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

import server.JournalEntry;
import server.Logger;
import server.Patient;
import server.Server;
import server.Staff;

public class Main {
	private static final int TYPE_GOV = 0;
	private static final int TYPE_PATIENT = 1;
	private static final int TYPE_NURSE = 2;
	private static final int TYPE_DOCTOR = 3;
	private Scanner sc;
	private Client client;
	private String id;

	public Main() {
		sc = new Scanner(System.in);
		
		System.out.print("ID: ");
		id = sc.next();
		System.out.print("Password: ");
		client = new Client("localhost", 10000, "certificates/" + id + ".jks", sc.next());
	}

	public static void main(String[] args) {
		Main m = new Main();
		m.buildMenu();

	}

	private void buildMenu() {
		boolean exit = false;
		int type = findType(id);
		if (type == TYPE_PATIENT) {
			System.out.println("Welcome patient " + id
					+ " here is the list of your record(s):");
			patientMenu(id, id);
		} else if (type == TYPE_DOCTOR) {
			System.out.println("Welcome doctor, what would you like to do?");
			do {
				System.out.println("1. Read a record");
				System.out.println("2. Write to a record");
				System.out
						.println("3. Add a new record to an existing patient");
				int choice = sc.nextInt();
				docMenu(choice, id);
				System.out.println("Would you like to exit? Y/N");
				String s = sc.next();
				exit = s.equals("Y") || s.equals("y");
			} while (!exit);

		} else if (type == TYPE_NURSE) {
			System.out.println("Welcome nurse, what would you like to do?");
			do {
				System.out.println("1. Read a record");
				System.out.println("2. Write to a record");
				int choice = sc.nextInt();
				nurseMenu(choice, id);
				System.out.println("Would you like to exit? Y/N");
				String s = sc.next();
				exit = s.equals("Y") || s.equals("y");
			} while (!exit);
		} else {
			System.out
					.println("Welcome goverment agency, what would you like to do?");
			do {
				System.out.println("1. Read a record");
				System.out.println("2. Delete a record");
				int choice = sc.nextInt();
				govMenu(choice, id);
				System.out.println("Would you like to exit? Y/N");
				String s = sc.next();
				exit = s.equals("Y") || s.equals("y");
			} while (!exit);
		}
	}

	private void patientMenu(String id, String caller) {
		// findPatient(id).getJournal().journalPrint();
		// log(caller,id,"read record");
		ArrayList<JournalEntry> je = client.getAllEntries(id);
		if(je!=null){
			for (JournalEntry e : je) {
			System.out.println(e.printStr());
			}
		}
		

	}

	private void docMenu(int choice, String docId) {
		System.out.println("Please enter patient's id: ");
		String id = sc.next();

		switch (choice) {
		case 1:
			patientMenu(id, docId);
			break;
		case 2:
			ArrayList<JournalEntry> je = client.getAllEntries(id);
			if(je != null){
				
				System.out
						.println("Which record would you like to add a note to? (enter a number)");
				for (int i = 0; i < je.size(); i++) {
					System.out.println(i + " " + je.get(i));
				}
				int no = sc.nextInt();
				//JournalEntry j = je.get(no);
				System.out.println("Add note :");
				if(!client.addNote(id, no, sc.next())){
					System.out.println("You have no right to perform this action");
				}
			}else{
				System.out.println("Finns inga journaler att skriva till");
			}
			break;

		case 3:
			System.out
					.println("Please enter a nurse id to associate to the new record :");
			String nurseId = sc.next();
			System.out.println("Please enter name of division :");
			if (!client.createEntry(id, docId, nurseId, sc.next())) {
				System.out.println("You have no right to perform this action");
			}

			break;

		}
	}

	private void nurseMenu(int choice, String nurseId) {
		System.out.println("Please enter patient's id: ");
		String id = sc.next();

		switch (choice) {
		case 1:
			patientMenu(id, nurseId);
			break;
		case 2:
			ArrayList<JournalEntry> je = client.getAllEntries(id);
			System.out.println("Which record would you like to add a note to? (enter a number)");
			for (int i = 0; i > je.size(); i++) {
				System.out.println(i + " " + je.get(i));
			}
			int no = sc.nextInt();
			JournalEntry j = je.get(no);
			System.out.println("Add note :");
			j.addNote(sc.next());
			break;

		}

	}

	private void govMenu(int choice, String govId) {
		System.out.println("Please enter patient's id: ");
		String id = sc.next();
		if (choice == 1) {
			patientMenu(id, "Goverment Agency");
		} else if (choice == 2) {
			System.out.println("Really delete all records for patient ? Y/N");
			String ans = sc.next();
			if (ans.equals("Y") || ans.equals("y")) {
				if (client.deleteEntries(id));
				System.out.println("Successfully deleted all records for patient ");
			}

		} 

	}



	private int findType(String id) {
		if (id.charAt(0) == 'd') {
			return TYPE_DOCTOR;
		} else if (id.charAt(0) == 'n') {
			return TYPE_NURSE;
		} else if (id.charAt(0) == 'p') {
			return TYPE_PATIENT;
		}
		return TYPE_GOV;
	}

}