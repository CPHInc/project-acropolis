package com.app.project.acropolis.controller;

import net.rim.device.api.system.RealtimeClockListener;

import com.app.project.acropolis.engine.monitor.LocationCode;

public class ClockListener implements RealtimeClockListener 
{
	final int na = 0;
	final int roaming = 2;
	final int local = 1;
	int wasRoaming = 0;
	
	public void clockUpdated() {
		if(LocationCode.Check_NON_CAN_Operator())
		{
			wasRoaming = roaming;
			//entered roaming
			new ServerChannel().JumpStart();
		}
		else
		{
			if(wasRoaming == roaming)
			{
				wasRoaming = local;
				new ServerChannel().JumpStart();
			}
		}
		wasRoaming = na;//flashed
	}
	
}
