import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import com.google.gson.Gson;

public class Parser {
	/*
	 * Parser handles the original .xml file (Wiki dump file).
	 * It searches for a given number of pages and extracts their
	 * Title and references to other pages.
	 * The output are stored in a json file.
	 * */
	BufferedReader in;
	Pattern pageHeadPattern, pageEndPattern, titlePattern, 
		referPattern, keywordPattern;
	Matcher pageMatcher, titleMatcher, referMatcher, keywordMatcher;
	ArrayList<Entry> entrySet;
	
	Parser(String path) throws FileNotFoundException{
		// Construct a .xml parser from a given Wikipedia dump file
		in = new BufferedReader(new FileReader(path));
		pageHeadPattern = Pattern.compile("<page>");
		pageEndPattern = Pattern.compile("</page>");
		titlePattern = Pattern.compile("<title>(.*?)</title>");
		referPattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
		keywordPattern = Pattern.compile("([^\\|#]+)");
		entrySet = new ArrayList<Entry>();
	}

	void parseEntry(int entryNum) throws IOException {
		/*
		 * Only when a page (fenced with <page> and </page>) 
		 * has a title (fenced with <title> and </title>), is it considered 
		 * as an entry. 
		 * Each "[[]]" are a reference link in which the string on the right
		 * of first "|" is the referred link's title.
		 * All information extraction can be handled by Regular Expression.
		 * THe first-stage output are stored in a .json file which requires
		 * dependency of Google's GSON.
		 * 
		 * */
		int entryCount = 0;
		String str;
		while (entryCount < entryNum) {
			// looking for <page>
			str = in.readLine();
			if (str == null)
				break;
			pageMatcher = pageHeadPattern.matcher(str);
			
			while(!pageMatcher.find()) {
				str = in.readLine();
				pageMatcher.reset(str);
			}
			// found a new page
			
			pageMatcher = pageEndPattern.matcher(str);
			Entry entry = new Entry();
			Boolean foundTitle = false;
			
			// This loop ends when </page> is found. And In this loop, regex
			// searches for title and reference.
			while(!pageMatcher.find()) {
				if (!foundTitle) {
					titleMatcher = titlePattern.matcher(str);
					if (titleMatcher.find()) {
						entry.setName(titleMatcher.group(1));
						//System.out.println("found title: " 
						// 				+ titleMatcher.group(1));
						foundTitle = true;
					}
				}
				
				referMatcher = referPattern.matcher(str);
				if (referMatcher.find()) {
					keywordMatcher = keywordPattern.matcher(
							referMatcher.group(1));
					if (keywordMatcher.find()) {
						String keyword = keywordMatcher.group(1); 
						//Do I need to add this: .replace("&quot;", "\"");
						
						//System.out.println("found reference to: " + keyword);
						if (foundTitle)
							entry.addRefer(keyword);
						else
							System.out.println("Error: found reference "+
												"however title is not found!");
					}
				}
				
				// input another line
				str = in.readLine();
				if (str == null)
					break;
				pageMatcher.reset(str);
			}
			entryCount++;
			entrySet.add(entry);
		}
		System.out.println("Parser: parsed " + entryCount + " entries.");
	}
	
	void showOutput() {
		Gson gson = new Gson();
		System.out.println(gson.toJson(entrySet));
	}
	
	void saveOutput() {
		Gson gson = new Gson();
	}
	class Entry{
		String name;
		ArrayList<String> refer;
		
		Entry(){
			this.name = null;
			this.refer = new ArrayList<String>();
		}
		void setName(String name){
			if (this.name != null) {
				System.out.println(
						"Error: entry's name is already set as " + this.name);
			}
			this.name = name;
		}
		void addRefer(String refer) {
			this.refer.add(refer);
		}
	}
}
