package com.app.project.acropolis.engine.monitor;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.engine.mail.MailCode;

/**
 *	@author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *	
 * @version $Revision: 1.0 $
 */

public class LocationCode implements Runnable{
	
	public static int interval = 1;
	public static String errorstream;
	private static double latitude;
	private static double longitude;
	private static float accuracy;
	private static boolean roaming;
	
//	public BlackBerryCriteria bbcriteria;
//	public BlackBerryLocationProvider bblocationprovider;
	public static Criteria bbcriteria;
	public static LocationProvider bblocationprovider;
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	public static Date date;
	
	public static boolean NON_CANOperatorCheck = true;
	public final static String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
	public static String CurrentNetworkName = "";

	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		new Logger().LogMessage(">>LocationCode<<");
		CurrentLocation();
	}
	
	/**
	 * Method CurrentLocation.
	 * @return boolean */
	public static boolean CurrentLocation() {
		boolean retval = true;
		try {
			new Logger().LogMessage("Autonomous scanning initiated...");
//			bbcriteria = new BlackBerryCriteria(GPSInfo.getDefaultGPSMode());
			bbcriteria = new Criteria();	
			/*
			 *	HorizontalAccuracy	N/A			N/A				No_Req
			 *	VerticalAccuracy 	N/A			N/A				No_Req
			 *	Cost				false		true			true
			 *	Power				N/A			Med/High/No_Req	Low
			 *						Autonomous	Assisted		Cellsite
			 */
//			bbcriteria.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
//			bbcriteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
			bbcriteria.setCostAllowed(false);		//default "TRUE" dependent on device-cum-operator
//			bbcriteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
			//applicable in net.rim.device.api.gps.BlackBerryCriteria
			//HIGH == autonomous
			//MEDIUM == assist
			//LOW == cell site
			bbcriteria.setAddressInfoRequired(true);
//			bblocationprovider = (BlackBerryLocationProvider) LocationProvider.getInstance(bbcriteria);
			bblocationprovider = (LocationProvider) LocationProvider.getInstance(bbcriteria);
			if(bblocationprovider.getState() == LocationProvider.AVAILABLE)
			{
				bblocationprovider.setLocationListener(new LocationListenerActivity(), interval, 1, 1);
				retval = true;
			}
			else
			{
				new Logger().LogMessage("GPS Chip missing");
				date = new Date();
				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device time
				
				TimeZone timezone = TimeZone.getTimeZone("GMT");
				String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); 	//GMT time for server
				
				new MailCode().SendMail("");
				errorstream = "#1.0.1|ErrorStream|"+  Phone.getDevicePhoneNumber(false) + "|"
				+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
				+ String.valueOf(Check_NON_CAN_Operator()) + "|"
				+ 0.0 + "|" 
				+ 0.0 + "|"
				+ 0.0 +"##";
				retval = false;
			}
		} catch (LocationException e) {
			System.out.println("Error: " + e.toString());
		}

		return retval;
	}

	/**
	 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
	 * @version $Revision: 1.0 $
	 */
	public static class LocationListenerActivity implements LocationListener {
		/**
		 * Method locationUpdated.
		 * @param provider LocationProvider
		 * @param location Location
		 * @see javax.microedition.location.LocationListener#locationUpdated(LocationProvider, Location) */
		public void locationUpdated(LocationProvider provider, Location location) {
			if (location.isValid()) {
				longitude = location.getQualifiedCoordinates().getLongitude();
				latitude = location.getQualifiedCoordinates().getLatitude();
				new Logger().LogMessage("Lat::"+latitude+"\r\nLon::"+longitude);
				// this is to get the accuracy of the GPS Cords
				QualifiedCoordinates qc = location.getQualifiedCoordinates();
				accuracy = qc.getHorizontalAccuracy();
			}
			if( ( RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING ) !=0 )
				roaming = true; 
			else
				roaming = false;
		}

		/**
		 * Method providerStateChanged.
		 * @param provider LocationProvider
		 * @param newState int
		
		 * @see javax.microedition.location.LocationListener#providerStateChanged(LocationProvider, int) */
		public void providerStateChanged(LocationProvider provider, int newState) {
			// no-op
		}
	}
	
//	/**
//	 * Method PauseTracking.
//	 * @param interval int
//	 */
//	public void PauseTracking(int interval)
//	{
//		bblocationprovider.pauseLocationTracking(interval);
//	}
//	
//	public void ResumeTracking()
//	{
//		bblocationprovider.resumeLocationTracking();
//	}
//	
//	public void StopTracking()
//	{
//		bblocationprovider.stopLocationTracking();
//	}
	
	public static void ResetTracking()
	{
		bblocationprovider.reset();
	}
	
	/**
	 * Method getLatitude.
	
	 * @return double */
	public static double getLatitude()
	{
		return latitude;
	}
	
	/**
	 * Method getLongitude.
	
	 * @return double */
	public static double getLongitude()
	{
		return longitude;
	}
	
	/**
	 * Method getAccuracy.
	
	 * @return double */
	public static double getAccuracy()
	{
		return accuracy;
	}

	/**
	 * Method Check_NON_CAN_Operator.
	
	 * @return boolean */
	public static boolean Check_NON_CAN_Operator()
	{
		CurrentNetworkName = RadioInfo.getCurrentNetworkName();
		
		if( CurrentNetworkName.equalsIgnoreCase(CanadianOperators[0]) 
		  			|| CurrentNetworkName.equalsIgnoreCase(CanadianOperators[1])
		   			||CurrentNetworkName.equalsIgnoreCase(CanadianOperators[2]) )
			NON_CANOperatorCheck = false;				//if Current Operator is CANADIAN then **FALSE**
		else
			NON_CANOperatorCheck = true;				//if Current Operator is not CANADIAN then **TRUE** hence ROAMING
		    	
		return NON_CANOperatorCheck;
	 }
	
	
}
