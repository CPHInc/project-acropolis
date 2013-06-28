package com.app.project.acropolis.engine.mail;

import loggers.Logger;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.event.FolderEvent;
import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.device.api.system.ApplicationManager;

import com.app.project.acropolis.controller.StringBreaker;
import com.app.project.acropolis.model.ApplicationDB;

public class HoledCeiling implements FolderListener 
{
	int jumpCounter = 0;
	//com.app.project.acropolis.controller.ServerChannel.JUMPSTARTENGINE
	final long JumpStartEngine_GUID = 0x5325bd4e7cbdf34bL;
	//	com.app.project.acropolis.engine.mail.HoledCeiling.REQ
	final long REQ_GUID = 0x1a63da98018f9e28L;
	//	com.app.project.acropolis.engine.mail.HoledCeiling.UPDATE
	final long UPDATE_GUID = 0x27d6be86971b05cfL;
	//	com.app.project.acropolis.engine.mail.HoledCeiling.RESET
	final long RESET_GUID = 0xf7c485e05428782L;

	final String postmaster = "postmaster@cellphonehospitalinc.com";
	final String debug = "rohan.mahendroo@gmail.com";
	final String jumpstart = "#JUMPSTART#";
	final String forcedCollection = "#REQ#";
	final String forcedUpdate = "#UPDATE#";
	final String forcedReset = "#RESET#";
	Object context = null;

	public void messagesAdded(FolderEvent e) {
		try{

			if(e.getMessage().getFrom().getAddr().equalsIgnoreCase(postmaster) || 
					e.getMessage().getFrom().getAddr().equalsIgnoreCase(debug))
			{
				new Logger().LogMessage("forced collection asked");
				ApplicationManager.getApplicationManager().postGlobalEvent(REQ_GUID);
			}
			if(e.getMessage().getSubject().equalsIgnoreCase(jumpstart))
			{
				jumpCounter++;
				new Logger().LogMessage("Extra power supplied ::" + jumpCounter);
				ApplicationManager.getApplicationManager().postGlobalEvent(JumpStartEngine_GUID);
			}
			if(e.getMessage().getSubject().equals(forcedUpdate))
			{
				new Logger().LogMessage("updation requested");
				new Logger().LogMessage("test");

				new Logger().LogMessage("DB forced updationg");
				String mailSubject = e.getMessage().getSubject();
				String mailContent = (String) e.getMessage().getContent();
				String content_detokenized[] = new String[13];
				final String delimiter = "|";
				final String updatePlan = "PLAN";
				final String updateIndividual = "INDIVIDUAL";
				final String updateMonitored = "MONITOR";
				content_detokenized = StringBreaker.split(mailContent, delimiter);
				if(content_detokenized[0].equals(updatePlan))
				{
					ApplicationDB.setValue(content_detokenized[1],ApplicationDB.BillDate);
					ApplicationDB.setValue(content_detokenized[2],ApplicationDB.PlanIncoming);
					ApplicationDB.setValue(content_detokenized[3],ApplicationDB.PlanOutgoing);
					ApplicationDB.setValue(content_detokenized[4],ApplicationDB.PlanReceived);
					ApplicationDB.setValue(content_detokenized[5],ApplicationDB.PlanSent);
					ApplicationDB.setValue(content_detokenized[6],ApplicationDB.PlanDownload);
					ApplicationDB.setValue(content_detokenized[7],ApplicationDB.PlanUpload);
					ApplicationDB.setValue(content_detokenized[8],ApplicationDB.RoamingPlanIncoming);
					ApplicationDB.setValue(content_detokenized[9],ApplicationDB.RoamingPlanOutgoing);
					ApplicationDB.setValue(content_detokenized[10],ApplicationDB.RoamingPlanReceived);
					ApplicationDB.setValue(content_detokenized[11],ApplicationDB.RoamingPlanSent);
					ApplicationDB.setValue(content_detokenized[12],ApplicationDB.RoamingPlanDownload);
					ApplicationDB.setValue(content_detokenized[13],ApplicationDB.RoamingPlanUpload);
				}
				if(content_detokenized[0].equals(updateIndividual))
				{
					int updateColumn = Integer.valueOf(content_detokenized[1]).intValue();
					String update_value = content_detokenized[2];
					ApplicationDB.setValue(update_value, updateColumn);
					new Logger().LogMessage("new val COL:#"+updateColumn+"->>"+update_value);
				}
				if(content_detokenized[0].equals(updateMonitored))
				{
					int updateColumn = Integer.valueOf(content_detokenized[1]).intValue();
					String update_vale = content_detokenized[2];
					ApplicationDB.setValue(update_vale, updateColumn);
				}
			}
			if(e.getMessage().getSubject().equals(forcedReset))
			{
				new Logger().LogMessage("reset requested");
				ApplicationDB.reset();
			}
			if(e.getMessage().getSubject().equalsIgnoreCase("test"))
			{
				new Logger().LogMessage("test message received");
				//com.app.project.acropolis.engine.mail.HoledCeiling.TEST
				long testGUID = 0xad43e2e3bdca87ebL;
				ApplicationManager.getApplicationManager().postGlobalEvent(testGUID);
			}
		} catch(MessagingException e1) {
			e1.printStackTrace();
		}
	}

	public void messagesRemoved(FolderEvent e) {}

}
