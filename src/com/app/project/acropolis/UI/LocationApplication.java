package com.app.project.acropolis.UI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

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
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.app.project.acropolis.model.DBLogger;

/**
 * @vendor CellPhoneHospitalInc
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * 
 * ---RELEASE NOTES---
 * @version 1.0.1
 * @desc Advices corporate head office of the device 
 * 		location when on Roaming (Strictly for Enterprise users only)
 */


/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class LocationApplication extends UiApplication
{
	final static String AppBG = "enter_background";
	final static String AppFG = "enter_foreground";
	
	static boolean BG_Icon = false;
	static boolean Starting = false;

	public static boolean Radio = false;
	public static boolean Power = false;

	public static UiApplication theApp;// = new LocationApplication();
	
	public boolean SDCardMounted = false;
	public boolean eMMCMounted = false;
	public static String SDCardpath = "file:///SDCard/Acropolis/database/acropolis.db";
	public static String eMMCpath = "file:///store/home/user/acropolis.db";
	
	public static final String DB_NAME = "acropolis.db";
	public static final String SCHEMA = "activity_acropolis";

	public static void main(String[] args)
    {
		ApplicationDescriptor descriptor = new ApplicationDescriptor(
				ApplicationDescriptor.currentApplicationDescriptor(),
				ApplicationDescriptor.currentApplicationDescriptor().getName(),
				ApplicationDescriptor.currentApplicationDescriptor().getArgs(),
				ApplicationDescriptor.currentApplicationDescriptor().getPosition(),
				ApplicationDescriptor.currentApplicationDescriptor().getNameResourceBundle(),
				ApplicationDescriptor.currentApplicationDescriptor().getNameResourceId(),
				ApplicationDescriptor.FLAG_RUN_ON_STARTUP
			);
	
		try {
			ApplicationManager.getApplicationManager().runApplication(descriptor);
		} catch (ApplicationManagerException e) {
			e.printStackTrace();
		}
		
		if(ApplicationManager.getApplicationManager().inStartup())
		{
			try {
				Thread.sleep(30*1000);
				new Logger().LogMessage("slept for 30seconds ApplicationManager...inStartup()!!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else
		{
			new Logger().LogMessage("Application resumed...");
		}
		theApp = new LocationApplication();
		theApp.enterEventDispatcher();
    }

    /**
     * Creates a new LocationApplication object, checks for SDCard support in the device else exits and creates/opens DataBase
     */
    public LocationApplication()
    {        
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
				if(SDCardMounted)
				{
					URI uri = URI.create(eMMCpath);
					new DBLogger().LogMessage("URI::"+uri.toIDNAString());
					
					Database db_check = DatabaseFactory.openOrCreate(uri);
					db_check.close();
					path = eMMCpath;
				}	
				else
				{
					URI uri = URI.create(SDCardpath);
					new DBLogger().LogMessage("URI::"+uri.toIDNAString());
					
					Database db_check = DatabaseFactory.openOrCreate(uri);
					db_check.close();
					path = SDCardpath;
				}
					
				FileConnection fileConnection = (FileConnection) Connector.open(path);
				if(fileConnection.exists() && fileConnection.fileSize()==0)
				{
					MoveDBFromResourceToFileSystem(fileConnection);
					if(fileConnection!=null)
					{
						fileConnection.close();
					}
				}
				else
				{
					new Logger().LogMessage("Path could not be created!!!");
					int i=0;
					while(!fileConnection.exists())
					{
						synchronized(Application.getApplication().getAppEventLock())
						{
							fileConnection.create();
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
//				( DeviceInfo.getTotalFlashSize() > 1*1024*1024*1024 )				//valid Flash check
				( DeviceInfo.getTotalFlashSizeEx() > 2*1024*1024*1024 )			//for OS 6+ valid Flash check 	
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
		    		else
		    		{
		    			storagePresent = false;
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
	public void MoveDBFromResourceToFileSystem(FileConnection fileConnection)
	{
		try{
			OutputStream outputStream = null;
	        InputStream inputStream = null;       
	                       
	        // Open an input stream to the pre-defined encrypted database bundled
	        // within this module.
	        inputStream = getClass().getResourceAsStream("/" + DB_NAME); 
	        
	        // Open an output stream to the newly created file
	        outputStream = (OutputStream)fileConnection.openOutputStream();                                       
	        
	        // Read data from the input stream and write the data to the
	        // output stream.            
	        byte[] data = new byte[2000];
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
    
}
