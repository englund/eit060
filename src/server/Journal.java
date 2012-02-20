package server;

import java.util.ArrayList;

public class Journal {
	private Patient p;
	private ArrayList<JournalEntry> entries;
	
	public Journal(Patient p){
		this.p = p;
		entries = new ArrayList<JournalEntry>();
	}
	public void addEntry(JournalEntry je){
		entries.add(je);
	}
	
	public ArrayList<JournalEntry> getEntries() {
		return entries;
	}
	public void journalPrint(){
		for (int i = 0; i< entries.size(); i++) {
			System.out.println(entries.get(i));
		}
	}

	public boolean findDoctor(String id){
		for(JournalEntry e : entries){
			if(e.getDoctorId().equals(id)){
				return true;
			}
		}
		return false;
	}
	public boolean findNurse(String nurseId) {
		for(JournalEntry e : entries){
			if(e.getNurseId().equals(nurseId)){
				return true;
			}
		}
		return false;
	}
	
}
