package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.data.TimelineData;
import be.ac.ua.ansymo.cheopsj.visualizer.data.TimelinePoint;
import be.ac.ua.ansymo.cheopsj.visualizer.util.GraphicsUtils;
import be.ac.ua.ansymo.cheopsj.visualizer.views.widgets.DependencyDialog;

public class Timeline extends Composite {
	// Static Values
	private static Color COLOR_GRID = new Color(Display.getCurrent(), 200, 200, 200);
	private static Color COLOR_TICK = new Color(Display.getCurrent(), 0,0,0);
	private static Color COLOR_LINE = new Color(Display.getCurrent(), 255,255,255);
	private static Color COLOR_DEPENDENTS = new Color(Display.getCurrent(), 240,22,22);
	private static Color COLOR_DEPENDENCIES = new Color(Display.getCurrent(), 22,240,22);
	
	private static int DOMAIN_SPACING = 75;
	private static int RANGE_SPACING = 35;
	
	private static int PADDING_DEFAULT = 25;
	private static int PADDING_LABEL = 25;
	private static int TICK_SIZE = 4;
	// Painting related members
	private Canvas canvas = null;
	private Image timeline_image =  null;
	private boolean shouldRedrawTimeline = true;
	private boolean shouldShowDependents = false;
	private String dependentRelationDate = "";
	private String dependentRelationID = "";
	private boolean shouldShowDependecies = false;
	private String dependenciesRelationDate = "";
	private String dependenciesRelationID = "";
	
	private int markers = 0;
	private int range_size = 0;
	
	private final Point timelineSize = new Point(0,0);
	private final Point offset = new Point(0,0);
	private ScrollBar hBar = null;
	private ScrollBar vBar = null;
	
	private Composite parent = null;
	
	private TimelineData data_store = null;
		
	public Timeline(Composite parent, int domain_size, int range_size, int width, int height, TimelineData data) {
		super(parent, SWT.BORDER);
		this.setLayout(new FillLayout());
		
		this.canvas = new Canvas(this, SWT.NO_REDRAW_RESIZE | SWT.H_SCROLL | SWT.V_SCROLL);
		this.parent = parent;
		
		this.markers = domain_size;
		this.range_size = range_size;
		this.timelineSize.x = width;
		this.timelineSize.y = height; 
		
		this.data_store = data;
		
		
		this.initialize();
	}
	
	
	private void initialize() {				
		this.timeline_image = new Image(Display.getCurrent(), timelineSize.x, timelineSize.y);
		
		this.canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
					paint(e);			
			}
		});
		
		this.hBar = this.canvas.getHorizontalBar();
		this.vBar = this.canvas.getVerticalBar();
		
		this.hBar.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				((TimelineComposite) parent).scrollHorizontal(hBar.getSelection());
			}
		});
						
		this.hBar.setVisible(true);
		
		this.vBar.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				((TimelineComposite) parent).scrollVertical(vBar.getSelection());
			}
		});
		
		this.vBar.setVisible(false);
		
		
		this.canvas.addListener (SWT.Resize,  new Listener () {
			@Override
			public void handleEvent (Event e) {
				Rectangle rect = timeline_image.getBounds ();
				Rectangle client = canvas.getClientArea ();
				hBar.setMaximum (rect.width);
				vBar.setMaximum(rect.height);
				hBar.setThumb (Math.min (rect.width, client.width));
				vBar.setThumb(Math.min(rect.height, client.height));
				int hPage = rect.width - client.width;
				int hSelection = hBar.getSelection();
				if (hSelection >= hPage) {
					if (hPage <= 0) {
						hSelection = 0;
					}
					offset.x = -hSelection;
				}
				
				int vPage = rect.height - client.height;
				int vSelection = vBar.getSelection ();
				if (vSelection >= vPage) {
					if (vPage <= 0) vSelection = 0;
					offset.y = -vSelection;
				}
				canvas.redraw ();
			}
		});

		this.canvas.addMouseListener(new MouseListener() {
			
			Menu popupMenu = null;
			@Override
			public void mouseDown(MouseEvent e) {
				popupMenu = new Menu(getShell(), SWT.POP_UP);
				canvas.setMenu(popupMenu);
				final Point selection = new Point(e.x - offset.x, e.y - offset.y);
				if (hasChangeAtPoint(e.x - offset.x, e.y - offset.y)) {
					MenuItem showChange = new MenuItem(popupMenu, SWT.NONE);
					showChange.setText("Show Info");
					showChange.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							showChangeInfoForPoint(selection.x, selection.y);
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
					MenuItem sep = new MenuItem(popupMenu, SWT.SEPARATOR);
					MenuItem inspectRel = new MenuItem(popupMenu, SWT.NONE);
					inspectRel.setText("Inspect relations");
					inspectRel.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							showRelationDialog(selection.x, selection.y);
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							
						}
					});
					MenuItem showDept = new MenuItem(popupMenu, SWT.NONE);
					showDept.setText("Show Dependents");
					showDept.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							showDependentRelation(selection.x, selection.y);
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
					MenuItem hideDept = new MenuItem(popupMenu, SWT.NONE);
					hideDept.setText("Hide Dependents");
					hideDept.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							hideDependentRelation();
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
					if (shouldShowDependents == false) {
						hideDept.setEnabled(false);
					}
					MenuItem showDepe = new MenuItem(popupMenu, SWT.NONE);
					showDepe.setText("Show Dependencies");
					showDepe.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							showDepenciesRelation(selection.x, selection.y);
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
					MenuItem hideDepe = new MenuItem(popupMenu, SWT.NONE);
					hideDepe.setText("Hide Dependencies");
					hideDepe.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							hideDependenciesRelation();
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
					if (shouldShowDependecies == false) {
						hideDepe.setEnabled(false);
					}
					MenuItem sep2 = new MenuItem(popupMenu, SWT.SEPARATOR);
				}
				MenuItem exit = new MenuItem(popupMenu, SWT.NONE);
				exit.setText("Exit");
				exit.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						popupMenu.setVisible(false);
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						popupMenu.setVisible(false);
					}
				});
				
				if (e.button == 1) {
					final Canvas canvas = (Canvas)e.widget;
					Point point = canvas.toDisplay(e.x, e.y);
					popupMenu.setLocation(point.x, point.y);
					popupMenu.setVisible(true);
				}
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {}

			@Override
			public void mouseUp(MouseEvent e) {}
		});
	}
	
	private void paint(PaintEvent e) {		
		if (this.shouldRedrawTimeline) {
			GC gc = new GC(this.timeline_image);
			gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			gc.fillRectangle(gc.getClipping());
			gc.setForeground(COLOR_GRID);
			
			int[] temp_y_coordinates = new int[this.range_size];
			
			// draw the horizontal lines for the y axis
			for (int i = 0; i < this.range_size; ++i) {
				int x1 = 0;
				int x2 = this.timelineSize.x;
				int y1 = this.PADDING_DEFAULT + (i*RANGE_SPACING);
				int y2 = y1;
				
				gc.setForeground(COLOR_LINE);
				gc.drawLine(x1 + offset.x, y1, x2 + offset.x, y2);
				
				temp_y_coordinates[i] = y1;
			}
			
			Vector<String> dateLabels = this.data_store.getChangeDateLabels();
			
			for (int i = 0; i < this.markers; ++i) {
				int x0 = PADDING_DEFAULT + (i * DOMAIN_SPACING) + offset.x;
				int x1 = x0;
				int y0 = 0;
				int y1 = y0 + TICK_SIZE;
				
				for (int j = 0; j < temp_y_coordinates.length; ++j) {
					Point p = new Point(x0, temp_y_coordinates[j]);
					String entId = this.data_store.getEntityIDForIndex(j);
					String date = dateLabels.get(i);
					if (this.data_store.changeOccured(entId, date)) {
						TimelinePoint tlp = new TimelinePoint(entId, date, p);
						this.data_store.addTimelinePoint(tlp);
					}
				}
			}
			
			gc.setForeground(COLOR_GRID);
			gc.drawLine(offset.x, timelineSize.y, timelineSize.x + offset.x, timelineSize.y);
			

			for (Entry<String, List<IChange>> entry : this.data_store.getMEC().entrySet()) {
				String entID = entry.getKey();
				for (IChange change : entry.getValue()) {
					@SuppressWarnings("deprecation")
					String sDate = change.getTimeStamp().getDate() + "-" + (change.getTimeStamp().getMonth() +1) 
							+ "-" + (change.getTimeStamp().getYear()+1900);
					if (this.data_store.changeOccured(entID, sDate)) {
						Point point = this.data_store.getPointForChange(change);
						if (point == null) {
							System.err.println("POINT WAS NULL FOR: " + entID + " - " + sDate);
							continue;
						}
						Image img = ((AtomicChange) change).getIcon();
						int x_offset = img.getBounds().width / 2;
						int y_offset = img.getBounds().height / 2;
						gc.drawImage(img, point.x - x_offset, point.y - y_offset);
					}
				}
			}
			
			// Draw Dependents
			if (shouldShowDependents) {
				gc.setForeground(COLOR_DEPENDENTS);
				Point focus = this.data_store.getPointForFocusEntity(dependentRelationID, dependentRelationDate);
				Vector<Point> point_to = this.data_store.getDependentsLocations(dependentRelationID, dependentRelationDate);
				
				for (Point pt : point_to) {
					gc.drawLine(focus.x, focus.y, pt.x, pt.y);
				}
			}
			
			// Draw Dependencies
			if (shouldShowDependecies) {
				gc.setForeground(COLOR_DEPENDENCIES);
				Point focus = this.data_store.getPointForFocusEntity(dependenciesRelationID, dependenciesRelationDate);
				Vector<Point> point_to = this.data_store.getDependenciesLocations(dependenciesRelationID, dependenciesRelationDate);
				
				for (Point pt : point_to) {
					gc.drawLine(focus.x, focus.y, pt.x, pt.y);
				}
			}
			
			this.shouldRedrawTimeline = false;
			gc.dispose();
		}
		e.gc.drawImage(this.timeline_image, offset.x, offset.y);
	}
	
	public void scrollVertical(int selection) {
		int destY = -selection - offset.y;
		this.canvas.scroll(0, destY, 0, 0, timelineSize.x, timelineSize.y, false);
		this.offset.y = -selection;
		this.vBar.setSelection(selection);
	}
	
	public void scrollHorizontal(int selection) {
		int destX = -selection - offset.x;
		this.canvas.scroll(destX, 0, 0, 0, timelineSize.x, timelineSize.y, false);
		this.offset.x = -selection;
		this.hBar.setSelection(selection);
	}
	
	public void refresh() {
		this.shouldRedrawTimeline = true;
		this.canvas.redraw();
	}
	
	public int getHeightOfCanvas() {
		return this.timelineSize.y;
	}
	
	public int getWidthOfCanvas() {
		return this.timelineSize.x;
	}
	
	public int getTimelineRangeSize() {
		return this.range_size;
	}
	
	public int getTimelineDomainSize() {
		return this.markers;
	}
	
	private boolean hasChangeAtPoint(int x, int y) {
		for (TimelinePoint point : this.data_store.getTimelinePoints()) {
			if (Math.abs(point.getTimelinePoint().x - x) < 10) {
				if (Math.abs(point.getTimelinePoint().y - y) < 10) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void showChangeInfoForPoint(int x, int y) {
		String entID = null;
		String cDate = null;
		for (TimelinePoint point : this.data_store.getTimelinePoints()) {
			if (Math.abs(point.getTimelinePoint().x - x) < 10) {
				if (Math.abs(point.getTimelinePoint().y - y) < 10) {
					entID = point.getEntityID();
					cDate = point.getChangeDate();
					break;
				}
			}
		}
		
		if (entID == null || cDate == null) {
			System.err.println("NO ENTITY FOUND");
			return;
		}
		
		String cMessage = "Change information for '"+entID+"'\n";
		cMessage += "   Change date: " + cDate + "\n";
		int index = 1;
		Vector<String> previousChanges = new Vector<String>();
		boolean doContinue = false;
		for (IChange change : this.data_store.getMEC().get(entID)) {
			String date = change.getTimeStamp().getDate() + "-" + (change.getTimeStamp().getMonth() +1) 
					+ "-" + (change.getTimeStamp().getYear()+1900);
			String temp = date+change.getChangeType()+change.getFamixType()+change.getUser()+change.getIntent();
			for (String s : previousChanges) {
				if (s.equals(temp)) {
					doContinue = true;
				}
			}
			previousChanges.add(temp);
			if (date.equals(cDate) && !doContinue) {
				cMessage += "\n   Change " + index +":\n";
				cMessage += "   Change type:    " + change.getChangeType() + "\n";
				cMessage += "   Famix type:     " + change.getFamixType() + "\n";
				cMessage += "   By user:        " + change.getUser() + "\n";
				cMessage += "   Commit message: " + change.getIntent() + "\n";
				index++;
			}
			doContinue = false;
		}
		
		MessageDialog dialog = new MessageDialog(getShell(), 
												 "Change Information", 
												 null, 
												 cMessage, 
												 MessageDialog.INFORMATION, 
												 new String [] {"OK"}, 
												 0);
		dialog.open();
	}
	
	private void showDependentRelation(int x, int y) {
		String cDate = null;
		String entID = null;
		for (TimelinePoint point : this.data_store.getTimelinePoints()) {
			if (Math.abs(point.getTimelinePoint().x - x) < 10) {
				if (Math.abs(point.getTimelinePoint().y - y) < 10) {
					cDate = point.getChangeDate();
					entID = point.getEntityID();
					break;
				}
			}
		}
		
		if (cDate == null || entID == null) {
			System.err.println("NO ENTITY FOUND");
			return;
		}
		
		this.shouldShowDependents = true;
		this.shouldRedrawTimeline = true;
		this.dependentRelationDate = cDate;
		this.dependentRelationID = entID;
		this.canvas.redraw();
	}
	
	private void hideDependentRelation() {
		this.shouldShowDependents = false;
		this.shouldRedrawTimeline = true;
		this.canvas.redraw();
	}
	
	private void showDepenciesRelation(int x, int y) {
		String cDate = null;
		String entID = null;
		for (TimelinePoint point : this.data_store.getTimelinePoints()) {
			if (Math.abs(point.getTimelinePoint().x - x) < 10) {
				if (Math.abs(point.getTimelinePoint().y - y) < 10) {
					entID = point.getEntityID();
					cDate = point.getChangeDate();
					break;
				}
			}
		}
		
		if (cDate == null || entID == null) {
			System.err.println("NO ENTITY FOUND");
			return;
		}
		
		this.shouldShowDependecies = true;
		this.shouldRedrawTimeline = true;
		this.dependenciesRelationDate = cDate;
		this.dependenciesRelationID = entID;
		this.canvas.redraw();
	}
	
	private void hideDependenciesRelation() {
		this.shouldShowDependecies = false;
		this.shouldRedrawTimeline = true;
		this.canvas.redraw();
	}
	
	private void showRelationDialog(int x, int y) {
		String entID = null;
		String cDate = null;
		for (TimelinePoint point : this.data_store.getTimelinePoints()) {
			if (Math.abs(point.getTimelinePoint().x - x) < 10) {
				if (Math.abs(point.getTimelinePoint().y - y) < 10) {
					entID = point.getEntityID();
					cDate = point.getChangeDate();
					break;
				}
			}
		}
		
		if (entID == null || cDate == null) {
			System.err.println("NO ENTITY FOUND");
			return;
		}
		
		IChange change = this.data_store.getChangeForKey(entID, cDate);
		Vector<IChange> dept_vec = this.data_store.getDependentsForKey(entID, cDate);
		Vector<IChange> depe_vec = this.data_store.getDependenciesForKey(entID, cDate);
		
		DependencyDialog depdialog = new DependencyDialog(getShell());
		depdialog.setEntityData(entID, 
								cDate, 
								change.getChangeType(), 
								change.getFamixType(), 
								change.getUser(), 
								change.getIntent());
		depdialog.setRelationData(dept_vec, depe_vec);
		depdialog.create();
		depdialog.open();
	}
}
