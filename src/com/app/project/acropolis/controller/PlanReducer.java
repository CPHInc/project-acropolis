package com.app.project.acropolis.controller;

import com.app.project.acropolis.model.ApplicationDB;

public class PlanReducer 
{
	public static final int LocalIncoming = 7;
	public static final int LocalOutgoing = 8;
	public static final int LocalReceived = 9;
	public static final int LocalSent = 10;
	public static final int LocalDownload = 11;
	public static final int LocalUpload = 12;
	public static final int RoamingIncoming = 13;
	public static final int RoamingOutgoing = 14;
	public static final int RoamingReceived = 15;
	public static final int RoamingSent = 16;
	public static final int RoamingDownload = 17;
	public static final int RoamingUpload = 18;
	public static final int RoamingQuota = 19;
	//com.app.project.acropolis.controller.PlanReducer.PLANEND
	final static long PlanEnd_GUID = 0xcace26796909dc44L;

	//Big-O(n)
	public PlanReducer(int from,long value)
	{
		switch(from)
		{
		case LocalIncoming:
		{
			if(!ApplicationDB.getValue(ApplicationDB.PlanIncoming).equals(""))
			{
				int plan = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.PlanIncoming)).intValue();
				plan -= (int)value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.PlanIncoming);
			}
		};
		case LocalOutgoing:
		{
			if(!ApplicationDB.getValue(ApplicationDB.PlanOutgoing).equals(""))
			{
				int plan = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.PlanOutgoing)).intValue();
				plan -= (int)value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.PlanOutgoing);
			}
		};
		case LocalReceived:
		{
			if(!ApplicationDB.getValue(ApplicationDB.PlanReceived).equals(""))
			{
				int plan = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.PlanReceived)).intValue();
				plan -= (int)value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.PlanReceived);
			}
		};
		case LocalSent:
		{
			if(!ApplicationDB.getValue(ApplicationDB.PlanSent).equals(""))
			{
				int plan = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.PlanSent)).intValue();
				plan -= (int)value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.PlanSent);
			}
		};
		case LocalDownload:
		{
			if(!ApplicationDB.getValue(ApplicationDB.PlanDownload).equals(""))
			{
				long plan = Long.parseLong(ApplicationDB.getValue(ApplicationDB.PlanDownload));
				plan -= value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.PlanDownload);
			}
		};
		case LocalUpload:
		{
			if(!ApplicationDB.getValue(ApplicationDB.PlanUpload).equals(""))
			{
				long plan = Long.parseLong(ApplicationDB.getValue(ApplicationDB.PlanUpload));
				plan -= value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.PlanUpload);
			}
		};
		case RoamingIncoming:
		{
			if(!ApplicationDB.getValue(ApplicationDB.RoamingPlanIncoming).equals(""))
			{
				int plan = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingPlanIncoming)).intValue();
				plan -=(int)value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.RoamingPlanIncoming);
			}
		};
		case RoamingOutgoing:
		{
			if(!ApplicationDB.getValue(ApplicationDB.RoamingPlanOutgoing).equals(""))
			{
				int plan = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingPlanOutgoing)).intValue();
				plan -= value;
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.RoamingPlanOutgoing);
			}
		};
		case RoamingReceived:
		{
			if(!ApplicationDB.getValue(ApplicationDB.RoamingPlanReceived).equals(""))
			{
				int plan = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingPlanReceived)).intValue();
				plan -=(int)value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.RoamingPlanReceived);
			}
		};
		case RoamingSent:
		{
			if(!ApplicationDB.getValue(ApplicationDB.RoamingPlanSent).equals(""))
			{
				int plan = Integer.valueOf(ApplicationDB.getValue(ApplicationDB.RoamingPlanSent)).intValue();
				plan -=(int)value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.RoamingPlanSent);
			}
		};
		case RoamingDownload:
		{
			if(!ApplicationDB.getValue(ApplicationDB.RoamingPlanDownload).equals(""))
			{
				long plan = Long.parseLong(ApplicationDB.getValue(ApplicationDB.RoamingPlanDownload));
				plan -= value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.RoamingPlanDownload);
			}
		};
		case RoamingUpload:
		{
			if(!ApplicationDB.getValue(ApplicationDB.RoamingPlanUpload).equals(""))
			{
				long plan = Long.parseLong(ApplicationDB.getValue(ApplicationDB.RoamingPlanUpload));
				plan -= value;
				if(plan==0)
					PlanReached();
				ApplicationDB.setValue(String.valueOf(plan), ApplicationDB.RoamingPlanUpload);
			}
		};
		}

	}
	
//	public boolean CheckRoamingPlan()
//	{
//		return ApplicationDB.getValue(ApplicationDB.RoamingQuota).equalsIgnoreCase("true");
//	}
	
	public static void PlanReached()
	{
		new ServerChannel();
//		ApplicationManager.getApplicationManager().postGlobalEvent(PlanEnd_GUID);
	}

}