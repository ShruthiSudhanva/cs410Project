package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.Collections;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;

public class CustomAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader){
		
		Tokenizer source = new ClassicTokenizer(Version.LUCENE_47, reader);
	    TokenStream filter = new StandardFilter(Version.LUCENE_47, source);
	    filter = new LowerCaseFilter(Version.LUCENE_47,filter);
	    SynonymMap mySynonymMap = null;
		try {
			mySynonymMap = buildSynonym();
		} catch (IOException | ParseException e) {
			
			e.printStackTrace();
		}
	    filter = new SynonymFilter(filter, mySynonymMap, false);	    
	    return new TokenStreamComponents(source, filter);
	}
	
	private SynonymMap buildSynonym() throws IOException, ParseException
	{
		File file = new File("C:\\My Box Files\\CS410_TIS\\Project\\prolog\\wn_s.pl");
		InputStream stream = new FileInputStream(file);
		Reader rulesReader = new InputStreamReader(stream); 
		SynonymMap.Builder parser = null;
        parser = new WordnetSynonymParser(true, true, new StandardAnalyzer(Version.LUCENE_47));
        ((WordnetSynonymParser) parser).parse(rulesReader);         
        SynonymMap synonymMap = parser.build();
        return synonymMap;
	}
}
