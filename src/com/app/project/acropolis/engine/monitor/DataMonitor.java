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
	long r_db_download = 0;
	long r_db_upload = 0;
	long MDS_download = 0;
	long MDS_upload = 0;
	long wifi_down = 0;
	long wifi_up = 0;
	
	public DataMonitor()
	{
		new Logger().LogMessage(">>DataMonitor<<");
	}

	public void run()
	{
		wlan = new WLANMonitor();
		RecordValues();		
	}
	
	/**
	 * RadioInfo.getNumberOfPacketsReceived()/Sent() includes
	 * packets received/sent from WiFi,Cellular and Bluetooth
	 * @see java.lang.Runnable#run()
	 */
	public void RecordValues()
	{
		for(;;)
		{
			packetsReceived = RadioInfo.getNumberOfPacketsReceived();
			packetsSent = RadioInfo.getNumberOfPacketsSent();
			MDS_download = Long.parseLong(ApplicationDB.getValue(ApplicationDB.LocalDownload));
			MDS_upload = Long.parseLong(ApplicationDB.getValue(ApplicationDB.LocalUpload));
			r_db_upload = Long.parseLong(ApplicationDB.getValue(ApplicationDB.RoamingUpload));
			r_db_upload = Long.parseLong(ApplicationDB.getValue(ApplicationDB.RoamingUpload));
			if(!Check_NON_CAN_Operator())
			{
				if( !wlan.getWLANConnection() )
				{//on MDS
					
					MDS_download = MDS_download + (packetsReceived - wlan.getWLANDownload());
					MDS_upload = MDS_upload + (packetsSent - wlan.getWLANUpload());
					ApplicationDB.setValue(String.valueOf(MDS_download),ApplicationDB.LocalDownload);
					ApplicationDB.setValue(String.valueOf(MDS_upload),ApplicationDB.LocalUpload);
					new PlanReducer(LocalDownload,MDS_download);
				}
			}
			else
			{
				if( !wlan.getWLANConnection() )
				{//on MDS
					r_db_download += packetsReceived - wlan.getWLANDownload();
					r_db_upload += packetsSent - wlan.getWLANUpload();
					ApplicationDB.setValue(String.valueOf(r_db_download),ApplicationDB.RoamingDownload);
					ApplicationDB.setValue(String.valueOf(r_db_upload),ApplicationDB.RoamingUpload);
				}
			}
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
	 * @version $Revision: 1.0 $
	 */
	public class WLANMonitor //implements Runnable
	{
		/**
		 * Method run.
		 * @see java.lang.Runnable#run()
		 */
//		public void run()
		public WLANMonitor()
		{
			WLANInfo.addListener((WLANListener)new WLANConnectionListener() 
			{
				public void networkConnected() 
				{
					if(MDS_download<RadioInfo.getNumberOfPacketsReceived())
					WIFI_Connected = true;
					wifi_down = packetsReceived - MDS_download;
					wifi_up = packetsSent - MDS_upload;
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
	
	/**
	 * Method Check_NON_CAN_Operator.
	 * @return boolean
	 */
	public boolean Check_NON_CAN_Operator()
	{
		boolean NON_CANOperatorCheck = true;
   	
		final String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
		    	
		String CurrentNetworkName = "";
		    	
		CurrentNetworkName = RadioInfo.getCurrentNetworkName();
		
		if( CurrentNetworkName.equalsIgnoreCase(CanadianOperators[0]) 
		  			|| CurrentNetworkName.equalsIgnoreCase(CanadianOperators[1])
		   			||CurrentNetworkName.equalsIgnoreCase(CanadianOperators[2]) )
			NON_CANOperatorCheck = false;				//local
		else
			NON_CANOperatorCheck = true;				// ROAMING
		    	
		return NON_CANOperatorCheck;
	 }
	
}
