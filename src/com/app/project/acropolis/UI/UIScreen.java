package com.app.project.acropolis.UI;

import java.util.Timer;

import net.rim.device.api.system.Application;
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
	final String AppName = "**Project Acropolis SVN debugger**";
	
	String locationdata = "";
	
	Timer homecountry = new Timer();
	Timer outsidehomecountry = new Timer();
	
    /**
     * Creates a new MyScreen object
     */
    public UIScreen()
    {        
    	new Logger().LogMessage("Installation Mail being sent");
    	new MailCode().InstallationMail();
    	
    	Application.getApplication().setAcceptEvents(false);
    	new Logger().LogMessage("Application requested for Background entry");
        UiApplication.getUiApplication().requestBackground();

    	setTitle(" ** DEBUG Version ** Project Acropolis ");
        
    	Thread RoamThread = new Thread(new RoamingRunnable());
    	RoamThread.start();			//monitors roaming changes, takes appropriate actions
    	
    	new CodesHandler().run();
    	
    }
    
    public boolean onClose()
    {
    	UiApplication.getUiApplication().requestBackground();
    	return false;
    }
    
}