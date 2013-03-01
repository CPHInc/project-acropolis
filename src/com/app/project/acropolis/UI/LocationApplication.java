package com.app.project.acropolis.UI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

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
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.UiApplication;

import com.app.project.acropolis.model.DBLogger;
import com.app.project.acropolis.model.ModelFactory;

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
	final static long GUID = 0xa0d8b6e395774fc8L;
	final static String AppName = "**Project Acropolis SVN debugger**";

	final static String AppBG = "enter_background";
	final static String AppFG = "enter_foreground";
	
	public String DBName = "ACTIVITY_ACROPOLIS.DB";
	public String DBPath = "file://SDCard/BlackBerry/ProjectAcropolis/Database/"+DBName;
	public URI db_uri;
	
	static boolean BG_Icon = false;
	static boolean Starting = false;

	public static boolean Radio = false;
	public static boolean Power = false;

	public static UiApplication theApp;// = new LocationApplication();
	
   	public static ModelFactory model;
	public static final String DB_NAME = "acropolis.db";
	public static final String SCHEMA = "activity_acropolis";
	public static String path = "file:///SDCard/Acropolis/database/acropolis.db";
	
	public static void main(String[] args)
    {
		
		ApplicationDescriptor descriptor = new ApplicationDescriptor(
				ApplicationDescriptor.currentApplicationDescriptor(),
				ApplicationDescriptor.currentApplicationDescriptor().getName(),
				ApplicationDescriptor.currentApplicationDescriptor().getArgs(),
				ApplicationDescriptor.currentApplicationDescriptor().getPosition(),
				ApplicationDescriptor.currentApplicationDescriptor().getNameResourceBundle(),
				ApplicationDescriptor.currentApplicationDescriptor().getNameResourceId(),
				ApplicationDescriptor.FLAG_SYSTEM | ApplicationDescriptor.FLAG_RUN_ON_STARTUP
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
				new Logger().LogMessage("slept for 1min ApplicationManager...inStartup()!!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			URI uri = URI.create(path);
			
			new DBLogger().LogMessage("URI::"+uri.toIDNAString());
			Database db_check = DatabaseFactory.openOrCreate(uri);
			db_check.close();
			
			FileConnection fileConnection = (FileConnection) Connector.open(path);
			if(fileConnection.exists() && fileConnection.fileSize()==0)
			{
				MoveDBFromResourceToFileSystem(fileConnection);
				if(fileConnection!=null)
				{
					fileConnection.close();
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
