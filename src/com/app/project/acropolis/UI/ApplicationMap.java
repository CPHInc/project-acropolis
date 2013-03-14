package com.app.project.acropolis.UI;

import net.rim.device.api.lbs.MapField;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public class ApplicationMap extends MapField
{
	 // For cursor
    public Bitmap _cursor = Bitmap.getBitmapResource("48.png");
    // For preferred height
    public LabelField _sampleLabel;    
    public int _textHeight = 0;
    public boolean _turnOffText = false;       
    public String Map_Text = "Determining location ...";
    public boolean fix = false;
    
	public ApplicationMap()
	{
		_sampleLabel = new LabelField();
        _textHeight = _sampleLabel.getPreferredHeight();        
        _sampleLabel = null; 
	}
	
	public void paint(Graphics g)
	{
		g.setDrawingStyle(Graphics.DRAWSTYLE_AAPOLYGONS,true);
		super.paint(g);
		
		//Places the cursor permanently at the center of the map.
		g.drawBitmap(getWidth() >> 1, getHeight() >> 1, getWidth(), getHeight(), _cursor, 0, 0);
	}
	
	public void setZoom(int zoom)
	{
		fix = true;
		super.setZoom(zoom);
	}
	
//	/**
//     * @see net.rim.device.api.ui.Field#navigationMovement(int, int, int, int)
//     */
//    public boolean navigationMovement(int dx, int dy, int status, int time) 
//    {
//        // The map is shifted in relation to the current zoom level.
//        int zoom = getZoom();
//        int latitude = getLatitude() - ((dy << 3) << zoom);     // << 3 is equivalent to multiplication by 8.
//        int longitude = getLongitude() + ((dx << 3) << zoom);                  
//        moveTo(latitude, longitude);       
//        return true;        
//    }
    
    public void setMapLabel(String newText)
    {
    	_turnOffText = false;
    	Map_Text = newText;
    }

}