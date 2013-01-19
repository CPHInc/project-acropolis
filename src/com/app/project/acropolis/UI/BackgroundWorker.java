package com.app.project.acropolis.UI;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;

/**
 * 
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *
 */

public class BackgroundWorker {
	
	final long GUID = 0x8cac0a9b91bd97b5L;
	final String AppName = "Project Acropolis";
	
	CodesHandler codes;

	public BackgroundWorker()
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		
		StartWorking();
	}
	
	public void StartWorking()
	{
		new CodesHandler().run();
		
		if(getRoamingState())
		{
			//do nothing till the device is in roaming
		}
	}
	
	 public boolean getRoamingState()
		{
			//TODO proximity listener will produce accuracy *ROAMING*
			boolean roaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
			return roaming;
		}
	    
	
}
