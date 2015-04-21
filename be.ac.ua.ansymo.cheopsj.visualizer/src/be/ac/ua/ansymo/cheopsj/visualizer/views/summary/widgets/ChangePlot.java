package be.ac.ua.ansymo.cheopsj.visualizer.views.summary.widgets;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.data.SummaryPlotData;

public class ChangePlot extends ChartComposite {
	private JFreeChart plot = null;
	private LowerBasedNumberAxis rangeAxis = null;
	private DateAxis domainAxis = null;
	private GridData gData = null;
	private boolean showAll = false;
	private boolean showAdd = false;
	private boolean showDel = false;
	
	private DateTickUnit domainTickUnit = null;
	
	// Time definitions
	private static long MILLIS_IN_SECOND = 1000;
	private static long MILLIS_IN_MINUTE = 60 * MILLIS_IN_SECOND;
	private static long MILLIS_IN_HOUR = 60 * MILLIS_IN_MINUTE;
	private static long MILLIS_IN_DAY = 24 * MILLIS_IN_HOUR;
	private static long MILLIS_IN_MONTH = 30 * MILLIS_IN_DAY;
	private static long MILLIS_IN_YEAR = 365 * MILLIS_IN_DAY;
	
	/**
	 * Default constructor
	 * @param comp - the parent composite
	 * @param style - the style of the widget
	 */
	public ChangePlot(Composite comp, int style) {
		super(comp, style, null, true);
				
		// Generate the dataset
		XYDataset data = this.createDataset();
		
		// Setup the plot
		this.plot = this.createChart(data);
		
		this.setChart(this.plot);
	}
	
	private XYDataset createDataset() {
		TimeSeries total_changes = new TimeSeries("Total changes");
		TimeSeries add_changes = new TimeSeries("Additions");
		TimeSeries del_changes = new TimeSeries("Deletions");
		
		// Construct the time series
		SummaryPlotData spd = DataStore.getInstance().constructSummaryPlotData();
		
		for (Map.Entry<String, int[]> entry : spd.getDateChangeCountMap().entrySet()) {
			if (entry.getValue().length < 3) 
				continue;
			String[] date_split = entry.getKey().split("-");
			Day day = new Day(Integer.valueOf(date_split[0]), Integer.valueOf(date_split[1]), Integer.valueOf(date_split[2]));
			total_changes.add(day, entry.getValue()[0]);
			add_changes.add(day, entry.getValue()[1]);
			del_changes.add(day, entry.getValue()[2]);
		}
		
		// Construct the data set
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		if (this.showAll) dataset.addSeries(total_changes);
		if (this.showAdd) dataset.addSeries(add_changes);
		if (this.showDel) dataset.addSeries(del_changes);
		
		return dataset;
	}
	
	private JFreeChart createChart(XYDataset data) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Change History", "Date", "Changes", data);
		this.domainAxis = (DateAxis) chart.getXYPlot().getDomainAxis();
		this.domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
		this.rangeAxis = new LowerBasedNumberAxis(chart.getXYPlot().getRangeAxis().getLabel());
		this.rangeAxis.setTickUnit(new NumberTickUnit(1));
		this.rangeAxis.setRange(0, 10);
		chart.getXYPlot().setRangeAxis(this.rangeAxis);
		return chart;
	}
	
	private int calculateTickTimes(Date start, Date end, DateTickUnitType tick_type, int multiple) {
		int result = multiple;
		long diff = end.getTime() - start.getTime();
		if (tick_type.equals(DateTickUnitType.YEAR)) {
			diff = diff / MILLIS_IN_YEAR;
		} else if (tick_type.equals(DateTickUnitType.MONTH)) {
			diff = diff / MILLIS_IN_MONTH;
		} else if (tick_type.equals(DateTickUnitType.DAY)) {
			diff = diff / MILLIS_IN_DAY; 
		} else if (tick_type.equals(DateTickUnitType.HOUR)) {
			diff = diff / MILLIS_IN_HOUR;
		} else if (tick_type.equals(DateTickUnitType.MINUTE)) {
			diff = diff / MILLIS_IN_MINUTE;
		}
		
		if ((diff/result) > 32) {
			result = (int) diff/32;
		}
		
		return result;
	}
	
	public void updatePlot() {
		this.rebuildPlot();
		this.redraw();
	}
	
	private void rebuildPlot() {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Change History", "Date", "Changes", createDataset());
		chart.getXYPlot().setDomainAxis(this.domainAxis);
		chart.getXYPlot().setRangeAxis(this.rangeAxis);
		this.plot = chart;
		this.setChart(this.plot);
	}
	
	public void updateVisibleData(boolean all, boolean add, boolean del) {
		this.showAll = all;
		this.showAdd = add;
		this.showDel = del;
	}
	
	public void updateDomainAxis(DateTickUnitType tick_type, int multiple, Date start, Date end, String date_format) {		
		// Setup the tick unit for the domain axis
		// The tick unit is based on the range and the tick unit type. 
		int times = this.calculateTickTimes(start, end, tick_type, multiple);
		this.domainTickUnit = new DateTickUnit(tick_type, times);
		this.domainAxis.setTickUnit(this.domainTickUnit);
		this.domainAxis.setDateFormatOverride(new SimpleDateFormat(date_format));
		this.domainAxis.setRange(start, end);
		this.domainAxis.setVerticalTickLabels(true);
	}
	
	/**
	 * Update the range axis of the plot
	 * @param start - the lower bound of the range
	 * @param end - the upper bound of the range
	 * @param tick - the step size of the range axis
	 */
	public void updateRangeAxis(double start, double end, double tick) {
		this.rangeAxis = new LowerBasedNumberAxis("Changes");
		this.rangeAxis.setRange(start, end);
		this.rangeAxis.setTickUnit(new NumberTickUnit(tick));
		this.plot.getXYPlot().setRangeAxis(this.rangeAxis);
	}
	
	public DateTickUnit getDomainDateTickUnit() {
		return this.domainTickUnit;
	}

}
