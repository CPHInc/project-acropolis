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
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;

public class RoamingRunnable implements Runnable
{
	final long GUID = 0x7f7af45a49451784L;
	final String AppName = "Project Acropolis SVN debugger";
	
	boolean isRoaming = false;
	
	String NewNetwork = "";
	
	public String errorstream;
	public String datatobeMailed;
	
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

	public RoamingRunnable()
	{
		EventLogger.register(GUID, AppName, EventLogger.ALWAYS_LOG);
	}
	
	public void run() 
	{
		RegisterRoamingListener();
	} 
	
	public void RegisterRoamingListener()
	{
		Application.getApplication().addRadioListener((RadioListener)new RoamingListener());
	}
	
	public class RoamingListener implements RadioStatusListener
	{
		/**
		 * works when Base Station (Radio Tower) has changed
		 */
		public void baseStationChange() {
		}

		public void networkScanComplete(boolean success) {
		}

		public void networkServiceChange(int networkId, int service) {
			NewNetwork = RadioInfo.getCurrentNetworkName();
			EventLogger.logEvent(GUID, ("Network Changed:" + NewNetwork ).getBytes(), EventLogger.VIEWER_STRING);
			
			/*GET THE CURRENT CHANE IN ROAMING CO-ORDINATES*/
			CollectedData();
		
//			Application.getApplication().removeRadioListener((RadioListener) new RoamingListener());
		}

		public void networkStarted(int networkId, int service) {
		}

		public void networkStateChange(int state) {
		}

		public void pdpStateChange(int apn, int state, int cause) {
		}

		public void radioTurnedOff() {
		}

		public void signalLevel(int level) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public boolean CurrentLocation() 
	{
		boolean retval = true;
		EventLogger.logEvent(GUID, ("Autonomous scanning initiated...").getBytes(), EventLogger.DEBUG_INFO);
		bbcriteria = new BlackBerryCriteria();
		bbcriteria.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
		bbcriteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
//		bbcriteria.setFailoverMode(GPSInfo.GPS_MODE_CELLSITE, 2, 120);
		bbcriteria.setCostAllowed(true);		//default "TRUE" dependent on device-cum-operator 
		bbcriteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
		//HIGH == autonomous
		//MEDIUM == assist
		//LOW == cell site
		
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
			+ String.valueOf(Check_NON_CAN_Operator()) + "|"
			+ 0.0 + "|" 
			+ 0.0 + "|"
			+ 0.0 +"##";
			retval = false;
		}

		return retval;
	}
	
	
	public void CollectedData()
	{
		/*if in ROAMING detect and locate co-ordinates and send data*/
		TimeZone timezone = TimeZone.getTimeZone("GMT");
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); 	//GMT time for server		
		
		CurrentLocation();
		
		int i=0;
		int j=0;
		int k=0;
		/**
		 * Standard -- 
		 * 			fix within 7 minutes sends location for each iteration gives 20 seconds resting time to device
		 * 				if NOT wait for 20 minutes and repeat
		 * 				(also adds 1/4 minute to 6 minutes on each iteration) 
		 */
		
		for(int a=0 ; a<=14 ; a++)
		{
			if( getLatitude() != 0 && getLongitude() != 0 )
				// [ 0 < i < 7 ] (8 times) ++ [ 9 < i < 12 ] ++ (4 times)
			{
				date = new Date();
				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
				
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(Check_NON_CAN_Operator()) + "|"
						+ getLatitude() + "|" 
						+ getLongitude() + "|"
						+ getAccuracy() +"##";
				
				new MailCode().SendMail(datatobeMailed);
				
				StopTracking();
				ResetTracking();
				
				break;
			}
			else if(a==8)
			{
				try {
					PauseTracking(20*1000);
					ResumeTracking();
					Thread.sleep(30*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if(a==13)
			{
				date = new Date();
				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device t  ime
				
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(Check_NON_CAN_Operator()) + "|"				//CodesHandler Roaming method 
						+ 67.43125 + "|" 
						+ -45.123456 + "|"											//southern Greenland
						+ 1234.1234 +"##";
				
				new MailCode().SendMail(datatobeMailed);
				
				StopTracking();
				ResetTracking();
				
				break;
			}
			else
			{
				try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public class LocationListenerActivity implements LocationListener {
		public void locationUpdated(LocationProvider provider, Location location) {
			if (location.isValid()) {
				longitude = location.getQualifiedCoordinates().getLongitude();
				latitude = location.getQualifiedCoordinates().getLatitude();
 
				// this is to get the accuracy of the GPS Cords
				QualifiedCoordinates qc = location.getQualifiedCoordinates();
				accuracy = (qc.getVerticalAccuracy() + qc.getHorizontalAccuracy()) / 2;
			}
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
		boolean NON_CANOperatorCheck = true;
   	
		final String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
		    	
		String CurrentNetworkName = "";
		int CurrentNetworkID = 0;
		    	
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