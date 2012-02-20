package server;

import java.util.ArrayList;
import java.util.HashMap;

public class Users {
	
	private HashMap<String, Patient> patients;
	private HashMap<String, Staff> staff;
	private HashMap<String, Government> govMap;
	
	public Users() {
		patients = new HashMap<String, Patient>();
		staff = new HashMap<String, Staff>();
		govMap = new HashMap<String, Government>();
	}
	
	public Patient getPatient(String id) {
		return patients.get(id);
	}
	
	public Staff getStaff(String id) {
		return staff.get(id);
	}
	
	public ArrayList<Patient> getPatients() {
		return new ArrayList<Patient>(patients.values());
	}
	
	public void fillTestUsers() {
		Government gov = new Government("g01", "Socialstyrelsen");
		govMap.put("g01", gov);
		
		//Skapar patient Julia
		Patient julia = new Patient("p01", "Julia Mauritsson");
		julia.addJournalEntry("d01", "n01", "Eye");
		julia.addJournalEntry("d02", "n02", "Ear");
		patients.put(julia.getId(), julia);
		
		//Skapar patient Annie
		Patient annie = new Patient("p02", "Annie Sukino");
		annie.addJournalEntry("d01", "n01", "Eye");
		patients.put(annie.getId(), annie);
		
		//Skapar läkare Victor
		Staff victor = new Staff("d01", "Victor Englund", "Eye", true);
		staff.put(victor.getId(), victor);
		//Skapar läkare Henrik
		Staff henrik = new Staff("d02", "Henrik Andersson", "Ear", true);
		staff.put(henrik.getId(), henrik);
		
		//Skapar nurse Tina
		Staff tina = new Staff("n01", "Christina Schmidt", "Eye", false);
		staff.put(tina.getId(), tina);
		Staff eliza = new Staff("n02", "Eliza Jing", "Ear", false);
		staff.put(eliza.getId(), eliza);
	}

	public boolean userExist(String uid) {
		return (patients.containsKey(uid) || staff.containsKey(uid) || govMap.containsKey(uid));
	}

}
