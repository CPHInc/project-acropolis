package com.app.project.acropolis.UI;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.UiApplication;

/**
 * @vendor CellPhoneHospitalInc
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * ---RELEASE NOTES---
 * @version 1.0.1
 * @desc Advices corporate head office of the device 
 * 		location when on Roaming (Strictly for Enterprise users only)
 */


/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class LocationApplication extends UiApplication
{
	final static long GUID = 0xa0d8b6e395774fc8L;
	final static String AppName = "Project Acropolis";

	final static String AppBG = "enter_background";
	final static String AppFG = "enter_foreground";
	
	static boolean BG_Icon = false;
	static boolean PowerOFF = false;
   	static boolean PowerON = true;
   	
   	static Thread thread; 
   	static String[] app_arg = new String[10];
   	
   	
	public static void main(String[] args)
    {
//		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		app_arg = args;
		final LocationApplication theApp = new LocationApplication();
        
//        if(args[0].equalsIgnoreCase(AppFG))
//        {
		while(PowerON==false)
        	LocationApplication.getApplication().addSystemListener(new SystemListener() {

				public void batteryGood() {
					// TODO Auto-generated method stub
					PowerON = true;
					try{
						Thread.sleep(2*1000);
					} catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				public void batteryLow() {
					// TODO Auto-generated method stub
					
				}

				public void batteryStatusChange(int status) {
					// TODO Auto-generated method stub
				}

				public void powerOff() {
					// TODO Auto-generated method stub
				}

				public void powerUp() {
					//thread = Thread.currentThread();
					//thread.notifyAll();
					PowerON = true;
					try{
						Thread.sleep(3*1000);		//launches application 3 sec after booting up  
					} catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					
					if(app_arg[0].equalsIgnoreCase(AppBG))
						theApp.enterEventDispatcher();
					else
						theApp.enterEventDispatcher();
				}
        		
        	});
        	
        	//        }
//        else
//        {
//	      	EventLogger.logEvent(GUID, ("Application entered Back-ground state").getBytes(), EventLogger.DEBUG_INFO);
//	      	theApp.enterEventDispatcher();
////      	new BackgroundWorker();
//      	//TODO -- background thread to be executed
//      	}
//        else
//        {
//        	EventLogger.logEvent(GUID, ("Application in an undefined state").getBytes(), EventLogger.SEVERE_ERROR);
//        	BG_Icon = false;
//        	theApp.enterEventDispatcher();
//        	//Application in a huge problem
//        }

    }

    /**
     * Creates a new LocationApplication object
     */
    public LocationApplication()
    {        
        // Push a screen onto the UI stack for rendering.
        pushScreen(new UIScreen());
    }

//    /**
//     * Hides the icon when in Background processing mode
//     */
//    public boolean acceptsForeground()
//    {
//    	return BG_Icon;
//    }
}
