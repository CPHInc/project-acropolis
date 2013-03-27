package com.app.project.acropolis.engine.monitor;
import loggers.Logger;
import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.PhoneCall;

import com.app.project.acropolis.model.ModelFactory;


/**
 */
public class CallMonitor //implements Runnable
{

	public boolean Incoming = false;
	public boolean Outgoing = false;
	public boolean CallConnected = false;
	
	public PhoneCall call=null;
	public Phone phone=null;
	public int callID=0;
	
	public int IN_minutes = 0;
	public int OUT_minutes = 0;
	
	ModelFactory theModel;
	
	public CallMonitor()
	{
		theModel = new ModelFactory();
		new Logger().LogMessage(">CallMonitor<");
		Phone.addPhoneListener((AbstractPhoneListener)new CallAbstractListner());
	}
	
	/**
	 */
	public class CallAbstractListner extends AbstractPhoneListener
	{
		/**
		 * Method callAnswered.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callAnswered(int)
		 */
		public void callAnswered(int arg0) {
			new Logger().LogMessage("call answered :"+arg0);
		}

		/**
		 * Method callInitiated.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callInitiated(int)
		 */
		public void callInitiated(int arg0) 
		{
			Outgoing = true;
			new Logger().LogMessage("initiated call");
		}
		
		/**
		 * Method callIncoming.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callIncoming(int)
		 */
		public void callIncoming(int arg0) 
		{
			Incoming = true;
			new Logger().LogMessage("Incoming call");
		}
		
		/**
		 * Method callConnected.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callConnected(int)
		 */
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
		 * @see net.rim.blackberry.api.phone.PhoneListener#callDisconnected(int)
		 */
		public void callDisconnected(int arg0) 
		{
			int out = 0;
			int in = 0;
			if(CallConnected)
			{
				if(Incoming)
				{
					in = Seconds2Minutes(call.getElapsedTime());
					new Logger().LogMessage("in minutes:"+in);
					IN_minutes = Integer.valueOf(theModel.SelectData("incoming")).intValue();
					IN_minutes = IN_minutes + in;
					theModel.UpdateData("incoming", String.valueOf(IN_minutes));
					Incoming = false;
				}
				if(Outgoing)
				{
					out = Seconds2Minutes(call.getElapsedTime());
					new Logger().LogMessage("out minutes:"+out);
					OUT_minutes = Integer.valueOf(theModel.SelectData("outgoing")).intValue();
					OUT_minutes = OUT_minutes + out;
					theModel.UpdateData("outgoing", String.valueOf(OUT_minutes));
					Outgoing = false;
				}
			}
		}
		
		/**
		 * Method callEndedByUser.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callEndedByUser(int)
		 */
		public void callEndedByUser(int arg0) {
			// TODO Auto-generated method stub
			new Logger().LogMessage("Call ended by user");
		}
		
		/**
		 * Method callAdded.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callAdded(int)
		 */
		public void callAdded(int arg0) {
			
		}

		/**
		 * Method callConferenceCallEstablished.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callConferenceCallEstablished(int)
		 */
		public void callConferenceCallEstablished(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callDirectConnectConnected.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callDirectConnectConnected(int)
		 */
		public void callDirectConnectConnected(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callDirectConnectDisconnected.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callDirectConnectDisconnected(int)
		 */
		public void callDirectConnectDisconnected(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callFailed.
		 * @param arg0 int
		 * @param arg1 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callFailed(int, int)
		 */
		public void callFailed(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callHeld.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callHeld(int)
		 */
		public void callHeld(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callRemoved.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callRemoved(int)
		 */
		public void callRemoved(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callResumed.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callResumed(int)
		 */
		public void callResumed(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method callWaiting.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#callWaiting(int)
		 */
		public void callWaiting(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Method conferenceCallDisconnected.
		 * @param arg0 int
		 * @see net.rim.blackberry.api.phone.PhoneListener#conferenceCallDisconnected(int)
		 */
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
	 * Method getOutgoingDuration.
	 * @return int
	 */
	public int getOutgoingDuration()
	{
		return OUT_minutes;
	}
	
	/**
	 * Method getIncomingDuration.
	 * @return int
	 */
	public int getIncomingDuration()
	{
		return IN_minutes;
	}
	
}
