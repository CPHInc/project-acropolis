package com.app.project.acropolis.controller;

import loggers.Logger;

import com.app.project.acropolis.engine.mail.PlanFeeder;
import com.app.project.acropolis.model.PlanModelFactory;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 */
public class RemoteControl implements Runnable
{
	PlanFeeder plan;//= new PlanFeeder();
	PlanModelFactory thePlan;
	StringBreaker breaker;
	
	final int serverRequested = 1;
	final String serverRequestLiteral = "#REQ#";
	final String serverSendLiteral = "#SEND#";
	final String serverUpdateLiteral = "#UPDATE#";
	String serverUpdateParam = "";
	String serverUpdateValue = "";
	final String serverPromptLiteral = "#PROMPT#";
	String serverPromptMsgLiteral = "";
	
	/**
	 * Allows server to control application remotely from server
	 * according to set of word tags or literals remote activation starts
	 */
	public RemoteControl()
	{
		new Logger().LogMessage(">>RemoteControl<<");
//		if(!new Thread(plan).isAlive())
//		{
//			new Thread(plan).start();
//		}
	}
	
	public void run()
	{
		
		if(plan.getIncomingServerMailAlert() == serverRequested)
		{
			if(plan.getIncomingServerMailSubject().equalsIgnoreCase(serverRequestLiteral))
			{
				new Logger().LogMessage("Manual collection requested");
				new CodesHandler();				//manually call CodesHandler()
				//request collected data mail
			}
			if(plan.getIncomingServerMailSubject().equalsIgnoreCase(serverUpdateLiteral))
			{
				new Logger().LogMessage("Update application values..");
				thePlan = new PlanModelFactory();
				String column_toUpdate = StringBreaker.split(plan.getIncomingServerMailContent() , "|")[0];
				String data_toUpdated = StringBreaker.split(plan.getIncomingServerMailContent(),"|")[1];
				thePlan.UpdateData(column_toUpdate, data_toUpdated);
				//updates counters to specified value
			}
			if(plan.getIncomingServerMailSubject().equalsIgnoreCase(serverPromptMsgLiteral))
			{
				new Logger().LogMessage("Prompted acquired");
				serverPromptMsgLiteral = plan.getIncomingServerMailContent();
				//prompt on device
			}
		}
		
	}
	
	public void PromptMsg()
	{
		
	}
	
}
