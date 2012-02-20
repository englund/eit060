/**
 * 
 */
package server;

/**
 * @author dt08al8
 *
 */
public class Staff {
	private boolean isDoctor;
	private String unit, id, name;


	public Staff(String id, String name, String unit, boolean isDoctor) {
		this.id = id;
		this.name = name;
		this.unit = unit;
		this.isDoctor = isDoctor;
	}
	
	public boolean isDoctor() {
		return isDoctor;
	}

	public String getId() {
		return id;
	}

	public String getUnit() {
		return unit;
	}
}
