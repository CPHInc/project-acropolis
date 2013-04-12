package com.app.project.acropolis.engine.monitor;

import loggers.Logger;
import net.rim.blackberry.api.phone.phonelogs.CallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogListener;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogs;

import com.app.project.acropolis.model.ApplicationDB;

public class CallMonitor_ver2 
{

	public CallMonitor_ver2()
	{
		new Logger().LogMessage(">>CallMonitor version2<<");
		PhoneLogs.addListener(new LogListener());
	}
	
	public class LogListener implements PhoneLogListener
	{

		public void callLogAdded(CallLog cl) {
			PhoneCallLog phonecalllog = (PhoneCallLog)cl;
			if(phonecalllog.getType() == PhoneCallLog.TYPE_PLACED_CALL)
			{
				new Logger().LogMessage("outgoing");
				UpdateDB(cl.getDuration(),OUTGOING);
			}
			if(phonecalllog.getType() == PhoneCallLog.TYPE_RECEIVED_CALL)
			{
				new Logger().LogMessage("incoming");
				UpdateDB(cl.getDuration(),INCOMING);
			}
			
		}

		public void callLogUpdated(CallLog cl, CallLog oldCl) {
			
		}
		
		public void callLogRemoved(CallLog cl) {}

		public void reset() {}
	
	}
	
	protected final int INCOMING = 100;
	protected final int OUTGOING = 101;
	
	public void UpdateDB(int seconds,int type)
	{
		int temp_values = 0;
		if(!LocationCode.Check_NON_CAN_Operator())
		{
			if(type==INCOMING)
			{
				temp_values = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalIncoming)).intValue() + 
						Seconds2Minutes(seconds);
				ApplicationDB.setValue(String.valueOf(temp_values),ApplicationDB.LocalIncoming);
			}
			if(type==OUTGOING)
			{
				temp_values = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalOutgoing)).intValue() + 
						Seconds2Minutes(seconds);
				ApplicationDB.setValue(String.valueOf(temp_values),ApplicationDB.LocalOutgoing);
			}
		}
		else
		{
			if(type==INCOMING)
			{
				temp_values = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingIncoming)).intValue() + 
						Seconds2Minutes(seconds);
				ApplicationDB.setValue(String.valueOf(temp_values),ApplicationDB.RoamingIncoming);
			}
			if(type==OUTGOING)
			{
				temp_values = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingOutgoing)).intValue() + 
						Seconds2Minutes(seconds);
				ApplicationDB.setValue(String.valueOf(temp_values),ApplicationDB.RoamingOutgoing);
			}
		}
	}
	
	/**
     * Convert seconds to minutes
     * @param seconds int
	 * @return Minutes */
    protected int Seconds2Minutes(int seconds)
    {
    	int minutes=0;
    	if(seconds == 0)
    	{
    		minutes = 0;
    	}
    	else 
    	{
    		minutes = seconds/60 + 1;
    	}
    	return minutes;
    }
	
}
