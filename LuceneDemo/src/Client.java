import java.io.*;
import java.net.*;

public class Client
{
 public static void main(String argv[]) throws Exception
 {
  String sentence;
  BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
  Socket clientSocket = new Socket("localhost", 6789);
  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
  BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  sentence = inFromUser.readLine();
  outToServer.writeBytes(sentence + '\n');
  String result = inFromServer.readLine();
  String [] results = result.split("@@@");
  System.out.println("Length:"+results.length);
  for(String r: results){
	  String [] hotels = r.split("~"); 
	  String hotelName = hotels[0];
	  String location = hotels[1];
	  System.out.println("Hotel Name: "+hotelName);
	  System.out.println("Hotel location: "+location);
	  for(int i=2; i< hotels.length; i++ ){
		  System.out.println(hotels[i]);
	  }
  }
  System.out.println("\n");
  clientSocket.close();
 }
}