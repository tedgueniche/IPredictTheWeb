package datasets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

/**
 * A database containing a set of logs stored as sessions (sequence)
 */
public class LogDatabase {

	private final int maxSessionDuration;
	
	private HashSet<Session> sessionDatabase;
	
	private HashMap<Integer, Session> activeSessions;
	
	
	/**
	 * Creates an empty database of logs stored as sessions
	 * @param maxSessionDuration Maximum time (in minutes) between two consecutive logs from the same user to be considered in the same session.
	 */
	public LogDatabase(int maxSessionDuration) {
		this.maxSessionDuration = maxSessionDuration;
		
		sessionDatabase = new HashSet<LogDatabase.Session>();
		activeSessions = new HashMap<Integer, Session>();
	}
	
	/**
	 * Adds a log in an active or a new session
	 * @param userId Unique identifier used to identify a session
	 * @param itemId Unique identified describing a ressource
	 * @param timestamp Timestamp in seconds of the log
	 */
	public void addLog(Integer userId, Integer itemId, long timestamp) {
		
		Session session = activeSessions.get(userId);
		
		Long lastRequestTime = (session != null) ? session.getLastRequestTime() : null;
		Long timeSinceLastRequest = (session != null) ? (timestamp - lastRequestTime) : 0;
		
		//if this request belongs in an active session
		if(session != null && ((float)timeSinceLastRequest / 60) <= maxSessionDuration) {
			
			//updating the active session by setting this new timestamp as the last request time
			session.addItem(itemId, timestamp);
			activeSessions.put(userId, session);
			
		}
		else {
			
			if(session != null) {
				
				//transferring this active session into the session database since this session is no longer active
				sessionDatabase.add(session);
				activeSessions.remove(itemId);
			}
			
			session = new Session(itemId, timestamp);
			activeSessions.put(userId, session);
		}
	}
	
	/**
	 * Closed all active session and transfer them into the session database
	 */
	public void finalizeSessions() {
		
		for(Entry<Integer, Session> activeSession : activeSessions.entrySet()) {
			sessionDatabase.add(activeSession.getValue());
		}
		
		activeSessions.clear();
	}
	
	
	public HashSet<Session> getSession() {
		return sessionDatabase;
	}
		
	/**
	 * Given a String, returns an id as an integer
	 */
	public int StringToId(String...inputs) {
		
		String output = "";
		
		for(String input : inputs) {
			output += input;
		}
		
		return Math.abs(output.hashCode()) % 10000000;
	}
	
	/**
	 * Represents a log session containing a sequence of logs (as itemId) and the last time the session was active
	 */
	public class Session {
		
		private long lastRequestTime;
		private Sequence sequence;
		
		public Session(int itemId, long timestamp) {
			sequence = new Sequence(-1);
			addItem(itemId, timestamp);
		}
		
		public long getLastRequestTime() {
			return lastRequestTime;
		}
		
		public Sequence getSequence() {
			return sequence;
		}
		
		public void addItem(int itemId, long timestamp) {
			lastRequestTime = timestamp;
			sequence.addItem(new Item(itemId));
		}
		
		
	}
}
