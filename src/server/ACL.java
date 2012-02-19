package server;

import java.util.ArrayList;

public class ACL {
	
	private static final int TYPE_GOV 		= 0;
	private static final int TYPE_PATIENT 	= 1;
	private static final int TYPE_NURSE 	= 2;
	private static final int TYPE_DOCTOR 	= 3;
	
	public boolean isType(Person p, int type) {
		if (p != null) {
			String typeClass = "";
			if (type == TYPE_GOV) {
				typeClass = "Government";
			} else if (type == TYPE_DOCTOR || type == TYPE_NURSE) {
				typeClass = "Staff";
			} else if (type == TYPE_PATIENT) {
				typeClass = "Patient";
			}
			
			if (p.getClass().getName().equals("server."+typeClass)) {
				if (typeClass.equals("Staff")) {
					Staff staff = (Staff) p;
					if (type == TYPE_DOCTOR) {
						return staff.isDoctor();
					}
				}
				return true;
			}
		}
		return false;
	}

	public boolean personCanRead(Person user, Patient person, JournalEntry entry) {
		if (user.getClass().getName().equals("server.Government")) {
			return true;
		} else if (user.getClass().getName().equals("server.Staff")) {
			Staff s = (Staff) user;
			if (s.isDoctor()) {
				if (entry.getDoctorId() == s.getId()
						|| entry.getHospital() == s.getHospital()
						|| entry.getUnit() == s.getUnit()) {
					return true;
				}
			} else {
				if (entry.getUnit() == s.getUnit()) {
					return true;
				}
			}
		} else if (user.getClass().getName().equals("server.Patient")) {
			Patient p = (Patient) user;
			if (p.getId() == person.getId()) {
				return true;
			}
		}
		return false;
	}

	public boolean canSeePatient(Person user, Patient patient) {
		if (user.getClass().getName().equals("server.Government")) {
			return true;
		} else if (user.getClass().getName().equals("server.Staff")) {
			Staff s = (Staff) user;
			ArrayList<JournalEntry> entries = patient.getJournal().getEntries();
			for (JournalEntry entry : entries) {
				if (s.isDoctor()) {
					if (entry.getDoctorId() == s.getId()
							|| entry.getHospital() == s.getHospital()
							|| entry.getUnit() == s.getUnit()) {
						return true;
					}
				} else {
					if (entry.getUnit() == s.getUnit()) {
						return true;
					}
				}
			}
		} else if (user.getClass().getName().equals("server.Patient")) {
			Patient p = (Patient) user;
			if (p.getId() == patient.getId()) {
				return true;
			}
		}
		return false;
	}
}
