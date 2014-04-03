package be.ac.ua.ansymo.cheopsj.model.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.handlers.HandlerUtil;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ui.view.changeinspector.ChangeView;

public class ClearChangeModel extends AbstractHandler {
	/**
	 * Load the state of the model
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ModelManager.getInstance().clearModel();
		IViewPart findView = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView("be.ac.ua.ansymo.cheopsj.model.ui.view.changeinspector.ChangeView");
		ChangeView view = (ChangeView) findView;
		view.refresh();
		return null;
	}
}
