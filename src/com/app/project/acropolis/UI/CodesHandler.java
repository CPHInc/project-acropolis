package com.app.project.acropolis.UI;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.EventLogger;


/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * Gathers and arranges codes from LocationCode class and MailCode class
 * 
 * <reason for Runnable over Thread--resusability>
 */
public class CodesHandler implements Runnable {

	final long GUID = 0x29ef40e6e31efd2L;
	final String AppName = "Project Acropolis";
	
	LocationCode location;
	
	double Latitude = 0;
	double Longitude = 0;
	double Accuracy = 0;
	double Satellites = 0;
	boolean Roaming = false;
	
	/*format followed #1.0.1|Data Stream|PhoneNumber|TimeStamp(GMT)|DeviceTime|Roaming|LAT|LNG|Accuracy## */
	public String datatobeMailed = "";
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	public Date date;
	public Calendar cal; 
	
	
	/**
	 * 
	 */
	public void run()
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		/*if in ROAMING detect and locate co-ordinates and send data*/
	
		date = new Date();
		String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device time
		
		location = new LocationCode();
		
		for(;;)
		{
			if(location.getLatitude()!=0 && location.getLongitude()!=0)
			{
				try {
					Thread.sleep(10*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		
		TimeZone timezone = TimeZone.getTimeZone("GMT");
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); //GMT time for server				location.StartScanning();
		
		datatobeMailed = 
					"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
					+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
					+ String.valueOf(Roaming) + "|"
					+ location.getLatitude() + "|" 
					+ location.getLongitude() + "|"
					+ location.getAccurcay() +"##";

		new MailCode().SendMail(datatobeMailed);
		
	}
	
	/**
	 * @return Latitude from LocationCode class
	 */
	public double getLatitude()
	{
		return location.getLatitude();
	}
	
	/**
	 * @return Longitude from LocationCode class
	 */
	public double getLongitude()
	{
		return location.getLongitude();
	}
	
	/**
	 * @return Accuracy of co-ordinates from LocationCode class
	 */
	public double getAccuracy()
	{
		return location.getAccurcay();
	}
	
	/**
	 * @return No. of assissting satellites from LocationCode class
	 */
	public double getSatelliteNumbers()
	{
		return (double)Double.parseDouble(location.getSatelliteAvailable());
	}
	
	
	/**
	 *	Most crucial within the application
	 * 
	 * @return Roaming boolean state from LocationCode class
	 */
	public boolean getRoamingState()
	{
		return location.getRoamingState();
	}
	
	/**
	 * @return Device's formatted phone number
	 */
	public String getDevicePhoneNumber()
	{
		return Phone.getDevicePhoneNumber(true);
	}
	
}