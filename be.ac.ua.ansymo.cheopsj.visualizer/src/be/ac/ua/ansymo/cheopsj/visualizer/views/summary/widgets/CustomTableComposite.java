/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.summary.widgets;

import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.data.SummaryData;

/**
 * The composite for a custom table for the Change Summary View
 * @author nicolasdemarbaix
 *
 */
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
	
	// Dimensions
	private static int CELL_WIDTH = 112;
	private static int CELL_HEIGHT = 20;
	private GridData gData = null;
	
	/**
	 * Public constructor
	 * @param parent (Composite) the parent component
	 * @param style (int) the SWT graphics style
	 */
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
	
	/**
	 * Setup the table header
	 */
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
	
	/**
	 * Setup the table data
	 */
	private void setupData() {
		SummaryData data = DataStore.getInstance().constructSummaryTableData();
		int totalCount = 0;
		int totalChangeCount = 0;
		int totalAdditionCount = 0;
		int totalRemovalCount = 0;
		
		for (Entry<String, int[]> entry : data.getTypeMap().entrySet()) {
			int entCount = entry.getValue()[0];
			int changeCount = entry.getValue()[1];
			int addCount = entry.getValue()[2];
			int remCount = entry.getValue()[3];
			
			totalCount += entCount;
			totalChangeCount += changeCount;
			totalAdditionCount += addCount;
			totalRemovalCount += remCount;
			
			this.addRow(entry.getKey(),
						String.valueOf(entCount),
						String.valueOf(changeCount),
						String.valueOf(addCount),
						String.valueOf(remCount));
		}
		
		this.addRow("","","","","");
		this.addRow("Total",
					String.valueOf(totalCount),
					String.valueOf(totalChangeCount),
					String.valueOf(totalAdditionCount),
					String.valueOf(totalRemovalCount));
	}
	
	/**
	 * Add a row to the table
	 * @param key (String) the key for the row
	 * @param elems (String) the number of entities for the type 'key'
	 * @param changes (String) the number of changes for the type 'key'
	 * @param adds (String) the number of additions for the type 'key'
	 * @param dels (String) the number of removals for the type 'key'
	 */
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
