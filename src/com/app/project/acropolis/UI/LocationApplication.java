package com.app.project.acropolis.UI;

import net.rim.device.api.io.URI;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.UiApplication;

import com.app.project.acropolis.model.ModelFactory;

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
public class LocationApplication extends UiApplication implements RadioStatusListener
{
	final static long GUID = 0xa0d8b6e395774fc8L;
	final static String AppName = "Project Acropolis SVN debugger";

	final static String AppBG = "enter_background";
	final static String AppFG = "enter_foreground";
	
	public String DBName = "ACTIVITY_ACROPOLIS.DB";
	public String DBPath = "file://SDCard/BlackBerry/ProjectAcropolis/Database/"+DBName;
	public URI db_uri;
	
	static boolean BG_Icon = false;
	static boolean Starting = false;

	public static boolean Radio = false;
	public static boolean Power = false;
	
	static Thread codeThread = new Thread(new CodesHandler());
   	
   	public static ModelFactory model = new ModelFactory();
	static LocationApplication theApp = new LocationApplication();
	public static void main(String[] args)
    {
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		
		ApplicationManager.getApplicationManager().setCurrentPowerOnBehavior(ApplicationDescriptor.FLAG_RUN_ON_STARTUP);
		Application.getApplication().invokeAndWait(new Runnable()
		{
			public void run()
			{
				if(ApplicationManager.getApplicationManager().inStartup())
				{
					try {
						Thread.sleep(1*60*1000);
						EventLogger.logEvent(GUID, ("slept for 1min ApplicationManager...inStartup()!!").getBytes(),EventLogger.ALWAYS_LOG);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		Application.getApplication().addRadioListener((RadioListener)theApp);
		EventLogger.logEvent(GUID, ("Listeners registered").getBytes(),EventLogger.ALWAYS_LOG);

		EventLogger.logEvent(GUID, ("Application in event dispatching").getBytes(),EventLogger.ALWAYS_LOG);
		theApp.enterEventDispatcher();
    }

    /**
     * Creates a new LocationApplication object, checks for SDCard support in the device else exits and creates/opens DataBase
     */
    public LocationApplication()
    {        
    	// Push a screen onto the UI stack for rendering.
        pushScreen(new UIScreen());
    }

    public static void AddListeners()
    {
    	Application.getApplication().addRadioListener((RadioListener) new RadioStatusListener()
		{
			public void baseStationChange() {}

			public void networkScanComplete(boolean success) {}

			public void networkServiceChange(int networkId, int service) {}

			public void networkStarted(int networkId, int service) {
			
			}

			public void networkStateChange(int state) {}

			public void pdpStateChange(int apn, int state, int cause) {}

			public void radioTurnedOff() {
				
			}

			public void signalLevel(int level) {}
			
		});
    }

	public void baseStationChange() {
		// TODO Auto-generated method stub
		
	}

	public void networkScanComplete(boolean success) {
		// TODO Auto-generated method stub
		
	}

	public void networkServiceChange(int networkId, int service) {
		// TODO Auto-generated method stub
		
	}

	public void networkStarted(int networkId, int service) {
		// TODO Auto-generated method stub
		synchronized(Application.getApplication().getAppEventLock())
		{
			codeThread.notify();
			EventLogger.logEvent(GUID, ("Radio & CodesHandler() started").getBytes(), EventLogger.ALWAYS_LOG);
		}
	}

	public void networkStateChange(int state) {
		// TODO Auto-generated method stub
		
	}

	public void pdpStateChange(int apn, int state, int cause) {
		// TODO Auto-generated method stub
		
	}

	public void radioTurnedOff() {
		// TODO Auto-generated method stub
		Radio = false;
		synchronized(Application.getApplication().getAppEventLock())
		{
			try {
				EventLogger.logEvent(GUID, ("Radio OFF Thread.wait() CodesHandler()").getBytes(),EventLogger.ALWAYS_LOG);
				codeThread.wait();		//make the CodesHandler() wait
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}

	public void signalLevel(int level) {
		// TODO Auto-generated method stub
		
	}
    
}
