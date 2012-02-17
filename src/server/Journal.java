package server;

import java.util.ArrayList;

public class Journal {
	private Patient p;
	private ArrayList<JournalEntry> entries;
	private String currentHospital;
	private String currentUnit;
	
	public Journal(Patient p){
		this.p = p;
		entries = new ArrayList<JournalEntry>();
	}
	public void addEntry(JournalEntry je){
		entries.add(je);
		currentHospital = je.getHospital();
		currentUnit = je.getUnit();
	}
	
	public ArrayList<JournalEntry> getEntries() {
		return entries;
	}
	public void journalPrint(){
		for (int i = 0; i< entries.size(); i++) {
			System.out.println("hejhop!!");
			JournalEntry e = entries.get(i);
			System.out.println(i + ". " + e.getHospital() + " " + e.getUnit() + " " + e.getDoctorId());
		}
	}
	
}
