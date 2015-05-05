/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import be.ac.ua.ansymo.cheopsj.visualizer.views.summary.ChangeSummary;

/**
 * Handler for opening the change summary view
 * @author nicolasdemarbaix
 *
 */
public class OpenChangeSummaryHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		// Execute can only be performed if window is not null
		if (window == null)
			return null;
		
		IWorkbenchPage page = window.getActivePage();
		
		// Execute can only be performed if an active page is found
		if (page == null) 
			return null;
		
		try {
			page.showView(ChangeSummary.ID);
		} catch (PartInitException e) {
			System.err.println("CHEOPSJ.VISUALIZER (Error)"+
					"   OpenChangeGraphHandler::execute::Cannot show view"+
					"   "+e.getMessage());
		}
		return null;
	}

}
