package com.app.project.acropolis.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.engine.mail.MailCode;
import com.app.project.acropolis.engine.monitor.LocationCode;
import com.app.project.acropolis.model.ApplicationDB;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class RoamingHandler implements Runnable
{
	final String[] MapKeys = {"PhoneNumber","Roaming","RoamingLatitude","RoamingLongitude",
			"RoamingFixAck","RoamingFixDeviceTime","RoamingFixServerTime","RoamingIncoming",
			"RoamingOutgoing","RoamingDownload","RoamingUpload","RoamingReceived","RoamingSent"};
	
	public String datatobeMailed;
	
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	public Date date;
	
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
		for(;;)
		{
			if(LocationCode.Check_NON_CAN_Operator())
			{
				switch ( ((RadioInfo.getActiveWAFs() & RadioInfo.WAF_3GPP)!=0 ? 1:0) )
				{
					case 0:	//Radio OFF
					{
						try {
							new Logger().LogMessage("Radio OFF");
							new Logger().LogMessage("woke up ..");
							Thread.sleep(1*20*1000);
							new Logger().LogMessage("sleeping ..");
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					};
					case 1:
					{
						new Logger().LogMessage("Radio ON");
						new Logger().LogMessage("woke up ..");
						CollectedData();
						new Logger().LogMessage("sleeping ..");
						try {
							Thread.sleep(16*60*60*1000);
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
		ApplicationDB.setValue("true",ApplicationDB.Roaming);
		/*if in ROAMING detect and locate co-ordinates and send data*/
		TimeZone timezone = TimeZone.getDefault();
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); 	//GMT time for server		
		/**
		 * Standard -- fix within 10 minutes 
		 */
		LocationCode location = new LocationCode();
		new Logger().LogMessage("location requested");
		try {
			Thread.sleep(10*60*1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		location.StopTracking();
		if(!ApplicationDB.getValue(ApplicationDB.Latitude).equalsIgnoreCase("0"))
		{
			new Logger().LogMessage("Operator available::" + RadioInfo.getCurrentNetworkName());
			TimeZone serverTimeZone = TimeZone.getTimeZone("GMT-04:00");
			Calendar calendar = Calendar.getInstance(serverTimeZone);
			calendar.setTime(new Date(System.currentTimeMillis()));
			String recordedTimeStamp = sdf.format(calendar.getTime());		//Mailing time
			
			ApplicationDB.setValue("true",ApplicationDB.ACK);
			ApplicationDB.setValue(recordedTimeStamp,ApplicationDB.FixServerTime);
			ApplicationDB.setValue(gmtTimeStamp,ApplicationDB.FixDeviceTime);
			//data monitor addition
			datatobeMailed = 
					"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
					+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
					+ String.valueOf(LocationCode.Check_NON_CAN_Operator()) + "|"
					+ ApplicationDB.getValue(ApplicationDB.Latitude) + "|" 
					+ ApplicationDB.getValue(ApplicationDB.Longitude) + "|"
					+ "100" + "|"
					+ "Down:"+ ApplicationDB.getValue(ApplicationDB.LocalDownload) + "|"
					+ "Up:" + ApplicationDB.getValue(ApplicationDB.LocalUpload) + "|"
					+ "Received Msgs:" + ApplicationDB.getValue(ApplicationDB.LocalReceived) + "|" 
					+ "Sent Msgs:" + ApplicationDB.getValue(ApplicationDB.LocalSent) + "|"
					+ "Incoming Duration:"+ ApplicationDB.getValue(ApplicationDB.LocalIncoming) + "|"
					+ "Outgoing Duration:" + ApplicationDB.getValue(ApplicationDB.LocalOutgoing) + "##";
			new MailCode().DebugMail(datatobeMailed);
		}
		else
		{
			TimeZone serverTimeZone = TimeZone.getTimeZone("GMT-04:00");
			Calendar calendar = Calendar.getInstance(serverTimeZone);
			calendar.setTime(new Date(System.currentTimeMillis()));
			String recordedTimeStamp = sdf.format(calendar.getTime());		//Mailing time
			
			ApplicationDB.setValue("false",ApplicationDB.ACK);
			ApplicationDB.setValue(recordedTimeStamp,ApplicationDB.FixServerTime);
			ApplicationDB.setValue(gmtTimeStamp,ApplicationDB.FixDeviceTime);
			//Data monitoring
			datatobeMailed = 
					"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
					+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
					+ String.valueOf(LocationCode.Check_NON_CAN_Operator()) + "|"				//LocalHandler Roaming method
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
		}
	}
	
}