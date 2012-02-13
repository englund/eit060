/**
 * 
 */
package server;

/**
 * @author dt08al8
 *
 */
public class Staff extends Person {
	private boolean isDoctor;
	private boolean isNurse;
	private String hospital, unit;

	/**
	 * @param id
	 * @param name
	 * @param division
	 * @param hospital
	 */
	public Staff(String id, String name, String unit, String hospital, boolean isDoctor) {
		super(id, name);
		this.hospital = hospital;
		this.unit = unit;
		this.isDoctor = isDoctor;
		isNurse = !isDoctor;
	}

}
