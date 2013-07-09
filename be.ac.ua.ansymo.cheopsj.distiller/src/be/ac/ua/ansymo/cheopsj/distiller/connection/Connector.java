/**
 * 
 * @file: Connector.java
 * @Description: This file contains the abstract connector.
 * 
 * @author Chris Vesters, Glenn De Jonghe, Ward Loos
 * 
 **/
package be.ac.ua.ansymo.cheopsj.distiller.connection;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;



/**
 * 
 * The abstract Connector.
 * This class is the root of all types of connectors.
 * 
 **/
public abstract class Connector {
	
	// The url of the repository.
	protected String fURL = "";
	
	// The username of the user.
	protected String fUserName = "";
	
	// The password of the user.
	protected String fUserPassword = "";
	
	/**
	 * Constructor gets username and password to the repository.
	 * This will immediately initialize the connector as well.
	 * 
	 * @param userName The name of the user for the repository.
	 * @param userPassword The password for the user for the repository.
	 * @param url The url of the repository.
	 * 
	 **/
	public Connector(String userName, String userPassword, String url) {
		this.fUserName = userName;
		this.fUserPassword = userPassword;
		this.fURL = url;
	}
	
	/**
	 * 
	 * This method initializes the connector.
	 * 
	 **/
	public abstract void initialize();
	
	/**
	 * 
	 * This method will update the file to the specified revision.
	 * 
	 * @param file The file we want to update.
	 * @param revisionNumber The revision number to which we will update the file.
	 * @param monitor A monitor.
	 *
	 **/
	public abstract void updateToRevision(File file, long revisionNumber, IProgressMonitor monitor);
	
	/**
	 * 
	 * This method gets the current revision of a certain file.
	 * 
	 * @param file The file from which we want to know the current revision.
	 * 
	 * @return The current revision number.
	 *
	 **/
	public abstract long getCurrentRevision(File file);
	
	/**
	 * Find out what the revision number is of the latest (HEAD) revision.
	 * 
	 * @param file The given file for which we want to know what the latest revision number is (can be a directory).
	 *
	 * @return The revision number of the latest revision of the given file.
	 * 
	 **/
	public abstract long getHeadRevisionNumber(File file);
	
	/**
	 * 
	 * This method will get the commit message of a certain revision.
	 * 
	 * @param file The file on which we will be working.
	 * @param revisionNumber The number of the revision.
	 * @param handler The handler of the repository.
	 *
	 * @return The message of the specified revision.
	 *
	 **/
	public abstract void getCommitMessage(File file, long revisionNumber, LogEntryHandler handler);
	
	/**
	 * 
	 * This method returns the contents of a file.
	 * 
	 * @param file The file we want the contents of.
	 * @param revision The revision number we will deal with.
	 * 
	 * @return The contents of the file.
	 *
	 **/
	public abstract String getFileContents(String file, long revision);
	
	/**
	 * 
	 * This method returns a log entry handler for this type of repository.
	 * 
	 * @return
	 *
	 **/
	public abstract LogEntryHandler getLogEntryHandler();
}
