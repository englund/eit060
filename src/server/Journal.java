package server;

import java.util.ArrayList;

public class Journal {
	private String id, name;
	private ArrayList<JournalEntry> entries; 
	
	public Journal(String id, String name){
		this.id= id;
		this.name = name;
		entries = new ArrayList<JournalEntry>();
	}
	public void addEntry(JournalEntry je){
		entries.add(je);
	}
	
}
