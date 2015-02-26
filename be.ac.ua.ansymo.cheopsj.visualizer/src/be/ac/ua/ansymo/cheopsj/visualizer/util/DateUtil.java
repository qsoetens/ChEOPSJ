package be.ac.ua.ansymo.cheopsj.visualizer.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	private static DateUtil instance = null;
	
	public static DateUtil getInstance() {
		if (instance == null) {
			instance = new DateUtil();
		}
		return instance;
	}
	
	public Date constructDate(int year, int month, int day) {
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, day);
		return cal.getTime();
	}
	
	/**
	 * Checks whether the date d1/m1/y1 happens before d2/m2/y2
	 * Also returns true if the dates are equal
	 * @param y1
	 * @param m1
	 * @param d1
	 * @param y2
	 * @param m2
	 * @param d2
	 * @return
	 */
	public boolean before(int y1, int m1, int d1, int y2, int m2, int d2) {
		if (y1 <= y2) {
			if (m1 <= m2) {
				if (d1 <= d2) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks whether the date d1/m1/y1 happens after d2/m2/y2
	 * also returns true if the dates are equal
	 * @param y1
	 * @param m1
	 * @param d1
	 * @param y2
	 * @param m2
	 * @param d2
	 * @return
	 */
	public boolean after(int y1, int m1, int d1, int y2, int m2, int d2) {
		if (y1 >= y2) {
			if (m1 >= m2) {
				if (d1 >= d2) {
					return true;
				}
			}
		}
		return false;
	}
}
