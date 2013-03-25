package com.app.project.acropolis.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.engine.mail.MailCode;
import com.app.project.acropolis.model.ModelFactory;
import com.app.project.acropolis.model.PlanModelFactory;

public class RoamingHandler implements Runnable
{
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
	
	ModelFactory theModel;
	PlanModelFactory thePlan;
	MailCode mailer;
	
	/*Roaming*/
	public int roamAvailMins = 0;
	public int roamAvailData = 0;
	public int roamAvailMsgs = 0;
	public int roamIncomingMins = 0;
	public int roamOutgoingMins = 0;
	public int roamUsedMins = 0;
	public int roamReceivedMsgs = 0;
	public int roamSentMsgs = 0;
	public int roamUsedMsgs = 0;
	public int roamDownload = 0;
	public int roamUpload = 0;
	public int roamUsedData = 0;
	
	/*Local*/
	public int UsedIncomingMins = 0;
	public int UsedOutgoingMins = 0;
	public int UsedMins = 0;
	public int UsedSentMsgs = 0;
	public int UsedReceivedMsgs = 0;
	public int UsedMsgs = 0;
	public int UsedDownload = 0;
	public int UsedUpload = 0;
	public int UsedData = 0;

	int computationCounter = 0;
	
	public RoamingHandler()
	{
		new Logger().LogMessage(">>RoamingHandler<<");
	}
	
	public void run() 
	{
		int i=0;
		for(;;)
		{
			if(Check_NON_CAN_Operator())
			{
				if(i==0)
				{
					CollectedData();
					try {
						Thread.sleep(1*60*60*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++;
				}
			}
		}
	} 
	
	public void CollectedData()
	{
//		if(thePlan.SelectData("roam_quota").equalsIgnoreCase("true"))
//		{
//			roamAvailMins = Integer.valueOf(thePlan.SelectData("roam_min")).intValue();
//			roamAvailMsgs = Integer.valueOf(thePlan.SelectData("roam_msg")).intValue();
//			roamAvailData = Integer.valueOf(thePlan.SelectData("roam_data")).intValue();
//			
//			roamUsedMins = 0;
//			roamUsedMsgs = 0;
//			roamUsedData = 0;
//			roamIncomingMins = 0;
//			roamOutgoingMins = 0;
//			roamReceivedMsgs = 0;
//			roamSentMsgs = 0;
//			roamDownload = 0;
//			roamUpload = 0;
//			
//			UsedIncomingMins = Integer.valueOf(theModel.SelectData("incoming")).intValue();
//			UsedOutgoingMins = Integer.valueOf(theModel.SelectData("outgoing")).intValue();
//			UsedMins = UsedIncomingMins + UsedOutgoingMins;
//			
//			UsedReceivedMsgs = Integer.valueOf(theModel.SelectData("received")).intValue();
//			UsedSentMsgs = Integer.valueOf(theModel.SelectData("sent")).intValue();
//			UsedMsgs = UsedReceivedMsgs + UsedSentMsgs;
//					
//			UsedDownload = 
//					Bytes2MegaBytes(Double.valueOf(theModel.SelectData("downloaded")).doubleValue());
//			UsedUpload = 
//					Bytes2MegaBytes(Double.valueOf(theModel.SelectData("uploaded")).doubleValue());
//			UsedData = UsedDownload + UsedUpload;
//			
//			Application.getApplication().invokeLater(new Runnable()
//			{
//				public void run()
//				{
//				roamIncomingMins = 
//						Integer.valueOf(theModel.SelectData("incoming")).intValue() - UsedIncomingMins;
//				roamOutgoingMins = 
//						Integer.valueOf(theModel.SelectData("outgoing")).intValue() - UsedOutgoingMins;
//				roamUsedMins = roamIncomingMins + roamOutgoingMins;
//				
//				roamReceivedMsgs = 
//						Integer.valueOf(theModel.SelectData("received")).intValue() - UsedReceivedMsgs;
//				roamSentMsgs = 
//						Integer.valueOf(theModel.SelectData("sent")).intValue() - UsedSentMsgs;
//				roamUsedMsgs = 
//						roamReceivedMsgs + roamSentMsgs;
//				
//				roamDownload = 
//						Bytes2MegaBytes(Double.valueOf(theModel.SelectData("downloaded")).doubleValue()) - UsedDownload;
//				roamUpload =
//						Bytes2MegaBytes(Double.valueOf(theModel.SelectData("uploaded")).doubleValue()) - UsedUpload;
//				roamUsedData = 
//						roamDownload + roamUpload;
//				
//				theModel.UpdateData("roam_min", String.valueOf(roamUsedMins) );
//				theModel.UpdateData("roam_msg", String.valueOf(roamUsedMsgs) );
//				theModel.UpdateData("roam_data", String.valueOf(roamUsedData) );
//				}
//			},6*60*60*1000,true);
//		}
		
		/*if in ROAMING detect and locate co-ordinates and send data*/
		TimeZone timezone = TimeZone.getTimeZone("GMT");
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); 	//GMT time for server		
		
		CurrentLocation();
		
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
				
				theModel.UpdateData("device_time", recordedTimeStamp);
				theModel.UpdateData("lat", String.valueOf((getLatitude())));
				theModel.UpdateData("lng", String.valueOf((getLongitude())));
				theModel.UpdateData("acc", String.valueOf(getAccuracy()));
				theModel.UpdateData("roaming", String.valueOf(Check_NON_CAN_Operator()));
				
				new MailCode().SendMail(datatobeMailed);
				if(Check_NON_CAN_Operator())
					theModel.UpdateData("roaming","true");
				else 
					theModel.UpdateData("roaming","false");
				//data monitor addition
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//						+ String.valueOf(Check_NON_CAN_Operator()) + "|"
						+ String.valueOf(RoamingCheck()) + "|"
						+ getLatitude() + "|" 
						+ getLongitude() + "|"
						+ getAccuracy() + "|"
						+ "Down:"+ getRoamingDownload() + "|"
						+ "Up:" + getRoamingUpload() + "|"
						+ "Received Msgs:" + getRoamingReceived() + "|" 
						+ "Sent Msgs:" + getRoamingSent() + "|"
						+ "Incoming Duration:"+ getRoamingIncoming() + "|"
						+ "Outgoing Duration:" + getRoamingOutgoing() + "##";
				new MailCode().DebugMail(datatobeMailed);
				
				
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
				
				theModel.UpdateData("device_time", recordedTimeStamp);
				theModel.UpdateData("lat", String.valueOf(67.43125));
				theModel.UpdateData("lng", String.valueOf(-45.123456));
				theModel.UpdateData("acc", String.valueOf(1234.1234));
				theModel.UpdateData("roaming", String.valueOf(Check_NON_CAN_Operator()));
				
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(Check_NON_CAN_Operator()) + "|"				//CodesHandler Roaming method 
						+ 67.43125 + "|" 
						+ -45.123456 + "|"											//southern Greenland
						+ 1234.1234 +"##";
				
				new MailCode().SendMail(datatobeMailed);
				if(Check_NON_CAN_Operator())
					theModel.UpdateData("roaming","true");
				else 
					theModel.UpdateData("roaming","false");
				//data monitor addition
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(Check_NON_CAN_Operator()) + "|"
//						+ String.valueOf(RoamingCheck()) + "|"
						+ 67.43125 + "|" 
						+ -45.123456 + "|"											//southern Greenland
						+ 1234.1234 + "|"
						+ "Down:"+ getRoamingDownload() + "|"
						+ "Up:" + getRoamingUpload() + "|"
						+ "Received Msgs:" + getRoamingReceived() + "|" 
						+ "Sent Msgs:" + getRoamingSent() + "|"
						+ "Incoming Duration:"+ getRoamingIncoming() + "|"
						+ "Outgoing Duration:" + getRoamingOutgoing() + "##";
				new MailCode().DebugMail(datatobeMailed);
				
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
	
	public boolean CurrentLocation() 
	{
		boolean retval = true;
		new Logger().LogMessage("Autonomous scanning initiated...");
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
	
	public int Bytes2MegaBytes(double bytes)
	{
		return Integer.valueOf(
				StringBreaker.split(String.valueOf(bytes/(1024*10234)),".")[0]).intValue();
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
	
	public int getRoamingIncoming()
	{
		return roamIncomingMins;
	}
	
	public int getRoamingOutgoing()
	{
		return roamOutgoingMins;
	}
	
	public int getRoamingDownload()
	{
		return roamDownload;
	}
	
	public int getRoamingUpload()
	{
		return roamUpload;
	}
	
	public int getRoamingReceived()
	{
		return roamReceivedMsgs;
	}
	
	public int getRoamingSent()
	{
		return roamSentMsgs;
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
	
	public boolean RoamingCheck()
	{
		if((RadioInfo.getNetworkService() & RadioInfo.NETWORK_SERVICE_ROAMING)!=0)
			return true;
		else
			return false;
	}
	
}