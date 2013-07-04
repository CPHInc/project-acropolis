package com.app.project.acropolis.UI;

import java.util.Calendar;
import java.util.TimeZone;

import loggers.Logger;
import net.rim.blackberry.api.mail.Session;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;

import com.app.project.acropolis.controller.CodeValidator;
import com.app.project.acropolis.controller.ServerChannel;
import com.app.project.acropolis.engine.mail.HoledCeiling;
import com.app.project.acropolis.model.ApplicationDB;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * ---RELEASE NOTES---
 * @version 1.0.1
 */

public class ApplicationEntry
{
	final static String GUI = "gui";
	final static String Global = "global";

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args)
	{
		while( ApplicationManager.getApplicationManager().inStartup() )
		{
			try {
				if(RadioInfo.getCurrentNetworkName()!=null)
					break;
				else
					Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(args!=null&&args[0].equals(GUI)&&args.length>0)
		{
			GUIApplication theApp = new GUIApplication();
			theApp.setAcceptEvents(true);
			theApp.enterEventDispatcher();
		}
		if(args!=null&&args[0].equals(Global)&&args.length>0)
		{
			GlobalAction theAction = new GlobalAction();
			theAction.enterEventDispatcher();
		}
		MinimizedApplication theMin = new MinimizedApplication();
		theMin.setAcceptEvents(false);
		theMin.enterEventDispatcher();
	}
}

final class GlobalAction extends Application implements GlobalEventListener
{
	//com.app.project.acropolis.controller.ServerChannel.JUMPSTARTENGINE
	final long JumpStartEngine_GUID = 0x5325bd4e7cbdf34bL;
	//com.app.project.acropolis.engine.mail.HoledCeiling.REQ
	final long Request_GUID = 0x1a63da98018f9e28L;
	final long DateChange_GUID = net.rim.device.api.util.DateTimeUtilities.GUID_DATE_CHANGED;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd");
	private String dbBillDate = "";
	private int jumpstart_counter = 0;
	
	public GlobalAction()
	{
		Application.getApplication().addGlobalEventListener(this);
	}

	public void eventOccurred(long guid, int data0, int data1, Object object0,
			Object object1) {
		
		if(guid == JumpStartEngine_GUID)
		{
			jumpstart_counter++;
			Application.getApplication().invokeLater(new Runnable() {
				public void run()
				{
					new Logger().LogMessage("Jump starting engine boost supplied!!! ::" + jumpstart_counter);
					new ServerChannel().JumpStart();
				}
			});
		}
		
		if(guid == Request_GUID)
		{
			Application.getApplication().invokeLater(new Runnable() {
				public void run()
				{
					new ServerChannel();
				}
			});
		}
		if(guid == DateChange_GUID)
		{
			new Logger().LogMessage("Date Chane\r\nGUID::"+guid);
			TimeZone timezone = TimeZone.getDefault();
			String gmtTimeStamp = sdf.format( Calendar.getInstance(timezone).getTime()); 	//GMT time for server
			dbBillDate = ApplicationDB.getValue(ApplicationDB.BillDate);
//			dbBillDate = dbBillDate.substring(dbBillDate.length()-2, dbBillDate.length()); 
			if(gmtTimeStamp.equalsIgnoreCase(dbBillDate))
			{
				Application.getApplication().invokeLater(new Runnable() {
					public void run()
					{
						while((RadioInfo.getActiveWAFs()&RadioInfo.WAF_3GPP) ==0)
						{}//wait for Radio to turn ON
						new ServerChannel();
						ApplicationDB.reset();
					}
				});
			}
		}
	}

	public boolean shouldAppearInApplicationSwitcher()
	{
		return false;
	}

}

final class GUIApplication extends UiApplication
{
//	static GUIApplication gui;
	/**
	 * Creates a new LocationApplication object, checks for SDCard support in the device else exits and creates/opens DataBase
	 */
	public GUIApplication()
	{        
		new Logger().LogMessage("Screen pushed");
		pushScreen(new UIScreen());
	}

	public boolean shouldAppearInApplicationSwitcher()
	{
		return true;
	}

}

final class MinimizedApplication extends Application
{
//	static MinimizedApplication _instance;
//	boolean _initialize = false;

	public MinimizedApplication()
	{
		new Logger().LogMessage("Engines ON");
		//SyncManager.getInstance().addSyncEventListener(new RestoreEventListener());
		InboxScanner();
		PersistenceCreation();
//		this.addRealtimeClockListener(new ClockListener());
		new Thread(new CodeValidator()).start();
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
		return ApplicationDB.isEmpty();
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
