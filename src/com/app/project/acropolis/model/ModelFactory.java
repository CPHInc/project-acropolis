package com.app.project.acropolis.model;

import java.util.Enumeration;

import javax.microedition.io.file.FileSystemRegistry;

import loggers.DBLogger;
import loggers.Logger;
import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseIOException;
import net.rim.device.api.database.DatabasePathException;
import net.rim.device.api.database.Statement;
import net.rim.device.api.io.IDNAException;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 */
public class ModelFactory {

	public final String ACTIVE_DB = "acropolis.db";
	public final String DB_NAME = "activity_acropolis";
	
	public Database db;
	
	public boolean SDCardMounted = false;
	public boolean eMMCMounted = false;
	public String SDCardPath = "file:///SDCard/Acropolis/database/"+ACTIVE_DB;
	public String eMMCPath = "file:///store/home/user/"+ACTIVE_DB;
	public String dbPath = "";
	public URI db_URI = null;
	
	public String update_query = "update "+DB_NAME+" set ";
//	public String update_query = "update activity_acropolis set sent=\'4\'";
	public String select_query = "select ";
	public String select_part2 = " from "+DB_NAME;
	public String select_all = "select * from "+DB_NAME;

	public boolean eMMCFound = false;
	public boolean SDFound = false;
	
	public ModelFactory()
	{
		new DBLogger().LogMessage(">>-ModelFactory-<<");
	}
	
	/**
	 * Update db with specified values
	 * @param column
	 * @param data
	 */
	public void UpdateData(String column,String data)
	{
		OpenDB();
		try{
			db.beginTransaction();
			Statement st_update = db.createStatement(update_query + column + " = \'" + data + "\'");
			st_update.prepare();
			st_update.execute();
			st_update.close();
			db.commitTransaction();
		} catch (DatabaseException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DatabaseException:"+e.getClass()+"::"+e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
		CloseDB();
	}
	
	/**
	 * Method SelectData.
	 * @param column String
	 * @return String
	 */
	public String SelectData(String column)
	{
		String collected = "";
		int colIndex = 0;
		OpenDB();
		try{
//			db.beginTransaction();
			Statement st_select = db.createStatement(select_query + column +select_part2);
			st_select.prepare();
			Cursor cursor = st_select.getCursor();
			cursor.first();
			colIndex = cursor.getColumnIndex(column);
			collected = cursor.getRow().getString(colIndex);
			cursor.close();
			st_select.close();
//			db.commitTransaction();
		} catch (DatabaseException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DatabaseException:"+e.getClass()+"::"+e.getMessage());
		} catch(DataTypeException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DataTypeException:"+e.getClass()+"::"+e.getMessage());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		} 
		CloseDB();
		String temp = column;
		if(collected == null)
		{
			for(int i=0;i<2;i++)
			{
				collected = SelectData(temp);
				if(collected!=null)
				{
					break;
				}
				else
				{
					synchronized(Application.getApplication().getAppEventLock())
					{
						try {
							new Thread().wait(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return collected;
	}
	
	/**
	 * Method SelectAll.
	 * @return String[]
	 */
	public String[] SelectAll()
	{
		OpenDB();
		String collectedAll[] = new String[100];
		try{
			Statement st_select = db.createStatement(select_all);
			st_select.prepare();
			Cursor cursor = st_select.getCursor();
			cursor.first();
			int columns = cursor.getRow().getColumnNames().length;
			int i=0;
			while(i<columns)
			{
				collectedAll[i] = cursor.getRow().getString(i);
				new Logger().LogMessage(collectedAll[i]);
				i++;
			}
			cursor.close();
			st_select.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DatabaseException:"+e.getClass()+"::"+e.getMessage());
		} catch(DataTypeException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DataTypeException:"+e.getClass()+"::"+e.getMessage());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CloseDB();
		return collectedAll;
		
	}
	
	  /**
	   * Method DBPresence.
	   * @return boolean
	   */
	  public boolean DBPresence()
      {
	      boolean storagePresent = false;
	      String root = null;
	      try {
		      if
//		        ( DeviceInfo.getTotalFlashSize() > 1*1024*1024*1024 )                          //valid Flash check
		        ( DeviceInfo.getTotalFlashSizeEx() > 2*1024*1024*1024 )                 //for OS 6+ valid Flash check   
		  //only if device flash is above 2GB
	          {
	                  storagePresent = true;
	                  eMMCMounted = true;
	          }
		      else
		      {
	              Enumeration enum = FileSystemRegistry.listRoots();
	              while (enum.hasMoreElements())
	              {
	                      root = (String)enum.nextElement();
	                      if(root.equalsIgnoreCase("sdcard/"))                                                                                    //valid SDCard check
	                      {
	                              storagePresent = true;
	                              SDCardMounted = true;
	                      }  
	              }
	              if(!SDCardMounted)
	              {
	                      UiApplication.getUiApplication().invokeAndWait(new Runnable()
	                      {
	                              public void run()
	                              { 
	                                      new DBLogger().LogMessage("SDCard & valid eMMC storage missing...");
	                                      Dialog.alert("SDCard is required for the application to operate");
	                                      System.exit(0);            
	                              }
	                      }); 
	              }
              }
	      } catch(Exception e) {
	              e.printStackTrace();
	              new DBLogger().LogMessage("Exception:::"+e.getMessage()+"\r\n"+e.getClass());
	      }
              return storagePresent;
      }
       
      public void DBExistence()
      {
	      try{
	    	  if(DBPresence())
              {
                  if(eMMCMounted && SDCardMounted)
                  {
                          eMMCMounted = true;
                          SDCardMounted = false;
                          dbPath = eMMCPath;
                  }
                  else if(eMMCMounted)
                  {
                          URI usage_uri = URI.create(eMMCPath);
                          dbPath = eMMCPath;
                  }       
                  else
                  {
                          URI usage_uri = URI.create(SDCardPath);
                          dbPath = SDCardPath;
                  }
              }
	      } catch (IllegalArgumentException e) {
	              // TODO Auto-generated catch block
	              e.printStackTrace();
	      } catch (MalformedURIException e) {
	              // TODO Auto-generated catch block
	              e.printStackTrace();
	      }
      }
	
	 public void OpenDB()
     {
         try {
        	 DBExistence();
             db_URI = URI.create(dbPath);
             db = DatabaseFactory.open(db_URI);
         } catch (IllegalArgumentException e) {
                 e.printStackTrace();
         } catch (MalformedURIException e) {
                 e.printStackTrace();
         } catch (ControlledAccessException e) {
                 e.printStackTrace();
         } catch (DatabaseIOException e) {
                 e.printStackTrace();
         } catch (DatabasePathException e) {
                 e.printStackTrace();
         }
     }

     public void CloseDB()
     {
         try {
                 db.close();
         } catch (DatabaseIOException e) {
                 e.printStackTrace();
         }
     }
	
}