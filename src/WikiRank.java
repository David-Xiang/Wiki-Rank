package src;
import java.io.*;

class WikiRank{
	/*
	 * WikiRank's main class
	 * Each time run WikiRank, please modify the params:
	 * fileNum, entryPerFile and xmlPath.
	 * 
	 * DEPENDENCY: Google's GSON which is used to I/O .json files.
	 * */
	
	int fileNum = 100;
	int entryPerFile = 1000;
	
	String xmlPath = "data/enwiki.xml"; 
	// I use a softlink to link original file to data/enwiki.xml
	
	String outputPath = "data/output.txt";
	
	NodesBuilder nodesBuilder;
	PRIterator prIterator;
	
	public static void main(String[] args) throws IOException{
		/*
		 * The WikiRank's whole process are divided into 3 main process:
		 * 1. Parse the original .xml dump file and extract's a given number of 
		 *    pages' title and references.
		 * 2. Filter the references in which entry title are not included and 
		 *    build a sparse graph (nodes array) based on these entries. 
		 * 3. Do PageRank Algorithm and save ouput.
		 * */
		
		WikiRank wikiRank = new WikiRank();
		//wikiRank.parseXML();
		wikiRank.buildNodes();
		wikiRank.iteratePageRank();
		
	}
	void parseXML() throws IOException {
		BufferedReader in = new BufferedReader(
				new FileReader(xmlPath));
		
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
		nodesBuilder.buildNodes();
	} 
	void iteratePageRank() throws IOException {
		prIterator = new PRIterator(nodesBuilder);
		prIterator.init();
		prIterator.iterate();
		prIterator.saveOutput(outputPath);
	}
}