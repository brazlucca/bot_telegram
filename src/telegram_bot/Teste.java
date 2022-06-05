package telegram_bot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Teste {
	

	public static void main(String[] args) throws Exception {
		String a = "0 15 3 25 98 47";
		System.out.println(a);
		List<String> asList = Arrays.asList(a.split(" "));
		System.out.println(asList);
		Collections.sort(asList);
		System.out.println(asList);
	}
}
