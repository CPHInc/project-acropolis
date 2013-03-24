package com.app.project.acropolis.UI;

import java.util.Timer;

import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontManager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.GridFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

import com.app.project.acropolis.controller.CodeValidator;
import com.app.project.acropolis.controller.RoamingRunnable;
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
	/*Local Rates*/
	final double LocalVoiceRate = 0.10;//incoming free(general)
	final double LocalMessageRate = 0.10;//some plan 250 free after 10cents/msg
	final double LocalDataRate = 0.06;//500MB free after 6cent/MB
	
	/*Roaming Rates (Outside Canada)*/
	final double RoamingVoiceRate = 2.00;
	final double RoamingMessageRate = 0.60;
	final double RoamingDataRate = 5.00;

	/*Long Distance Rates*/
	final double LongDistanceVoiceRate = 0.20;
	final double LongDistanceMessageRate = 0.0;
	final double LongDistanceDataRate = 0.0;		
	
	String locationdata = "";
	
	Timer homecountry = new Timer();
	Timer outsidehomecountry = new Timer();
	
	RoamingRunnable theRoamer;
	ModelFactory theModel = new ModelFactory();
	Thread validatorThread = new CodeValidator();
	ApplicationMap map = new ApplicationMap();
	int Device_Orientation = 0;
	final int ZOOM_MIN = 3;					//supported MAX = 15 && MIN = 0
	final int ZOOM_MAX = 5;
 	int Latitude= 4364169;
	int Longitude= -7962804;

	
	final int Charges_Rows = 1;
	final int Charges_Columns = 2;
	final int Rows = 3;
	final int Columns = 2;
	
	String waitText = "waiting..";
	String RoamingString = "Roaming";
	String RoamingResultString = "No";
	String FixString = "Fix acquired";
	String LatitudeString = "Latitude";
	String LongitudeString = "Longitude";
	String LatitudeResultString = "0";
	String LongitudeResultString = "0";
	String IncomingString = "Incoming (Mins)";
	String IncomingResultString = "";
	String OutgoingString = "Outgoing (Mins)";
	String OutgoingResultString = "";
	String TotalMinString = "Total (Mins)";
	String TotalResultMinString = "";
	String ReceivedString = "Received";
	String ReceivedResultString = "";
	String SentString = "Sent Messages";
	String SentResultString = "";
	String TotalMsgString = "Total Messages";
	String TotalResultMsgString = "";
	String DownloadString = "Downloaded (KB)";
	String DownloadResultString = "";
	String UploadString = "Uploaded (KB)";
	String UploadResultString = "";
	String TotalDataString = "Total (KB)";
	String TotalResultDataString = "";
	String TotalLocalCharges = "Total Monitored Cost";
	String TotalResultLocalCharges = "";
	String TotalRoamingCharges = "Total Roaming Cost";
	String TotalResultRoamingCharges = "$0.0";
	String RoamingChargesString = "Roaming Charges(Approx.)";
	String RoamingMinutesChargesString = "Minutes \'$2.00/min\':-- $";
	String RoamingMessageChargesString = "Messages \'$0.6/msg\':-- $";
	String RoamingDataChargesString = "Data \'$5.00/MB\':-- $";
	String LocalChargesString = "Local Charges (Approx.)";
	String LocalMinutesChargesString = "Minutes \'$0.10/min\' (\u221E Incoming) $";
	String LocalMessageChargesString = "Messages \'$0.10/msg\':-- $";
	String LocalDataChargesString = "Data \'$0.06/MB\':-- $";
	
	GridFieldManager LocalChargesGrid = new GridFieldManager(Charges_Rows,Charges_Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager RoamingChargesGrid = new GridFieldManager(Charges_Rows,Charges_Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager LocationGrid = new GridFieldManager(Rows,Columns,GridFieldManager.USE_ALL_WIDTH);
    GridFieldManager VoiceGrid = new GridFieldManager(Rows,Columns,GridFieldManager.USE_ALL_WIDTH);
    GridFieldManager MessageGrid = new GridFieldManager(Rows,Columns,GridFieldManager.USE_ALL_WIDTH);
    GridFieldManager DataGrid = new GridFieldManager(Rows,Columns, GridFieldManager.USE_ALL_WIDTH);
	
	RichTextField phonenumberText = new RichTextField("Phone Number : " + Phone.getDevicePhoneNumber(true) );
	RichTextField roamText = new RichTextField(RoamingString,Field.FOCUSABLE|Field.FIELD_LEFT);
	RichTextField roamResultText = new RichTextField("",Field.FOCUSABLE);
//	RichTextField FixAckText = new RichTextField(FixString,Field.NON_FOCUSABLE|Field.FIELD_LEFT); 
	
	RichTextField LatitudeText = new RichTextField(LatitudeString,Field.FOCUSABLE);
	RichTextField LatitudeResultText = new RichTextField(LatitudeResultString,Field.FOCUSABLE);
	RichTextField UsageDetails = new RichTextField("Monitored Usage",Field.FIELD_HCENTER|Field.FIELD_VCENTER);
	RichTextField LongitudeText = new RichTextField(LongitudeString,Field.FOCUSABLE);
	RichTextField LongitudeResultText = new RichTextField(LongitudeResultString,Field.FOCUSABLE);
	
	RichTextField MinutesMonitor = new RichTextField("Minutes usage");
	RichTextField IncomingUsage = new RichTextField(IncomingString);
	RichTextField IncomingResultUsage = new RichTextField("",Field.FOCUSABLE);
	RichTextField OutgoingUsage = new RichTextField(OutgoingString);
	RichTextField OutgoingResultUsage = new RichTextField("",Field.FOCUSABLE);
	RichTextField TotalMinsUsage = new RichTextField(TotalMinString,Field.FIELD_RIGHT);
	RichTextField TotalResultMinsUsage = new RichTextField("",Field.FOCUSABLE|Field.FIELD_LEFT);
	
	RichTextField MessagingMonitor = new RichTextField("Messaging usage");
	RichTextField ReceivedMsgUsage = new RichTextField(ReceivedString);
	RichTextField ReceivedResultMsgUsage = new RichTextField("",Field.FOCUSABLE);
	RichTextField SentMsgUsage = new RichTextField(SentString);
	RichTextField SentResultMsgUsage = new RichTextField("",Field.FOCUSABLE);
	RichTextField TotalMsgUsage = new RichTextField(TotalMsgString,Field.FOCUSABLE|Field.FIELD_RIGHT);
	RichTextField TotalResultMsgUsage = new RichTextField("",Field.FOCUSABLE|Field.FIELD_LEFT);

	RichTextField DataMonitor = new RichTextField("Data usage");
	RichTextField DownloadUsage = new RichTextField(DownloadString);
	RichTextField DownloadResultUsage = new RichTextField(DownloadString,Field.FOCUSABLE);
	RichTextField UploadUsage = new RichTextField(UploadString);
	RichTextField UploadResultUsage = new RichTextField(UploadString,Field.FOCUSABLE);
	RichTextField TotalDataUsage = new RichTextField(TotalDataString,Field.FIELD_RIGHT);
	RichTextField TotalResultDataUsage = new RichTextField(TotalResultDataString,Field.FOCUSABLE|Field.FIELD_LEFT);
	
	RichTextField TotalLocal = new RichTextField(TotalLocalCharges,Field.FIELD_HCENTER);
	RichTextField TotalResultLocal = new RichTextField(TotalResultLocalCharges,Field.FIELD_HCENTER);
	RichTextField LocalCharges =  new RichTextField(LocalChargesString);
	RichTextField LocalMinutesCharges = new RichTextField(LocalMinutesChargesString);
	RichTextField LocalMessageCharges = new RichTextField(LocalMessageChargesString);
	RichTextField LocalDataCharges = new RichTextField(LocalDataChargesString);
	
	RichTextField TotalRoaming = new RichTextField(TotalRoamingCharges,Field.FIELD_HCENTER);
	RichTextField TotalResultRoaming = new RichTextField(TotalResultRoamingCharges,Field.FIELD_HCENTER);
	RichTextField RoamingCharges =  new RichTextField(RoamingChargesString,Field.FIELD_HCENTER);
	RichTextField RoamingMinutesCharges = new RichTextField(RoamingMinutesChargesString);
	RichTextField RoamingMessageCharges = new RichTextField(RoamingMessageChargesString);
	RichTextField RoamingDataCharges = new RichTextField(RoamingDataChargesString);

    /**
     * Creates a new MyScreen object
     */
    public UIScreen()
    {        
    	super(MainScreen.FIELD_VCENTER|MainScreen.FIELD_HCENTER|
    			MainScreen.VERTICAL_SCROLLBAR|MainScreen.HORIZONTAL_SCROLLBAR|MainScreen.USE_ALL_WIDTH);
    	
    	super.setBackground(BackgroundFactory.createLinearGradientBackground
    			(Color.CORNSILK, Color.CORNSILK, Color.CORNSILK, Color.CORNSILK));
    	
    	this.getMainManager().setBackground(BackgroundFactory.createLinearGradientBackground
    			(Color.LIGHTGOLDENRODYELLOW, Color.LIGHTGRAY, Color.WHEAT, Color.WHEAT));
    	
    	DeriveApplicationFont();
    	
    	Application.getApplication().setAcceptEvents(true);
    	new Logger().LogMessage("Application requested for Foreground entry");
        UiApplication.getUiApplication().requestForeground();

    	setTitle(" ** DEBUG Version ** Project Acropolis ");		//if required
    	
//    	this.getMainManager().setBackground(
//                BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
//                0x0099CCFF,0x00336699,0x00336699));
    	//execute CodeValidator() for checking device properties and code handling
    	
    	validatorThread.start();
    	
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	theModel.UpdateData("phone_number", Phone.getDevicePhoneNumber(true));
    	
		add(phonenumberText);
    	
//    	add(LocalCharges);
//    	add(LocalMinutesCharges);
//    	add(LocalMessageCharges);
//    	add(LocalDataCharges);
    	
//    	add(RoamingCharges);
//    	add(RoamingMinutesCharges);
//    	add(RoamingMessageCharges);
//    	add(RoamingDataCharges);
    	ChargesGrid();
    	add(new RichTextField("   ",Field.NON_FOCUSABLE));
    	MonitoredField();
    	PositionGrid();
    	MinuteField();
    	VoiceMinutesGrid();
    	MessageField();
    	MessageGrid();
    	DataField();
    	DataGrid();
    	
    	TextInserter();
    	
    	Application.getApplication().invokeLater(new ScreenTextUpdater(),5*60*1000, true);
    }
    
    public void DeriveApplicationFont()
    {
         FontManager.getInstance().load(
         		this.getClass().getResourceAsStream("/Vera.ttf"), "Vera", FontManager.APPLICATION_FONT);
         try {
 			FontFamily fontFamily = FontFamily.forName("Vera");
 			if(StringBreaker.split(DeviceInfo.getSoftwareVersion(),".")[0].equalsIgnoreCase("7"))
 			{
 				Font appFont = fontFamily.getFont(Font.PLAIN, 30);
 				FontManager.getInstance().setApplicationFont(appFont);
 			}
 			else
 			{
 				Font appFont = fontFamily.getFont(Font.PLAIN, 26);
 				FontManager.getInstance().setApplicationFont(appFont);
 			}
 		} catch (ClassNotFoundException e) {
 			e.printStackTrace();
 		}
    }
    
    public void ChargesGrid()
    {
    	LocalChargesGrid.setColumnProperty(0, GridFieldManager.FIXED_SIZE, Display.getWidth()/2);
    	LocalChargesGrid.setColumnProperty(1, GridFieldManager.FIXED_SIZE, Display.getWidth()/2);
    	LocalChargesGrid.add(TotalLocal);
    	TotalResultLocal.setText(TotalResultLocalCharges);
    	LocalChargesGrid.add(TotalResultLocal);
//    	Bitmap roundedBMP = Bitmap.getBitmapResource("rounded-border.png");
//    	LocalChargesGrid.setBorder(BorderFactory.createBitmapBorder(new XYEdges(3,3,3,3),roundedBMP));
    	LocalChargesGrid.setBorder(BorderFactory.createSimpleBorder(new XYEdges(3,3,3,3),Border.STYLE_TRANSPARENT));
    	this.getMainManager().add(LocalChargesGrid);
    	
    	RoamingChargesGrid.setColumnProperty(0, GridFieldManager.FIXED_SIZE, Display.getWidth()/2);
    	RoamingChargesGrid.setColumnProperty(1, GridFieldManager.FIXED_SIZE, Display.getWidth()/2);
    	RoamingChargesGrid.add(TotalRoaming);
    	TotalResultRoaming.setText(TotalResultRoamingCharges);
    	RoamingChargesGrid.add(TotalResultRoaming);
    	RoamingChargesGrid.setBorder(BorderFactory.createSimpleBorder(new XYEdges(3,3,3,3),Border.STYLE_TRANSPARENT));
    	this.getMainManager().add(RoamingChargesGrid);
    }
    
    public void MonitoredField()
    {
    	UsageDetails.setFont(Font.getDefault().derive(
    			Font.BROKEN_LINE_UNDERLINED,45,Ui.UNITS_px,Font.ANTIALIAS_DEFAULT
    			,Font.EMBOSSED_EFFECT));
    	add(UsageDetails);
    }
    
    public void MinuteField()
    {
    	MinutesMonitor.setFont(Font.getDefault().derive(
    			Font.ITALIC,MinutesMonitor.getContentHeight(),Ui.UNITS_px,Font.ANTIALIAS_DEFAULT
    			,Font.EMBOSSED_EFFECT));
    	add(MinutesMonitor);
    }
    
    public void MessageField()
    {
    	MessagingMonitor.setFont(Font.getDefault().derive(
    			Font.ITALIC,MinutesMonitor.getContentHeight(),Ui.UNITS_px,Font.ANTIALIAS_DEFAULT
    			,Font.EMBOSSED_EFFECT));
    	add(MessagingMonitor);
    }
    
    public void DataField()
    {
    	DataMonitor.setFont(Font.getDefault().derive(
    			Font.ITALIC,MinutesMonitor.getContentHeight(),Ui.UNITS_px,Font.ANTIALIAS_DEFAULT
    			,Font.EMBOSSED_EFFECT));
    	add(DataMonitor);
    }
    
    public void PositionGrid()
    {
    	LocationGrid.setColumnProperty(0,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	LocationGrid.setColumnProperty(1,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	
    	/*Row 1*/
    	LocationGrid.add(roamText);
    	roamResultText.setText(RoamingResultString);
    	LocationGrid.add(roamResultText);
    	
    	/*Row 2*/
    	LocationGrid.add(LatitudeText);
    	LatitudeResultText.setText(LatitudeResultString);
    	LocationGrid.add(LatitudeResultText);
    	
    	/*Row 3*/
    	LocationGrid.add(LongitudeText);
    	LongitudeResultText.setText(LongitudeResultString);
    	LocationGrid.add(LongitudeResultText);
    	
    	LocationGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
    	this.getMainManager().add(LocationGrid);
    }
    
    public void VoiceMinutesGrid()
    {
    	VoiceGrid.setColumnProperty(0,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	VoiceGrid.setColumnProperty(1,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	
    	/*Row 1*/
    	VoiceGrid.add(IncomingUsage);
    	IncomingResultUsage.setText(IncomingResultString);
    	VoiceGrid.add(IncomingResultUsage);
    	
    	/*Row 2*/
    	VoiceGrid.add(OutgoingUsage);
    	OutgoingResultUsage.setText(OutgoingResultString);
    	VoiceGrid.add(OutgoingResultUsage);
    	
    	/*Row 3*/
    	TotalMinsUsage.setFont(Font.getDefault().derive(Font.ITALIC));
    	VoiceGrid.add(TotalMinsUsage);
    	TotalResultMinsUsage.setText(TotalResultMinString);
    	TotalResultMinsUsage.setFont(Font.getDefault().derive(Font.BOLD));
    	VoiceGrid.add(TotalResultMinsUsage);
    	
    	VoiceGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
    	this.getMainManager().add(VoiceGrid);
    }
    
    public void MessageGrid()
    {
    	MessageGrid.setColumnProperty(0,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	MessageGrid.setColumnProperty(1,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
    	
    	/*Row 1*/
    	MessageGrid.add(ReceivedMsgUsage);
    	ReceivedResultMsgUsage.setText(ReceivedResultString);
    	MessageGrid.add(ReceivedResultMsgUsage);
    	
    	/*Row 2*/
    	MessageGrid.add(SentMsgUsage);
    	SentResultMsgUsage.setText(SentResultString);
    	MessageGrid.add(SentResultMsgUsage);
    	
    	/*Row 3*/
    	TotalMsgUsage.setFont(Font.getDefault().derive(Font.ITALIC));
    	MessageGrid.add(TotalMsgUsage);
    	TotalResultMsgUsage.setText(TotalResultMsgString);
    	TotalResultMsgUsage.setFont(Font.getDefault().derive(Font.BOLD));
    	MessageGrid.add(TotalResultMsgUsage);

    	MessageGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
    	this.getMainManager().add(MessageGrid);
    }
    
   public void DataGrid()
   {
	   DataGrid.setColumnProperty(0,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
	   DataGrid.setColumnProperty(1,GridFieldManager.FIXED_SIZE,Display.getWidth()/2);
	   
	   /*Row 1*/
	   DataGrid.add(DownloadUsage);
	   DownloadResultUsage.setText(DownloadResultString);
	   DataGrid.add(DownloadResultUsage);
	   
	   /*Row 2*/
	   DataGrid.add(UploadUsage);
	   UploadResultUsage.setText(UploadResultString);
	   DataGrid.add(UploadResultUsage);
	   
	   /*Row 3*/
	   TotalDataUsage.setFont(Font.getDefault().derive(Font.ITALIC));
	   DataGrid.add(TotalDataUsage);
	   TotalResultDataUsage.setText(TotalResultDataString);
	   TotalResultDataUsage.setFont(Font.getDefault().derive(Font.BOLD));
	   DataGrid.add(TotalResultDataUsage);
	   
	   DataGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
	   this.getMainManager().add(DataGrid);
   }
    
    public String FormatDecimal(double value)
    {
    	String formated = "";
    	String unitDigit = StringBreaker.split(String.valueOf(value), ".")[0];
    	unitDigit = unitDigit.trim();
    	String decimalDigit = StringBreaker.split(String.valueOf(value), ".")[1];
    	if(decimalDigit.length()>2)
    		decimalDigit = decimalDigit.trim().substring(0,2);
    	else
    		decimalDigit = decimalDigit.trim();
    	formated = unitDigit +"." + decimalDigit;
    	return formated;
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
    
    public void TextInserter()
    {
    	synchronized(Application.getEventLock())
    	{
			//Java AutoBoxing used for parsing String to int(via Integer) 
			int incomingMin = Integer.valueOf(theModel.SelectData("incoming")).intValue();
			int outgoingMin = Integer.valueOf(theModel.SelectData("outgoing")).intValue(); 
			int totalMin = incomingMin + outgoingMin;
			
			int rcvMsg = (Integer.valueOf(theModel.SelectData("received"))).intValue();
			int sntMsg = (Integer.valueOf(theModel.SelectData("sent"))).intValue();
			int totalMsg = rcvMsg + sntMsg;
			
			String downData = String.valueOf(
					(Long.parseLong(
							theModel.SelectData("downloaded")))/(1024));
			String upData = String.valueOf(
					(Long.parseLong(
							theModel.SelectData("uploaded")))/(1024));
			String totalData = String.valueOf( 
					( Long.parseLong(downData) + Long.parseLong(upData) ));
			
			String temptotalData = StringBreaker.split(String.valueOf(Integer.valueOf(totalData).intValue()/1024),".")[0];
			
			String totalCost = FormatDecimal(
						((outgoingMin*LocalVoiceRate) +
								(totalMsg*LocalMessageRate) +
								((Double.valueOf(temptotalData).doubleValue())*LocalDataRate)));
			
			TotalResultLocal.setText("$"+totalCost);
			
			if(theModel.SelectData("roaming").equalsIgnoreCase("false"))
				roamResultText.setText("No");
			else if(theModel.SelectData("roaming").equalsIgnoreCase("true"))
				roamResultText.setText("Yes");
			
//			LocalMinutesCharges.setText(LocalMinutesCharges.getText() + String.valueOf((incomingMin*LocalVoiceRate)));
//			LocalMessageCharges.setText(LocalMessageCharges.getText() + String.valueOf((totalMsg*LocalMessageRate)));
//			LocalDataCharges.setText(LocalDataCharges.getText() + 
//					(String.valueOf((Integer.valueOf(temptotalData).intValue()/1024)*LocalDataRate).toString()));
			
//			int roamTotalMinutes = theRoamer.getRoamingIncoming() + theRoamer.getRoamingOutgoing();
//			int roamTotalMessages = theRoamer.getRoamingReceived() + theRoamer.getRoamingSent();
//			int roamTotalData = theRoamer.getRoamingDownload() + theRoamer.getRoamingUpload();
//			RoamingMinutesCharges.setText(RoamingMinutesCharges.getText() + String.valueOf((roamTotalMinutes*RoamingVoiceRate)).toString());
//			RoamingMessageCharges.setText(RoamingMessageCharges.getText() + String.valueOf((roamTotalMessages*RoamingMessageRate)).toString());
//			RoamingDataCharges.setText(RoamingDataCharges.getText() + String.valueOf((roamTotalData*RoamingDataRate)).toString());
			
			if(theModel.SelectData("fix_ack").equals("true"))
			{
	//			FixAckText.setText(FixString + "Last known location");
				LatitudeResultText.setText(theModel.SelectData("lat"));
				LongitudeResultText.setText(theModel.SelectData("lng"));
			}
			else if(theModel.SelectData("fix_ack").equals("false"))
			{
	//			FixAckText.setText(FixString + "Searching");
				//			FixAckText.setText(FixString + "Last known location");
				LatitudeResultText.setText(theModel.SelectData("lat"));
				LongitudeResultText.setText(theModel.SelectData("lng"));
			}
			
			IncomingResultUsage.setText( IncomingResultString + String.valueOf(incomingMin).toString() );
			OutgoingResultUsage.setText( OutgoingResultString + String.valueOf(outgoingMin).toString() );
			TotalResultMinsUsage.setText( TotalResultMinString + String.valueOf(totalMin).toString() );
			
			ReceivedResultMsgUsage.setText(ReceivedResultString + String.valueOf(rcvMsg).toString());
			SentResultMsgUsage.setText(SentResultString + String.valueOf(sntMsg).toString() );
			TotalResultMsgUsage.setText(TotalResultMsgString + String.valueOf(totalMsg).toString() );
			
			DownloadResultUsage.setText(DownloadResultString + StringBreaker.split(downData, ".")[0] );
			new Logger().LogMessage(this.getClass() + " Uploaded::"+ StringBreaker.split(downData, ".")[0] );//+ strBreak.split(downData, ".")[1]);
			UploadResultUsage.setText(UploadResultString + StringBreaker.split(upData, ".")[0] );//+ " " + strBreak.split(upData, ".")[1]);
			new Logger().LogMessage(this.getClass() + " Uploaded::"+ StringBreaker.split(upData, ".")[0]);
			TotalResultDataUsage.setText(TotalResultDataString + StringBreaker.split(totalData,".")[0]);
			
    	}
    }
    
    /**
     * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
     * Updates screen text via java.util.TimerTask
     */
    public class ScreenTextUpdater implements Runnable//extends TimerTask
    {
    	public void run()
    	{
    		synchronized(Application.getEventLock())
    		{
    			TextInserter();
    		}
    	}
    }
    
    public boolean onClose()
    {
    	UiApplication.getUiApplication().requestBackground();
    	return false;
    }
    
}