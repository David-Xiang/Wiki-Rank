package src;
import java.io.*;
import java.util.Set;
import java.util.TreeSet;
import com.google.gson.*;

public class statistic {
	public static void main(String[] args) throws IOException {
		Set<String> set = new TreeSet<>();
 		JsonParser parser = new JsonParser();
		int count = 0;
		for (int i = 0; i < 1000; i++) {
			BufferedReader in = new BufferedReader(new FileReader("data/entry"+i+".json"));
			String str = in.readLine();
			if (str.length() < 100)
				System.out.println("Error: " + i +"th file!");
			JsonArray array = parser.parse(str).getAsJsonArray();
			for (JsonElement obj: array) {
				/*
				 * The obj here has two attributes: title & refer
				 * title is a String and refer is an Array of String.
				 * */
				String title = obj.getAsJsonObject().get("title").getAsString();
				if (title.length() == 0) {
					System.out.println("i="+i+":title is null!");
				}else {
					set.add(title);
				}
			}
		}
		FileWriter out = new FileWriter(new File("data/title.txt"));
		for (String s: set) {
			out.write(s+"\n");
		}
		out.close();
		System.out.println("Total number of entries is " + set.size());
	}
}
