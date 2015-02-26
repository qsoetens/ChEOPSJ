package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import be.ac.ua.ansymo.cheopsj.visualizer.util.GraphicsUtils;
import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.data.ItemDataset;

public class Timeline extends Composite {
	// Static Values
	private static Color COLOR_GRID = new Color(Display.getCurrent(), 200, 200, 200);
	private static List<Color> COLOR_LINE = new ArrayList<Color>() {
		{
			new Color(Display.getCurrent(), 44, 102, 230);
			new Color(Display.getCurrent(), 196, 10, 32);
			new Color(Display.getCurrent(), 196, 10, 196);
			new Color(Display.getCurrent(), 10, 196, 193);
			new Color(Display.getCurrent(), 10, 196, 44);
			new Color(Display.getCurrent(), 225, 232, 12);
			new Color(Display.getCurrent(), 225, 175, 36);
		}
	};
	
	private static int[] LINE_TYPE = {SWT.LINE_SOLID, SWT.LINE_DASH, SWT.LINE_DASHDOT, SWT.LINE_DOT};
	
	private static int MAX_VISIBLE_RANGE_ELEMENTS = 5;
	private static int MAX_VISIBLE_DOMAIN_MARKERS = 20;
	
	private static int DOMAIN_SPACING = 20;
	private static int RANGE_SPACING = 30;
	
	private static int PADDING_DEFAULT = 25;
	private static int PADDING_LABEL = 25;
		
	// Data related members
	private ItemDataset data = null;
	private Date earliest = null;
	private Date latest = null;
	
	// Graphics related members
	private TimelineLegend legend = null;
	
	// Painting related members
	private Canvas canvas = null;
	
	private int CURRENT_COLOR_LINE_INDEX = 0;
	private int MAX_COLOR_LINE_INDEX = 6;
	private int CURRENT_LINE_TYPE_INDEX = 0;
	private int MAX_LINE_TYPE_INDEX = 3;
	
	private int markers = 0;
	private int range_size = 0;
	
	private int canvas_size = 0;
	private int canvas_height = 0;
	
	private final Point timelineSize = new Point(0,0);
	private final Point offset = new Point(0,0);
	private ScrollBar hBar = null;
	private ScrollBar vBar = null;
	
	private Composite parent = null;
	
	public Timeline(Composite parent, ItemDataset data) {
		super(parent, SWT.NONE);
		this.data = data;
		this.setLayout(new FillLayout());
		this.canvas = new Canvas(this, SWT.NO_REDRAW_RESIZE | SWT.H_SCROLL | SWT.V_SCROLL);
		if (data != null) this.earliest = data.getEarliest();
		if (data != null) this.latest = data.getLatest();
		this.parent = parent;
		this.initialize();
		
		this.canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
					paint(e);			
			}
		});
		
		
	}
	
	
	private void initialize() {
		int width = 84600;
		int height = 420;
		
		this.timelineSize.x = width;
		this.timelineSize.y = height; 
		
		this.hBar = this.canvas.getHorizontalBar();
		this.vBar = this.canvas.getVerticalBar();
		
		this.hBar.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				int hSelection = hBar.getSelection() * 5;
				int destX = -hSelection - offset.x;
				canvas.scroll(destX, 0, 0, 0, timelineSize.x, timelineSize.y+10, false);
				offset.x = -hSelection;
			}
		});
				
		this.hBar.setVisible(true);
		
		this.canvas.addListener(SWT.Resize, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				Rectangle client = getClientArea();
				hBar.setMaximum(timelineSize.x);
				hBar.setThumb(Math.min(timelineSize.x, client.width));
				int hPage = timelineSize.y - client.width;
				int hSelection = hBar.getSelection();
				if (hSelection >= hPage) {
					if (hPage <= 0) {
						hSelection = 0;
					}
					offset.x = -hSelection;
				}
				parent.redraw();
			}
		});
	}
	
	private void paint(PaintEvent e) {
		e.gc.setForeground(COLOR_GRID);
		int width = 84600;
		int height = 420;
		e.gc.drawRectangle(PADDING_DEFAULT + PADDING_LABEL + offset.x, PADDING_DEFAULT, width - (2 * PADDING_DEFAULT) - PADDING_LABEL + offset.x, height - (2* PADDING_DEFAULT) - PADDING_LABEL);
		Vector<Point> crossings = new Vector<Point>();
		// Draw Y-Axis
		for (int i = 0; i < 5; ++i) {
			int x0 = PADDING_DEFAULT + PADDING_LABEL + offset.x;
			int x1 = 4 + x0;
			int y0 = height - ((i*(height - PADDING_DEFAULT*2 - PADDING_LABEL)) / 5 + PADDING_DEFAULT + PADDING_LABEL);
			int y1 = y0;
			
			e.gc.setForeground(new Color(Display.getCurrent(), 255,255,255));
			e.gc.drawLine(PADDING_DEFAULT + PADDING_LABEL + 1 + 4 + offset.x, y0, width - PADDING_DEFAULT + offset.x, y1);
			
			e.gc.setForeground(new Color(Display.getCurrent(), 0,0,0));
			e.gc.drawLine(x0, y0, x1, y1);
			
			crossings.add(new Point(0, y0));
		}
		Vector<Point> actual_crossings = new Vector<Point>();
		// Draw X-Axis 
		for (int i = 0; i < 31; ++i) {
			int x0 = (i) * (680 - PADDING_DEFAULT * 2 - PADDING_LABEL) / (30) + PADDING_DEFAULT + PADDING_LABEL + offset.x;
			int x1 = x0;
			int y0 = 420 - PADDING_DEFAULT - PADDING_LABEL;
			int y1 = y0 - 4;
			
			e.gc.setForeground(new Color(Display.getCurrent(), 255,255,255));
			e.gc.drawLine(x0, 420 - PADDING_DEFAULT - PADDING_LABEL - 4, x1, PADDING_DEFAULT);
			// Draw the label
			String xLabel = "";
			if (i < 9) {
				xLabel = "0" + (i+1) + "-01-15";
			} else {
				xLabel = (i+1) + "-01-15";
			}
			Image label = GraphicsUtils.createRotatedText(xLabel, 
														  e.gc.getFont(), 
														  new Color(Display.getCurrent(), 255,10,10), 
														  e.gc.getBackground(), 
														  SWT.UP);
			e.gc.drawImage(label, x0 - label.getBounds().width/2 , y0 + 1);
			
			e.gc.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
			e.gc.drawLine(x0, y0, x1, y1);
			
			for (int k = 0; k < crossings.size(); k++) {
				if (k == 0) continue;
				actual_crossings.add(new Point(x0, crossings.get(k).y));
			}
		}
		Image img = new Image(Display.getCurrent(), "/Users/nicolasdemarbaix/git/ChEOPSJ/be.ac.ua.ansymo.cheopsj.visualizer/icons/add.gif");
		for (int i = 0; i < actual_crossings.size(); ++i) {
			e.gc.drawImage(img, actual_crossings.get(i).x- img.getBounds().width/2, actual_crossings.get(i).y - img.getBounds().height/2);
		}
		
		
		e.gc.drawLine(PADDING_DEFAULT + PADDING_LABEL + offset.x, height - PADDING_DEFAULT - PADDING_LABEL, PADDING_DEFAULT + PADDING_LABEL + offset.x, PADDING_DEFAULT);
		e.gc.drawLine(PADDING_DEFAULT + PADDING_LABEL + offset.x,height - PADDING_DEFAULT - PADDING_LABEL, width - PADDING_DEFAULT + offset.x, height - PADDING_DEFAULT - PADDING_LABEL);
	}
	
	public static void main(String[] args) {
		Display d = Display.getCurrent();
		Shell shell = new Shell(d);
		shell.setText("Test");
		shell.setLayout(new FillLayout());
		shell.setBackground(new Color(d, 255, 255, 255));
		
		Timeline timeline = new Timeline(shell, null);
		shell.open();
		while(!shell.isDisposed()) {
			if (!d.readAndDispatch()) {
				d.sleep();
			}
		}
		d.dispose();
	}
}
