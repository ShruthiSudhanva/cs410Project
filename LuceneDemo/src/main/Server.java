package main;
import java.io.*;
import java.io.ObjectInputStream.GetField;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.SortField;

import queryparse.QueryObject;
import queryparse.Specification;

public class Server
{
	
	public static String search(QueryObject queryObject) {
		int room = 0 ;
		int cleanliness =0;
		int loc = 0;
		int service=0;
		int value=0;
		String resultString="";
		String name = queryObject.getHotelName();
		String location = queryObject.getLocation();
		HashMap<String, Specification> aspectsHashMap = queryObject.getAspects();
		ArrayList<SortField> sortList = new ArrayList<SortField>();
		String other ="";
		for(String aspect: aspectsHashMap.keySet()){
			ArrayList<String> others;
			if(aspect.contains("value")){
				value = queryObject.getSpecification(aspect).getRating();
				sortList.add(new SortField("value", SortField.Type.FLOAT, true));
				others = queryObject.getSpecification(aspect).getMapEntry("price");
			}
			else{
				float weight = queryObject.getSpecification(aspect).getWeight();
				if(aspect.equals("location")){
					sortList.add(new SortField("loc", SortField.Type.FLOAT, true));
					loc = queryObject.getSpecification(aspect).getRating();
				}
				else
					sortList.add(new SortField(aspect, SortField.Type.FLOAT, true));
				if(aspect.equals("room")){
					room = queryObject.getSpecification(aspect).getRating();
				}
				if(aspect.equals("service")){
					service = queryObject.getSpecification(aspect).getRating();
				}
				if(aspect.equals("cleanliness")){
					cleanliness = queryObject.getSpecification(aspect).getRating();
				}
				others = queryObject.getSpecification(aspect).getMapEntry(aspect);
			}
			for(String each: others){
				if(other.isEmpty()){
					other = each;
				}
				else{
				other+= "," + each;
				}
			}
		}
		try {
			SortField[] sortFields = new SortField[sortList.size()];
			sortFields = (SortField[]) sortList.toArray(sortFields);
			ArrayList<Hotel> results = HotelIndexer.searchIndex(location, name , other , aspectsHashMap, sortFields);
			System.out.println("Results: "+results.size());
			resultString = room + "%%%" + loc +"%%%" + service +"%%%" + cleanliness +"%%%" + value + "%%%";
			for(Hotel hotel: results){
				resultString +=  hotel.getId() + "###" + hotel.getHotelName().trim() + "###" + hotel.getLocation() +"###" + hotel.count + "###" ;
				resultString +=  hotel.ratings.get("room") +"###" + hotel.ratings.get("loc") + "###" + hotel.ratings.get("service") + "###" + hotel.ratings.get("cleanliness") + "###" + hotel.ratings.get("value") + "###";
			
				resultString += "@@@";
			}
		} 
		catch (ParseException | IOException e) {
			
			e.printStackTrace();
		}
		return resultString;
	}
	
	public static void main(String argv[]) throws Exception
      {
         String query;
         String capitalizedSentence;
         ServerSocket welcomeSocket = new ServerSocket(6789);
     	 QueryProcessor queryProcessor = new QueryProcessor();
         System.out.println("Started server...");
         while(true)
         {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient =
               new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            query = inFromClient.readLine();
            System.out.println("Received: " + query);
            capitalizedSentence = query + '\n';
            QueryObject queryObject = queryProcessor.processQuery(query);
            String resultString = search(queryObject)+"\n";
            
            outToClient.writeBytes(resultString);
         }
      }
}
