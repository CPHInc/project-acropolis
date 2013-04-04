package com.app.project.acropolis.model;

import java.util.Enumeration;

import loggers.DBLogger;
import loggers.Logger;
import net.rim.blackberry.api.phone.Phone;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.MultiMap;

public class ApplicationDatabase 
{
	final long PERSIST_KEY = 0x3a090f86b9137748L;

	/*MULTIMAP args*/
	final int APP_LAUNCH_HASHTABLECAP = 2;
	final int APP_LAUNCH_VECTORCAP = 2;
	final int LOCAL_USAGE_HASHTABLECAP = 13;
	final int LOCAL_USAGE_VECTORCAP = 13;
	final int LOCAL_PLAN_HASHTABLECAP = 9;
	final int LOCAL_PLAN_VECTORCAP = 9;
	final int ROAMING_USAGE_HASHTABLECAP = 13;
	final int ROAMING_USAGE_VECTORCAP = 13;
	final int ROAMING_PLAN_HASHTABLECAP = 7;
	final int ROAMING_PLAN_VECTORCAP = 7;
	final int SERVER_CMDS_HASHTABLECAP = 8;
	final int SERVER_CMDS_VECTORCAP = 8;
	
	final String[] AppLaunch_MAPKEYS = {"FIRST_LAUNCH","CONTINUATION"};
	String[] LocalUsage_MapKeys = {"PhoneNumber","Roaming","Latitude","Longitude",
			"FixAck","FixDeviceTime","FixServerTime","Incoming",
			"Outgoing","Download","Upload","Received","Sent"};
	String[] RoamingUsage_MapKeys = {"PhoneNumber","Roaming","Latitude","Longitude",
			"FixAck","FixDeviceTime","FixServerTime","Incoming",
			"Outgoing","Download","Upload","Received","Sent"};
	String[] LocalPlan_MapKeys = {"PhoneNumber","Roaming_Quota","BillingDate",
			"IncomingPlan","OutgoingPlan","DownloadPlan","UploadPlan","Received","Sent"};
	String[] RoamingPlan_MapKeys = {"PhoneNumber","RoamingIncomingPlan",
			"RoamingOutgoingPlan","RoamingDownloadPlan","RoamingUploadPlan",
			 "RoamingReceivedPlan","RoamingSentPlan"}; 
	String[] ServerCommands_MapKeys = {};
	
	String[] AppLaunch_VALUES = {"true","false"};
	String[] LocalUsageDUMMY = {Phone.getDevicePhoneNumber(true),"false","0","0","false","0","0","0","0","0","0","0","0"};
	String[] LocalPlanDUMMY = {Phone.getDevicePhoneNumber(true),"false","0","0","0","0","0","0","0"};
	String[] RoamingUsageDUMMY = {Phone.getDevicePhoneNumber(true),"false","0","0","false","0","0","0","0","0","0","0","0"};
	String[] RoamingPlanDUMMY = {Phone.getDevicePhoneNumber(true),"0","0","0","0","0","0"};
	String[] ServerCommandsDUMMY = {"0","0","0","0","0","0","0","0"};
	MultiMap AppLaunchMap = new MultiMap(APP_LAUNCH_HASHTABLECAP,APP_LAUNCH_VECTORCAP);
	MultiMap LocalUsageMap = new MultiMap(LOCAL_USAGE_HASHTABLECAP,LOCAL_USAGE_VECTORCAP);
	MultiMap LocalPlanMap = new MultiMap(LOCAL_PLAN_HASHTABLECAP,LOCAL_PLAN_VECTORCAP);
	MultiMap RoamingUsageMap = new MultiMap(ROAMING_USAGE_HASHTABLECAP,ROAMING_USAGE_VECTORCAP);
	MultiMap RoamingPlanMap = new MultiMap(ROAMING_PLAN_HASHTABLECAP,ROAMING_PLAN_VECTORCAP);
	MultiMap ServerCommandsMap = new MultiMap(SERVER_CMDS_HASHTABLECAP,SERVER_CMDS_VECTORCAP);
	
	public ApplicationDatabase()
	{
		new DBLogger().LogMessage(">>ApplicationDatabase<<");
	}
	
	public class ApplicationLaunchRecorder
	{
		final long KEY = 0x332c1c9a3dda75f2L;
		PersistentObject persist;
		
		public ApplicationLaunchRecorder()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				AppLaunchMap.add(AppLaunch_MAPKEYS[0], AppLaunch_VALUES[0]);
				AppLaunchMap.add(AppLaunch_MAPKEYS[1], AppLaunch_VALUES[1]);
				persist.setContents((MultiMap)AppLaunchMap);
				persist.commit();
			}
		}
	}
	
	public class LocalUsageDB
	{
		final long KEY = 0xf96b407391de827bL;
		PersistentObject persist;
		
		public LocalUsageDB()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				if(LocalUsageMap.isEmpty())
				{
					for(int i=0;i<LocalUsage_MapKeys.length;i++)
					{
						LocalUsageMap.add(LocalUsage_MapKeys[i], LocalUsageDUMMY[i]);
					}
				}
				persist.setContents(LocalUsageMap);
				persist.commit();
			}
		}
		
		public boolean setValue(String key,String value)
		{
			MultiMap fetchedMap = (MultiMap) persist.getContents();
			synchronized(persist)
			{
				fetchedMap.removeKey(key);
				fetchedMap.add(key, value);
				persist.setContents(fetchedMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean setValues(String[] keys,String[] values)
		{
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
				fetchedMap.clear();
				for(int i=0;i<keys.length;i++)
				{
					fetchedMap.add(keys[i], values[i]);
				}
			}
			return true;
		}
		
		public String getValue(String key)
		{
			String collected = "";
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
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
		
		public String[] getValues(String[] keys)
		{
			String[] collected = new String[100];
			int counter = 0;
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap) persist.getContents();
				Enumeration enum_values = fetchedMap.elements();
				while(enum_values.hasMoreElements())
				{
					collected[counter] = (String) enum_values.nextElement();
				}
			}
			return collected;
		}
		
		public boolean reset()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				persist.setContents(LocalUsageMap);
				persist.commit();
				
			}
			return true;
		}
		
		public boolean destroy()
		{
			PersistentStore.destroyPersistentObject(KEY);
			return true;
		}
	}
	
	
	public class LocalPlanDB
	{
		final long KEY = 0xe8fc700e98b0ff2dL;
		PersistentObject persist;
		public LocalPlanDB()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				if(LocalPlanMap.isEmpty())
				{
					for(int i=0;i<LocalPlan_MapKeys.length;i++)
					{
						LocalPlanMap.add(LocalPlan_MapKeys[i], LocalPlanDUMMY[i]);
					}
				}
				persist.setContents(LocalPlanMap);
				persist.commit();
			}
		}
		
		public boolean setValue(String key,String value)
		{
			MultiMap fetchedMap = (MultiMap) persist.getContents();
			synchronized(persist)
			{
				fetchedMap.add(key, value);
				persist.setContents(fetchedMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean setValues(String[] keys,String[] values)
		{
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
				fetchedMap.clear();
				for(int i=0;i<keys.length;i++)
				{
					fetchedMap.add(keys[i], values[i]);
				}
			}
			return true;
		}
		
		public String getValue(String key)
		{
			String collected = "";
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
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
		
		public String[] getValues(String[] keys)
		{
			String[] collected = new String[100];
			int counter = 0;
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap) persist.getContents();
				Enumeration enum_values = fetchedMap.elements();
				while(enum_values.hasMoreElements())
				{
					collected[counter] = (String) enum_values.nextElement();
				}
			}
			return collected;
		}
		
		public boolean reset()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				persist.setContents(LocalPlanMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean destroy()
		{
			PersistentStore.destroyPersistentObject(KEY);
			return true;
		}
		
	}
	
	public class RoamingUsageDB
	{
		final long KEY = 0x7ada9124c328e0eL;
		PersistentObject persist;
		public RoamingUsageDB()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				if(RoamingUsageMap.isEmpty())
				{
					for(int i=0;i<RoamingUsage_MapKeys.length;i++)
					{
						RoamingUsageMap.add(RoamingUsage_MapKeys[i], RoamingUsageDUMMY[i]);
					}
				}
				persist.setContents(RoamingUsageMap);
				persist.commit();
			}
		}
		
		public boolean setValue(String key,String value)
		{
			MultiMap fetchedMap = (MultiMap) persist.getContents();
			synchronized(persist)
			{
				fetchedMap.add(key, value);
				persist.setContents(fetchedMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean setValues(String[] keys,String[] values)
		{
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
				fetchedMap.clear();
				for(int i=0;i<keys.length;i++)
				{
					fetchedMap.add(keys[i], values[i]);
				}
			}
			return true;
		}
		
		public String getValue(String key)
		{
			String collected = "";
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
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
		
		public String[] getValues(String[] keys)
		{
			String[] collected = new String[100];
			int counter = 0;
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap) persist.getContents();
				Enumeration enum_values = fetchedMap.elements();
				while(enum_values.hasMoreElements())
				{
					collected[counter] = (String) enum_values.nextElement();
				}
			}
			return collected;
		}
		
		public boolean reset()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				persist.setContents(RoamingUsageMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean destroy()
		{
			PersistentStore.destroyPersistentObject(KEY);
			return true;
		}
	}
	
	public class RoamingPlanDB
	{
		final long KEY = 0x2e437f70003868f3L;
		PersistentObject persist;
		public RoamingPlanDB()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				if(RoamingPlanMap.isEmpty())
				{
					for(int i=0;i<RoamingPlan_MapKeys.length;i++)
					{
						RoamingPlanMap.add(RoamingPlan_MapKeys[i], RoamingPlanDUMMY[i]);
					}
				}
				persist.setContents(RoamingPlanMap);
				persist.commit();
			}
		}
		
		public boolean setValue(String key,String value)
		{
			MultiMap fetchedMap = (MultiMap) persist.getContents();
			synchronized(persist)
			{
				fetchedMap.add(key, value);
				persist.setContents(fetchedMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean setValues(String[] keys,String[] values)
		{
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
				fetchedMap.clear();
				for(int i=0;i<keys.length;i++)
				{
					fetchedMap.add(keys[i], values[i]);
				}
			}
			return true;
		}
		
		public String getValue(String key)
		{
			String collected = "";
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
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
		
		public String[] getValues(String[] keys)
		{
			String[] collected = new String[100];
			int counter = 0;
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap) persist.getContents();
				Enumeration enum_values = fetchedMap.elements();
				while(enum_values.hasMoreElements())
				{
					collected[counter] = (String) enum_values.nextElement();
				}
			}
			return collected;
		}
		
		public boolean reset()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				persist.setContents(RoamingPlanMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean destroy()
		{
			PersistentStore.destroyPersistentObject(KEY);
			return true;
		}
		
	}
	
	public class ServerCommandsDB
	{
		final long KEY = 0xb19b31db67c0f8eL;
		PersistentObject persist;
		public ServerCommandsDB()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				if(ServerCommandsMap.isEmpty())
				{
					for(int i=0;i<ServerCommands_MapKeys.length;i++)
					{
						ServerCommandsMap.add(ServerCommands_MapKeys[i], ServerCommandsDUMMY[i]);
					}
				}
				persist.setContents(ServerCommandsMap);
				persist.commit();
			}
		}
		
		public boolean setValue(String key,String value)
		{
			MultiMap fetchedMap = (MultiMap) persist.getContents();
			synchronized(persist)
			{
				fetchedMap.add(key, value);
				persist.setContents(fetchedMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean setValues(String[] keys,String[] values)
		{
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
				fetchedMap.clear();
				for(int i=0;i<keys.length;i++)
				{
					fetchedMap.add(keys[i], values[i]);
				}
			}
			return true;
		}
		
		public String getValue(String key)
		{
			String collected = "";
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap)persist.getContents();
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
		
		public String[] getValues(String[] keys)
		{
			String[] collected = new String[100];
			int counter = 0;
			synchronized(persist)
			{
				MultiMap fetchedMap = (MultiMap) persist.getContents();
				Enumeration enum_values = fetchedMap.elements();
				while(enum_values.hasMoreElements())
				{
					collected[counter] = (String) enum_values.nextElement();
				}
			}
			return collected;
		}
		
		public boolean reset()
		{
			persist = PersistentStore.getPersistentObject(KEY);
			synchronized(persist)
			{
				persist.setContents(ServerCommandsMap);
				persist.commit();
			}
			return true;
		}
		
		public boolean destroy()
		{
			PersistentStore.destroyPersistentObject(KEY);
			return true;
		}
	}
	
}