package com.app.project.acropolis.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;

import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.engine.mail.MailCode;
import com.app.project.acropolis.engine.monitor.LocationCode;
import com.app.project.acropolis.model.ApplicationDB;

public class ServerChannel //extends TimerTask//implements Runnable//extends Thread
{
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	public String datatobeMailed = "";

	public ServerChannel()
	{
		new Logger().LogMessage("ServerChannel");
	}

	public void JumpStart()
	{
		while(true)
		{
			/*if in ROAMING detect and locate co-ordinates and send data*/
			TimeZone timezone = TimeZone.getDefault();
			String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime()); 	//GMT time for server
			LocationCode location = new LocationCode();
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

						if(!LocationCode.Check_NON_CAN_Operator())
						{
							//data monitor addition
							datatobeMailed = 
									"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
											+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
											+ String.valueOf(LocationCode.Check_NON_CAN_Operator()) + "|"
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
							location.StopTracking();
							location.ResetTracking();
						}
						else
						{
							datatobeMailed = 
									"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
											+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
											+ "true" + "|"
											+ ApplicationDB.getValue(ApplicationDB.Latitude) + "|" 
											+ ApplicationDB.getValue(ApplicationDB.Longitude) + "|"
											+ location.getAccuracy() + "|"
											+ "Down:"+ ApplicationDB.getValue(ApplicationDB.RoamingDownload) + "|"
											+ "Up:" + ApplicationDB.getValue(ApplicationDB.RoamingUpload) + "|"
											+ "Received Msgs:" + ApplicationDB.getValue(ApplicationDB.RoamingReceived) + "|" 
											+ "Sent Msgs:" + ApplicationDB.getValue(ApplicationDB.RoamingSent) + "|"
											+ "Incoming Duration:"+ ApplicationDB.getValue(ApplicationDB.RoamingIncoming) + "|"
											+ "Outgoing Duration:" + ApplicationDB.getValue(ApplicationDB.RoamingOutgoing) + "##";
							new MailCode().DebugMail(datatobeMailed);
							location.StopTracking();
							location.ResetTracking();

						}
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
			try {
				Thread.sleep(5*60*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
