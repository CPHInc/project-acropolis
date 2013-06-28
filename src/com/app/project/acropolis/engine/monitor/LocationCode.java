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
import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.gps.GPSInfo;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.engine.mail.MailCode;

/**
 *	@author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *	
 * @version $Revision: 1.0 $
 */

public class LocationCode implements Runnable
{	
	public  int interval = 1;
	public  String errorstream = "";
	private  double latitude = 0;
	private  double longitude = 0;
	private  float accuracy = 0;

	//	public BlackBerryCriteria bbcriteria;
	//	public BlackBerryLocationProvider bblocationprovider;
	BlackBerryCriteria bbcriteria;
	BlackBerryLocationProvider bblocationprovider; 
	{
		bbcriteria = new BlackBerryCriteria(GPSInfo.getDefaultGPSMode());
		try {
			bblocationprovider =(BlackBerryLocationProvider)LocationProvider.getInstance(bbcriteria);
		} catch (LocationException e) {
			e.printStackTrace();
		}
	}
	public  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

	public static  boolean NON_CANOperatorCheck = true;
	public final static  String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
	public static  String CurrentNetworkName = "";
	Location _Location;
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		new Logger().LogMessage(">>LocationCode<<");
		CurrentLocation();
	}
	boolean searchInProgress = false;
	/**
	 * Method CurrentLocation.
	 * @return boolean */
	public  boolean CurrentLocation() {
		searchInProgress = true;
		new Logger().LogMessage("$$LocationCode#CurrentLocation");		
		boolean retval = true;
		new Logger().LogMessage("Autonomous scanning initiated...");
		/* applicable in javax.microedition.location.Criteria
		 *	HorizontalAccuracy	N/A			N/A				No_Req
		 *	VerticalAccuracy 	N/A			N/A				No_Req
		 *	Cost				false		true			true
		 *	Power				N/A			Med/High/No_Req	Low
		 *						Autonomous	Assisted		Cellsite
		 */
		bbcriteria.setCostAllowed(true);		//default "TRUE" dependent on device-cum-operator
		bbcriteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
		//applicable in net.rim.device.api.gps.BlackBerryCriteria
		//HIGH == autonomous
		//MEDIUM == assist
		//LOW == cell site
		bbcriteria.setAddressInfoRequired(true);
		if(bblocationprovider.getState() == LocationProvider.AVAILABLE)
		{
			bblocationprovider.setLocationListener(new LocationListenerActivity(), interval, 1, 1);
			retval = true;
		}
		else if(bblocationprovider.getState() == LocationProvider.TEMPORARILY_UNAVAILABLE)
		{
			//TODO
		}
		else
		{
			new Logger().LogMessage("GPS Chip missing");
			TimeZone serverTimeZone = TimeZone.getTimeZone("GMT-04:00");
			Calendar calendar = Calendar.getInstance(serverTimeZone);
			calendar.setTime(new Date(System.currentTimeMillis()));
			String recordedTimeStamp = sdf.format(calendar.getTime());	
			TimeZone timezone = TimeZone.getDefault();
			String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime());  	//GMT time for server

			new MailCode().DebugMail("");
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
	QualifiedCoordinates qc;
	/**
	 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
	 * @version $Revision: 1.0 $
	 */
	public  class LocationListenerActivity implements LocationListener {
		/**
		 * Method locationUpdated.
		 * @param provider LocationProvider
		 * @param location Location
		 * @see javax.microedition.location.LocationListener#locationUpdated(LocationProvider, Location) */
		public void locationUpdated(LocationProvider provider, Location location) 
		{
			if (location.isValid()) 
			{
				_Location = location;
				longitude = location.getQualifiedCoordinates().getLongitude();
				latitude = location.getQualifiedCoordinates().getLatitude();
				qc = location.getQualifiedCoordinates();
				accuracy = qc.getHorizontalAccuracy();
				new Logger().LogMessage("AddressINFO::"+location.getAddressInfo().toString());
			}
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

	/**
	 * Method PauseTracking.
	 * @param interval int
	 */
	public  void PauseTracking(int interval)
	{
		bblocationprovider.pauseLocationTracking(interval);
	}

	public  void ResumeTracking()
	{
		bblocationprovider.resumeLocationTracking();
	}

	public  void StopTracking()
	{
		bblocationprovider.stopLocationTracking();
	}

	public  void ResetTracking()
	{
		bblocationprovider.reset();
	}

	/**
	 * Method getLatitude.

	 * @return double */
	public  double getLatitude()
	{
		return latitude;
	}

	/**
	 * Method getLongitude.

	 * @return double */
	public  double getLongitude()
	{
		return longitude;
	}

	/**
	 * Method getAccuracy.

	 * @return double */
	public  double getAccuracy()
	{
		return accuracy;
	}

	/**
	 * Method Check_NON_CAN_Operator.
	 * @return boolean */
	public static boolean Check_NON_CAN_Operator()
	{
		while(RadioInfo.getCurrentNetworkName()==null)
		{}//stay here
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