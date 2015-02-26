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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

public class TimelineComposite extends Composite {

	private Timeline timeline = null;
	public TimelineComposite(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());
		
		this.initialize();
	}
	
	private void initialize() {
	}
	
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		shell.setText("Canvas test");
		
		final Canvas canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE | SWT.H_SCROLL);
		canvas.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		final Point timelineSize = new Point(84600, 50);
		final Point offset = new Point(0,0);
		final ScrollBar hBar = canvas.getHorizontalBar();
		
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				for (int x = 100; x < timelineSize.x; x += 100) {
					e.gc.drawLine(x + offset.x, 0, x + offset.x, 20);
					e.gc.drawText(Integer.toString(x), x + offset.x, 30, true);
				}
			}
		});
		
		hBar.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				int hSelection = hBar.getSelection();
				int destX = -hSelection - offset.x;
				canvas.scroll(destX, 0, 0, 0, timelineSize.x, timelineSize.y, false);
				offset.x = -hSelection;
			}
		});
		
		canvas.addListener(SWT.Resize, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				Rectangle client = canvas.getClientArea();
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
				shell.redraw();
			}
		});
		
		shell.open();
		while(!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}
