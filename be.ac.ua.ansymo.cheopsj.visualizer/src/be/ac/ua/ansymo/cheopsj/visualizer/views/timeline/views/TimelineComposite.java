package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.jfree.ui.VerticalAlignment;

import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.data.TimelineData;

public class TimelineComposite extends Composite {

	private Timeline timeline = null;
	private TimelineLegend legend = null;
	private DomainLabelView domainView = null;
	private RangeLabelView rangeView = null;
	
	private TimelineData data_store = null;
	
	private int width = 0;
	private int height = 0;
	
	public TimelineComposite(Composite parent, String entityName) {
		super(parent, SWT.NONE);
		this.setLayout(setupLayout());
		this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		this.data_store = DataStore.getInstance().constructTimelineData(entityName);
		
		if (this.data_store == null) {
			this.data_store = new TimelineData(null, null, null, "");
		}
		this.initialize();
	}
	
	private GridLayout setupLayout() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 3;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.makeColumnsEqualWidth = false;
		return layout;
	}
	
	private void initialize() {
		
		int domainSize = this.data_store.getChangeDateLabels().size();
		int rangeSize = this.data_store.getNumberOfEntities();
		int w = 75 * domainSize + 50;
		int h = 35 * rangeSize + 50;
		
		this.rangeView = new RangeLabelView(this, h, rangeSize, this.data_store);
		GridData rvData = new GridData();
		rvData.verticalSpan = 2;
		rvData.horizontalSpan = 1;
		rvData.grabExcessHorizontalSpace = false;
		rvData.grabExcessVerticalSpace = true;
		rvData.horizontalAlignment = GridData.FILL;
		rvData.verticalAlignment = GridData.FILL;
		rvData.widthHint = 250;
		this.rangeView.setLayoutData(rvData);
		
		this.timeline = new Timeline(this, domainSize, rangeSize, w, h, this.data_store);
		GridData tlData = new GridData();
		tlData.verticalSpan = 2;
		tlData.horizontalSpan = 2;
		tlData.grabExcessHorizontalSpace = true;
		tlData.grabExcessVerticalSpace = true;
		tlData.horizontalAlignment = GridData.FILL;
		tlData.verticalAlignment = GridData.FILL;
		this.timeline.setLayoutData(tlData);
		
		this.legend = new TimelineLegend(this, SWT.NONE);
		GridData tgData = new GridData();
		tgData.verticalSpan = 1;
		tgData.horizontalSpan = 1;
		tgData.widthHint = 250;
		tgData.horizontalAlignment = GridData.FILL;
		tgData.verticalAlignment = GridData.FILL;
		legend.setLayoutData(tgData);
		
		this.domainView = new DomainLabelView(this, w, domainSize, this.data_store);
		GridData dvData = new GridData();
		dvData.verticalSpan = 1;
		dvData.horizontalSpan = 2;
		dvData.grabExcessHorizontalSpace = true;
		dvData.grabExcessVerticalSpace = false;
		dvData.horizontalAlignment = GridData.FILL;
		dvData.verticalAlignment = GridData.FILL;
		this.domainView.setLayoutData(dvData);
		
		this.width = 200 + w;
		this.height = 100 + h;
	}
	
	protected void scrollVertical(int step_size) {
		this.timeline.scrollVertical(step_size);
		this.rangeView.scrollVertical(step_size);
	}
	
	protected void scrollHorizontal(int step_size) {
		this.timeline.scrollHorizontal(step_size);
		this.domainView.scrollHorizontal(step_size);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}
