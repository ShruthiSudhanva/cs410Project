package indexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;

import records.Address;

public class HotelIndexer extends Indexer {
	static String[] stopwords = {"road", "street", "drive", "dr", "pkwy"};	
	
	public HotelIndexer(String indexPath){
		super(indexPath);
		m_queryTypeList = new QueryItem[] {
				new QueryItem("postalcode", QueryType.QT_term), 
				new QueryItem("city_state", QueryType.QT_term), 
				new QueryItem("city", QueryType.QT_phrase), 
				new QueryItem("state", QueryType.QT_term), 
				new QueryItem("country", QueryType.QT_term), 
				new QueryItem("name", QueryType.QT_phrase), 
				new QueryItem("description", QueryType.QT_phrase), 
				new QueryItem("name", QueryType.QT_vector), 
				new QueryItem("description", QueryType.QT_vector)
		};
	}
		
	@Override
	protected void indexDoc(IndexWriter writer, String filename) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
		String tmpTxt, container[];
		Address addr;
		Document doc;
		int docSize = 0, fullAddress = 0;
		while((tmpTxt=reader.readLine())!=null){
			if (tmpTxt.isEmpty())
				continue;
			container = tmpTxt.trim().split("\t");
			doc = new Document();
			doc.add(new StringField("ID", container[0], Field.Store.YES));
			doc.add(new TextField("name", container[1], Field.Store.YES));
			
			if (container[2].equals("null")==false) {
				addr = new Address(container[2], true);
				doc.add(new TextField("description", addr.toIndexString(), Field.Store.NO));//combination of street, extend-street, state
				
				if (addr.m_city != null)
					doc.add(new TextField("city", addr.m_city, Field.Store.NO));//to enable phrase query
				
				if (addr.m_state != null)
					doc.add(new StringField("state", addr.m_state, Field.Store.NO));
				
				if (addr.m_city != null && addr.m_state != null)
					doc.add(new StringField("city_state", String.format("%s, %s", addr.m_city, addr.m_state), Field.Store.NO));
				
				if (addr.m_postalcode != null)
					doc.add(new StringField("postalcode", addr.m_postalcode, Field.Store.NO));
				
				if (addr.m_country != null)
					doc.add(new StringField("country", addr.m_country, Field.Store.NO));
				
				fullAddress ++;
			}
			
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            writer.addDocument(doc);
			} else {	                        
				writer.updateDocument(new Term("ID", container[0]), doc);
			}
			
			docSize ++;
		}
		reader.close();
		System.out.println(String.format("[Info]Indexed (%d/%d) documents...", fullAddress, docSize));
	}
	
	public static void main(String[] args) {
		HotelIndexer indexer = new HotelIndexer("/home/shruthi/cs410Project/LuceneDemo/Hotel");
		//indexer.IndexFile("Data/Info/hotelist.txt");
		System.out.println(indexer.searchIndex("Seattle Sheraton"));
	}
}
