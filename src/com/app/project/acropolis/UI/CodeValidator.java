package com.app.project.acropolis.UI;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.SystemListener2;

public class CodeValidator implements SystemListener2,RadioStatusListener
{

	final int NO_BATTERY = 8388608;
	final int LOW_BATTERY = 268435456;
	final int NO_RADIO_BATTERY = 16384;
	final int CHARGING_BATTERY = 1;
	final int CHARGING_AC_BATTERY = 16;
	final int CHANGE_LEVEL_BATTERY = 2;
	final int EXTERNAL_POWER = 4;
	
	CodesHandler codehandler = new CodesHandler(); 
	
	public CodeValidator()
	{
		new Logger().LogMessage("--->CodeValidator()<---");
		codehandler.run();
//		new Logger().LogMessage("Listeners registered");
//		Application.getApplication().addSystemListener((SystemListener)this);
//		Application.getApplication().addRadioListener((RadioListener)this);
	}
	
	/** Battery Validations **/
	public void batteryStatusChange(int status) {
		new Logger().LogMessage("Battery status changed:"+status +
				" level @"+DeviceInfo.getBatteryLevel()+"%");
		
		switch(status)
		{
		
			case LOW_BATTERY:
			{
				new Logger().LogMessage("Battery low level @"+DeviceInfo.getBatteryLevel()+"%");
				if(DeviceInfo.getBatteryLevel() < 20)
				{
//					synchronized( Application.getEventLock() )
//					{
//						try {
//							codehandler.wait();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
				}
			};	
			
			case ((CHARGING_BATTERY|CHARGING_AC_BATTERY)):
			{
				new Logger().LogMessage("Battery charging");
				if(DeviceInfo.getBatteryLevel() > 20)
				{
//					synchronized( Application.getEventLock() )
//					{
//						codehandler.notify();
//					}
				}
			};
			
		}
	}

	/** Power Validations **/
	public void fastReset() 
	{									//exit application on battery pull or fast-reset command
		System.exit(0);
	}
	
	/**
	 * APPLICATION
	 * NOT DEPENDENT
	 */
	public void radioTurnedOff() 
	{
		new Logger().LogMessage("Radio turned off");
	}
	
	public void batteryGood() 
	{
		new Logger().LogMessage("Battery good @"+DeviceInfo.getBatteryLevel()+"%");
		
	}

	public void batteryLow() 
	{
		new Logger().LogMessage("Battery low @"+DeviceInfo.getBatteryLevel()+"%");
	}
	
	public void powerOffRequested(int reason) 
	{}
	
	public void powerOff() 
	{}

	public void powerUp() 
	{}
	
	public void networkStarted(int networkId, int service) {}
	
	public void baseStationChange() {}

	public void networkScanComplete(boolean success) {}

	public void networkServiceChange(int networkId, int service) {}

	public void networkStateChange(int state) {}

	public void pdpStateChange(int apn, int state, int cause) {}

	public void signalLevel(int level) {}

	public void backlightStateChange(boolean on) {}

	public void cradleMismatch(boolean mismatch) {}

	/**
	 * TODO- look for USB SYNC options
	 */
	public void usbConnectionStateChange(int state) {}
	
	
	
}
