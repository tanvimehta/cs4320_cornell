import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

/**
 * BPlusTree Class
 * Assumptions: 1. No duplicate keys inserted
 * 2. Order D: D<=number of keys in a node <=2*D
 * 3. All keys are non-negative
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {

        LeafNode leaf = searchLeafNode(root, key);
        return (T)leaf.searchValueByKey(key);
	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {

        // If BPlusTree is empty
        if (root == null) {
            root = new LeafNode<K, T>(key, value);
            return;
        }
	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<splittingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {

        // Keys after first D keys in new right node
        List<K> rightKeys = new ArrayList<K>();
        rightKeys.addAll(leaf.keys.subList(D, leaf.keys.size()));

        // Values after first D values for new right node
        List<T> rightValues = new ArrayList<T>();
        rightValues.addAll(leaf.values.subList(D, leaf.values.size()));

        // Create new right node
        LeafNode right = new LeafNode(rightKeys, rightValues);

        // Remove all keys after D keys
        // Found this here: http://stackoverflow.com/questions/10797663/removing-tail-of-x-elements-from-a-list
        leaf.keys.subList(D, leaf.keys.size()).clear();
        leaf.values.subList(D, leaf.keys.size()).clear();

        if (leaf.nextLeaf != null) {
            right.nextLeaf = leaf.nextLeaf;
        }

        leaf.nextLeaf = right;

        // Splitting key is the first key in new right node
        K splittingKey = (K)right.keys.get(0);

        // Found this here: http://stackoverflow.com/questions/3110547/java-how-to-create-new-entry-key-value
		return new AbstractMap.SimpleEntry<K, Node<K,T>>(splittingKey, right);
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<splittingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {

        // D+1th to last key in new right node
        List<K> rightKeys = new ArrayList<K>();
        rightKeys.addAll(index.keys.subList(D + 1, index.keys.size()));

        // D+1th to last children in new right node
        List<Node> rightChildren = new ArrayList<Node>();
        rightChildren.addAll(index.children.subList(D, index.children.size()));

        IndexNode right = new IndexNode(rightKeys, rightChildren);

        // Splitting key is the Dth key in new left node
        // Do this before removing the trailing keys
        K splittingKey = (K)index.keys.get(D);

        // Remove all keys and children after Dth entry
        index.keys.subList(D, index.keys.size()).clear();
        index.children.subList(D+1, index.children.size()).clear();

		return new AbstractMap.SimpleEntry<K, Node<K,T>>(splittingKey, right);
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param leftIndex
	 *            : the smaller node
	 * @param rightIndex
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}

    /**
     * Helper method to (recursively)find the appropriate leaf node given a key
     * @param root root node of the tree(or subtree)
     * @param key key of the node to be found
     * @return the leafNode to be searched
     */
    public LeafNode searchLeafNode(Node root, K key) {

        if (root == null) {
            return null;
        } else if (root.isLeafNode) {
            return (LeafNode)root;
        } else {
            // If node is an index node
            IndexNode<K, T> indexNode = (IndexNode) root;

            // If key > last key in the node then traverse the rightmost child
            if (key.compareTo(indexNode.keys.get(indexNode.keys.size() - 1)) > 1) {
                return searchLeafNode(indexNode.children.get(indexNode.children.size()-1), key);
            // If key < first key in the node, then traverse the leftmost child
            } else if (key.compareTo(indexNode.keys.get(0)) < 1) {
                return searchLeafNode(indexNode.children.get(0), key);
            } else {
                // Traverse through the node to find the leafNode
                ListIterator<K> iterator = indexNode.keys.listIterator();
                while (iterator.hasNext()) {
                    if (iterator.next().compareTo(key) > 0) {
                        return searchLeafNode(indexNode.children.get(iterator.previousIndex()), key);
                    }
                }
            }

        }
        return null;
    }
}
