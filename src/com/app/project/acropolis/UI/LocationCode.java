package com.app.project.acropolis.UI;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.ProximityListener;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.gps.GPSInfo;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;

/**
 *	@author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *	@vendor Cell Phone Hospital Inc.
 */

public class LocationCode implements Runnable{
	final long GUID = 0x3c38b852369c643L;
	final String AppName = "Project Acropolis";
	
	public String errorstream;
	private double latitude;
	private double longitude;
	private String satCountStr;
	private float accuracy;
	private double heading;
	private double altitude;
	private double speed;
	private int interval = 1; // time in seconds to get new gps data
	private boolean roaming;
	
	public BlackBerryCriteria bbcriteria;
	public BlackBerryLocationProvider bblocationprovider;
	
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	public Date date;
	
	/**
	 * contructor
	 * registers EventLogger for the application to view within Device Stack Trace
	 */
	public LocationCode()
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
//		if(CurrentLocation())
//		{
//			//nothing to do here
//		}
	}
	
	public void run()
	{
		CurrentLocation();
	}
	
	public boolean CurrentLocation() {
		boolean retval = true;
		try {
			EventLogger.logEvent(GUID, ("Assist scanning initiated...").getBytes(), EventLogger.DEBUG_INFO);
			bbcriteria = new BlackBerryCriteria();
			bbcriteria.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
			bbcriteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
//			bbcriteria.setFailoverMode(GPSInfo.GPS_MODE_CELLSITE, 2, 120);
			bbcriteria.setCostAllowed(true);		//default "TRUE" dependent on device-cum-operator 
			bbcriteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
			//HIGH == autonomous
			//MEDIUM == assist
			//LOW == cell site
			
//			LocationProvider locationprovider = LocationProvider.getInstance(null);
			bblocationprovider = (BlackBerryLocationProvider) LocationProvider.getInstance(bbcriteria);
			if(bblocationprovider.getState() == BlackBerryLocationProvider.AVAILABLE)
			{
				bblocationprovider.setLocationListener(new LocationListenerActivity(), interval, 1, 1);
				retval = true;
			}
			else
			{
				date = new Date();
				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device time
				
				TimeZone timezone = TimeZone.getTimeZone("GMT");
				String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); 	//GMT time for server
				
				new MailCode().SendMail("");
				errorstream = "#1.0.1|ErrorStream|"+  Phone.getDevicePhoneNumber(false) + "|"
				+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
				+ String.valueOf(this.getRoamingState()) + "|"
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

	public class LocationListenerActivity implements LocationListener {
		public void locationUpdated(LocationProvider provider, Location location) {
			if (location.isValid()) {
				longitude = location.getQualifiedCoordinates().getLongitude();
				latitude = location.getQualifiedCoordinates().getLatitude();
				speed = location.getSpeed();
 
				// this is to get the accuracy of the GPS Cords
				QualifiedCoordinates qc = location.getQualifiedCoordinates();
				accuracy = qc.getHorizontalAccuracy();
			}
			if( ( RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING ) !=0 )
				roaming = true; 
			else
				roaming = false;
		}

		public void providerStateChanged(LocationProvider provider, int newState) {
			// no-op
		}
	}
	
	public void PauseTracking(int interval)
	{
		bblocationprovider.pauseLocationTracking(interval);
	}
	
	public void ResumeTracking()
	{
		bblocationprovider.resumeLocationTracking();
	}
	
	public void StopTracking()
	{
		bblocationprovider.stopLocationTracking();
	}
	
	public void ResetTracking()
	{
		bblocationprovider.reset();
	}
	
	/*TODO ProximityListener */
	public class LocationProximitySensor implements ProximityListener
	{
		public void monitoringStateChanged(boolean isMonitoringActive) {
			// TODO Auto-generated method stub
			
		}

		public void proximityEvent(Coordinates coordinates, Location location) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public boolean getRoamingState()
	{
		//TODO proximity listener will produce accuracy *ROAMING*
		roaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
		return roaming;
	}
	
	public double getAccuracy()
	{
		return accuracy;
	}
	
	
	
	
	
}
