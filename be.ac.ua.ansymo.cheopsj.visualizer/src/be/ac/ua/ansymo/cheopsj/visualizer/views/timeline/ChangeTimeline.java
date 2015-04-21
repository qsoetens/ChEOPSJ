package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views.TimelineComposite;

public class ChangeTimeline extends ViewPart {
	
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.ChangeTimeline";
	private String subjectID = null;
	private Composite parent = null;
	
	@Override
	public void createPartControl(Composite par) {
		parent = par;
		parent.setLayout(new FillLayout());
		
		if (subjectID == null) 
			return;
		
		TimelineComposite tlc = new TimelineComposite(parent, subjectID);
		System.err.println("Timeline Composite created");
	}
	
	public void reCreatePartControl() {
		this.createPartControl(this.parent);
	}
	
	public void setSubjectID(String id) {
		this.subjectID = id;
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
