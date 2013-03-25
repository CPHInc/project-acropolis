package com.app.project.acropolis.controller;

import java.util.Timer;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;

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
	public PlanModelFactory thePlan;
	
	public CodeValidator()
	{
		new Logger().LogMessage("--->CodeValidator()<---");
		/**
		 * TODO - USB MTP/MS detection for safety of database & application
		 * In process
		 * 
		 */
	}
	
	public void run()
	{
		new Thread(new RemoteControl()).start();
		new Logger().LogMessage("Remote Control initiated..");
		new Logger().LogMessage("Monitoring-Engine initiated....");
		new TextMonitor();
		new CallMonitor();
		Application.getApplication().invokeLater(new DataMonitor(), 60*1000 , true);
//		new Timer().schedule(new DataMonitor(), 10*1000);			//keep listening every 10 minutes
		theModel = new ModelFactory();
		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//		thePlan = new PlanModelFactory();
//		if(thePlan.SelectData("roam_quota").toString().equalsIgnoreCase("true"))
//			theModel.UpdateData("roam_quota", "true");
//		else if(thePlan.SelectData("roam_quota").toString().equalsIgnoreCase("false"))
//			theModel.UpdateData("roam_quota", "false");
		
		new Logger().LogMessage("Active Roaming Engine ON");
		
		new CodesHandler().run();
		new RoamingRunnable().run();
	}
	
	public boolean Check_NON_CAN_Operator()
	{
		boolean NON_CANOperatorCheck = true;
   	
		final String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
		    	
		String CurrentNetworkName = "";
		    	
		CurrentNetworkName = RadioInfo.getCurrentNetworkName();
		
		if( CurrentNetworkName.equalsIgnoreCase(CanadianOperators[0]) 
		  			|| CurrentNetworkName.equalsIgnoreCase(CanadianOperators[1])
		   			||CurrentNetworkName.equalsIgnoreCase(CanadianOperators[2]) )
			NON_CANOperatorCheck = false;				//if Current Operator is CANADIAN then **FALSE**
		else
			NON_CANOperatorCheck = true;				//if Current Operator is not CANADIAN then **TRUE** hence ROAMING
		    	
		return NON_CANOperatorCheck;
	 }
	
	public boolean RoamingCheck()
	{
		if((RadioInfo.getNetworkService() & RadioInfo.NETWORK_SERVICE_ROAMING)!=0)
			return true;
		else
			return false;
	}
}
