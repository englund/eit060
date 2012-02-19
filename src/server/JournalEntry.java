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
	public JournalEntry(String date, String doctorId, String nurseId, String hospital, String unit, String content) {
		this.date = date;
		this.doctorId = doctorId;
		this.nurseId = nurseId;
		this.hospital = hospital;
		this.unit = unit;
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public String getContent() {
		return content;
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
		return date+":"+doctorId+":"+nurseId+":"+hospital+":"+unit+":"+content;
	}
	
	public String printStr() {
		return "Date: " + date + "\n" +
			   "Doctor: " + doctorId + "\n" +
			   "Nurse: " + nurseId + "\n" +
			   "Hospital: " + hospital + "\n" +
			   "Unit: " + unit + "\n" +
			   "Content: " + content + "\n";
	}

}
