package com.app.project.acropolis.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.engine.mail.MailCode;
import com.app.project.acropolis.engine.monitor.LocationCode;
import com.app.project.acropolis.model.ApplicationDB;


/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * Gathers and arranges codes from LocationCode class and MailCode class
 * 
 * <reason for Runnable over Thread--resusability>
 * @version $Revision: 1.0 $
 */
public class LocalHandler implements Runnable
{
	String[] MapKeys = {"PhoneNumber","Roaming","Latitude","Longitude",
			"FixAck","FixDeviceTime","FixServerTime","Incoming",
			"Outgoing","Download","Upload","Received","Sent"};
	
	/*format followed #1.0.1|Data Stream|PhoneNumber|TimeStamp(GMT)|DeviceTime|Roaming|LAT|LNG|Accuracy# */
	public String datatobeMailed = "";
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	public Date date;
	public Calendar cal; 
	
	public boolean NON_CANOperatorCheck = true;
	public final String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
	public String CurrentNetworkName = "";
	
	public Timer handler = new Timer();
	public int WAFs = 0;
//	ModelFactory theModel = new ModelFactory();
	LocationCode location;
	
	public LocalHandler()
	{
		new Logger().LogMessage("--->LocalHandler()<---");
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if(!Check_NON_CAN_Operator())
			{
				ApplicationDB.setValue("false",ApplicationDB.Roaming);
				for(;;)
				{
					switch ( ((RadioInfo.getActiveWAFs() & RadioInfo.WAF_3GPP)!=0 ? 1:0) )
					{
						case 0:	//Radio OFF
						{
							new Logger().LogMessage("Radio OFF");
							new Logger().LogMessage("woke up ..");
							try {
								Thread.sleep(1*60*60*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						};
						case 1: //Radio ON
						{
							new Logger().LogMessage("woke up...");
							CollectedData();
							new Logger().LogMessage("Radio ON");
							new Logger().LogMessage("sleeping...");
							try {
								Thread.sleep(12*60*60*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						};
					}
				}
			}
	}
	
	public void CollectedData()
	{
		/*if in ROAMING detect and locate co-ordinates and send data*/
		TimeZone timezone = TimeZone.getDefault();
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime()); 	//GMT time for server
		location = new LocationCode();
		/**
		 * Standard -- 
		 * 			fix within 7 minutes sends location for each iteration gives 20 seconds resting time to device
		 * 				if NOT wait for 20 minutes and repeat
		 * 				(also adds 1/4 minute to 6 minutes on each iteration) 
		 */
		location.run();
		for(int a=0;a<14;a++)
		{
			if( RadioInfo.getCurrentNetworkName()!=null )
			{
				new Logger().LogMessage("Operator available::" + RadioInfo.getCurrentNetworkName());
				if( location.getLatitude() != 0 && location.getLongitude() != 0 )
					// [ 0 < i < 7 ] (8 times) ++ [ 9 < i < 12 ] ++ (4 times)
				{
					TimeZone serverTimeZone = TimeZone.getTimeZone("GMT-04:00");
					Calendar calendar = Calendar.getInstance(serverTimeZone);
					calendar.setTime(new Date(System.currentTimeMillis()));
					String recordedTimeStamp = sdf.format(calendar.getTime());		//Mailing time
					
					ApplicationDB.setValue("true",ApplicationDB.ACK);
					ApplicationDB.setValue(recordedTimeStamp,ApplicationDB.FixServerTime);
					ApplicationDB.setValue(gmtTimeStamp,ApplicationDB.FixDeviceTime);
					ApplicationDB.setValue(String.valueOf(location.getLatitude()),ApplicationDB.Latitude);
					ApplicationDB.setValue(String.valueOf(location.getLongitude()),ApplicationDB.Longitude);
					//data monitor addition
					datatobeMailed = 
							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//							+ String.valueOf(Check_NON_CAN_Operator()) + "|"
							+ String.valueOf(RoamingCheck()) + "|"
							+ ApplicationDB.getValue(ApplicationDB.Latitude) + "|" 
							+ ApplicationDB.getValue(ApplicationDB.Longitude) + "|"
							+ location.getAccuracy() + "|"
							+ "Down:"+ ApplicationDB.getValue(ApplicationDB.LocalDownload) + "|"
							+ "Up:" + ApplicationDB.getValue(ApplicationDB.LocalUpload) + "|"
							+ "Received Msgs:" + ApplicationDB.getValue(ApplicationDB.LocalReceived) + "|" 
							+ "Sent Msgs:" + ApplicationDB.getValue(ApplicationDB.LocalSent) + "|"
							+ "Incoming Duration:"+ ApplicationDB.getValue(ApplicationDB.LocalIncoming) + "|"
							+ "Outgoing Duration:" + ApplicationDB.getValue(ApplicationDB.LocalOutgoing) + "##";
					new MailCode().DebugMail(datatobeMailed);
//					new Logger().LogMessage("Downloaded and Uploaded mail sent");
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
					TimeZone serverTimeZone = TimeZone.getTimeZone("GMT-04:00");
					Calendar calendar = Calendar.getInstance(serverTimeZone);
					calendar.setTime(new Date(System.currentTimeMillis()));
					String recordedTimeStamp = sdf.format(calendar.getTime());		//Mailing time
					
					ApplicationDB.setValue("false",ApplicationDB.ACK);
					ApplicationDB.setValue(recordedTimeStamp,ApplicationDB.FixServerTime);
					ApplicationDB.setValue(gmtTimeStamp,ApplicationDB.FixDeviceTime);
					ApplicationDB.setValue(String.valueOf(67.43125),ApplicationDB.Latitude);
					ApplicationDB.setValue(String.valueOf(-45.123456),ApplicationDB.Longitude);
					//Data monitoring
					datatobeMailed = 
							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
							+ String.valueOf(Check_NON_CAN_Operator()) + "|"				//LocalHandler Roaming method
							+ 67.43125 + "|" 
							+ -45.123456 + "|"											//southern Greenland
							+ 1234.1234 +"|"
							+ "Down:"+ ApplicationDB.getValue(ApplicationDB.LocalDownload) + "|"
							+ "Up:" + ApplicationDB.getValue(ApplicationDB.LocalUpload) + "|"
							+ "Received Msgs:" + ApplicationDB.getValue(ApplicationDB.LocalReceived) + "|" 
							+ "Sent Msgs:" + ApplicationDB.getValue(ApplicationDB.LocalSent) + "|"
							+ "Incoming Duration:"+ ApplicationDB.getValue(ApplicationDB.LocalIncoming) + "|"
							+ "Outgoing Duration:" + ApplicationDB.getValue(ApplicationDB.LocalOutgoing) + "##";
					new MailCode().DebugMail(datatobeMailed);
//					new Logger().LogMessage("Downloaded and Uploaded mail sent");
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
			else
			{
				new Logger().LogMessage("No operator will check after 20seconds");
				try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
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
		CurrentNetworkName = RadioInfo.getNetworkName(RadioInfo.getCurrentNetworkIndex());
		    	
		if(CurrentNetworkName == null)
		{
			new Logger().LogMessage("no network found");
		}
		else
		{
			new Logger().LogMessage("Device registered on " + CurrentNetworkName);
			if( CurrentNetworkName.equalsIgnoreCase(CanadianOperators[0]) 
			  			|| CurrentNetworkName.equalsIgnoreCase(CanadianOperators[1])
			   			||CurrentNetworkName.equalsIgnoreCase(CanadianOperators[2]) )
				NON_CANOperatorCheck = false;				//if Current Operator is CANADIAN then **FALSE**
			else
				NON_CANOperatorCheck = true;				//if Current Operator is not CANADIAN then **TRUE** hence ROAMING
			    
		}
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