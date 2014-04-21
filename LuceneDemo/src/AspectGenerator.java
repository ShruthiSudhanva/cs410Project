/**/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;


public class AspectGenerator {

	/**
	 * @param args
	 */
	public static HashMap<String,String> getFromDictionary(java.util.HashMap<String,String> hm, String query)
	{
	java.util.HashMap<String,String> identified_tags = new java.util.HashMap<String,String>();
	
	if(query.indexOf(",")!=-1)
	{
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(query,",");
		String s;
		while(tokenizer.hasMoreTokens())
		{
			s=tokenizer.nextToken();
			if(s.indexOf(" ")!=-1)
			{
			java.util.StringTokenizer tokenizermini = new java.util.StringTokenizer(s," ");
			while(tokenizermini.hasMoreTokens())
				{
	 			String word=tokenizermini.nextToken();
	 			if(hm.get(word)!=null)
	 				{
	 				String tag=hm.get(word);
	 				if(identified_tags.get(tag)==null)
	 					{
	 					identified_tags.put(word,tag);
	 					}
	 				}
	 			else
	 			{
	 				identified_tags.put(word, "misc");
	 			}
				}
			}
			else
			{
			if(s!="" && s!=null)
			{
			if(identified_tags.get(s)==null)
			   {	
				if(hm.get(s)!=null)
				{
				String tag=hm.get(s);
				identified_tags.put(s,tag);	
				}
				else
				{
					identified_tags.put(s, "misc");
				}
		   	   }
		}
			}
		}	
	}
	else
	{
	if(query!="" && query!=null)
		{
		if(identified_tags.get(query)==null)
		   {	
				if(hm.get(query)!=null)
				{
				String tag=hm.get(query);
				identified_tags.put(query,tag);	
				}
				else
				{
				identified_tags.put(query, "misc");
				}
		   }
		}
	}
	return identified_tags;
	}
	
	public static HashMap<String,String> buildDictionary(String filename)
	{
	HashMap<String,String> hm=new HashMap<String,String>();
	try{
	BufferedReader br=new BufferedReader(new FileReader(filename));
	String s="";
	while((s=br.readLine())!=null && s!="")
		{
		StringTokenizer st=new StringTokenizer(s," ");
		int first=1;
		String key="";
		String value="";
		while(st.hasMoreTokens())
		{
		if(first==1)
		{
		key=st.nextToken();
		key=key.substring(1,key.length()-1);
		first=0;
		}
		else
		{
		value=st.nextToken();
		if(key!="" && value!="")
		hm.put(value,key);
		}
		}
		}
		
	}catch(Exception e)
	{
	System.out.println(e);
	}
	return hm;
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String,String> hm=new HashMap<String,String>();
	    hm=buildDictionary("/home/shruthi/cs410Project/LuceneDemo/src/hotel_bootstrapping_new.dat");
	    /*for(String key:hm.keySet())
			{
			System.out.println(key+" "+hm.get(key));
			}*/
			
	    String query1="Find a hotel that is cheap";
	    String query2="hotels near chicago downtown";
	    String query3="hotels with good food";
	    String query4="good hotel in Seattle";
	    String query5="classy hotels in Maryland";
	    String query6="California downtown hotels";
	    String query7="hotels under $500";
	    String query8="hotels with cheap food";
	    String query9="best hotel in chicago";
	    String query10="best hotel with cheap price in chicago";
	    
	    //after noun phrases are identified query is the noun phrases string delimited by , and " " within
	    String query="hotel,chicago,good location,good location,cheap price, large room";
	    QueryProcessor processor = new QueryProcessor();
	    
	    HashMap<String,String> hm_result=new HashMap<String,String>();
	    hm_result=getFromDictionary(hm,query);
	     for(String key:hm_result.keySet())
	    	System.out.println(key+" "+hm_result.get(key));
	    	
	}

}
