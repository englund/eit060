package server;

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
			
			System.out.println("typeClass:"+typeClass);
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
}
