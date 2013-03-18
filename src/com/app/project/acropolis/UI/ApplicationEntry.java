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

/**
 * @vendor CellPhoneHospitalInc
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * ---RELEASE NOTES---
 * @version 1.0.1
 * @desc Advices corporate head office of the device 
 * 		location when on Roaming (Strictly for Enterprise users only)
 */

public class ApplicationEntry extends UiApplication
{
	final static long GUID = 0x5c4288d815f58838L;
	
	public boolean SDCardMounted = false;
	public boolean eMMCMounted = false;
	public static String SDCardpath = "file:///SDCard/Acropolis/database/";
	public static String eMMCpath = "file:///store/home/user/";
	
	public static final String USAGE_DB = "acropolis.db";
	public static final String PLAN_DB = "acropolis_mobile_plan.db";

	public static PlanFeeder feeder = new PlanFeeder();
	
	public static void main(String[] args)
    {
		if(ApplicationManager.getApplicationManager().inStartup())
		{
			try {
				Thread.sleep(30*1000);
				new Logger().LogMessage("slept for 30seconds...inStartup()!!");
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
		
		new Thread(feeder).start();
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
		new Logger().LogMessage("Database start-up checking..");
    	StartUP_Check();
		new Logger().LogMessage("Screen pushed");
    	// Push a screen onto the UI stack for rendering.
        pushScreen(new UIScreen());
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
    
	
	private class USBStateListener implements SystemListener2 {

		public void usbConnectionStateChange(int state) {
			new Logger().LogMessage("USB State::" + state);
		}
		
		public void batteryGood() {}

		public void batteryLow() {}

		public void batteryStatusChange(int status) {}

		public void powerOff() {}

		public void powerUp() {}

		public void backlightStateChange(boolean on) {}

		public void cradleMismatch(boolean mismatch) {}

		public void fastReset() {}

		public void powerOffRequested(int reason) {}
		
	}
	
	private class RestoreEventListener implements SyncEventListener {
		public boolean syncStarted = false;

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
