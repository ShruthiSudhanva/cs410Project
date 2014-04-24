package queryparse;

import java.util.ArrayList;
import java.util.HashMap;

public class Specification {

	String aspectName;
	float weight;
	int rating;
	HashMap<String, ArrayList<String>> specificationMap;
	
	public Specification(String aspectName)
	{
		this.aspectName = aspectName;
		specificationMap= new HashMap<String, ArrayList<String>>();
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
		if(specificationMap.containsKey(specificationName))
		{
			ArrayList<String> words = specificationMap.get(specificationName);
			if(!words.contains(value))
				words.add(value);
			specificationMap.put(specificationName, words);
		}
		else {
			ArrayList<String> words = new ArrayList<String>();
			words.add(value);
			specificationMap.put(specificationName, words);
		}
		return this;
	}
	
	public ArrayList<String> getMapEntry(String specificationName) {
		return specificationMap.get(specificationName);
	}
}
