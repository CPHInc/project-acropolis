package com.app.project.acropolis.UI;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

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
	
	double trylater = 0;
	
	
	public void run()
	{
		EventLogger.register(GUID, AppName, EventLogger.DEBUG_INFO);
		
		TimeZone timezone = TimeZone.getTimeZone("GMT");
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); //GMT time for server		
		
		location = new LocationCode();
		while(true)
		{
//			if(trylater < 5*60*1000)							//if not then sleep for 30mins and acquire a fix
//			{
				if(location.getLatitude()!=0 && location.getLongitude()!=0)
				{
					break;
				}
				else
				{
					try {
						Thread.sleep(5*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
//			}
//			else
//			{
//				try {
//					Thread.sleep(30 * 60 * 1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			++trylater;
		}
		
		location.RemoveProviders();
	
		date = new Date();
		String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device time
		
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



/**
 * OLD CODE SNIPPET - 19th January 2013 work
 * Power consuming and inefficient
 */
//	
//	public void somerun()
//	{
//		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
//		/**if in ROAMING detect and locate co-ordinates and send data**/
//	
//		TimeZone timezone = TimeZone.getTimeZone("GMT");
//		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); //GMT time for server		
//		
////		location = new LocationCode();
//		try{
//			LocationProvider locationprovider = LocationProvider.getInstance(null);
//			
//			if(locationprovider!=null) {
//				locationprovider.setLocationListener(new LocationListener()
//				{
//	
//					public void locationUpdated(LocationProvider provider, Location location) {
//						if(provider.getState() == LocationProvider.AVAILABLE){			//FIXED
//							if(location.isValid())
//							{
//								Latitude = location.getQualifiedCoordinates().getLatitude();
//								Longitude = location.getQualifiedCoordinates().getLongitude();
//								
//								Accuracy = (double)
//										(location.getQualifiedCoordinates().getHorizontalAccuracy() + 
//										location.getQualifiedCoordinates().getVerticalAccuracy()) / 2;
//							}
//						}
//						else			//FIX N/A
//						{
//							Latitude = -1;
//							Longitude = -1;
//							Accuracy = -1;
//							try {
//								Thread.sleep(10*1000);			//sleep for 10sec
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//	
//					public void providerStateChanged(LocationProvider provider,
//							int newState) {
//						// TODO Auto-generated method stub
//						if(newState != LocationProvider.AVAILABLE)
//						{
//							
//							provider.reset();
//						}
//						else
//						{
//							//do nothing
//						}
//					}
//					
//				}, -1, -1, -1);
//		}
//		} catch(LocationException e) {
//			e.printStackTrace();
//		}
//		
//		date = new Date();
//		String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device time
//		
//		datatobeMailed = 
//				"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
//				+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//				+ String.valueOf(Roaming) + "|"
//				+ Latitude + "|" 
//				+ Longitude + "|"
//				+ Accuracy +"##";
//		
//		new MailCode().SendMail(datatobeMailed);
//		
//		
//		//TODO old code
//		
//		/*for(;;)
//		{
//			if(location.getLatitude()!=0 && location.getLongitude()!=0)
//			{
//				try {
//					Thread.sleep(3*1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				break;
//			}
//			else{
//				try {
//					Thread.sleep(3*1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				continue;
//			}
//		}*/
//			
////		datatobeMailed = 
////					"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
////					+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
////					+ String.valueOf(Roaming) + "|"
////					+ location.getLatitude() + "|" 
////					+ location.getLongitude() + "|"
////					+ location.getAccurcay() +"##";
////
////		new MailCode().SendMail(datatobeMailed);
//		
//	}