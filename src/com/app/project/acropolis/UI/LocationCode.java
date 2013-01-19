package com.app.project.acropolis.UI;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.ProximityListener;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;

/**
 *	@author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *	@vendor Cell Phone Hospital Inc.
 */

public class LocationCode {
	final long GUID = 0x3c38b852369c643L;
	final String AppName = "Project Acropolis";
			
	
	private double latitude;
	private double longitude;
	private String satCountStr;
	private float accuracy;
	private double heading;
	private double altitude;
	private double speed;
	private int interval = 1; // time in seconds to get new gps data
	private boolean roaming;
	
	/**
	 * contructor
	 * registers EventLogger for the application to view within Device Stack Trace
	 */
	public LocationCode()
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		if(CurrentLocation())
		{
			//nothing to do here
		}
	}
	
	public boolean CurrentLocation() {
		boolean retval = true;
		try {
			LocationProvider locationprovider = LocationProvider.getInstance(null);
			if(locationprovider!=null) {
				locationprovider.setLocationListener(new LocationListenerImpl(), interval, 1, 1);
			} else { 
				retval = false;
			}
		} catch (LocationException e) {
			System.out.println("Error: " + e.toString());
		}

		return retval;
	}

	public class LocationListenerImpl implements LocationListener {
		public void locationUpdated(LocationProvider provider, Location location) {
			if (location.isValid()) {
				longitude = location.getQualifiedCoordinates().getLongitude();
				latitude = location.getQualifiedCoordinates().getLatitude();
				speed = location.getSpeed();
 
				// This is to get the Number of Satellites
				String NMEA_MIME = "application/X-jsr179-location-nmea";
				satCountStr = location.getExtraInfo("satellites");
				if (satCountStr == null) {
					satCountStr = location.getExtraInfo(NMEA_MIME);
				}

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
	
	
//	/**
//	 * registers LocationListener, aiding to detect change in position/co-ordinates
//	 * @throws LocationException
//	 */
//	public void StartScanning() 
//	{
//		EventLogger.logEvent(GUID, ("scanning initiated...").getBytes(), EventLogger.DEBUG_INFO);
//		try{
//		LocationProvider Applp = LocationProvider.getInstance(null);
//		Applp.setLocationListener(new LocationChangeListener(), -1, -1, 1);		//Age = 10seconds
//		} catch(LocationException e){
//			e.printStackTrace();
//		}
//	}
	
	
	
//	private class LocationChangeListener implements LocationListener
//	{
//		
//		public void locationUpdated(LocationProvider provider, Location location) {
//			// TODO Auto-generated method stub
//			if(location.isValid() && provider !=null)
//			{
//				QualifiedCoordinates Appcoordinates = location.getQualifiedCoordinates();
//				if(Appcoordinates != null)
//				{
//					lat = Appcoordinates.getLatitude();
//					lng = Appcoordinates.getLongitude();
//					accuracy = ( Appcoordinates.getVerticalAccuracy() + Appcoordinates.getHorizontalAccuracy() ) / 2 ;
//					satcount = location.getExtraInfo("satellites");
//					if(satcount==null)
//					{
//						satcount = location.getExtraInfo("applicaiton/X-jsr179-location-nmea");
//					}
//					else
//					{
//						satcount = "-1";
//					}
//				}
//				else
//				{
//					lat = -1;
//					lng = -1;
//					accuracy = -1;
//				}
//			}
//		}
//		
//		public void providerStateChanged(LocationProvider provider, int newState) {
//			// TODO Auto-generated method stub
////			prov
//		}
//		
//	}
	
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
	
	public double getAccurcay()
	{
		return accuracy;
	}
	
	public String getSatelliteAvailable()
	{
		return satCountStr;
	}
}
