import java.io.*;
import java.net.*;

import queryparse.QueryObject;

public class Server
{
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
            QueryObject qObject = queryProcessor.processQuery(query);
            outToClient.writeBytes(capitalizedSentence);
         }
      }
}