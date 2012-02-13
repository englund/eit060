package server;

public class Patient extends Person {
	private String nurseId, doctorId; // Borde liggar i JournalEntry ist?  "Each patient has one or serveral medical records"
	private Journal journal;

	public Patient(String id, String name) {
		super(id, name);
		journal = new Journal(this);
	}

	/**
	 * @return the nurseId
	 */
	public String getNurseId() {
		return nurseId;
	}

	/**
	 * @param nurseId the nurseId to set
	 */
	public void setNurseId(String nurseId) {
		this.nurseId = nurseId;
	}

	/**
	 * @return the doctorId
	 */
	public String getDoctorId() {
		return doctorId;
	}

	/**
	 * @param doctorId the doctorId to set
	 */
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	

}
