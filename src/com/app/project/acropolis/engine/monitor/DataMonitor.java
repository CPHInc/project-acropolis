package com.app.project.acropolis.engine.monitor;

import loggers.Logger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANConnectionListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.system.WLANListener;

import com.app.project.acropolis.controller.PlanReducer;
import com.app.project.acropolis.model.ApplicationDB;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class DataMonitor implements Runnable//extends TimerTask
{
	WLANMonitor wlan;
	boolean WIFI_Connected = false;

	public static final int LocalDownload = 11;
	public static final int LocalUpload = 12;
	public static final int RoamingDownload = 17;
	public static final int RoamingUpload = 18;

	int counter = 0;
	long packetsReceived = 0;
	long packetsSent = 0;
	long r_download = 0;
	long r_upload = 0;
	long r_db_download = 0;
	long r_db_upload = 0;
	long MDS_download = 0;
	long MDS_upload = 0;
	long DB_MDS_download = 0;
	long DB_MDS_upload = 0;
	long wifi_down = 0;
	long wifi_up = 0;

	public DataMonitor()
	{
		new Logger().LogMessage(">>DataMonitor<<");
		wlan = new WLANMonitor();
		new Thread(wlan).start();
	}

	public void run()
	{
		RecordValues();		
	}

	/**
	 * RadioInfo.getNumberOfPacketsReceived()/Sent() includes
	 * packets received/sent from WiFi,Cellular and Bluetooth
	 * @see java.lang.Runnable#run()
	 */
	public void RecordValues()
	{
		//all in bytes
		for(;;)
		{
			packetsReceived = RadioInfo.getNumberOfPacketsReceived();
			packetsSent = RadioInfo.getNumberOfPacketsSent();
			DB_MDS_download = Long.parseLong(ApplicationDB.getValue(ApplicationDB.LocalDownload));
			DB_MDS_upload = Long.parseLong(ApplicationDB.getValue(ApplicationDB.LocalUpload));
			r_db_upload = Long.parseLong(ApplicationDB.getValue(ApplicationDB.RoamingUpload));
			r_db_upload = Long.parseLong(ApplicationDB.getValue(ApplicationDB.RoamingUpload));
			if(RadioInfo.getCurrentNetworkName()!=null)
			{
				if(!LocationCode.Check_NON_CAN_Operator())
				{
					if( !wlan.getWLANConnection() )
					{//on MDS
						MDS_download = packetsReceived - wlan.getWLANDownload();
						MDS_upload = packetsSent - wlan.getWLANUpload();
						DB_MDS_download =+ MDS_download;
						DB_MDS_upload =+ MDS_upload;
						ApplicationDB.setValue(String.valueOf(DB_MDS_download),ApplicationDB.LocalDownload);
						ApplicationDB.setValue(String.valueOf(DB_MDS_upload),ApplicationDB.LocalUpload);
						new PlanReducer(LocalDownload,MDS_download);
					}
				}
				else
				{
					if( !wlan.getWLANConnection() )
					{//on MDS
						r_download = packetsReceived - wlan.getWLANDownload();
						r_upload = packetsSent - wlan.getWLANUpload();
						r_db_download =+ r_download;
						r_db_upload =+ r_upload;
						ApplicationDB.setValue(String.valueOf(r_db_download),ApplicationDB.RoamingDownload);
						ApplicationDB.setValue(String.valueOf(r_db_upload),ApplicationDB.RoamingUpload);
					}
				}
			}
			try {
				Thread.sleep(4*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
	 * @version $Revision: 1.0 $
	 */
	public class WLANMonitor implements Runnable
	{
		/**
		 * Method run.
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		//public WLANMonitor()
		{
			WLANInfo.addListener((WLANListener)new WLANConnectionListener() 
			{
				public void networkConnected() 
				{
					WIFI_Connected = true;
					if(!LocationCode.Check_NON_CAN_Operator())
					{
						wifi_down = packetsReceived - MDS_download;
						wifi_up = packetsSent - MDS_upload;
					}
					else
					{
						wifi_down = packetsReceived - r_download;
						wifi_up = packetsSent - r_upload;
					}
				}

				public void networkDisconnected(int reason) 
				{
					WIFI_Connected = false;
				}

			});
		}

		/**
		 * Method getWLANDownload.
		 * @return long */
		public long getWLANDownload()
		{
			return wifi_down;
		}

		/**
		 * Method getWLANUpload.
		 * @return long */
		public long getWLANUpload()
		{
			return wifi_up;
		}

		/**
		 * Method getWLANConnection.
		 * @return boolean */
		public boolean getWLANConnection()
		{
			return WIFI_Connected;
		}

	}

}
