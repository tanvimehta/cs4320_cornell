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
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
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
//		if (root.isLeafNode && root.keys.size()==1 && (key.compareTo(root.keys.get(0))==0)) {
//			root = null;
//			return;
//		}
//		
//		// Handle case where key is in root
//		int indexToDelete = deleteHelper(key, root, null);
	}
	
	private IndexNode<K, T> searchIndexNode (Node<K, T> root, K key) {
		if (root == null || root.isLeafNode) {
			return null;
		}
		
		if (key.compareTo(root.keys.get(0)) < 0) {
			return searchIndexNode(((IndexNode<K, T>)root).children.get(0), key);
		} else if (key.compareTo(root.keys.get(root.keys.size() - 1)) > 0) {
			return searchIndexNode(((IndexNode<K, T>)root).children.get(((IndexNode<K, T>)root).children.size() - 1), key);
		} else {
            ListIterator<K> iterator = root.keys.listIterator();
            while (iterator.hasNext()) {
                if (iterator.next().compareTo(key) == 0) {
                    return (IndexNode<K, T>)root;
                }
            }
		}
		
		return null;
	}
	
	private int deleteHelper (K key, Node<K, T> child, IndexNode<K, T> parent, int splitIndex) {
		
		if (parent != null) {
			child.setParent(parent);
			child.setIndexInParent();
		}
		
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
            
            if (node.isUnderflowed() && node != root) {
            	return handleLeafUnderflowHelper(node);
            } 
//            else {
//            	//TODO: FILL THIS WHEN YOU UNDERSTAND WHY YOU NEED THIS
//            	if (node.keys.size() > 0) {
//            		IndexNode<K, T> indexNode = searchIndexNode(root, key);
//            		if (indexNode != null) {
//            			ListIterator<K> indexIterator = indexNode.keys.listIterator();
//                        while (indexIterator.hasNext()) {
//                        	K compareKey = indexIterator.next();
//                            if (compareKey.compareTo(key) == 0) {
//                            		indexNode.keys.set(indexIterator.previousIndex(), node.keys.get(0));
//                            		break;
//                            }
//                            
//                            if (compareKey.compareTo(key) > 0) {
//                            	break;
//                            }
//                        }
//            		}
//            	}
//            }
		} 
		// Index node case
		else {
			IndexNode<K, T> node = (IndexNode<K, T>) child;
			if (key.compareTo(node.keys.get(0)) < 0) {
				splitIndex = deleteHelper(key, node.children.get(0), node, splitIndex);
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

		if (splitIndex != -1 && child != root) {
			splitIndex = handleSplitKeyDeletion(splitIndex, child);
		}
		
		return splitIndex;
	}
	
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

	private int handleSplitKeyDeletion(int splitIndex, Node<K, T> node) {
		
		node.keys.remove(splitIndex);
		
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
		
		// If redistribute is possible 
		if ((left.keys.size() + right.keys.size()) >= (2*D)) {
			int childIndex = parent.children.indexOf(right);
			// Either node could be underflow
			if (left.isUnderflowed()) {
				left.insertSorted(right.keys.remove(0), right.values.remove(0));
			} else {
				right.insertSorted(left.keys.remove(left.keys.size()-1), left.values.remove(left.values.size()-1));
			}
			parent.keys.set(childIndex - 1, parent.children.get(childIndex).keys.get(0));
			return -1;
		} 
		// If redistribute not possible, merge
		else {
			left.keys.addAll(right.keys);
			left.values.addAll(right.values);
			left.nextLeaf = right.nextLeaf;
			
			int index = parent.children.indexOf(right) - 1;
			parent.children.remove(right);
			
			return index;
		}
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
		
		int splittingIndex = 0;
		// Find the splitting index
		for (int i = 0; i < parent.keys.size(); i++) {
			if (parent.children.get(i) == leftIndex && parent.children.get(i+1) == rightIndex) {
				splittingIndex = i;
			}
		}
		
		// Redistribute if possible
		if ((leftIndex.keys.size() + rightIndex.keys.size()) >= (2*D)) {
			if (leftIndex.isUnderflowed()) {
				leftIndex.keys.add(parent.keys.get(splittingIndex));
				parent.keys.set(splittingIndex, rightIndex.keys.remove(0));
				leftIndex.children.add(rightIndex.children.remove(0));
			} else {
				rightIndex.keys.add(0, parent.keys.get(splittingIndex));
				rightIndex.children.add(0, leftIndex.children.remove(leftIndex.children.size() - 1));
				parent.keys.set(parent.keys.size()-1, leftIndex.keys.remove(leftIndex.keys.size() - 1));
			}	
			return -1;
			
		} else {
			// MERGE LOGIC
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
