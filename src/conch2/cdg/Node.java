package conch2.cdg;

import java.util.ArrayList;

public abstract class Node {
	int nodeId;
	Node parent = null;
	ArrayList<Node> children = new ArrayList<Node>();

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
	public boolean hasChild(Node kid){
		return children.contains(kid);
	}
	
	public void addChild(Node kid){
		children.add(kid);
	}
	
	public int childrenSize(){
		return children.size();
	}
	
	
	
	
}
