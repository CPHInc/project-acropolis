package com.app.project.acropolis.controller;

import java.util.Timer;

import net.rim.device.api.system.Application;

import loggers.Logger;

import com.app.project.acropolis.engine.monitor.DataMonitor;

/**
 * All the Engines, Handlers, Runnable are passed and verified
 * if all true then executed
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class CodeValidator implements Runnable
{
	public CodeValidator()
	{
		new Logger().LogMessage("--->CodeValidator()<---");
	}

	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		new com.app.project.acropolis.engine.monitor.CallMonitor();
		//		new com.app.project.acropolis.engine.monitor.CallMonitor_ver2();
		new com.app.project.acropolis.engine.monitor.TextMonitor();

		//		Thread handling
		Thread _dataMonitor = new Thread(new DataMonitor());
		_dataMonitor.setPriority(Thread.NORM_PRIORITY);
		_dataMonitor.start();
		try{
			Thread.sleep(10*1000);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		new ServerChannel();
		
		Application.getApplication().invokeLater(new Runnable() {
			public void run()
			{
				new ServerChannel();
			}
		}, 11*60*1000, true);

	}

}