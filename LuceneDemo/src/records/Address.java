/**
 * 
 */
package records;

import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * @author hongning
 * General structure for location information
 * All the text are in lower case
 */
public class Address {
	public String m_street;
	public String m_extendedStreet; // some hotel might have such info
	public String m_city; // can be a combination of words
	public String m_state;
	public String m_postalcode;
	public String m_country; // default it is "U.S."
	
	//specialized for hotel entity
	//can be loaded from html or json
	public Address(String address, boolean isJson){
		if (!isJson){
			Document doc = Jsoup.parseBodyFragment(address);
			getStreet(doc);
			getExtendedStreet(doc);
			getLocality(doc);
			getCountry(doc);
		} else {
			try {
				JSONObject addr = new JSONObject(address);
				getStreet(addr);
				getExtendedStreet(addr);
				getCountry(addr);
				getLocality(addr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getStreet(Document doc){
		Element el = doc.getElementsByClass("street-address").first();
		if (el!=null)
			m_street = el.text().toLowerCase();
		else
			m_street = null;
	}
	
	private void getStreet(JSONObject obj) throws JSONException{
		if (obj.has("street"))
			m_street = obj.getString("street").toLowerCase();
		else
			m_street = null;
	}
	
	private void getExtendedStreet(Document doc){
		Element el = doc.getElementsByClass("extended-address").first();
		if (el!=null)
			m_extendedStreet = el.text().toLowerCase();
		else
			m_extendedStreet = null;
	}
	
	private void getExtendedStreet(JSONObject obj) throws JSONException{
		if (obj.has("extended-street"))
			m_extendedStreet = obj.getString("extended-street").toLowerCase();
		else
			m_extendedStreet = null;
	}
	
	private void getCountry(Document doc){
		Element el = doc.getElementsByClass("country-name").first();
		if (el!=null)
			m_country = el.text().toLowerCase();
		else
			m_country = "U.S.";
	}
	
	private void getCountry(JSONObject obj) throws JSONException{
		if (obj.has("country"))
			m_country = obj.getString("country").toLowerCase();
		else
			m_country = null;
	}
	
	private void getLocality(Document doc){
		Element el = doc.getElementsByClass("locality").first();
		if (el==null)
			return;//something wrong with the html file
		
		Element item = el.getElementsByAttributeValue("property", "v:region").first();
		if (item!=null)
			m_state = item.text().toLowerCase();
		else
			m_state = null;
		
		item = el.getElementsByAttributeValue("property", "v:postal-code").first();
		if (item!=null)
			m_postalcode = item.text().toLowerCase();
		else
			m_postalcode = null;
		
		item = el.getElementsByAttributeValue("property", "v:locality").first();		
		if (item!=null){
			m_city = item.text();
			Node extendedCity = item.nextSibling();
			if (extendedCity!=null && extendedCity.nodeName().equals("#text")){
				String extCity = trimCityName(extendedCity.toString());
				if (extCity!=null)
					m_city += extCity;
			}
		} else{
			Node city = el.childNode(0);
			if (city.nodeName().equals("#text")) {//maybe the city name is in the text field
				m_city = trimCityName(city.toString());//maybe null
			} else 
				m_city = null;
		}
			
	}
	
	private void getLocality(JSONObject obj) throws JSONException{
		if (obj.has("state"))
			m_state = obj.getString("state").toLowerCase();
		else
			m_state = null;
		
		if (obj.has("postal-code"))
			m_postalcode = obj.getString("postal-code").toLowerCase();
		else
			m_postalcode = null;
		
		if (obj.has("city"))
			m_city = obj.getString("city").toLowerCase();
		else
			m_city = null;
	}
	
	private String trimCityName(String city){
		city = city.trim();
		int pos = city.lastIndexOf(',');
		if (pos<=0){
			if (city.length()>1)
				return city.toLowerCase();
			else
				return null;
		} else
			return city.substring(0, pos).toLowerCase();
	}
	
	public String toIndexString(){
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(m_street);
		
		if (m_extendedStreet!=null){
			buffer.append("\t");
			buffer.append(m_extendedStreet);
		}
		
		if (m_state!=null){
			buffer.append("\t");
			buffer.append(m_state);
		}
		
		if (m_city!=null){
			buffer.append("\t");
			buffer.append(m_city);
		}
		return buffer.toString();
	}
	
	public String toJson(){
		JSONObject address = new JSONObject();
		try {
			if (m_street!=null)
				address.put("street", m_street);
			
			if (m_extendedStreet!=null)
				address.put("extended-street", m_extendedStreet);
			
			if (m_city!=null)
				address.put("city", m_city);
			
			if (m_state!=null)
				address.put("state", m_state);
			
			if (m_country!=null)
				address.put("country", m_country);
			
			if (m_postalcode!=null)
				address.put("postal-code", m_postalcode);
			
			return address.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		Address test = new Address("<address> <span rel=\"v:address\"> <span dir=\"ltr\"><span class=\"street-address\" property=\"v:street-address\">4125 Kildeer Drive</span>, <span class=\"locality\"><span property=\"v:locality\">Indianapolis</span>, <span property=\"v:region\">IN</span> <span property=\"v:postal-code\">46237</span></span> </span> </span> </address>", false);
		System.out.println(test.toJson());
	}
}
