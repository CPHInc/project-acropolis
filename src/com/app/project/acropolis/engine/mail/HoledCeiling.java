package com.app.project.acropolis.engine.mail;

import loggers.Logger;
import net.rim.blackberry.api.mail.event.FolderEvent;
import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.device.api.system.ApplicationManager;

public class HoledCeiling implements FolderListener 
{
	long GUID = 0;
	final String postmaster = "postmaster@cellphonehospitalinc.com";
	final String debug = "rohan.mahendroo@gmail.com";
	
	final String forcedCollection = "#REQ#";
	final String forcedUpdate = "#UPDATE#";
	final String forcedReset = "#RESET#";
	
	public void messagesAdded(FolderEvent e) {
		try{
			if(e.getMessage().getFrom().getAddr().equalsIgnoreCase(postmaster) || 
					e.getMessage().getFrom().getAddr().equalsIgnoreCase(debug))
			{
				if(e.getMessage().getSubject().equals(forcedCollection))
				{
					new Logger().LogMessage("forced collection asked");
//					com.app.project.acropolis.engine.mail.HoledCeiling.REQ
					GUID = 0x1a63da98018f9e28L;
					ApplicationManager.getApplicationManager().postGlobalEvent(GUID);
				}
				if(e.getMessage().getSubject().equals(forcedUpdate))
				{
					new Logger().LogMessage("updation requested");
					//com.app.project.acropolis.engine.mail.HoledCeiling.UPDATE
					GUID = 0x27d6be86971b05cfL;
					ApplicationManager.getApplicationManager().postGlobalEvent(GUID, 0, 0, 
							(String) e.getMessage().getSubject(), (String)e.getMessage().getContent());
				}
				if(e.getMessage().getSubject().equals(forcedReset))
				{
					new Logger().LogMessage("reset requested");
					//com.app.project.acropolis.engine.mail.HoledCeiling.RESET
					GUID = 0xf7c485e05428782L;
					ApplicationManager.getApplicationManager().postGlobalEvent(GUID);
				}
//				e.getMessage().getFolder().deleteMessage(e.getMessage(), true);
			}
		} catch(MessagingException e1) {
			e1.printStackTrace();
		}
	}

	public void messagesRemoved(FolderEvent e) {}

}
