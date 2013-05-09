package com.app.project.acropolis.controller;

import net.rim.device.api.system.RealtimeClockListener;

import com.app.project.acropolis.engine.monitor.LocationCode;

public class ClockListener implements RealtimeClockListener 
{

	boolean wasRoaming = false;
	
	public void clockUpdated() {
		if(LocationCode.Check_NON_CAN_Operator())
		{
			//entered roaming
			new ServerChannel();
			wasRoaming = true;
		}
		else
		{
			if(wasRoaming)
			{
				new ServerChannel();
				wasRoaming = false;//flashed
			}
		}
	}
	
}
