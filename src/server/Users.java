package server;

import java.util.HashMap;

public class Users {
	
	private HashMap<String, Person> users;
	
	public Users() {
		users = new HashMap<String, Person>();
	}
	
	public Person getUser(String id) {
		return users.get(id);
	}
	
	public void fillTestUsers() {
		Patient julia = new Patient("900401", "Julia Mauritsson");
		Journal j = julia.getJournal();
		j.addEntry(new JournalEntry("2012-02-19", "daaa", "naaa", "Lund", "Öronkliniken", "content"));
		j.addEntry(new JournalEntry("2012-02-19", "dbbb", "nbbb", "Malmö", "Öronkliniken", "content"));
		j.addEntry(new JournalEntry("2012-02-19", "dccc", "nccc", "Söderhamn", "Öronkliniken", "content"));
		users.put(julia.getId(), julia);
		
		Patient annie = new Patient("870117", "Annie Sukino");
		Journal a = annie.getJournal();
		a.addEntry(new JournalEntry("2012-02-19", "d890810", "n850112", "Lund", "Ögonkliniken", "content"));
		users.put(annie.getId(), annie);
		
		Staff victor = new Staff("d891121", "Victor", "Ögonkliniken", "Lund", true);
		users.put(victor.getId(), victor);
		
		Staff henrik = new Staff("n850112", "Henrik", "Ögonkliniken", "Lund", false);
		users.put(henrik.getId(), henrik);
	}

}
