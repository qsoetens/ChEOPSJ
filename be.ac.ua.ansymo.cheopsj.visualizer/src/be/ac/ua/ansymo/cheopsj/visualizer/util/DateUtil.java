/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Util class for Date related functionality
 * @author nicolasdemarbaix
 *
 */
public class DateUtil {
	private static DateUtil instance = null;
	
	public static DateUtil getInstance() {
		if (instance == null) {
			instance = new DateUtil();
		}
		return instance;
	}
	
	/**
	 * Construct a Date object based on the given year, month and day
	 * @param year (int) the given year
	 * @param month (int) the given month
	 * @param day (int) the given day
	 * @return (java.util.Date) Date object
	 */
	public Date constructDate(int year, int month, int day) {
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, day);
		return cal.getTime();
	}
	
	/**
	 * Construct a Date object based on the given year, month, day, hour, minute and second
	 * @param year (int) the given year
	 * @param month (int) the given month
	 * @param day (int) the given day
	 * @param hour (int) the given hour
	 * @param minute (int) the given minute
	 * @param second (int) the given second
	 * @return (java.util.Date) Date object
	 */
	public Date constructDateAndTime(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute, second);
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
	
	/**
	 * Calculates the amount of days between the dates d1 and d2
	 * The order of the objects does not influence the result. The dates are first checked for order
	 * @param d1 - Date object 
	 * @param d2 - Date object
	 * @return The amount of days between d1 and d2
	 */
	public int daysBetween(Date d1, Date d2) {
		boolean d1Befored2 = d1.before(d2);
		
		int DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
		
		if (d1Befored2) {
			return 1 + (int) (( d2.getTime() - d1.getTime()) / DAY_IN_MILLIS);
		} else {
			return 1 + (int) ((d1.getTime() - d2.getTime()) / DAY_IN_MILLIS);
		}
	}
	
	/**
	 * Construct a string representation of a given Date
	 * @param date (java.util.Date) the given date
	 * @return (String) string representation of the given date
	 */
	@SuppressWarnings("deprecation")
	public String constructDateString(Date date) {
		return date.getDate() + "-" + (date.getMonth()+1) + "-" + (date.getYear()+1900);
	}
	
	/**
	 * Get the successive date for a given instance
	 * @param current (java.util.Date) the current date
	 * @return (java.util.Date) the successive date
	 */
	public Date getNext(Date current) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	/**
	 * Get the previous date for a given instance
	 * @param current (java.util.Date) the current date
	 * @return (java.util.Date) the previous date
	 */
	public Date getPrevious(Date current) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}
}
