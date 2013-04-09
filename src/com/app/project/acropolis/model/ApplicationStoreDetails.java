package com.app.project.acropolis.model;

import java.util.Vector;

import loggers.DBLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Persistable;

/**
 */
public class ApplicationStoreDetails
{
	final int VECTOR_SIZE = 33;
	static Vector vector;
	static final long PERSIST_KEY = 0xae5da5e4fb87089L;
	static PersistentObject persist;
	
	public ApplicationStoreDetails()
	{
		new DBLogger().LogMessage(">>ApplicationStoreDetails<<");
		persist = PersistentStore.getPersistentObject(PERSIST_KEY);
		synchronized(persist)
		{
			if(persist.getContents() == null)
			{
				persist.setContents(new Vector(33));
				persist.commit();
			}
			else
			{
				vector = new Vector(VECTOR_SIZE);
				if(vector.isEmpty())
				{
					for(int i=0;i<vector.capacity();++i)
					{
						vector.addElement(new String("0.0"));
//						vector.setElementAt(new String("0"),i);
					}
				}
			}
		}
		vector = new Vector(33);
		vector = (Vector) persist.getContents();
	}
	
	/**
	 * Method SupplyVector.
	 * @return Vector
	 */
	public Vector SupplyVector()
	{
		persist = PersistentStore.getPersistentObject(PERSIST_KEY);
		synchronized(persist)
		{
			if(persist.getContents()==null)
			{
			
			}
			else
			{
				vector = (Vector)persist.getContents();
			}
//			vector = new Vector();
		}
		return vector;
	}
	
	/**
	 * Method CommitVector.
	 * @param vEctor Vector
	 */
	public void CommitVector(Vector vEctor)
	{
		persist = PersistentStore.getPersistentObject(PERSIST_KEY);
//		vector = new Vector();
		vector = vEctor;
		synchronized(persist)
		{
			if(persist.getContents()==null)
			{
				persist.setContents(new Vector(VECTOR_SIZE));
				persist.commit();
			}
			else
			{
				persist.setContents(vector);
				persist.commit();
			}
		}
	}

}