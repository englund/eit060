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
	
}
