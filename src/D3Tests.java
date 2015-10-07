import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

public class D3Tests {
	
	@Test
	public void testDeleteAll() {
		
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		
		tree.delete('a');
		tree.delete('b');
		tree.delete('c');
		tree.delete('d');
		tree.delete('e');
		tree.delete('f');
		tree.delete('g');
		assertEquals(null, tree.root);
	}
	
	//D = 3 Leaf merging left heavy
	@Test
	public void testD3InsertionLeafRedistributeLeftSide() {

		System.out.println("\n testD3Insertion");
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}

		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);
		String test = Utils.outputTree(tree);
		String correct = "@d/@%%[(a,a);(b,b);(c,c);]#[(d,d);(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);

		tree.delete('e');
		tree.delete('f');
		test = Utils.outputTree(tree);
		correct = "[(a,a);(b,b);(c,c);(d,d);(g,g);]$%%";
		assertEquals(correct, test);
	}
		
	@Test
	public void testD3InsertionIndexMerge() {
		Integer primeNumbers[] = new Integer[] { 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

		String test = Utils.outputTree(tree);

		tree.delete(26);
		tree.delete(23);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		String correct = "@14/@%%@4/8/11/@@17/20/26/@%%[(1,1);(2,2);(3,3);]#[(4,4);(5,5);(7,7);]#[(8,8);(9,9);(10,10);]#[(11,11);(12,12);(13,13);]$[(14,14);(15,15);(16,16);]#[(17,17);(18,18);(19,19);]#[(20,20);(21,21);(22,22);(24,24);(25,25);]#[(27,27);(28,28);(29,29);(30,30);]$%%";
		assertEquals(test, correct);
	}
	
	//D = 3 Leaf Merge when index key is deleted
	@Test
	public void testD3InsertionLeafMergeIndexKeyDelete() {

		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();	
		}
	
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);
		String test = Utils.outputTree(tree);
		String correct = "@7/10/13/@%%[(2,2);(4,4);(5,5);]#[(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);
		
		tree.delete(7);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@10/13/@%%[(2,2);(4,4);(5,5);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);(16,16);]$%%";	
		assertEquals(test, correct);
	}
		
	// add some nodes, see if it comes out right, delete one, see if it's right
	@Test
	public void testD3InsertionLeafMerge() {
		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

		String test = Utils.outputTree(tree);
		String correct = "@7/10/13/@%%[(2,2);(4,4);(5,5);]#[(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);

		tree.delete(2);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		correct = "@10/13/@%%[(4,4);(5,5);(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);
	}
	
	@Test
	public void testD3InsertionIndexRedistribution() {
		Integer primeNumbers[] = new Integer[] { 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

		String test = Utils.outputTree(tree);

		tree.delete(11);
		test = Utils.outputTree(tree);
		Utils.printTree(tree);
		String correct = "@17/@%%@4/8/14/@@20/23/26/@%%[(1,1);(2,2);(3,3);]#[(4,4);(5,5);(7,7);]#[(8,8);(9,9);(10,10);(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$[(17,17);(18,18);(19,19);]#[(20,20);(21,21);(22,22);]#[(23,23);(24,24);(25,25);]#[(26,26);(27,27);(28,28);(29,29);(30,30);]$%%";
		assertEquals(correct, test);
	}
	
	@Test
	public void testD3InsertionLeafRedistirbution() {
		System.out.println("\n testD3Insertion");
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = Utils.outputTree(tree);
		String correct = "@d/@%%[(a,a);(b,b);(c,c);]#[(d,d);(e,e);(f,f);(g,g);]$%%";

		assertEquals(correct, test);

		tree.delete('a');

		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);
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
	public void testSimpleSearch(){
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
		//Utils.printTree(tree);
		// Initial tree
		String test = Utils.outputTree(tree);
		String correct = "@13/17/24/30/@%%[(2,2);(3,3);(5,5);(7,7);]#[(14,14);(16,16);]#[(19,19);(20,20);(22,22);]#[(24,24);(27,27);(29,29);]#[(33,33);(34,34);(38,38);(39,39);]$%%";
		assertEquals(test, correct);
		
	}
}
