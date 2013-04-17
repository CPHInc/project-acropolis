package com.app.project.acropolis.UI;

import loggers.Logger;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.Application;

import com.app.project.acropolis.controller.CodeValidator;
import com.app.project.acropolis.controller.GlobalActionListener;
import com.app.project.acropolis.engine.mail.HoledCeiling;
import com.app.project.acropolis.model.ApplicationDB;

public class MinimizedEntry extends Application 
{

	public MinimizedEntry()
	{
		new Logger().LogMessage("SyncEventListener registered");
    	SyncManager.getInstance().addSyncEventListener(new RestoreEventListener());
    	InboxScanner();
    	Application.getApplication().addGlobalEventListener(new GlobalActionListener());
		new Logger().LogMessage("Database start-up checking..-->persitence");
		PersistenceCreation();
		new CodeValidator().run();
	}
	
	public boolean shouldAppearInApplicationSwitcher()
	{
		return false;
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
