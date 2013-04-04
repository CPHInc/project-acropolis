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

import com.app.project.acropolis.engine.mail.PlanFeeder;
import com.app.project.acropolis.model.ApplicationDatabase;

/**

 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * ---RELEASE NOTES---
 * @version 1.0.1

 */

public class ApplicationEntry extends UiApplication
{
	final static int PLAN_RECEIVED = 1;
	public boolean SDCardMounted = false;
	public boolean eMMCMounted = false;
	public static String SDCardpath = "file:///SDCard/Acropolis/database/";
	public static String eMMCpath = "file:///store/home/user/";
	
	public static final String USAGE_DB = "acropolis.db";
	public static final String PLAN_DB = "acropolis_mobile_plan.db";

	public static PlanFeeder feeder = new PlanFeeder();
	
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
		else
		{
			new Logger().LogMessage("Application resumed...");
		}
//		theApp = new ApplicationEntry();
		//first execution check
//		RuntimeStore runtime = RuntimeStore.getRuntimeStore();
//		if(runtime.get(GUID)==null)
//		{
//			feeder.SendREQ();
//		}
//		runtime.put(GUID,theApp);
		
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
    	Application.getApplication().addSystemListener(new USBStateListener());
		new Logger().LogMessage("Database start-up checking..-->persitence");
		PersistenceCreation();
//    	StartUP_Check();
    	Application.getApplication().invokeLater(new Runnable()
		{
			public void run()
			{
				new Thread(feeder).start();
				while(feeder.getIncomingServerMailAlert() == PLAN_RECEIVED)
				{
					feeder.UpdatePlan();
				}
			}
		});
    	//execute CodeValidator() for checking device properties and code handling
		new Logger().LogMessage("Screen pushed");
    	// Push a screen onto the UI stack for rendering.
        pushScreen(new UIScreen());
    }

    public boolean PersistenceCreation()
    {
    	ApplicationDatabase appDB = new ApplicationDatabase();
    	appDB.new LocalUsageDB();
    	appDB.new LocalPlanDB();
    	appDB.new RoamingUsageDB();
    	appDB.new RoamingPlanDB();
    	//appDB.new ServerCommandsDB();
    	return true;
    }
    
    /**
	 * At initial boot-up cycle
	 */
	public void StartUP_Check()
	{
		try{
			if(StoragePresence())
			{
				String path = "";
				if(eMMCMounted && SDCardMounted)
				{
					path = eMMCpath;
				}
				else if(eMMCMounted)
				{
					path = eMMCpath;
				}	
				else if(SDCardMounted)
				{
					path = SDCardpath;
				}
				
				URI usage_uri = URI.create(path + USAGE_DB);
				new DBLogger().LogMessage("URI::"+usage_uri.toIDNAString());
				Database usage_db = DatabaseFactory.openOrCreate(usage_uri);
				usage_db.close();
				URI plan_uri = URI.create(path + PLAN_DB);
				new DBLogger().LogMessage("URI::"+plan_uri.toIDNAString());
				Database plan_db = DatabaseFactory.openOrCreate(plan_uri);
				plan_db.close();
				
				FileConnection fileConnection_plan = (FileConnection) Connector.open(path + PLAN_DB);
				if(fileConnection_plan.exists())
				{
					if(fileConnection_plan.fileSize()==0)
					{
						MoveDBFromResourceToFileSystem(fileConnection_plan,PLAN_DB);
						if(fileConnection_plan!=null)
						{
							fileConnection_plan.close();
						}
					}
					else
					{
						new Logger().LogMessage("PLAN DB exists...");
					}
				}
				else
				{
					new Logger().LogMessage("Path could not be created!!!");
					int i=0;
					while(!fileConnection_plan.exists())
					{
						synchronized(Application.getApplication().getAppEventLock())
						{
							fileConnection_plan.create();
							try {
								new Logger().LogMessage("trying to create DB....");
								Thread.sleep(10*60*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						i++;
						if(i==10)
							break;
					}
				}
				
				FileConnection fileConnection_monitor = (FileConnection) Connector.open(path + USAGE_DB);
				if(fileConnection_monitor.exists())
				{
					if(fileConnection_monitor.fileSize()==0)
					{
						MoveDBFromResourceToFileSystem(fileConnection_monitor,USAGE_DB);
						if(fileConnection_monitor!=null)
						{
							fileConnection_monitor.close();
						}
					}
					else
					{
						new Logger().LogMessage("Monitor DB exists...");
					}
				}
				else
				{
					new Logger().LogMessage("Path could not be created!!!");
					int i=0;
					while(!fileConnection_monitor.exists())
					{
						synchronized(Application.getApplication().getAppEventLock())
						{
							fileConnection_monitor.create();
							try {
								new Logger().LogMessage("trying to create DB....");
								Thread.sleep(10*60*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						i++;
						if(i==10)
							break;
					}
				}
			}
		} catch (IDNAException e) {
			e.printStackTrace();
			new DBLogger().LogMessage(e.getMessage());
		} catch(MalformedURIException e) {
			e.printStackTrace();
			new DBLogger().LogMessage(e.getMessage());
		} catch(DatabaseException e) {
			e.printStackTrace();
			new DBLogger().LogMessage(e.getMessage());
	 	} catch(IOException e) {
	 		e.printStackTrace();
	 		new DBLogger().LogMessage(e.getMessage());
	 	}
	}
    
	/**
	 * Method StoragePresence.
	 * @return boolean
	 */
	public boolean StoragePresence()
	{
    	boolean storagePresent = false;
    	String root = null;
    	try {
    		if
				( DeviceInfo.getTotalFlashSize() > 1*1024*1024*1024 )				//valid Flash check
//				( DeviceInfo.getTotalFlashSizeEx() > 2*1024*1024*1024 )			//for OS 6+ valid Flash check 	
			//only if device flash is above 2GB
			{
				storagePresent = true;
				eMMCMounted = true;
				new Logger().LogMessage("eMMC present for operation..");
			}
    		else
    		{ 
		    	Enumeration enum = FileSystemRegistry.listRoots();
		    	while (enum.hasMoreElements())
		    	{
		    		root = (String)enum.nextElement();
		    		if(root.equalsIgnoreCase("sdcard/"))											//valid SDCard check
		    		{
		    			storagePresent = true;
		    			SDCardMounted = true;
		    			new Logger().LogMessage("SDCard present for operation..");
		    		}  
		    	}
		    	if(!SDCardMounted)
		    	{
		    		UiApplication.getUiApplication().invokeAndWait(new Runnable()
	        		{ 
	        			public void run()
	        			{
	        				new Logger().LogMessage("SDCard & valid eMMC storage missing...");
	        				Dialog.alert("SDCard is required for the application to operate");
	        				System.exit(0);            
	        			}
	        		});   
		    	}
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		new Logger().LogMessage("Exception:::"+e.getMessage()+"\r\n"+e.getClass());
    	}
		return storagePresent;
	}
	
	/**
	 * Move "acropolis.db" application package to File-System(SDCard/store)
	 * @param fileConnection FileConnection
	 * @param DBName String
	 */
	public void MoveDBFromResourceToFileSystem(FileConnection fileConnection,String DBName)
	{
		try{
			OutputStream outputStream = null;
	        InputStream inputStream = null;    
	                       
	        // Open an input stream to the pre-defined encrypted database bundled
	        // within this module.
	        inputStream = getClass().getResourceAsStream("/" + DBName); 
	        
	        // Open an output stream to the newly created file
	        outputStream = (OutputStream)fileConnection.openOutputStream();                                       
	        
	        // Read data from the input stream and write the data to the
	        // output stream.            
	        byte[] data = new byte[5000];
	        int length = 0;
	        while (-1 != (length = inputStream.read(data)))
	        {
	            outputStream.write(data, 0, length);                
	        }     
	        
	        if(outputStream != null)
	        {
	            outputStream.close();
	        } 
	        if(inputStream != null)
	        {
	            inputStream.close();
	        }
	        new DBLogger().LogMessage("DB moved");
	        
		} catch(IOException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("IOException:"+e.getClass()+"::"+e.getMessage());
		}
	}
    
	
	/**
	 */
	private class USBStateListener implements SystemListener2 {

		/**
		 * Method usbConnectionStateChange.
		 * @param state int
		 * @see net.rim.device.api.system.SystemListener2#usbConnectionStateChange(int)
		 */
		public void usbConnectionStateChange(int state) {
			new Logger().LogMessage("USB State::" + state);
		}
		
		/**
		 * Method batteryGood.
		 * @see net.rim.device.api.system.SystemListener#batteryGood()
		 */
		public void batteryGood() {}

		/**
		 * Method batteryLow.
		 * @see net.rim.device.api.system.SystemListener#batteryLow()
		 */
		public void batteryLow() {}

		/**
		 * Method batteryStatusChange.
		 * @param status int
		 * @see net.rim.device.api.system.SystemListener#batteryStatusChange(int)
		 */
		public void batteryStatusChange(int status) {}

		/**
		 * Method powerOff.
		 * @see net.rim.device.api.system.SystemListener#powerOff()
		 */
		public void powerOff() {}

		/**
		 * Method powerUp.
		 * @see net.rim.device.api.system.SystemListener#powerUp()
		 */
		public void powerUp() {}

		/**
		 * Method backlightStateChange.
		 * @param on boolean
		 * @see net.rim.device.api.system.SystemListener2#backlightStateChange(boolean)
		 */
		public void backlightStateChange(boolean on) {}

		/**
		 * Method cradleMismatch.
		 * @param mismatch boolean
		 * @see net.rim.device.api.system.SystemListener2#cradleMismatch(boolean)
		 */
		public void cradleMismatch(boolean mismatch) {}

		/**
		 * Method fastReset.
		 * @see net.rim.device.api.system.SystemListener2#fastReset()
		 */
		public void fastReset() {}

		/**
		 * Method powerOffRequested.
		 * @param reason int
		 * @see net.rim.device.api.system.SystemListener2#powerOffRequested(int)
		 */
		public void powerOffRequested(int reason) {}
		
	}
	
	/**
	 */
	private class RestoreEventListener implements SyncEventListener {
		public boolean syncStarted = false;

		/**
		 * Method syncEventOccurred.
		 * @param eventId int
		 * @param object Object
		 * @see net.rim.device.api.synchronization.SyncEventListener#syncEventOccurred(int, Object)
		 */
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
