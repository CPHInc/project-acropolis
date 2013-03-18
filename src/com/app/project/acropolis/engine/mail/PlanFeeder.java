package com.app.project.acropolis.engine.mail;

import loggers.Logger;
import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.AddressException;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.Transport;
import net.rim.blackberry.api.mail.event.FolderEvent;
import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.blackberry.api.phone.Phone;

import com.app.project.acropolis.controller.StringBreaker;
import com.app.project.acropolis.model.PlanModelFactory;

public class PlanFeeder implements Runnable
{
	String in_Mail = "rohan@cellphonehospitalinc.com";
	String in_Mail_server = "postmaster@cellphonehospitalinc.com";
//	String in_Name = "Rohan K Mahendroo";
//	String in_Name_server = "postmaster";
	String device_Mail = "";
	String device_Name = "";
	int incoming_serverMail = 0;
	String incoming_subject = "";
	String incoming_content = "";
	String incomingDelimiter = "|";
	String[] planDatabaseColumns = {"billing_date","minutes","text","data","roam_quota","roam_min","roam_msg","roam_data"};
	String[] strPlanArray = new String[40];
	StringBreaker strBreak = new StringBreaker();
	PlanModelFactory thePlan;
	
	public void run()
	{
		new Logger().LogMessage(">>PlanFeeder<<");
		ReadMail();
	}
	
	public void SendREQ()
	{
		try {
			Session session = Session.getDefaultInstance();
			Folder[] folders = session.getStore().list(Folder.OUTBOX);
			Folder outbox = folders[0];
			Message message = new Message(outbox);
			String device_mail = session.getServiceConfiguration().getEmailAddress();
			String device_mail_name = session.getServiceConfiguration().getName();
			Address req_receiver = new Address("postmaster@cellphonehospitalinc,com","postmaster");
			Address req_receiver_debug = new Address("rohan.mahendroo@gmail.com","rohan");
			Address req_sender = new Address(device_mail,device_mail_name);

			message.setSubject("Plan Requested");
			message.setFrom(req_sender);
			message.addRecipients(Message.RecipientType.TO, new Address[] {req_receiver,req_receiver_debug});
			message.setContent("#REQ|"+Phone.getDevicePhoneNumber(false));
			Transport.send(message);
			new Logger().LogMessage("Plan requested!!");
			outbox.deleteMessage(message, true);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void ReadMail()
	{
		incoming_serverMail = 0;
		Session read_session = Session.getDefaultInstance();
		Folder[] read_folder = read_session.getStore().list(Folder.INBOX);
		Folder inbox = read_folder[0];
		Message read_message = new Message(inbox);
		
		device_Mail = read_session.getServiceConfiguration().getEmailAddress();
		device_Name = read_session.getServiceConfiguration().getName();
		inbox.addFolderListener(new FolderListener()
		{
			public void messagesAdded(FolderEvent e) {
				try{
					if(e.getType() == FolderEvent.MESSAGE_ADDED)
					{
						if(e.getMessage().getFrom().getAddr().equalsIgnoreCase(in_Mail) ||  
								e.getMessage().getFrom().getAddr().equalsIgnoreCase(in_Mail_server))
						{
							new Logger().LogMessage("Mobile Plan received!!");
							new Logger().LogMessage("inmail address:"+e.getMessage().getFrom().getAddr());
							incoming_serverMail = 1;
							incoming_subject = e.getMessage().getSubject();
							if( e.getMessage().getSubject().equalsIgnoreCase("Device Plan Details") )
							{
								incoming_content = e.getMessage().getBodyText();
								new Logger().LogMessage("Content::"+incoming_content);
								strPlanArray = strBreak.split(incoming_content, incomingDelimiter);
								for(int i=0;i<=strPlanArray.length;i++)
								{
									thePlan.UpdateData( planDatabaseColumns[i], strPlanArray[i] );
								}
							}
						}
					}
				} catch(MessagingException e1) {
					e1.printStackTrace();
					new Logger().LogMessage("Messaging Exception:"+e1.getClass()+"::"+e1.getMessage());
				}
			}

			public void messagesRemoved(FolderEvent e) 
			{}
		});
	}
	
	public int getIncomingServerMailAlert()
	{
		return incoming_serverMail;
	}
	
	public String getIncomingServerMailSubject()
	{
		return incoming_subject;
	}
	
	public String getIncomingServerMailContent()
	{
		return incoming_content;
	}
}
