/**
 * 
 */
package indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author hongning
 * generic indexing facilities
 */
public abstract class Indexer {
	
	enum QueryType {
		QT_term,
		QT_phrase,
		QT_vector
	}
	
	class QueryItem {
		String m_field;
		QueryType m_type;
		
		public QueryItem(String field, QueryType type){
			m_field = field;
			m_type = type;
		}
	}
	
	protected final int MaxResultSize = 500; // maximal return size
	
	protected String m_indexPath;
	StandardAnalyzer m_analyzer;
	IndexSearcher m_searcher;
	QueryItem[] m_queryTypeList;
	
	public Indexer(String indexPath){
		m_indexPath = indexPath;
		m_analyzer = new StandardAnalyzer(Version.LUCENE_46);
		m_searcher = null;
	}
	
	//build index for the loaded file
	public void IndexFile(String filename){
		try {
			File path = new File(m_indexPath);
			Directory dir = FSDirectory.open(path);	
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46, m_analyzer);
			
			if (!path.exists())
				iwc.setOpenMode(OpenMode.CREATE);
			else
				iwc.setOpenMode(OpenMode.APPEND);
			
			IndexWriter writer = new IndexWriter(dir, iwc);
		    indexDoc(writer, filename);
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//index the specific fields loaded from file
	protected abstract void indexDoc(IndexWriter writer, String filename) throws IOException;
	//build up the query parser for vector-typed queries
	protected Map<String, QueryParser> prepareQParsers(){
		HashMap<String, QueryParser> parserlist = new HashMap<String, QueryParser>();
		for(QueryItem qit:m_queryTypeList){
			if (qit.m_type == QueryType.QT_vector){
				parserlist.put(qit.m_field, new QueryParser(Version.LUCENE_46, qit.m_field, m_analyzer));
			}
		}
		
		return parserlist;
	}
	
	//search in the index
	public ArrayList<String> searchIndex(String queryStr){
		ArrayList<String> results = null;
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(m_indexPath)));
			m_searcher = new IndexSearcher(reader);
		    
			String[] terms = queryStr.trim().split(" ");
			PhraseQuery phrase;
			QueryParser parser;
			Map<String, QueryParser> parserTable = prepareQParsers();//prepare the query parsers for each field
			
			for(QueryItem qit:m_queryTypeList){//iterate through all the searchable fields in the given order
	        	if (qit.m_type == QueryType.QT_term){
	        		if ((results=search4items(new TermQuery(new Term(qit.m_field, queryStr)))) != null)
			        	return results;
	        	} else if (qit.m_type == QueryType.QT_phrase){
	        		phrase = new PhraseQuery();
		        	for(String t:terms)
		        		phrase.add(new Term(qit.m_field, t));
		        	if ((results=search4items(phrase)) != null)
		        		return results;
	        	} else if (qit.m_type == QueryType.QT_vector){
	        		parser = parserTable.get(qit.m_field);
	        		if ((results=search4items(parser.parse(queryStr))) != null)
	        			return results;
	        	}
	        }
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
	
	//codes for debugging purpose
	public void searchIndex(){
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(m_indexPath)));
			m_searcher = new IndexSearcher(reader);
		    Map<String, QueryParser> parserTable = prepareQParsers();//prepare the query parsers for each field
		    QueryParser parser;
		    String[] terms;
		    PhraseQuery phrase;
		    
		    BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		    while (true) {
		    	System.out.println("Enter query: ");
		    	String line = in.readLine();
		    	if (line == null || line.length() == -1)
		            break;

		        line = line.trim().toLowerCase();//to lower case
		        if (line.length() == 0)
		            break;
		        
		        terms = line.split(" ");
		        for(QueryItem qit:m_queryTypeList){//iterate through all the searchable fields
		        	if (qit.m_type == QueryType.QT_term){
		        		if (doSearch(new TermQuery(new Term(qit.m_field, line))))
				        	break;
		        	} else if (qit.m_type == QueryType.QT_phrase){
		        		phrase = new PhraseQuery();
			        	for(String t:terms)
			        		phrase.add(new Term(qit.m_field, t));
			        	if (doSearch(phrase))
			        		break;
		        	} else if (qit.m_type == QueryType.QT_vector){
		        		parser = parserTable.get(qit.m_field);
		        		if (doSearch(parser.parse(line)))
		        			break;
		        	}
		        }
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	//basic module to access the lucene index for debugging purpose
	protected boolean doSearch(Query q) throws IOException{
		TopDocs results = m_searcher.search(q, MaxResultSize);
        ScoreDoc[] hits = results.scoreDocs;
        if (hits.length>0){
        	System.out.println("Hit by " + q + " with " + hits.length +  " results...");
       
	        for(int i=0; i<Math.min(10, hits.length); i++){
	        	Document doc = m_searcher.doc(hits[i].doc);
	        	System.out.println(doc.get("name"));
	        }
	        return true;
        } else 
        	return false;
	}
	
	//basic module to access the lucene index
	protected ArrayList<String> search4items(Query q) throws IOException{
		TopDocs results = m_searcher.search(q, MaxResultSize);
        ScoreDoc[] hits = results.scoreDocs;
        if (hits.length>0){
        	ArrayList<String> hotelist = new ArrayList<String>();
        	for(int i=0; i<hits.length; i++){
	        	Document doc = m_searcher.doc(hits[i].doc);
	        	hotelist.add(doc.get("ID"));
	        }
        	return hotelist;
        }
        return null;
	}
}
