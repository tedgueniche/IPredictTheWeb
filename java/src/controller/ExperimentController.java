package controller;

import request.ExperimentRequest;
import bridge.SequentialFileBridge;
import ca.ipredict.helpers.StatsLogger;
import ca.ipredict.predictor.Evaluator;

public class ExperimentController {

	
	public static void main(String...args) {
		
		// Default values for paths
		String requestFolder = "/home/ted/tmp/wwwIPredictRequest";
		String datasetFolder = "/home/ted/java/IPredict/datasets";
		
		//Usage and argument capture
		if(args.length != 2) {
			System.err.println("Usage: <pathToRequest> <pathToDataset>");
			System.out.println("Using default paths");
		}
		else {
			requestFolder = args[0];
			datasetFolder = args[1];
		}
		
		System.out.println("Request Folder: "+ requestFolder);
		System.out.println("Dataset Folder: "+ datasetFolder);
		
		
		//Creating the communication bridge
		SequentialFileBridge bridge = new SequentialFileBridge(requestFolder, ".experimentRequest", ".experimentResponse");
		System.out.println("Waiting for request");
		
		//For ever and after
		while(true) {

			//Fetch a request, if any
			String id = bridge.getNextRequestId();
						
			if(id != null) {
				
				System.out.println("Processing request #"+ id);
				
				//Reading the request
				String reqString = bridge.getRequest(id);
	
				//Creating the request object
				ExperimentRequest req = new ExperimentRequest(reqString);
				
				//Executing the experiment request
				StatsLogger stats = req.execute(new Evaluator(datasetFolder));
				
				//Writing the results of the experiment
				bridge.writeResponse(id, stats.toJsonString());
			
				//Deletes the request
				bridge.finalizeRequest(id);
				
				//DEBUG: Output the result as JSON
				System.out.println(stats.toJsonString());
			}
			
			//Sleeping to minimize CPU and disk usage
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
