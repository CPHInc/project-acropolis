package com.app.project.acropolis.engine.monitor;

import java.util.TimerTask;

import loggers.Logger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANConnectionListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.system.WLANListener;

import com.app.project.acropolis.model.ApplicationDatabase;
import com.app.project.acropolis.model.ModelFactory;

/**
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
	
	ModelFactory theModel = new ModelFactory();
	ApplicationDatabase appDB = new ApplicationDatabase();
	ApplicationDatabase.LocalUsageDB localUsage = appDB.new LocalUsageDB();
	ApplicationDatabase.RoamingUsageDB roamUsage = appDB.new RoamingUsageDB();
	
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
//		for(;;)
//		{
//			try {
//				Thread.sleep(60*1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		WLANMonitor wlan = new WLANMonitor();
		wlan.run();
		
		if(!Check_NON_CAN_Operator())
		{
			if( !wlan.getWLANConnection() )
			{//on MDS
//				db_download = Long.parseLong(theModel.SelectData("downloaded"));	//db values
//				db_upload = Long.parseLong(theModel.SelectData("uploaded"));
				db_download = Long.parseLong(localUsage.getValue(MapKeys[9]));
				db_upload = Long.parseLong(localUsage.getValue(MapKeys[10]));
	//			new Logger().LogMessage("MDS active");
				MDS_download = db_download + (RadioInfo.getNumberOfPacketsReceived() - wlan.getWLANDownload());
				MDS_upload = db_upload + (RadioInfo.getNumberOfPacketsSent() - wlan.getWLANUpload());
				/*Download check*/
				localUsage.setValue(MapKeys[9], String.valueOf(MDS_download));
//				theModel.UpdateData("downloaded", String.valueOf(MDS_download).toString());
				/*Upload check*/
				localUsage.setValue(MapKeys[10], String.valueOf(MDS_upload));
//				theModel.UpdateData("uploaded", String.valueOf(MDS_upload).toString());
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
	//			new Logger().LogMessage("MDS active");
//				r_db_download = Long.parseLong(theModel.SelectData("roam_data"));
				r_db_download = Long.parseLong(roamUsage.getValue(MapKeys[9]));
				r_db_upload = Long.parseLong(roamUsage.getValue(MapKeys[10]));
				r_db_download += RadioInfo.getNumberOfPacketsReceived() - wlan.getWLANDownload();
				r_db_upload += RadioInfo.getNumberOfPacketsSent() - wlan.getWLANUpload();
				/*Download check*/
//				new Logger().LogMessage("RadioInfo packets down-->"+RadioInfo.getNumberOfPacketsReceived());
				roamUsage.setValue(MapKeys[9], String.valueOf(r_db_download));
				roamUsage.setValue(MapKeys[10], String.valueOf(r_db_upload));
//				theModel.UpdateData("roam_data", String.valueOf(r_db_data).toString());
				/*Upload check*/
//				new Logger().LogMessage("RadioInfo packets up-->"+RadioInfo.getNumberOfPacketsSent());
			}
		}
	}

	/**
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
		
		/**
		 * Method getWLANDownload.
		 * @return long
		 */
		public long getWLANDownload()
		{
			return wifi_down;
		}
		
		/**
		 * Method getWLANUpload.
		 * @return long
		 */
		public long getWLANUpload()
		{
			return wifi_up;
		}
	
		/**
		 * Method getWLANConnection.
		 * @return boolean
		 */
		public boolean getWLANConnection()
		{
			return WIFI_Connected;
		}
		
		/**
		 * Method getWLANProfileName.
		 * @return String
		 */
		public String getWLANProfileName()
		{
			return WLANInfo.getAPInfo().getProfileName();
		}
	}
	
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
