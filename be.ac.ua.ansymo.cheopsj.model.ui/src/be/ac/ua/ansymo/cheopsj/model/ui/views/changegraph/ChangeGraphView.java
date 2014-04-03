package be.ac.ua.ansymo.cheopsj.model.ui.views.changegraph;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.ui.view.changeinspector.ChangeView;

public class ChangeGraphView extends ViewPart {
	
	public static final String ID = "be.ac.ua.ansymo.cheopsj.model.ui.view.changegraph.ChangeGraphView";

	private static GraphViewer viewer = null;

	ISelectionListener listener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection sel) {
			if (!(sel instanceof IStructuredSelection))
				return;
			IStructuredSelection ss = (IStructuredSelection) sel;
			Object o = ss.getFirstElement();
			if (o instanceof AtomicChange && viewerIsVisible())
				viewer.setInput(ss.toList());
		}

		private boolean viewerIsVisible() {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window == null)
				return false;

			// Get the active page
			IWorkbenchPage page = window.getActivePage();
			if (page == null)
				return false;

			IViewPart thisView = page.findView(ID);
			if(page.isPartVisible(thisView)){
				return true;
			}
			return false;
		}
	};
	public void createPartControl(Composite parent) {
		viewer = new GraphViewer(parent, SWT.BORDER);
		viewer.setContentProvider(new ChangeGraphViewContentProvider());
		viewer.setLabelProvider(new ChangeGraphViewLabelProvider());

		LayoutAlgorithm layout = new ChangeGraphViewLayout(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		viewer.setLayoutAlgorithm(layout, true);
		viewer.applyLayout();

		createToolbarButtons();

		getSite().getPage().addSelectionListener(listener);
	}

	@Override
	public void setFocus() {
	}

	private void createToolbarButtons() {
		IToolBarManager toolBarMgr = getViewSite().getActionBars().getToolBarManager();
		toolBarMgr.add(new GroupMarker("additions"));
	}

	public void resetViewLayout() {
		viewer.applyLayout();
	}

}
