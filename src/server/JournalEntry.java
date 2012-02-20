package server;

public class JournalEntry {

	private String nurseId, doctorId;
	private String unit;
	private StringBuilder notes;

	public JournalEntry(String doctorId, String nurseId, String unit) {
		this.doctorId = doctorId;
		this.nurseId = nurseId;
		this.unit = unit;
		notes = new StringBuilder();
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public String toString() {
		String n = getNotes();
		return doctorId + ":" + nurseId + ":" + unit + ":" + (n.isEmpty() ? "null" : n);
	}

	public String printStr() {
		return	"Doctor: " + doctorId + "\n" + 
				"Nurse: " + nurseId + "\n" + 
				"Unit: " + unit + "\n" + 
				"Note: " + getNotes();
	}

	public String getNurseId() {
		return nurseId;
	}
	public void addNote(String string) {
		notes.append(string + "\n");
		
	}
	public String getNotes(){
		return notes.toString();
	}

}
