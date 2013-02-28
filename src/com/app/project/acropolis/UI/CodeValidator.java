package com.app.project.acropolis.UI;

import com.app.project.acropolis.model.ModelFactory;

public class CodeValidator
{

	final int NO_BATTERY = 8388608;
	final int LOW_BATTERY = 268435456;
	final int NO_RADIO_BATTERY = 16384;
	final int CHARGING_BATTERY = 1;
	final int CHARGING_AC_BATTERY = 16;
	final int CHANGE_LEVEL_BATTERY = 2;
	final int EXTERNAL_POWER = 4;
	
	public ModelFactory theModel;
	
	public CodeValidator()
	{
		new Logger().LogMessage("--->CodeValidator()<---");
		
		theModel = new ModelFactory();
		theModel.UpdateData("received","45");
		theModel.SelectData("received");
		
		new CodesHandler();
	}
	
}
