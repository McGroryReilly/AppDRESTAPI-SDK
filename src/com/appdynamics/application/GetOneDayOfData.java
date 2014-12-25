package com.appdynamics.application;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

public class GetOneDayOfData {

	public GetOneDayOfData() {
		// TODO Auto-generated constructor stub
	}
	
	public int getData(String application, int numberOfHours)
	{
		
		 
		 DateTime currentTime = new DateTime();
		
		
		 DateTime roundedTime = new DateTime(currentTime.getYear(), currentTime.getMonthOfYear() ,
		 currentTime.getDayOfMonth(), currentTime.getHourOfDay(), 0);
		 
		 DateTime yesterday = roundedTime.minusHours(24);
		 GetOneHourOfData hourOfData = new GetOneHourOfData();
		 hourOfData.init();

		 System.out.println(yesterday.toString());
		 for (int i = 0; i < 24; i++) {
			 DateTime newTime = yesterday.plusHours(i);
			 DateTime futureHour = newTime.plusHours(1);
			 System.err.println(newTime.toString());
			 Map<String,String> row = hourOfData.getOneHourForApplication(application, newTime.getMillis(), futureHour.getMillis());
			 System.out.println(row);
		 }
		 

		 return 0;
		 
	}

	public static void main(String[] args) {
		GetOneDayOfData dayofData = new GetOneDayOfData();
		dayofData.getData("XfinityConnect", 24);
		

		

	}

}
