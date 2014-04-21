import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause;


public class HotelIndexer 
{
	public static void main(String[] args)
	{
		try
		{
			//	Specify the analyzer for tokenizing text.
		    //	The same analyzer should be used for indexing and searching
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
			
			//	Code to create the index
			Directory index = new RAMDirectory();
			
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
			
			IndexWriter w = new IndexWriter(index, config);
			//Use DataSetParser here
			addDoc(w, "Ace Hotel", "Best Western Downtown close to lake navy pier");
			addDoc(w, "Whitehall Suites", "Wouldn't recommend it. ");
			addDoc(w, "Marriott New York","great location times square");
			addDoc(w, "Hilton Times Square","a block from times square");
			addDoc(w, "Holiday Inn Central","view central park from the window");
			addDoc(w, "Best Western Pioneer Square Hotel","cheap and comfortable");
			w.close();
			
			BooleanQuery qry = new BooleanQuery();
			
			String name = "Hilton";
			String other = "times square";
			Query query1 = new QueryParser(Version.LUCENE_47, "name",analyzer).parse(name);
			Query query2 = new QueryParser(Version.LUCENE_47, "contents",analyzer).parse(other);
			qry.add(query1, BooleanClause.Occur.SHOULD);
			qry.add(query2, BooleanClause.Occur.SHOULD);
			// Searching code
			int hitsPerPage = 10;
		    IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		    searcher.search(qry, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    //	Code to display the results of search
		    System.out.println("Found " + hits.length + " hits.");
		    for(int i=0;i<hits.length;++i) 
		    {
		      int docId = hits[i].doc;
		      Document d = searcher.doc(docId);
		      System.out.println((i + 1) + ". " +d.get("name") + "\t" + d.get("contents"));
		    }
		    
		    // reader can only be closed when there is no need to access the documents any more
		    reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	private static void addDoc(IndexWriter w,  String name, String contents) throws IOException 
	{
		  Document doc = new Document();
		  doc.add(new StringField("name", name, Field.Store.YES));
		  // A text field will be tokenized
		  // We use a string field for name because we don\'t want it tokenized
		  doc.add(new TextField("contents", contents, Field.Store.YES)); 
		  w.addDocument(doc);
	}
}