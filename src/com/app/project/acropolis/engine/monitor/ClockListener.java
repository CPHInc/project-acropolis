package com.app.project.acropolis.engine.monitor;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import loggers.Logger;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.WLANInfo;

import com.app.project.acropolis.controller.LocalHandler;
import com.app.project.acropolis.controller.RadioStateListener;
import com.app.project.acropolis.controller.RoamingHandler;
import com.app.project.acropolis.model.ApplicationDB;

public class ClockListener implements RealtimeClockListener 
{

	public void clockUpdated() 
	{
//		new Logger().LogMessage("tester " + this.getClass().toString());
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
					new LocalHandler(false).run();
				else
					new RoamingHandler(false).run();
				ApplicationDB.reset();
				new Logger().LogMessage("Bill date reset");
			}
			else
			{
				RadioStateListener.IsItInNeed(true);
			}
		}
	}
}
