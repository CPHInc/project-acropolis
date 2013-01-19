package com.app.project.acropolis.UI;

import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.AddressException;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.ServiceConfiguration;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.Store;
import net.rim.blackberry.api.mail.TextBodyPart;
import net.rim.blackberry.api.mail.Transport;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Dialog;

public class MailCode 
{
	final long GUID = 0x160984bf976d84ddL;
	final String AppName ="Project Acropolis";

	public Session defaultSession;
	public ServiceConfiguration defaultSC;

	public String defaultName ="";
	public String defaultMailAddress = "";
	public Address defaultFromMailAddr;
	
	public String toName = "CPH Postmaster";
	public String toMailAddress = "postmaster@cellphonehospitalinc.com";
	public Address toMail;
	
	public String toName_debug = "Rohan";
	public String toMailAddress_debug = "rohan@cellphonehospitalinc.com";
	public Address toMail_debug;
	
	public Message message;
	
	public final String MailSubject = "Location Data Phone:-";
	public final String MailContent = "";

	public MailCode()
	{
	}
	
	/**
	 * 
	 * @param stream_coordinates
	 */
	public void SendMail(String stream_coordinates) 
	{
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
	    	String toMail2 = "rohan@cellphonehospitalinc.com";
	    	String toMailName2 = "rohan";
	    	String toMail2_b = "postmaster@cellphonehospitalinc.com";
	    	String toMailName2_b = "postmaster";
	    	String fromMail2;
	    	String fromMailName2;
	    	Session session2;
	    	Address fromadd2;
	    	Address toadd2;
	    	Address toadd2_b;
	    	int messageID;
	    	
	    	TextBodyPart mailbody2;
	    	Message message2;
	    	
	    	session2 = Session.getDefaultInstance();
	    	Folder[] folders = session2.getStore().list(Folder.SENT);
	    	Folder sentfolder = folders[0];
	    	message2 = new Message(sentfolder);
//	    	message2 = new Message();
	    	if(session2 == null)
	    	{
	    		/**No present session found on device*/
	    	}
	    	else
	    	{
	    		try{
		    		fromMail2 = session2.getServiceConfiguration().getEmailAddress().toLowerCase();
		    		fromMailName2 = session2.getServiceConfiguration().getName().toLowerCase();
		    		
	        		fromadd2 = new Address(fromMail2,fromMailName2);
	        		toadd2 = new Address(toMail2,toMailName2);
	        		toadd2_b = new Address(toMail2_b,toMailName2_b);
	    		
		    		message2.setSubject(" ");														//Subject
		    		message2.setFrom(fromadd2);																//Sender address	(BB mail account)
		    		message2.addRecipients(Message.RecipientType.TO, new Address[] {toadd2,toadd2_b});		//sending to		(rohan & ashwin)
		    	
		    		message2.setContent(stream_coordinates); 
		    		
		    		Transport.send(message2);
		    		
		    		messageID = message2.getMessageId();
		    		DeleteSentMail(messageID, sentfolder);
	    		} catch(AddressException e) {
	        		e.printStackTrace();
	        		System.err.print(e.getMessage());
	        	} catch(MessagingException e) {
	        		e.printStackTrace();
	        		System.err.print(e.getMessage());
	        	}
	    	}
		
	}
	
	public boolean DeleteSentMail(int msgID, Folder messagefolder)
	{
		Message deleteMsg = Store.getMessage(msgID);
		messagefolder.deleteMessage(deleteMsg);
		return true;
	}
	
	/** 
	 *  Prompts dialog with warning or severe error 
	 */
	public void WarnOnScreen()
	{
		synchronized(LocationApplication.getEventLock())
		{
			//TODO
		}
	}
	
	/**
	 * returns device's default mail address
	 * @return defaultMailAddress
	 */
	public String getDeviceDefaultAddress()
	{
		return defaultMailAddress;
	}
	
	/**
	 * returns device's default mail address's name
	 * @return defaultName
	 */
	public String getDeviceDefaultName()
	{
		return defaultName;
	}
	
}
