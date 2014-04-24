package queryparse;

import java.util.HashMap;

public class QueryObject {

	private String hotelName;
	private HashMap<String,Specification> aspects;
	
	public QueryObject(String hotelName){
		this.hotelName = hotelName;
		aspects = new HashMap<String, Specification>();
	}
	
	public QueryObject setSpecification(Specification specification)
	{
		aspects.put(specification.getAspectName(), specification);
		return this;
	}
	
	public Specification getSpecification(String aspect) {
		return aspects.get(aspect);
	}
	
	public boolean hasSpecification(String aspect) {
		return aspects.containsKey(aspect);
	}
	
	public String getHotelName() {
		return hotelName;
	}
	
	public HashMap<String, Specification> getAspects(){
		return aspects;
	}
	
	//Use this function to access example objects
	public static QueryObject [] examples()
	{
		QueryObject [] exampleQueries = new QueryObject[5];
		
		//Query1 = "Hampton Inn hotel in Urbana cheap near campus
		exampleQueries[0] = new QueryObject("Hampton Inn Urbana");
		Specification valueSpecification = new Specification("value");
		valueSpecification.setWeight(0.5f).setRating(4).setMapEntry("price", "<100");
		Specification locationSpecification = new Specification("location");
		locationSpecification.setWeight(0.5f).setRating(3).setMapEntry("location", "campus");
		exampleQueries[0].setSpecification(valueSpecification).setSpecification(locationSpecification);
		
		//Query2 = Hotel downtown Chicago magnificent mile average price big rooms
		exampleQueries[1] = new QueryObject("MileNorth Hotel");
		valueSpecification.setWeight(0.4f).setRating(4).setMapEntry("price", ">70, <120");
		locationSpecification.setWeight(0.3f).setRating(3).setMapEntry("location", "downtown magnificent mile");
		Specification roomSpecification = new Specification("room");
		roomSpecification.setWeight(0.3f).setRating(3).setMapEntry("room", "big");
		exampleQueries[1].setSpecification(valueSpecification).setSpecification(locationSpecification).setSpecification(roomSpecification);
		
		//Query3 = "Hotel close to Chicago Union Station"
		exampleQueries[2] = new QueryObject("Congress Plaza Hotel");
		Specification miscSpecification = new Specification("misc");
		locationSpecification.setWeight(0.3f).setRating(3).setMapEntry("location", "Chicago");
		miscSpecification.setWeight(0.1f).setRating(3).setMapEntry("misc", "Union Station");
		exampleQueries[2].setSpecification(locationSpecification).setSpecification(miscSpecification);	
		
		//Query4 = "Clean hotels in Seattle with good room service"
		exampleQueries[3] = new QueryObject("MayFlower Park Hotel");
		Specification cleanlinessSpecification = new Specification("cleanliness");
		cleanlinessSpecification.setWeight(0.4f).setRating(4).setMapEntry("cleanliness", "clean");
		locationSpecification.setWeight(0.2f).setRating(3).setMapEntry("location", "Seattle");
		Specification serviceSpecification = new Specification("service");
		serviceSpecification.setWeight(0.4f).setRating(4).setMapEntry("service", "good");
		exampleQueries[3].setSpecification(cleanlinessSpecification).setSpecification(locationSpecification).setSpecification(serviceSpecification);
			
		//Query5 = "San Jose Airport hotel"
		exampleQueries[4] = new QueryObject("Hyatt Hotel");
		locationSpecification.setWeight(0.3f).setRating(3).setMapEntry("location", "San Jose");
		miscSpecification.setWeight(0.1f).setRating(3).setMapEntry("misc", "Airport");
		exampleQueries[4].setSpecification(locationSpecification).setSpecification(miscSpecification);
		
		System.out.println(exampleQueries[4].hasSpecification("location"));
		
		return exampleQueries;
	}
	
	public static void  main(String args[]) {
		QueryObject[] EQ = examples();
		
	}
	
}
