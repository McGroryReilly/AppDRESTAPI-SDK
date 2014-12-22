package com.appdynamics.application;

import org.appdynamics.appdrestapi.RESTAccess;
import org.appdynamics.appdrestapi.data.*;
import org.appdynamics.appdrestapi.util.PostEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;


/**
 * 
 * @author hbrien@appdynamics.com
 * 
 */
public class GetOneHourOfData {

	RESTAccess access;
	
	public enum CriticalEventTypes {
		POLICY_OPEN_CRITICAL, 
		POLICY_OPEN_WARNING,
		DEADLOCK,
		DIAGNOSTIC_SESSION,
		MOBILE_CRASH_IOS_EVENT,
		MOBILE_CRASH_ANDROID_EVENT,
		ERROR
	}
	
	public enum ScoreCardEvents {
		SLOW,
		VERY_SLOW,
		STALLED,
		ERROR
	}
	
	
	public static String CONTROLLER = "comcast27.saas.appdynamics.com";
	public static String PORT = "443";
	public static boolean USESSL = true;
	public static String USER = "hbrien";
	public static String PASSWORD = "mon*ss10";
	public static String ACCOUNT = "comcast27";
	
	
	String[] PERFORMANCE_METRIC_PATH = {
			"Overall Application Performance|Average Response Time (ms)",
			"Overall Application Performance|Calls per Minute",
			"Overall Application Performance|Normal Average Response Time (ms)",
			"Overall Application Performance|Number of Slow Calls",
			"Overall Application Performance|Number of Very Slow Calls",
			"Overall Application Performance|Stall Count" };

	String[] ERROR_METRIC_PATH = {
			"Overall Application Performance|Errors per Minute",
			"Overall Application Performance|Error Page Redirects per Minute",
			"Overall Application Performance|Exceptions per Minute",
			"Overall Application Performance|Infrastructure Errors per Minute",
			"Overall Application Performance|HTTP Error Codes per Minute" };
	
	public void init() {

		String controller = GetOneHourOfData.CONTROLLER;
		String port = GetOneHourOfData.PORT;
		boolean useSSL = GetOneHourOfData.USESSL;
		String user = GetOneHourOfData.USER;
		String password = GetOneHourOfData.PASSWORD;
		String account = GetOneHourOfData.ACCOUNT;

		String site = "/";
		access = new RESTAccess(controller, port, useSSL, user, password,
				account);

	}

	public void getHour() {

		Calendar cal = Calendar.getInstance();
		long end = cal.getTimeInMillis();
		cal.add(Calendar.MINUTE, -60);
		long start = cal.getTimeInMillis();

	}

	public static void main(String[] args) {

		GetOneHourOfData runner = new GetOneHourOfData();
		runner.init();
		Calendar cal = Calendar.getInstance();
		long endTime = cal.getTimeInMillis();
		cal.add(Calendar.MINUTE, -60);
		long startTime = cal.getTimeInMillis();
		Map<String,String> rowMap = runner.getOneHourForApplication("XfinityConnect", startTime, endTime);
	}

	public Map<String,String> getOneHourForApplication(String appName, long beginTime, long endTime) {
		
		//System.out.printf("%s, %s, %s, %s,  %s \n", row);
		
		Map<String,String> aiRow = new HashMap<String,String>();
		for (int i = 0; i < PERFORMANCE_METRIC_PATH.length; i++) {
			MetricDatas appResponseTime = access.getRESTGenericMetricQuery(appName,PERFORMANCE_METRIC_PATH[i], beginTime, endTime, true);
			long max = appResponseTime.getSingleRollUpMetricValue().getMax();
			long value = appResponseTime.getSingleRollUpMetricValue().getValue();
			long min = appResponseTime.getSingleRollUpMetricValue().getMin();
			double stdDev = appResponseTime.getSingleRollUpMetricValue().getStdDev();
			String name = PERFORMANCE_METRIC_PATH[i];
			Object[] row = new Object[]{name, new Long(value), new Long(min), new Long(max), new Double(stdDev)};
			
			System.out.printf("%s,\t%s,\t%s,\t%s,\t%s \n", row);

		}
		
		MetricDatas appCallsPerMinute = access.getRESTGenericMetricQuery(appName,PERFORMANCE_METRIC_PATH[1], beginTime, endTime, true);				
		String eventQuery = "POLICY_OPEN_CRITICAL";		
		Events errorEvents = access.getEvents(appName, eventQuery, "INFO,WARN,ERROR", beginTime, endTime);
		int eventCount = errorEvents.getEvents().size();
		Object[] args = {new Integer(eventCount), eventQuery};
		System.out.printf("Application has %s %s events", args);
		System.out.println("");
		eventQuery = "POLICY_OPEN_WARNING";
		Events warningEvents = access.getEvents(appName, eventQuery, "INFO,WARN,ERROR", beginTime, endTime);
		int warningEventsCount = warningEvents.getEvents().size();
		Object[] warningEventArgs = {new Integer(warningEventsCount), eventQuery};
		System.out.printf("Application has %s %s events", warningEventArgs);
		return aiRow;

	}

}
