package queryparse;

import java.util.HashMap;

class Specification
{
	String aspectName;
	float weight;
	int rating;
	HashMap<String, String> specificationMap;
	
	public Specification(String aspectName)
	{
		this.aspectName = aspectName;
		specificationMap= new HashMap<String, String>();
	}
	
	public String getAspectName() {
		return aspectName;
	}
	
	public Specification setWeight(float weight)
	{
		this.weight = weight;
		return this;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public Specification setRating(int rating) {
		this.rating = rating;
		return this;
	}
	
	public int getRating() {
		return rating;
	}
	
	public Specification setMapEntry(String specificationName, String value) {
		specificationMap.put(specificationName, value);
		return this;
	}
	
	public String getMapEntry(String specificationName) {
		return specificationMap.get(specificationName);
	}
}


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
		
		//Query3 = 	
		
		return exampleQueries;
	}
	
	public static void  main(String args[]) {
		examples();
	}
	
}
