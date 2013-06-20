package com.app.project.acropolis.model;

import java.util.Vector;

import loggers.DBLogger;
import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Persistable;

import com.app.project.acropolis.engine.monitor.LocationCode;

/**
 */
public final class ApplicationDB implements Persistable
{
	static final long KEY = 0xeff98108be81b2b8L;
	//	static final long KEY = 0x81a1a23d4b2ed297L;
	//KEY === com.app.project.acropolis.model.ApplicationDB.version1
	static PersistentObject persist;

	public static final int PhoneNumber = 0;
	public static final int Roaming = 1;
	public static final int Latitude = 2;
	public static final int Longitude = 3;
	public static final int ACK = 4;
	public static final int FixDeviceTime = 5;
	public static final int FixServerTime = 6;
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
	public static final int BillDate = 20;	//dd
	public static final int PlanIncoming = 21;
	public static final int PlanOutgoing = 22;
	public static final int PlanReceived = 23;
	public static final int PlanSent = 24;
	public static final int PlanDownload = 25;
	public static final int PlanUpload = 26;
	public static final int RoamingPlanIncoming = 27;
	public static final int RoamingPlanOutgoing = 28;
	public static final int RoamingPlanReceived = 29;
	public static final int RoamingPlanSent = 30;
	public static final int RoamingPlanDownload = 31;
	public static final int RoamingPlanUpload = 32;

	final static int VectorSize = 33;
	public static Vector appVector = new Vector(VectorSize);

	public ApplicationDB()
	{
		new DBLogger().LogMessage(">>-ApplicationDatabase-<<");
		persist = PersistentStore.getPersistentObject(KEY);
		synchronized(persist)
		{
			if(persist.getContents()==null)
			{
				for(int i=0;i<appVector.capacity();++i)
				{
					appVector.addElement(new String("0"));
					//						appVector.setElementAt(new String("0"),i);
				}
				persist.setContents(appVector);
				persist.commit();
			}
			else
			{
				appVector = (Vector)persist.getContents();
			}
		}

	}

	/**
	 * Method setValue.
	 * @param value String
	 * @param id int
	 * @return boolean
	 */
	public static boolean setValue(String value,int id)
	{
		persist = PersistentStore.getPersistentObject(KEY);
		synchronized(persist)
		{
			Vector vector = (Vector)persist.getContents();
			vector.setElementAt(value,id);
			persist.setContents(vector);
			persist.commit();
		}
		return true;
	}

	/**
	 * Method getValue.
	 * @param id int
	 * @return String
	 */
	public static String getValue(int id)
	{
		String data = "";
		persist = PersistentStore.getPersistentObject(KEY);
		synchronized(persist)
		{
			Vector vector = (Vector) persist.getContents();
			data = (String) vector.elementAt(id);
		}
		return data;
	}

	public static void reset()
	{
		setDumbValues();
		new Logger().LogMessage("full monty");
	}

	public static boolean isEmpty()
	{
		boolean empty = false;
		destroyOld();
		persist = PersistentStore.getPersistentObject(KEY);
		synchronized(persist)
		{
			if(persist.getContents()==null)
			{
				setDumbValues();
				empty=true;
			}
			else
				empty=false;
		}
		return empty;
	}

	public static void setDumbValues()
	{
		persist = PersistentStore.getPersistentObject(KEY);
		synchronized(persist)
		{
			Vector vector = new Vector(VectorSize);
			for(int i=0;i<vector.capacity();i++)
			{
				vector.addElement("0");
			}
			vector.setElementAt((String)Phone.getDevicePhoneNumber(true),ApplicationDB.PhoneNumber);
			if(LocationCode.Check_NON_CAN_Operator())
				vector.setElementAt("true", ApplicationDB.Roaming);
			else
				vector.setElementAt("false", ApplicationDB.Roaming);
			persist.setContents(vector);
			persist.forceCommit();
		}
	}

	public static boolean destroyOld()
	{
		//new key = 0xeff98108be81b2b8 <--vector
		long oldkey = 0x3a090f86b9137748L;	//<--MultiMap
		PersistentStore.destroyPersistentObject(oldkey);
		return true;
	}
	
}