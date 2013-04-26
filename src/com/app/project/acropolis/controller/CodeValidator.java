package com.app.project.acropolis.controller;

import loggers.Logger;
import net.rim.blackberry.api.mail.Session;

import com.app.project.acropolis.engine.mail.HoledCeiling;
import com.app.project.acropolis.engine.monitor.CallMonitor_ver2;
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
		InboxScanner();
		new Logger().LogMessage("Engine ready steady GOO!!..");
//		new CallMonitor();
		new CallMonitor_ver2();
		new TextMonitor();
		new Thread(new DataMonitor()).start();
//		new Timer().schedule(new DataMonitor(),10*1000);
//		Application.getApplication().invokeLater(new DataMonitor(),60*1000, true);
		new Thread(new RoamingHandler()).start();
		new Thread(new LocalHandler()).start();
	}
	
    public boolean InboxScanner()
    {
    	Session.getDefaultInstance().getStore().addFolderListener(new HoledCeiling());
    	return true;
    }

	
}
