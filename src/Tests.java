import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

public class Tests {
	
	@Test
	public void testDeleteAll() {
		
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		String test = Utils.outputTree(tree);
		String correct = "@c/e/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);
		
		tree.delete('a');
		tree.delete('b');
		tree.delete('c');
		tree.delete('d');
		tree.delete('e');
		tree.delete('f');
		tree.delete('g');
		assertEquals(null, tree.root);
		
	}
	
	@Test
	public void testDeleteMultipleLeafUnderflowToRoot() {
		System.out.println("\n testSimpleHybrid");
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		String test = Utils.outputTree(tree);
		String correct = "@c/e/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);

		//Testing Leaf Underflow which causes leaf merging
		tree.delete('a');
		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);
		
		//Testing leaf underflow with merging
		tree.delete('b');
		tree.delete('c');
		test = Utils.outputTree(tree);
		correct = "@f/@%%[(d,d);(e,e);]#[(f,f);(g,g);]$%%";
		assertEquals(correct, test);
		
		tree.delete('d');
		test = Utils.outputTree(tree);
		correct = "[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);
		
		//Checking if the root has been made a leaf node
		assertEquals(true, tree.root.isLeafNode);
	}
	
	// add some nodes, see if it comes out right, delete one, see if it's right
	@Test
	public void testSimpleHybrid() {
		System.out.println("\n testSimpleHybrid");
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = Utils.outputTree(tree);
		String correct = "@c/e/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";

		assertEquals(correct, test);

		tree.delete('a');

		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);

	}

	// add some nodes, see if it comes out right, delete one, see if it's right
	@Test
	public void testSimpleHybrid2() {
		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

		String test = Utils.outputTree(tree);
		String correct = "@10/@%%@5/8/@@12/14/@%%[(2,2);(4,4);]#[(5,5);(7,7);]#[(8,8);(9,9);]$[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(correct, test);

		tree.delete(2);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@8/10/12/14/@%%[(4,4);(5,5);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(correct, test);
	}

	@Test
	public void testIndexRedistribute() {
		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

		String test = Utils.outputTree(tree);
		String correct = "@10/@%%@5/8/@@12/14/@%%[(2,2);(4,4);]#[(5,5);(7,7);]#[(8,8);(9,9);]$[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(correct, test);

		tree.delete(5);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@8/10/12/14/@%%[(2,2);(4,4);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(correct, test);
	}
	
	@Test
	public void insertConditionSimpleIndexRedistribute() {
		Integer numbers[] = new Integer[] { 1,3,7,10,11,13,14,15,18,16,19,24,25,26,21,4,5,20,22,2,17,12,6};
		String numbersStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			numbersStrings[i] = (numbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, numbersStrings);

		String test = Utils.outputTree(tree);
		String correct = "@14/@%%@3/7/11/@@16/19/24/@%%[(1,1);(2,2);]#[(3,3);(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]#[(24,24);(25,25);(26,26);]$%%";
		assertEquals(test, correct);		
	}
	
	@Test
	public void insertConditionIndexRedistribute() {
		Integer numbers[] = new Integer[] { 1,3,7,10,11,13,14,15,18,16,19,24,25,26,21,4,5,20,22,2,17,12,6,29,40,35,38};
		String numbersStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			numbersStrings[i] = (numbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, numbersStrings);

		String test = Utils.outputTree(tree);
		String correct = "@14/24/@%%@3/7/11/@@16/19/@@26/35/@%%[(1,1);(2,2);]#[(3,3);(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$[(24,24);(25,25);]#[(26,26);(29,29);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);		
	}
	
	@Test
	public void deleteConditionLeftsibling() {
		Integer numbers[] = new Integer[] { 1,3,7,10,11,13,14,15,18,16,19,24,25,26,21,4,5,20,22,2,17,12,6,29,40,35,38};
		String numbersStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			numbersStrings[i] = (numbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, numbersStrings);

		String test = Utils.outputTree(tree);
		String correct = "@14/24/@%%@3/7/11/@@16/19/@@26/35/@%%[(1,1);(2,2);]#[(3,3);(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$[(24,24);(25,25);]#[(26,26);(29,29);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);
		tree.delete(2);
		test = Utils.outputTree(tree);
		correct = "@14/24/@%%@4/7/11/@@16/19/@@26/35/@%%[(1,1);(3,3);]#[(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$[(24,24);(25,25);]#[(26,26);(29,29);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);
		//tree.delete(22);
		tree.delete(24);
		test = Utils.outputTree(tree);
		correct = "@14/@%%@4/7/11/@@16/19/24/35/@%%[(1,1);(3,3);]#[(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]#[(25,25);(26,26);(29,29);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);
		
	}
	
	@Test
	public void deleteConditionRightsibling() {
		Integer numbers[] = new Integer[] { 1,3,7,10,11,13,14,15,18,16,19,24,25,26,21,4,5,20,22,2,17,12,6,29,40,35,38};
		String numbersStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			numbersStrings[i] = (numbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, numbersStrings);

		String test = Utils.outputTree(tree);
		String correct = "@14/24/@%%@3/7/11/@@16/19/@@26/35/@%%[(1,1);(2,2);]#[(3,3);(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$[(24,24);(25,25);]#[(26,26);(29,29);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);
		tree.delete(2);
		test = Utils.outputTree(tree);
		correct = "@14/24/@%%@4/7/11/@@16/19/@@26/35/@%%[(1,1);(3,3);]#[(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$[(24,24);(25,25);]#[(26,26);(29,29);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);
		//tree.delete(22);
		tree.delete(26);
		tree.insert(30, "30");
		tree.insert(31, "31");
		test = Utils.outputTree(tree);
		correct = "@14/24/@%%@4/7/11/@@16/19/@@29/35/@%%[(1,1);(3,3);]#[(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$[(24,24);(25,25);]#[(29,29);(30,30);(31,31);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);		
	}
	
	@Test
	public void simpleDeleteConditionRightsibling() {
		Integer numbers[] = new Integer[] { 1,3,7,10,11,13,14,15,18,16,19,24,25,26,21,4,5,20,22,2,17,12,6,29,40,35,38};
		String numbersStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			numbersStrings[i] = (numbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, numbersStrings);

		String test = Utils.outputTree(tree);
		String correct = "@14/24/@%%@3/7/11/@@16/19/@@26/35/@%%[(1,1);(2,2);]#[(3,3);(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$[(24,24);(25,25);]#[(26,26);(29,29);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);
		tree.delete(2);
		test = Utils.outputTree(tree);
		correct = "@14/24/@%%@4/7/11/@@16/19/@@26/35/@%%[(1,1);(3,3);]#[(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$[(24,24);(25,25);]#[(26,26);(29,29);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);
		tree.delete(22);
		tree.delete(29);
		tree.insert(30, "30");
		tree.insert(31, "31");
		test = Utils.outputTree(tree);
		correct = "@14/24/@%%@4/7/11/@@16/19/@@26/35/@%%[(1,1);(3,3);]#[(4,4);(5,5);(6,6);]#[(7,7);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);]$[(24,24);(25,25);]#[(26,26);(30,30);(31,31);]#[(35,35);(38,38);(40,40);]$%%";
		assertEquals(test, correct);		
	}
	
	
	@Test
	public void searchTest() {
		Integer numbers[] = new Integer[] { 1,3,7,10,11,13,14,15,18,16,19,24,25,26,21,4,5,20,22,2,17,12,6,29,40,35,38};
		String numbersStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			numbersStrings[i] = (numbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, numbersStrings);
		assertEquals(("24"), tree.search(24));	
		assertEquals(("40"), tree.search(40));
		assertEquals(("7"), tree.search(7));
		assertEquals((null), tree.search(100));
	}
	
	@Test
	public void testBookExampleShort() {
		Integer exampleNumbers[] = new Integer[] { 2, 3, 13, 14, 17, 19, 24, 27,
				30, 33, 34, 38, 5, 7, 16, 20, 22, 29 };
		String primeNumberStrings[] = new String[exampleNumbers.length];
		for (int i = 0; i < exampleNumbers.length; i++) {
			primeNumberStrings[i] = (exampleNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, exampleNumbers, primeNumberStrings);
		Utils.printTree(tree);
		tree.delete(13);
		tree.delete(17);
		tree.delete(30);
		tree.insert(39, "39");
		Utils.printTree(tree);
		// Initial tree
		String test = Utils.outputTree(tree);
		String correct = "@13/17/24/30/@%%[(2,2);(3,3);(5,5);(7,7);]#[(14,14);(16,16);]#[(19,19);(20,20);(22,22);]#[(24,24);(27,27);(29,29);]#[(33,33);(34,34);(38,38);(39,39);]$%%";
		assertEquals(correct, test);
	}

	// testing proper leaf node merging behaviour
	@Test
	public void testDeleteLeafNodeRedistribute() {
		Integer testNumbers[] = new Integer[] { 2, 4, 7, 8, 5, 6, 3 };
		String testNumberStrings[] = new String[testNumbers.length];
		for (int i = 0; i < testNumbers.length; i++) {
			testNumberStrings[i] = (testNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, testNumbers, testNumberStrings);
		Utils.printTree(tree);
		tree.delete(6);
		tree.delete(7);
		tree.delete(8);
		String test = Utils.outputTree(tree);
		Utils.printTree(tree);
		String result = "@4/@%%[(2,2);(3,3);]#[(4,4);(5,5);]$%%";
		assertEquals(result, test);
	}

	// Testing appropriate depth and node invariants on a big tree
	@Test
	public void testLargeTree() {
		BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>();
		ArrayList<Integer> numbers = new ArrayList<Integer>(100000);
		for (int i = 0; i < 100000; i++) {
			numbers.add(i);
		}
		Collections.shuffle(numbers);
		for (int i = 0; i < 100000; i++) {
			tree.insert(numbers.get(i), numbers.get(i));
		}
		testTreeInvariants(tree);

		assertTrue(treeDepth(tree.root) < 11);
	}

	public <K extends Comparable<K>, T> void testTreeInvariants(
			BPlusTree<K, T> tree) {
		for (Node<K, T> child : ((IndexNode<K, T>) (tree.root)).children)
			testNodeInvariants(child);
	}

	public <K extends Comparable<K>, T> void testNodeInvariants(Node<K, T> node) {
		assertFalse(node.keys.size() > 2 * BPlusTree.D);
		assertFalse(node.keys.size() < BPlusTree.D);
		if (!(node.isLeafNode))
			for (Node<K, T> child : ((IndexNode<K, T>) node).children)
				testNodeInvariants(child);
	}

	public <K extends Comparable<K>, T> int treeDepth(Node<K, T> node) {
		if (node.isLeafNode)
			return 1;
		int childDepth = 0;
		int maxDepth = 0;
		for (Node<K, T> child : ((IndexNode<K, T>) node).children) {
			childDepth = treeDepth(child);
			if (childDepth > maxDepth)
				maxDepth = childDepth;
		}
		return (1 + maxDepth);
	}
	
	@Test
	public void testcase1() {

		BPlusTree<String, String> tree = new BPlusTree<String, String>();
		tree.insert("a1", "a1");

		String test = tree.search("a1");
		String correct = "a1";

		assertEquals(correct, test);

		tree.insert("b2", "b2");

		test = tree.search("b2");
		correct = "b2";

		assertEquals(correct, test);
	}

	@Test
	public void testcase2() {
		Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g','h' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		
		String test = tree.search('a');
		String correct = "a";
		assertEquals(correct, test);

		test = tree.search('g');
		correct = "g";
		assertEquals(correct, test);

		test = tree.search('c');
		correct = "c";
		assertEquals(correct, test);

		tree.delete('a');

		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('b');
		test = Utils.outputTree(tree);
		correct = "@e/@%%[(c,c);(d,d);]#[(e,e);(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('c');

		test = Utils.outputTree(tree);
		correct = "@f/@%%[(d,d);(e,e);]#[(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);
//		Utils.printTree(tree);
		tree.delete('d');

		test = Utils.outputTree(tree);
//		Utils.printTree(tree);
		correct = "@g/@%%[(e,e);(f,f);]#[(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('e');

		test = Utils.outputTree(tree);
		correct = "[(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('f');

		test = Utils.outputTree(tree);
		correct = "[(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('g');
		test = Utils.outputTree(tree);
		correct = "[(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('h');
	}

	@Test
	public void testcase3() {
		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14,
				15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

		String test = tree.search(2);
		String correct = "2";
		assertEquals(test, correct);

		test = tree.search(16);
		correct = "16";
		assertEquals(test, correct);

		test = tree.search(5);
		correct = "5";
		assertEquals(test, correct);

		test = tree.search(8);
		correct = "8";
		assertEquals(test, correct);

		tree.delete(2);
		test = Utils.outputTree(tree);
		correct = "@8/10/12/14/@%%[(4,4);(5,5);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);

		tree.insert(3, "3");
		test = Utils.outputTree(tree);
		correct = "@8/10/12/14/@%%[(3,3);(4,4);(5,5);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);

		tree.delete(8);
		test = Utils.outputTree(tree);
		correct = "@5/10/12/14/@%%[(3,3);(4,4);]#[(5,5);(7,7);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);

		Integer specialNumbers[] = new Integer[] { 22, 24, 25, 27, 28, 29, 30, 31, 32, 33};
		String specialNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < specialNumbers.length; i++) {
			specialNumberStrings[i] = (specialNumbers[i]).toString();
		}
		Utils.bulkInsert(tree, specialNumbers, specialNumberStrings);
		correct = "@12/24/@%%@5/10/@@14/16/@@27/29/31/@%%[(3,3);(4,4);]#[(5,5);(7,7);(9,9);]#[(10,10);(11,11);]$[(12,12);(13,13);]#[(14,14);(15,15);]"
				+ "#[(16,16);(22,22);]$[(24,24);(25,25);]#[(27,27);(28,28);]#[(29,29);(30,30);]#[(31,31);(32,32);(33,33);]$%%";
		test = Utils.outputTree(tree);		
		assertEquals(test, correct);		
		tree.delete(3);		
		tree.delete(4);		
		tree.delete(5);		
		tree.delete(33);		
		tree.delete(32);		
		tree.delete(31);
		tree.delete(30);
		correct = "@14/@%%@10/12/@@16/24/27/@%%[(7,7);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]$[(14,14);(15,15);]#[(16,16);(22,22);]#[(24,24);(25,25);]#[(27,27);(28,28);(29,29);]$%%";		
		test = Utils.outputTree(tree);		
		assertEquals(test, correct);
	}
}
