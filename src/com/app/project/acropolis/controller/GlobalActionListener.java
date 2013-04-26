package com.app.project.acropolis.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import loggers.Logger;
import net.rim.blackberry.api.mail.ServiceConfiguration;
import net.rim.blackberry.api.mail.Session;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANInfo;

import com.app.project.acropolis.engine.monitor.LocationCode;
import com.app.project.acropolis.model.ApplicationDB;

public class GlobalActionListener implements GlobalEventListener 
{
	//com.app.project.acropolis.controller.PlanReducer.PLANEND
	final long PLAN_END_GUID = 0xcace26796909dc44L;
	//com.app.project.acropolis.engine.monitor.LocationCode.LocationListenerActivity.ACTIVATE
	final long LOCATION_LISTENER_ACTIVATE_GUID = 0xd5841d310496f925L;
	//com.app.project.acropolis.engine.monitor.LocationCode.LocationListenerActivity
	final long LOCATION_LISTENER_GUID = 0x79f17800d9a25207L;
	final long DATE_CHANGED_GUID = net.rim.device.api.util.DateTimeUtilities.GUID_DATE_CHANGED;
	final long SERVICE_BOOK_REMOVED = net.rim.device.api.servicebook.ServiceBook.GUID_SB_REMOVED;
	//com.app.project.acropolis.engine.mail.HoledCeiling.REQ
	final long Request_GUID = 0x1a63da98018f9e28L;
	//com.app.project.acropolis.engine.mail.HoledCeiling.UPDATE
	final long Update_GUID = 0x27d6be86971b05cfL;
	//com.app.project.acropolis.engine.mail.HoledCeiling.RESET
	final long Reset_GUID = 0xf7c485e05428782L;
	String mailSubject = "";
	String mailContent = "";
	String Latitude = "";
	String Longitude = "";
	
	public void eventOccurred(long arg0, int arg1, int arg2, Object arg3,
			Object arg4) {
		
		if(arg0 == PLAN_END_GUID)
		{
			new Logger().LogMessage("Plan ended");
			if(!LocationCode.Check_NON_CAN_Operator())
				new LocalHandler().CollectedData();
			else
				new RoamingHandler().CollectedData();
		}
		if(arg0 == DATE_CHANGED_GUID)
		{
			SimpleDateFormat sdf_date = new SimpleDateFormat("yyyyMMdd");
			TimeZone serverTimeZone = TimeZone.getTimeZone("GMT-04:00");
			Calendar calendar = Calendar.getInstance(serverTimeZone);
			calendar.setTime(new Date(System.currentTimeMillis()));
			String currentDate = sdf_date.format(calendar.getTime());
			if(currentDate.equalsIgnoreCase(ApplicationDB.getValue(ApplicationDB.BillDate)))
			{
				if ((RadioInfo.getActiveWAFs() == RadioInfo.WAF_3GPP) || 
						(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED))
				{
					RadioStateListener.IsItInNeed(false);
					if(!LocationCode.Check_NON_CAN_Operator())
						new LocalHandler().CollectedData();
					else
						new RoamingHandler().CollectedData();
					ApplicationDB.reset();
					new Logger().LogMessage("Bill date reset");
				}
				else
				{
					RadioStateListener.IsItInNeed(true);
				}
			}
		}
		if(arg0 == Request_GUID)
		{
			mailSubject = (String) arg3;
			new Logger().LogMessage("Handlers forced collection");
			if(!LocationCode.Check_NON_CAN_Operator())
				new LocalHandler().CollectedData();
			else
				new RoamingHandler().CollectedData();
		}
		if(arg0 == Update_GUID)
		{
			new Logger().LogMessage("DB forced updationg");
			mailSubject = (String) arg3;
			mailContent = (String) arg4;
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
			}
			if(content_detokenized[0].equals(updateMonitored))
			{
				int updateColumn = Integer.valueOf(content_detokenized[1]).intValue();
				String update_vale = content_detokenized[2];
				ApplicationDB.setValue(update_vale, updateColumn);
			}
		}
		if(arg0 == Reset_GUID)
		{
			new Logger().LogMessage("DB forced reset");
			mailSubject = (String) arg3;
			mailContent = (String) arg4;
			ApplicationDB.reset();
		}
	}

}
