/**
 * 
 * @file: LogEntryHandler.java
 * @Description: This file contains the log entry handler.
 * 
 * @author Chris Vesters, Glenn De Jonghe, Ward Loos
 * 
 **/
package be.ac.ua.ansymo.cheopsj.distiller.connection;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.ac.ua.ansymo.cheopsj.distiller.changeextractor.ChangeExtractor;


/**
 * 
 * The LogEntryHandler.
 * This class holds all information of a revision.
 * 
 **/
public abstract class LogEntryHandler {
	
	public enum Change {ADDED, MODIFIED, DELETED};
	
	// The message of the entry.
	protected String message = "";
	
	// The paths that have changed due to the entry.
	protected Map <String, Change> paths = new HashMap <String, Change>();
	
	// The revision number of the entry.
	protected long revision = 0;
	
	// The date of the entry.
	protected Date date;
	
	// The user that committed the entry.
	protected String user = "";

	
	/**
	 * 
	 * This method returns the changed paths.
	 * 
	 * @return The changed paths.
	 *
	 **/
	public Map <String, Change> getChangedPaths() {
		return this.paths;
	}
	
	/**
	 * 
	 * This method returns whether the revision contains a bugfix.
	 * This is simply based on the message of the revision.
	 * If this message contains words such as bfix(es/ing), bbugs,
	 * bdefects, bpatch or bissues a bugfix will have occured, otherwise not.
	 * 
	 * @return True if the revision contains a bugfix, false otherwise.
	 *
	 **/
	public boolean entryIsBugFix() {

		final String KEYWORD_REGEX = "\\bfix(e[ds])?\\b|\\bfixing\\b|\\bbugs?\\b|\\bdefects?\\b|\\bpatch\\b|\\bissues?\\b";
		
		Pattern p = Pattern.compile(KEYWORD_REGEX);
		Matcher m = p.matcher(this.message);
		
		return m.find();
	}
	
	/**
	 * 
	 * This method will create a ChangeExtractor for this log entry.
	 * 
	 * @return The change extractor for the log entry.
	 *
	 **/
	public ChangeExtractor getExtractor() {
		return new ChangeExtractor(this.message, this.date, this.user);
	}
	
}