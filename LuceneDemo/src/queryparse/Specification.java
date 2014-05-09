package queryparse;

import main.HotelIndexer;
import main.QueryProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.queryparser.classic.ParseException;

public class Specification {

	String aspectName;
	float weight;
	public int ratingWeight=0;
	int rating;
	HashMap<String, ArrayList<String>> specificationMap;
	HashMap<String, ArrayList<Integer>> count;
	
	public Specification(String aspectName)
	{
		this.aspectName = aspectName;
		specificationMap= new HashMap<String, ArrayList<String>>();
		count = new HashMap<String, ArrayList<Integer>>();
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
		this.ratingWeight+=rating;
		return this;
	}
	
	public int getRating() {
		return rating;
	}
	
	public Specification setMapEntry(String specificationName, String value) {
		if(specificationMap.containsKey(specificationName))
		{
			ArrayList<String> words = specificationMap.get(specificationName);
			if(!words.contains(value)){
				words.add(value);
				if(this.ratingWeight<5 && !specificationName.equals("misc"))
					this.ratingWeight+=1;
			}
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
	
	public ArrayList<Integer> getCount(String phrase) {
		return count.get(phrase);
	}
	
	public void computeCounts(ArrayList<String> hotelIds) throws ParseException, IOException {
			for(String specificationName: specificationMap.keySet()){
				for(String phrase: specificationMap.get(specificationName)){
						ArrayList<Integer> counts = new ArrayList<Integer>();
						for(String id: hotelIds){
						//System.out.println("i'm here for" + specificationName);
						//System.out.println(phrase+ " "+ HotelIndexer.getCount(id, phrase));
						//counts.add(HotelIndexer.getCount(id, phrase));
					}
					count.put(phrase, counts);
				}
			}
		}
}
