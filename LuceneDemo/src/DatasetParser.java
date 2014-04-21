import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*Parse the tripadvisor dataset*/
public class DatasetParser {

	File folder;
	BufferedReader bReader;
	File [] listOfFiles;
	public DatasetParser(String path) throws IOException
	{
		folder = new File(path);
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
	
	public void parse(String path) throws IOException {
		bReader = new BufferedReader(new FileReader(path));
		bReader.close();
	}
}
