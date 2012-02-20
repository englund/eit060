package server;

public class Patient {
	private Journal journal;
	private String id, name;


	public Patient(String id, String name) {
		this.id = id;
		this.name = name;
		journal = new Journal(this);
	}
	public Journal getJournal(){
		return journal;
	}
	
	public String getId(){
		return id;
	}
	public void addJournalEntry(String doctorId, String nurseId, String unit){
		journal.addEntry(new JournalEntry(doctorId, nurseId, unit));
	}


	public void wipeJournals() {
		journal = new Journal(this);
		
	}

	
	public String toString() {
		return id + ":" + name;
	}
}
