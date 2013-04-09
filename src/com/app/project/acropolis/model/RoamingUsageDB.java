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
public class RoamingUsageDB implements Persistable
{
	public static final long RoamingUsageKEY = 0x44098e743b5797f1L;
	static PersistentObject RoamingUsagepersist;

	final int ROAMING_USAGE_HASHTABLECAP = 13;
	final int ROAMING_USAGE_VECTORCAP = 13;
	String[] RoamingUsage_MapKeys = {"PhoneNumber","Roaming","Latitude","Longitude",
			"FixAck","FixDeviceTime","FixServerTime","Incoming",
			"Outgoing","Download","Upload","Received","Sent"};
	String[] RoamingUsageDUMMY = {Phone.getDevicePhoneNumber(true),"false","0","0","false","0","0","0","0","0","0","0","0"};
	MultiMap RoamingUsageMap = new MultiMap(ROAMING_USAGE_HASHTABLECAP,ROAMING_USAGE_VECTORCAP);
	
	public RoamingUsageDB()
	{
		RoamingUsagepersist = PersistentStore.getPersistentObject(RoamingUsageKEY);
		synchronized(RoamingUsagepersist)
		{
			if(RoamingUsageMap.isEmpty())
			{
				for(int i=0;i<RoamingUsage_MapKeys.length;i++)
				{
					RoamingUsageMap.add(RoamingUsage_MapKeys[i], RoamingUsageDUMMY[i]);
				}
			}
			RoamingUsagepersist.setContents(RoamingUsageMap);
			RoamingUsagepersist.commit();
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
		RoamingUsagepersist = PersistentStore.getPersistentObject(RoamingUsageKEY);
		MultiMap fetchedMap = (MultiMap) RoamingUsagepersist.getContents();
		synchronized(RoamingUsagepersist)
		{
			fetchedMap.add(key, value);
			RoamingUsagepersist.setContents(fetchedMap);
			RoamingUsagepersist.commit();
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
		RoamingUsagepersist = PersistentStore.getPersistentObject(RoamingUsageKEY);
		synchronized(RoamingUsagepersist)
		{
			MultiMap fetchedMap = (MultiMap)RoamingUsagepersist.getContents();
			fetchedMap.clear();
			for(int i=0;i<keys.length;i++)
			{
				fetchedMap.add(keys[i], values[i]);
			}
			RoamingUsagepersist.commit();
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
		RoamingUsagepersist = PersistentStore.getPersistentObject(RoamingUsageKEY);
		String collected = "";
		synchronized(RoamingUsagepersist)
		{
			MultiMap fetchedMap = (MultiMap)RoamingUsagepersist.getContents();
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
		RoamingUsagepersist = PersistentStore.getPersistentObject(RoamingUsageKEY);
		String[] collected = new String[100];
		int counter = 0;
		synchronized(RoamingUsagepersist)
		{
			MultiMap fetchedMap = (MultiMap) RoamingUsagepersist.getContents();
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
		RoamingUsagepersist = PersistentStore.getPersistentObject(RoamingUsageKEY);
		synchronized(RoamingUsagepersist)
		{
			RoamingUsagepersist.setContents(RoamingUsageMap);
			RoamingUsagepersist.commit();
		}
		return true;
	}
	
	/**
	 * Method destroy.
	 * @return boolean
	 */
	public boolean destroy()
	{
		PersistentStore.destroyPersistentObject(RoamingUsageKEY);
		return true;
	}
}
