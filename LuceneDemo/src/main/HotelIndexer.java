package main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
import org.apache.lucene.search.PhraseQuery;
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
	public static Random random;
	
	public static void buildIndex() {
		try
		{
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);;
			DatasetParser parser = new DatasetParser("/home/shruthi/cs410Project/LuceneDemo/src/TripAdvisor/TripAdvisor");
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
			Directory index = FSDirectory.open(new File( INDEX_DIRECTORY ));
			IndexWriter indexWriter = new IndexWriter(index, config);
			random = new Random();
			int i=0;
			//	Specify the analyzer for tokenizing text.
		    //	The same analyzer should be used for indexing and searching
			File [] listofFiles = parser.getListOfFiles();
			for(File file: listofFiles){
				Hotel hotel = parser.parse(file);
				if(hotel.getLocation() == null || hotel.getHotelName() == null || hotel.getReviews() == null)
					continue;
				addDoc(indexWriter, hotel.getLocation(), hotel.getHotelName(), hotel.getReviews(), hotel.getId());
			}
		
			indexWriter.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private static void addDoc(IndexWriter w,  String location, String name, ArrayList<String> contents, String id) throws IOException 
	{
		for(String review: contents){
			  Document doc = new Document();
			  doc.add(new TextField("id", id,Field.Store.YES));
			  doc.add(new TextField("location", location, Field.Store.YES));
			  doc.add(new TextField("name", name, Field.Store.YES));; 
			  doc.add(new TextField("contents", review , Field.Store.YES));
			  w.addDocument(doc);
		}
	}
	
	
	public static ArrayList<Hotel> searchIndex(String location, String name, String other) throws ParseException, IOException
	{
		System.out.println("location"+" "+location+" name"+ name +" other " +other);
		ArrayList<Hotel> results = new ArrayList<Hotel>();
		BooleanQuery qry = new BooleanQuery();
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
		//	Text to search
		if(!location.isEmpty())
		{
			//Query query2 = new QueryParser(Version.LUCENE_47, "location",analyzer).parse(location);
			PhraseQuery query2 = new PhraseQuery();
			String [] locationString = location.toLowerCase().split(" ");
			for(String loc: locationString){
				query2.add(new Term("location",loc));
			}
			query2.setSlop(0);
			qry.add(query2, BooleanClause.Occur.MUST);
		}
		if(!name.isEmpty()){
			PhraseQuery query3 = new PhraseQuery();
			String [] nameString = location.toLowerCase().split(" ");
			for(String n: nameString){
				query3.add(new Term("name",n));
			}
			query3.setSlop(0);
			 qry.add(query3, BooleanClause.Occur.SHOULD);
		}
		if(!other.isEmpty()){
			PhraseQuery query4 = new PhraseQuery();
			String [] otherString = other.toLowerCase().split(" ");
			for(String o: otherString){
				query4.add(new Term("contents",o));
			}
			query4.setSlop(0);
			qry.add(query4, BooleanClause.Occur.SHOULD);
		}
		// Searching code
		int hitsPerPage = 100;
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
	      String resultHotelName = d.get("name");
	      String resultHotelLocation = d.get("location");
	      String [] resultReviews = d.getValues("contents");
	      //String review = d.get("contents");
	      System.out.println(d.get("id") + ". " +resultHotelName + "\t" + resultHotelLocation +"\t" + resultReviews);
	      //Check if hotel in list and add review to it
	      Hotel hotel = new Hotel();
	      hotel.setHotelName(resultHotelName);
	      hotel.setLocation(resultHotelLocation);
	      hotel.setReviews(resultReviews);
	      //hotel.addReview(review);
	      results.add(hotel);
	    }
	    
	    // reader can only be closed when there is no need to access the documents any more
	    reader.close();
	    return results;
	}
	
	public static int getCount(String id, String phrase) throws ParseException, IOException
	{
		System.out.println("id "+" "+id+" phrase "+phrase);
		BooleanQuery qry = new BooleanQuery();
			PhraseQuery phraseQuery = new PhraseQuery();
			String [] phraseString = phrase.toLowerCase().split(" ");
			for(String p: phraseString){
				phraseQuery.add(new Term("contents",p));
			}
			phraseQuery.setSlop(0);
			qry.add(phraseQuery, BooleanClause.Occur.MUST);
			
			PhraseQuery idQuery = new PhraseQuery();
			idQuery.add(new Term("id",id));
			idQuery.setSlop(0);
			qry.add(idQuery, BooleanClause.Occur.MUST);
			
		// Searching code
		int count = 0;
		int hitsPerPage = 100;
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
	      String resultHotelName = d.get("name");
	      String resultHotelLocation = d.get("location");
	      String [] resultReviews = d.getValues("contents");
	      System.out.println(d.get("id") + ". " +resultHotelName + "\t" + resultHotelLocation +"\t" + resultReviews);
	      count++;
	    }
	    
	    // reader can only be closed when there is no need to access the documents any more
	    reader.close();
	    return count;
	}
	
	public static void main(String[] args)
	{
		HotelIndexer.buildIndex();
		/*String location = "Seattle";
		String name = "";
		String other = "clean room";
		try {
			ArrayList<Hotel> hotels = HotelIndexerOrig.searchIndex(location,name,other);
			for(Hotel hotel: hotels){
				//System.out.println(hotel.getHotelName()+"----------\n");
				for(String review: hotel.getReviews()){
					System.out.println(review);
				}
			}
			System.out.println(HotelIndexerOrig.getCount("100564", other));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
