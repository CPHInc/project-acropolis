package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;

/**
 * 
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *
 */

public class BackgroundWorker implements Runnable{
	
	final long GUID = 0x8cac0a9b91bd97b5L;	
	final String AppName = "Project Acropolis";
	
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
		
		if(getRoamingState())
		{
			timer.schedule(new TimerTask() 
			{
				public void run()
				{
					new CodesHandler().run();
				}
			}, 1000, 1*60*60*1000);
		}
		else
		{
			timer.schedule(new TimerTask() 
			{
				public void run()
				{
					new CodesHandler().run();
				}
			}, 1000, 1*60*60*1000);
		}
	}
	
	 public boolean getRoamingState()
		{
			boolean roaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
			
			String homeOperatorName = "";
	    	String currentOperatorName = "";
	    	
	    	currentOperatorName = RadioInfo.getCurrentNetworkName();
			
			return roaming;
			
			
		}
	
}