package server;

public class Patient extends Person {
	private Journal journal;

	public Patient(String id, String name) {
		super(id, name);
		journal = new Journal(this);
	}

	public Journal getJournal(){
		return journal;
	}
	
	public String getId(){
		return id;
	}
	
	public String toString() {
		return id + ":" + name;
	}
}
