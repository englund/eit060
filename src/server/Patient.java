package server;

public class Patient extends Person {
	private String nurseId, doctorId;
	private Journal journal;

	public Patient(String id, String name, String unit, String hospital, Journal j) {
		super(id, name, unit, hospital);
		journal = j;
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
