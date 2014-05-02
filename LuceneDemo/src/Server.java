	import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.ParseException;

import queryparse.QueryObject;

public class Server
{
	public static String search(QueryObject queryObject) {
		String resultString="";
		String name = queryObject.getHotelName();
		String location = queryObject.getLocation();
		//String other = queryObject.getSpecification("location").getMapEntry("location").get(1);
		//System.out.println(queryObject.getSpecification("location").getMapEntry("location"));
		try {
			ArrayList<Hotel> results = HotelIndexer.searchIndex(location, name , "");
			for(Hotel hotel: results){
				resultString += hotel.getHotelName().trim() + "\t" + hotel.getLocation() +"~";
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
            System.out.println(resultString);
            outToClient.writeBytes(resultString);
         }
      }
}