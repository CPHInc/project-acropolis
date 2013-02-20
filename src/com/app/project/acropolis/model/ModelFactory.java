package com.app.project.acropolis.model;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseIOException;
import net.rim.device.api.database.DatabasePathException;
import net.rim.device.api.database.DatabaseSecurityOptions;
import net.rim.device.api.database.Row;
import net.rim.device.api.database.Statement;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.EventLogger;

public class ModelFactory 
{
	final long GUID = 0x2ba36897214e5561L;
	final String AppName = "*DB*Project Acropolis SVN debugger**";
	
	/*DB Location/Path & Name*/
	public String DBName = "ACTIVITY_ACROPOLIS.txt";
	public String DBPath = "file:///databases/ProjectAcropolis/Database/"+DBName;
	public URI db_uri;
	public Database db;
	public Statement statement;
	
	/*DB Schema*/
	public final String SCHEMA = "ACROPOLIS_LAST_COORDINATES";
	public final String COORD_TABLE = "CREATE TABLE ACROPOLIS_LAST_COORDINATES (" +
			"PHONE_NUMBER TEXT NOT NULL," +				//FORMATTED 	+1 (647)-464-8458
			"FIX_DATE TEXT NOT NULL," +					//DATE 			mm/dd/yyyy
			"FIX_TIME TEXT NOT NULL," +					//TIME 			hh:mm **24hrs
			"ROAMING TEXT NOT NULL," +					//ROAMING 		true/false
			"FIX_ACK TEXT NOT NULL," +					//ACK.			true/false
			"LAT TEXT NOT NULL," +						//LATITUDE		xx.xxxxxx or 67.43125**iff FALSE
			"LNG TEXT NOT NULL," +						//LONGITUDE		xx.xxxxxx or -45.123456**iff FALSE
			"ACC TEXT NOT NULL" +						//ACCURACY		xx.xxx or 1234.1234**iff FALSE
			")";
	
	/*Schema queries*/
	public final String INSERT_QUERY = "INSERT INTO " + SCHEMA + " VALUES (";
	public final String UPDATE_QUERY = "UPDATE " + SCHEMA + " SET ";
	public final String SELECT_ALL_QUERY = "SELECT * FROM " + SCHEMA;
	public final String DELETE_ALL_QUERY = "DELETE FROM "+SCHEMA;
	
	//Always log for model workers
	public ModelFactory()
	{
		EventLogger.register(GUID, AppName, EventLogger.ALWAYS_LOG);
	}

	public void CreateDB()
	{
		try{
			db_uri = URI.create(DBPath);				//creates file on SDCard with new URI(Uniform Resource Identifier)
			EventLogger.logEvent(GUID, (db_uri.toString() + "::CREATED!!!").getBytes());
												//db_uri.toIDNAString()  ASCII characters are encoded to String
												//db_uri.toIRIString()	ASCII characters are encoded as %xx String
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}try {
			db_uri = URI.create(DBPath);				//creates file on SDCard with new URI(Uniform Resource Identifier)
			EventLogger.logEvent(GUID, (db_uri.toString() + "::CREATED!!!").getBytes());
												//db_uri.toIDNAString()  ASCII characters are encoded to String
												//db_uri.toIRIString()	ASCII characters are encoded as %xx String
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Normal Text file is created and converted to App DATABASE from the URI
	 */
	public void InstantiateDB()
	{
		try {
			db = DatabaseFactory.openOrCreate(db_uri,new DatabaseSecurityOptions(false));		//creates DB from URI
			EventLogger.logEvent(GUID, (DBName + "::CREATED").getBytes());
		} catch (DatabaseIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabasePathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * DB when created can be opened for working under or over
	 */
	public void OpenDB()
	{
		try {
			db = DatabaseFactory.open(db_uri);		//opens DB
			EventLogger.logEvent(GUID, (DBName + "::OPENED").getBytes());
		} catch (ControlledAccessException e) {
			e.printStackTrace();
		} catch (DatabaseIOException e) {
			e.printStackTrace();
		} catch (DatabasePathException e) {
			e.printStackTrace();
		}
	}
	
	/*******************************************************/
	/*********				CRUD				************/
	/*****************	C - reate		********************/
	/*****************	R - ead			********************/
	/*****************	U - pdate		********************/
	/*****************	D - elete		********************/
	/*******************************************************/
	
	/**
	 * Creates COORD_TABLE
	 */
	public void CreateSchema()
	{
		try{
			statement = db.createStatement(COORD_TABLE);
			statement.prepare();
			statement.execute();
			statement.close();
			db.close();
			EventLogger.logEvent(GUID, (SCHEMA + "::CREATED").getBytes());
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts data in DB
	 */
	public void InsertData( String phno, String date, String time, String roam, 
			String ack, String lat, String lng, String acc )
	{
		try{
			statement = db.createStatement(INSERT_QUERY + phno + "," + date + "," + 
					time + "," + roam + "," + ack + "," + lat + "," + lng + "," + acc + ")");
			statement.prepare();
			statement.execute();
			statement.close();
			db.close();
			EventLogger.logEvent(GUID, (DBName + "::INSERTED").getBytes());
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * if required can be used
	 * @param column
	 * @param data
	 */
	public void UpdateData(String column,String data, String where_col,String where_arg)
	{
		try{
			statement = db.createStatement(UPDATE_QUERY + column + " = " + data + " WHERE " + where_col + " = " + where_arg);
			statement.prepare();
			statement.execute();
			statement.close();
			db.close();
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetches all the data from the table
	 * @return All[Row][Column]
	 */
	public String[][] SelectAll()
	{
		int row_counter=0,column_counter=0;
		int TableColumns = 0;
		String[][] All = new String[40][40];		//All [Row] [Column]
		Row row;
		Cursor cursor;
		
		try{
			statement = db.createStatement(SELECT_ALL_QUERY);
			statement.prepare();
			statement.execute();
			cursor = statement.getCursor();
			cursor.first();									//point @ first row
			row = cursor.getRow();
			TableColumns = row.getColumnNames().length;		//returns 8
			while( cursor.next() )
			{
				++row_counter;
				for(column_counter=1 ; column_counter<=TableColumns ; column_counter++)
				{
					All[row_counter][column_counter] = row.getString(column_counter);		//fetches String represented data from all the COLUMNS of the "current ROW"
				}
			}
		} catch(DatabaseException e) {
			e.printStackTrace();
		} catch(DataTypeException e) {
			e.printStackTrace();
		}
		return All;
	}
	
	/**
	 * Deletes all data from the table
	 */
	public void DeleteAll()
	{
		try{
			statement = db.createStatement(DELETE_ALL_QUERY);
			statement.prepare();
			statement.execute();
			statement.close();
			db.close();
		} catch(DatabaseException e) {
			e.printStackTrace();
		}
	}
	
}
