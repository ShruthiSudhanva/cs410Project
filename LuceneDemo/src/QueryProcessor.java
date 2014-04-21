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
	}
	
	
	public float dependencyParse(String query) {
		
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
		System.out.println(query + " " +rating/2);
		return rating/2;
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
			if(tag.contains("B-NP"))
			{
				if(nounPhrase!=null)
					nounPhrases.add(nounPhrase);
				nounPhrase = tokens[token];
			}
			if(tag.contains("I-NP"))
			{
				nounPhrase = nounPhrase.concat(" "+tokens[token]);
			}
			token++;
		}
		nounPhrases.add(nounPhrase);
		System.out.println(nounPhrases);
		return nounPhrases;
	}
	
	public static void main(String args[]) throws IOException{
		QueryProcessor qProcessor = new QueryProcessor();
		/* This is slow. Not sure why. And needs more parsing!*/
		String query = "Find extremely cheap hotels in exceptionally good location in Chicago";
		ArrayList<String> nounPhrases = qProcessor.chunk(query);
		//Change AspectGenerator to take input as ArrayList of strings
		//Change output to be chunk => aspect 
		/*Sindu's function called here*/
		
		//To find hotel location - prep_in and prep_near???
		//To find hotel name???
		
		//Do the following to only the chunks that returned an aspect
		//Create a QueryObject
		QueryObject queryObject = new QueryObject("");
		for(String nounPhrase: nounPhrases)
		{
			float rating = qProcessor.dependencyParse(nounPhrase);
			//for each corresponding aspect create a specification
				//Specification specification = new Specification(aspect);
				//specification.setRating(rating);
		}
	}
}
