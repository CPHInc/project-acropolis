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
	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args)
	{
		if(ApplicationManager.getApplicationManager().inStartup())
		{
			try {
				Thread.sleep(45*1000);
				new Logger().LogMessage("inStartup() 45seconds...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(args!=null&&args[0].equals("gui")&&args.length>0)
		{
			UiApplicationEntry theApp = new UiApplicationEntry();
			theApp.setAcceptEvents(true);
			theApp.enterEventDispatcher();
		}
		else
		{
			MinimizedEntry theMin = new MinimizedEntry();
			theMin.setAcceptEvents(false);
			theMin.enterEventDispatcher();
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
}