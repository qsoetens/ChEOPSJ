package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import be.ac.ua.ansymo.cheopsj.visualizer.data.TimelineData;
import be.ac.ua.ansymo.cheopsj.visualizer.util.GraphicsUtils;

public class RangeLabelView extends Composite {

	private static int RANGE_SPACING = 35;
	private static Color COLOR_LINE = new Color(Display.getCurrent(), 255,255,255);	
	private static Color COLOR_LABEL = new Color(Display.getCurrent(), 0, 0, 0);
	private static int TICK_SIZE = 4;
	private static int PADDING_DEFAULT = 25;
	
	private Canvas canvas = null;
	private ScrollBar vBar = null;
	private ScrollBar hBar = null;
	
	private final Point canvas_size = new Point(500,0);
	private final Point offset = new Point(0,0);
	private int range_size;
	
	private Composite parent = null;
	
	private Image rangelabel_image = null;
	
	private boolean shouldRedrawImage = true;
	
	private TimelineData data_store = null;
	
	public RangeLabelView(Composite parent, int height, int range, TimelineData data) {
		super(parent, SWT.BORDER);
		this.setLayout(new FillLayout());
		
		this.data_store = data;

		this.parent = parent;
		this.range_size = range;
		this.canvas = new Canvas(this, SWT.NO_REDRAW_RESIZE | SWT.H_SCROLL | SWT.V_SCROLL);

		this.initialize(height);
		
		this.rangelabel_image = new Image(Display.getCurrent(), this.canvas_size.x, this.canvas_size.y);
	}
	
	private void initialize(int height) {
		this.canvas_size.y = height;
		this.vBar = this.canvas.getVerticalBar();
		this.hBar = this.canvas.getHorizontalBar();
		this.vBar.setVisible(false);
		this.hBar.setVisible(false);
				
		this.setupListeners();		
	}
	
	private void setupListeners() {
		// TODO add hBar and hBarListener
		this.canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
					paint(e);			
			}
		});
		this.vBar.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				((TimelineComposite) parent).scrollVertical(vBar.getSelection());
			}
		});
		this.hBar.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int selection = hBar.getSelection();
				int destX = -selection - offset.x;
				canvas.scroll(destX, 
								   0, 
								   0, 
								   0, 
								   rangelabel_image.getBounds().width, 
								   rangelabel_image.getBounds().height, 
								   false);
				offset.x = -selection;
				hBar.setSelection(selection);
			}
			
		});
		this.canvas.addListener (SWT.Resize,  new Listener () {
			@Override
			public void handleEvent (Event e) {
				Rectangle rect = rangelabel_image.getBounds ();
				Rectangle client = canvas.getClientArea ();
				vBar.setMaximum (rect.height);
				hBar.setMaximum(rect.width);
				vBar.setThumb (Math.min (rect.height, client.height));
				hBar.setThumb(Math.min(rect.width, client.width));
				int vPage = rect.height - client.height;
				int vSelection = vBar.getSelection ();
				if (vSelection >= vPage) {
					if (vPage <= 0) vSelection = 0;
					offset.y = -vSelection;
				}
				int hPage = rect.width - client.width;
				int hSelection = hBar.getSelection();
				if (hSelection >= hPage) {
					if (hPage <= 0) hSelection = 0;
					offset.x = -hSelection;
				}
				canvas.redraw ();
			}
		});
	}
	
	private void paint(PaintEvent e) {
		if (this.shouldRedrawImage) {
			this.rangelabel_image = new Image(Display.getCurrent(), this.canvas_size.x, this.canvas_size.y);
			GC gc = new GC(this.rangelabel_image);
			gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			gc.fillRectangle(gc.getClipping());
			
			gc.setForeground(COLOR_LINE);
			// Draw the vertical ticks
			for (int i = 0; i < this.range_size; ++i) {
				int x1 = this.canvas_size.x;
				int x2 = 0;
				int y1 = PADDING_DEFAULT + (i*RANGE_SPACING);
				int y2 = y1;
				
				gc.drawLine(x1, y1, x2, y2);
			}
			
			gc.setForeground(COLOR_LABEL);
			// Draw the package name labels
			for (int i = 0; i < this.range_size; ++i) {
				int x = 2;
				int y = PADDING_DEFAULT + (i*RANGE_SPACING) - 10;
				gc.drawText(this.data_store.getEntityIDForIndex(i), x, y);
			}
			
			gc.dispose();
			this.shouldRedrawImage = false;
		}
		e.gc.drawImage(this.rangelabel_image, this.offset.x, this.offset.y);
	}
	
	public void scrollVertical(int selection) {
		int destY = -selection - offset.y;
		this.canvas.scroll(0, 
						   destY, 
						   0, 
						   0, 
						   this.rangelabel_image.getBounds().width, 
						   this.rangelabel_image.getBounds().height, 
						   false);
		this.offset.y = -selection;
		this.vBar.setSelection(selection);
	}
}
