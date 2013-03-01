package com.app.project.acropolis.UI;

import java.util.TimerTask;

import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.model.ModelFactory;

public class DataMonitor extends TimerTask
{
	ModelFactory theModel;
	
	long download = 0;
	long upload = 0;
	
	public void run()
	{
		theModel = new ModelFactory();

		if(RadioInfo.getNumberOfPacketsReceived() > Long.parseLong(theModel.SelectData("downloaded"))
				&& RadioInfo.getNumberOfPacketsSent() > Long.parseLong(theModel.SelectData("uploaded")))
		{
			download = RadioInfo.getNumberOfPacketsReceived();
			upload = RadioInfo.getNumberOfPacketsSent();
			download = download + Long.parseLong( theModel.SelectData("downloaded") );
			upload = upload + Long.parseLong( theModel.SelectData("uploaded") );
			
			theModel.UpdateData("downloaded", String.valueOf(download));
			theModel.UpdateData("uploaded", String.valueOf(upload));
		}
		else
		{
			download = RadioInfo.getNumberOfPacketsReceived();
			upload = RadioInfo.getNumberOfPacketsSent();
			
			download = download + Long.parseLong( theModel.SelectData("downloaded") );
			upload = upload + Long.parseLong( theModel.SelectData("uploaded") );
			
			theModel.UpdateData("downloaded", String.valueOf(download));
			theModel.UpdateData("uploaded", String.valueOf(upload));
		}
		
	}
	
}
