import java.io.*;
import java.util.*;
public class testDict{
	public static HashMap<String,String> getFromDictionary(java.util.HashMap<String,String> hm, ArrayList<String> queryList)
	{
	
		 StringBuilder queryBuilder = new StringBuilder();
		    int last=queryList.size();
		    last=last-1;
		    for(int i=0;i<last+1;i++)
		    {
		    	queryBuilder.append(queryList.get(i));
		    	if(i!=last)
		    	queryBuilder.append(",");
		    }
		    String query=queryBuilder.toString();
		    System.out.println(query);
		
	java.util.HashMap<String,String> identified_tags = new java.util.HashMap<String,String>();
	
	if(query.indexOf(",")!=-1)
	{
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(query,",");
		String s;
		while(tokenizer.hasMoreTokens())
		{  
			s=tokenizer.nextToken();
			if(s.contains("$"))
				identified_tags.put(s, "value");
			else if(s.indexOf(" ")!=-1)
			{
			java.util.StringTokenizer tokenizermini = new java.util.StringTokenizer(s," ");
			while(tokenizermini.hasMoreTokens())
				{
	 			String word=tokenizermini.nextToken();
	 			if(hm.get(word)!=null)
	 				{
	 				String tag=hm.get(word);
	 				    //identified_tags.put(word,tag);
	 					identified_tags.put(s,tag); 			
	 				}
	 			else
	 			{
	 				//identified_tags.put(word, "misc");
	 				identified_tags.put(s, "misc");
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
		if(query.contains("$"))
			identified_tags.put(query, "value");
		else if(query.indexOf(" ")!=-1)
		{
		java.util.StringTokenizer tokenizermini = new java.util.StringTokenizer(query," ");
		while(tokenizermini.hasMoreTokens())
			{
 			String word=tokenizermini.nextToken();
 			if(hm.get(word)!=null)
 				{
 				String tag=hm.get(word);
 					//identified_tags.put(word,tag);
 					identified_tags.put(query,tag);
 					
 				}
 			else
 			{
 				if(identified_tags.get(query)==null)
 				identified_tags.put(query, "misc");
 			}
			}
		}
		else
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
		} //else
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
public static void main(String[] args)
{
	HashMap<String,String> hm=new HashMap<String,String>();
    hm=buildDictionary("C:\\Users\\sindu_000\\TISProject\\ReviewMiner\\src\\hotel_bootstrapping_new.dat");
	
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
    java.util.ArrayList<String> queryList=new java.util.ArrayList<String>();
    queryList.add("hotel location price");
    queryList.add("chicago");
    queryList.add("good location");
    queryList.add("good location");
    queryList.add("$200");
    queryList.add("cheap price");
 
    HashMap<String,String> hm_result=new HashMap<String,String>();
    hm_result=getFromDictionary(hm,queryList);
     for(String key:hm_result.keySet())
    	System.out.println(key+"=>"+hm_result.get(key));
    	
}
}
