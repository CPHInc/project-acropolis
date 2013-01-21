package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;

/**
 * 
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *
 */

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class UIScreen extends MainScreen
{
	final long GUID = 0xde15415aec6cfa55L;
	final String AppName = "Project Acropolis";
	
	String locationdata = "";
	
	CodesHandler codes;
	
    /**
     * Creates a new MyScreen object
     */
    public UIScreen()
    {        
    	EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
        
    	//Sends the application in background
        UiApplication.getUiApplication().requestBackground();

     // Set the displayed title of the screen       
    	setTitle("Project Acropolis");
        
//		if(getRoamingState())
//        {
//	        CodesHandler codes = new CodesHandler();
//	        codes.run();
//	        
//	        Thread codethread = new Thread(codes);
//	        
//			Timer timer = new Timer();
//	        timer.schedule(new TimerTask() {
//	        	public void run()
//	        	{
//	        		new CodesHandler().run();
//	        	}
//	        }, 1000, 1*60*60*1000);
//			       			
//        }
//		else				//not on roaming	
//        {
    	
        	new CodesHandler().run();
        	
        	Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	        	public void run()
	        	{
	        		new CodesHandler().run();
	        	}
	        }, 1000, 10*60*1000);		//each 1hour
//        }
    }
    
    public boolean getRoamingState()
	{
		//TODO proximity listener will produce accuracy *ROAMING*
		boolean roaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
		return roaming;
	}
    
    public boolean onClose()
    {
    	UiApplication.getUiApplication().requestBackground();
    	return false;
    }
    
}
