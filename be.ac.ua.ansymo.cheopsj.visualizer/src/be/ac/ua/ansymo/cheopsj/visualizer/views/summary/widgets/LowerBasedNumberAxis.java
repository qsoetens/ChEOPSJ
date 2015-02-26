package be.ac.ua.ansymo.cheopsj.visualizer.views.summary.widgets;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.Range;

public class LowerBasedNumberAxis extends NumberAxis {
	private static final long serialVersionUID = 1777248642156264874L;
	
	public LowerBasedNumberAxis(String label) {
		super(label);
	}
	
	@Override
	protected double calculateLowestVisibleTickValue() {
		return getRange().getLowerBound();
	}
	
	@Override
	protected int calculateVisibleTickCount() {
		double unit = getTickUnit().getSize();
		Range range = getRange();
		return (int)Math.ceil((range.getUpperBound() - range.getLowerBound() + 1) / unit);
	}

}
