package server;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {	
	
	private PrintStream out;
	
	public Logger(String file){
		try {
			FileOutputStream fout = new FileOutputStream (file,true);
			out = new PrintStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public void print(String performer, String patient, String action){
		out.println (getDateTime() +performer +" " +action + " to patient" +patient);
		out.flush();
	}
	private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
