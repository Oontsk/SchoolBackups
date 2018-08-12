import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cmsc420.sortedmap.Treap;
import cmsc420.meeshquest.part2.MeeshQuest;

/** Submit server public tests for CMSC 420 MeeshQuest Part 2 */
public class PublicTests extends junit.framework.TestCase {
	private static final int INITBUFSIZE = 32768; // Initial output buffer size
	// in bytes
	private static final String[] args = {}; // For passing to main()

	/**
	 * Runs Main.main() with stdin redirected from inputXMLFile and checks that
	 * the output it produces is equal to outputXMLFIle according to xmlDiff().
	 * Messes with System.in and System.out and doesn't put them back.
	 */
	public static boolean mainGivesCorrectOutput(String inputXMLFile,
			String outputXMLFile) throws ParserConfigurationException,
	SAXException, IOException {
		// stdin comes from a file
		System.setIn(new FileInputStream(inputXMLFile));
		// stdout goes to a buffer
		ByteArrayOutputStream outbuf = new ByteArrayOutputStream(INITBUFSIZE);
		System.setOut(new PrintStream(outbuf, false, "UTF-8"));
		// Get actual output by running main() and filling the buffer
		MeeshQuest.main(args);
		ByteArrayInputStream actual = new ByteArrayInputStream(
				outbuf.toByteArray());
		// Get expected output from a file
		InputStream expected = new FileInputStream(outputXMLFile);

		// Compare
		// GeneralXMLDiff diff = new GeneralXMLDiff();
		// return (diff.generalXMLDiff(expected, actual) == 0) ;
		return !XMLDiff.xmlDiff(expected, actual, "Treap");
	}
	
	public static boolean mainGivesValidTreapOutput(String inputXMLFile, int[] sizes) throws Exception {
		// stdin comes from a file
		System.setIn(new FileInputStream(inputXMLFile));
		// stdout goes to a buffer
		ByteArrayOutputStream outbuf = new ByteArrayOutputStream(INITBUFSIZE);
		System.setOut(new PrintStream(outbuf, false, "UTF-8"));
		// Get actual output by running main() and filling the buffer
		MeeshQuest.main(args);
		ByteArrayInputStream actual = new ByteArrayInputStream(
				outbuf.toByteArray());

		System.setIn(actual);

		ByteArrayOutputStream finalOutBuf = new ByteArrayOutputStream(
				INITBUFSIZE);
		System.setOut(new PrintStream(finalOutBuf, false, "UTF-8"));

		TreapValidator tv = new TreapValidator(ReverseGenericComparator.instance, sizes);
				
		String results = finalOutBuf.toString("UTF-8");
		if (results.contains("xception") || results.contains("rror"))
			return false;
		return true;
	}

	
	// private final int N = 1000; // Number of things added to the map in most
	// tests
	// private SortedMap<String, Integer> rb; // 'rb' for Java's 'red-black'
	// private SortedMap<String, Integer> map;
	private final int seed = 2017;
	private Random rand = new Random(seed);


	public void testPublicListCities() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPublicListCities did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.public.listCities.input.xml",
						"testfiles/part2.public.listCities.output.xml"));
	}

	public void testPublicNearestCity() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPublicNearestCity did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.public.nearestCity.input.xml",
						"testfiles/part2.public.nearestCity.output.xml"));
	}

	public void testPublicRange() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPublicRange did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.public.range.input.xml",
						"testfiles/part2.public.range.output.xml"));
	}

	public void testPublicShortestPath() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPublicShortestPath did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.public.shortestPath.input.xml",
						"testfiles/part2.public.shortestPath.output.xml"));
	}
	
	public void testTreapPublic() throws Exception {
		int[] sizes = {1,2,3};
		assertTrue(
				"testTreapPublic did not give correct output",
				mainGivesValidTreapOutput(
						"testfiles/part2.public.treap.input.xml", sizes));
	}


	// ========== "realease" tests ============


	public void testAll1() throws ParserConfigurationException, SAXException,
	IOException {
		assertTrue(
				"testAll1 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.all1.input.xml",
						"testfiles/part2.all1.output.xml"));
	}

	public void testAll2() throws ParserConfigurationException, SAXException,
	IOException {
		assertTrue(
				"testAll2 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.all2.input.xml",
						"testfiles/part2.all2.output.xml"));
	}

	public void testAll3() throws ParserConfigurationException, SAXException,
	IOException {
		assertTrue(
				"testAll3 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.all3.input.xml",
						"testfiles/part2.all3.output.xml"));
	}

	public void testNearestCity1() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testNearestCity1 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.nearestCity1.input.xml",
						"testfiles/part2.nearestCity1.output.xml"));
	}

	public void testNearestCity2() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testNearestCity2 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.nearestCity2.input.xml",
						"testfiles/part2.nearestCity2.output.xml"));
	}

	public void testNearestIsolatedCity1() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testNearestIsolatedCity1 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.nearestIsolatedCity1.input.xml",
						"testfiles/part2.nearestIsolatedCity1.output.xml"));
	}

	public void testNearestIsolatedCity2() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testNearestIsolatedCity2 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.nearestIsolatedCity2.input.xml",
						"testfiles/part2.nearestIsolatedCity2.output.xml"));
	}

	public void testNearestPossiblyIsolatedCity() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testNearestPossiblyIsolatedCity did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.nearestPossiblyIsolatedCity.input.xml",
						"testfiles/part2.nearestPossiblyIsolatedCity.output.xml"));
	}

	public void testNearestRoad1() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testNearestRoad1 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.nearestRoad1.input.xml",
						"testfiles/part2.nearestRoad1.output.xml"));
	}

	public void testNonfatalError1()
			throws ParserConfigurationException, SAXException, IOException {
		assertTrue(
				"testNonfatalError1 did not give correct output",
				mainGivesCorrectOutput(
						"testfiles/part2.nonfatalError1.input.xml",
						"testfiles/part2.nonfatalError1.output.xml"));
	}

	public void testNonfatalError2()
			throws ParserConfigurationException, SAXException, IOException {
		assertTrue(
				"testNonfatalError2 did not give correct output",
				mainGivesCorrectOutput(
						"testfiles/part2.nonfatalError2.input.xml",
						"testfiles/part2.nonfatalError2.output.xml"));
	}

	public void testPM3Insert1()
			throws ParserConfigurationException, SAXException, IOException {
		assertTrue(
				"testPM3Insert1 did not give correct output",
				mainGivesCorrectOutput(
						"testfiles/part2.pm3Insert1.input.xml",
						"testfiles/part2.pm3Insert1.output.xml"));
	}

	public void testPM3Insert2() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPM3Insert2 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.pm3Insert2.input.xml",
						"testfiles/part2.pm3Insert2.output.xml"));
	}

	public void testPM3Insert3() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPM3Insert3 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.pm3Insert3.input.xml",
						"testfiles/part2.pm3Insert3.output.xml"));
	}

	public void testPM3Insert4() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPM3Insert4 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.pm3Insert4.input.xml",
						"testfiles/part2.pm3Insert4.output.xml"));
	}

	public void testPM3InsertWithIsolatedCities1() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPM3InsertWithIsolatedCities1 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.pm3InsertWithIsolatedCities1.input.xml",
						"testfiles/part2.pm3InsertWithIsolatedCities1.output.xml"));
	}

	public void testPM3InsertWithIsolatedCities2() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPM3InsertWithIsolatedCities2 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.pm3InsertWithIsolatedCities2.input.xml",
						"testfiles/part2.pm3InsertWithIsolatedCities2.output.xml"));
	}

	public void testPM3InsertWithIsolatedCities3() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testPM3InsertWithIsolatedCities3 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.pm3InsertWithIsolatedCities3.input.xml",
						"testfiles/part2.pm3InsertWithIsolatedCities3.output.xml"));
	}

	public void testRangeCities1() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testRangeCities1 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.rangeCities1.input.xml",
						"testfiles/part2.rangeCities1.output.xml"));
	}

	public void testRangeCities2() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testRangeCities2 did not give correct output",
				mainGivesCorrectOutput(
						"testfiles/part2.rangeCities2.input.xml",
						"testfiles/part2.rangeCities2.output.xml"));
	}

	public void testRangeRoads1() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testRangeRoads1 did not give correct output",
				mainGivesCorrectOutput(
						"testfiles/part2.rangeRoads1.input.xml",
						"testfiles/part2.rangeRoads1.output.xml"));
	}

	public void testRangeRoads2()
			throws ParserConfigurationException, SAXException, IOException {
		assertTrue(
				"testRangeRoads2 did not give correct output",
				mainGivesCorrectOutput(
						"testfiles/part2.rangeRoads2.input.xml",
						"testfiles/part2.rangeRoads2.output.xml"));
	}

	public void testRangeRoads3() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testRangeRoads3 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.rangeRoads3.input.xml",
						"testfiles/part2.rangeRoads3.output.xml"));
	}

	public void testShortestPath1() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testShortestPath1 did not give correct output",
				mainGivesCorrectOutput("testfiles/part2.shortestPath1.input.xml",
						"testfiles/part2.shortestPath1.output.xml"));
	}

	public void testShortestPath2() throws ParserConfigurationException,
	SAXException, IOException {
		assertTrue(
				"testShortestPath2 did not give correct output",
				mainGivesCorrectOutput(
						"testfiles/part2.shortestPath2.input.xml",
						"testfiles/part2.shortestPath2.output.xml"));
	}

	public void testTreap1() throws Exception {
		int[] sizes = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
		assertTrue(
				"testTreap1 did not give correct output",
				mainGivesValidTreapOutput(
						"testfiles/part2.testTreap1.input.xml", sizes));
	}
	
	public void testTreap2() throws Exception {
		int[] sizes = {51,55};
		assertTrue(
				"testTreap2 did not give correct output",
				mainGivesValidTreapOutput(
						"testfiles/part2.testTreap2.input.xml", sizes));
	}
	
	public void testTreap3() throws Exception {
		int[] sizes = {1018, 1998};
		assertTrue(
				"testTreap3 did not give correct output",
				mainGivesValidTreapOutput(
						"testfiles/part2.testTreap3.input.xml", sizes));
	}
	
	
	public void testSortedMapBasic() {
		testConstructor(0, 1);
		testEmpty(0, 1);
		testEmpty(1, 1);
	}

	// 	testNItems(int type,  int length, boolean duplicates, int max, boolean newValue) {
	public void testSortedMapSimpleData() {
		// 1 item, no duplicates
		testNItems(0,1, false, 10, false);
		testNItems(1,1, false, 10, false);
		// 10 items, no duplicates
		testNItems(0,10, false, 10, false);
		testNItems(1,10, false, 10, false);
	}

	public void testSortedMapMoreData() {
		// 100 items, no duplicates
		testNItems(0,100, false, 1000, false);
		testNItems(1,100, false, 1000, false);

		// 1000 items, no duplicates
		testNItems(0,1000, false, 10000, false);
		testNItems(1,1000, false, 10000, false);

		// 10 items, with duplicates
		testNItems(0,10, true, 20, false);
		testNItems(1,10, true, 20, false);

		// 100 items, with duplicates
		testNItems(0,100, true, 100, false);
		testNItems(1,100, true, 100, false);

		// 10 items, with duplicates and new value
		testNItems(0,10, true, 10, true);
		testNItems(1,10, true, 10, true);

		// 100 items, with duplicates and new value
		testNItems(0,100, true, 100, true);
		testNItems(1,100, true, 100, true);
		
		// 1000 items, with duplicates and new value
		testNItems(0,1000, true, 2000, true);
		testNItems(1,1000, true, 2000, true);
	}

	// testFirstKey(int type,  int length, int max, boolean first, boolean duplicates) {

	public void testSortedMapFirstKeyLastKey() {
		// null check
		testFirstLastKey(0, 1, false, 10, true, true);
		testFirstLastKey(1, 1, false, 10, true, true);
		testFirstLastKey(0, 1, false, 10, false, true);
		testFirstLastKey(1, 1, false, 10, false, true);

		// one element
		testFirstLastKey(0, 1, false, 10, true, false);
		testFirstLastKey(1, 1, false, 10, true, false);
		testFirstLastKey(0, 1, false, 10, false, false);
		testFirstLastKey(1, 1, false, 10, false, false);

		// 10 elements, no duplicates
		testFirstLastKey(0, 10, false, 10, true, false);
		testFirstLastKey(1, 10, false, 10, true, false);
		testFirstLastKey(0, 10, false, 10, false, false);
		testFirstLastKey(1, 10, false, 10, false, false);

		// 10 elements, duplicates
		testFirstLastKey(0, 10, true, 10, true, false);
		testFirstLastKey(1, 10, true, 10, true, false);
		testFirstLastKey(0, 10, true, 10, false, false);
		testFirstLastKey(1, 10, true, 10, false, false);

		// 100 elements, duplicates
		testFirstLastKey(0, 100, true, 100, true, false);
		testFirstLastKey(1, 100, true, 100, true, false);
		testFirstLastKey(0, 100, true, 100, false, false);
		testFirstLastKey(1, 100, true, 100, false, false);

		// 1 element, keep replacing it
		testFirstLastKey(0, 100, true, 1, true, false);
		testFirstLastKey(1, 100, true, 1, true, false);
		testFirstLastKey(0, 100, true, 1, false, false);
		testFirstLastKey(1, 100, true, 1, false, false);
	}

	// testContains(int type,  int length, boolean duplicates, int max, boolean nullCheck, 

	public void testSortedMapContains() {
		// null checks
		testContains(0, 1, false, 1, true);
		testContains(1, 1, false, 1, true);

		// 1 element
		testContains(0, 1, false, 10, false);
		testContains(1, 1, false, 10, false);

		// 10 elements, no duplicates
		testContains(0, 10, false, 10, false);
		testContains(1, 10, false, 10, false);

		// 10 elements with duplicates
		testContains(0, 10, true, 10, false);
		testContains(1, 10, true, 10, false);

		// 100 elements with duplicates
		testContains(0, 100, true, 100, false);
		testContains(1, 100, true, 100, false);
		
		// 10000 elements with duplicates
		testContains(0, 100, true, 100, false);
		testContains(1, 100, true, 100, false);
	}

	// tests get, size, putall
	public void testSortedMapFuncs() {
		// get null check
		testGet(0, 1, false, 1, true);
		testGet(1, 1, false, 1, true);

		// get with 100 elements, no duplicates
		testGet(0, 100, false, 100, false);
		testGet(1, 100, false, 100, false);

		// get with 100 elements, duplicates
		testGet(0, 100, true, 200, false);
		testGet(1, 100, true, 200, false);

		// get with 1000 elements, no duplicates
		testGet(0, 1000, false, 1000, false);
		testGet(1, 1000, false, 1000, false);

		// get with 1000 elements, duplicates
		testGet(0, 1000, true, 2000, false);
		testGet(1, 1000, true, 2000, false);

		// size with 1 element
		testSize(0, 1, false, 10);
		testSize(1, 1, false, 10);

		// size with 100 elements, no duplicates
		testSize(0, 100, false, 100);
		testSize(1, 100, false, 100);

		// size with 1000 elements, with duplicates
		testSize(0, 1000, true, 2000);
		testSize(1, 1000, true, 2000);

		// putAll null check
		testPutAll(0, 1, false, 10, true);
		testPutAll(1, 1, false, 10, true);

		// putAll 10 elements no duplicates
		testPutAll(0, 10, false, 20, false);
		testPutAll(0, 10, false, 20, false);

		// putAll 100 elements no duplicates
		testPutAll(0, 100, true, 200, false);
		testPutAll(0, 100, true, 200, false);

		// putAll 1000 elements with duplicates
		testPutAll(0, 1000, true, 2000, false);
		testPutAll(1, 1000, true, 2000, false);
	}

	// 	private void testEntrySet(int type,  int length, boolean duplicates, int max, 
	// 	boolean testIterator) {

	public void testSortedMapEntrySet () {
		// test entry set with empty map
		testEntrySet(0, 0, false, 10, false);
		testEntrySet(1, 0, false, 10, false);

		// test entry set with 1 element
		testEntrySet(0, 1, false, 10, false);
		testEntrySet(1, 1, false, 10, false);

		// test entry set with 100 elements, no duplicates
		testEntrySet(0, 100, false, 200, false);
		testEntrySet(1, 100, false, 200, false);

		// test entry set with 1000 elements, and duplicates
		testEntrySet(1, 1000, true, 2000, false);
		testEntrySet(1, 1000, true, 2000, false);
	}
	
	public void testSortedMapEntrySetIterator() {
		// test entry set + iterator with 10 elements, no duplicates
		testEntrySet(1, 10, false, 20, true);
		testEntrySet(1, 10, false, 20, true);

		// test entry set + iterator with 1000 elements, and duplicates
		testEntrySet(1, 1000, true, 2000, true);
		testEntrySet(1, 1000, true, 2000, true);
	}

	// testSubMap( int length, boolean duplicates, int max, int from, int to, boolean nullCheck, 
	// boolean testEntrySet, boolean testSubmapSubmap) {

	public void testSortedMapSubMap () {
		// null check
		testSubMap(10, false, 20, 0, 10, true, false, false);

		// test with 10 elements, no duplicates
		testSubMap(10, false, 20, 2, 8, false, false, false);	// range entirely within map
		testSubMap(10, false, 20, -5, 5, false, false, false);	// range covers lower half of map
		testSubMap(10, false, 20, 5, 15, false, false, false);	// range covers upper half of map
		testSubMap(10, false, 20, 11, 20, false, false, false);	// range is outside map

		// test with 10 elements, duplicates
		testSubMap(10, true, 20, 5, 15, false, false, false);	// range entirely within map
		testSubMap(10, true, 20, -10, 10, false, false, false);	// range covers lower half of map
		testSubMap(10, true, 20, 10, 20, false, false, false);	// range covers upper half of map
		testSubMap(10, true, 20, 21, 30, false, false, false);	// range is outside map

		// test with 100 elements, duplicates
		testSubMap(100, true, 200, 50, 150, false, false, false);	// range entirely within map
		testSubMap(100, true, 200, -100, 100, false, false, false);	// range covers lower half of map
		testSubMap(100, true, 200, 100, 200, false, false, false);	// range covers upper half of map
		testSubMap(100, true, 200, 210, 300, false, false, false);	// range is outside map

		// test edge cases
		testSubMap(20, true, 2, 1, 2, false, false, false);
	}
	
	// testSubMap( int length, boolean duplicates, int max, int from, int to, boolean nullCheck, 
	// boolean testEntrySet, boolean testSubmapSubmap) {
	
	public void testSortedMapSubMapEntrySet() {
		// test with 10 elements, no duplicates, tests entry set
		testSubMap(10, false, 20, 5, 15, false, true, false);	// range entirely within map
		testSubMap(10, false, 20, -10, 10, false, true, false);	// range covers lower half of map
		testSubMap(10, false, 20, 10, 20, false, true, false);	// range covers upper half of map
		testSubMap(10, false, 20, 50, 100, false, true, false);	// range is outside map		
		
		// test with 100 elements, duplicates, tests entry set
		testSubMap(100, true, 200, 50, 150, false, true, false);	// range entirely within map
		testSubMap(100, true, 200, -100, 100, false, true, false);	// range covers lower half of map
		testSubMap(100, true, 200, 100, 200, false, true, false);	// range covers upper half of map
		testSubMap(100, true, 200, 210, 300, false, true, false);	// range is outside map
		
	}
	
	// testSubMap( int length, boolean duplicates, int max, int from, int to, boolean nullCheck, 
	// boolean testEntrySet, boolean testSubmapSubmap) {
	
	public void testSortedMapSubmapSubmap() {
		
		// test submap submap with 100 elements, no duplicates
		testSubMap(100, false, 200, 50, 150, false, false, true);	// range entirely within map
		testSubMap(100, false, 200, -100, 100, false, false, true);	// range covers lower half of map
		testSubMap(100, false, 200, 100, 200, false, false, true);	// range covers upper half of map
		testSubMap(100, false, 200, 210, 300, false, false, true);	// range is outside map
		
		// test submap submap with 100 elements, duplicates, tests entry set
		testSubMap(100, true, 200, 50, 150, false, true, true);	// range entirely within map
		testSubMap(100, true, 200, -100, 100, false, true, true);	// range covers lower half of map
		testSubMap(100, true, 200, 100, 200, false, true, true);	// range covers upper half of map
		testSubMap(100, true, 200, 210, 300, false, true, true);	// range is outside map
		
	}	
	
	public void testSortedMapStress1() {
		int length = 15000000, refresh = 1000000;
		SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
		SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
		for (int i = 0; i < length; i++) {
			if (i % refresh == 0) {
				map.clear();
				rb.clear();
			}
			int val = rand.nextInt(Integer.MAX_VALUE);
			map.put(val, val);
			rb.put(val, val);
		}
		checkMapMatch(map, rb);
	}
	
	
	
	// tests put, get, and first/last key
	public void testSortedMapStress2() {
		int length = 10000000, refresh = 1000000;
		SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
		SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
		
		Set<Map.Entry<Integer,Integer>> mapSet = map.entrySet();
		Set<Map.Entry<Integer,Integer>> rbSet = rb.entrySet();
		
		for (int i = 1; i < length; i++) {
			if (i % refresh == 0) {
				map.clear();
				rb.clear();
			}
			int val = rand.nextInt(length * 10);
			map.put(val, val);
			rb.put(val, val);
		}
		
		for (int i = 0; i < length; i++) {
			int val = rand.nextInt(length * 10);
			assertEquals(map.get(val),rb.get(val));
			assertEquals(map.firstKey(),rb.firstKey());
			assertEquals(map.lastKey(),rb.lastKey());
		}
	}
	
	// tests put and entry set + iterator
	public void testSortedMapStress3() {
		int length = 15000000, refresh = 1000000;
		SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
		SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
		
		Set<Map.Entry<Integer,Integer>> mapSet = map.entrySet();
		Set<Map.Entry<Integer,Integer>> rbSet = rb.entrySet();
		
		for (int i = 1; i < length; i++) {
			if (i % refresh == 0) {
				map.clear();
				rb.clear();
			}
			int val = rand.nextInt(Integer.MAX_VALUE);
			map.put(val, val);
			rb.put(val, val);
		}
		checkMapMatch(map, rb);
		
		Iterator<Map.Entry<Integer,Integer>> mapIter = mapSet.iterator();
		Iterator<Map.Entry<Integer,Integer>> rbIter = rbSet.iterator();

		while(rbIter.hasNext()) {
			assertTrue("Entry set iterator ran out of entries",	mapIter.hasNext());
			Map.Entry<Integer,Integer> mapEntry = mapIter.next();
			Map.Entry<Integer,Integer> rbEntry = rbIter.next();
			assertEquals(mapEntry, rbEntry);
		}

		assertFalse("Iterator has too many elements", mapIter.hasNext());

		try {
			mapIter.next();
			fail("Invalid iterator.next() did not throw NoSuchElementException");
		} catch (NoSuchElementException e) {}
		
		
	}
	
	private void checkMapMatch(SortedMap map, SortedMap rb) {
		assertTrue("Treap is wrong - equals doesn't match.", map.equals(rb));
		assertTrue("Treap is wrong - eq3uals doesn't match.", rb.equals(map));
		assertTrue("Treap is wrong - toString doesn't match.", map.toString().equals(rb.toString()));
		assertTrue("Treap is wrong - toString doesn't match.", rb.toString().equals(map.toString()));
		assertTrue("Treap is wrong - hashCode doesn't match.", map.hashCode() == rb.hashCode());
	}

	private void checkSetMatch(Set map, Set rb) {
		assertTrue("Entry set is wrong - equals doesn't match.", map.equals(rb));
		assertTrue("Entry set is wrong - equals doesn't match.", rb.equals(map));
		assertTrue("Entry set is wrong - toString doesn't match.", map.toString().equals(rb.toString()));
		assertTrue("Entry set is wrong - toString doesn't match.", rb.toString().equals(map.toString()));
		assertTrue("Entry set is wrong - hashCode doesn't match.", map.hashCode() == rb.hashCode());
	}

	private void testConstructor(int type,  int length) {
		SortedMap<String,Integer> map = new Treap<String, Integer>();
		map = new Treap<String, Integer>(new StringComparator());
	}

	private void testEmpty(int type,  int length) {
		if (type == 0) {
			SortedMap<String,Integer> map = new Treap<String, Integer>(new StringComparator());
			assertTrue("isEmpty is wrong.", map.isEmpty());
			map.put("0", 0);
			assertFalse("isEmpty is wrong.", map.isEmpty());
		} else {
			SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
			assertTrue("isEmpty is wrong.", map.isEmpty());
			map.put(0, 0);
			assertFalse("isEmpty is wrong.", map.isEmpty());
		}
	}

	private void testNItems(int type,  int length, boolean duplicates, int max, boolean newValue) {
		if (type == 0) {
			SortedMap<String,Integer> map = new Treap<String, Integer>(new StringComparator());
			SortedMap<String,Integer> rb = new TreeMap<String, Integer>(new StringComparator());
			checkMapMatch(map, rb);
			for (int i = 0; i < length; i++) {
				int val = rand.nextInt(max);
				if (!duplicates)
					val = i;
				map.put(Integer.toString(val), val);
				if (!duplicates) { assertFalse(map.equals(rb));	}
				rb.put(Integer.toString(val), val);
				checkMapMatch(map, rb);
			}
			if (newValue) {
				for (int i = 0; i < 5; i++) {
					int val = rand.nextInt(max);
					map.put(Integer.toString(val), -val);
					rb.put(Integer.toString(val), -val);
					checkMapMatch(map, rb);
				}
			}
		} else {
			SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
			SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
			checkMapMatch(map, rb);
			for (int i = 0; i < length; i++) {
				int val = rand.nextInt(max);
				if (!duplicates)
					val = i;
				map.put(val, val);
				if (!duplicates) { assertFalse(map.equals(rb)); }
				rb.put(val, val);
				checkMapMatch(map, rb);
			}
			if (newValue) {
				for (int i = 0; i < 5; i++) {
					int val = rand.nextInt(max);
					map.put(val, -val);
					rb.put(val, -val);
					checkMapMatch(map, rb);
				}
			}
		}
	}

	private void testFirstLastKey(int type,  int length, boolean duplicates, int max, boolean first, boolean nullCheck) {
		if (type == 0) {
			SortedMap<String,Integer> map = new Treap<String, Integer>(new StringComparator());
			SortedMap<String,Integer> rb = new TreeMap<String, Integer>(new StringComparator());

			if (nullCheck) {
				try {
					String key = "";
					if (first) {
						key = map.firstKey();
					} else {
						key = map.lastKey();
					}
					fail("calling firstKey or lastKey on an empty map should throw a NoSuchElementException.");
				} catch (NoSuchElementException e) {
					return;
				}
			}

			for (int i = 0; i < length; i++) {
				int val = rand.nextInt(max);
				if (!duplicates)
					val = i;
				map.put(Integer.toString(val), val);
				if (!duplicates) { assertFalse(map.equals(rb)); }
				rb.put(Integer.toString(val), val);
				if (first)
					assertEquals("firstKey did not return correct key.",map.firstKey(), rb.firstKey());
				else
					assertEquals("lastKey did not return correct key.",map.lastKey(), rb.lastKey());
				checkMapMatch(map, rb);
			}
		} else {
			SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
			SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();

			if (nullCheck) {
				try {
					int key = 0;
					if (first) {
						key = map.firstKey();
					} else {
						key = map.lastKey();
					}
					fail("calling firstKey or lastKey on an empty map should throw a NoSuchElementException.");
				} catch (NoSuchElementException e) {
					return;
				}
			}

			for (int i = 0; i < length; i++) {
				int val = rand.nextInt(max);
				if (!duplicates)
					val = i;
				map.put(val, val);
				if (!duplicates) { assertFalse(map.equals(rb)); }
				rb.put(val, val);
				if (first)
					assertEquals("firstKey did not return correct key.",map.firstKey(), rb.firstKey());
				else
					assertEquals("lastKey did not return correct key.",map.lastKey(), rb.lastKey());
				checkMapMatch(map, rb);
			}
		}
	}

	private void testContains(int type,  int length, boolean duplicates, int max, boolean nullCheck) {

		if (type == 0) {
			SortedMap<String,Integer> map = new Treap<String, Integer>(new StringComparator());
			SortedMap<String,Integer> rb = new TreeMap<String, Integer>(new StringComparator());
			if (nullCheck) {
				assertFalse("Treap contains a key that it shouldn't.", map.containsKey("1"));
				assertFalse("Treap contains a value that it shouldn't.",map.containsValue(1));
				try {
					map.containsKey(null);
					fail("containsKey(null) should throw a NullPointerException.");
				} catch (NullPointerException e) {}
			} else {
				for (int i = 0; i < length; i++) {
					int val = rand.nextInt(max);
					if (!duplicates)
						val = i;
					map.put(Integer.toString(val), val);
					if (!duplicates) { assertFalse(map.equals(rb)); }
					rb.put(Integer.toString(val), val);
					assertTrue("containsKey returned the wrong result.", map.containsKey(Integer.toString(val)));
					assertTrue("containsValue returned the wrong result.", map.containsValue(val));
				}
				for (int i = 0; i < length/10; i++) {
					int val = rand.nextInt(max);
					assertEquals("containsKey returned the wrong result.", map.containsKey(Integer.toString(val)), rb.containsKey(Integer.toString(val)));
					assertEquals("containsValue returned the wrong result.", map.containsValue(val), rb.containsValue(val));
				}
				assertFalse("Treap contains a key that it shouldn't.", map.containsKey(Integer.toString(max+1)));
				assertFalse("Treap contains a value that it shouldn't.",map.containsValue(max+1));
				assertFalse("Treap contains a key that it shouldn't.", map.containsKey(Integer.toString(-1)));
				assertFalse("Treap contains a value that it shouldn't.",map.containsValue(-1));
				assertFalse("Treap contains a value that it shouldn't.",map.containsValue("1"));
			}
		} else {
			SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
			SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
			if (nullCheck) {
				assertFalse("Treap contains a key that it shouldn't.", map.containsKey(1));
				assertFalse("Treap contains a value that it shouldn't.",map.containsValue(1));
				try {
					map.containsKey(null);
					fail("containsKey(null) should throw a NullPointerException.");
				} catch (NullPointerException e) {}
			} else {
				for (int i = 0; i < length; i++) {
					int val = rand.nextInt(max);
					if (!duplicates)
						val = i;
					map.put(val, val);
					if (!duplicates) { assertFalse(map.equals(rb)); }
					rb.put(val, val);
					assertTrue("containsKey returned the wrong result.",map.containsKey(val));
					assertTrue("containsValue returned the wrong result.",map.containsValue(val));
				}
				for (int i = 0; i < length/10; i++) {
					int val = rand.nextInt(max);
					assertEquals("containsKey returned the wrong result.",map.containsKey(val), rb.containsKey(val));
					assertEquals("containsValue returned the wrong result.",map.containsValue(val), rb.containsValue(val));
				}
				assertFalse("Treap contains a key that it shouldn't.", map.containsKey(max+1));
				assertFalse("Treap contains a value that it shouldn't.",map.containsValue(max+1));
				assertFalse("Treap contains a key that it shouldn't.", map.containsKey(-1));
				assertFalse("Treap contains a value that it shouldn't.",map.containsValue(-1));
			}
		}
	}

	// returns null if type is wrong
	// nullpointer if passing in null
	// returns null if not found

	private void testGet(int type,  int length, boolean duplicates, int max, boolean nullCheck) {
		if (type == 0) {
			SortedMap<String,Integer> map = new Treap<String, Integer>(new StringComparator());
			SortedMap<String,Integer> rb = new TreeMap<String, Integer>(new StringComparator());
			if (nullCheck) {
				assertNull("get did not return null when it should have.", map.get("1"));
				assertNull("get did not return null when it should have.", map.get(1));
				try {
					map.get(null);
					fail("get(null) should throw a NullPointerException.");
				} catch (NullPointerException e) {}
			} else {
				for (int i = 0; i < length; i++) {
					int val = rand.nextInt(max);
					if (!duplicates)
						val = i;
					map.put(Integer.toString(val), val);
					if (!duplicates) { assertFalse(map.equals(rb)); }
					rb.put(Integer.toString(val), val);
					assertEquals("get returned the wrong result.", map.get(Integer.toString(val)), new Integer(val));
				}
				for (int i = 0; i < length/10; i++) {
					int val = rand.nextInt(max);
					assertEquals("get returned the wrong result.",map.get(Integer.toString(val)), rb.get(Integer.toString(val)));
				}
				assertNull("get did not return null when it should have.", map.get(Integer.toString(max+1)));
				assertNull("get did not return null when it should have.", map.get(Integer.toString(-1)));
			}
		} else {
			SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
			SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
			if (nullCheck) {
				assertNull("get did not return null when it should have.", map.get("1"));
				assertNull("get did not return null when it should have.", map.get(1));
				try {
					map.get(null);
					fail("get(null) should throw a NullPointerException.");
				} catch (NullPointerException e) {}
			} else {
				for (int i = 0; i < length; i++) {
					int val = rand.nextInt(max);
					if (!duplicates)
						val = i;
					map.put(val, val);
					if (!duplicates) { assertFalse(map.equals(rb)); }
					rb.put(val, val);
					assertTrue("get returned the wrong result.", map.containsKey(val));
					assertTrue("get returned the wrong result.", map.containsValue(val));
				}
				for (int i = 0; i < length/10; i++) {
					int val = rand.nextInt(max);
					assertEquals("get returned the wrong result.", map.get(val), rb.get(val));
				}
				assertNull("get did not return null when it should have.", map.get(max+1));
				assertNull("get did not return null when it should have.", map.get(-1));
			}
		}
	}

	private void testSize(int type,  int length, boolean duplicates, int max) {
		if (type == 0) {
			SortedMap<String,Integer> map = new Treap<String, Integer>(new StringComparator());
			SortedMap<String,Integer> rb = new TreeMap<String, Integer>(new StringComparator());
			assertEquals("size returned the wrong result.", map.size(), rb.size());
			for (int i = 0; i < length; i++) {
				int val = rand.nextInt(max);
				if (!duplicates)
					val = i;
				map.put(Integer.toString(val), val);
				if (!duplicates) { assertFalse(map.equals(rb)); }
				rb.put(Integer.toString(val), val);
				assertEquals("size returned the wrong result.", map.size(), rb.size());
			}
			assertEquals("size returned the wrong result.", map.size(), rb.size());
		} else {
			SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
			SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();

			for (int i = 0; i < length; i++) {
				int val = rand.nextInt(max);
				if (!duplicates)
					val = i;
				map.put(val, val);
				if (!duplicates) { assertFalse(map.equals(rb)); }
				rb.put(val, val);
				assertEquals("size returned the wrong result.", map.size(), rb.size());
			}
			assertEquals("size returned the wrong result.", map.size(), rb.size());
		}
	}

	private void testPutAll(int type,  int length, boolean duplicates, int max, boolean nullCheck) {
		if (type == 0) {
			SortedMap<String,Integer> map = new Treap<String, Integer>(new StringComparator());
			SortedMap<String,Integer> rb = new TreeMap<String, Integer>(new StringComparator());
			SortedMap<String,Integer> temp = new TreeMap<String,Integer>(new StringComparator());
			if (nullCheck) {
				try {
					map.putAll(null);
					fail("putAll with a null argument should throw a NullPointerException.");
				} catch (NullPointerException e) {}
			} else {
				for (int i = 0; i < length; i++) {
					int val = rand.nextInt(max);
					temp.put(Integer.toString(val), val);
					if (duplicates) {
						map.putAll(temp);
						if (!duplicates) { assertFalse(map.equals(rb)); }
						rb.putAll(temp);
						checkMapMatch(map,rb);
					}
				}
				map.putAll(temp);
				rb.putAll(temp);
				checkMapMatch(map, rb);
				map.put(Integer.toString(max+1), max+1);
				map.putAll(temp);
				assertFalse("Treap is wrong after putAll. Possible reason: previous map entries were removed/overridden.", map.equals(temp));
				assertFalse("Treap is the wrong size after putAll.", map.size() == temp.size());
			}
		} else {
			SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
			SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
			SortedMap<Integer,Integer> temp = new TreeMap<Integer, Integer>();
			if (nullCheck) {
				try {
					map.putAll(null);
					fail("putAll with a null argument should throw a NullPointerException.");
				} catch (NullPointerException e) {}
			} else {
				for (int i = 0; i < length; i++) {
					int val = rand.nextInt(max);
					temp.put(val, val);
					if (duplicates) {
						map.putAll(temp);
						if (!duplicates) { assertFalse(map.equals(rb)); }
						rb.putAll(temp);
						checkMapMatch(map,rb);
					}
				}
				map.putAll(temp);
				rb.putAll(temp);
				checkMapMatch(map, rb);
				map.put(max+1, max+1);
				map.putAll(temp);
				assertFalse("Treap is wrong after putAll. Possible reason: previous map entries were removed/overridden.",map.equals(temp));
				assertFalse("Treap is the wrong size after putAll.",map.size() == temp.size());
			}
		}
	}

	private void testEntrySet(int type,  int length, boolean duplicates, int max, boolean testIterator) {
		if (type == 0) {
			SortedMap<String,Integer> map = new Treap<String, Integer>(new StringComparator());
			SortedMap<String,Integer> rb = new TreeMap<String, Integer>(new StringComparator());
			Set<Map.Entry<String,Integer>> mapSet = map.entrySet();
			Set<Map.Entry<String,Integer>> rbSet = rb.entrySet();
			for (int i = 0; i < length; i++) {
				int val = rand.nextInt(max);
				if (!duplicates)
					val = i;
				map.put(Integer.toString(val), val);
				if (!duplicates) { assertFalse(map.equals(rb)); }
				rb.put(Integer.toString(val), val);
				checkMapMatch(map, rb);
			}
			if (duplicates) {
				for (int i = 0; i < length/10; i++) {
					int val = rand.nextInt(max);
					map.put(Integer.toString(val), -val);
					rb.put(Integer.toString(val), -val);
					checkMapMatch(map, rb);
				}
			}
			Set<Map.Entry<String,Integer>> mapSet2 = map.entrySet();
			Set<Map.Entry<String,Integer>> rbSet2 = rb.entrySet();
			checkSetMatch(mapSet, rbSet);
			checkSetMatch(mapSet2, rbSet2);

			assertTrue("entrySet is wrong.", mapSet.containsAll(rbSet));
			assertTrue("entrySet is wrong.", rbSet.containsAll(mapSet));
			assertEquals("entrySet size is wrong.", mapSet.size(), rbSet.size());

			if (testIterator) {
				Iterator<Map.Entry<String,Integer>> mapIter = mapSet.iterator();
				Iterator<Map.Entry<String,Integer>> rbIter = rbSet.iterator();

				while(rbIter.hasNext()) {
					assertTrue("Entry set iterator ran out of entries",	mapIter.hasNext());
					assertEquals("Entry set iterator is wrong", rbIter.next(),	mapIter.next());

					Map.Entry<String,Integer> mapEntry = mapIter.next();
					Map.Entry<String,Integer> rbEntry = rbIter.next();
					assertEquals(mapEntry, rbEntry);
					assertTrue(mapSet.contains(rbEntry));

					rbEntry.setValue(999999);
					assertFalse("bad entry setValue()", rb.equals(map));
					assertFalse("bad entry setValue()", rbSet.equals(mapSet));
					mapEntry.setValue(999999);
					assertTrue("bad entry setValue()", rb.equals(map));
					assertTrue("bad entry setValue()", rbSet.equals(mapSet));
				}

				assertFalse("Iterator has too many elements", mapIter.hasNext());
				assertTrue("Entry set isEmpty is wrong.", mapSet.isEmpty());

				try {
					mapIter.next();
					fail("Invalid iterator.next() did not throw NoSuchElementException");
				} catch (NoSuchElementException e) {}
			}
		} else {
			SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
			SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
			Set<Map.Entry<Integer,Integer>> mapSet = map.entrySet();
			Set<Map.Entry<Integer,Integer>> rbSet = rb.entrySet();
			for (int i = 0; i < length; i++) {
				int val = rand.nextInt(max);
				if (!duplicates)
					val = i;
				map.put(val, val);
				if (!duplicates) { assertFalse(map.equals(rb)); }
				rb.put(val, val);
				checkMapMatch(map, rb);
			}
			if (duplicates) {
				for (int i = 0; i < length/10; i++) {
					int val = rand.nextInt(max);
					map.put(val, -val);
					rb.put(val, -val);
					checkMapMatch(map, rb);
				}
			}
			Set<Map.Entry<Integer,Integer>> mapSet2 = map.entrySet();
			Set<Map.Entry<Integer,Integer>> rbSet2 = rb.entrySet();
			checkSetMatch(mapSet, rbSet);
			checkSetMatch(mapSet2, rbSet2);

			assertTrue("entrySet is wrong.", mapSet.containsAll(rbSet));
			assertTrue("entrySet is wrong.", rbSet.containsAll(mapSet));
			assertEquals("entrySet size is wrong.", mapSet.size(), rbSet.size());

			if (testIterator) {
				Iterator<Map.Entry<Integer,Integer>> mapIter = mapSet.iterator();
				Iterator<Map.Entry<Integer,Integer>> rbIter = rbSet.iterator();

				while(rbIter.hasNext()) {
					assertTrue("Entry set iterator ran out of entries",	mapIter.hasNext());

					Map.Entry<Integer,Integer> mapEntry = mapIter.next();
					Map.Entry<Integer,Integer> rbEntry = rbIter.next();
					assertEquals(mapEntry, rbEntry);
					assertTrue(mapSet.contains(rbEntry));

					rbEntry.setValue(999999);
					assertFalse("bad entry setValue()", rb.equals(map));
					assertFalse("bad entry setValue()", rbSet.equals(mapSet));
					mapEntry.setValue(999999);
					assertTrue("bad entry setValue()", rb.equals(map));
					assertTrue("bad entry setValue()", rbSet.equals(mapSet));


				}

				if (mapIter.hasNext() || rbIter.hasNext()) {
					fail("EntrySet iterator is the wrong size. ");
				}

				try {
					mapIter.next();
					fail("iterator.next() did not throw NoSuchElementException");
				} catch (NoSuchElementException e) {
				}
			}
		}
	}

	private void testSubMap( int length, boolean duplicates, int max, int from, int to, 
			boolean nullCheck, boolean testEntrySet, boolean testSubmapSubmap) {

		SortedMap<Integer,Integer> map = new Treap<Integer, Integer>();
		SortedMap<Integer,Integer> rb = new TreeMap<Integer, Integer>();
		SortedMap<Integer,Integer> mapSub = map.subMap(from, to);
		SortedMap<Integer,Integer> rbSub = rb.subMap(from, to);
		int start = from;
		int end = to;
		
		if (testSubmapSubmap) {
			SortedMap<Integer,Integer> mapsub1 = map.subMap(from, to);
			SortedMap<Integer,Integer> rbsub1 = rb.subMap(from, to);
			int quarter = (to - from)/4;
			start = from + quarter;
			end = to - quarter;
			mapSub = mapsub1.subMap(start, end);
			rbSub = rbsub1.subMap(start, end);
		}

		Set<Map.Entry<Integer,Integer>> mapSet = null;
		Set<Map.Entry<Integer,Integer>> rbSet = null;

		if (testEntrySet) {
			mapSet = mapSub.entrySet();
			rbSet = rbSub.entrySet();
		}

		if (nullCheck) {
			try {
				mapSub.put(to+1, to+1);
				fail("IllegalArgumentException should be thrown when inserting a key outside the subMap range into the subMap.");
			} catch (IllegalArgumentException e) {}
			return;
		}

		// add stuff to map, check that submap and map are correct
		for (int i = 0; i < length; i++) {
			int val = rand.nextInt(max);
			if (!duplicates)
				val = i;
			map.put(val, val);
			if (!duplicates) { assertFalse(map.equals(rb)); }
			rb.put(val, val);
			checkMapMatch(map, rb);
			checkMapMatch(mapSub, rbSub);
			if (testEntrySet) { checkSetMatch(mapSet, rbSet); }
		}
		if (duplicates) {
			for (int i = 0; i < 5; i++) {
				int val = rand.nextInt(max);
				map.put(val, -val);
				rb.put(val, -val);
				checkMapMatch(map, rb);
				checkMapMatch(mapSub, rbSub);
				if (testEntrySet) { checkSetMatch(mapSet, rbSet); }
			}
		}

		map.clear();
		rb.clear();
		checkMapMatch(mapSub, rbSub);
		if (testEntrySet) { checkSetMatch(mapSet, rbSet); }

		// add stuff to submap, check if submap and map are the same
		for (int i = 0; i < length; i++) {
			int val = rand.nextInt(max);
			if (!duplicates)
				val = i;
			if (val >= start && val < end) {
				mapSub.put(val, val);
				if (!duplicates) { assertFalse(map.equals(rb)); }
				rbSub.put(val, val);
				checkMapMatch(mapSub, rbSub);
				checkMapMatch(mapSub, rbSub);
				if (testEntrySet) { checkSetMatch(mapSet, rbSet); }
			}
		}
		checkMapMatch(mapSub, rbSub);
		checkMapMatch(map, rb);

		if (testEntrySet) {
			assertTrue("entrySet is wrong.", mapSet.containsAll(rbSet));
			assertTrue("entrySet is wrong.", rbSet.containsAll(mapSet));
			assertEquals("entrySet size is wrong.", mapSet.size(), rbSet.size());

			Iterator<Map.Entry<Integer,Integer>> mapIter = mapSet.iterator();
			Iterator<Map.Entry<Integer,Integer>> rbIter = rbSet.iterator();

			while(rbIter.hasNext()) {
				assertTrue("Submap entry set iterator ran out of entries",	mapIter.hasNext());

				Map.Entry<Integer,Integer> mapEntry = mapIter.next();
				Map.Entry<Integer,Integer> rbEntry = rbIter.next();
				assertEquals("Submap entry set iterator is wrong", mapEntry, rbEntry);
				assertTrue("Submap entry set iterator is wrong", mapSet.contains(rbEntry));

				rbEntry.setValue(999999);
				assertFalse("Submap entry set bad entry setValue()", rb.equals(map));
				assertFalse("Submap entry set bad entry setValue()", rbSet.equals(mapSet));
				mapEntry.setValue(999999);
				assertTrue("Submap entry set bad entry setValue()", rb.equals(map));
				assertTrue("Submap entry set bad entry setValue()", rbSet.equals(mapSet));
			}

			if (mapIter.hasNext() || rbIter.hasNext()) {
				fail("EntrySet iterator is the wrong size. ");
			}

			try {
				mapIter.next();
				fail("iterator.next() did not throw NoSuchElementException");
			} catch (NoSuchElementException e) {
			}
		}
	}
}
