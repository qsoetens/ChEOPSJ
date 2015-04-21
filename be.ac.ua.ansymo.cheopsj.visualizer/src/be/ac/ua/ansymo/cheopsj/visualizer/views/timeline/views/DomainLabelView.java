package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class DomainLabelView extends Composite {
	
	private static Color COLOR_TICK = new Color(Display.getCurrent(), 0,0,0);
	private static int TICK_SIZE = 4;
	private static int DOMAIN_SPACING = 75;
	private static int PADDING_DEFAULT = 25;
	
	private static SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
	
	private Composite parent = null;
	private Canvas canvas = null;
	private ScrollBar hBar = null;
	
	private Image domainlabel_image = null;
	private boolean shouldRedrawImage = false;
	
	private final Point canvas_size = new Point(0,4*PADDING_DEFAULT);
	private final Point offset = new Point(0,0);
	private int domain_size = 0;
	
	private TimelineData data_store = null;

	public DomainLabelView(Composite parent, int width, int size, TimelineData data) {
		super(parent, SWT.BORDER);
		this.setLayout(new FillLayout());
		this.parent = parent;
		System.out.println("DOMAINLABELVIEW:DOMAINSIZE == " + size);
		this.domain_size = size;
		if (this.domain_size == 0) {
			this.domain_size = 100;
		}
		this.canvas_size.x = DOMAIN_SPACING * this.domain_size + 2*PADDING_DEFAULT;

		
		this.data_store = data;
		
		this.canvas = new Canvas(this, SWT.NO_REDRAW_RESIZE | SWT.H_SCROLL);
		initialize();
	}
	
	private void initialize() {
		this.hBar = this.canvas.getHorizontalBar();
		this.hBar.setVisible(false);
		setupListeners();
		
		this.domainlabel_image = new Image(Display.getCurrent(), this.canvas_size.x, this.canvas_size.y);
		this.shouldRedrawImage = true;
	}
	
	private void setupListeners() {
		this.canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				paint(e);
			}
		});
		this.hBar.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				((TimelineComposite) parent).scrollHorizontal(hBar.getSelection());
			}
		});
		this.canvas.addListener (SWT.Resize,  new Listener () {
			@Override
			public void handleEvent (Event e) {
				Rectangle rect = domainlabel_image.getBounds ();
				Rectangle client = canvas.getClientArea ();
				hBar.setMaximum (rect.width);
				hBar.setThumb (Math.min (rect.width, client.width));
				int vPage = rect.width - client.width;
				int vSelection = hBar.getSelection ();
				if (vSelection >= vPage) {
					if (vPage <= 0) vSelection = 0;
					offset.x = -vSelection;
				}
				canvas.redraw ();
			}
		});
	}
	
	private void paint(PaintEvent e) {
		if (shouldRedrawImage) {
			//this.domainlabel_image = new Image(Display.getCurrent(), this.canvas_size.x, this.canvas_size.y);
			GC gc = new GC(domainlabel_image);
			gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			gc.fillRectangle(gc.getClipping());
			gc.setForeground(COLOR_TICK);
			
			// draw the tick marks on the x axis
			for (int i = 0; i < this.domain_size; ++i) {
				int x0 = PADDING_DEFAULT + (i * DOMAIN_SPACING) + offset.x;
				int x1 = x0;
				int y0 = 0;
				int y1 = y0 + TICK_SIZE;
				
				gc.setForeground(COLOR_TICK);
				gc.drawLine(x0, y0, x1, y1);
			}
			
			/*for (int i = 0; i < this.domain_size; ++i) {
				Image label = GraphicsUtils.createRotatedText(i + "th Marker", 
															  gc.getFont(), 
															  new Color(Display.getCurrent(), 255,10,10), 
															  gc.getBackground(), 
															  SWT.UP);
				gc.drawImage(label, 
							 PADDING_DEFAULT + (i * DOMAIN_SPACING) + offset.x - label.getBounds().width/2, 
							 2*TICK_SIZE);
			}*/
			for (int k = 0; k < this.data_store.getChangeDateLabels().size(); ++k) {
				String date_string = this.data_store.getChangeDateLabels().get(k);
				Image label = GraphicsUtils.createRotatedText(date_string, 
															  gc.getFont(), 
															  new Color(Display.getCurrent(), 255,10,10), 
															  gc.getBackground(), 
															  SWT.UP);
				gc.drawImage(label, 
						 PADDING_DEFAULT + (k * DOMAIN_SPACING) + offset.x - label.getBounds().width/2, 
						 2*TICK_SIZE);
			}
			gc.dispose();
			this.shouldRedrawImage = false;
		}
		e.gc.drawImage(domainlabel_image, offset.x, offset.y);
	}
	
	public void scrollHorizontal(int selection) {
		int destX = -selection - offset.x;
		this.canvas.scroll(destX, 
						   0, 
						   0, 
						   0, 
						   this.domainlabel_image.getBounds().width, 
						   this.domainlabel_image.getBounds().height, 
						   false);
		this.offset.x = -selection;
		this.hBar.setSelection(selection);
	}
}
