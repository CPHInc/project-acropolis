package com.app.project.acropolis.UI;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.RadioInfo;

/**
 *	@author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *	@vendor Cell Phone Hospital Inc.
 */

public class LocationCode implements Runnable{
	final long GUID = 0x3c38b852369c643L;
	final String AppName = "**Project Acropolis SVN debugger**";
	
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
	
	public boolean NON_CANOperatorCheck = true;
	public final String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
	public String CurrentNetworkName = "";

	public void run()
	{
		CurrentLocation();
	}
	
	public boolean CurrentLocation() {
		boolean retval = true;
		try {
			new Logger().LogMessage("Automous scanning initiated...");
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
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public double getAccuracy()
	{
		return accuracy;
	}

	public boolean Check_NON_CAN_Operator()
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
