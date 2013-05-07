package com.app.project.acropolis.engine.monitor;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MultipartMessage;
import javax.wireless.messaging.TextMessage;

import loggers.Logger;
import net.rim.blackberry.api.sms.OutboundMessageListener;
import net.rim.device.api.system.RadioInfo;

import com.app.project.acropolis.model.ApplicationDB;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */

public class TextMonitor //implements Runnable
{
	
	/**
	 * So as to track/listen SMS and MMS listening events
	 * the "Connection" has to be 
	 * 		"server" -- Usage to receive or listen
	 * 				 messaging activity
	 * 		(sms://:<port> |||| mms://:<port> )
	 * 
	 * 		"client" -- Usage for initiating a 
	 * 				connection or transmission
	 * 			(sms://<address/SMSAddress> : <port> 
	 * 			|||| mms://<address/ > :<port> )
	 *
	 *  SMS Aiding APIs -- 
	 *  "javax.microedition.io.Connection" is direct requisite of
	 *  "javax.microedition.io.Connector" which handles all the BB connections
	 *  but since SMS has/contains Datagram best outcome would come from
	 *  "javax.microedition.io.DatagramConnection"
	 *  
	 *  MMS Aiding APIs --
	 *  
	 */
	
	String[] MapKeys = {"PhoneNumber","Roaming","Latitude","Longitude",
			"FixAck","FixDeviceTime","FixServerTime","Incoming",
			"Outgoing","Download","Upload","Received","Sent"};
	
	final String SMS_Server = "sms://:0";
	final String SMS_Server_noport = "sms://";
	MessageConnection msg_conn;
	DatagramConnection datagram_conn;
	
	Datagram dg_sms;
	Message msg_rcv;
	Message msg_snd;
	TextMessage textmsg_rcv;
	TextMessage textmsg_snd;
	BinaryMessage binmsg_rcv;
	BinaryMessage binmsg_snd;
	MultipartMessage multimsg_rcv;
	MultipartMessage multimsg_snd;
	
	String sms_address = "";
	String sms_out_address = "";
	
	byte[] sms_payload = new byte[2000];	//store upto 2000 characters
	byte[] sms_out_payload = new byte[2000];
	
	public int sent = 0;
	public int received = 0;
	
	public TextMonitor()
	{
		new Logger().LogMessage(">TextMonitor<");
		HandleMessageConnection();
	}
	
	/** Via MessageConnection ***/
	public void HandleMessageConnection()
	{
		try {
			msg_conn = (MessageConnection)Connector.open(SMS_Server);	// 'sms://:0'
			msg_conn.setMessageListener(new TextListener());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
	 * @version $Revision: 1.0 $
	 */
	public class TextListener implements OutboundMessageListener
	{
		/**
		 * Method notifyIncomingMessage.
		 * @param conn MessageConnection
		
		 * @see javax.wireless.messaging.MessageListener#notifyIncomingMessage(MessageConnection) */
		public void notifyIncomingMessage(MessageConnection conn)
		{
			final int msg_in = 1;
			new Logger().LogMessage(">>--"+this.getClass()+"--<<");
			try {
				msg_rcv = conn.receive();
				new Logger().LogMessage("Receiving message..");				
				
				if(!LocationCode.Check_NON_CAN_Operator())
				{
					if(msg_rcv instanceof TextMessage)
					{
						textmsg_rcv = (TextMessage)msg_rcv;
						received = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalReceived)).intValue();
						received = received + msg_in;
						ApplicationDB.setValue(String.valueOf(received),ApplicationDB.LocalReceived);
						new Logger().LogMessage("TextMessage received"+
								"\r\nCount:"+received);
					}
					else if(msg_rcv instanceof BinaryMessage)
					{
						binmsg_rcv = (BinaryMessage)msg_rcv;
						received = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalReceived)).intValue();
						received = received + msg_in;
						ApplicationDB.setValue(String.valueOf(received),ApplicationDB.LocalReceived);
						new Logger().LogMessage("BinaryMessage received" + 
								"\r\nCount:" + received);
					}
					else if(msg_rcv instanceof MultipartMessage)
					{
						multimsg_rcv = (MultipartMessage)msg_rcv;
						received = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalReceived)).intValue();
						received = received + msg_in;
						ApplicationDB.setValue(String.valueOf(received),ApplicationDB.LocalReceived);
						new Logger().LogMessage("MultipartMessage received" + 
								"\r\nCount:" + received);
					}
				}
				else
				{
					if(msg_rcv instanceof TextMessage)
					{
						textmsg_rcv = (TextMessage)msg_rcv;
						received = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingReceived)).intValue();
						received = received + msg_in;
						ApplicationDB.setValue(String.valueOf(received),ApplicationDB.RoamingReceived);
						new Logger().LogMessage("TextMessage received"+
								"\r\nCount:"+received);
					}
					else if(msg_rcv instanceof BinaryMessage)
					{
						binmsg_rcv = (BinaryMessage)msg_rcv;
						received = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingReceived)).intValue();
						received = received + msg_in;
						ApplicationDB.setValue(String.valueOf(received),ApplicationDB.RoamingReceived);
						new Logger().LogMessage("BinaryMessage received" + 
								"\r\nCount:" + received);
					}
					else if(msg_rcv instanceof MultipartMessage)
					{
						multimsg_rcv = (MultipartMessage)msg_rcv;
						received = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingReceived)).intValue();
						received = received + msg_in;
						ApplicationDB.setValue(String.valueOf(received),ApplicationDB.RoamingReceived);
						new Logger().LogMessage("MultipartMessage received" + 
								"\r\nCount:" + received);
					}
				}
			} catch (InterruptedIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	
		/**
		 * Method notifyOutgoingMessage.
		 * @param message Message
		
		 * @see net.rim.blackberry.api.sms.OutboundMessageListener#notifyOutgoingMessage(Message) */
		public void notifyOutgoingMessage(Message message) 
		{
			final int msg_out = 1;
			new Logger().LogMessage(">>--"+this.getClass()+"--<<");
			new Logger().LogMessage("Sending message");
			if(!LocationCode.Check_NON_CAN_Operator())
			{
				if(message instanceof TextMessage)
				{
					textmsg_snd = (TextMessage)message;
					sent = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalSent)).intValue();
					sent = sent + msg_out;
					ApplicationDB.setValue(String.valueOf(sent),ApplicationDB.LocalSent);
					new Logger().LogMessage("TextMessage sent"+
							"\r\nCount:"+sent);
				}
				else if (message instanceof BinaryMessage)
				{
					binmsg_snd = (BinaryMessage)message;
					sent = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalSent)).intValue();
					sent = sent + msg_out;
					ApplicationDB.setValue(String.valueOf(sent),ApplicationDB.LocalSent);
					new Logger().LogMessage("BinaryMessage sent"+
							"\r\nCount:"+sent);
				}
				else if (message instanceof MultipartMessage)
				{
					multimsg_snd = (MultipartMessage)message;
					sent = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.LocalSent)).intValue();
					sent = sent + msg_out;
					ApplicationDB.setValue(String.valueOf(sent),ApplicationDB.LocalSent);
					new Logger().LogMessage("MultipartMessage sent"+
							"\r\nCount:"+sent);
				}
			}
			else
			{
				if(message instanceof TextMessage)
				{
					textmsg_snd = (TextMessage)message;
					sent = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingSent)).intValue();
					sent = sent + msg_out;
					ApplicationDB.setValue(String.valueOf(sent),ApplicationDB.RoamingSent);
					new Logger().LogMessage("TextMessage sent"+
							"\r\nCount:"+sent);
				}
				else if (message instanceof BinaryMessage)
				{
					binmsg_snd = (BinaryMessage)message;
					sent = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingSent)).intValue();
					sent = sent + msg_out;
					ApplicationDB.setValue(String.valueOf(sent),ApplicationDB.RoamingSent);
					new Logger().LogMessage("BinaryMessage sent"+
							"\r\nCount:"+sent);
				}
				else if (message instanceof MultipartMessage)
				{
					multimsg_snd = (MultipartMessage)message;
					sent = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingSent)).intValue();
					sent = sent + msg_out;
					ApplicationDB.setValue(String.valueOf(sent),ApplicationDB.RoamingSent);
					new Logger().LogMessage("MultipartMessage sent"+
							"\r\nCount:"+sent);
				}
			}
		}
	}
	
}
