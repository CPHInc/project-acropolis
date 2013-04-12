package com.app.project.acropolis.engine.monitor;

import java.util.TimerTask;

import loggers.Logger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANConnectionListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.system.WLANListener;

import com.app.project.acropolis.model.ApplicationDB;
import com.app.project.acropolis.model.ApplicationStoreDetails;
import com.app.project.acropolis.model.RoamingUsageDB;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class DataMonitor extends TimerTask
{
	String[] MapKeys = {"PhoneNumber","Roaming","Latitude","Longitude",
			"FixAck","FixDeviceTime","FixServerTime","Incoming",
			"Outgoing","Download","Upload","Received","Sent"};
	
	boolean WIFI_Connected = false;
	
	int counter = 0;
	final int Add_DB_Values = 0;
	final int Use_Device_Values = 3;
	
	long db_download = 0;
	long db_upload = 0;
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
	
	/**
	 * RadioInfo.getNumberOfPacketsReceived()/Sent() includes
	 * packets received/sent from WiFi,Cellular and Bluetooth
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		WLANMonitor wlan = new WLANMonitor();
		wlan.run();
		if(!Check_NON_CAN_Operator())
		{
			if( !wlan.getWLANConnection() )
			{//on MDS
				db_download = Long.parseLong(ApplicationDB.getValue(ApplicationDB.LocalDownload));
				db_upload = Long.parseLong(ApplicationDB.getValue(ApplicationDB.LocalUpload));
				MDS_download = db_download + (RadioInfo.getNumberOfPacketsReceived() - wlan.getWLANDownload());
				MDS_upload = db_upload + (RadioInfo.getNumberOfPacketsSent() - wlan.getWLANUpload());
				ApplicationDB.setValue(String.valueOf(MDS_download),ApplicationDB.LocalDownload);
				ApplicationDB.setValue(String.valueOf(MDS_upload),ApplicationDB.LocalUpload);
			}
			else
			{//on WIFI
				new Logger().LogMessage("Conected to WIFI@"+wlan.getWLANProfileName());
			}
		}
		else
		{
			if( !wlan.getWLANConnection() )
			{//on MDS
				r_db_download = Long.parseLong(ApplicationDB.getValue(ApplicationDB.RoamingDownload));
				r_db_upload = Long.parseLong(ApplicationDB.getValue(ApplicationDB.RoamingUpload));
				r_db_download += RadioInfo.getNumberOfPacketsReceived() - wlan.getWLANDownload();
				r_db_upload += RadioInfo.getNumberOfPacketsSent() - wlan.getWLANUpload();
				ApplicationDB.setValue(String.valueOf(r_db_download),ApplicationDB.RoamingDownload);
				ApplicationDB.setValue(String.valueOf(r_db_upload),ApplicationDB.RoamingUpload);
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
		{
			WLANInfo.addListener((WLANListener)new WLANConnectionListener() 
			{
				public void networkConnected() 
				{
					WIFI_Connected = true;
					if(RadioInfo.getNumberOfPacketsReceived() >= MDS_download)
						wifi_down = RadioInfo.getNumberOfPacketsReceived() - MDS_download;
					else
						wifi_down = MDS_download - RadioInfo.getNumberOfPacketsReceived();
					if(RadioInfo.getNumberOfPacketsSent() >= MDS_upload)
						wifi_up = RadioInfo.getNumberOfPacketsSent() - MDS_upload;
					else
						wifi_up = MDS_upload - RadioInfo.getNumberOfPacketsSent();
					new Logger().LogMessage("WLAN Download::"+wifi_down);
					new Logger().LogMessage("WLAN Upload::"+wifi_up);
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
		
		/**
		 * Method getWLANProfileName.
		
		 * @return String */
		public String getWLANProfileName()
		{
			return WLANInfo.getAPInfo().getProfileName();
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
