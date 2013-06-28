package com.app.project.acropolis.UI;

import java.util.Timer;

import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontManager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.GridFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.StringProvider;

import com.app.project.acropolis.controller.StringBreaker;
import com.app.project.acropolis.engine.monitor.LocationCode;
import com.app.project.acropolis.model.ApplicationDB;

/**
 * 
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 *
 * @version $Revision: 1.0 $
 */

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class UIScreen extends MainScreen
{
	final String AppTitle = "Carillion Wireless Monitoring System";
	//	final String AppTitle = "BLUE GIANT Wireless Monitoring System";

	/*Local Rates*/
	final static double LocalVoiceRate = 0.10;		//incoming free(general)
	final static double LocalMessageRate = 0.10;	//some plan 250 free after 10cents/msg
	final static double LocalDataRate = 0.06;		//500MB free after 6cent/MB

	/*Roaming Rates (Outside Canada)*/
	final static double RoamingVoiceRate = 2.00;
	final static double RoamingMessageRate = 0.60;
	final static double RoamingDataRate = 5.00;

	/*Long Distance Rates*/
	final double LongDistanceVoiceRate = 0.20;
	final double LongDistanceMessageRate = 0.0;
	final double LongDistanceDataRate = 0.0;		

	String locationdata = "";

	Timer homecountry = new Timer();
	Timer outsidehomecountry = new Timer();

	int Device_Orientation = 0;
	final int ZOOM_MIN = 3;					//supported MAX = 15 && MIN = 0
	final int ZOOM_MAX = 5;
	int Latitude= 4364169;
	int Longitude= -7962804;


	final int Charges_Rows = 1;
	final int Charges_Columns = 2;
	final int Position_Rows = 1;
	final int Position_Columns = 2;
	final int Rows = 4;
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
	static String DownloadString = "Downloaded (MB)";
	String DownloadResultString = "";
	static String UploadString = "Uploaded (MB)";
	String UploadResultString = "";
	String TotalDataString = "Total (MB)";
	static String TotalResultDataString = "";
	String TotalRunningCost = "Running Cost";
	String TotalLocalCharges = "Monthly";
	static String TotalResultLocalCharges = "";
	String TotalRoamingCharges = "Roaming";
	static String TotalResultRoamingCharges = "$0.0";
	static String RoamingMinutesString = "Roaming";
	static String RoamingResultMinutesString = "";
	String RoamingMessageString = "Roaming";
	static String RoamingResultMessageString = "";
	String RoamingDataString = "Roaming (MB)";
	static String RoamingResultDataString = "";
	GridFieldManager LocalChargesGrid = new GridFieldManager(Charges_Rows,Charges_Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager RoamingChargesGrid = new GridFieldManager(Charges_Rows,Charges_Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager LocationGrid = new GridFieldManager(Position_Rows,Position_Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager VoiceGrid = new GridFieldManager(Rows,Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager MessageGrid = new GridFieldManager(Rows,Columns,GridFieldManager.USE_ALL_WIDTH);
	GridFieldManager DataGrid = new GridFieldManager(Rows,Columns, GridFieldManager.USE_ALL_WIDTH);

	public static RichTextField Country = new RichTextField(); 

	String companyName = "Carillion";
	String companyCopyrightString = companyName + "\u00A9 Copyrights protected"; 
	BitmapField LogoField;
	RichTextField CompanyCopyright = new RichTextField(companyCopyrightString);
	RichTextField RunningCostText = new RichTextField(TotalRunningCost);
	RichTextField phonenumberText = new RichTextField("Phone Number : " + Phone.getDevicePhoneNumber(true) );
	RichTextField roamText = new RichTextField(RoamingString,Field.FIELD_LEFT);
	static RichTextField roamResultText = new RichTextField("");
	//	RichTextField FixAckText = new RichTextField(FixString,Field.NON_FOCUSABLE|Field.FIELD_LEFT); 

	RichTextField LatitudeText = new RichTextField(LatitudeString);
	RichTextField LatitudeResultText = new RichTextField(LatitudeResultString);
	RichTextField UsageDetails = new RichTextField("Monitored Usage",Field.FIELD_HCENTER|Field.FIELD_VCENTER);
	RichTextField LongitudeText = new RichTextField(LongitudeString);
	RichTextField LongitudeResultText = new RichTextField(LongitudeResultString);

	RichTextField MinutesMonitor = new RichTextField("Minutes usage");
	RichTextField IncomingUsage = new RichTextField(IncomingString);
	static RichTextField IncomingResultUsage = new RichTextField("");
	RichTextField OutgoingUsage = new RichTextField(OutgoingString);
	static RichTextField OutgoingResultUsage = new RichTextField("");
	RichTextField TotalMinsUsage = new RichTextField(TotalMinString,Field.FIELD_RIGHT);
	static RichTextField TotalResultMinsUsage = new RichTextField("",Field.FIELD_LEFT);

	RichTextField MessagingMonitor = new RichTextField("Messaging usage");
	RichTextField ReceivedMsgUsage = new RichTextField(ReceivedString);
	static RichTextField ReceivedResultMsgUsage = new RichTextField("");
	RichTextField SentMsgUsage = new RichTextField(SentString);
	static RichTextField SentResultMsgUsage = new RichTextField("");
	RichTextField TotalMsgUsage = new RichTextField(TotalMsgString,Field.FIELD_RIGHT);
	static RichTextField TotalResultMsgUsage = new RichTextField("",Field.FIELD_LEFT);

	RichTextField DataMonitor = new RichTextField("Data usage");
	RichTextField DownloadUsage = new RichTextField(DownloadString);
	static RichTextField DownloadResultUsage = new RichTextField(DownloadString);
	RichTextField UploadUsage = new RichTextField(UploadString);
	static RichTextField UploadResultUsage = new RichTextField(UploadString);
	RichTextField TotalDataUsage = new RichTextField(TotalDataString,Field.FIELD_RIGHT);
	static RichTextField TotalResultDataUsage = new RichTextField(TotalResultDataString,Field.FIELD_LEFT);

	RichTextField TotalLocal = new RichTextField(TotalLocalCharges,Field.FIELD_HCENTER);
	static RichTextField TotalResultLocal = new RichTextField(TotalResultLocalCharges,Field.FIELD_HCENTER);
	RichTextField TotalRoaming = new RichTextField(TotalRoamingCharges,Field.FIELD_HCENTER);
	static RichTextField TotalResultRoaming = new RichTextField(TotalResultRoamingCharges,Field.FIELD_HCENTER);

	RichTextField RoamingMinutes = new RichTextField(RoamingMinutesString);
	static RichTextField RoamingResultMinutes = new RichTextField(RoamingResultMinutesString);
	RichTextField RoamingMessages = new RichTextField(RoamingMessageString);
	static RichTextField RoamingResultMessages = new RichTextField(RoamingResultMessageString);
	RichTextField RoamingData = new RichTextField(RoamingDataString);
	static RichTextField RoamingResultData = new RichTextField(RoamingResultDataString);

	//	ModelFactory theModel = new ModelFactory();
	/**
	 * Creates a new MyScreen object
	 */
	public UIScreen()
	{        
		super(MainScreen.FIELD_VCENTER|MainScreen.FIELD_HCENTER|
				MainScreen.VERTICAL_SCROLL|MainScreen.USE_ALL_WIDTH);
		//testing global handler
		ApplicationManager.getApplicationManager().postGlobalEvent(0xde15415aec6cfa55L);

		Application.getApplication().setAcceptEvents(true);
		DeriveApplicationFont();
		setTitle(AppTitle);		//if required

		UiApplication.getUiApplication().requestForeground();
		AppMenu();

		add(Country);

		CompanyLogo();
		add(RunningCostText);
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

		Application.getApplication().invokeLater(new ScreenTextUpdater(),10*1000, true);
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

	public void CompanyLogo()
	{
		Bitmap companyBMP = Bitmap.getBitmapResource("carillion.png");
		//    	Bitmap companyBMP = Bitmap.getBitmapResource("bluegiant.png");
		LogoField = new BitmapField(companyBMP,BitmapField.HCENTER);
		add(LogoField);
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
		LocationGrid.add(roamText);//col1
		roamResultText.setText(RoamingResultString);
		LocationGrid.add(roamResultText);//col2

		//    	/*Row 2*/
		//    	LocationGrid.add(LatitudeText);
		//    	LatitudeResultText.setText(LatitudeResultString);
		//    	LocationGrid.add(LatitudeResultText);
		//    	/*Row 3*/
		//    	LocationGrid.add(LongitudeText);
		//    	LongitudeResultText.setText(LongitudeResultString);
		//    	LocationGrid.add(LongitudeResultText);

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

		/*Row 4*/
		RoamingMinutes.setFont(Font.getDefault().derive(Font.BOLD));
		VoiceGrid.add(RoamingMinutes);
		RoamingResultMinutes.setText("0");
		RoamingResultMinutes.setFont(Font.getDefault().derive(Font.BOLD|Font.ITALIC));
		VoiceGrid.add(RoamingResultMinutes);

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

		/*Row 4*/
		RoamingMessages.setFont(Font.getDefault().derive(Font.BOLD));
		MessageGrid.add(RoamingMessages);
		RoamingResultMessages.setText("0");
		RoamingResultMessages.setFont(Font.getDefault().derive(Font.BOLD|Font.ITALIC));
		MessageGrid.add(RoamingResultMessages);

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

		/*Row 4*/
		RoamingData.setFont(Font.getDefault().derive(Font.BOLD));
		DataGrid.add(RoamingData);
		RoamingResultData.setText("0");
		RoamingResultData.setFont(Font.getDefault().derive(Font.BOLD|Font.ITALIC));
		DataGrid.add(RoamingResultData);

		DataGrid.setBorder(BorderFactory.createBevelBorder(new XYEdges(4,4,4,4)));
		this.getMainManager().add(DataGrid);
	}

	/**
	 * Method FormatDecimal. Trims down leading decimal to 100th unit
	 * @param value double
	 * @return xx.xx */
	public static String FormatDecimal(double value)
	{
		String formated = "";
		String unitDigit = StringBreaker.split(String.valueOf(value), ".")[0];
		unitDigit = unitDigit.trim();
		String decimalDigit = StringBreaker.split(String.valueOf(value), ".")[1];
		if(decimalDigit.equals("0"))
			decimalDigit = "00";
		if(decimalDigit.length()>2)
			decimalDigit = decimalDigit.trim().substring(0,2);
		else
			decimalDigit = decimalDigit.trim();
		formated = unitDigit +"." + decimalDigit;
		return formated;
	}

	public static void TextInserter()
	{
		synchronized(Application.getEventLock())
		{
			/* * Local * */
			//Java AutoBoxing used for parsing String to int(via Integer) 
			int incomingMin = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalIncoming)).intValue();
			int outgoingMin = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalOutgoing)).intValue();
			int localTotalMinutes = incomingMin+outgoingMin;
			int rcvMsg = (Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalReceived))).intValue();
			int sntMsg = (Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalSent))).intValue();
			int localTotalMsg = rcvMsg+sntMsg;
			double downData = (Double.valueOf(ApplicationDB.getValue(ApplicationDB.LocalDownload)).doubleValue())/(1024*1024);
			double upData = (Double.valueOf(ApplicationDB.getValue(ApplicationDB.LocalUpload)).doubleValue())/(1024*1024);
			double localTotalData = downData + upData;//MegaBytes
			/* * Roaming * */
			if(RadioInfo.getCurrentNetworkName()!=null)
			{
				//				if(ApplicationDB.getValue(ApplicationDB.Roaming).equalsIgnoreCase("false") || 
				//						ApplicationDB.getValue(ApplicationDB.Roaming).equalsIgnoreCase("0"))
				if(!LocationCode.Check_NON_CAN_Operator())
					roamResultText.setText("No");
				//				else if(ApplicationDB.getValue(ApplicationDB.Roaming).equalsIgnoreCase("true"))
				else
					roamResultText.setText("Yes");
			}
			else
			{
				roamResultText.setText("Fetching Roaming fix");
			}
			int roamInMinutes = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingIncoming)).intValue();
			int roamOutMinutes = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingOutgoing)).intValue();
			int roamTotalMinutes =  roamInMinutes + roamOutMinutes;
			int roamRcvMsg = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingReceived)).intValue();
			int roamSntMsg = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingSent)).intValue();
			int roamTotalMsg =  roamRcvMsg + roamSntMsg;
			double roamDownData = (Double.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingDownload)).doubleValue())/(1024*1024);
			double roamUpData = (Double.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingUpload)).doubleValue())/(1024*1024);
			double roamTotalData = roamDownData + roamUpData;

			int totalIncoming = incomingMin + roamInMinutes;
			int totalOutgoing = outgoingMin + roamOutMinutes;
			double totalDownload = downData + roamDownData;
			double totalUpload = upData + roamUpData;
			int totalReceived = rcvMsg + roamRcvMsg;
			int totalSent = sntMsg + roamSntMsg;
			int totalMin = localTotalMinutes + roamTotalMinutes;
			int totalMsg = localTotalMsg + roamTotalMsg;
			double totalData = localTotalData + roamTotalData;

			IncomingResultUsage.setText( String.valueOf(totalIncoming).toString() );
			OutgoingResultUsage.setText( String.valueOf(totalOutgoing).toString() );
			TotalResultMinsUsage.setText( String.valueOf(totalMin).toString() );
			ReceivedResultMsgUsage.setText( String.valueOf(totalReceived).toString());
			SentResultMsgUsage.setText( String.valueOf(totalSent).toString() );
			TotalResultMsgUsage.setText( String.valueOf(totalMsg).toString() );
			DownloadResultUsage.setText( StringBreaker.split(String.valueOf(totalDownload), ".")[0] );
			UploadResultUsage.setText( StringBreaker.split(String.valueOf(totalUpload), ".")[0] );//+ " " + strBreak.split(upData, ".")[1]);
			TotalResultDataUsage.setText( StringBreaker.split(String.valueOf(totalData),".")[0]);

			double	templocalTotalCost = 
					(outgoingMin*LocalVoiceRate) +
							(localTotalMsg*LocalMessageRate) +
							(localTotalData*LocalDataRate);

			String localTotalCost = FormatDecimal(templocalTotalCost);
			
			String totalRoamCost = FormatDecimal(
					(roamTotalMinutes*RoamingVoiceRate) 
					+ (roamTotalMsg*RoamingMessageRate)
					+ (roamTotalData*RoamingDataRate));

			String totalCost = String.valueOf(Double.valueOf(localTotalCost).doubleValue() 
					+ Double.valueOf(totalRoamCost).doubleValue());  
			TotalResultLocal.setText("$"+totalCost);
			RoamingResultMinutes.setText(StringBreaker.split(String.valueOf(roamTotalMinutes),".")[0]);
			RoamingResultMessages.setText(StringBreaker.split(String.valueOf(roamTotalMsg),".")[0]);
			RoamingResultData.setText(StringBreaker.split(String.valueOf(roamTotalData),".")[0]);
			TotalResultRoaming.setText("$"+totalRoamCost);

			//			/* * Location/Position * */
			//			LatitudeResultText.setText(theModel.SelectData("lat"));
			//			LongitudeResultText.setText(theModel.SelectData("lng"));
		}
	}

	/**
	 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
	 * Updates screen text via java.util.TimerTask
	 * @version $Revision: 1.0 $
	 */
	public static class ScreenTextUpdater implements Runnable//extends TimerTask
	{
		/**
		 * Method run.
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			synchronized(Application.getEventLock())
			{
				TextInserter();
			}
		}
	}

	public void AppMenu()
	{
		String menuResetString = "Reset Monitored Data";
		String menuRefreshString = "Refresh Values";
		int menuResetOrdinal = 0x230000;
		int menuRefreshOrdinal = 0x250000;
		int menuResetPriority = 0;
		int menuRefreshPriority = 1;
		MenuItem refreshScreen = new MenuItem(new StringProvider(menuRefreshString),menuRefreshOrdinal,menuRefreshPriority);
		refreshScreen.setCommand(new Command(new CommandHandler() {
			public void execute(ReadOnlyCommandMetadata metadata, Object context) {
				new ScreenTextUpdater().run();
			}
		}));
		addMenuItem(refreshScreen);

		MenuItem resetData = new MenuItem(new StringProvider(menuResetString),menuResetOrdinal,menuResetPriority);
		resetData.setCommand(new Command(new CommandHandler() {
			public void execute(ReadOnlyCommandMetadata metadata,Object context) {
				ApplicationDB.reset();
				Application.getApplication().invokeLater(new ScreenTextUpdater());
//				new ScreenTextUpdater().run();
			}
		}));
		addMenuItem(resetData);
	}

	public void close()
	{
		super.close();
		System.exit(0);	
	}

	/**
	 * Method onSavePrompt.
	 * @return boolean
	 */
	public boolean onSavePrompt()
	{
		return false;
	}

}