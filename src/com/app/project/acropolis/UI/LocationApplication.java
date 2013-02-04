package com.app.project.acropolis.UI;

import java.util.Enumeration;

import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.io.URI;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

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
public class LocationApplication extends UiApplication implements SystemListener,RadioStatusListener
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
   	static boolean PowerON = false;
	static boolean RadioON = false;
   	
   	static Thread thread;
   	static String[] app_arg = new String[10];
   	
   	public static ModelFactory model = new ModelFactory();
   	
   	public static boolean Roaming = false;
   	
	public static void main(String[] args)
    {
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
		app_arg = args;
    
		LocationApplication theApp = new LocationApplication();
		BackgroundWorker theBGApp = new BackgroundWorker();
		
		ApplicationManager.getApplicationManager().setCurrentPowerOnBehavior(ApplicationDescriptor.FLAG_RUN_ON_STARTUP);
		
		Starting = ApplicationManager.getApplicationManager().inStartup();
		if(Starting)
		{
			Application.getApplication().addSystemListener(theApp);
		}
		else
		{
			new MailCode().InstallationMail();
			theApp.enterEventDispatcher();
		}
		
    }

    /**
     * Creates a new LocationApplication object
     */
    public LocationApplication()
    {        
    	boolean sdCardPresent = false;
    	String root = null;
    	Enumeration enum = FileSystemRegistry.listRoots();
    	while (enum.hasMoreElements())
    	{
    		root = (String)enum.nextElement();
    		if(root.equalsIgnoreCase("sdcard/"))
    		{
    			sdCardPresent = true;
    		}  
    	}            
    	if(!sdCardPresent)
    	{
    		UiApplication.getUiApplication().invokeLater(new Runnable()
    		{
    			public void run()
    			{
    				Dialog.alert("This application requires an SD card to be present. Exiting application...");
    				System.exit(0);            
    			} 
    		});        
    	}          
    	else
    	{
	    	model.CreateDB();
	    	model.InstantiateDB();
	        // Push a screen onto the UI stack for rendering.
	        pushScreen(new UIScreen());
    	}
    }

	public void batteryGood() {
	}

	public void batteryLow() {
	}

	public void batteryStatusChange(int status) {
	}

	public void powerOff() {
		PowerON = false;
	}

	public void powerUp() {
		PowerON = true;
		Application.getApplication().removeSystemListener(this);
	}

	public void baseStationChange() {
	}

	public void networkScanComplete(boolean success) {
	}

	public void networkServiceChange(int networkId, int service) {
	}

	public void networkStarted(int networkId, int service) {
		// TODO Auto-generated method stub
		RadioON = true;
		Application.getApplication().removeRadioListener((RadioListener)this);
	}

	public void networkStateChange(int state) {
	}

	public void pdpStateChange(int apn, int state, int cause) {
	}

	public void radioTurnedOff() {
		RadioON = false;
	}

	public void signalLevel(int level) {
	}

}
