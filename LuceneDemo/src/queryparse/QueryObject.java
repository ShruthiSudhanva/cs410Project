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
		
		return exampleQueries;
	}
	
	public static void  main(String args[]) {
		QueryObject[] EQ = examples();
		
		/*The code below prints Specification objects which in turn contains Aspects Map from which required details can be extracted
		for(int i=0;i<EQ.length;i++)
		{
			System.out.println("Printing " + i + " Query Object");
			
			System.out.println(EQ[i].getHotelName());
			if(EQ[i].getSpecification("location")!=null)
			{
			System.out.print("location: ");
			System.out.print(EQ[i].getSpecification("location"));
			System.out.println();
			}
			if(EQ[i].getSpecification("misc")!=null)
			{
		    System.out.print("miscellaneous: ");
			System.out.println(EQ[i].getSpecification("misc"));
			System.out.println();
			}
			if(EQ[i].getSpecification("price")!=null)
			{
			System.out.print("Value: ");
			System.out.print(EQ[i].getSpecification("price"));
			System.out.println();
			}
			if(EQ[i].getSpecification("service")!=null)
			{
			System.out.print("Service: ");
			System.out.print(EQ[i].getSpecification("service"));
			System.out.println();
			}
			if(EQ[i].getSpecification("cleanliness")!=null)
			{
		    System.out.print("Cleanliness: ");
			System.out.print(EQ[i].getSpecification("cleanliness"));
			System.out.println();
			}
			if(EQ[i].getSpecification("room")!=null)
			{
			System.out.print("Room: ");
			System.out.print(EQ[i].getSpecification("room"));
			System.out.println();
			}
		}*/
	}
	
}
