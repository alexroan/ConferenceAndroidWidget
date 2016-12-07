package com.alr16.conferencewidget;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.StrictMode;
import android.util.Log;

public class XMLHandler {
	
	private static String KEY_CURRENT_TIME = "currentTime";
	private static String KEY_CURRENT_MESSAGE = "currentMessage";
	private static String KEY_SESSION = "session";
	public static String KEY_SESSION_TITLE = "sessiontitle";
	public static String KEY_SESSION_TYPE = "type";
	public static String KEY_SESSION_START_TIME = "startTime";
	public static String KEY_SESSION_END_TIME = "endTime";

	private String url;
	private ArrayList<HashMap<String, String>> sessions;
	private HashMap<String, String> currentSession;
	private HashMap<String, String> nextSession;
	private String currentMessage;
	private Date currentTime;
	
	
	public XMLHandler(){
		this.currentSession = null;
		this.nextSession = null;
		this.currentMessage = null;
		this.currentTime = null;
	}
	
	public XMLHandler(String url){
		this.url = url;
		this.currentSession = null;
		this.nextSession = null;
		this.currentMessage = null;
		this.currentTime = null;
	}
	
	
	
	public boolean retrieveCurrentAndNextSessions(){
		boolean returned = false;
		if(this.sessions != null){
			HashMap<String, String> thisSession;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			Date thisStartTime;
			Date thisEndTime;
			for(int i=0; i<this.sessions.size(); i++){
				try{
					thisSession = this.sessions.get(i);
					thisStartTime = sdf.parse(thisSession.get(KEY_SESSION_START_TIME));
					Log.e("start time", thisStartTime.toLocaleString());
					thisEndTime = sdf.parse(thisSession.get(KEY_SESSION_END_TIME));
					Log.e("end time", thisEndTime.toLocaleString());
					//If current time is later than start time and earlier that end time then this is current session
					if((this.currentTime.compareTo(thisStartTime)>0) && (this.currentTime.compareTo(thisEndTime)<0)){
						this.currentSession = thisSession;
						Log.e("found current sesh", thisSession.get(KEY_SESSION_TITLE));
						this.nextSession = this.sessions.get(i+1);
					}
				}
				catch(Exception e){
					Log.e("error", e.getLocalizedMessage());
				}
			}
			returned = true;
		}
		return returned;
	}
	 
	
	public ArrayList<HashMap<String, String>> retrieveXML(){
		this.sessions = null;
		try{
			String xml = this.retrieveXMLFromUrl(this.url);
			Document doc = this.getDomElement(xml);
			//current time
			NodeList nodes = doc.getElementsByTagName(KEY_CURRENT_TIME);
			Element e = (Element) nodes.item(0);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			this.currentTime = sdf.parse(this.getElementValue(e));
			Log.e("current time", this.currentTime.toLocaleString());
			//current message
			nodes = doc.getElementsByTagName(KEY_CURRENT_MESSAGE);
			e = (Element) nodes.item(0);
			this.currentMessage = this.getElementValue(e);
			//sessions
			nodes = doc.getElementsByTagName(KEY_SESSION);
			this.sessions = new ArrayList<HashMap<String, String>>();
			
			HashMap<String, String> map;
			for(int i=0; i<nodes.getLength(); i++){
				map = new HashMap<String, String>();
				e = (Element) nodes.item(i);
				map.put(KEY_SESSION_TITLE, this.getValue(e, KEY_SESSION_TITLE));
				map.put(KEY_SESSION_TYPE, this.getValue(e, KEY_SESSION_TYPE));
				String startTime = e.getAttribute(KEY_SESSION_START_TIME);
				String endTime = e.getAttribute(KEY_SESSION_END_TIME);
				map.put(KEY_SESSION_START_TIME, startTime);
				map.put(KEY_SESSION_END_TIME, endTime);
				this.sessions.add(map);
			}
		}
		catch(Exception e){
			Log.e("error", e.getLocalizedMessage());
		}		
		
		return this.sessions;
	}
	
	
	private String retrieveXMLFromUrl(String url){
		String xml = null;		
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
			StrictMode.setThreadPolicy(policy);
			DefaultHttpClient http = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse response;
			response = http.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
			xml = EntityUtils.toString(httpEntity);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return xml;
	}
	
	private Document getDomElement(String xml){
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
		}catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
		return doc;
	}
	
	private String getValue(Element item, String string){
		NodeList nodes = item.getElementsByTagName(string);		
		return this.getElementValue(nodes.item(0));
	}
	

	private final String getElementValue(Node elem){
		Node child;
		if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();                        
                    }
                }
            }
        }
		return "";
	}
	
	public Date getCurrentTime(){
		return this.currentTime;
	}
	
	public ArrayList<HashMap<String, String>> getSessions(){
		return this.sessions;
	}
	
	public String getCurrentMessage(){
		return this.currentMessage;
	}
	
	public HashMap<String, String> getCurrentSession(){
		return this.currentSession;
	}
	
	public HashMap<String, String> getNextSession(){
		return this.nextSession;
	}
}
