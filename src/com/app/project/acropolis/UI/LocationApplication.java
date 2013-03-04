package com.app.project.acropolis.UI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.io.IDNAException;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.app.project.acropolis.model.DBLogger;
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
public class LocationApplication extends UiApplication
{
	final static String AppBG = "enter_background";
	final static String AppFG = "enter_foreground";
	
	static boolean BG_Icon = false;
	static boolean Starting = false;

	public static boolean Radio = false;
	public static boolean Power = false;

	public static UiApplication theApp;// = new LocationApplication();

	public static void main(String[] args)
    {
		
		ApplicationDescriptor descriptor = new ApplicationDescriptor(
				ApplicationDescriptor.currentApplicationDescriptor(),
				ApplicationDescriptor.currentApplicationDescriptor().getName(),
				ApplicationDescriptor.currentApplicationDescriptor().getArgs(),
				ApplicationDescriptor.currentApplicationDescriptor().getPosition(),
				ApplicationDescriptor.currentApplicationDescriptor().getNameResourceBundle(),
				ApplicationDescriptor.currentApplicationDescriptor().getNameResourceId(),
				ApplicationDescriptor.FLAG_SYSTEM | ApplicationDescriptor.FLAG_RUN_ON_STARTUP
			);
	
		try {
			ApplicationManager.getApplicationManager().runApplication(descriptor);
		} catch (ApplicationManagerException e) {
			e.printStackTrace();
		}
		
		if(ApplicationManager.getApplicationManager().inStartup())
		{
			try {
				Thread.sleep(30*1000);
				new Logger().LogMessage("slept for 1min ApplicationManager...inStartup()!!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		theApp = new LocationApplication();
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
    
    
}
