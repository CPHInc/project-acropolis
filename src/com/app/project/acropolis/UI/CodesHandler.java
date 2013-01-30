package com.app.project.acropolis.UI;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;


/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * Gathers and arranges codes from LocationCode class and MailCode class
 * 
 * <reason for Runnable over Thread--resusability>
 */
public class CodesHandler implements Runnable {

	final long GUID = 0x29ef40e6e31efd2L;
	final String AppName = "Project Acropolis SVN debugger";
	
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
		 * 			fix within 7 minutes sends location for each iteration gives 20 seconds resting time to device
		 * 				if NOT wait for 20 minutes and repeat
		 * 				(also adds 1/4 minute to 6 minutes on each iteration) 
		 */
		location.run();
		
		for(int a=0 ; a<=14 ; a++)
		{
			
			if( location.getLatitude() != 0 && location.getLongitude() != 0 )
				// [ 0 < i < 7 ] (8 times) ++ [ 9 < i < 12 ] ++ (4 times)
			{
				date = new Date();
				String recordedTimeStamp = sdf.formatLocal(date.getTime());		//Mailing time
				
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(Check_NON_CAN_Operator()) + "|"
						+ location.getLatitude() + "|" 
						+ location.getLongitude() + "|"
						+ location.getAccuracy() +"##";
				
				new MailCode().SendMail(datatobeMailed);
				
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
				
				datatobeMailed = 
						"#1.0.1|DataStream|"+  Phone.getDevicePhoneNumber(false) + "|"
						+ gmtTimeStamp + "|" + recordedTimeStamp + "|" 
						+ String.valueOf(Check_NON_CAN_Operator()) + "|"				//CodesHandler Roaming method 
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
				try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
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
	    
	public boolean Check_NON_CAN_Operator()
	{
		boolean NON_CANOperatorCheck = true;
   	
		final String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
		    	
		String CurrentNetworkName = "";
		    	
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