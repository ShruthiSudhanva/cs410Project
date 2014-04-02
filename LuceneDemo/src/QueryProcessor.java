/*Pre process query here*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;
import LBJ2.nlp.SentenceSplitter;
import LBJ2.nlp.WordSplitter;
import LBJ2.nlp.seg.PlainToTokenParser;
import LBJ2.nlp.seg.Token;

public class QueryProcessor {

	public QueryProcessor()
	{
		
	}
	
	public void chunk() {
		Chunker tagger = new Chunker();
		String query = "clean and big hotel in Seattle in good location";
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
			System.out.println(tag+" "+tokens[token++]);
		}
		nounPhrases.add(nounPhrase);
		System.out.println(nounPhrases);
			
	}
	
	public static void main(String args[]) throws IOException{
		QueryProcessor qProcessor = new QueryProcessor();
		/* This is slow. Not sure why. And needs more parsing!*/
		qProcessor.chunk();
	}
	
}
