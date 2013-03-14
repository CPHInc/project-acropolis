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

import com.app.project.acropolis.model.ModelFactory;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
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

	ModelFactory theModel;
	
	public TextMonitor()
	{
		new Logger().LogMessage(">TextMonitor<");
		theModel = new ModelFactory();
		HandleMessageConnection();
	}
	
	/** Via DatagramConnection ***/
	
	/**
	 * Opens javax.microedition.io.DatagramConnection server and fetch
	 * javax.microedition.io.Datagram message container storing UDH[User Data Header],
	 * Payload(message text), Address(receipient's number)
	 */
	public void HandleDatagramConnection()
	{
		new Logger().LogMessage(">>"+this.getClass()+"<<");
		try{
//			datagram_conn = (DatagramConnection) Connector.open(SMS_Server);			// 'sms://:0'
			datagram_conn = (DatagramConnection) Connector.open(SMS_Server_noport);		// 'sms://'
			new Logger().LogMessage("DatagramConnection open..");
			for(;;)
			{
				dg_sms = datagram_conn.newDatagram(datagram_conn.getMaximumLength());	//fetch SMS
				
				/*RECIEVing*/
				datagram_conn.receive(dg_sms);											//recieve SMS
				sms_payload = dg_sms.getData();											//payload
				sms_address = dg_sms.getAddress();
				received++;
				new Logger().LogMessage("SMS Received :"+received + " by @"+sms_address +
						" content--" + new String(sms_payload));
			
				/*SENDing*/
				datagram_conn.send(dg_sms);
				sent=+1;
				if(sent>0)
				{
					new Logger().LogMessage("SMS Sent :" + sent + 
						" to :" + dg_sms.getAddress() +
						" content:" + new String (dg_sms.getData()) );
				}
			}
			
		} catch(IOException e) {
			new Logger().LogMessage(e.getMessage());
			e.printStackTrace();
			try {
				new Thread().wait(10*1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} catch(SecurityException e) {
			e.printStackTrace();
			new Logger().LogMessage("SMS server port denied!!!HELP");
			new Logger().LogMessage(e.getMessage());
			try {
				new Thread().wait(10*1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		} catch(Throwable t) {
			t.printStackTrace();
			new Logger().LogMessage(t.getMessage());
			try {
				new Thread().wait(10*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Via MessageConnection ***/
	public void HandleMessageConnection()
	{
		try {
			msg_conn = (MessageConnection)Connector.open(SMS_Server);	// 'sms://:0'
			new Logger().LogMessage("Registered message listener..");
			msg_conn.setMessageListener(new TextListener());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class TextListener implements OutboundMessageListener
	{
	
		public void notifyIncomingMessage(MessageConnection conn)
		{
			int msg_in = 1;
			new Logger().LogMessage(">>--"+this.getClass()+"--<<");
			try {
				msg_rcv = conn.receive();
				new Logger().LogMessage("Receiving message..");				
				
				if(msg_rcv instanceof TextMessage)
				{
					textmsg_rcv = (TextMessage)msg_rcv;
					received = Integer.parseInt(theModel.SelectData("received"));
					received = received + msg_in;
					theModel.UpdateData("received", String.valueOf(received));
					new Logger().LogMessage("TextMessage received"+
							"\r\nAddress:"+textmsg_rcv.getAddress() + 
							"\r\nPayload:"+textmsg_rcv.getPayloadText() +
							"\r\nCount:"+received);
				}
				else if(msg_rcv instanceof BinaryMessage)
				{
					binmsg_rcv = (BinaryMessage)msg_rcv;
					received = Integer.parseInt(theModel.SelectData("received"));
					received = received + msg_in;
					theModel.UpdateData("received", String.valueOf(received));
					new Logger().LogMessage("BinaryMessage received" + 
							"\r\nAddress:" + binmsg_rcv.getAddress() +
							"\r\nCount:" + received);
				}
				else if(msg_rcv instanceof MultipartMessage)
				{
					multimsg_rcv = (MultipartMessage)msg_rcv;
					received = Integer.parseInt(theModel.SelectData("received"));
					received = received + msg_in;
					theModel.UpdateData("received", String.valueOf(received));
					new Logger().LogMessage("MultipartMessage received" + 
							"\r\nAddress:" + multimsg_rcv.getAddress() +
							"\r\nCount:" + received);
				}
			} catch (InterruptedIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	
		public void notifyOutgoingMessage(Message message) 
		{
			int msg_out = 1;
			new Logger().LogMessage(">>--"+this.getClass()+"--<<");
			new Logger().LogMessage("Sending message");
			if(message instanceof TextMessage)
			{
				textmsg_snd = (TextMessage)message;
				sent = Integer.parseInt(theModel.SelectData("sent"));
				sent = sent + msg_out;
				theModel.UpdateData("sent", String.valueOf(sent));
				new Logger().LogMessage("TextMessage sent"+
						"\r\nTo Address"+textmsg_snd.getAddress()+
						"\r\nCount:"+sent);
			}
			else if (message instanceof BinaryMessage)
			{
				binmsg_snd = (BinaryMessage)message;
				sent = Integer.parseInt(theModel.SelectData("sent"));
				sent = sent + msg_out;
				theModel.UpdateData("sent", String.valueOf(sent));
				new Logger().LogMessage("BinaryMessage sent"+
						"\r\nTo Address:"+binmsg_snd.getAddress()+
						"\r\nCount:"+sent);
			}
			else if (message instanceof MultipartMessage)
			{
				multimsg_snd = (MultipartMessage)message;
				sent = Integer.parseInt(theModel.SelectData("sent"));
				sent = sent + msg_out;
				theModel.UpdateData("sent", String.valueOf(sent));
				new Logger().LogMessage("MultipartMessage sent"+
						"\r\nTo Address:"+multimsg_snd.getAddress()+
						"\r\nCount:"+sent);
			}
		}
	}
	
}
