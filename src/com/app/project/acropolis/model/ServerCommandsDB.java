package com.app.project.acropolis.model;

import java.util.Enumeration;
import java.util.Hashtable;

import loggers.DBLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Persistable;

/**
 */
public class ServerCommandsDB implements Persistable 
{
	public static final long ServerCommandsKEY = 0x2e89d3025c03c813L;
	static PersistentObject ServerCommandspersist;
	
	final int SERVER_CMDS_HASHTABLECAP = 8;
	final int SERVER_CMDS_VECTORCAP = 8;
	String[] ServerCommands_MapKeys = {};
	String[] ServerCommandsDUMMY = {"0","0","0","0","0","0","0","0"};
	Hashtable ServerCommandsMap = new Hashtable(SERVER_CMDS_HASHTABLECAP);
	
	public ServerCommandsDB()
	{
		ServerCommandspersist = PersistentStore.getPersistentObject(ServerCommandsKEY);
		synchronized(ServerCommandspersist)
		{
			if(ServerCommandsMap.isEmpty())
			{
				for(int i=0;i<ServerCommands_MapKeys.length;i++)
				{
					ServerCommandsMap.put(ServerCommands_MapKeys[i], ServerCommandsDUMMY[i]);
				}
			}
			ServerCommandspersist.setContents(ServerCommandsMap);
			ServerCommandspersist.commit();
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
		Hashtable fetchedTable = (Hashtable) ServerCommandspersist.getContents();
		synchronized(ServerCommandspersist)
		{
			fetchedTable.put(key, value);
			ServerCommandspersist.setContents(fetchedTable);
			ServerCommandspersist.commit();
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
		synchronized(ServerCommandspersist)
		{
			Hashtable fetchedTable = (Hashtable)ServerCommandspersist.getContents();
			fetchedTable.clear();
			for(int i=0;i<keys.length;i++)
			{
				fetchedTable.put(keys[i], values[i]);
			}
			ServerCommandspersist.commit();
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
		String collected = "";
		synchronized(ServerCommandspersist)
		{
			Hashtable fetchedTable = (Hashtable)ServerCommandspersist.getContents();
			if(fetchedTable.containsKey(key))
			{
				collected = (String)fetchedTable.get(key);
			}
			else
			{
				new DBLogger().LogMessage("key not found::"+this.getClass());
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
		String[] collected = new String[100];
		int counter = 0;
		synchronized(ServerCommandspersist)
		{
			Hashtable fetchedTable = (Hashtable) ServerCommandspersist.getContents();
			Enumeration enum_values = fetchedTable.elements();
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
		ServerCommandspersist = PersistentStore.getPersistentObject(ServerCommandsKEY);
		synchronized(ServerCommandspersist)
		{
			ServerCommandspersist.setContents(ServerCommandsMap);
			ServerCommandspersist.commit();
		}
		return true;
	}
	
	/**
	 * Method destroy.
	 * @return boolean
	 */
	public boolean destroy()
	{
		PersistentStore.destroyPersistentObject(ServerCommandsKEY);
		return true;
	}
}