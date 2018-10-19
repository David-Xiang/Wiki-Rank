import java.net.URL;
import java.io.*;
import java.util.*;

class WikiRank{
    public static void main(String[] args) throws IOException{
    	Parser parser = new Parser("data/test.xml");
    	for (int i = 0; i < 1000; i++) {
    		parser.parseEntry(1000);
        	//parser.showOutput();
        	//parser.saveOutput("data/first-stage.json");
    	}
    }
}