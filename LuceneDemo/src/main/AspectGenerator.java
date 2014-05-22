package main;
/*
Uses hotel_bootstrapping_new.dat to assign aspects to noun phrases passed as input
Called from QueryProcessor class
*/
import java.io.*;
import java.util.*;
public class AspectGenerator{
	private HashMap<String, String> dictionary;
	/*Replace the path with the path to your src*/
	private String path = "/home/shruthi/cs410Project/LuceneDemo/src/"; 
	
	/*Constructor generates a dictionary from the file containing words and aspects*/
	public AspectGenerator()
	{
		dictionary = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path + "hotel_bootstrapping_new.dat"));
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
			if(word.contains("$") || word.contains("%") || word.matches(".*\\d+")){
				return "value";
			}
		}
		return "misc";
	}
	
	public static void main(String args[])
	{
		AspectGenerator aspectGenerator = new AspectGenerator();
		
		System.out.println(aspectGenerator.generateAspects("200"));
	}
	
}
