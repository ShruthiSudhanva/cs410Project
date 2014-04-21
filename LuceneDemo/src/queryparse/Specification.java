package queryparse;

import java.util.HashMap;

public class Specification {

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
