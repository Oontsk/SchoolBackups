import java.util.Comparator;


public class StringComparator implements Comparator<String> {

	@Override
	public int compare(String s1, String s2) {
		if (s1 == null) {
			return (s2 == null) ? 0 : -1;
		}
		if (s2 == null) {
			return 1;
		}
		return s1.compareTo(s2);
	}

}
