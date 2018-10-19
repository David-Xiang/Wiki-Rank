package src;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.*;
import java.util.Set;
import java.util.TreeSet;

class NodesBuilder {
	Map<Integer, String> id2title;
	Map<String, Integer> title2id;
	Node[] nodes;
	int fileNum;
	int entryPerFile;
	/*
	 * NodesBuilder is responsible for deleting all references directing to the
	 * entries not included in the .json files. 
	 * */
	NodesBuilder(int fileNum, int entryPerFile){
		this.fileNum = fileNum;
		this.entryPerFile = entryPerFile;
		id2title = new HashMap<Integer, String>();
		title2id = new HashMap<String, Integer>();
		nodes = new Node[fileNum * entryPerFile];
	}
	public static void main(String[] args) throws IOException {
		//NodesBuilder nodesBuilder = new NodesBuilder();
		//nodesBuilder.readJson(1);
	}
	void readJson() throws IOException {
		Set<String> dict = new TreeSet<String>();
		JsonArray[] jsonArray = new JsonArray[fileNum];
		JsonParser parser = new JsonParser();
		
		int count = 0;
		for (int i = 0; i < fileNum; i++) {
			BufferedReader in = new BufferedReader(
					new FileReader("data/entry"+i+".json"));
			String str = in.readLine();
			JsonArray array = parser.parse(str).getAsJsonArray();
			jsonArray[i] = array;
			for (JsonElement obj: array) {
				/*
				 * The obj here has two attributes: title & refer
				 * title is a String and refer is an Array of String.
				 * */
				String title = obj.getAsJsonObject().get("title").getAsString();
				if (title.length() == 0) {
					System.out.println("i="+i+":title is null!");
				}else {
					dict.add(title);
					nodes[count] = new Node(title, count, 1.0/fileNum*entryPerFile);
					id2title.put(count, title);
					title2id.put(title, count);
					count++;
				}
			}
			in.close();
		}
		
		for (int i = 0; i < fileNum; i++) {
			for (JsonElement obj: jsonArray[i]) {
				JsonArray refers = obj.getAsJsonObject().get("refer").getAsJsonArray();
				String title = obj.getAsJsonObject().get("title").getAsString();
				for (JsonElement refer: refers) {
					String r = refer.getAsString();
					if (dict.contains(r)) {
						nodes[title2id.get(r)].addIncident(title2id.get(title));
					}
				}
			}
		}		
	}
}

class Node implements Comparable<Node>{
	String title;
	int index;
	double prValue;
	ArrayList<Integer> incidents;
	Node(String title, int index, double prValue){
		this.title = title;
		this.index = index;
		this.incidents = new ArrayList<Integer>();
		this.prValue = prValue;
	}
	
	void addIncident(int index) {
		incidents.add(index);
	}
	double getPrVlaue() {
		return prValue;
	}
	void updataPrValue(double prValue) {
		this.prValue = prValue;
	}
	public int compareTo(Node n) {
		return this.title.compareTo(n.title);
	}
}
