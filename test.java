import java.util.regex.*;

public class test {
	public static void main(String[] args) {
		String str = "[[Apple inc.|Apple]]";
		Pattern pattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
		String str1 = "";
		Matcher matcher = pattern.matcher(str);
		//matcher.reset(str);
		System.out.println(str);
		if (matcher.find())
			System.out.println(matcher.group(1));
	}
}
