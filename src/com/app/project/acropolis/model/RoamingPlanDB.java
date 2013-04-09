package com.app.project.acropolis.model;

import java.util.Enumeration;

import loggers.DBLogger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.MultiMap;
import net.rim.device.api.util.Persistable;

/**
 */
public class RoamingPlanDB implements Persistable
{
	public static final long RoamingPlanKEY = 0xd450912cb9563b08L;
	static PersistentObject RoamingPlanpersist;
	
	final int ROAMING_PLAN_HASHTABLECAP = 7;
	final int ROAMING_PLAN_VECTORCAP = 7;
	String[] RoamingPlan_MapKeys = {"PhoneNumber","RoamingIncomingPlan",
			"RoamingOutgoingPlan","RoamingDownloadPlan","RoamingUploadPlan",
			 "RoamingReceivedPlan","RoamingSentPlan"}; 
	String[] RoamingPlanDUMMY = {Phone.getDevicePhoneNumber(true),"0","0","0","0","0","0"};
	MultiMap RoamingPlanMap = new MultiMap(ROAMING_PLAN_HASHTABLECAP,ROAMING_PLAN_VECTORCAP);
	
	public RoamingPlanDB()
	{
		RoamingPlanpersist = PersistentStore.getPersistentObject(RoamingPlanKEY);
		synchronized(RoamingPlanpersist)
		{
			if(RoamingPlanMap.isEmpty())
			{
				for(int i=0;i<RoamingPlan_MapKeys.length;i++)
				{
					RoamingPlanMap.add(RoamingPlan_MapKeys[i], RoamingPlanDUMMY[i]);
				}
			}
			RoamingPlanpersist.setContents(RoamingPlanMap);
			RoamingPlanpersist.commit();
		}
	}
	
	/**
	 * Method setValue.
	 * @param key String
	 * @param value String
	 * @return boolean
	 */
	public boolean setValue(String key,String value)
	{
		RoamingPlanpersist = PersistentStore.getPersistentObject(RoamingPlanKEY);
		MultiMap fetchedMap = (MultiMap) RoamingPlanpersist.getContents();
		synchronized(RoamingPlanpersist)
		{
			fetchedMap.add(key, value);
			RoamingPlanpersist.setContents(fetchedMap);
			RoamingPlanpersist.commit();
		}
		return true;
	}
	
	/**
	 * Method setValues.
	 * @param keys String[]
	 * @param values String[]
	 * @return boolean
	 */
	public boolean setValues(String[] keys,String[] values)
	{
		RoamingPlanpersist = PersistentStore.getPersistentObject(RoamingPlanKEY);
		synchronized(RoamingPlanpersist)
		{
			MultiMap fetchedMap = (MultiMap)RoamingPlanpersist.getContents();
			fetchedMap.clear();
			for(int i=0;i<keys.length;i++)
			{
				fetchedMap.add(keys[i], values[i]);
			}
			RoamingPlanpersist.commit();
		}
		return true;
	}
	
	/**
	 * Method getValue.
	 * @param key String
	 * @return String
	 */
	public String getValue(String key)
	{
		RoamingPlanpersist = PersistentStore.getPersistentObject(RoamingPlanKEY);
		String collected = "";
		synchronized(RoamingPlanpersist)
		{
			MultiMap fetchedMap = (MultiMap)RoamingPlanpersist.getContents();
			Enumeration enum_values = fetchedMap.elements(key);
			while(enum_values.hasMoreElements())
			{	
				if(fetchedMap.containsKey(key))
				{
					collected = (String)enum_values.nextElement().toString();
				}
				else
				{
					new DBLogger().LogMessage("key not found::"+this.getClass());
				}
			}
		}
		new DBLogger().LogMessage(collected);
		return collected;
	}
	
	/**
	 * Method getValues.
	 * @param keys String[]
	 * @return String[]
	 */
	public String[] getValues(String[] keys)
	{
		RoamingPlanpersist = PersistentStore.getPersistentObject(RoamingPlanKEY);
		String[] collected = new String[100];
		int counter = 0;
		synchronized(RoamingPlanpersist)
		{
			MultiMap fetchedMap = (MultiMap) RoamingPlanpersist.getContents();
			Enumeration enum_values = fetchedMap.elements();
			while(enum_values.hasMoreElements())
			{
				collected[counter] = (String) enum_values.nextElement();
			}
		}
		return collected;
	}
	
	/**
	 * Method reset.
	 * @return boolean
	 */
	public boolean reset()
	{
		RoamingPlanpersist = PersistentStore.getPersistentObject(RoamingPlanKEY);
		synchronized(RoamingPlanpersist)
		{
			RoamingPlanpersist.setContents(RoamingPlanMap);
			RoamingPlanpersist.commit();
		}
		return true;
	}
	
	/**
	 * Method destroy.
	 * @return boolean
	 */
	public boolean destroy()
	{
		PersistentStore.destroyPersistentObject(RoamingPlanKEY);
		return true;
	}
	
}
