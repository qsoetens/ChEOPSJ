package be.ac.ua.ansymo.cheopsj.visualizer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.ChangeTimeline;

public class OpenChangeTimelineHandler extends AbstractHandler {

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
			page.showView(ChangeTimeline.ID);
		} catch (PartInitException e) {
			System.err.println("CHEOPSJ.VISUALIZER (Error)"+
					"   OpenChangeTimelineHandler::execute::Cannot show view"+
					"   "+e.getMessage());
		}
		return null;
	}

}
