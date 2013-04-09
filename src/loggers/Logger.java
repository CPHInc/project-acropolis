package loggers;

import net.rim.device.api.system.EventLogger;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class Logger {

	final long GUID = 0x352293d8b09a8122L;
	final String AppName = "**Project Acropolis SVN Debugger**";
	
	public Logger()
	{
		EventLogger.register(GUID, AppName,EventLogger.VIEWER_STRING);
	}
	
	/**
	 * Method LogMessage.
	 * @param msg String
	 */
	public void LogMessage(String msg)
	{
		EventLogger.logEvent(GUID, msg.getBytes(), EventLogger.ALWAYS_LOG);
	}
	
}
