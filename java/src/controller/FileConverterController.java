package controller;

import java.io.IOException;

import datasets.ApacheLogConverter;

public class FileConverterController {

	
	public static void main(String...args) throws IOException {
		

//		String logFile = "/home/ted/tmp/wwwIPredictRequest/apache.logs";
//		String seqFile = "/home/ted/tmp/wwwIPredictRequest/apache.seq";
		
		String logFile = "/home/ted/tmp/wwwIPredictRequest/nginx.logs";
		String seqFile = "/home/ted/tmp/wwwIPredictRequest/nginx.seq";
		
		ApacheLogConverter converter = new ApacheLogConverter();
		int seqCount = converter.convert(logFile, seqFile, 15);
		
		System.out.println("Converted "+ seqCount + " sequences.");
		
	}
}
