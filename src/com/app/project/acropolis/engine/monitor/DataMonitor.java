package com.app.project.acropolis.engine.monitor;

import loggers.Logger;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.WLANConnectionListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.system.WLANListener;

import com.app.project.acropolis.model.ModelFactory;

public class DataMonitor implements Runnable,SystemListener//extends TimerTask
{
	ModelFactory theModel = new ModelFactory();
	
	boolean WIFI_Connected = false;
	
	boolean SumupData = false;
	int counter = 0;
	final int Add_DB_Values = 0;
	final int Use_Device_Values = 3;
	
	long db_download = 0;
	long db_upload = 0;
	long MDS_download = 0;
	long MDS_upload = 0;
	long wifi_down = 0;
	long wifi_up = 0;
	
	public DataMonitor()
	{
		new Logger().LogMessage(">>DataMonitor<<");
		Application.getApplication().addSystemListener(this);
	}
	
	/**
	 * RadioInfo.getNumberOfPacketsReceived()/Sent() includes
	 * packets received/sent from WiFi,Cellular and Bluetooth
	 */
	public void run()
	{
		WLANMonitor wlan = new WLANMonitor();
		wlan.run();
		
		db_download = Long.parseLong(theModel.SelectData("downloaded"));	//db values
		db_upload = Long.parseLong(theModel.SelectData("uploaded"));
		
		
		if( !wlan.getWLANConnection() )
		{//on MDS
			new Logger().LogMessage("MDS active");
			if(counter==Add_DB_Values)
			{
				MDS_download = db_download + (RadioInfo.getNumberOfPacketsReceived() - wlan.getWLANDownload());
				MDS_upload = db_upload + (RadioInfo.getNumberOfPacketsSent() - wlan.getWLANUpload());
				/*Download check*/
				new Logger().LogMessage("RadioInfo packets down-->"+RadioInfo.getNumberOfPacketsReceived());
				theModel.UpdateData("downloaded", String.valueOf(MDS_download).toString());
				/*Upload check*/
				new Logger().LogMessage("RadioInfo packets up-->"+RadioInfo.getNumberOfPacketsSent());
				theModel.UpdateData("uploaded", String.valueOf(MDS_upload).toString());
				counter=Use_Device_Values;
			}
			else if(counter==Use_Device_Values)
			{
				MDS_download = RadioInfo.getNumberOfPacketsReceived() - wlan.getWLANDownload();
				MDS_upload = RadioInfo.getNumberOfPacketsSent() - wlan.getWLANUpload();
				/*Download check*/
				new Logger().LogMessage("RadioInfo packets down-->"+RadioInfo.getNumberOfPacketsReceived());
				theModel.UpdateData("downloaded", String.valueOf(MDS_download).toString());
				/*Upload check*/
				new Logger().LogMessage("RadioInfo packets up-->"+RadioInfo.getNumberOfPacketsSent());
				theModel.UpdateData("uploaded", String.valueOf(MDS_upload).toString());
			}
		}
		else
		{//on WIFI
			new Logger().LogMessage("Conected to WIFI@"+wlan.getWLANProfileName());
		}
			
	}

	public void batteryGood() {}

	public void batteryLow() {}

	public void batteryStatusChange(int arg0) {}

	public void powerOff() {
		counter = Add_DB_Values;
		SumupData = true;
		long DeviceCollectedData_down = MDS_download;
		long DeviceCollectedData_up = MDS_upload;
		theModel.UpdateData("downloaded", String.valueOf(DeviceCollectedData_down).toString());
		theModel.UpdateData("uploaded", String.valueOf(DeviceCollectedData_up).toString());
		
		new Logger().LogMessage(this.getClass() + "\r\nPowering down");
	}

	public void powerUp() {
		SumupData = false;
		new Logger().LogMessage(this.getClass() + "\r\nPowering up");
	}
	
	
	public class WLANMonitor implements Runnable
	{
		public void run() 
		{
			WLANInfo.addListener((WLANListener)new WLANConnectionListener() 
			{
				public void networkConnected() 
				{
					WIFI_Connected = true;
					wifi_down = RadioInfo.getNumberOfPacketsReceived() - MDS_download;
					wifi_up = RadioInfo.getNumberOfPacketsSent() - MDS_upload;
					new Logger().LogMessage("WLAN Download::"+wifi_down);
					new Logger().LogMessage("WLAN Upload::"+wifi_up);
				}

				public void networkDisconnected(int reason) 
				{
					WIFI_Connected = false;
				}
				
			});
		}
		
		public long getWLANDownload()
		{
			return wifi_down;
		}
		
		public long getWLANUpload()
		{
			return wifi_up;
		}
	
		public boolean getWLANConnection()
		{
			return WIFI_Connected;
		}
		
		public String getWLANProfileName()
		{
			return WLANInfo.getAPInfo().getProfileName();
		}
	}
	
}
