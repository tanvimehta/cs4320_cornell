
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;


public class Tests {
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
	
	@Test
	public void testSimpleSearch(){
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		for(char c:alphabet){
			System.out.println("Searching char "+c+" in tree");
			assertEquals(tree.search(c), String.valueOf(c));
		}
		
		assertEquals(tree.search('m'), null);
		
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
		assertEquals(test, correct);

		tree.delete(2);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@8/10/12/14/@%%[(4,4);(5,5);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);
		
		tree.delete(8);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@7/10/12/14/@%%[(4,4);(5,5);]#[(7,7);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);
		
		tree.delete(10);
		tree.delete(12);
		System.out.println("deleted 12");
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@7/11/14/@%%[(4,4);(5,5);]#[(7,7);(9,9);]#[(11,11);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);
	}
	
	@Test
	public void testSimpleInsertion1() {
		Character alphabet[] = new Character[] { '1', '2', '3', '4','5' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = Utils.outputTree(tree);
		String correct = "@3/@%%[(1,1);(2,2);]#[(3,3);(4,4);(5,5);]$%%";

		assertEquals(correct, test);
	}
	
	@Test
	public void testSimpleInsertionWithHeight1() {
		Character alphabet[] = new Character[] { '1', '2', '3', '4','5', '6', '7' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = Utils.outputTree(tree);
		String correct = "@3/5/@%%[(1,1);(2,2);]#[(3,3);(4,4);]#[(5,5);(6,6);(7,7);]$%%";

		assertEquals(correct, test);
	}
	
	@Test
	public void testSimpleInsertionWithHeight2() {
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd','e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm'};
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = Utils.outputTree(tree);
		String correct = "@g/@%%@c/e/@@i/k/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);]$[(g,g);(h,h);]#[(i,i);(j,j);]#[(k,k);(l,l);(m,m);]$%%";

		assertEquals(correct, test);
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
		assertEquals(test, correct);
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

		tree.delete(6);
		tree.delete(7);
		tree.delete(8);
		String test = Utils.outputTree(tree);
		Utils.printTree(tree);

		String result = "@4/@%%[(2,2);(3,3);]#[(4,4);(5,5);]$%%";
		assertEquals(result, test);
	}
	
	@Test
	public void testVerySimpleTreeWithLeaf(){
		Character alphabet[] = new Character[] { '1', '2', '3', '4' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = Utils.outputTree(tree);
		System.out.println(test);
		String correct = "[(1,1);(2,2);(3,3);(4,4);]$%%";

		assertEquals(correct, test);
	}
	@Test
	public void deleteAllEntriesFromTree(){
		Integer numbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12};
		String primeNumberStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			primeNumberStrings[i] = (numbers[i]).toString();
		}
		
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, primeNumberStrings);
		tree.delete(2);
		tree.delete(4);
		tree.delete(5);
		tree.delete(7);
		tree.delete(8);
		tree.delete(9);
		tree.delete(10);
		tree.delete(11);

		assertEquals(Utils.outputTree(tree), "[(12,12);]$%%");

		tree.delete(12);
		assertEquals(null, tree.root);
		
	}

	@Test
	public void testIndexSplitting(){
		Integer numbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,13,14,15,16,17,18,19,20,21,22,23,24,25};
		String primeNumberStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			primeNumberStrings[i] = (numbers[i]).toString();
		}
		
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, primeNumberStrings);
		
		tree.delete(15);
		String expected="@16/@%%@5/8/10/12/@@18/20/22/@%%[(2,2);(4,4);]#[(5,5);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);(14,14);]$[(16,16);(17,17);]#[(18,18);(19,19);]#[(20,20);(21,21);]#[(22,22);(23,23);(24,24);(25,25);]$%%";
		assertEquals(expected, Utils.outputTree(tree));
	}
	
	@Test
	public void testIndexMerging(){
		Integer numbers[] = new Integer[] { 20, 40, 50, 70, 80, 90, 100, 110, 120,130,
				140,150,160,170,180,190,200,210,220,230,240,250, 81,82,83,84,85};
		String primeNumberStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			primeNumberStrings[i] = (numbers[i]).toString();
		}
		
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, primeNumberStrings);
		
		System.out.println(Utils.outputTree(tree));
		tree.delete(82);
		tree.delete(83);
		tree.delete(130);
		tree.delete(120);
		
		System.out.println(Utils.outputTree(tree));
		String expected="@84/160/@%%@50/80/@@100/140/@@180/200/220/@%%[(20,20);(40,40);]#[(50,50);(70,70);]#[(80,80);(81,81);]$[(84,84);(85,85);(90,90);]#[(100,100);(110,110);]#[(140,140);(150,150);]$[(160,160);(170,170);]#[(180,180);(190,190);]#[(200,200);(210,210);]#[(220,220);(230,230);(240,240);(250,250);]$%%";
		assertEquals(expected, Utils.outputTree(tree));
	}
	
	@Test
	public void testIndexMerging1(){
		Integer numbers[] = new Integer[] { 20, 40, 50, 70, 80, 90, 100, 110, 120,130,
				140,150,160,170,180,190,200,210,220,230,240,250, 131,132,133,134,135};
		String primeNumberStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			primeNumberStrings[i] = (numbers[i]).toString();
		}
		
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, primeNumberStrings);
		
		System.out.println(Utils.outputTree(tree));
		System.out.println("Delete 20:");
		tree.delete(20);
		
		System.out.println(Utils.outputTree(tree));
		String expected="@120/160/@%%@80/100/@@131/133/140/@@180/200/220/@%%[(40,40);(50,50);(70,70);]#[(80,80);(90,90);]#[(100,100);(110,110);]$[(120,120);(130,130);]#[(131,131);(132,132);]#[(133,133);(134,134);(135,135);]#[(140,140);(150,150);]$[(160,160);(170,170);]#[(180,180);(190,190);]#[(200,200);(210,210);]#[(220,220);(230,230);(240,240);(250,250);]$%%";
		assertEquals(expected, Utils.outputTree(tree));
	}
	
	
	@Test
	public void testRandomDeletions(){
		Integer numbers[] = new Integer[] { 20, 40, 50, 70, 80, 90, 100, 110, 120,130,
				140,150,160,170,180,190,200,210};
		String primeNumberStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			primeNumberStrings[i] = (numbers[i]).toString();
		}
		
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, primeNumberStrings);
		
		ArrayList<Integer> shuffledList= new ArrayList<Integer>(Arrays.asList(numbers));
		Collections.shuffle(shuffledList);
		
		for(int number:shuffledList){
			tree.delete(number);
		}
		
		assertEquals(tree.root, null);
	}
	
	@Test
	public void deleteAllEntriesFromTreeFromRight(){
		Integer numbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,13,14,15,16,17,18,19,20,21,22,23,24,25};
		String primeNumberStrings[] = new String[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			primeNumberStrings[i] = (numbers[i]).toString();
		}
		
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, numbers, primeNumberStrings);
		
		tree.delete(25);
		tree.delete(24);
		tree.delete(23);
		tree.delete(22);
		tree.delete(21);
		tree.delete(20);
		
		tree.delete(19);
		tree.delete(18);
		tree.delete(17);
		tree.delete(16);
		tree.delete(15);
		
		tree.delete(12);
		tree.delete(11);
		tree.delete(10);
		tree.delete(9);
		tree.delete(8);
		tree.delete(7);
		tree.delete(5);
		tree.delete(4);

		assertEquals(Utils.outputTree(tree), "[(2,2);(13,13);(14,14);]$%%");

		tree.delete(2);
		tree.delete(13);
		tree.delete(14);
		assertEquals(null, tree.root);
		
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
}
