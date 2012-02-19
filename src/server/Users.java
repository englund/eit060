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
		Journal j = new Journal(julia);
		j.addEntry(new JournalEntry("daaa", "naaa", "Lund", "Öronkliniken"));
		j.addEntry(new JournalEntry("dbbb", "nbbb", "Malmö", "Öronkliniken"));
		j.addEntry(new JournalEntry("dccc", "nccc", "Söderhamn", "Öronkliniken"));
		users.put(julia.getId(), julia);
		
		Patient annie = new Patient("870117", "Annie Sukino");
		Journal a = new Journal(annie);
		a.addEntry(new JournalEntry("d890810", "n850112", "Lund", "Ögonkliniken"));
		users.put(annie.getId(), annie);
		
		Staff victor = new Staff("d891121", "Victor", "Ögonkliniken", "Lund", true);
		users.put(victor.getId(), victor);
		
		Staff henrik = new Staff("n850112", "Henrik", "Ögonkliniken", "Lund", false);
		users.put(henrik.getId(), henrik);
	}

}
