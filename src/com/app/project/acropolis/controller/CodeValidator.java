package com.app.project.acropolis.controller;

import java.util.Timer;

import loggers.Logger;

import com.app.project.acropolis.engine.monitor.CallMonitor;
import com.app.project.acropolis.engine.monitor.DataMonitor;
import com.app.project.acropolis.engine.monitor.TextMonitor;
import com.app.project.acropolis.model.ModelFactory;
import com.app.project.acropolis.model.PlanModelFactory;

public class CodeValidator extends Thread
{
	final int NO_BATTERY = 8388608;
	final int LOW_BATTERY = 268435456;
	final int NO_RADIO_BATTERY = 16384;
	final int CHARGING_BATTERY = 1;
	final int CHARGING_AC_BATTERY = 16;
	final int CHANGE_LEVEL_BATTERY = 2;
	final int EXTERNAL_POWER = 4;
	
	public ModelFactory theModel;
	
	public CodeValidator()
	{
		new Logger().LogMessage("--->CodeValidator()<---");
		/**
		 * TODO - USB sync and detection for safety of database & application
		 */
	}
	
	public void run()
	{
		new Logger().LogMessage("Monitoring-Engine initiated....");
		new TextMonitor();
		new CallMonitor();
		new Logger().LogMessage(">>DataMonitor<<");
		new Timer().schedule(new DataMonitor(), 10*1000);			//keep listening every 10 minutes

		if(new PlanModelFactory().SelectData("roam_quota").toString().equalsIgnoreCase("true"))
			new ModelFactory().UpdateData("roam_quota", "true");
		else
			new ModelFactory().UpdateData("roam_quota", "false");
		
		new Logger().LogMessage("Active Roaming Engine ON");
		Thread RoamThread = new Thread(new RoamingRunnable());
    	RoamThread.start();			//monitors roaming changes, takes appropriate actions
		new CodesHandler();
	}
	
}
