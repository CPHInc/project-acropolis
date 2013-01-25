package com.app.project.acropolis.UI;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;


/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * Gathers and arranges codes from LocationCode class and MailCode class
 * 
 * <reason for Runnable over Thread--resusability>
 */
public class CodesHandler implements Runnable {

	final long GUID = 0x29ef40e6e31efd2L;
	final String AppName = "Project Acropolis";
	
	LocationCode location;
	
	/*format followed #1.0.1|Data Stream|PhoneNumber|TimeStamp(GMT)|DeviceTime|Roaming|LAT|LNG|Accuracy# */
	public String datatobeMailed = "";
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	public Date date;
	public Calendar cal; 
	
//	int NO_FIX_SLEEP = 20 *60 *1000;						//20 MINs
//	int FIX_BREATHING = 10 *1000;							//10 SECs
//	int LOCATION_BREATHING = 6 *1000;
//	int FIX_TIMER_DURATION = 10 *60 *1000 + 1;			//10MINs + 1

	int NO_FIX_SLEEP = 20 *60 *1000;						//20 MINs
	int FIX_BREATHING = 30 *1000;							//30 SECs
	int LOCATION_BREATHING = 6 *1000;						//6 SECs
	
	int FIX_TIMER_DURATION = 6;			//16MINs == 8 cycles == 8 minutes
	
	
	/**
	 * 
	 */
	public void run()
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		CollectedData();
	}
	
	
	public void CollectedData()
	{
		/*if in ROAMING detect and locate co-ordinates and send data*/
		TimeZone timezone = TimeZone.getTimeZone("GMT");
		String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime() ); 	//GMT time for server		
		
		location = new LocationCode();
		
		int i=0;
		int j=0;
		int k=0;
		/**
		 * Standard -- 
		 * 			fix within 6 minutes sends location for each iteration gives 20 seconds resting time to device
		 * 				if NOT wait for 20 minutes and repeat
		 * 				(also adds 1/4 minute to 6 minutes on each iteration) 
		 */
		//testing
//		i = FIX_TIMER_DURATION - 3;
		//testing
		
		location.run();
		
		for(int a=0 ; a<=14 ; a++)
		{
			
			if( location.getLatitude() != 0 && location.getLongitude() != 0 )
				// [ 0 < i < 3 ] (4 times) ++ [ 5 < i < 8 ] ++ (4 times)
			{
				date = new Date();
				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
				
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(location.getRoamingState()) + "|"
						+ location.getLatitude() + "|" 
						+ location.getLongitude() + "|"
						+ location.getAccuracy() +"##";
				
				new MailCode().SendMail(datatobeMailed);
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
				if(this.getRoamingState())
				{
					date = new Date();
					String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device time
					
					datatobeMailed = 
							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
							+ String.valueOf(this.getRoamingState()) + "|"				//CodesHandler Roaming method 
							+ 67.43125 + "|" 
							+ -45.123456 + "|"											//southern Greenland
							+ 1234.1234 +"##";
					
					new MailCode().SendMail(datatobeMailed);
					
					location.StopTracking();
					location.ResetTracking();
					
					break;
				}
				else
				{
					date = new Date();
					String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
					
					datatobeMailed = 
							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
							+ String.valueOf(this.getRoamingState()) + "|"				//CodesHandler Roaming method 
							+ 67.43125 + "|" 
							+ -45.123456 + "|"											//southern Greenland
							+ 1234.1234 +"##";
					
					new MailCode().SendMail(datatobeMailed);

					location.StopTracking();
					location.ResetTracking();
					
					break;
				}
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
		
		
//		
//		for(;;)			//waits for fix if not available waits for 10 seconds and trys again 
//		{
//			++k;
//			
//			if((location.getLatitude()!=0 && location.getLongitude()!=0) && i<FIX_TIMER_DURATION)
//			{		/*60 loops*/
//				date = new Date();
//				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
//				
//				datatobeMailed = 
//						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
//						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//						+ String.valueOf(location.getRoamingState()) + "|"
//						+ location.getLatitude() + "|" 
//						+ location.getLongitude() + "|"
//						+ location.getAccuracy() +"##";
//				
//				new MailCode().SendMail(datatobeMailed);
//				break;
//			}
//			
//			
//			
//			else if ( i>=( FIX_TIMER_DURATION + j ) )									//6mins	++ 0.25 for each iteration
//			{
//				/**
//				 * Application is going in sleep so as to conserve power and processing
//				 * */
//				
//				
//				if(this.getRoamingState())
//				{
//					date = new Date();
//					String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Device time
//					
//					datatobeMailed = 
//							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
//							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//							+ String.valueOf(this.getRoamingState()) + "|"				//CodesHandler Roaming method 
//							+ 67.43125 + "|" 
//							+ -45.123456 + "|"											//southern Greenland
//							+ 1234.1234 +"##";
//					
//					new MailCode().SendMail(datatobeMailed);
//				}
//				else
//				{
//					date = new Date();
//					String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
//					
//					datatobeMailed = 
//							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
//							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//							+ String.valueOf(this.getRoamingState()) + "|"				//CodesHandler Roaming method 
//							+ 67.43125 + "|" 
//							+ -45.123456 + "|"											//southern Greenland
//							+ 1234.1234 +"##";
//					
//					new MailCode().SendMail(datatobeMailed);
//				}
//				
//				
//				try 
//				{
//					location.PauseTracking( NO_FIX_SLEEP );
//					location.ResumeTracking();
//					Thread.sleep(NO_FIX_SLEEP);											//go to sleep for 20 minutes
//					k+=10;
//					i=0;
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
////				i++;	//increment of time for extra elongation
//				
//				
//			}
//			
//			
//			
//			else if(k == 34)													//58 minutes
//			{
//				Thread locationthread = new Thread(new LocationCode());
//				if(locationthread.isAlive())
//				{
//					date = new Date();
//					String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
//					
//					datatobeMailed = 
//							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
//							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//							+ String.valueOf(this.getRoamingState()) + "|"				//CodesHandler Roaming method 
//							+ 67.43125 + "|" 
//							+ -45.123456 + "|"											//southern Greenland
//							+ 1234.0987 +"##";											//1234.0987 TimerTask is rebooting with no proper fix
//					
//					new MailCode().SendMail(datatobeMailed);
//					locationthread.interrupt();
//					break;
//				}
//				else
//				{
//					date = new Date();
//					String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
//					
//					datatobeMailed = 
//							"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
//							+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
//							+ String.valueOf(this.getRoamingState()) + "|"				//CodesHandler Roaming method 
//							+ 67.43125 + "|" 
//							+ -45.123456 + "|"											//southern Greenland
//							+ 1234.0987 +"##";											//1234.0987 TimerTask is rebooting with no proper fix
//					
//					new MailCode().SendMail(datatobeMailed);
//					locationthread.interrupt();
//					break;
//				}
//					
//			}
//			
//			
//			
//			else		//trying for coordinates and now sleep definitely
//			{
//				try {
//					location.PauseTracking(LOCATION_BREATHING);				//6seconds breathing
//					location.ResumeTracking();			
//					Thread.sleep(FIX_BREATHING);						//now sleeping for 30 seconds 
//					//initial run trying to get the fix and 
//																//works once if no fix available try again after 30 seconds
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				j+=0.25;								//add quarter minute for each sleep-fix loop
//			}
//			location.StopTracking();
//			location.ResetTracking();
//		}
		
		
		
//		i+=FIX_BREATHING;
//		i++;
		
		
	}
	
	/**
	 *	Most crucial within the application
	 * 
	 * @return Roaming boolean state from LocationCode class
	 */
	public boolean getRoamingState()
	{
	   	boolean roaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
		return roaming;
	}
	    
	
	/**
	 * @return Device's formatted phone number
	 */
	public String getDevicePhoneNumber()
	{
		return Phone.getDevicePhoneNumber(true);
	}
	
}