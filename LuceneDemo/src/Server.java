	import java.io.*;
import java.io.ObjectInputStream.GetField;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.queryparser.classic.ParseException;

import queryparse.QueryObject;
import queryparse.Specification;

public class Server
{
	public static String search(QueryObject queryObject) {
		String resultString="";
		String name = queryObject.getHotelName();
		String location = queryObject.getLocation();
		HashMap<String, Specification> aspectsHashMap = queryObject.getAspects();
		String other ="";
		for(String aspect: aspectsHashMap.keySet()){
			ArrayList<String> others;
			if(aspect.contains("value")){
				others = queryObject.getSpecification(aspect).getMapEntry("price");
			}
			else{
				others = queryObject.getSpecification(aspect).getMapEntry(aspect);
			}
			for(String each: others){
				other+= " "+ each;
			}
		}
		//String other = queryObject.getSpecification("location").getMapEntry("location").get(1);
		//System.out.println(queryObject.getSpecification("location").getMapEntry("location"));
		try {
			ArrayList<Hotel> results = HotelIndexer.searchIndex(location, name , other);
			System.out.println("Results: "+results.size());
			for(Hotel hotel: results){
				resultString += hotel.getHotelName().trim() + "~" + hotel.getLocation() +"~";
				/*for(String review: hotel.getReviews()){
					resultString += review.replaceAll("[^A-Za-z0-9 .!?,()$%<>]", "") + "~";
				}*/
				resultString += "@@@";
			}
		} 
		catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
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
            //System.out.println(resultString);
            outToClient.writeBytes(resultString);
         }
      }
}