package com.appdynamics.application;

import java.text.DateFormat;
import java.util.Calendar;
import org.joda.time.DateTime;

public class ManageDays {

	public ManageDays() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		
		ManageDays days = new ManageDays();
		days.moreDates();
		days.useJodaTime();
	}

	public void moreDates() {
		
		final DateFormat df = DateFormat.getDateTimeInstance();
		final Calendar c = Calendar.getInstance();
		c.clear();
		for (c.set(2014, Calendar.DECEMBER, 1, 0, 0, 0);
		     c.get(Calendar.MONTH) == 12;
		     c.add(Calendar.HOUR_OF_DAY, 1))
		{
		  System.out.println(df.format(c.getTime()));
		}
		
		c.clear();
		c.set(2014, Calendar.DECEMBER, 1, 0, 0, 0);
	    int month = c.get(Calendar.MONTH);
	    c.add(Calendar.HOUR_OF_DAY, 1);
		int day = c.get(Calendar.MONDAY);
		System.out.printf("Month is %s and Day is %s", new Object[]{new Integer(month), new Integer(day)});
		
		System.out.println(" # # # # # # #  Times Stuff # # # # # # # ");
		
		for (int i = 0; i < 20; i++) {
			
			c.add(Calendar.HOUR_OF_DAY, 1);
			int newMonth = c.get(Calendar.MONDAY);
			int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
			int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
			System.out.printf("Month is %s and Day is %s and Hour of Day %s \n", 
					new Object[]{new Integer(newMonth), 
					new Integer(dayOfMonth),
					new Integer(hourOfDay)});
			
		}
	}
	
	private void init() {
		final DateFormat df = DateFormat.getDateTimeInstance();
		final Calendar calendar = Calendar.getInstance();
		calendar.clear();
		
		calendar.set(2014, Calendar.JANUARY, 1, 0, 0, 0);
		calendar.get(Calendar.YEAR); 
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		{
			
			System.out.println(df.format(calendar.getTime()));
			
		}

	}
	
	public void useJodaTime()
	{
		
		   DateTime dt = new DateTime();
		   int dayOfTheWeek = dt.getDayOfWeek();
		   int era = dt.getEra();
		   int jyear =  dt.getYear();
		   int jweekyear = dt.getWeekyear();
		   int jcentury = dt.getCenturyOfEra();
		   int jyearera = dt.getYearOfEra();
		   int jyearofcentury = dt.getYearOfCentury();
		   int jmonthofYear = dt.getMonthOfYear();
		   int jweekofweek = dt.getWeekOfWeekyear();
		   int jyearofyear = dt.getDayOfYear();
		   int jdayofmonth = dt.getDayOfMonth();
		   int jdayofweek = dt.getDayOfWeek();
		   
		   System.out.printf( "Day of the Week %s \n ", new Object[]{new Integer(dayOfTheWeek)}); 
		   System.out.printf( "Year is  %s \n ", new Object[]{new Integer(jyear)}); 
		   System.out.printf( "Day of the WeekYear  %s \n ", new Object[]{new Integer(jweekyear)}); 
		   
		   
		
		
	}

}
