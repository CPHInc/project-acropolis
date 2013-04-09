package com.app.project.acropolis.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.engine.mail.MailCode;
import com.app.project.acropolis.engine.monitor.LocationCode;
import com.app.project.acropolis.model.RoamingUsageDB;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class RoamingHandler implements Runnable
{
	boolean isRoaming = false;
	
	String NewNetwork = "";
	
	final String[] MapKeys = {"PhoneNumber","Roaming","RoamingLatitude","RoamingLongitude",
			"RoamingFixAck","RoamingFixDeviceTime","RoamingFixServerTime","RoamingIncoming",
			"RoamingOutgoing","RoamingDownload","RoamingUpload","RoamingReceived","RoamingSent"};
	
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
	
	LocationCode location;
//	ModelFactory theModel = new ModelFactory();
	RoamingUsageDB roamUsage = new RoamingUsageDB();
//	PlanModelFactory thePlan = new PlanModelFactory();
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
	public int LocalUsedIncomingMins = 0;
	public int LocalUsedOutgoingMins = 0;
	public int LocalUsedMins = 0;
	public int LocalUsedSentMsgs = 0;
	public int LocalUsedReceivedMsgs = 0;
	public int LocalUsedDownload = 0;
	public int LocalUsedUpload = 0;
	public int LocalUsedMsgs = 0;
	public int LocalUsedData = 0;

	int computationCounter = 0;
	
	public RoamingHandler()
	{
		new Logger().LogMessage(">>RoamingHandler<<");
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		location = new LocationCode();
		location.run();
		int i=0;
		for(;;)
		{
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if(Check_NON_CAN_Operator())
			{
				if(i==0)
				{
//					theModel.UpdateData("roaming","true");
//					MonitoredValues();
					roamUsage.setValue(MapKeys[1], "true");
					CollectedData();
					try {
						Thread.sleep(16*60*60*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++;
				}
			}
		}
	} 
	
	public void MonitoredValues()
	{
//		if(thePlan.SelectData("roam_quota").equalsIgnoreCase("true"))
//		{
//			roamAvailMins = Integer.valueOf(thePlan.SelectData("roam_min")).intValue();
//			roamAvailMsgs = Integer.valueOf(thePlan.SelectData("roam_msg")).intValue();
//			roamAvailData = Integer.valueOf(thePlan.SelectData("roam_data")).intValue();
			
//			roamUsedMins = 0;
//			roamUsedMsgs = 0;
//			roamUsedData = 0;
//			roamIncomingMins = 0;
//			roamOutgoingMins = 0;
//			roamReceivedMsgs = 0;
//			roamSentMsgs = 0;
//			roamDownload = 0;
//			roamUpload = 0;
			
//			LocalUsedIncomingMins = Integer.valueOf(theModel.SelectData("incoming")).intValue();
//			LocalUsedOutgoingMins = Integer.valueOf(theModel.SelectData("outgoing")).intValue();
//			LocalUsedMins = LocalUsedIncomingMins + LocalUsedOutgoingMins;
//			
//			LocalUsedReceivedMsgs = Integer.valueOf(theModel.SelectData("received")).intValue();
//			LocalUsedSentMsgs = Integer.valueOf(theModel.SelectData("sent")).intValue();
//			LocalUsedMsgs = LocalUsedReceivedMsgs + LocalUsedSentMsgs;
//					
//			LocalUsedDownload = 
//					Bytes2MegaBytes(Double.valueOf(theModel.SelectData("downloaded")).doubleValue());
//			LocalUsedUpload = 
//					Bytes2MegaBytes(Double.valueOf(theModel.SelectData("uploaded")).doubleValue());
//			LocalUsedData = LocalUsedDownload + LocalUsedUpload;
			
//			Application.getApplication().invokeLater(new Runnable()
//			{
//				public void run()
//				{
//					new Logger().LogMessage("monitor roaming calc...");
//					roamIncomingMins = 
//							Integer.valueOf(theModel.SelectData("incoming")).intValue() - LocalUsedIncomingMins;
//					roamOutgoingMins = 
//							Integer.valueOf(theModel.SelectData("outgoing")).intValue() - LocalUsedOutgoingMins;
//					roamUsedMins = roamIncomingMins + roamOutgoingMins;
//					roamUsedMins += Integer.valueOf(theModel.SelectData("roam_min")).intValue();
//					
//					roamReceivedMsgs = 
//							Integer.valueOf(theModel.SelectData("received")).intValue() - LocalUsedReceivedMsgs;
//					roamSentMsgs = 
//							Integer.valueOf(theModel.SelectData("sent")).intValue() - LocalUsedSentMsgs;
//					roamUsedMsgs = 
//							roamReceivedMsgs + roamSentMsgs;
//					roamUsedMsgs += Integer.valueOf(theModel.SelectData("roam_msg")).intValue();
//					
//					roamDownload = 
//							Bytes2MegaBytes(Double.valueOf(theModel.SelectData("downloaded")).doubleValue()) - LocalUsedDownload;
//					roamUpload =
//							Bytes2MegaBytes(Double.valueOf(theModel.SelectData("uploaded")).doubleValue()) - LocalUsedUpload;
//					roamUsedData = 
//							roamDownload + roamUpload;
//					roamUsedData += Integer.valueOf(theModel.SelectData("roam_data")).intValue();
//					
//					theModel.UpdateData("roam_min", String.valueOf(roamUsedMins) );
//					theModel.UpdateData("roam_msg", String.valueOf(roamUsedMsgs) );
//					theModel.UpdateData("roam_data", String.valueOf(roamUsedData) );
//				}
//			},60*1000,true);
//		}
	}
	
	public void CollectedData()
	{
		/*if in ROAMING detect and locate co-ordinates and send data*/
		TimeZone timezone = TimeZone.getTimeZone("GMT");
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); 	//GMT time for server		
		
		/**
		 * Standard -- 
		 * 			fix within 7 minutes sends location for each iteration gives 20 seconds resting time to device
		 * 				if NOT wait for 20 minutes and repeat
		 * 				(also adds 1/4 minute to 6 minutes on each iteration) 
		 */
		
		for(int a=0 ; a<=14 ; a++)
		{
			new Logger().LogMessage("Operator::"+RadioInfo.getCurrentNetworkName());
			if( location.getLatitude() != 0 && location.getLongitude() != 0 )
				// [ 0 < i < 7 ] (8 times) ++ [ 9 < i < 12 ] ++ (4 times)
			{
				date = new Date();
				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
				
				roamUsage.setValue(MapKeys[4], "true");
				roamUsage.setValue(MapKeys[5], recordedTimeStamp);
				roamUsage.setValue(MapKeys[2], String.valueOf(location.getLatitude()));
				roamUsage.setValue(MapKeys[3], String.valueOf(location.getLatitude()));
//				theModel.UpdateData("device_time", recordedTimeStamp);
//				theModel.UpdateData("device_time", recordedTimeStamp);
//				theModel.UpdateData("lat", String.valueOf((location.getLatitude())));
//				theModel.UpdateData("lng", String.valueOf((location.getLongitude())));
//				theModel.UpdateData("acc", String.valueOf(location.getAccuracy()));
//				theModel.UpdateData("roaming", String.valueOf(Check_NON_CAN_Operator()));
				roamUsage.setValue(MapKeys[6], gmtTimeStamp);
				//data monitor addition
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(Check_NON_CAN_Operator()) + "|"
						+ roamUsage.getValue(MapKeys[2]) + "|" 
						+ roamUsage.getValue(MapKeys[3]) + "|"
						+ location.getAccuracy() + "|"
//						+ "Down:"+ theModel.SelectData("roam_data") + "|"
//						+ "Up:" + "0" + "|"
//						+ "Received Msgs:" + theModel.SelectData("roam_msg") + "|" 
//						+ "Sent Msgs:" + "0" + "|"
//						+ "Incoming Duration:"+ theModel.SelectData("roma_min") + "|"
//						+ "Outgoing Duration:" + "0" + "##";
						+ "Down:"+ roamUsage.getValue(MapKeys[9]) + "|"
						+ "Up:" + roamUsage.getValue(MapKeys[10]) + "|"
						+ "Received Msgs:" + roamUsage.getValue(MapKeys[11]) + "|" 
						+ "Sent Msgs:" + roamUsage.getValue(MapKeys[12]) + "|"
						+ "Incoming Duration:"+ roamUsage.getValue(MapKeys[7]) + "|"
						+ "Outgoing Duration:" + roamUsage.getValue(MapKeys[8]) + "##";
				
				new MailCode().DebugMail(datatobeMailed);
				
				location.StopTracking();
				location.ResetTracking();
				
				break;
			}
			else if(a==8)
			{
				try {
					location.PauseTracking(20*1000);
					location.ResumeTracking();
					Thread.sleep(30*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if(a==13)
			{
				date = new Date();
				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device t  ime
				
				roamUsage.setValue(MapKeys[4], "false");
				roamUsage.setValue(MapKeys[5], recordedTimeStamp);
				roamUsage.setValue(MapKeys[2], String.valueOf(67.43125));
				roamUsage.setValue(MapKeys[3], String.valueOf(-45.123456));
//				theModel.UpdateData("device_time", recordedTimeStamp);
//				theModel.UpdateData("lat", String.valueOf(67.43125));
//				theModel.UpdateData("lng", String.valueOf(-45.123456));
//				theModel.UpdateData("acc", String.valueOf(1234.1234));
//				theModel.UpdateData("roaming", String.valueOf(Check_NON_CAN_Operator()));
				roamUsage.setValue(MapKeys[6], gmtTimeStamp);
				//data monitor addition
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(Check_NON_CAN_Operator()) + "|"
						+ 67.43125 + "|" 
						+ -45.123456 + "|"											//southern Greenland
						+ 1234.1234 + "|"
//						+ "Down:"+ theModel.SelectData("roam_data") + "|"
//						+ "Up:" + "0" + "|"
//						+ "Received Msgs:" + theModel.SelectData("roam_msg") + "|" 
//						+ "Sent Msgs:" + "0" + "|"
//						+ "Incoming Duration:"+ theModel.SelectData("roma_min") + "|"
//						+ "Outgoing Duration:" + "0" + "##";
						+ "Down:"+ roamUsage.getValue(MapKeys[9]) + "|"
						+ "Up:" + roamUsage.getValue(MapKeys[10]) + "|"
						+ "Received Msgs:" + roamUsage.getValue(MapKeys[11]) + "|" 
						+ "Sent Msgs:" + roamUsage.getValue(MapKeys[12]) + "|"
						+ "Incoming Duration:"+ roamUsage.getValue(MapKeys[7]) + "|"
						+ "Outgoing Duration:" + roamUsage.getValue(MapKeys[8]) + "##";
				new MailCode().DebugMail(datatobeMailed);
				
				location.StopTracking();
				location.ResetTracking();
				
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
	
	
	/**
	 * Method Bytes2MegaBytes.
	 * @param bytes double
	
	 * @return int */
	public int Bytes2MegaBytes(double bytes)
	{
		return Integer.valueOf(
				StringBreaker.split(String.valueOf(bytes/(1024*10234)),".")[0]).intValue();
	}
	
	/**
	 * Method getRoamingIncoming.
	
	 * @return int */
	public int getRoamingIncoming()
	{
		return roamIncomingMins;
	}
	
	/**
	 * Method getRoamingOutgoing.
	
	 * @return int */
	public int getRoamingOutgoing()
	{
		return roamOutgoingMins;
	}
	
	/**
	 * Method getRoamingDownload.
	
	 * @return int */
	public int getRoamingDownload()
	{
		return roamDownload;
	}
	
	/**
	 * Method getRoamingUpload.
	
	 * @return int */
	public int getRoamingUpload()
	{
		return roamUpload;
	}
	
	/**
	 * Method getRoamingReceived.
	
	 * @return int */
	public int getRoamingReceived()
	{
		return roamReceivedMsgs;
	}
	
	/**
	 * Method getRoamingSent.
	
	 * @return int */
	public int getRoamingSent()
	{
		return roamSentMsgs;
	}
	
	/**
	 * Method Check_NON_CAN_Operator.
	
	 * @return boolean */
	public boolean Check_NON_CAN_Operator()
	{
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * Method RoamingCheck.
	
	 * @return boolean */
	public boolean RoamingCheck()
	{
		if((RadioInfo.getNetworkService() & RadioInfo.NETWORK_SERVICE_ROAMING)!=0)
			return true;
		else
			return false;
	}
	
}