package main;
/*

buildDictionary method takes the hotel_bootstrapping_new.dat file as input and builds the dictionary and results a hashmap.
 
getFromDictionary method takes the Query in the form of only noun phrases in this format:
nounphrase1a nounphrase1b,nounphrase2,nounphrase3,nounphrase3a nounphrase3b
So, it has to be called right after LBJ is called with the query string as the parameter. Since we need the dictionary from previous step - we pass dictionary,query as the parameters.
It returns a hashmap that contains the noun phrases and their tag from the dictionary separated by space.

Note:uncomment the for loops in this code to see output.
*/
import java.io.*;
import java.util.*;
public class AspectGenerator{
	private HashMap<String, String> dictionary;
	public AspectGenerator()
	{
		dictionary = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("/home/shruthi/cs410Project/LuceneDemo/src/hotel_bootstrapping_new.dat"));
			String nextLine;
			while((nextLine = br.readLine())!= null)
			{
				String [] words = nextLine.split(" ");
				String aspect = words[0].replaceAll("<|>", "");
				if(aspect.contains("value"))
				{
					aspect = "value";
				}
				for (int i =1; i< words.length; i++ )
				{
					dictionary.put(words[i], aspect);
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getMaxAspect(HashMap<String, Set<String>> aspectWordList) {
		String maxAspect = "";
		int max = 0;
		for(String key: aspectWordList.keySet())
		{
			if(aspectWordList.get(key).size()>max)
			{
				max = aspectWordList.get(key).size();
				maxAspect = key;
			}
		}
		return maxAspect;
	}
	
	public HashMap<String, Set<String>> generateAspects(String nounPhrase) {
		String aspect = "";	
		String [] words = nounPhrase.split(" ");
		HashMap<String, Set<String>> aspectWordList = new HashMap<String, Set<String>>();
		for(String word: words)
		{
			aspect = getAspect(word);
			if(aspectWordList.containsKey(aspect))
			{
				aspectWordList.get(aspect).add(word);
			}
			else {
				Set<String> wordset = new HashSet<String>();
				wordset.add(word);
				aspectWordList.put(aspect, wordset);
			}
		}
		return aspectWordList;
	}
	
	public String getAspect(String word) {
		if(dictionary.containsKey(word))
		{
			return dictionary.get(word);
		}
		else {
			if(word.contains("$") || word.contains("%")){
				return "value";
			}
		}
		return "misc";
	}
	
	public static void main(String args[])
	{
		AspectGenerator aspectGenerator = new AspectGenerator();
		
		System.out.println(aspectGenerator.generateAspects("central station"));
	}
	
}