package com.app.project.acropolis.controller;

import loggers.Logger;

import com.app.project.acropolis.engine.monitor.CallMonitor;
import com.app.project.acropolis.engine.monitor.DataMonitor;
import com.app.project.acropolis.engine.monitor.TextMonitor;

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
		new Thread(new com.app.project.acropolis.engine.monitor.DataMonitor()).start();
		new Thread(new com.app.project.acropolis.controller.RoamingHandler(true)).start();
		new Thread(new com.app.project.acropolis.controller.LocalHandler(true)).start();
	}
	
}