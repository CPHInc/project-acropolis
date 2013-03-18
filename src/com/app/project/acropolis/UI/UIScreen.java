package com.app.project.acropolis.UI;

import java.util.Timer;
import java.util.TimerTask;

import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

import com.app.project.acropolis.controller.CodeValidator;
import com.app.project.acropolis.controller.StringBreaker;
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
	ApplicationMap map = new ApplicationMap();
	int Device_Orientation = 0;
	final int ZOOM_MIN = 3;					//supported MAX = 15 && MIN = 0
	final int ZOOM_MAX = 5;
 	int Latitude= 4364169;
	int Longitude= -7962804;
	
	String waitText = "waiting..";
	String RoamingString = "Roaming :-- ";
	String FixString = "Fix acquired -- ";
	String IncomingString = "Incoming Usage (Mins) :-- ";
	String OutgoingString = "Outgoing Usage (Mins) :-- ";
	String TotalMinString = "Total Usage (Mins) :---- ";
	String ReceivedString = "Received Messages :-- ";
	String SentString = "Sent Messages :-- ";
	String TotalMsgString = "Total Messages :---- ";
	String DownloadString = "Downloaded(MB) :-- ";
	String UploadString = "Uploaded(MB) :--";
	String TotalDataString = "Total Data(MB) :---- ";
	
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
    	if(theModel.SelectData("fix_ack").equals("true"))
    	{
    		map.moveTo( Integer.valueOf(theModel.SelectData("lat")).intValue(),
    				Integer.valueOf(theModel.SelectData("lng")).intValue() );
    		map.setZoom(ZOOM_MIN);
    	}
    	else
    	{
	    	map.moveTo(Latitude, Longitude);
	    	map.setZoom(ZOOM_MAX);
    	}
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
    	
    	Application.getApplication().invokeLater(new ScreenTextUpdater(),5*60*1000, true);
    	
    	//new Timer().schedule(new ScreenTextUpdater(), 1000, 10*1000);
//    	for(;;)
//    	{
//    		new ScreenTextUpdater();
//    		try {
//				Thread.sleep(60*1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}
//    	new Timer().schedule(new ScreenTextUpdater(), 20*1000 );
    }
    
    public void TextInserter()
    {
		roamText.setText(RoamingString + theModel.SelectData("roaming"));
		
		if(theModel.SelectData("fix_ack").equals("true"))
			FixAckText.setText(FixString + "Last known location!!");
		else
			FixAckText.setText(FixString + "Scupper(will try later!!)");
		
		//Java AutoBoxing used for parsing String to int(via Integer) 
		int incomingMin = Integer.valueOf(theModel.SelectData("incoming")).intValue();
		int outgoingMin = Integer.valueOf(theModel.SelectData("outgoing")).intValue(); 
		int totalMin = incomingMin + outgoingMin;
		IncomingUsage.setText( IncomingString + String.valueOf(incomingMin).toString() );
		OutgoingUsage.setText( OutgoingString + String.valueOf(outgoingMin).toString() );
		TotalMinsUsage.setText( TotalMinString + String.valueOf(totalMin).toString() );
		
		int rcvMsg = (Integer.valueOf(theModel.SelectData("received"))).intValue();
		int sntMsg = (Integer.valueOf(theModel.SelectData("sent"))).intValue();
		int totalMsg = rcvMsg + sntMsg;
		ReceivedMsgUsage.setText(ReceivedString + String.valueOf(rcvMsg).toString());
		SentMsgUsage.setText(SentString + String.valueOf(sntMsg).toString() );
		TotalMsgUsage.setText(TotalMsgString + String.valueOf(totalMsg).toString() );
		
		StringBreaker strBreak = new StringBreaker();
		String downData = String.valueOf(
				(Long.parseLong(
						theModel.SelectData("downloaded")))/(1024*1024));
		String upData = String.valueOf(
				(Long.parseLong(
						theModel.SelectData("uploaded")))/(1024*1024));
		String totalData = String.valueOf( 
				( Long.parseLong(downData) + Long.parseLong(upData) ));
		DownloadUsage.setText(DownloadString + strBreak.split(downData, ".")[0] );
		new Logger().LogMessage(this.getClass() + " Uploaded::"+ strBreak.split(downData, ".")[0] );//+ strBreak.split(downData, ".")[1]);
		UploadUsage.setText(UploadString + strBreak.split(upData, ".")[0] );//+ " " + strBreak.split(upData, ".")[1]);
		new Logger().LogMessage(this.getClass() + " Uploaded::"+ strBreak.split(upData, ".")[0]);
		TotalDataUsage.setText(TotalDataString + strBreak.split(totalData,".")[0]);
    }
    
    /**
     * Convert seconds to minutes
     * @param Seconds
     * @return Minutes
     */
    public int Seconds2Minutes(int seconds)
    {
    	int minutes=0;
    	if(seconds == 0)
    	{
    		minutes = 0;
    	}
    	else 
    	{
    		minutes = seconds/60 + 1;
    	}
    	return minutes;
    }
    
    /**
     * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
     * Updates screen text via java.util.TimerTask
     */
    public class ScreenTextUpdater implements Runnable//extends TimerTask
    {
    	public void run()
//    	public ScreenTextUpdater()
    	{
    		synchronized(Application.getEventLock())
    		{
    			TextInserter();
    			if(theModel.SelectData("fix_ack").equalsIgnoreCase("true"))
        		{
        			ScreenMap(Integer.parseInt(theModel.SelectData("lat")),
        					Integer.parseInt(theModel.SelectData("lng")),ZOOM_MIN);
        		}
        		else
        		{
        			ScreenMap(Latitude,Longitude,ZOOM_MAX);
        		}
    		}
    	}
    }
    
    public void ScreenMap(int latitude,int longitude,int zoom)
    {
    	synchronized(Application.getEventLock())
    	{
	    	Device_Orientation = Display.getOrientation();
			switch(Device_Orientation)
			{
				case Display.ORIENTATION_SQUARE:
				{
					map.setPreferredSize(Display.getWidth(), Display.getHeight()/3);
					map.moveTo(latitude, longitude);
			    	map.setZoom(zoom);
				};
				case Display.ORIENTATION_PORTRAIT:
				{
					map.setPreferredSize(Display.getWidth(), Display.getHeight()/3);
					map.moveTo(latitude, longitude);
			    	map.setZoom(zoom);
				};
				case Display.ORIENTATION_LANDSCAPE:
				{
					map.setPreferredSize(Display.getWidth(), Display.getHeight()/3);
					map.moveTo(latitude, longitude);
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