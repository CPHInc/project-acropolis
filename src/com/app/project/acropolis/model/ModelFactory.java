package com.app.project.acropolis.model;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseIOException;
import net.rim.device.api.database.DatabasePathException;
import net.rim.device.api.database.Statement;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.ControlledAccessException;

public class ModelFactory {

	public final String DB_NAME = "acropolis.db";
	public final String SCHEMA = "activity_acropolis";
	
	public Database db;
	public String path = "file:///SDCard/Acropolis/database/acropolis.db";
	public URI db_URI = null;
	
	public String update_query = "update activity_acropolis set ";
//	public String update_query = "update activity_acropolis set sent=\'4\'";
	public String select_query = "select ";
	public String select_part2 = " from activity_acropolis";
	
	public String select_all = "select * from activity_acropolis";
	
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
		}
		new DBLogger().LogMessage("DB updated");
		CloseDB();
	}
	
	public String SelectData(String column)
	{
		OpenDB();
		String collected = "";
		int colIndex = 0;
		try{
			Statement st_select = db.createStatement(select_query + column +select_part2);
			st_select.prepare();
			Cursor cursor = st_select.getCursor();
			cursor.first();
			colIndex = cursor.getColumnIndex(column);
			collected = cursor.getRow().getString(colIndex);
			cursor.close();
			st_select.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DatabaseException:"+e.getClass()+"::"+e.getMessage());
		} catch(DataTypeException e) {
			e.printStackTrace();
			new DBLogger().LogMessage("DataTypeException:"+e.getClass()+"::"+e.getMessage());
		}
		new DBLogger().LogMessage("selected from ::"+column +":::" + collected);
		CloseDB();
		return collected;
	}
	
	public String[] SelectAll()
	{
		OpenDB();
		String collectedAll[] = new String[100];
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
		}
		CloseDB();
		return collectedAll;
		
	}
	
	
	public void OpenDB()
	{
		try {
			db_URI = URI.create(path);
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
	
	
//	if(column.equalsIgnoreCase("phone_number"))
//	colIndex = 0;
//if(column.equalsIgnoreCase("roaming"))
//	colIndex = 3;
//if(column.equalsIgnoreCase("fix_ack"))
//	colIndex = 4;
//if(column.equalsIgnoreCase("lat"))
//	colIndex = 5;
//if(column.equalsIgnoreCase("lng"))
//	colIndex = 6;
//if(column.equalsIgnoreCase("acc"))
//	colIndex = 7;
//if(column.equalsIgnoreCase("incoming"))
//	colIndex = 8;
//if(column.equalsIgnoreCase("outgoing"))
//	colIndex = 9;
//if(column.equalsIgnoreCase("received"))
//	colIndex = 10;
//if(column.equalsIgnoreCase("sent"))
//	colIndex = 11;
//if(column.equalsIgnoreCase("downloaded"))
//	colIndex = 12;
//if(column.equalsIgnoreCase("uploaded"))
//	colIndex = 13;
	
}