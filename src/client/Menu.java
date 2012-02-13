package client;
import java.util.ArrayList;
import java.util.Scanner;

import server.Journal;
import server.JournalEntry;
import server.Patient;

public class Menu {

	private static final int TYPE_GOV 		= 0;
	private static final int TYPE_PATIENT 	= 1;
	private static final int TYPE_NURSE 	= 2;
	private static final int TYPE_DOCTOR 	= 3;
	public static ArrayList<JournalEntry> entries;

	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		Journal j = new Journal(new Patient("1234567890", "Kalle Kuling"));
		j.addEntry(new JournalEntry("daaa", "naaa", "Lund", "Öronkliniken"));
		j.addEntry(new JournalEntry("dbbb", "nbbb", "Malmö", "Öronkliniken"));
		j.addEntry(new JournalEntry("dccc", "nccc", "Söderhamn", "Öronkliniken"));
		//
		
		entries = j.getEntries();
		System.out.println("Ange personnummer/id:");
		String id = sc.next();
		//här ska authentiseringen ske
		
		System.out.println("Välkommen " +id +" vad vill du göra?");
		System.out.println("Vem vill du logga in som?" +"\n" +"0. Socialstyrelsen" +"\n" +"1. Patient" +"\n" +"2. Sjuksköterska" +"\n" +"3. Läkare");
		int type = sc.nextInt(); // vi får tillbaka en typ
		//while(id.charAt(2)<2 ){
		switch (type) {
		case Menu.TYPE_GOV:
			govMenu(sc);
			break;
		case Menu.TYPE_PATIENT:
			patientMenu(sc);
			break;
		case Menu.TYPE_NURSE:
			nurseMenu(sc);
			break;
		case Menu.TYPE_DOCTOR:
			doctorMenu(sc);
			break;
		}
	}
	private static void govMenu(Scanner sc) {
		System.out.println("1. Läsa journaler");
		System.out.println("2. Skriva journaler");
		System.out.println("3. Skapa journaler.");
		System.out.println("4. Lägg till ny journal.");
		System.out.println("5. Ta bort journaler.");

		
		
	}
	private static void nurseMenu(Scanner sc) {
		System.out.println("1. Läsa journaler");
		System.out.println("2. Skriva journaler");
		switch(sc.nextInt()){
		case 1:
		 System.out.println("Ange patientens personnummer: ");
		 int personnr = sc.nextInt();
		 System.out.println("Ange vilken av patientens journaler du vill läsa: ");
		 JournalEntry e = entries.get(sc.nextInt());
		break;
		
		case 2:
			System.out.println("Ange partientens personnummer: ");
			 int personnr2 = sc.nextInt();
			 System.out.println("Ange vilken av patientens journaler du vill skriva: ");
			 JournalEntry e2 = entries.get(sc.nextInt());
		break;
		
		}
		
	}
	private static void doctorMenu(Scanner sc) {
		System.out.println("1. Se journaler.");
		System.out.println("2. Skapa journaler.");
		System.out.println("3. Lägg till ny journal.");
		switch(sc.nextInt()){
		case 1: System.out.println("Ange patientens personnummer: ");
				int personnr = sc.nextInt();
				//hitta patienten
				//Kontroll av rättighet: Är personen skriven på samma unit som läkaren?
				//if(patient.currentHospital
				for (int i = 0; i< entries.size(); i++) {
					JournalEntry e = entries.get(i);
					System.out.println(i + ". " + e.getHospital() + " " + e.getUnit() + " " + e.getDoctorId());
				}
				
				System.out.print("Ange nummer på journal du vill läsa: ");
				int jint = sc.nextInt();
				
				System.out.println(entries.get(jint));
				break;
				
		case 2: System.out.println("Ange patientens personnummer: ");
				int personnr2 = sc.nextInt();
				//hitta personens journal
				//Skapa ett JournalEntry och lägg in i personens journal
				
				break;
		case 3: System.out.println("Ange patientens personnummer: ");
				String personnr3 = sc.next();
				System.out.println("Ange patientens namn: ");
				//hitta patienten, kontroll av 
				//tilldela patienten en nurse
		}

				
		
	}
	private static void patientMenu(Scanner sc) {
		//Scanner sc = new Scanner(System.in);
		
		System.out.println("1. Se journaler.");
		int cmd = sc.nextInt();
		
		switch (cmd) {
		case 1:
			// skicka förfrågan efter journalerna på servern
			// får tillbaka en lista	
			//

			for (int i = 0; i< entries.size(); i++) {
				JournalEntry e = entries.get(i);
				System.out.println(i + ". " + e.getHospital() + " " + e.getUnit() + " " + e.getDoctorId());
			}
			
			System.out.print("Ange nummer på journal du vill läsa: ");
			int jint = sc.nextInt();
			
			System.out.println(entries.get(jint));
			
			break;
			
		}

		
	}
	
}
