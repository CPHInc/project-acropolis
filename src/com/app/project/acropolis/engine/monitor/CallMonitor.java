package com.app.project.acropolis.engine.monitor;
import loggers.Logger;
import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.PhoneCall;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.model.ApplicationDB;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
public class CallMonitor implements Runnable
{
	String[] MapKeys = {"PhoneNumber","Roaming","Latitude","Longitude",
			"FixAck","FixDeviceTime","FixServerTime","Incoming",
			"Outgoing","Download","Upload","Received","Sent"};
	
	public boolean Incoming = false;
	public boolean Outgoing = false;
	public boolean CallConnected = false;
	
	public PhoneCall call=null;
	public Phone phone=null;
	public int callID=0;
	
	public int IN_minutes = 0;
	public int OUT_minutes = 0;
	public int R_IN_minutes = 0;
	public int R_OUT_minutes = 0;
//	ModelFactory theModel = new ModelFactory();
//	ApplicationStoreDetails DBDetails = new ApplicationStoreDetails();
		
	public CallMonitor()
	{
		new Logger().LogMessage(">CallMonitor<");
	}
	
	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		Phone.addPhoneListener((AbstractPhoneListener)new CallAbstractListner());
	}
	
	/**
	 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
	 * @version $Revision: 1.0 $
	 */
	public class CallAbstractListner extends AbstractPhoneListener
	{
		/**
		 * Method callAnswered.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callAnswered(int) */
		public void callAnswered(int arg0) {
			new Logger().LogMessage("call answered :"+arg0);
		}

		/**
		 * Method callInitiated.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callInitiated(int) */
		public void callInitiated(int arg0) 
		{
			Outgoing = true;
			new Logger().LogMessage("Outgoing call");
		}
		
		/**
		 * Method callIncoming.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callIncoming(int) */
		public void callIncoming(int arg0) 
		{
			Incoming = true;
			new Logger().LogMessage("Incoming call");
		}
		
		/**
		 * Method callConnected.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callConnected(int) */
		public void callConnected(int arg0)
		{
			CallConnected = true;
			new Logger().LogMessage("Call connected!!");
			if(Incoming)
			{
				call = Phone.getCall(arg0);
				new Logger().LogMessage("Answered Incoming call");
			}
			if(Outgoing)
			{
				call = Phone.getCall(arg0);
				new Logger().LogMessage("Answered Outgoing call");
			}
		}

		/**
		 * Method callDisconnected.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callDisconnected(int) */
		public void callDisconnected(int arg0) 
		{
			int out = 0;
			int in = 0;
			if(CallConnected)
			{
				if(!Check_NON_CAN_Operator())
				{
					if(Incoming)
					{
						in = Seconds2Minutes(call.getElapsedTime());
						IN_minutes = Integer.parseInt(ApplicationDB.getValue(ApplicationDB.LocalIncoming));
						new Logger().LogMessage("DB Lin minutes:"+IN_minutes);
						IN_minutes = IN_minutes + in;
						ApplicationDB.setValue(String.valueOf(IN_minutes), ApplicationDB.LocalIncoming);
						Incoming = false;
					}
					if(Outgoing)
					{
						out = Seconds2Minutes(call.getElapsedTime());
						OUT_minutes = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalOutgoing)).intValue();
						new Logger().LogMessage("DB Lout minutes:"+OUT_minutes);
						OUT_minutes = OUT_minutes + out;
						ApplicationDB.setValue(String.valueOf(OUT_minutes), ApplicationDB.LocalOutgoing);
						Outgoing = false;
					}
				}
				else
				{
					if(Incoming)
					{
						in = Seconds2Minutes(call.getElapsedTime());
						R_IN_minutes = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingIncoming)).intValue();
						new Logger().LogMessage("DB Rin minutes:"+R_IN_minutes);
						R_IN_minutes = R_IN_minutes + in;
						ApplicationDB.setValue(String.valueOf(R_IN_minutes),ApplicationDB.RoamingIncoming);
						Incoming = false;
					}
					if(Outgoing)
					{
						out = Seconds2Minutes(call.getElapsedTime());
						R_OUT_minutes = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingOutgoing)).intValue();
						new Logger().LogMessage("DB Rout minutes:"+R_OUT_minutes);
						R_OUT_minutes = R_OUT_minutes + out;
						ApplicationDB.setValue(String.valueOf(R_OUT_minutes),ApplicationDB.RoamingOutgoing);
						Outgoing = false;
					}
				}
			}
		}
		
		/**
		 * Method callEndedByUser.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callEndedByUser(int) */
		public void callEndedByUser(int arg0) {
			// TODO Auto-generated method stub
			new Logger().LogMessage("Call ended by user");
		}
		
		/**
		 * Method callAdded.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callAdded(int) */
		public void callAdded(int arg0) {
			
		}

		/**
		 * Method callConferenceCallEstablished.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callConferenceCallEstablished(int) */
		public void callConferenceCallEstablished(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callDirectConnectConnected.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callDirectConnectConnected(int) */
		public void callDirectConnectConnected(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callDirectConnectDisconnected.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callDirectConnectDisconnected(int) */
		public void callDirectConnectDisconnected(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callFailed.
		 * @param arg0 int
		 * @param arg1 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callFailed(int, int) */
		public void callFailed(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callHeld.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callHeld(int) */
		public void callHeld(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callRemoved.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callRemoved(int) */
		public void callRemoved(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callResumed.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callResumed(int) */
		public void callResumed(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callWaiting.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#callWaiting(int) */
		public void callWaiting(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method conferenceCallDisconnected.
		 * @param arg0 int
		
		 * @see net.rim.blackberry.api.phone.PhoneListener#conferenceCallDisconnected(int) */
		public void conferenceCallDisconnected(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/**
     * Convert seconds to minutes
    
    
     * @param seconds int
	 * @return Minutes */
    public int Seconds2Minutes(int seconds)
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
	
	/**
	 * Method Check_NON_CAN_Operator.
	 * @return boolean
	 */
	public boolean Check_NON_CAN_Operator()
	{
		boolean NON_CANOperatorCheck = true;
   	
		final String CanadianOperators[] = {"Rogers Wireless" , "Telus" , "Bell"};
		    	
		String CurrentNetworkName = "";
		    	
		CurrentNetworkName = RadioInfo.getCurrentNetworkName();
		
		if( CurrentNetworkName.equalsIgnoreCase(CanadianOperators[0]) 
		  			|| CurrentNetworkName.equalsIgnoreCase(CanadianOperators[1])
		   			||CurrentNetworkName.equalsIgnoreCase(CanadianOperators[2]) )
			NON_CANOperatorCheck = false;				//if Current Operator is CANADIAN then **FALSE**
		else
			NON_CANOperatorCheck = true;				//if Current Operator is not CANADIAN then **TRUE** hence ROAMING
		    	
		return NON_CANOperatorCheck;
	 }
	
}
