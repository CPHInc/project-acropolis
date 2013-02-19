package com.app.project.acropolis.UI;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.UiApplication;

public class ApplicationListeners implements Runnable 
{
	final long GUID = 0x2362d8d8f5013171L;
	final String AppName = "Project Acropolis SVN Debugger";
	
	public boolean Radio = false;
	public boolean Power = false;
	
	Thread codeThread = new Thread(new CodesHandler());
	public static ApplicationListeners listen = new ApplicationListeners();
	public ApplicationListeners()
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
//		Application.getApplication().addRadioListener((RadioListener)this);		
	}
	
	public void run()
	{
	}

}