package src;
import java.io.*;

class WikiRank{
	int fileNum = 1000;
	int entryPerFile = 1000;
	NodesBuilder nodesBuilder;
	PRIterator prIterator;
    public static void main(String[] args) throws IOException{
    	
    }
    void parseXML() throws IOException {
    	// I used a softlink to link original file to data/enwiki.xml
    	BufferedReader in = new BufferedReader(
    			new FileReader("data/enwiki.xml"));
    	
    	int count = 0;
    	Parser parser = new Parser();
    	for (int i = 0; i < fileNum; i++) {
    		count = parser.parseEntry(entryPerFile, in);
    		parser.saveOutput("data/entry"+i+".json");

    		if (count < fileNum) {
       			System.out.println("Early breaks at loop " + i + ", " + count
    					+ " entries are read in this loop.");
    			break;
    		}

    		System.out.println("Status: " + (i+1) / 10.0 + "%");
    	}
    }
    void buildNodes() throws IOException {  	
    	nodesBuilder = new NodesBuilder(fileNum, entryPerFile);
    	nodesBuilder.readJson();
    } 
    void iteratePageRank(NodesBuilder nodesBuilder) {
    	prIterator = new PRIterator(nodesBuilder);
    	prIterator.iterate();
    	prIterator.saveOutput("data/output.txt");
    }
}
