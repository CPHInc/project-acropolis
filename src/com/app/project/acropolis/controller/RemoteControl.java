package com.app.project.acropolis.controller;

import loggers.Logger;

import com.app.project.acropolis.engine.mail.PlanFeeder;

/**
 * @author Rohan Kumar Mahendroo <rohan.mahendroo@gmail.com>
 */
public class RemoteControl implements Runnable
{
	PlanFeeder plan;//= new PlanFeeder();
	
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
				//request collected data mail
			}
			if(plan.getIncomingServerMailSubject().equalsIgnoreCase(serverUpdateLiteral))
			{
				//updates counters to specified value
			}
			if(plan.getIncomingServerMailSubject().equalsIgnoreCase(serverPromptMsgLiteral))
			{
				//prompt on device
			}
		}
		
		
	}
	
}
