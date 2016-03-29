package conch2.cdg;

public class Operator extends Node{
	public enum OpType{AND, XOR};
	public OpType type;
	public OpType getType() {
		return type;
	}
	public void setType(OpType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		String result = "[";
		if (this.type == OpType.AND)
			result += "* ";
		else
			result += "+ ";
		
		for (int i=0; i<this.childrenSize(); i++){
			Node kid = this.children.get(i);
			result += kid.toString();
			result += " ";
		}
		result += "]";
		return result;
	}
}
