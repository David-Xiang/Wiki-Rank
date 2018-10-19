package src;
import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import com.google.gson.Gson;

class Parser {
	/*
	 * Parser handles the original .xml file (Wiki dump file).
	 * It searches for a given number of entries and extracts their title and
	 * references to other pages. Redirect pages and non-concept pages (see 
	 * in next paragraph) are not considered in. 
	 * The outputs are stored in a json file.
	 * 
	 * ATTENTION: We ONLY calculate the concepts' PageRank, thus entries like: 
	 * Category:XXX, Wiki:XXX, File:XXX are not extracted. However, I don't
	 * how many types of entries "XXX:XXX" are irrelavant, so I filtered ALL
	 * entries with character ':' in its title which is very brutal. However,
	 * it matters little in final PageRank calculation.
	 * 
	 * */
	
	Pattern pageHeadPattern, pageEndPattern, titlePattern, referPattern, 
		keywordPattern, redirectPattern;
	Matcher pageMatcher, titleMatcher, referMatcher, keywordMatcher,
		redirectMatcher;
	ArrayList<Entry> entrySet;
	
	Parser() throws FileNotFoundException{
		// Construct a .xml parser from a given Wikipedia dump file
		
		pageHeadPattern = Pattern.compile("<page>");
		pageEndPattern = Pattern.compile("</page>");
		titlePattern = Pattern.compile("<title>(.*?)</title>");
		referPattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
		keywordPattern = Pattern.compile("([^\\|#]+)");
		redirectPattern = Pattern.compile("<redirect title(.*?)>");
		
	}

	int parseEntry(int entryNum, BufferedReader in) throws IOException {
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
		entrySet = new ArrayList<Entry>();
		
		while (entryCount < entryNum) {
			// looking for <page>
			str = in.readLine();

			if (str == null)
				return entryCount;
			
			pageMatcher = pageHeadPattern.matcher(str);
			
			while(!pageMatcher.find()) {
				str = in.readLine();
				if (str ==  null)
					return entryCount;
				pageMatcher.reset(str);
			}
			
			// page begins
			
			pageMatcher = pageEndPattern.matcher(str);
			Entry entry = new Entry();
			Boolean foundTitle = false;
			Boolean isRedirect = false;
			
			// This loop ends when </page> is found. And In this loop, regex
			// searches for title and reference.
			while(!pageMatcher.find()) {
				if (isRedirect) {
					// do nothing
				}else if (!foundTitle) {
					// search for title
					
					titleMatcher = titlePattern.matcher(str);
					if (titleMatcher.find() && 
						titleMatcher.group(1).indexOf(":") == -1) {
						entry.setTitle(titleMatcher.group(1));
						//System.out.println("found title: " 
						// 				+ titleMatcher.group(1));
						foundTitle = true;
					}
				}else {
					// search for reference and redirect tag
					
					redirectMatcher = redirectPattern.matcher(str);
					if (redirectMatcher.find())
						isRedirect = true;
					
					referMatcher = referPattern.matcher(str);
					while (referMatcher.find()) {
						keywordMatcher = keywordPattern.matcher(
								referMatcher.group(1));
						if (keywordMatcher.find() &&
							keywordMatcher.group(1).indexOf(":") == -1) {
							String keyword = keywordMatcher.group(1); 
							//Do I need to add this: .replace("&quot;", "\"");
							
							//System.out.println("found reference to: " + keyword);
							if (foundTitle)
								entry.addRefer(keyword);
						}
					}
				}
				
				// input another line
				str = in.readLine();
				if (str == null)
					return entryCount;
				pageMatcher.reset(str);
			}
			
			// page ends
			if (!isRedirect && foundTitle) {
				entryCount++;
				entrySet.add(entry);
			}
		}
		return entryCount;
	}
	
	void showOutput() {
		Gson gson = new Gson();
		System.out.println(gson.toJson(entrySet));
	}
	
	void saveOutput(String path) throws IOException {
		Gson gson = new Gson();
		String str = gson.toJson(entrySet);
		FileWriter out = new FileWriter(new File(path));
		
		out.write(str);
		out.close();
	}
	
	class Entry{
		String title;
		ArrayList<String> refer;
		
		Entry(){
			this.title = null;
			this.refer = new ArrayList<String>();
		}
		void setTitle(String title){
			if (this.title != null) {
				System.out.println(
						"Error: entry's name is already set as " + this.title);
			}
			this.title = title;
		}
		void addRefer(String refer) {
			this.refer.add(refer);
		}
	}
}
