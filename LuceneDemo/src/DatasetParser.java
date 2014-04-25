import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Hotel{
	private String hotelName;
	private String location;
	private ArrayList<String> reviews;
	
	public Hotel() {
		// TODO Auto-generated constructor stub
		reviews = new ArrayList<String>();
	}
	
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	
	public String getHotelName() {
		return hotelName;
	}
	
	public String getLocation() {
		return location;
	}
	
	public ArrayList<String> getReviews() {
		return reviews;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void addReview(String review) {
		reviews.add(review);
	}
	
}

/*Parse the tripadvisor dataset*/
public class DatasetParser {

	private File folder;
	private BufferedReader bReader;
	private File [] listOfFiles;
	public DatasetParser(String dirpath) throws IOException
	{
		folder = new File(dirpath);
		listOfFiles = folder.listFiles();
	}
	//List all files in folder
	//Read one by one
	//Read so called title
	//Read each review
	
	public File [] getListOfFiles()
	{
		return listOfFiles;
	}
	
	public Hotel parse(File file) throws IOException {
		Hotel hotel = new Hotel();
		String path = file.getAbsolutePath();
		bReader = new BufferedReader(new FileReader(path));
		String line,url="",content="",location="";
		String hotelName="";
		while((line = bReader.readLine())!=null){
			if(line.contains("<Hotel Name>"))
			{
				hotelName = line.replace("<Hotel Name>", "");
				hotel.setHotelName(hotelName);
			}
			if (line.contains("<URL>")) {
				Pattern pattern = Pattern.compile("-(\\w*).html");
				Matcher matcher = pattern.matcher(line);
				if (matcher.find())
				{
				    location = matcher.group(1).replaceAll("_", " ");
				    hotel.setLocation(location);
				}
			}
			if (line.contains("<Content>")) {
				content = line.replace("<Content>", "");
				hotel.addReview(content);
			}
		}
		bReader.close();
		return hotel;
	}
	
	public static void main(String args[]) {
		DatasetParser parser;
		try {
			parser = new DatasetParser("/home/shruthi/cs410Project/LuceneDemo/src/TripAdvisor/sample");
			File [] listofFiles = parser.getListOfFiles();
			for(File file: listofFiles){
				Hotel hotel = parser.parse(file);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
}

