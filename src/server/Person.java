package server;

abstract public class Person {
	private String id; //Globalt unikt id
	private String name, unit, hospital;
	
	
	
	
	/**
	 * @param id
	 * @param name
	 * @param unit
	 * @param hospital
	 */
	public Person(String id, String name, String unit, String hospital) {
		this.id = id;
		this.name = name;
		this.unit = unit;
		this.hospital = hospital;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getUnit() {
		return unit;
	}
	public String getHospital() {
		return hospital;
	}
	
	

}
