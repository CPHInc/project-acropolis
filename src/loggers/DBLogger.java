package loggers;

import net.rim.device.api.system.EventLogger;

public class DBLogger {
	final long GUID = 0xc53912f4c0358187L;
	final String AppName = "*DB*Project Acropolis SVN Debugger**";
	
	public DBLogger()
	{
		EventLogger.register(GUID, AppName,EventLogger.VIEWER_STRING);
	}
	
	public void LogMessage(String msg)
	{
		EventLogger.logEvent(GUID, msg.getBytes(), EventLogger.ALWAYS_LOG);
	}
	
}
