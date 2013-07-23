/**
 * 
 * @file: SVNLogEntryHandler.java
 * @Description: This file contains the new SVN log entry handler.
 * 
 * @author Chris Vesters, Glenn De Jonghe, Ward Loos
 * 
 **/
package be.ac.ua.ansymo.cheopsj.distiller.connection.svnconnection;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import be.ac.ua.ansymo.cheopsj.distiller.connection.LogEntryHandler;

/**
 * 
 * The SVNLogEntryHandler.
 * This class is required to handle SVN logs.
 * 
 **/
public class SVNLogEntryHandler extends LogEntryHandler implements
		ISVNLogEntryHandler {

	/**
	 * 
	 * This method will be used from within SVN to gather all the information about a certain revision.
	 * @see org.tmatesoft.svn.core.ISVNLogEntryHandler#handleLogEntry(org.tmatesoft.svn.core.SVNLogEntry)
	 *
	 * @param entry The entry from which we want the data.
	 *
	 **/
	@Override
	public void handleLogEntry(SVNLogEntry entry) throws SVNException {
		this.message = entry.getMessage();
		this.revision = entry.getRevision();
		this.date = entry.getDate();
		this.user = entry.getAuthor();
		
		@SuppressWarnings("unchecked")
		Map <String, SVNLogEntryPath> changes = entry.getChangedPaths();
		if (changes != null) {
			Iterator <Entry <String, SVNLogEntryPath>> it = changes.entrySet().iterator();
			while (it.hasNext()) {
				Entry <String, SVNLogEntryPath> change = it.next();
				
				Change type;
				switch (change.getValue().getType()) {
				case SVNLogEntryPath.TYPE_ADDED: 
					type = Change.ADDED;
					break;
				case SVNLogEntryPath.TYPE_DELETED:
					type = Change.DELETED;
					break;
				case SVNLogEntryPath.TYPE_MODIFIED:
					type = Change.MODIFIED;
					break;
				default:
					type = null;
					break;
				}
				
				this.paths.put(change.getKey(), type);
			}
		}
	}
}