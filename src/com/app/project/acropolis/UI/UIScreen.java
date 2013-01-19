package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.system.Application;
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
//    	EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
        
    	//Sends the application in background
        UiApplication.getUiApplication().requestBackground();

     // Set the displayed title of the screen       
    	setTitle("Project Acropolis");
        
//      new MailCode().SendMail("test test");
    	
//    	LocationApplication.getUiApplication().setAcceptEvents(false);
    	
//    	Timer timer = new Timer();
		if(getRoamingState())
        {
//	        timer.cancel();
	        CodesHandler codes = new CodesHandler();
	        codes.run();
	        
	        Thread codethread = new Thread(codes);
	        
	        if(codethread.isAlive())
	        {
				codethread.interrupt();
				Timer timer = new Timer();
		        timer.schedule(new TimerTask() {
		        	public void run()
		        	{
		        		new CodesHandler().run();
		        	}
		        }, 100, 1*60*60*1000);
	        }
			       
//        	    		}
//	        }
        }
        if(!getRoamingState())		
        {
        	/* not in roaming*/
//        				new CodesHandler().run();
//                		try {
//        					Thread.sleep(24 * 60 * 60 * 1000);			//10 seconds of resting time
//        					//1day = 24hrs = 24*60 mins = 24*60*60 seconds = 24*60*60*1000 milli
//        				} catch (InterruptedException e) {
//        					e.printStackTrace();
//        				}
        	
//    		if(LocationApplication.isEventDispatchThread())
//    		{
        	Timer timer = new Timer();
        	new CodesHandler().run();
        	
	        timer.schedule(new TimerTask() {
	        	public void run()
	        	{
	        		new CodesHandler().run();
	        	}
	        }, 100, 1*60*60*1000);
	        
        }
		
//        while( !getRoamingState() )
//    	{
//    		synchronized(LocationApplication.getEventLock())
//    		{
//    			rtf_heading = new RichTextField("Not in roaming",Field.NON_FOCUSABLE|Field.USE_ALL_WIDTH);
//    			add(rtf_heading);
//    		}
//    	}
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
    
//    /**
//     * Disables Save Prompt dialog
//     */
//    public boolean onSavePrompt()
//    {
//    	return false;
//    }
    
}
