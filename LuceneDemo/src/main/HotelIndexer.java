package main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericFieldConfigListener;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause;

import queryparse.Specification;


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
			File [] listofFiles = parser.getListOfFiles();
			for(File file: listofFiles){
				Hotel hotel = parser.parse(file);
				if(hotel.getLocation() == null || hotel.getHotelName() == null || hotel.getReviews() == null)
					continue;
				addDoc(indexWriter, hotel.getLocation(), hotel.getHotelName(), hotel.getReviews(), hotel.getId(), hotel.ratings, hotel.count);
			}
			indexWriter.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private static void addDoc(IndexWriter w,  String location, String name, ArrayList<String> contents, String id, HashMap<String,Float> ratings, long count) throws IOException 
	{
		Document doc = new Document();
		doc.add(new TextField("id", id,Field.Store.YES));
		doc.add(new TextField("location", location, Field.Store.YES));
		doc.add(new TextField("name", name, Field.Store.YES));
		doc.add(new FloatField("value", ratings.get("value") , Field.Store.YES));
		doc.add(new FloatField("loc", ratings.get("location") , Field.Store.YES));
		doc.add(new FloatField("room", ratings.get("room") , Field.Store.YES));
		doc.add(new FloatField("service", ratings.get("service") , Field.Store.YES));
		doc.add(new FloatField("cleanliness", ratings.get("cleanliness") , Field.Store.YES));
		doc.add(new LongField("count", count, Field.Store.YES));
		for(String review: contents){
			doc.add(new TextField("contents", review , Field.Store.YES));
		}
		w.addDocument(doc);
	}
	
	public static ArrayList<Hotel> simpleSearch(String location, String name, String other) throws ParseException, IOException
	{
		System.out.println("location "+location+" name "+ name +" other " +other);
		ArrayList<Hotel> results = new ArrayList<Hotel>();
		BooleanQuery qry = new BooleanQuery();
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
			qry.add(query4, BooleanClause.Occur.MUST);
		}
		Directory index =  FSDirectory.open(new File( INDEX_DIRECTORY ));
	    IndexReader reader = DirectoryReader.open(index);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    SortField[] sortFields = new SortField[]{ new SortField("loc", SortField.Type.FLOAT, true), new SortField("count", SortField.Type.LONG, true)};
	    TopDocs hits = searcher.search(qry,10000);//, new Sort(sortFields));//collector.topDocs().scoreDocs;
	    //	Code to display the results of search
	    ScoreDoc [] docs = hits.scoreDocs;
	    System.out.println("Found " + hits.totalHits + " hits.");
	    for(int i=0;i<hits.totalHits;++i) 
	    {
	      int docId = docs[i].doc;
	      Document d = searcher.doc(docId);
	      String resultHotelName = d.get("name");
	      String resultHotelLocation = d.get("location");
	      String resultReviews = d.get("contents");
	      //String review = d.get("contents");
	      //System.out.println(d.get("id") + ". " +resultHotelName + "\t" + resultHotelLocation +"\t" + resultReviews +"\t" + d.get("value"));
	      System.out.println(d.get("id") + ". " +resultHotelName + "\t" + d.get("loc") + "\t" + d.get("count"));
	      //Check if hotel in list and add review to it
	      Hotel hotel = new Hotel();
	      hotel.setHotelName(resultHotelName);
	      hotel.setLocation(resultHotelLocation);
	      hotel.ratings.put("cleanliness", Float.parseFloat(d.get("cleanliness")));
	      hotel.ratings.put("service", Float.parseFloat(d.get("service")));
	      hotel.ratings.put("loc" , Float.parseFloat(d.get("loc")));
	      hotel.ratings.put("room", Float.parseFloat(d.get("room")));
	      hotel.ratings.put("value", Float.parseFloat(d.get("value")));
	      System.out.println(hotel.ratings.get("cleanliness"));
	      //hotel.setReviews(resultReviews);
	      //hotel.addReview(review);
	      results.add(hotel);
	    }
	    
	    // reader can only be closed when there is no need to access the documents any more
	    reader.close();
	    return results;
	}
	
	
	public static ArrayList<Hotel> searchIndex(String location, String name, String other, HashMap<String,Specification> aspects, SortField [] sortList) throws ParseException, IOException
	{
		System.out.println("location "+location+" name "+ name +" other " +other);
		ArrayList<Hotel> results = new ArrayList<Hotel>();
		BooleanQuery qry = new BooleanQuery();
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
			String [] allStrings = other.toLowerCase().split(",");
			for(String s: allStrings)
			{
				PhraseQuery query4 = new PhraseQuery();
				String [] otherString = s.toLowerCase().split(" ");
				for(String o: otherString){
					query4.add(new Term("contents",o));
				}
				query4.setSlop(0);
				qry.add(query4, BooleanClause.Occur.MUST);
			}
			
		}
		float maxRating = 5.0f;
		float weight;
		if(aspects.containsKey("value"))
		{
			weight = (float)aspects.get("value").ratingWeight;
			Query value = NumericRangeQuery.newFloatRange("value", weight, 5.0f, true, true);
			qry.add(value,BooleanClause.Occur.SHOULD);
		}
		if(aspects.containsKey("location"))
		{
			weight = (float)aspects.get("location").ratingWeight;
			Query loc = NumericRangeQuery.newFloatRange("loc", weight, 5.0f, true, true);
			qry.add(loc,BooleanClause.Occur.SHOULD);
		}
		if(aspects.containsKey("service"))
		{
			weight = (float)aspects.get("service").ratingWeight;
			Query service = NumericRangeQuery.newFloatRange("service", weight, 5.0f, true, true);
			qry.add(service,BooleanClause.Occur.SHOULD);
		}
		if(aspects.containsKey("cleanliness"))
		{
			weight = (float)aspects.get("cleanliness").ratingWeight;
			Query clean = NumericRangeQuery.newFloatRange("cleanliness", weight, 5.0f, true, true);
			qry.add(clean,BooleanClause.Occur.SHOULD);
		}
		if(aspects.containsKey("room"))
		{
			weight = (float)aspects.get("room").ratingWeight;
			Query room = NumericRangeQuery.newFloatRange("room", weight, 5.0f, true, true);
			qry.add(room,BooleanClause.Occur.SHOULD);
		}
		// Searching code
		Directory index =  FSDirectory.open(new File( INDEX_DIRECTORY ));
	    IndexReader reader = DirectoryReader.open(index);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    //
	    TopDocs hits = searcher.search(qry,10000);//, new Sort(sortList));//, new Sort(sortFields));//collector.topDocs().scoreDocs;
	    //	Code to display the results of search
	    ScoreDoc [] docs = hits.scoreDocs;
	    System.out.println("Found " + hits.totalHits + " hits.");
	    for(int i=0;i<hits.totalHits;++i) 
	    {
	      int docId = docs[i].doc;
	      Document d = searcher.doc(docId);
	      String resultHotelName = d.get("name");
	      String resultHotelLocation = d.get("location");
	      String resultReviews = d.get("contents");
	      System.out.println(d.get("id")+" Location-->"+d.get("loc")+" Value-->"+d.get("value")+" Service-->"+d.get("service")+" Room-->"+d.get("room")+" Count-->"+d.get("count"));
	      Hotel hotel = new Hotel();
	      hotel.setId(d.get("id"));
	      hotel.setHotelName(resultHotelName);
	      hotel.setLocation(resultHotelLocation);
	      hotel.count = Long.parseLong(d.get("count"));
	      hotel.ratings.put("cleanliness", Float.parseFloat(d.get("cleanliness")));
	      hotel.ratings.put("service", Float.parseFloat(d.get("service")));
	      hotel.ratings.put("loc" , Float.parseFloat(d.get("loc")));
	      hotel.ratings.put("room", Float.parseFloat(d.get("room")));
	      hotel.ratings.put("value", Float.parseFloat(d.get("value")));
	      //System.out.println(hotel.ratings.get("cleanliness"));
	      results.add(hotel);
	    }
	    
	    // reader can only be closed when there is no need to access the documents any more
	    reader.close();
	    return results;
	}
		
	public static void main(String[] args)
	{
		//HotelIndexer.buildIndex();
		String location = "Seattle";
		String name = "";
		String other = "good location,clean room";
		//String [] sortList = {"location","value"};
		SortField[] sortList = new SortField[]{ new SortField("loc", SortField.Type.FLOAT, true), new SortField("count", SortField.Type.LONG, true)};
		/*try {
			ArrayList<Hotel> hotels = HotelIndexer.searchIndex(location,name,other, sortList);
			for(Hotel hotel: hotels){
				//System.out.println(hotel.getHotelName()+"----------\n");
				for(String review: hotel.getReviews()){
					//System.out.println(review);
				}
			}
			//System.out.println(HotelIndexer.getCount("100564", other));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}