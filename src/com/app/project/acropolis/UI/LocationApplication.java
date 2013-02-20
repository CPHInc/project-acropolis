package com.app.project.acropolis.UI;

import net.rim.device.api.io.URI;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.SystemListener2;
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
public class LocationApplication extends UiApplication implements RadioStatusListener,SystemListener2
{
	final static long GUID = 0xa0d8b6e395774fc8L;
	final static String AppName = "**Project Acropolis SVN debugger**";

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
   	static CodesHandler code = new CodesHandler();
	
   	public static ModelFactory model = new ModelFactory();
	static UiApplication theApp;// = new LocationApplication();
	
	public static void main(String[] args)
    {
		ApplicationManager.getApplicationManager().setCurrentPowerOnBehavior(ApplicationDescriptor.FLAG_RUN_ON_STARTUP);
	
		if(ApplicationManager.getApplicationManager().inStartup())
		{
			try {
				Thread.sleep(1*60*1000);
				new Logger().LogMessage("slept for 1min ApplicationManager...inStartup()!!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		new Logger().LogMessage("Registering listeners!!!");
		
		theApp = new LocationApplication();
		theApp.addRadioListener((RadioListener)theApp);
		theApp.addSystemListener((SystemListener)theApp);
		
		theApp.enterEventDispatcher();
    }

    /**
     * Creates a new LocationApplication object, checks for SDCard support in the device else exits and creates/opens DataBase
     */
    public LocationApplication()
    {        
		new Logger().LogMessage("Screen pushed");
    	// Push a screen onto the UI stack for rendering.
        pushScreen(new UIScreen());
    }

	public void baseStationChange() {}

	public void networkScanComplete(boolean success) {}

	public void networkServiceChange(int networkId, int service) {}

	public void networkStarted(int networkId, int service) {
		synchronized(Application.getApplication().getEventLock())
		{
			new Logger().LogMessage("Radio & CodesHandler().notify() started");
			code.notify();
		}
	}

	public void networkStateChange(int state) {}

	public void pdpStateChange(int apn, int state, int cause) {}

	public void radioTurnedOff() {
		Radio = false;
		synchronized(Application.getApplication().getEventLock())
		{
			try {
				new Logger().LogMessage("Radio OFF CodesHandler().wait()");
				code.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}	
	}

	public void signalLevel(int level) {}

	public void batteryGood() {}

	public void batteryLow() {}

	public void batteryStatusChange(int arg0) {}

	public void powerOff() {}

	public void powerUp() {}

	public void backlightStateChange(boolean on) {}

	public void cradleMismatch(boolean mismatch) {}

	public void fastReset() {
		//System.exit(0);
	}

	public void powerOffRequested(int reason) {
		if(reason == SystemListener2.POWER_OFF_KEY_PRESSED)
		{
			new Logger().LogMessage("Power off key DOWN -- shut down requested");
			System.exit(0);
		}
	}

	public void usbConnectionStateChange(int state) {}
    
}
