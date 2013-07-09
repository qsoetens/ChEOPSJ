package be.ac.ua.ansymo.cheopsj.distiller.connection;

import java.io.File;

import org.eclipse.team.svn.core.operation.file.SVNFileStorage;
import org.eclipse.team.svn.core.resource.IRepositoryResource;

import be.ac.ua.ansymo.cheopsj.distiller.connection.svnconnection.SVNConnector;

public class ConnectorFactory {
	
	/**
	 * 
	 * This method will create a specific type of connector based on the project.
	 * 
	 * @param userName The username used to connect with.
	 * @param userPassword The password for the username.
	 * @param projectFile The file of the project.
	 * 
	 * @return A connector which is responsible for the connection with the project.
	 * 
	 **/
	public static Connector createConnector(String userName, String userPassword, File projectFile) {
		
		//Try SVN
		IRepositoryResource repository_resource = SVNFileStorage.instance().asRepositoryResource(projectFile, true);
		if(repository_resource != null){
			//SVN Found! Filter URL and return SVNConnector
			String url = repository_resource.getUrl();
	        if (url.contains("/trunk/")) {
	            url = url.substring(0, url.indexOf("/trunk"));
	        } else if (url.contains("/tags/")) {
	            url = url.substring(0, url.indexOf("/tags"));
	        } else if (url.contains("/branches/")) {
	            url = url.substring(0, url.indexOf("/branches"));
	        }
	        return new SVNConnector(userName, userPassword, url); 
		}
		//Try other providers
		
		//No providers found
		return null;
	}
}
