import java.io.File;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause;


public class HotelIndexer 
{
	
	public static String INDEX_DIRECTORY = "indexDirectory";
	
	public static void buildIndex() {
		try
		{
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);;
			DatasetParser parser = new DatasetParser("/home/shruthi/cs410Project/LuceneDemo/src/TripAdvisor/sample");
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
			Directory index = FSDirectory.open(new File( INDEX_DIRECTORY ));
			IndexWriter indexWriter = new IndexWriter(index, config);
			//	Specify the analyzer for tokenizing text.
		    //	The same analyzer should be used for indexing and searching
			File [] listofFiles = parser.getListOfFiles();
			for(File file: listofFiles){
				Hotel hotel = parser.parse(file);
				for(String review: hotel.getReviews()){
					addDoc(indexWriter, hotel.getLocation(), hotel.getHotelName(), review);
				}
			}
			/*addDoc(indexWriter,"Chicago", "Best Western Downtown","close to lake navy pier");
			addDoc(indexWriter,"Chicago", "Whitehall Suites","right on the magnificient mile");
			addDoc(indexWriter,"New York", "Marriott New York","great location times square");
			addDoc(indexWriter,"New York", "Hilton Times Square","a block from times square");
			addDoc(indexWriter,"New York", "Holiday Inn Central","view central park from the window");
			addDoc(indexWriter,"Seattle", "Best Western Pioneer Square Hotel","cheap and comfortable");*/
			indexWriter.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private static void addDoc(IndexWriter w,  String location, String name, String contents) throws IOException 
	{
		  Document doc = new Document();
		  // A text field will be tokenized
		  doc.add(new TextField("location", location, Field.Store.YES));
		  // We use a string field for name because we don\'t want it tokenized
		  doc.add(new TextField("name", name, Field.Store.YES));
		  doc.add(new TextField("contents", contents, Field.Store.YES)); 
		  w.addDocument(doc);
	}
	
	
	public static void searchIndex() throws ParseException, IOException
	{
		BooleanQuery qry = new BooleanQuery();
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
		//	Text to search
		String location = "Seattle";
		String name = "";
		String other = "downtown";
		if(!location.isEmpty())
		{
			Query query2 = new QueryParser(Version.LUCENE_47, "location",analyzer).parse(location);
			qry.add(query2, BooleanClause.Occur.SHOULD);
		}
		if(!name.isEmpty()){
			 Query query3 = new QueryParser(Version.LUCENE_47, "name",analyzer).parse(name);
			 qry.add(query3, BooleanClause.Occur.SHOULD);
		}
		if(!other.isEmpty()){
			Query query4 = new QueryParser(Version.LUCENE_47, "contents",analyzer).parse(other);
			qry.add(query4, BooleanClause.Occur.SHOULD);
		}
		// Searching code
		int hitsPerPage = 10;
		Directory index =  FSDirectory.open(new File( INDEX_DIRECTORY ));
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
	      System.out.println((i + 1) + ". " +d.get("name") + "\t" + d.get("location") +"\t" + d.get("contents"));
	    }
	    
	    // reader can only be closed when there is no need to access the documents any more
	    reader.close();
	}
	
	public static void main(String[] args)
	{
		try {
			//HotelIndexer.buildIndex();
			HotelIndexer.searchIndex();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}