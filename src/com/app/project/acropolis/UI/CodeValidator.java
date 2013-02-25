package com.app.project.acropolis.UI;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.SystemListener2;

public class CodeValidator
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
		
		new CodesHandler();
	}
	
}
