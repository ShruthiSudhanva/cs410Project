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
	
	
	public int dependencyParse(String query) {
		
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
				}
			}
			if(tdDependency.reln().getShortName().equals("advmod"))
			{
				String adverb = tdDependency.dep().pennString().trim();
				if(adverbMap.containsKey(adverb))
				{
					rating+=adverbMap.get(adverb);
				}
			}
			//System.out.println(tdDependency + "    Relation->" + tdDependency.reln() + "   Dependency->" + tdDependency.dep().pennString());
	    }
		return (int)rating*3/10;
	}
	
	public ArrayList<String> chunk(String query) {
		Chunker tagger = new Chunker();
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
	
	public Specification createSpecification(String aspect, int rating, String nounPhrase, String query)
	{
		Specification specification = new Specification(aspect);
		specification.setRating(rating);
		if(aspect == "value")
		{
			if(nounPhrase.contains("$"))
				for(String word: priceAdjectives.keySet())
					if(query.contains(word))
						specification.setMapEntry("price", priceAdjectives.get(word)+nounPhrase);
		}
		if(aspect == "location")
		{
			for(String word: locationWords)
			{
				if(nounPhrase.contains(word))
				{
					specification.setMapEntry("location", word);
				}
			}
		}
		if(aspect == "room" || aspect == "service" || aspect == "cleanliness")
		{
			specification.setMapEntry("misc", nounPhrase);
		}
		return specification;
	}
	
	public QueryObject processQuery(String query) {
		
		/* This is slow. Not sure why. And needs more parsing!*/
		
		ArrayList<String> nounPhrases = chunk(query);
		/*Sindu's function called here*/
		HashMap<String, String> aspectsHashMap = new HashMap<String, String>();
		//To find hotel location - prep_in and prep_near???
		//To find hotel name??? -- Find hotel/suites/inn beside etc.
		
		//Do the following to only the chunks that returned an aspect
		//Create a QueryObject
		aspectsHashMap.put("Hotels", "misc");
		aspectsHashMap.put("central Seattle", "location");
		aspectsHashMap.put("excellent service", "service");
		QueryObject queryObject = new QueryObject("");
		//For aspects not present set rating to default (0)
		for(String nounPhrase: nounPhrases)
		{
			int rating = dependencyParse(nounPhrase);
			String aspect = aspectsHashMap.get(nounPhrase);
			if(!aspect.equals("misc"))
			{
				rating+=2;
				System.out.println(nounPhrase + "-" +rating);
				Specification specification = createSpecification(aspect, rating, nounPhrase, query);
				//System.out.println(specification.getMapEntry("location"));
				queryObject.setSpecification(specification);
			}
		}
		return queryObject;
	}
	
	public static void main(String args[]) throws IOException{
		QueryProcessor qProcessor = new QueryProcessor();
		String query = "Hotels in central Seattle with excellent service";
		QueryObject queryObject = qProcessor.processQuery(query);
	}
}
