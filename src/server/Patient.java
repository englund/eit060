package server;

public class Patient {
	private Journal journal;
	private String id, name;

	public Patient(String id, String name) {
		//super(id, name);
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
	

}
