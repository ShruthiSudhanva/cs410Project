package main;
/* This class processes the text query given as input
 * Tip : Use class in a server for faster response
 * 
 */

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import queryparse.*;

import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;
import LBJ2.nlp.SentenceSplitter;
import LBJ2.nlp.WordSplitter;
import LBJ2.nlp.seg.PlainToTokenParser;
import LBJ2.nlp.seg.Token;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

/*The class which performs all processing on input text query*/
public class QueryProcessor {

	public static LexicalizedParser lp;
	public static TreebankLanguagePack tlp;
	GrammaticalStructureFactory gsf;
	public static HashMap<String, Integer> adverbMap;
	public static HashMap<String, Integer> adjectiveMap;	
	public static HashMap<String,String> priceAdjectives;
	public static ArrayList<String> locationWords;
	public QueryObject queryObject;
	public Chunker tagger;
	public static HashSet<String> stop = new HashSet<String>();
	private static String path = "/home/shruthi/cs410Project/LuceneDemo/src/";
	
	/*Constructor*/
	public QueryProcessor() throws IOException
	{
		/* Load and initialize Chunker and Dependency parser*/
		if(lp==null)
		{
			lp = LexicalizedParser.loadModel(
				"edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz",
				"-maxLength", "80", "-retainTmpSubcategories");
		}
		if(tlp==null)
			tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();
		tagger = new Chunker(); 
		createWordMaps();
		buildStopWords();
	}

	/*A module to read a list of adverbs and adjectives to parse for*/
	public void createWordMaps() throws NumberFormatException, IOException
	{
		if(adverbMap==null)
		{
			adverbMap = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new FileReader(path + "adverbWords.txt"));
			String line;
			while ((line = br.readLine()) != null) {
			   String [] field = line.split(" ");
			   adverbMap.put(field[0], Integer.parseInt(field[1]));
			}
			br.close();
		}
		if(adjectiveMap==null)
		{
			adjectiveMap = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new FileReader(path + "adjectiveWords.txt"));
			String line;
			while ((line = br.readLine()) != null) {
			   String [] field = line.split(" ");
			   adjectiveMap.put(field[0], Integer.parseInt(field[1]));
			}
			br.close();
		}
		if(priceAdjectives==null)
		{
			priceAdjectives = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(new FileReader(path + "priceAdjectives.txt"));
			String line;
			while ((line = br.readLine()) != null) {
			   String [] fields = line.split(" ");
			   priceAdjectives.put(fields[0], fields[1]);
			}
			br.close();
		}
		if(locationWords==null)
		{
			locationWords = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(path + "locationDictionary.txt"));
			String line;
			while ((line = br.readLine()) != null) {
			   String [] fields = line.split(" ");
			   for(String word: fields)
				   locationWords.add(word);
			}
			br.close();
		}
	}
	
	
	/*A intermediary class to store multiple return values from module dependencyParse*/
	public class ReturnValue{
		public String nounPhrase;
		public String parsedNounPhrase;
		public int rating;
	}
	
	
	/*Identifies adverbs and adjectives and assigns weights defined in adjectiveWords.txt and adverbWords.txt*/
	public ReturnValue dependencyParse(String query) {
		
		ReturnValue returnvalue = new ReturnValue();
		returnvalue.parsedNounPhrase = query;
		String[] sent = query.split("[ ]+");
		Tree parse = lp.apply(Sentence.toWordList(sent));
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		Iterator iterator = tdl.iterator();
		float rating = 0;
		while(iterator.hasNext()) {
			TypedDependency tdDependency = (TypedDependency)iterator.next();
			if(tdDependency.reln().getShortName().equals("amod"))
			{
				String adjective = tdDependency.dep().pennString().trim();
				if(adjectiveMap.containsKey(adjective))
				{
					rating+=adjectiveMap.get(adjective);
					returnvalue.parsedNounPhrase = returnvalue.parsedNounPhrase.replace(adjective, "");
				}
			}
			if(tdDependency.reln().getShortName().equals("advmod"))
			{
				String adverb = tdDependency.dep().pennString().trim();
				if(adverbMap.containsKey(adverb))
				{
					rating+=adverbMap.get(adverb);
					returnvalue.parsedNounPhrase = returnvalue.parsedNounPhrase.replace(adverb, "");
				}
			}
	    }
		returnvalue.nounPhrase = query;
		returnvalue.rating = (int)rating*3/10;
		return returnvalue;
	}
	
	
	/*Chunks the input query into phrases*/
	public ArrayList<String> chunk(String query) {
		
		ArrayList<String> nounPhrases = new ArrayList<String>();
		String[] tokens = query.split("[ ]+");
		String[] args = {query};
		PlainToTokenParser parser =
			      new PlainToTokenParser(
			        new WordSplitter(
			          new SentenceSplitter(args)));
		Object nextObject;
		int token = 0;
		String nounPhrase = null;			
		String sign = "";
		while((nextObject = parser.next())!=null)
		{
			Token w = (Token) nextObject;
			String tag = tagger.discreteValue(w);
			//System.out.println(tokens[token] + " " + tag);
			if(priceAdjectives.containsKey(tokens[token])){
				sign = priceAdjectives.get(tokens[token]);
			}
			if(tokens[token].matches("\\d+") || tokens[token].contains("$")){
				tokens[token] = sign + tokens[token];
			}
			if(tag.contains("B-NP") )
			{
				if(nounPhrase!=null)
					nounPhrases.add(nounPhrase);
				nounPhrase = tokens[token];
			}
			if(tag.contains("I-NP")|| tag.contains("ADJP") || tag.contains("ADVP") || tag.contains("B-PP"))
			{
				if(!stop.contains(tokens[token]))
					nounPhrase = nounPhrase.concat(" "+tokens[token]);
			}
			token++;
		}
		nounPhrases.add(nounPhrase);
		System.out.println(nounPhrases);
		return nounPhrases;
	}
	
	/*Create specification objects for each aspect and assign rating, weights and phrases*/
	public void addToSpecification(String aspect, ReturnValue returnValue, HashMap<String, Set<String>> aspectSet)
	{
		Specification specification;
		aspect = aspect.trim();
		if(!aspect.equals("misc"))
		{
			returnValue.rating +=2;
		};
		if(queryObject.hasSpecification(aspect))
		{
			specification = queryObject.getSpecification(aspect);
			specification.setRating(Math.max(returnValue.rating, specification.getRating()));
		}
		else 
		{
			specification = new Specification(aspect);
			specification.setRating(returnValue.rating);
		}
		if(aspect.contains("value"))
		{
			for(String word: aspectSet.get(aspect))
			{
				specification.setMapEntry("price", returnValue.nounPhrase.trim());
				System.out.println("value " + specification.getMapEntry("price")+ " " + specification.getRating());
			}
		}
		if(aspect.contains("location"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("location", returnValue.nounPhrase.trim());
					System.out.println("location " + specification.getMapEntry("location")+" "+specification.getRating());
			}
		}
		if(aspect.contains("room"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("room", returnValue.nounPhrase.trim());
					System.out.println("room " + specification.getMapEntry("room")+" " + specification.getRating());
			}
		}
		if(aspect.contains("service"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("service", returnValue.nounPhrase.trim());
					System.out.println("service " + specification.getMapEntry("service")+" " + specification.getRating());
			}
		}
		if(aspect.contains("cleanliness"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("cleanliness", returnValue.nounPhrase.trim());
					System.out.println("cleanliness " + specification.getMapEntry("cleanliness")+" " + specification.getRating());
			}
		}
		if(aspect.contains("misc"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("misc", returnValue.nounPhrase.trim());
					System.out.println("misc " + specification.getMapEntry("misc")+" " + specification.getRating());
			}
		}
		queryObject.setSpecification(specification);
	}
	
	/*Module to assign relative weights to each aspect*/
	public void setWeight() {
		int sumRating = 0;
		HashMap<String, Specification> aspects = queryObject.getAspects();
		for(String aspect: aspects.keySet())
		{
			sumRating += queryObject.getSpecification(aspect).ratingWeight;
		}
		for(String aspect: aspects.keySet())
		{
			int rating = queryObject.getSpecification(aspect).ratingWeight;
			System.out.println(aspect+" "+(float)rating/sumRating);
			queryObject.getSpecification(aspect).setWeight((float)rating/sumRating);
		}
	}

	/*Module to create a dictionary for stop words*/
	public static void buildStopWords()
	{
		try{
			BufferedReader br = new BufferedReader(new FileReader(path + "stop_words.txt"));
			String line;
			while((line=br.readLine())!=null)
			   {
				stop.add(line);
			   }
			br.close();
		}catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	/*Main module invoked by user. Returns a QueryObject containing specifications and all details*/
	public QueryObject processQuery(String query) {
		ArrayList<String> nounPhrases = chunk(query);
		AspectGenerator aspectGenerator = new AspectGenerator();
		String hotelName = nounPhrases.remove(0).replace("Find hotel", "").trim();
		if(hotelName.length()>0)
			System.out.println(hotelName);
		queryObject = new QueryObject(hotelName);
		String location = nounPhrases.remove(0).trim();
		System.out.println(location);
		queryObject.setLocation(location);
		for(String nounPhrase: nounPhrases)
		{
			ReturnValue returnValue = dependencyParse(nounPhrase);
			HashMap<String, Set<String>> aspectSet = aspectGenerator.generateAspects(returnValue.parsedNounPhrase.trim());
			String aspect = aspectGenerator.getMaxAspect(aspectSet);
			addToSpecification(aspect, returnValue, aspectSet);
		}
		setWeight();
		return queryObject;
	}
	
	
	/*An example for usage*/
	public static void main(String args[]) throws IOException{
		QueryProcessor qProcessor = new QueryProcessor();
		String query = "Find hotels in Seattle having price below $200 and in city center and good service";
		QueryObject queryObject = qProcessor.processQuery(query);
	}
}
