package com.app.project.acropolis.UI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import loggers.DBLogger;
import loggers.Logger;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.io.IDNAException;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.app.project.acropolis.controller.CodeValidator;
import com.app.project.acropolis.controller.GlobalActionListener;
import com.app.project.acropolis.engine.mail.HoledCeiling;
import com.app.project.acropolis.model.ApplicationDB;

/**

 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * ---RELEASE NOTES---
 * @version 1.0.1

 */

public class ApplicationEntry extends UiApplication
{
	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args)
    {
		if(ApplicationManager.getApplicationManager().inStartup())
		{
			try {
				Thread.sleep(45*1000);
				new Logger().LogMessage("inStartup() 45seconds...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ApplicationEntry theApp = new ApplicationEntry();
		theApp.enterEventDispatcher();
    }

    /**
     * Creates a new LocationApplication object, checks for SDCard support in the device else exits and creates/opens DataBase
     */
    public ApplicationEntry()
    {        
    	new Logger().LogMessage("SyncEventListener registered");
    	SyncManager.getInstance().addSyncEventListener(new RestoreEventListener());
    	InboxScanner();
    	Application.getApplication().addGlobalEventListener(new GlobalActionListener());
		new Logger().LogMessage("Database start-up checking..-->persitence");
		PersistenceCreation();
		new CodeValidator().run();
		new Logger().LogMessage("Screen pushed");
        pushScreen(new UIScreen());
    }
    
    public boolean InboxScanner()
    {
    	Session.getDefaultInstance().getStore().addFolderListener(new HoledCeiling());
    	return true;
    }
    
    /**
     * Method PersistenceCreation.
     * @return boolean
     */
    public boolean PersistenceCreation()
    {
    	ApplicationDB.setValue(Phone.getDevicePhoneNumber(true),ApplicationDB.PhoneNumber);
    	new Logger().LogMessage(ApplicationDB.getValue(ApplicationDB.PhoneNumber));
    	return true;
    }
    
	/**
	 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
	 * @version $Revision: 1.0 $
	 */
	private class RestoreEventListener implements SyncEventListener {
		public boolean syncStarted = false;

		/**
		 * Method syncEventOccurred.
		 * @param eventId int
		 * @param object Object
		
		 * @see net.rim.device.api.synchronization.SyncEventListener#syncEventOccurred(int, Object) */
		public void syncEventOccurred(int eventId, Object object) {
			if (eventId == SyncEventListener.SERIAL_SYNC_STOPPED || 
					eventId == SyncEventListener.OTA_SYNC_TRANSACTION_STOPPED) 
			{
				new Logger().LogMessage("Sync Stopped");
				setSyncStopped();
			}
			else if (eventId == SyncEventListener.SERIAL_SYNC_STARTED ||
					eventId == SyncEventListener.OTA_SYNC_TRANSACTION_STARTED) 
			{
				new Logger().LogMessage("Sync Started");
				setSyncStarted();
			}
			waitOnSyncEnd();
		}

		private synchronized void setSyncStarted() {
			syncStarted = true;
			notifyAll();
		}

		private synchronized void setSyncStopped() {
			notifyAll();
		}

		public synchronized void waitOnSyncEnd() {
			boolean waitForEnd = false;
			if (!SyncManager.getInstance().isSerialSyncInProgress()) {
				new Logger().LogMessage("Not Currently restoring, wait for 3min to see if one starts");
				try {
					wait(180000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (syncStarted) 
				{
					new Logger().LogMessage("A Sync Event has started, wait for completion");
					waitForEnd = true;
				} 
				else
				{
					new Logger().LogMessage("No Sync started");
					waitForEnd = false;
				}
			} else {
				new Logger().LogMessage("Currently restoring, wait for completion");
				waitForEnd = true;
			}
			if (waitForEnd) {
				new Logger().LogMessage("Waiting on Sync End");
				try {
					wait(600000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}
	
}
