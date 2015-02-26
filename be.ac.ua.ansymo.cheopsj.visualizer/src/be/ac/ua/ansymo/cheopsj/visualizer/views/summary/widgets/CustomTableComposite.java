package be.ac.ua.ansymo.cheopsj.visualizer.views.summary.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;

public class CustomTableComposite extends Composite {
	/**
	 * 
	 * @author Baz (http://stackoverflow.com/questions/23402993/swt-how-to-make-rounded-border-label)
	 *
	 */
	private static class RoundedLabel extends Canvas {
		private String text = "";
		private static final int MARGIN = 3;
		private static int CELL_WIDTH = 136;
		private static int CELL_HEIGHT = 5;
		public RoundedLabel(Composite parent, int style) {
			super(parent, style);
			
			addPaintListener(new PaintListener() {
				
				@Override
				public void paintControl(PaintEvent e) {
					RoundedLabel.this.paintControl(e);
				}
			});
		}
		
		public RoundedLabel(Composite parent, int style, String t) {
			super(parent, style);
			this.setSize(CELL_WIDTH,25);
			addPaintListener(new PaintListener() {
				
				@Override
				public void paintControl(PaintEvent e) {
					RoundedLabel.this.paintControl(e);
				}
			});
			
			this.text = t;
		}
	
		void paintControl(PaintEvent e) {
			Point rect = getSize();
			e.gc.fillRectangle(0,0,rect.x, 25);
			e.gc.drawRoundRectangle(0, 0, rect.x-2, 
									18, MARGIN, MARGIN);
			e.gc.drawText(text, MARGIN*8, 1);
		}
		
		public String getText() {
			return text;
		}
		
		public void setText(String t) {
			this.text = t;
			redraw();
		}	
		
		@Override
		public Point computeSize(int wHint, int hHint, boolean changed) {
			GC gc = new GC(this);
			Point pt = gc.stringExtent(text);
			gc.dispose();
			return new Point(CELL_WIDTH+10, pt.y+8);
		}
	}
	
	// Colors
	private static Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private static Color COLOR_GRAY = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	private static Color COLOR_LIGHT_GRAY = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
	
	// Fonts
	private static Font FONT_HEADER = new Font(Display.getCurrent(), "SansSerif", 14, SWT.BOLD);
	private static Font FONT_CELL = new Font(Display.getCurrent(), "SansSerif", 12, SWT.NONE);
	
	// Dimensions
	private static int CELL_WIDTH = 112;
	private static int CELL_HEIGHT = 20;
	private GridData gData = null;
	
	// Model
	DataStore data_store = DataStore.getInstance();

	public CustomTableComposite(Composite parent, int style) {
		super(parent, SWT.BORDER);
		
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 5;
		
		this.setLayout(layout);
		
		this.gData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gData.minimumHeight = CELL_HEIGHT;
		gData.minimumWidth = CELL_WIDTH;
		
		this.setupHeader();
		this.setupData();
	}
	
	private void setupHeader() {
		RoundedLabel h1 = new RoundedLabel(this, SWT.NONE, "");
		h1.setLayoutData(this.gData);
		RoundedLabel h2 = new RoundedLabel(this, SWT.NONE, "# Elements");
		h2.setLayoutData(this.gData);
		RoundedLabel h4 = new RoundedLabel(this, SWT.NONE, "# Changes");
		h4.setLayoutData(this.gData);
		RoundedLabel h5 = new RoundedLabel(this, SWT.NONE, "# Additions");
		h5.setLayoutData(this.gData);
		RoundedLabel h6 = new RoundedLabel(this, SWT.NONE, "# Deletions");
		h6.setLayoutData(this.gData);		
	}
	
	private void setupData() {
		// TODO Change by access to data store
		this.addRow("Packages", "20", "40", "30", "10");
		this.addRow("Classes", "40", "80", "60", "20");
		this.addRow("Methods", "120", "240", "180", "60");
		this.addRow("", "", "", "", "");
		this.addRow("Total", "180", "360", "270", "90");
	}
	
	private void addRow(String key, String elems, String changes, String adds, String dels) {
		RoundedLabel h1 = new RoundedLabel(this, SWT.NONE, key);
		h1.setLayoutData(this.gData);
		RoundedLabel h2 = new RoundedLabel(this, SWT.NONE, elems);
		h2.setLayoutData(this.gData);
		h2.setBackground(COLOR_WHITE);
		RoundedLabel h4 = new RoundedLabel(this, SWT.NONE, changes);
		h4.setLayoutData(this.gData);
		h4.setBackground(COLOR_WHITE);
		RoundedLabel h5 = new RoundedLabel(this, SWT.NONE, adds);
		h5.setLayoutData(this.gData);
		h5.setBackground(COLOR_WHITE);
		RoundedLabel h6 = new RoundedLabel(this, SWT.NONE, dels);
		h6.setLayoutData(this.gData);
		h6.setBackground(COLOR_WHITE);
	}

}
