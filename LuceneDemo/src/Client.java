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
  String [] results = result.split("~");
  for(String r: results){
	  System.out.println("FROM SERVER: " + r + "\n");
  }
  clientSocket.close();
 }
}