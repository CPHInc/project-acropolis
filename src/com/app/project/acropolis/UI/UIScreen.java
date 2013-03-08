package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

import com.app.project.acropolis.model.ModelFactory;

/**
 * 
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *
 */

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class UIScreen extends MainScreen
{
	final long GUID = 0xde15415aec6cfa55L;
	final String AppName = "**Project Acropolis SVN debugger**";
	
	String locationdata = "";
	
	Timer homecountry = new Timer();
	Timer outsidehomecountry = new Timer();
	
	ModelFactory theModel = new ModelFactory();
	Thread validatorThread = new CodeValidator();
	AppMap map = new AppMap();
	int Device_Orientation = 0;
	final int ZOOM_MIN = 3;					//supported MAX = 15 && MIN = 0
	final int ZOOM_MAX = 5;
 	double Latitude= 43.641696;
	double Longitude= -79.628044;
	
	String waitText = "waiting..";
	String RoamingString = "Roaming :-- ";
	String FixString = "Fix acquired -- ";
	String IncomingString = "Incoming Usage (Mins) :-- ";
	String OutgoingString = "Outgoing Usage (Mins) :-- ";
	String TotalMinString = "Total Usage (Mins) :---- ";
	String ReceivedString = "Received Messages :-- ";
	String SentString = "Sent Messages :-- ";
	String TotalMsgString = "Total Messages :---- ";
	String DownloadString = "Downloaded(KB) :-- ";
	String UploadString = "Uploaded(KB) :--";
	String TotalDataString = "Total Data(KB) :---- ";
	
	RichTextField phonenumberText = new RichTextField("Phone Number :: " + Phone.getDevicePhoneNumber(true) );
	RichTextField roamText = new RichTextField(RoamingString,Field.FOCUSABLE|Field.FIELD_LEFT);
	RichTextField FixAckText = new RichTextField(FixString,Field.NON_FOCUSABLE|Field.FIELD_LEFT); 
	
	RichTextField UsageDetails = new RichTextField("-------Monitored Usage-------",Field.FIELD_HCENTER);
	RichTextField MinutesMonitor = new RichTextField("Minutes usage");
	RichTextField IncomingUsage = new RichTextField(IncomingString);
	RichTextField OutgoingUsage = new RichTextField(OutgoingString);
	RichTextField TotalMinsUsage = new RichTextField("Total Minutes(Mins) :---- ",Field.FOCUSABLE);
	
	RichTextField MessagingMonitor = new RichTextField("Messaging usage");
	RichTextField ReceivedMsgUsage = new RichTextField(ReceivedString);
	RichTextField SentMsgUsage = new RichTextField(SentString);
	RichTextField TotalMsgUsage = new RichTextField(TotalMsgString,Field.FOCUSABLE);

	RichTextField DataMonitor = new RichTextField("Data usage");
	RichTextField DownloadUsage = new RichTextField(DownloadString);
	RichTextField UploadUsage = new RichTextField(UploadString);
	RichTextField TotalDataUsage = new RichTextField(TotalDataString,Field.FOCUSABLE);
	
    /**
     * Creates a new MyScreen object
     */
    public UIScreen()
    {        
    	super(MainScreen.VERTICAL_SCROLLBAR|MainScreen.HORIZONTAL_SCROLLBAR|MainScreen.USE_ALL_WIDTH);
    	
    	Application.getApplication().setAcceptEvents(true);
    	new Logger().LogMessage("Application requested for Foreground entry");
        UiApplication.getUiApplication().requestForeground();

    	setTitle(" ** DEBUG Version ** Project Acropolis ");		//if required
        
    	//execute CodeValidator() for checking device properties and code handling
    	
    	validatorThread.start();
    	
    	theModel.UpdateData("phone_number", Phone.getDevicePhoneNumber(true));
    	
		add(phonenumberText);
    	add(roamText);
    	add(FixAckText);
    	
    	map.setPreferredSize(Display.getWidth(),Display.getHeight()/3);
    	map.moveTo((int)Latitude*100*1000 , (int)Longitude*100*1000);
    	map.setZoom(ZOOM_MAX);
    	add(map);
    	add(new RichTextField("   ",Field.NON_FOCUSABLE));
    	add(UsageDetails);
    	add(MinutesMonitor);
    	add(IncomingUsage);
    	add(OutgoingUsage);
    	add(TotalMinsUsage);
    	add(new RichTextField(" ",Field.NON_FOCUSABLE));
    	add(MessagingMonitor);
    	add(ReceivedMsgUsage);
    	add(SentMsgUsage);
    	add(TotalMsgUsage);
    	add(new RichTextField("",Field.NON_FOCUSABLE));
    	add(DataMonitor);
    	add(DownloadUsage);
    	add(UploadUsage);
    	add(TotalDataUsage);
    	
    	TextInserter();
    	
    	new Timer().schedule(new ScreenTextUpdater(), 20*1000 );
    }
    
    public void TextInserter()
    {
		roamText.setText(RoamingString + theModel.SelectData("roaming"));
		if(theModel.SelectData("fix_ack").equalsIgnoreCase("true"))
			ScreenMap(Double.parseDouble(theModel.SelectData("lat")),
					Double.parseDouble(theModel.SelectData("lng")),ZOOM_MIN);
		else
			ScreenMap((int)Latitude,(int)Longitude,ZOOM_MAX);
		
		if(theModel.SelectData("fix_ack").equals("true"))
			FixAckText.setText(FixString + "Successfull");
		else
			FixAckText.setText(FixString + "Scupper(will try later!!)");
		
		
		int incomingInt = (int)Math.floor(Double.parseDouble(theModel.SelectData("incoming"))/60);
		int outgoingInt = (int)Math.floor(Double.parseDouble(theModel.SelectData("outgoing"))/60); 
	
		if(incomingInt==0 )
		{
			IncomingUsage.setText(IncomingString + theModel.SelectData("incoming"));
		}
		else
		{
			incomingInt = 1 + incomingInt;
			IncomingUsage.setText(IncomingString + String.valueOf(incomingInt));
		}
		
		if(outgoingInt==0)
		{
			OutgoingUsage.setText(OutgoingString + "0" );
		}
		else
		{
			outgoingInt = 1 + outgoingInt;
			OutgoingUsage.setText(OutgoingString + String.valueOf(outgoingInt));
		}
		
		if(
			(incomingInt==0 && outgoingInt==0)
			)
			{
				TotalMinsUsage.setText(TotalMinString + "0");
			}
		else
		{
			TotalMinsUsage.setText(TotalMinString + String.valueOf(incomingInt + outgoingInt));
		}		
		
		ReceivedMsgUsage.setText(ReceivedString + theModel.SelectData("received"));
		SentMsgUsage.setText(SentString + theModel.SelectData("sent"));
		TotalMsgUsage.setText(TotalMsgString + 
				String.valueOf(
						Integer.parseInt(theModel.SelectData("received")) +
						Integer.parseInt(theModel.SelectData("sent")) 
						));
		
		DownloadUsage.setText(DownloadString +
				String.valueOf((Double.parseDouble(theModel.SelectData("downloaded"))/1000) ) );
		UploadUsage.setText(UploadString + 
				String.valueOf((Double.parseDouble(theModel.SelectData("uploaded"))/1000) ) );
		TotalDataUsage.setText(TotalDataString + 
				String.valueOf(
						(Double.parseDouble(theModel.SelectData("downloaded")) +
						Double.parseDouble(theModel.SelectData("uploaded")))
						/1000) 
						);
    }
    
    public class ScreenTextUpdater extends TimerTask
    {
    	public void run()
    	{
    		synchronized(Application.getEventLock())
    		{
    			TextInserter();
    		}
			
    	}
    }
    
    public void ScreenMap(double latitude,double longitude,int zoom)
    {
    	synchronized(Application.getEventLock())
    	{
	    	Device_Orientation = Display.getOrientation();
			switch(Device_Orientation)
			{
				case Display.ORIENTATION_SQUARE:
				{
					map.setPreferredSize(Display.getWidth(), Display.getHeight()/3);
					map.moveTo((int)latitude*100*1000, (int)longitude*100*1000);
			    	map.setZoom(zoom);
				};
				case Display.ORIENTATION_PORTRAIT:
				{
					map.setPreferredSize(Display.getWidth(), Display.getHeight()/3);
					map.moveTo((int)latitude*100*1000, (int)longitude*100*1000);
			    	map.setZoom(zoom);
				};
				case Display.ORIENTATION_LANDSCAPE:
				{
					map.setPreferredSize(Display.getWidth(), Display.getHeight()/3);
					map.moveTo((int)latitude*100*1000, (int)longitude*100*1000);
			    	map.setZoom(zoom);
				};
			}
    	}
    }
    
    public boolean onClose()
    {
    	UiApplication.getUiApplication().requestBackground();
    	return false;
    }
    
}