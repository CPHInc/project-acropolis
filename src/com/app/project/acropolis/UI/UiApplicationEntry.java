package com.app.project.acropolis.UI;

import loggers.Logger;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.UiApplication;

/**

 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * ---RELEASE NOTES---
 * @version 1.0.1

 */

public class UiApplicationEntry extends UiApplication
{
	
	final static String MINIMIZED = "minimize";
	final static String INTERFACE = "gui";
	
	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args)
    {
		if(ApplicationManager.getApplicationManager().inStartup())
		{
			try {
				Thread.sleep(20*1000);
				new Logger().LogMessage("inStartup() 20seconds...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(args!=null && args[0].equals(MINIMIZED) && args.length>0)
		{
			//on start-up and always
			MinimizedApplication theMinApp = new MinimizedApplication();
			theMinApp.setAcceptEvents(false);
			theMinApp.enterEventDispatcher();
		}
		else if(args!=null && args[0].equals(INTERFACE) && args.length>0)
		{
			//from application menu
			new Logger().LogMessage("uiApp GUI");
			UiApplicationEntry theApp = new UiApplicationEntry();
			theApp.setAcceptEvents(true);
			theApp.enterEventDispatcher();
		}
		else
		{
			//from universal search
		}
    }

    /**
     * Creates a new LocationApplication object, checks for SDCard support in the device else exits and creates/opens DataBase
     */
    public UiApplicationEntry()
    {        
		new Logger().LogMessage("Screen pushed");
        pushScreen(new UIScreen());
    }
    
    public boolean shouldAppearInApplicationSwitcher()
    {
    	return true;
    }
    
    public boolean requestClose()
    {
    	return true;
    }
    
}
