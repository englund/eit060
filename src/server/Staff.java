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

	/**
	 * @param id
	 * @param name
	 * @param unit
	 * @param hospital
	 */
	public Staff(String id, String name, String unit, String hospital, boolean isDoctor) {
		super(id, name, unit, hospital);
		this.isDoctor = isDoctor;
		isNurse = !isDoctor;
	}

}
