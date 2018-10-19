package src;
import java.util.regex.*;

public class test {
	public static void main(String[] args) {
		String str = "<redirect title=\"Computing\" />";
		Pattern pattern = Pattern.compile("<redirect title(.*?)>");
		Matcher matcher = pattern.matcher(str);
		System.out.println(str);
		if (matcher.find())
			System.out.println(matcher.group(0));
		
		String a = "apple";
		String b = "banana";
		System.out.println(a.compareTo(b));
	}
}
