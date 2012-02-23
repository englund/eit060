package server;

import java.util.ArrayList;

public class ACL {
	
	private static final int TYPE_GOV 		= 0;
	private static final int TYPE_PATIENT 	= 1;
	private static final int TYPE_NURSE 	= 2;
	private static final int TYPE_DOCTOR 	= 3;
	
	public boolean isType(String id, int type) {
		return (findType(id) == type);
	}
	
	public int findType(String id) {
		if(id.charAt(0) == 'd'){
			return TYPE_DOCTOR;
		}else if(id.charAt(0) == 'n'){
			return TYPE_NURSE;
		}else if(id.charAt(0) == 'p'){
			return TYPE_PATIENT;
		}
		return TYPE_GOV;
	}

	public boolean userCanRead(String uid, String uunit, Patient person, JournalEntry entry) {
		int type = findType(uid);
		if (type == TYPE_GOV) {
			return true;
		} else if (type == TYPE_DOCTOR) {
			if (entry.getDoctorId().equals(uid) || entry.getUnit().equals(uunit)) {
				return true;
			}
		} else if (type == TYPE_NURSE) {
			if (entry.getUnit().equals(uunit)) {
				return true;
			}
		} else if (type == TYPE_PATIENT) {
			if (person.getId().equals(uid)) {
				return true;
			}
		}
		return false;
	}

	public boolean userCanSeePatient(String uid, String uunit, Patient patient) {
		int type = findType(uid);
		if (type == TYPE_GOV) {
			return true;
		} else if (type == TYPE_DOCTOR || type == TYPE_NURSE) {
			ArrayList<JournalEntry> entries = patient.getJournal().getEntries();
			for (JournalEntry entry : entries) {
				if (entry.getDoctorId().equals(uid) || entry.getUnit().equals(uunit)) {
					return true;
				}
			}
		} else if (type == TYPE_PATIENT) {
			if (patient.getId().equals(uid)) {
				return true;
			}
		}
		return false;
	}

	public boolean docCanWrite(String docId, Patient p) {
		return p.getJournal().findDoctor(docId);
	}
	public boolean canWriteToJournal(String id, JournalEntry j){
		return (j.getDoctorId().equals(id)||j.getNurseId().equals(id));
	}
}
