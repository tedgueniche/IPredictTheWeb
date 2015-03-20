package request;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.helpers.StatsLogger;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.Predictor;

/**
 * A request to run an experiment on the IPredict framework
 */
public class ExperimentRequest {

	/**
	 * Name of the dataset used
	 */
	String dataset;
	
	/**
	 * Experiment's parameters
	 */
	private int sequenceCount;
	private int minS;
	private int prefixSize;
	private int suffixSize;
	private int kFold;
	
	/**
	 * Parameters per model
	 */
	HashMap<String, HashMap<String, String>> params;
	
	/**
	 * List of predictors
	 */
	private List<Predictor> predictor;
	
	
	
	public ExperimentRequest(String reqString) {
		
		JSONObject req = new JSONObject(reqString);
		
		params = new HashMap<String, HashMap<String,String>>();
		
		//Extracting basic parameters
		dataset = req.getString("dataset");
		sequenceCount = req.getInt("sequenceCount");
		minS = req.getInt("minS");
		prefixSize = req.getInt("prefixSize");
		suffixSize = req.getInt("suffixSize");
		kFold = req.getInt("kFold");
		
		//Extracting the models and their parameters
		//For each model for this experiment
		JSONArray modelsJSON = req.getJSONArray("model");
		for(int i = 0; i < modelsJSON.length(); i++) {
			
			String modelName = modelsJSON.getJSONObject(i).getString("name");
			params.put(modelName, new HashMap<String, String>());
			
			//Extracting parameters for this model, if any
			if(modelsJSON.getJSONObject(i).has("params")) {
				
				//For each parameter of this model 
				JSONArray paramsJSON = modelsJSON.getJSONObject(i).getJSONArray("params");
				for(int k = 0; k < paramsJSON.length(); k++) {
					
					//extracting key and value of this parameter for this model
					String key = paramsJSON.getJSONObject(k).getString("name");
					String value = paramsJSON.getJSONObject(k).getString("value");
					
					//Saving the value in the Params map
					HashMap<String, String> curParams = params.get(modelName);
					curParams.put(key, value);
					params.put(modelName, curParams);
				}
			}
			
		}
	}
	
	/**
	 * Executes this experiment request on the given Evaluator.
	 * @param evaluator
	 */
	public StatsLogger execute(Evaluator evaluator) {
	
		//Adding the dataset
		evaluator.addDataset(Format.valueOf(dataset), sequenceCount);
		
		//adding each selected model with their parameters to the evaluator
		for(String modelName : params.keySet()) {
			
			String modelParam = RequestHelper.HashMapToParamsString(params.get(modelName));
			
			evaluator.addPredictor(RequestHelper.StringToPredictor(modelName, modelName, modelParam));
		}
		
		//Starting the experiment
		evaluator.Start(Evaluator.KFOLD, kFold , false, false, false);
		
		return evaluator.stats;
	}
}
