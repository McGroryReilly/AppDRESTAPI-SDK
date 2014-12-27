package com.appdynamics.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

public class GetOneDayOfData {

	public GetOneDayOfData() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Map<String,String>> getData(String application, int numberOfHours)
	{
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		 DateTime currentTime = new DateTime();
		 DateTime roundedTime = new DateTime(currentTime.getYear(), currentTime.getMonthOfYear() ,
		 currentTime.getDayOfMonth(), currentTime.getHourOfDay(), 0);
		 DateTime yesterday = roundedTime.minusHours(numberOfHours);
		 GetOneHourOfData hourOfData = new GetOneHourOfData();
		 hourOfData.init();
		 Map<String,String> row = null;
		 System.out.println(yesterday.toString());
		 for (int i = 0; i < numberOfHours; i++) {
			 DateTime newTime = yesterday.plusHours(i);
			 DateTime futureHour = newTime.plusHours(1);
			 //System.err.println(newTime.toString());
			 row = hourOfData.getOneHourForApplication(application, newTime.getMillis(), futureHour.getMillis());
			 //System.out.println(row);
			 list.add(row);
			 
		 }
		 return list;
		 
		 
		 
	}

	public static void main(String[] args) {
		GetOneDayOfData dayofData = new GetOneDayOfData();
		List<Map<String,String>> list = dayofData.getData("XfinityConnect", 2);
		for (Map<String, String> rowItem : list) {
			
			Map names = list.get(0);
			Set entryset = names.entrySet();
			
			
			for (Entry<String, String> entry : rowItem.entrySet()) {
				
				System.out.print(entry.getValue() + ", ");
			}
				
			
		}

		

	}

}
