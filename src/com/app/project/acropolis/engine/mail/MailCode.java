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

	public String getDefaultMailAddress()
	{
		String emailAddress = "";
		if(Session.getDefaultInstance()!=null)
			emailAddress = Session.getDefaultInstance().getServiceConfiguration().getEmailAddress();
		else
		{
			new Logger().LogMessage("No e-mail");
			Application.getApplication().invokeAndWait(new Runnable()
			{
				public void run()
				{    			
					Dialog.alert("ERROR -- No E-Mail account found on device \r\n Please add an account " +
							"and start the application manually from Application Menu");
				}
			});
		}
		return emailAddress;
	}

	public String getDefaultMailName()
	{
		String emailName = "";
		if(Session.getDefaultInstance()!=null)
			emailName = Session.getDefaultInstance().getServiceConfiguration().getName();
		else
		{
			new Logger().LogMessage("No email found");
			Application.getApplication().invokeAndWait(new Runnable()
			{
				public void run()
				{    			
					Dialog.alert("ERROR -- No E-Mail account found on device \r\n Please add an account " +
							"and start the application manually from Application Menu");
				}
			});
		}
		return emailName;
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
			String debug_mail = "rohan@cellphonehospitalinc.com";
			String debug_name = "debug";
			String debug_mail2 = "postmaster@cellphonehospitalinc.com";
			String debug_name2 = "postmaster";
			String device_mail = getDefaultMailAddress();
			String device_name = getDefaultMailName();
			Folder[] outbox = debug_session.getStore().list(Folder.OUTBOX);
			Message debug_message = new Message(outbox[0]);

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
