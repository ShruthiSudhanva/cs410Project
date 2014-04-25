/*Pre process query here*/

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.management.relation.Relation;

import queryparse.*;

import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;
import LBJ2.nlp.SentenceSplitter;
import LBJ2.nlp.WordSplitter;
import LBJ2.nlp.seg.PlainToTokenParser;
import LBJ2.nlp.seg.Token;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.WordToSentenceProcessor.NewlineIsSentenceBreak;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

import queryparse.*;


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
	
	public QueryProcessor() throws IOException
	{
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
	}

	/*A module to read a list of adverbs and adjectives to parse for*/
	public void createWordMaps() throws NumberFormatException, IOException
	{
		if(adverbMap==null)
		{
			adverbMap = new HashMap<String, Integer>();
			BufferedReader br = new BufferedReader(new FileReader("/home/shruthi/cs410Project/LuceneDemo/src/adverbWords.txt"));
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
			BufferedReader br = new BufferedReader(new FileReader("/home/shruthi/cs410Project/LuceneDemo/src/adjectiveWords.txt"));
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
			BufferedReader br = new BufferedReader(new FileReader("/home/shruthi/cs410Project/LuceneDemo/src/priceAdjectives.txt"));
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
			BufferedReader br = new BufferedReader(new FileReader("/home/shruthi/cs410Project/LuceneDemo/src/locationDictionary.txt"));
			String line;
			while ((line = br.readLine()) != null) {
			   String [] fields = line.split(" ");
			   for(String word: fields)
				   locationWords.add(word);
			}
			br.close();
		}
	}
	
	public class ReturnValue{
		public String nounPhrase;
		public int rating;
	}
	
	public ReturnValue dependencyParse(String query) {
		
		ReturnValue returnvalue = new ReturnValue();
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
					query = query.replace(adjective, "");
				}
			}
			if(tdDependency.reln().getShortName().equals("advmod"))
			{
				String adverb = tdDependency.dep().pennString().trim();
				if(adverbMap.containsKey(adverb))
				{
					rating+=adverbMap.get(adverb);
					query = query.replace(adverb, "");
				}
			}
			//System.out.println(tdDependency + "    Relation->" + tdDependency.reln() + "   Dependency->" + tdDependency.dep().pennString());
	    }
		returnvalue.nounPhrase = query;
		returnvalue.rating = (int)rating*3/10;
		return returnvalue;
	}
	
	public ArrayList<String> chunk(String query) {
		
		//String query = "clean and big hotel in Seattle in good location";
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
		while((nextObject = parser.next())!=null)
		{
			Token w = (Token) nextObject;
			String tag = tagger.discreteValue(w);
			//System.out.println(tokens[token] + " " +tag);
			if(tag.contains("B-NP"))
			{
				if(nounPhrase!=null)
					nounPhrases.add(nounPhrase);
				nounPhrase = tokens[token];
			}
			if(tag.contains("I-NP")|| tag.contains("ADJP") )
			{
				nounPhrase = nounPhrase.concat(" "+tokens[token]);
			}
			token++;
		}
		nounPhrases.add(nounPhrase);
		System.out.println(nounPhrases);
		return nounPhrases;
	}
	
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
			specification.setMapEntry("price", returnValue.nounPhrase);
			System.out.println("value " + specification.getMapEntry("price")+ " " + specification.getRating());
		}
		if(aspect.contains("location"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("location", returnValue.nounPhrase);
					System.out.println("location " + specification.getMapEntry("location")+" "+specification.getRating());
			}
		}
		if(aspect.contains("room"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("room", returnValue.nounPhrase);
					System.out.println("room " + specification.getMapEntry("room")+" " + specification.getRating());
			}
		}
		if(aspect.contains("service"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("service", returnValue.nounPhrase);
					System.out.println("service " + specification.getMapEntry("service")+" " + specification.getRating());
			}
		}
		if(aspect.contains("cleanliness"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("cleanliness", returnValue.nounPhrase);
					System.out.println("cleanliness " + specification.getMapEntry("cleanliness")+" " + specification.getRating());
			}
		}
		if(aspect.contains("misc"))
		{
			for(String word: aspectSet.get(aspect))
			{
					specification.setMapEntry("misc", returnValue.nounPhrase);
					System.out.println("misc " + specification.getMapEntry("misc")+" " + specification.getRating());
			}
		}
		queryObject.setSpecification(specification);
		//System.out.println("return " + specification.getMapEntry("location"));
	}
	
	public void setWeight() {
		int sumRating = 0;
		int numOfRating = 0;
		HashMap<String, Specification> aspects = queryObject.getAspects();
		for(String aspect: aspects.keySet())
		{
			sumRating += queryObject.getSpecification(aspect).ratingWeight;
			numOfRating++;
		}
		for(String aspect: aspects.keySet())
		{
			int rating = queryObject.getSpecification(aspect).ratingWeight;
			System.out.println(aspect+" "+(float)rating/sumRating);
			queryObject.getSpecification(aspect).setWeight((float)rating/sumRating);
		}
	}
	
	public QueryObject processQuery(String query) {
		
		/* This is slow. Not sure why. And needs more parsing!*/
		//To find hotel location - prep_in and prep_near???
		//To find hotel name??? -- Find hotel/suites/inn beside etc.		
		ArrayList<String> nounPhrases = chunk(query);
		AspectGenerator aspectGenerator = new AspectGenerator();
		queryObject = new QueryObject("");
		
		//For aspects not present set rating to default (0)
		for(String nounPhrase: nounPhrases)
		{
			ReturnValue returnValue = dependencyParse(nounPhrase);
			int rating = returnValue.rating;
			//System.out.println(rating);
			HashMap<String, Set<String>> aspectSet = aspectGenerator.generateAspects(returnValue.nounPhrase);
			String aspect = aspectGenerator.getMaxAspect(aspectSet);
			addToSpecification(aspect, returnValue, aspectSet);
		}
		setWeight();
		return queryObject;
	}
	
	public static void main(String args[]) throws IOException{
		QueryProcessor qProcessor = new QueryProcessor();
		String query = "Find hotels with big rooms with very good service in San Francaisco";
		QueryObject queryObject = qProcessor.processQuery(query);
		//System.out.println(queryObject.getAspects());
	}
}
