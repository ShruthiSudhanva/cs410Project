package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Hotel{
	private String hotelName;
	private String location;
	private ArrayList<String> reviews;
	private String id;
	HashMap<String, Float> ratings;
	long count;
	long valueCount;
	long roomCount;
	long serviceCount;
	long cleanCount;
	long locationCount;
	static long allcount;
	public Hotel() {
		// TODO Auto-generated constructor stub
		reviews = new ArrayList<String>();
		ratings = new HashMap<String, Float>();
		ratings.put("value",0f);
		ratings.put("location",0f);
		ratings.put("service",0f);
		ratings.put("room",0f);
		ratings.put("cleanliness",0f);
		count = valueCount = locationCount = cleanCount = serviceCount = roomCount = 0;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId(){
		return id;
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
	
	public void setReviews(String [] reviews) {
		for(String review: reviews){
			addReview(review);
		}
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
		String id = file.getName().split(".dat")[0].split("hotel_")[1];
		hotel.setId(id);
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
				hotel.count++;
			}
			if(line.contains("<Value>")){
				float rating = Float.parseFloat(line.split("<Value>")[1]);
				if(rating < 0)
				{
					rating = 0f;
				}
				else {
					hotel.valueCount++;
				}
				rating = hotel.ratings.get("value") + rating ;
				hotel.ratings.put("value", rating);
			}
			if(line.contains("<Rooms>")){
				float rating = Float.parseFloat(line.split("<Rooms>")[1]);
				if(rating < 0)
				{
					rating = 0f;
				}
				else{
					hotel.roomCount++;
				}
				rating = hotel.ratings.get("room") + rating;
				hotel.ratings.put("room", rating);
			}
			if(line.contains("<Location>")){
				float rating = Float.parseFloat(line.split("<Location>")[1]);
				if(rating < 0)
				{
					rating = 0f;
				}
				else{
					hotel.locationCount++;
				}
				rating = hotel.ratings.get("location") + rating;
				hotel.ratings.put("location", rating);
			}
			if(line.contains("<Cleanliness>")){
				float rating = Float.parseFloat(line.split("<Cleanliness>")[1]);
				if(rating < 0)
				{
					rating = 0f;
				}
				else{
					hotel.cleanCount++;
				}
				rating = hotel.ratings.get("cleanliness") + rating;
				hotel.ratings.put("cleanliness", rating);
			}
			if(line.contains("<Service>")){
				float rating = Float.parseFloat(line.split("<Service>")[1]);
				if(rating < 0)
				{
					rating = 0f;
				}
				else{
					hotel.serviceCount++;
				}
				rating = hotel.ratings.get("service") + rating;
				hotel.ratings.put("service", rating);
			}
		}
		float rating = hotel.ratings.get("value")/hotel.valueCount;
		hotel.ratings.put("value", rating);
		rating = hotel.ratings.get("location")/hotel.locationCount;
		hotel.ratings.put("location", rating);
		rating = hotel.ratings.get("room")/hotel.roomCount;
		hotel.ratings.put("room", rating);
		rating = hotel.ratings.get("cleanliness")/hotel.cleanCount;
		hotel.ratings.put("cleanliness", rating);
		rating = hotel.ratings.get("service")/hotel.serviceCount;
		hotel.ratings.put("service", rating);
		bReader.close();
		Hotel.allcount += hotel.count;
		return hotel;
	}
	
	public static void main(String args[]) {
		DatasetParser parser;
		try {
			parser = new DatasetParser("/home/shruthi/cs410Project/LuceneDemo/src/TripAdvisor/TripAdvisor");
			File [] listofFiles = parser.getListOfFiles();
			int i=0;
			for(File file: listofFiles){
				Hotel hotel = parser.parse(file);
				i++;
			}
			System.out.println(Hotel.allcount/i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
}
