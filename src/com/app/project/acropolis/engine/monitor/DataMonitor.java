package com.app.project.acropolis.engine.monitor;

import loggers.Logger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANConnectionListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.system.WLANListener;

import com.app.project.acropolis.model.ModelFactory;

public class DataMonitor implements Runnable//extends TimerTask
{
	ModelFactory theModel;
	
	boolean WIFI_Connected = false;
	
	long db_down = 0;
	long db_up = 0;
	long MDS_download = 0;
	long MDS_upload = 0;
	long wifi_down = 0;
	long wifi_up = 0;
	
	/**
	 * RadioInfo.getNumberOfPacketsReceived()/Sent() includes
	 * packets received/sent from WiFi,Cellular and Bluetooth
	 */
	public void run()
	{
		theModel = new ModelFactory();

		db_down = Long.parseLong(theModel.SelectData("downloaded"));
		db_up = Long.parseLong(theModel.SelectData("uploaded"));
		
		WLANInfo.addListener((WLANListener)new WLANConnectionListener() 
		{
			public void networkConnected() {
				WIFI_Connected = true;
			}

			public void networkDisconnected(int reason) {
				WIFI_Connected = false;
			}
			
		});
		
		if( !WIFI_Connected )
		{//on MDS
			MDS_download = RadioInfo.getNumberOfPacketsReceived() - wifi_down;
			MDS_upload = RadioInfo.getNumberOfPacketsSent() - wifi_up;
			//new Logger().LogMessage("MDS active");
			/*Download check*/
			if(RadioInfo.getNumberOfPacketsReceived() >= db_down)
			{
				new Logger().LogMessage("RadioInfo packets down-->"+RadioInfo.getNumberOfPacketsReceived());
				MDS_download = RadioInfo.getNumberOfPacketsReceived();
				theModel.UpdateData("downloaded", String.valueOf(MDS_download).toString());
			}
			else
			{
				new Logger().LogMessage("RadioInfo packets down-->"+RadioInfo.getNumberOfPacketsReceived());
				MDS_download = db_down + RadioInfo.getNumberOfPacketsReceived();
				theModel.UpdateData("downloaded", String.valueOf(MDS_download).toString());
			}
			/*Upload check*/
			if(RadioInfo.getNumberOfPacketsSent() >= db_up)
			{
				new Logger().LogMessage("RadioInfo packets up-->"+RadioInfo.getNumberOfPacketsSent());
				MDS_upload = RadioInfo.getNumberOfPacketsSent();
				theModel.UpdateData("uploaded", String.valueOf(MDS_upload).toString());
			}
			else
			{
				new Logger().LogMessage("RadioInfo packets up-->"+RadioInfo.getNumberOfPacketsSent());
				MDS_upload = db_up + RadioInfo.getNumberOfPacketsSent();
				theModel.UpdateData("uploaded", String.valueOf(MDS_upload).toString());
			}
		}
		else
		{//on WIFI
			wifi_down = RadioInfo.getNumberOfPacketsReceived() - MDS_download;
			wifi_up = RadioInfo.getNumberOfPacketsSent() - MDS_upload;
		}
			
	}
	
}
