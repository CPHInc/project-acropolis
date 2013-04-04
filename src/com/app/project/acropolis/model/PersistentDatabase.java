package com.app.project.acropolis.model;

import net.rim.device.api.util.Persistable;

public class PersistentDatabase implements Persistable 
{

	public String VALUE = "";
	public String KEY = "";
	
	public PersistentDatabase(String kEY,String vALUE)
	{
		KEY = kEY;
		VALUE = vALUE;
	}

	public String getKEY() {
		return KEY;
	}

	public void setKEY(String kEY) {
		KEY = kEY;
	}

	public String getVALUE() {
		return VALUE;
	}

	public void setVALUE(String vALUE) {
		VALUE = vALUE;
	}
	
}
