package server;

public class JournalEntry {

	private String nurseId, doctorId;
	private String date, content;
	private String hospital;
	private String unit;

	/**
	 * @param date
	 * @param content
	 * @param signature
	 */
	public JournalEntry(String doctorId, String nurseId, String hospital, String unit) {
		this.doctorId = doctorId;
		this.nurseId = nurseId;
		this.hospital = hospital;
		this.unit = unit;
	}

	/**
	 * @return the hospital
	 */
	public String getHospital() {
		return hospital;
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
		return "Date: " + date + "\n" +
			   "Doctor: " + doctorId + "\n" +
			   "Nurse: " + nurseId + "\n" +
			   "Hospital: " + hospital + "\n" +
			   "Unit: " + unit + "\n" +
			   "Content: " + content + "\n";
	}

}
