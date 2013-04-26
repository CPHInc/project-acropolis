package com.app.project.acropolis.controller;

import loggers.Logger;

import com.app.project.acropolis.engine.monitor.LocationCode;
import com.app.project.acropolis.model.ApplicationDB;

import net.rim.device.api.system.RadioStatusListener;

public class RadioStateListener implements RadioStatusListener 
{
	static boolean InNeed = false;
	
	public void baseStationChange() {
		// TODO Auto-generated method stub
		
	}

	public void networkScanComplete(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	public void networkServiceChange(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void networkStarted(int arg0, int arg1)
	{
		if(InNeed)
		{
			if(!LocationCode.Check_NON_CAN_Operator())
				new LocalHandler().CollectedData();
			else
				new RoamingHandler().CollectedData();
			ApplicationDB.reset();
			new Logger().LogMessage("Bill date reset");
		}
	}

	public void networkStateChange(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public void pdpStateChange(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	public void radioTurnedOff() {
		// TODO Auto-generated method stub
		
	}

	public void signalLevel(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public static void IsItInNeed(boolean need)
	{
		if(need)
		{
			InNeed = true;
		}
		else
		{
			InNeed = false;
		}
	}
	
}