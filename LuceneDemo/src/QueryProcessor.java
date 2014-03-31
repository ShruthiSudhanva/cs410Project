/*Pre process query here*/

import java.io.IOException;

import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;
import LBJ2.nlp.SentenceSplitter;
import LBJ2.nlp.WordSplitter;
import LBJ2.nlp.seg.PlainToTokenParser;
import LBJ2.nlp.seg.Token;

public class QueryProcessor {

	public QueryProcessor()
	{
		
	}
	
	public void test() {
		Chunker tagger = new Chunker();
		String query = "Hotel Chicago in Seattle";
		String[] tokens = query.split("[ ]+");
		String[] args = {query};
		PlainToTokenParser parser =
			      new PlainToTokenParser(
			        new WordSplitter(
			          new SentenceSplitter(args)));
		Object nextObject;
		int token = 0;
		while((nextObject = parser.next())!=null)
		{
			Token w = (Token) nextObject;
			String tag = tagger.discreteValue(w);
			System.out.println(tag+" "+tokens[token++]);
		}
			
	}
	
	public static void main(String args[]) throws IOException{
		QueryProcessor qProcessor = new QueryProcessor();
		/* This is slow. Not sure why. And needs more parsing!*/
		qProcessor.test();
		String jarPath = "C:\\My Box Files\\CS410_TIS\\Project";
		String classpathString = jarPath+"\\LBJPOS.jar:"+jarPath+"\\LBJChunk.jar:"+jarPath+"\\LBJ2Library.jar";
		String file = "test.txt";
		String cmd = "java -classpath "+ classpathString + " LBJ2.nlp.seg.SegmentTagPlain edu.illinois.cs.cogcomp.lbj.chunk.Chunker "+file;
		//System.out.println(cmd);
		//Process p = Runtime.getRuntime().exec(cmd);
		
	}
	
}
