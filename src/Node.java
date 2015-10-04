import java.util.ArrayList;

public class Node<K extends Comparable<K>, T> {
	protected boolean isLeafNode;
	protected ArrayList<K> keys;
	protected IndexNode<K, T> parent;
	protected int indexInParent;

	public boolean isOverflowed() {
		return keys.size() > 2 * BPlusTree.D;
	}

	public boolean isUnderflowed() {
		return keys.size() < BPlusTree.D;
	}
	
	public void setParent(IndexNode<K, T> parent) {
		this.parent = parent;
	}
	
	public void setIndexInParent() {
		for (int index = 0; index < parent.children.size(); index++) {
			if (parent.children.get(index).equals(this)) {
				this.indexInParent = index;
				break;
			}
		}
	}
	
	public IndexNode<K, T> getParent() {
		return this.parent;
	}
	
	public int getIndexInParent() {
		return this.indexInParent;
	}
}
