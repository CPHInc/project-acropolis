package com.app.project.acropolis.engine.monitor;

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

		download = RadioInfo.getNumberOfPacketsReceived();
		upload = RadioInfo.getNumberOfPacketsSent();
		
		theModel.UpdateData("downloaded", String.valueOf(download).toString());
		theModel.UpdateData("uploaded", String.valueOf(upload).toString());
	}
	
}
