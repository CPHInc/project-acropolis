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

public class CodeValidator
{
	public static final String DB_NAME = "acropolis.db";
	public static final String SCHEMA = "activity_acropolis";
//	public static String path = "file:///SDCard/Acropolis/database/acropolis.db";
	public static String eMMCpath = "file:///store/home/user/Acropolis/Database/acropolis.db";
	
	
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
		
    	new Logger().LogMessage("Database start-up checking..");
    	StartUP_Check();
		
		new Logger().LogMessage("Monitoring-Engine initiated....");
		new TextMonitor();
		new CallMonitor();
		new Logger().LogMessage(">>DataMonitor<<");
		new Timer().schedule(new DataMonitor(), 10*60*1000);			//keep listening every 10 minutes
		theModel = new ModelFactory();
		
		new CodesHandler();
		
		/**
		 * TODO - USB sync and detection for safety of database & application
		 */
		
	}
	
	
	/**
	 * At initial boot-up cycle
	 */
	public void StartUP_Check()
	{
		try{
			if(SDCardPresence())
			{
//				URI uri = URI.create(path);
				URI uri = URI.create(eMMCpath);
				new DBLogger().LogMessage("URI::"+uri.toIDNAString());
				
				Database db_check = DatabaseFactory.openOrCreate(uri);
				db_check.close();
				
				FileConnection fileConnection = (FileConnection) Connector.open(eMMCpath);
				if(fileConnection.exists() && fileConnection.fileSize()==0)
				{
					MoveDBFromResourceToFileSystem(fileConnection);
					if(fileConnection!=null)
					{
						fileConnection.close();
					}
				}
				else if(!fileConnection.exists())
				{
					new Logger().LogMessage("Path could not be created!!!");
					int i=0;
					while(!fileConnection.exists())
					{
						synchronized(Application.getApplication().getAppEventLock())
						{
							try {
								new Thread().wait(10*60*1000);
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
    
	public boolean SDCardPresence()
	{
    	boolean sdCardPresent = false;
    	String root = null;
    	
    	Enumeration enum = FileSystemRegistry.listRoots();
    	while (enum.hasMoreElements())
    	{
    		root = (String)enum.nextElement();
    		if(root.equalsIgnoreCase("sdcard/"))											//SDCard presence check
    		{
    			sdCardPresent = true;
    		}  
    	}            
    	if(!sdCardPresent)
    	{
    		UiApplication.getUiApplication().invokeLater(new Runnable()
    		{
    			public void run()
    			{
    				new Logger().LogMessage("SDCard absent");
    				Dialog.alert("This application requires an SD card to be present. Exiting application...");
    				System.exit(0);            
    			} 
    		});        
    	}  
    	else
    	{
    		new Logger().LogMessage("SDCard present");
    	}
		return sdCardPresent;
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
