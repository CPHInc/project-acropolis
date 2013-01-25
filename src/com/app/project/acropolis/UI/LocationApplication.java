package com.app.project.acropolis.UI;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.system.DeviceInfo;
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
   	static boolean PowerON = false;
   	
   	static Thread thread; 
   	static String[] app_arg = new String[10];
   	
   	
	public static void main(String[] args)
    {
//		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		app_arg = args;
		final LocationApplication theApp = new LocationApplication();
    
//		ApplicationDescriptor appDesc = ApplicationDescriptor.currentApplicationDescriptor();
//		
//		new ApplicationDescriptor(appDesc , appDesc.getName() , appDesc.getArgs() , appDesc.getPosition() ,
//									appDesc.getNameResourceBundle() , appDesc.getNameResourceId() ,
//									4 , 		//FLAG_AUTO_RESTART
//									appDesc.getFolderName());
		
		
    	LocationApplication.getApplication().addSystemListener(new SystemListener() {

			public void batteryGood() {}

			public void batteryLow() {}

			public void batteryStatusChange(int status)
			{
				if(status == DeviceInfo.BSTAT_NO_RADIO)
					PowerON = false;
				else
					PowerON = true;
			}

			public void powerOff() 
			{
				PowerON = false;
			}

			public void powerUp() 
			{
				PowerON = true;
			}
    	});
        	
    	while(PowerON)
    	{
			theApp.enterEventDispatcher();
    	}
    	
    }

    /**
     * Creates a new LocationApplication object
     */
    public LocationApplication()
    {        
        // Push a screen onto the UI stack for rendering.
        pushScreen(new UIScreen());
    }

}
