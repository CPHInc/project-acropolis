package com.app.project.acropolis.UI;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;

import com.app.project.acropolis.model.ModelFactory;


/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * Gathers and arranges codes from LocationCode class and MailCode class
 * 
 * <reason for Runnable over Thread--resusability>
 */
public class CodesHandler// implements RadioStatusListener
{

	final long GUID = 0x29ef40e6e31efd2L;
	final String AppName = "**Project Acropolis SVN debugger**";
	
	LocationCode location;
	ModelFactory theModel = new ModelFactory();
	
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
	
	public CodesHandler()
	{
		new Logger().LogMessage("--->CodeHandler()<---");
		
		//checks Radio power 30secs if OFF on first trial 
		for(int i=0;i<=2;i++)
		{
			switch ( ((RadioInfo.getActiveWAFs() & RadioInfo.WAF_3GPP)!=0 ? 1:0) )
			{
				case 0:	//Radio OFF
				{
					new Logger().LogMessage("Radio OFF");
					try {
						Thread.sleep(30*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
				case 1: //Radio ON
				{
					new Logger().LogMessage("Radio ON");
					CollectedData();
					i=2;
				};
			}
		}
		
//		new Timer().schedule(new TimerTask() {
//			public void run()
//			{
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
						new Logger().LogMessage("sleeping ..");
					};
					case 1: //Radio ON
					{
						new Logger().LogMessage("Radio ON");
						try {
							Thread.sleep(5*60*60*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
						new Logger().LogMessage("woke up...");
						CollectedData();
						new Logger().LogMessage("sleeping...");
					};
				}
		}
//			}
//		}, 1*60*60*1000);
	}
	
	public void CollectedData()
	{
		/*if in ROAMING detect and locate co-ordinates and send data*/
		TimeZone timezone = TimeZone.getTimeZone("GMT-5");
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); 	//GMT time for server
		
//		new Logger().LogMessage("time -- "+gmtTimeStamp);
//		theModel.UpdateData("server_time", gmtTimeStamp);
		
		location = new LocationCode();
		/**
		 * Standard -- 
		 * 			fix within 7 minutes sends location for each iteration gives 20 seconds resting time to device
		 * 				if NOT wait for 20 minutes and repeat
		 * 				(also adds 1/4 minute to 6 minutes on each iteration) 
		 */
		location.run();
		theModel.UpdateData("phone_number", Phone.getDevicePhoneNumber(false) );
		for(int a=0;a<14;a++)
		{
			if( RadioInfo.getCurrentNetworkName()!=null )//||(RadioInfo.getCurrentNetworkName() ==null))
				
			{
				new Logger().LogMessage("Operator available" + RadioInfo.getCurrentNetworkName());
				if( location.getLatitude() != 0 && location.getLongitude() != 0 )
					// [ 0 < i < 7 ] (8 times) ++ [ 9 < i < 12 ] ++ (4 times)
				{
					theModel.UpdateData("fix_ack", "true");
					date = new Date();
					String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
					theModel.UpdateData("device_time", recordedTimeStamp);
					theModel.UpdateData("lat", String.valueOf(location.getLatitude()));
					theModel.UpdateData("lng", String.valueOf(location.getLongitude()));
					theModel.UpdateData("acc", String.valueOf(location.getAccuracy()));
					theModel.UpdateData("roaming", String.valueOf(Check_NON_CAN_Operator()));
					
					datatobeMailed = 
							"#1.0.1|DataStream|"+  theModel.SelectData("phone_number") + "|"
							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
							+ String.valueOf(Check_NON_CAN_Operator()) + "|"
							+ location.getLatitude() + "|" 
							+ location.getLongitude() + "|"
							+ location.getAccuracy() +"##";
					
					new MailCode().SendMail(datatobeMailed);
					
					//data monitor addition
					datatobeMailed = 
							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//							+ String.valueOf(Check_NON_CAN_Operator()) + "|"
							+ String.valueOf(RoamingCheck()) + "|"
							+ location.getLatitude() + "|" 
							+ location.getLongitude() + "|"
							+ location.getAccuracy() + "|"
							+ "Down:"+ theModel.SelectData("downloaded") + "|"
							+ "Up:" + theModel.SelectData("uploaded") + "|"
							+ "Received Msgs:" + theModel.SelectData("received") + "|" 
							+ "Sent Msgs:" + theModel.SelectData("sent") + "|"
							+ "Incoming Duration:"+ theModel.SelectData("incoming") + "|"
							+ "Outgoing Duration:" + theModel.SelectData("outgoing") + "##";
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
					theModel.UpdateData("fix_ack", "false");
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
					
					//Data monitoring
					datatobeMailed = 
							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//							+ String.valueOf(Check_NON_CAN_Operator()) + "|"				//CodesHandler Roaming method
							+ String.valueOf(RoamingCheck()) + "|"				//CodesHandler Roaming method
							+ 67.43125 + "|" 
							+ -45.123456 + "|"											//southern Greenland
							+ 1234.1234 +"|"
							+ "Down:"+ theModel.SelectData("downloaded") + "|"
							+ "Up:" + theModel.SelectData("uploaded") + "|"
							+ "Received Msgs:" + theModel.SelectData("received") + "|" 
							+ "Sent Msgs:" + theModel.SelectData("sent") + "|"
							+ "Incoming Duration:"+ theModel.SelectData("incoming") + "|"
							+ "Outgoing Duration:" + theModel.SelectData("outgoing") + "##";
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
	
	public boolean Check_NON_CAN_Operator()
	{
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

	
	public boolean RoamingCheck()
	{
		if((RadioInfo.getNetworkService() & RadioInfo.NETWORK_SERVICE_ROAMING)!=0)
			return true;
		else
			return false;
	}
	
}