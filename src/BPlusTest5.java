import static org.junit.Assert.*;

import org.junit.Test;

public class BPlusTest5 {

	@Test
	public void test() {
        Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,
                13, 14, 15, 16,17,18,19,20,21,22,1 };
        String primeNumberStrings[] = new String[primeNumbers.length];
        for (int i = 0; i < primeNumbers.length; i++) {
            primeNumberStrings[i] = (primeNumbers[i]).toString();
        }
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
        Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);
        Utils.printTree(tree);
        String correct = "@10/16/@%%@5/8/@@12/14/@@18/20/@%%[(1,1);(2,2);(4,4);]#[(5,5);(7,7);]#[(8,8);(9,9);]$[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);]$[(16,16);(17,17);]#[(18,18);(19,19);]#[(20,20);(21,21);(22,22);]$%%";
       // System.out.println("correct \n" +correct);
        //System.out.println( tree.search(22));
        tree.delete(2);
        assertEquals("@7/10/13/16/19/@%%[(1,1);(4,4);(5,5);]#[(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(1);
        //Utils.printTree(tree);
        assertEquals("@10/13/16/19/@%%[(4,4);(5,5);(7,7);(8,8);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(8);
       // Utils.printTree(tree);
        assertEquals("@10/13/16/19/@%%[(4,4);(5,5);(7,7);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(19,19);(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(19);
        //Utils.printTree(tree);
        assertEquals("@10/13/16/19/@%%[(4,4);(5,5);(7,7);(9,9);]#[(10,10);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(10);
        //Utils.printTree(tree);//ok
        System.out.println("Answer=");
        System.out.println(Utils.outputTree(tree));
        assertEquals("@9/13/16/19/@%%[(4,4);(5,5);(7,7);]#[(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
        tree.delete(4);
        
        assertEquals("@13/16/19/@%%[(5,5);(7,7);(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(18,18);]#[(20,20);(21,21);(22,22);]$%%",Utils.outputTree(tree));
     
        tree.delete(22);
        tree.delete(18);
        assertEquals("@13/16/@%%[(5,5);(7,7);(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(20,20);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(7);
        assertEquals("@13/16/@%%[(5,5);(9,9);(11,11);(12,12);]#[(13,13);(14,14);(15,15);]#[(16,16);(17,17);(20,20);(21,21);]$%%",Utils.outputTree(tree) );

        tree.delete(14);
        tree.delete(11);
        assertEquals("@16/@%%[(5,5);(9,9);(12,12);(13,13);(15,15);]#[(16,16);(17,17);(20,20);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(5);
        assertEquals("@16/@%%[(9,9);(12,12);(13,13);(15,15);]#[(16,16);(17,17);(20,20);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(15);
        tree.delete(20);
        assertEquals("@16/@%%[(9,9);(12,12);(13,13);]#[(16,16);(17,17);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(16);
        tree.delete(17);
        assertEquals("[(9,9);(12,12);(13,13);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(13);
        assertEquals("[(9,9);(12,12);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(9);
        assertEquals("[(12,12);(21,21);]$%%", Utils.outputTree(tree));

        tree.delete(12);
        tree.delete(21);
        assertEquals(null, tree.root);
	}

}
