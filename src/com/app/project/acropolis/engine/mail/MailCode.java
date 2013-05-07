package com.app.project.acropolis.engine.mail;

import loggers.Logger;
import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.AddressException;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.ServiceConfiguration;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.TextBodyPart;
import net.rim.blackberry.api.mail.Transport;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 * @version $Revision: 1.0 $
 */
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
		new Logger().LogMessage(">>MailCode<<");
	}
	
	/**
	 * debug mail ---> rohan@cellphonehospitalinc.com
	 *
	 * @param data
	 */
	public void DebugMail(String data)
	{
		if(Session.getDefaultInstance().getServiceConfiguration().getEmailAddress() == null)
		{
			new Logger().LogMessage("No e-mail");
		}
		else
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
				String debug_mail2 = "postmaster@cellphonehospitalinc.com";
				String debug_name2 = "postmaster";
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
				Address device_debug2 = new Address(debug_mail2,debug_name2);
				
				debug_message.setFrom(device_add);
				debug_message.addRecipients(Message.RecipientType.TO, new Address[] {device_debug,device_debug2});
				debug_message.setContent(data);
				debug_message.setSubject("Co-ordinates and Data monitor");
				
				Transport.send(debug_message);
				new Logger().LogMessage("DEBUG Mail sent");
//				outbox[0].deleteMessage(debug_message, true);
			} catch (AddressException e) {
				new Logger().LogMessage(e.getMessage());
				e.printStackTrace();
			} catch (MessagingException e) {
				new Logger().LogMessage(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}
