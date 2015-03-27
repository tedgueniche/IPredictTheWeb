package datasets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NginxLogConverter extends WebLogConverter {

	@Override
	public void addLogInDatabase(String log, LogDatabase database) {
		/**
		 * Typical Nginx access log
		 * 192.168.174.8 - - [27/Mar/2015:17:51:35 +0000] "GET index.php HTTP/1.1" 200 99 "https://mysite.com/index.php" "Mozilla/5.0 (Linux; Android 5.0.1; Nexus 4 Build/LRX22C) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.96 Mobile Safari/537.36"
		 * 
		 * Obviously the line should not be parsed based on space character but we can hack it.
		 */
		
		//splitting the line into space separated chunks
		String[] chunks = log.split("\\s");
		
		//raw chunks extraction
		String ip = chunks[0]; //ip
		String dateStr = chunks[3] + " " + chunks[4]; //date, ex: [23/Mar/2015:18:04:44 +0000]
		String url = chunks[5] + chunks[6]; //requested url, ex: "GET /index.php
		String code = chunks[8]; //HTTP status code, ex: 200
		String uagent = log.substring(log.indexOf(chunks[11]) + 1, log.length() - 1); //user agent string, ex: Mozilla/5.0 (X11; Linux x86_64) Ap...
		
		//chunks adjustements
		dateStr = dateStr.substring(1, dateStr.length() - 1); // transformed to 23/Mar/2015:18:04:44 +0000
		Date date = null;
		try {
			date = (new SimpleDateFormat("dd/MMM/yyyy:kk:mm:ss Z").parse(dateStr));
		} catch (ParseException e) {
			System.err.println("Malformatted data: "+ dateStr);
			return;
		}
		url = url.substring(1, url.length()); //transformed to GET /index.php
		
		
		int userId = database.StringToId(ip + uagent);
		int itemId = database.StringToId(url);
		long timestamp = (int) (date.getTime() / 1000);

		database.addLog(userId, itemId, timestamp);
	}

}
