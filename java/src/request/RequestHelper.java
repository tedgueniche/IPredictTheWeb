package request;

import java.util.HashMap;

import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.CPT.CPT.CPTPredictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;

public class RequestHelper {

	
	public static Predictor StringToPredictor(String predictor, String tag, String params) {
		
		if(params == null) {
			params = "";
		}
		
		if(tag == null) {
			tag = predictor;
		}
		
		switch(predictor) {
		
		case "akom":
			return new MarkovAllKPredictor(tag, params);
		
		case "cpt":
			return new CPTPredictor(predictor, params);
		
		case "dg":
			return new DGPredictor(tag, params);
		
		case "ppm":
			return new MarkovFirstOrderPredictor(tag, params);
			
//		case "tdag":
//			return new TDAGPredictor(tag, params);
		}
		

		return null;
	}
	
	public static String HashMapToParamsString(HashMap<String, String> params) {
		
		String output = "";
		
		for(String key : params.keySet()) {
			output += key + ":"+ params.get(key) + " ";
		}
		
		return output.trim();
	}
}
