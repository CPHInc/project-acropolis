package com.app.project.acropolis.UI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Timer;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.io.IDNAException;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.app.project.acropolis.model.DBLogger;
import com.app.project.acropolis.model.ModelFactory;

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
		new Timer().schedule(new DataMonitor(), 10*60*1000);			//keep listening every 10 minutes
		
		new Logger().LogMessage("Active Roaming Engine ON");
		Thread RoamThread = new Thread(new RoamingRunnable());
    	RoamThread.start();			//monitors roaming changes, takes appropriate actions
		new CodesHandler();
	}
	
}
