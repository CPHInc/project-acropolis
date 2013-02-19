package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SystemListener;

/**
 * 
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *
 */

public class BackgroundWorker implements Runnable//extends Thread implements SystemListener
{
	final long GUID = 0x8cac0a9b91bd97b5L;	
	final String AppName = "Project Acropolis";

	boolean PowerON = true;
	
	CodesHandler codes;

	public BackgroundWorker()
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		Application.getApplication().setAcceptEvents(false);			//not an event dispatching thread
	}
	
	public void run()
	{
		new CodesHandler().run();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() 
		{
			public void run()
			{
				new CodesHandler().run();
			}
		}, 1000, 1*60*60*1000);
		
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
	 
	public void batteryGood() {
		// TODO Auto-generated method stub
	}

	public void batteryLow() {
		// TODO Auto-generated method stub
	}

	public void batteryStatusChange(int status) {
		// TODO Auto-generated method stub
	}

	public void powerOff() {
		// TODO Auto-generated method stub
		PowerON = false;
	}

	public void powerUp() {
		// TODO Auto-generated method stub
		PowerON = true;
	}
	
}