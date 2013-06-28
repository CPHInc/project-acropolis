package com.app.project.acropolis.controller;

import loggers.Logger;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;

/**
 * All the Engines, Handlers, Runnable are passed and verified
 * if all true then executed
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class CodeValidator implements Runnable
{
	//com.app.project.acropolis.controller.ServerChannel.JUMPSTARTENGINE
	final long JumpStartEngine_GUID = 0x5325bd4e7cbdf34bL;
	
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

		
		Application.getApplication().invokeLater(new com.app.project.acropolis.engine.monitor.DataMonitor());
//				Thread handling
//		Thread _dataMonitor = new Thread(new com.app.project.acropolis.engine.monitor.DataMonitor());
//		_dataMonitor.setPriority(Thread.NORM_PRIORITY);
//		_dataMonitor.start();
		
		ApplicationManager.getApplicationManager().postGlobalEvent(JumpStartEngine_GUID);
	}

}