package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
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
	final String AppName = "Project Acropolis SVN debugger";

	
	String locationdata = "";
	
	Timer homecountry = new Timer();
	Timer outsidehomecountry = new Timer();
	
	
    /**
     * Creates a new MyScreen object
     */
    public UIScreen()
    {        
    	EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
    	
    	new MailCode().InstallationMail();
    	
    	Application.getApplication().setAcceptEvents(false);
    	EventLogger.logEvent(GUID, ("Application requested for Background entry").getBytes(),EventLogger.DEBUG_INFO);
    //	Sends the application in background
        UiApplication.getUiApplication().requestBackground();

     // Set the displayed title of the screen       
    	setTitle(" ** DEBUG Version ** Project Acropolis ");
        
    	Thread RoamThread = new Thread(new RoamingRunnable());
    	RoamThread.start();			//monitors roaming changes, takes appropriate actions
    	
    	new CodesHandler().run();
    	
    	
    	EventLogger.logEvent(GUID, ("10min for()").getBytes(),EventLogger.ALWAYS_LOG);
    	for(;;)
    	{
    		try {
				Thread.sleep(10*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		new CodesHandler().run();
    	}
    	
//    	EventLogger.logEvent(GUID, ("10min timertask").getBytes(),EventLogger.ALWAYS_LOG);
//    	new Timer().schedule(new TimerTask() 
//		{
//			public void run()
//			{
//				new CodesHandler().run();
//			}
//		}, 10*1000, 10*60*1000);
    	
    }
    
    public boolean onClose()
    {
    	UiApplication.getUiApplication().requestBackground();
    	return false;
    }
    
}