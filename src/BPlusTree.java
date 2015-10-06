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
	 * Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {

        LeafNode<K, T> leaf = searchLeafNode(root, key);
        return (T)leaf.searchValueByKey(key);
	}

	/**
	 * Insert a key/value pair into the BPlusTree
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
        leaf.values.subList(D, leaf.values.size()).clear();

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
	 * Delete a key/value pair from this B+Tree
	 * 
	 * @param key key of entry to be deleted
	 */
	public void delete(K key) {
		
		int splitIndex = -1;
		if (root != null) {
			splitIndex = deleteHelper(key, root, null, splitIndex);
		}
		
		if (splitIndex != -1) {
			root.keys.remove(splitIndex);
			if (root.keys.size() == 0) {
				root = ((IndexNode<K, T>) root).children.get(0);
			}
		}
		
		if (root.keys.size() == 0){
			root = null;
		}
	}
	
	/**
	 * Helper method for delete function, recursively traverses through the tree and calls functions to handle the underflows
	 * @param key key of entry to be deleted
	 * @param child the current node being traversed
	 * @param parent the parent of the current node being traversed
	 * @param splitIndex the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 * @return splitIndex as explained above
	 */
	private int deleteHelper (K key, Node<K, T> child, IndexNode<K, T> parent, int splitIndex) {
		
		// Add the parent information into the node
		if (parent != null) {
			child.setParent(parent);
			child.setIndexInParent();
		}
		
		// If node is a leaf, delete the key value pair from it
		if (child.isLeafNode) {
			LeafNode<K, T> node = (LeafNode<K, T>) child;
			ListIterator<K> iterator = node.keys.listIterator();
            while (iterator.hasNext()) {
                if (iterator.next().compareTo(key) == 0) {
                	node.keys.remove(key);
                	node.values.remove(iterator.previousIndex());
                	break;
                }
            }
            
            // Handle leaf node underflow
            if (node.isUnderflowed() && node != root) {
            	return handleLeafUnderflowHelper(node);
            } 
		}
		
		// In case current node is index node
		else {
			IndexNode<K, T> node = (IndexNode<K, T>) child;
			
			// If key is smaller than the first key in the index node, traverse left child
			if (key.compareTo(node.keys.get(0)) < 0) {
				splitIndex = deleteHelper(key, node.children.get(0), node, splitIndex);
			// If key is bigger than the last key in the index node, traverse the right child
			} else if (key.compareTo(node.keys.get(node.keys.size() - 1)) >= 0) {
				splitIndex = deleteHelper(key, node.children.get(node.children.size() - 1), node, splitIndex);
			} else {
				ListIterator<K> iterator = node.keys.listIterator();
	            while (iterator.hasNext()) {
	                if (iterator.next().compareTo(key) > 0) {
	                	splitIndex = deleteHelper(key, node.children.get(iterator.previousIndex()), node, splitIndex);
	                	break;
	                }
	            }
			}
		}

		// Split key deletion
		if (splitIndex != -1 && child != root) {
			splitIndex = handleSplitKeyDeletion(splitIndex, child);
		}
		
		return splitIndex;
	}
	
	/**
	 * Checks if node has a left sibling, if not, checks for right sibling. Calls handleLeafNodeUnderflow with the appropriate sibling.
	 * @param node
	 * @return split key index
	 */
	private int handleLeafUnderflowHelper(LeafNode<K, T> node) {
		// Has left sibling
    	if (node.getIndexInParent() >= 1) {
    		LeafNode<K, T> leftSibling = (LeafNode<K, T>) node.getParent().children.get(node.getIndexInParent() - 1);
    		return handleLeafNodeUnderflow(leftSibling, node, node.getParent());
    	} else {
    		// Does not have left sibling, so try right sibling
    		LeafNode<K, T> rightSibling = (LeafNode<K, T>) node.getParent().children.get(node.getIndexInParent() + 1);
    		return handleLeafNodeUnderflow(node, rightSibling, node.getParent());
    	}
	}

	/**
	 * Deletes the entry at the input index from input node
	 * @param splitIndex split index
	 * @param node
	 * @return split key index
	 */
	private int handleSplitKeyDeletion(int splitIndex, Node<K, T> node) {
		
		node.keys.remove(splitIndex);
		
		// Check node underflowed, call handle index underflow
		if (node.isUnderflowed()) {
			if (node.getIndexInParent() >= 1) {
				IndexNode<K, T> leftSibling = (IndexNode<K, T>) node.getParent().children.get(node.getIndexInParent() - 1);
				return handleIndexNodeUnderflow(leftSibling, (IndexNode<K, T>)node, node.getParent());
			} else {
				IndexNode<K, T> rightSibling = (IndexNode<K, T>) node.getParent().children.get(node.getIndexInParent() + 1);
				return handleIndexNodeUnderflow((IndexNode<K, T>)node, rightSibling, node.getParent());
			}
		}
		return -1;
	}
	
	/**
	 * Handle LeafNode Underflow (merge or redistribution)
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
		
		// If redistribute is possible 
		if ((left.keys.size() + right.keys.size()) >= (2*D)) {
			
			int childIndex = parent.children.indexOf(right);
			
			// Store all keys and all values from left and right nodes
			List<K> allKeys = new ArrayList<K>();
			allKeys.addAll(left.keys);
			allKeys.addAll(right.keys);
			List<T> allValues = new ArrayList<T>();
			allValues.addAll(left.values);
			allValues.addAll(right.values);
			
			// New size of left would be half of the total keys in left and right
			int newLeftSize = (left.keys.size() + right.keys.size())/2;
			
			// Clear all keys and values from left and right nodes
			left.keys.clear();
			right.keys.clear();
			left.values.clear();
			right.values.clear();
			
			// Add first half keys and values into left and rest into right
			left.keys.addAll(allKeys.subList(0, newLeftSize));
			left.values.addAll(allValues.subList(0, newLeftSize));
			right.keys.addAll(allKeys.subList(newLeftSize, allKeys.size()));
			right.values.addAll(allValues.subList(newLeftSize, allValues.size()));
			
			parent.keys.set(childIndex - 1, parent.children.get(childIndex).keys.get(0));
			return -1;
		} 
		// If redistribute not possible, merge
		else {
			left.keys.addAll(right.keys);
			left.values.addAll(right.values);
			
			left.nextLeaf = right.nextLeaf;
			
			if (right.nextLeaf != null) {
				right.nextLeaf.previousLeaf = left;
			}
			
			int index = parent.children.indexOf(right) - 1;
			parent.children.remove(right);
			
			return index;
		}
	}

	/**
	 * Handle IndexNode Underflow (merge or redistribution)
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
		
		int splittingIndex = 0;
		
		// Find the splitting index
		for (int i = 0; i < parent.keys.size(); i++) {
			if (parent.children.get(i) == leftIndex && parent.children.get(i+1) == rightIndex) {
				splittingIndex = i;
			}
		}
		
		// Redistribute if possible
		if ((leftIndex.keys.size() + rightIndex.keys.size()) >= (2*D)) {
			
			// All keys including the parent key
			List<K> allKeys = new ArrayList<K>();
			allKeys.addAll(leftIndex.keys);
			allKeys.add(parent.keys.get(splittingIndex));
			allKeys.addAll(rightIndex.keys);
			
			// All children from left and right
			List<Node<K, T>> allChildren = new ArrayList<Node<K,T>>();
			allChildren.addAll(leftIndex.children);
			allChildren.addAll(rightIndex.children);
			
			// Get the index of allKeys that will be the new parent key. It would be the middle key in case of odd 
			// total keys. And one left of the middle for even number of keys.
			int newParentIndex = 0;
			if (allKeys.size() % 2 == 0) {
				newParentIndex = (allKeys.size()/2) - 1;
			} else {
				newParentIndex = allKeys.size()/2;
			}
			
			// Add the new parent key to the splitting index.
			// Add all keys left of the new parent key into the leftIndex
			// Add all keys right of the new parent key into the rightIndex
			leftIndex.keys.clear();
			leftIndex.keys.addAll(allKeys.subList(0, newParentIndex));
			parent.keys.set(splittingIndex, allKeys.get(newParentIndex));
			rightIndex.keys.clear();
			rightIndex.keys.addAll(allKeys.subList(newParentIndex + 1, allKeys.size()));
			
			// Add all the (n+1) children from 0 to n+1 to the left node
			// Add the rest of the children to the right index node
			leftIndex.children.clear();
			leftIndex.children.addAll(allChildren.subList(0, newParentIndex + 1));
			rightIndex.children.clear();
			rightIndex.children.addAll(allChildren.subList(newParentIndex + 1, allChildren.size()));
	
			return -1;
			
		} else {
			// Merge logic
			leftIndex.keys.add(parent.keys.get(splittingIndex));
			leftIndex.keys.addAll(rightIndex.keys);
			leftIndex.children.addAll(rightIndex.children);
			
			parent.children.remove(parent.children.indexOf(rightIndex));
			return splittingIndex;
		}		
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