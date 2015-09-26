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

        LeafNode<K, T> leaf = searchLeafNode(root, key);
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
        }

        // Handle index overflow
        Entry<K, Node<K,T>> indexOverflow = insertHelper (root, key, value);
        if (indexOverflow != null) {
            root = new IndexNode<K, T>(indexOverflow.getKey(), root, indexOverflow.getValue());
        }
	}

	/**
	 * Helper method for insert, recursively looks for the right position to insert key 
	 * Calls handleLeafOverflow
	 * @param root 
	 * @param key
	 * @param value
	 * @return
	 */
    public Entry<K, Node<K,T>> insertHelper (Node<K, T> root, K key, T value) {

        Entry<K, Node<K,T>> overflow = null;
        if (root.isLeafNode) {
            ((LeafNode<K, T>) root).insertSorted(key, value);
            if (((LeafNode<K, T>) root).isOverflowed()) {
                return splitLeafNode((LeafNode<K, T>) root);
            }
        } else {
            IndexNode<K, T> indexNode = (IndexNode<K, T>) root;
            if (key.compareTo(indexNode.keys.get(0)) < 0) {
                overflow = insertHelper(indexNode.children.get(0), key, value);
            } else if (key.compareTo(indexNode.keys.get(indexNode.keys.size() - 1)) >= 0) {
                overflow = insertHelper(indexNode.children.get(indexNode.children.size() - 1), key, value);
            } else {

                ListIterator<K> iterator = indexNode.keys.listIterator();
                while (iterator.hasNext()) {
                    if (iterator.next().compareTo(key) > 0) {
                        overflow = insertHelper(indexNode.children.get(iterator.previousIndex()), key, value);
                        break;
                    }
                }
            }
        }

        return handleLeafOverflow(root, overflow);
    }

    /**
     * In case of a leaf overflow, finds the right position to insert the new split node.
     * Checks for index overflow, in case of an index overflow, returns the split index node
     * @param root
     * @param overflow
     * @return
     */
    public Entry<K, Node<K,T>> handleLeafOverflow (Node<K, T> root, Entry<K, Node<K,T>> overflow) {

        if (overflow != null && root instanceof IndexNode) {

            IndexNode<K, T> indexNode = (IndexNode<K, T>) root;
            if (overflow.getKey().compareTo(indexNode.keys.get(0)) < 0) {
                indexNode.insertSorted(overflow, 0);
            } else if (overflow.getKey().compareTo(indexNode.keys.get(indexNode.keys.size() - 1)) > 0) {
                indexNode.insertSorted(overflow, indexNode.keys.size());
            } else {
                ListIterator<K> iterator = indexNode.keys.listIterator();
                while (iterator.hasNext()) {
                    if (iterator.next().compareTo(overflow.getKey()) > 0) {
                        indexNode.insertSorted(overflow, iterator.previousIndex());
                        break;
                    }
                }
            }

            // In case of index overflow
            if (indexNode.isOverflowed()) {
                return splitIndexNode(indexNode);
            } else {
                return null;
            }
        }

        return overflow;
    }
    
	/**
	 * Splits a leaf node and returns the new right node and the splitting
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
        LeafNode<K, T> right = new LeafNode<K, T>(rightKeys, rightValues);

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
	 * Splits an indexNode and return the new right node and the splitting
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
        List<Node<K, T>> rightChildren = new ArrayList<Node<K, T>>();
        rightChildren.addAll(index.children.subList(D + 1, index.children.size()));

        IndexNode<K, T> right = new IndexNode<K, T>(rightKeys, rightChildren);

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
    public LeafNode<K, T> searchLeafNode(Node<K, T> root, K key) {

        if (root == null) {
            return null;
        } else if (root.isLeafNode) {
            return (LeafNode<K, T>)root;
        } else {
            // If node is an index node
            IndexNode<K, T> indexNode = (IndexNode<K, T>) root;

            // If key > last key in the node then traverse the rightmost child
            if (key.compareTo(indexNode.keys.get(indexNode.keys.size() - 1)) >= 0) {
                return searchLeafNode(indexNode.children.get(indexNode.children.size()-1), key);
            // If key < first key in the node, then traverse the leftmost child
            } else if (key.compareTo(indexNode.keys.get(0)) < 0) {
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
