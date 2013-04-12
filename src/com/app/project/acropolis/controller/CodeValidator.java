package com.app.project.acropolis.controller;

import java.util.Timer;

import loggers.Logger;

import com.app.project.acropolis.engine.monitor.CallMonitor;
import com.app.project.acropolis.engine.monitor.DataMonitor;
import com.app.project.acropolis.engine.monitor.LocationCode;
import com.app.project.acropolis.engine.monitor.TextMonitor;

/**
 * All the Engines, Handlers, Runnable are passed and verified
 * if all true then executed
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class CodeValidator implements Runnable
{
	final int NO_BATTERY = 8388608;
	final int LOW_BATTERY = 268435456;
	final int NO_RADIO_BATTERY = 16384;
	final int CHARGING_BATTERY = 1;
	final int CHARGING_AC_BATTERY = 16;
	final int CHANGE_LEVEL_BATTERY = 2;
	final int EXTERNAL_POWER = 4;
	
	public CodeValidator()
	{
		new Logger().LogMessage("--->CodeValidator()<---");
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		new Logger().LogMessage("Remote Control initiated..");
		new Logger().LogMessage("Monitoring-Engine initiated....");
		new Thread(new LocationCode()).start();
		new Thread(new CallMonitor()).start();		//for application permission first run
		new TextMonitor().run();
		new Timer().schedule(new DataMonitor(),60*1000);
		new Logger().LogMessage("Positioning Engine enqueued");
		new RoamingHandler().run();
		new LocalHandler().run();
	}
}
