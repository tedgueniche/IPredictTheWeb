package datasets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.ipredict.database.Item;
import datasets.LogDatabase.Session;

public abstract class WebLogConverter {

	/**
	 * Convert a file into a new format
	 * @param inputFilePath Path to the input file
	 * @param outputFile Path Path to the output file
	 * @param timeWindow Max number of minutes between two request to be considered in the same session
	 * @return
	 * @throws IOException 
	 */
	public int convert(String inputFilePath, String outputFilePath, int timeWindow) throws IOException {
	
		LogDatabase database = new LogDatabase(timeWindow);

		//Opening both the input and output files
		BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
		
		//Reading the file input file line by line
		String line;
		while( (line = reader.readLine()) != null) {
			
			//extract information from the log and adds it into the database
			addLogInDatabase(line, database);
		}
		
		//finalizing the sessions
		database.finalizeSessions();
		
		//writing the sequences to the output file
		int sequenceCount = 0;
		for(Session session : database.getSession()) {
			
			String sequence = "";
			for(Item item : session.getSequence().getItems()) {
				sequence += "" + item + " ";
			}
			writer.write(sequence.trim() + "\n");
			sequenceCount++;
		}
		
		
		//Closing both files
		reader.close();
		writer.close();

		return sequenceCount;
	}
	
	
	public abstract void addLogInDatabase(String log, LogDatabase database);
}
