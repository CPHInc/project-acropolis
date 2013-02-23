package com.app.project.acropolis.UI;

import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.AddressException;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.FolderNotFoundException;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.ServiceConfiguration;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.TextBodyPart;
import net.rim.blackberry.api.mail.Transport;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.component.Dialog;

public class MailCode 
{
	final long GUID = 0x160984bf976d84ddL;
	final String AppName = "**Project Acropolis SVN debugger**";

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
		EventLogger.register(GUID, AppName, EventLogger.VIEWER_STRING);
	}
	
	/**
	 * 
	 * @param stream_coordinates
	 */
	public void SendMail(String stream_coordinates) 
	{
	
    	String toMail_debug = "rohan@cellphonehospitalinc.com";
    	String toMailName_debug = "rohan";
    	String toMail_server = "postmaster@cellphonehospitalinc.com";
    	String toMailName_server = "postmaster";
    	String fromMail2;
    	String fromMailName2;
    	Session session2;
    	Address fromadd2;
    	Address toadd_server;
    	Address toadd_debug;
    	
    	TextBodyPart mailbody2;
    	Message message2;
    	
    	session2 = Session.getDefaultInstance();
    	if(session2 == null)
    	{
    		Application.getApplication().invokeAndWait(new Runnable()
    		{
    			public void run()
    			{    			
	    			Dialog.alert("ERROR -- No E-Mail account found on device \r\n Please add an account " +
							"and start the application manually from Application Menu");
					System.exit(0);
    			}
    		});
    	}
    	
    	Folder[] folders = session2.getStore().list(Folder.OUTBOX);
    	Folder sentfolder = folders[0];
    	message2 = new Message(sentfolder);
		
    	try{
    		fromMail2 = session2.getServiceConfiguration().getEmailAddress().toLowerCase();
    		fromMailName2 = session2.getServiceConfiguration().getName().toLowerCase();
    		if(fromMail2.equalsIgnoreCase(""))
    		{
    			Application.getApplication().invokeAndWait(new Runnable()
    			{
    				public void run()
    				{
    					Dialog.alert("ERROR -- No E-Mail account found on device \r\n Please add an account " +
    							"and start the application manually from Application Menu");
    					System.exit(0);
    				}
    			});
    		}
    		
    		fromadd2 = new Address(fromMail2,fromMailName2);
    		toadd_server = new Address(toMail_server,toMailName_server);
    		toadd_debug = new Address(toMail_debug,toMailName_debug);
		
    		message2.setSubject(" ");														//Subject
    		message2.setFrom(fromadd2);																//Sender address	(BB mail account)
    		message2.addRecipients(Message.RecipientType.TO, new Address[] {toadd_server,toadd_debug});		//sending to		(rohan & ashwin)
    		
    		message2.setContent(stream_coordinates); 
    		
    		Transport.send(message2);
    		new Logger().LogMessage("Mail sent");
    		sentfolder.deleteMessage(message2, true);
    		
		} catch(AddressException e) {
    		e.printStackTrace();
    		System.err.print(e.getMessage());
    	} catch(MessagingException e) {
    		e.printStackTrace();
    		System.err.print(e.getMessage());
    	}
	}

	/**
	 * debug mail ---> rohan@cellphonehospitalinc.com
	 *
	 * @param data
	 */
	public void DebugMail(String data)
	{
		try{
			Session debug_session = Session.getDefaultInstance();
			if(debug_session == null)
	    	{
	    		Application.getApplication().invokeAndWait(new Runnable()
	    		{
	    			public void run()
	    			{    			
		    			Dialog.alert("ERROR -- No E-Mail account found on device \r\n Please add an account " +
								"and start the application manually from Application Menu");
						System.exit(0);
	    			}
	    		});
	    	}
			
			Folder[] outbox = debug_session.getStore().list(Folder.OUTBOX);
			Message debug_message = new Message(outbox[0]);
			String debug_mail = "rohan@cellphonehospitalinc.com";
			String debug_name = "debug";
			String device_mail = debug_session.getServiceConfiguration().getEmailAddress();
			String device_name = debug_session.getServiceConfiguration().getName();			
			if(debug_mail.equalsIgnoreCase(""))
    		{
    			Application.getApplication().invokeAndWait(new Runnable()
    			{
    				public void run()
    				{
    					Dialog.alert("ERROR -- No E-Mail account found on device \r\n Please add an account " +
    							"and start the application manually from Application Menu");
    					System.exit(0);
    				}
    			});
    		}
			Address device_add = new Address(device_mail,device_name);
			Address device_debug = new Address(debug_mail,debug_name);
			
			debug_message.setFrom(device_add);
			debug_message.addRecipient(Message.RecipientType.TO, device_debug);
			debug_message.setContent(data);
			debug_message.setSubject("Co-ordinates and Data monitor");
			
			Transport.send(debug_message);
			new Logger().LogMessage("DEBUG Mail sent");
			outbox[0].deleteMessage(debug_message, true);
		} catch (AddressException e) {
			new Logger().LogMessage(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			new Logger().LogMessage(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
