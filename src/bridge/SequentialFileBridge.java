package bridge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;

import org.json.JSONObject;

/**
 * A Bridge that takes request from files and write response back
 */
public class SequentialFileBridge {

	/**
	 * Suffix for the file containing a JSON request for an experiment
	 */
	private final String requestSuffix = ".experimentRequest";
	
	/**
	 * Suffix for the file containing a JSON response for an experiment
	 */
	private final String responseSuffix = ".experimentResponse";
	
	/**
	 * Path to the folder containing the request files
	 */
	private final String basePath;
	
	/**
	 * A queue of requests to process
	 */
	private TreeSet<String> requestsQueue;
	
	/**
	 * @param basePath Path to the folder containing the request files
	 */
	public SequentialFileBridge(String basePath) {
		this.basePath = basePath;
		requestsQueue = new TreeSet<String>();
	}
	
	/**
	 * Tries to return a request id if any in the queue
	 */
	public String getNextRequestId() {
		
		String requestId = null;
		
		//Fetching the latest requests
		fetchRequests();
		
		//If there is at least one request
		if(requestsQueue.size() >= 1) {
			
			requestId = requestsQueue.first();
		}
		
		return requestId;
	}
	
	/**
	 * Reads a request as a String from the provided ID
	 */
	public String getRequest(String requestId) {
		
		String content = readFile(basePath + "/" + requestId);
		return content;
	}
	
	/**
	 * Writes a response to a response file and remove any reference of the request
	 */
	public boolean writeResponse(String requestId, String response) {
		
		boolean status = true;
		
		//writes to the response file
		String responseFilePath = requestId.replaceAll(requestSuffix, responseSuffix);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(basePath + "/" + responseFilePath));
			bw.write(response);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
	
			status = false;
		}
		
		//deletes the request file
		status = status && new File(requestId).delete();
		
		//remove from the queue
		status = status && requestsQueue.remove(requestId);
		
		return status;
	}
	
	/**
	 * Removes this request from the queue and deletes the request file
	 */
	public boolean finalizeRequest(String requestId) {
		
		//Removes the request from the queue
		requestsQueue.remove(requestId);
		
		//Deletes the request file
		boolean isDeleted = new File(basePath + "/" + requestId).delete();

		return isDeleted;
	}
	
	
	/**
	 * Returns a list of file request
	 */
	private void fetchRequests() {
	
		String[] p = new File(basePath).list();
		
		String[] requestFiles = new File(basePath).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (name.length() > requestSuffix.length() && name.endsWith(requestSuffix));
			}
		});
		
		if(requestFiles == null) {
			System.err.println("The request folder does not exists: "+ basePath);
		}
		
		//Pushing each request file to queue
		requestsQueue.addAll(Arrays.asList(requestFiles));
	}
	
	/**
	 * Reads the first line of a file. The request should be on one line.
	 * @return Returns the content of the file or null
	 */
	private String readFile(String filePath) {
		
		String content = null;
		
		try {
			
			content = new BufferedReader(new FileReader(filePath)).readLine();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return content;
	}
		
}
