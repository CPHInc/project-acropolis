package com.app.project.acropolis.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;

import loggers.Logger;
import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.io.http.HttpProtocolConstants;

public class RESTServerConnection 
{
	static boolean LoginSuccess;
	static StreamConnection _streamConnection;
	static HttpConnection _httpConnection;	
	static HttpConnection httpStream;
	static OutputStream dataout;
	static OutputStream os;
	static URLEncodedPostData _postdata;
	static URLEncodedPostData _streamPostData ;
	final static String _encryption = "UTF-8";
	static String _contentlength;
	static byte[] _restbytes = new byte[10000];
	static int _RESTType;	//0 = GET 1 = POST

	//TODO
	static String _url = "http://cphcloud.biz";
	static String _port = ":80";
	static String _connectionParameters = ";deviceside=false";	//	BES/MDS
	//login form
	static String urlStreamPOST = "";
	static String urlStreamPort = ":80";
	static String urlStreamParams = ";deviceside=false";
	static String _loginName = "";	
	static String _loginValue = "";
	static String _passwordName = "";
	static String _passwordValue = "";
	//stream form
	static String emailName = "";
	static String emailValue = "";
	static String streamName = "";
	static String streamValue = "";

	RESTServerConnection(int restType)
	{
		LoginSuccess = false;
		_streamConnection = null;
		_httpConnection = null;
		_postdata = null;
		emailName = null;
		emailValue = null;
		streamName = null;
		streamValue = null;
		new Logger().LogMessage("RESTServerConnection");
		_RESTType = restType;
	}

	void openPOSTConnection() throws IOException
	{
		_streamConnection = (StreamConnection) Connector.open(_url+_port+_connectionParameters);
		_httpConnection = (HttpConnection) _streamConnection;
	}

	void openGETConnection() throws IOException
	{
		_url = _url + _port + _connectionParameters +
				"?"+_loginName+"="+_loginValue+"&" + _passwordName + "=" + _passwordValue;
		_streamConnection = (StreamConnection) Connector.open("");
	}

	void RESTHeaderData() throws IOException
	{
		if(_RESTType == 0)	//GET
		{
			_httpConnection.setRequestMethod(HttpConnection.GET);
			_httpConnection.setRequestProperty(_loginName, _loginValue);
			_httpConnection.setRequestProperty(_passwordName, _passwordValue);
			LoginSuccess = true;
		}
		if(_RESTType == 1) //POST
		{
			_httpConnection.setRequestMethod(HttpConnection.POST);
			_httpConnection.setRequestProperty(_loginName, _loginValue);
			_httpConnection.setRequestProperty(_passwordName, _passwordValue);
			_postdata = new URLEncodedPostData(_encryption,null);
			_postdata.append(_loginName, _loginValue);
			_postdata.append(_passwordName, _passwordValue);
			_restbytes = _postdata.getBytes();
			_contentlength = String.valueOf(_restbytes.length);
		}
	}

	void RESTBodyData()	throws Exception //for POST
	{
		_httpConnection.setRequestProperty(HttpProtocolConstants.HEADER_CONTENT_LENGTH, _contentlength);
		_httpConnection.setRequestProperty(HttpProtocolConstants.HEADER_CONTENT_TYPE, 
				HttpProtocolConstants.CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
		_httpConnection.setRequestProperty(HttpProtocolConstants.HEADER_CONNECTION, 
				HttpProtocolConstants.HEADER_KEEP_ALIVE);

		dataout = _httpConnection.openOutputStream();
		dataout.write(_restbytes);
		dataout.flush();
		dataout.close();

		int HttpLength = (int)_httpConnection.getLength();
		byte[] readBytes = new byte[HttpLength];
		if(_httpConnection.getResponseCode() == HttpConnection.HTTP_OK)
		{
			new Logger().LogMessage("HTTP OK 200");
			InputStream datain = _httpConnection.openInputStream();

			String readString = null;
			int sofarRead = 0;
			if(HttpLength>0)
			{
				int totalRead = 0;

				while( (totalRead != HttpLength) && (totalRead != -1) )
				{
					sofarRead = datain.read(readBytes, totalRead, HttpLength-totalRead);
					totalRead =+ sofarRead; 
				}
			}
			readString = new String(readBytes,"UTF-8");
			new Logger().LogMessage(readString);
			LoginSuccess = true;
		}
		else
			new Logger().LogMessage(_httpConnection.getResponseMessage());
	}

	void exec(String _email,String _stream)
	{
		try{
			if(_RESTType==1)		//POST
			{
				openPOSTConnection();
				RESTHeaderData();
				RESTBodyData();
				if(LoginSuccess)
				{
					execUploadData(_email,_stream);
				}
			}
			else if(_RESTType==0)	//GET
			{
				openGETConnection();
				RESTHeaderData();
				if(LoginSuccess)
				{
					execUploadData(_email,_stream);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(_httpConnection != null)
			{
				try {
					_httpConnection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	void execUploadData(String _email,String _stream)
	{
		try {
			emailName = "";
			emailValue = _email;
			streamName = "";
			streamValue = _stream;
			
			urlStreamPOST += urlStreamPort + urlStreamParams;
			httpStream = (HttpConnection) Connector.open(urlStreamPOST);
			httpStream.setRequestMethod(HttpConnection.POST);
			httpStream.setRequestProperty(emailName, emailValue);
			httpStream.setRequestProperty(streamName, streamValue);

			_streamPostData  = new URLEncodedPostData(_encryption,null);
			_streamPostData .append(emailName, emailValue);
			_streamPostData .append(streamName, streamValue);
			httpStream.setRequestProperty(HttpProtocolConstants.HEADER_CONTENT_LENGTH, 
					String.valueOf(_streamPostData .getBytes().length));

			os = httpStream.openOutputStream();
			os.write(urlStreamPOST.getBytes());
			os.flush();
			os.close();

			new Logger().LogMessage("ResponseCode::#"+httpStream.getResponseCode());
			if(httpStream.getResponseCode() == HttpConnection.HTTP_OK)
			{
				new Logger().LogMessage("Stream uploaded");
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if(httpStream!=null)
					httpStream.close();
				if(os!=null)
					os.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}

}
