package com.app.project.acropolis.UI;

import net.rim.device.api.system.RadioInfo;

public class RoamingRunnable implements Runnable{

	boolean isRoaming = false;
	
	public void run() {
		isRoaming = ( (RadioInfo.getState() & RadioInfo.NETWORK_SERVICE_ROAMING) != 0 );
	}
	
	public boolean getIsRoaming()
	{
		return isRoaming;
	}
	
}
