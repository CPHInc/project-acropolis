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
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class ModelFactory {

	public final String DB_NAME = "acropolis.db";
	public final String SCHEMA = "activity_acropolis";
	
	public Database db;
	
	public boolean SDCardMounted = false;
	public boolean eMMCMounted = false;
	public String SDCardPath = "file:///SDCard/Acropolis/database/acropolis.db";
	public String eMMCPath = "file:///store/home/user/acropolis.db";
	public String dbPath = "";
	public URI db_URI = null;
	
	public String update_query = "update activity_acropolis set ";
//	public String update_query = "update activity_acropolis set sent=\'4\'";
	public String select_query = "select ";
	public String select_part2 = " from activity_acropolis";
	public String select_all = "select * from activity_acropolis";

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
//			db.beginTransaction();
			Statement st_update = db.createStatement(update_query + column + " = \'" + data + "\'");
			st_update.prepare();
			st_update.execute();
			st_update.close();
//			db.commitTransaction();
		} catch (DatabaseException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DatabaseException:"+e.getClass()+"::"+e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} 
		CloseDB();
		new DBLogger().LogMessage("DB updated");
	}
	
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
			new Logger().LogMessage("index:->"+colIndex);
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
		new DBLogger().LogMessage("selected from ::"+column +":::" + collected);
		return collected;
	}
	
	public String[] SelectAll()
	{
		String collectedAll[] = new String[100];
		OpenDB();
		try{
			Statement st_select = db.createStatement(select_all);
			st_select.prepare();
			
			Cursor cursor = st_select.getCursor();
			if(cursor.first())
			{
				collectedAll[0] = cursor.getRow().getString(0);
				collectedAll[1] = cursor.getRow().getString(1);
				collectedAll[2] = cursor.getRow().getString(2);
				collectedAll[3] = cursor.getRow().getString(3);
				collectedAll[4] = cursor.getRow().getString(4);
				collectedAll[5] = cursor.getRow().getString(5);
				collectedAll[6] = cursor.getRow().getString(6);
				collectedAll[7] = cursor.getRow().getString(7);
				collectedAll[8] = cursor.getRow().getString(8);
				collectedAll[9] = cursor.getRow().getString(9);
				collectedAll[10] = cursor.getRow().getString(10);
				collectedAll[11] = cursor.getRow().getString(11);
				collectedAll[12] = cursor.getRow().getString(12);
				collectedAll[13] = cursor.getRow().getString(13);
			}
			int i=0;
			while(i <= collectedAll.length)
			{
				new DBLogger().LogMessage(collectedAll[i]);
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
	
	  public boolean DBPresence()
      {
	      boolean storagePresent = false;
	      String root = null;
	      try {
		      if
//		        ( DeviceInfo.getTotalFlashSize() > 1*1024*1024*1024 )                           //valid Flash check
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
	                                      new Logger().LogMessage("SDCard & valid eMMC storage missing...");
	                                      Dialog.alert("SDCard is required for the application to operate");
	                                      System.exit(0);            
	                              }
	                      }); 
	              }
              }
	      } catch(Exception e) {
	              e.printStackTrace();
	              new Logger().LogMessage("Exception:::"+e.getMessage()+"\r\n"+e.getClass());
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
                          new DBLogger().LogMessage("URI::"+usage_uri.toIDNAString());
                          dbPath = eMMCPath;
                  }       
                  else
                  {
                          URI usage_uri = URI.create(SDCardPath);
                          new DBLogger().LogMessage("URI::"+usage_uri.toIDNAString());
                          dbPath = SDCardPath;
                  }
              }
	      } catch (IllegalArgumentException e) {
	              // TODO Auto-generated catch block
	              e.printStackTrace();
	      } catch (MalformedURIException e) {
	              // TODO Auto-generated catch block
	              e.printStackTrace();
	      } catch (IDNAException e) {
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