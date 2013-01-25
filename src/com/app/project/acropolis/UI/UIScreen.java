package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Application;
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
	
	Timer homecountry = new Timer();
	Timer outsidehomecountry = new Timer();
	
    /**
     * Creates a new MyScreen object
     */
    public UIScreen()
    {        
    	EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);

    	Application.getApplication().setAcceptEvents(false);
    	EventLogger.logEvent(GUID, ("Application in BG").getBytes(),EventLogger.DEBUG_INFO);
    //	Sends the application in background
        UiApplication.getUiApplication().requestBackground();

     // Set the displayed title of the screen       
    	setTitle("Project Acropolis");
        
		if(getRoamingState())
        {
			homecountry.cancel();
			new CodesHandler().run();
			outsidehomecountry = new Timer();
			outsidehomecountry.schedule(new TimerTask() {
	        	public void run()
	        	{
	        		new CodesHandler().run();
	        		
	        		System.gc();
	        	}
	        }, 3000, 1*60*60*1000);
        }
		else if(!getRoamingState())
        {
			outsidehomecountry.cancel();
        	new CodesHandler().run();
        	homecountry = new Timer();
	        homecountry.schedule(new TimerTask() {
	        	public void run()
	        	{
	        		new CodesHandler().run();
	        		
	        		System.gc();
	        	}
	        }, 3000, 1*60*60*1000);
        }
		
    }
    
    public boolean getRoamingState()
	{
    	boolean roaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
    	
    	int roamInt = (roaming ? 1 : 0);
    	
    	switch ( roamInt )
    	{
    		case 1:
    			EventLogger.logEvent(GUID, ("Device in Local Country - Not Roaming!!! ").getBytes(), EventLogger.ALWAYS_LOG);
    			;
    		
    		case 0:
    			EventLogger.logEvent(GUID, ("Device Outside HOME COUNTRY - Roaming!!! ").getBytes(), EventLogger.ALWAYS_LOG);
    			;
    	
    			
    	
    	}
    	
		return roaming;
	}
    
    public boolean onClose()
    {
    	UiApplication.getUiApplication().requestBackground();
    	return false;
    }
    
//    /**
//     * Disables Save Prompt dialog
//     */
//    public boolean onSavePrompt()
//    {
//    	return false;
//    }
    
}
