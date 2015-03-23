package controller;

import java.io.IOException;

import bridge.SequentialFileBridge;
import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.DG.DGPredictor;

public class PredictionController {
	
	/**
	 * Creates a predictor for this controller
	 */
	public static Predictor createPredictor() {
		
		Predictor predictor = new DGPredictor();
		
		return predictor;
	}
	
	
	public static void main(String...args) throws IOException {
		
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
		
		//creating the predictor
		Predictor predictor = createPredictor();

		//training the predictor
		Evaluator evaluator = new Evaluator(datasetFolder);
		evaluator.addDataset(Format.MSNBC, 1000);
		evaluator.addPredictor(predictor);
		evaluator.Start(Evaluator.HOLDOUT, 1f, false, false, false);
		System.out.println("Predictor is trained and ready");
		
		//Creating the communication bridge
		SequentialFileBridge bridge = new SequentialFileBridge(requestFolder, ".predictionRequest", ".predictionResponse");
		System.out.println("Waiting for request");
		
		//For ever and after
		while(true) {

			//Fetch a request, if any
			String id = bridge.getNextRequestId();
						
			if(id != null) {
				
				System.out.println("Processing request #"+ id);
				
				//Reading the request
				String reqString = bridge.getRequest(id);
	
				//Generating a sequence from the request
				Sequence toPredict = new Sequence(-1);
				String[] itemsStr = reqString.split("\\s");
				for(String itemStr : itemsStr) {
					
					Integer itemId = null;
					try {
						itemId = Integer.parseInt(itemStr);
					} catch(NumberFormatException e) {
						System.err.println("Not a number: "+ itemStr);
					}
					
					toPredict.addItem(new Item(itemId));
				}
				
				//Predicting the sequence
				Sequence predicted = predictor.Predict(toPredict);
				
				//Formating the output
				String output = "";
				for(Item item : predicted.getItems()) {
					output += item.val + " ";
				}
				output = output.trim();
				
				//Writing the results of the experiment
				bridge.writeResponse(id, output);
			
				//Deletes the request
				bridge.finalizeRequest(id);
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
