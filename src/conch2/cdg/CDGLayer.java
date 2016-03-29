package conch2.cdg;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import conch2.cdg.Operator.OpType;

public class CDGLayer {
	private Component root;
	private Statement statement;
	private Connection connection;

	/**
	 * retrieve the whole CDG from Conch.
	 * 
	 * @param compName
	 * @param statement
	 */
	public CDGLayer(String compName, Connection conn) {
		this.connection = conn;
		try{
			this.statement = conn.createStatement();
		} catch (SQLException e) {
			System.err.println("Fail creating statement in CDGLayer.");
			e.printStackTrace();
			System.exit(1);
		}
		root = new Component();
		root.setName(compName);
		root.setNodeId(this.getNodeId(compName));

		this.expandLayer(root);

	}

	/**
	 * Given a list of component IDs, merge them with existing dependencies.
	 * 
	 * @param depts
	 */

	public void addInstance(ArrayList<String> depts) {
		Operator andNode = this.createOperator(OpType.AND);
		for (int i = 0; i < depts.size(); i++) {
			int nodeId = this.getNodeId(depts.get(i));
			Component comp = (Component) this.getNode(nodeId);
			andNode.addChild(comp);
			this.addEdge(andNode, comp);
		}
		if (this.root.childrenSize() == 0){
			this.setRootKid(andNode);
			this.addEdge(root, andNode);
			
		}
		
		else {
			Operator orNode = this.createOperator(OpType.XOR);
			orNode.addChild(this.getRootKid());
			this.addEdge(orNode, this.getRootKid());
			orNode.addChild(andNode);
			this.addEdge(orNode, andNode);
			
			this.deleteEdge(root, this.getRootKid());
			this.setRootKid(orNode);
			this.addEdge(root, orNode);
			this.merge();
		}
	}

	@Override
	public String toString() {

		return this.getRootKid().toString();
	}

	private void merge() {

	}

	private void setRootKid(Operator op) {
		this.root.children.clear();
		this.root.children.add(op);
	}

	private Operator getRootKid() {
		Node node = (this.root.children.get(0));
		return (Operator) node;
	}

	/* -----------------------------DB helpers ----------------------------- */

	private int getNodeId(String compName) {
		int id = this.getCompId(compName);
		String idString = String.valueOf(id);
		if (!this.compDDExists(compName)) {
			String query = "INSERT INTO Nodes VALUES(null, 'COMP', " + idString
					+ ", null);";
			try {
				statement.execute(query);
			} catch (SQLException e) {
				System.err.println("SQLException in getNodeId()");
				e.printStackTrace();
				System.exit(1);
			}
		}

		String query = "SELECT NodeId FROM Nodes WHERE ComponentId=" + idString
				+ ";";
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();
			if (results.next() == false) {
				System.err.println("Given component id does not exist, weired");
				System.exit(1);
			}
			return results.getInt(1);
		} catch (SQLException e) {
			System.err.println("SQLException in getNodeId()");
			e.printStackTrace();
			System.exit(1);
			return 0;
		}
	}

	/**
	 * Check whether the component exists as a node of an existing CDG
	 * 
	 * @param name
	 *            component name
	 * @return true/false
	 */
	private boolean compDDExists(String name) {
		String query = "SELECT * FROM Nodes WHERE ComponentId = (SELECT ComponentId FROM ComponentMeta WHERE ComponentName='"
				+ name + "');";
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();

			return results.next();
		} catch (SQLException e) {
			System.err.println("SQLException in compDDExists()");
			e.printStackTrace();
			System.exit(1);
			return true;
		}
	}

	private int getCompId(String name) {
		String query = "SELECT ComponentId FROM ComponentMeta WHERE ComponentName='"
				+ name + "';";
		int id = 0;
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();
			if (results.next() == false) {
				System.err.println("Given component name does not exist");
				return -1;
			}
			id = results.getInt(1);
		} catch (Exception e) {
			System.err.println("SQLException in getCompId");
			e.printStackTrace();
			System.exit(1);
		}

		return id;
	}

	/**
	 * From the given node, query all its children in Conch and expand its
	 * children list, until getting to another component.
	 * 
	 * @param node
	 *            a node with nodeId specified.
	 */
	private void expandLayer(Node node) {
		String pnodeIdString = String.valueOf(node.getNodeId());
		String query = "SELECT CNodeId FROM Edges WHERE PNodeId="
				+ pnodeIdString + ";";
		try {
			Statement stat = this.connection.createStatement();
			stat.execute(query);
			ResultSet results = stat.getResultSet();
			while (results.next() != false) {
				int kidId = results.getInt(1);
				Node kid = this.getNode(kidId);
				node.addChild(kid);
				if (kid instanceof Operator)
					this.expandLayer(kid);
			}

		} catch (SQLException e) {
			System.err.println("SQLException in expandLayer()");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Given nodeId, return a Component or Operator object.
	 * 
	 * @param nodeId
	 *            node Id of a component or object
	 * @return Node object, either Component or Operator
	 */
	private Node getNode(int nodeId) {
		String idString = String.valueOf(nodeId);
		String query = "SELECT NodeType, ComponentId, OperatorType FROM Nodes WHERE NodeId="
				+ idString + ";";

		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();
			if (results.next() == false) {
				System.err.println("Given nodeId does not exist");
				return null;
			}
			String type = results.getString(1);
			int compId = results.getShort(2);
			String opType = results.getString(3);

			if (type.compareTo("COMP") == 0) {
				Component tmpComp = new Component();
				tmpComp.setNodeId(nodeId);
				tmpComp.setName(this.getCompName(compId));
				return tmpComp;
			} else {
				Operator tmpOp = new Operator();
				tmpOp.setNodeId(nodeId);
				if (opType.compareTo("AND") == 0) {
					tmpOp.setType(OpType.AND);
				} else {
					tmpOp.setType(OpType.XOR);
				}
				return tmpOp;
			}

		} catch (SQLException e) {
			System.err.println("SQLException in getNode");
			e.printStackTrace();
			System.exit(1);
			return null;
		}

	}

	/**
	 * Given component id, retrieve component name
	 * 
	 * @param compId
	 * @return
	 */
	public String getCompName(int compId) {
		String idString = String.valueOf(compId);
		String query = "SELECT ComponentName FROM ComponentMeta WHERE ComponentId="
				+ idString + ";";
		try {
			statement.execute(query);
			ResultSet results = statement.getResultSet();
			if (results.next() == false) {
				System.err.println("Given component id does not exist");
				return null;
			}
			return results.getString(1);
		} catch (SQLException e) {
			System.err.println("SQLException in getCompName()");
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	private Operator createOperator(OpType type) {
		Operator op = new Operator();
		op.setType(type);
		String opType;
		if (type == OpType.XOR)
			opType = "XOR";
		else
			opType = "AND";

		try {
			String query = "INSERT INTO Nodes VALUES(null, 'OP', null, '"
					+ opType + "');";
			statement.execute(query);
			query = "SELECT LAST_INSERT_ID() FROM Nodes";
			statement.execute(query);
			ResultSet results = statement.getResultSet();
			if (results.next() == false) {
				System.err.println("fail creating a new Operator node.");
				return null;
			}
			int id = results.getInt(1);
			op.setNodeId(id);
			return op;
		} catch (SQLException e) {
			System.err.println("SQLException in createOperator()");
			e.printStackTrace();
			System.exit(1);
			return null;
		}

	}
	
	private void addEdge(Node parent, Node child){
		int pid = parent.getNodeId();
		int cid = child.getNodeId();
		String pidString = String.valueOf(pid);
		String cidString = String.valueOf(cid);
		String query = "INSERT INTO Edges VALUES(" + pidString + ", " + cidString + ", null);";
		try {
			statement.execute(query);
			
		} catch (SQLException e) {
			System.err.println("SQLException in addEdge()");
			e.printStackTrace();
			System.exit(1);
			
		}
	}
	
	private void deleteEdge(Node parent, Node child){
		int pid = parent.getNodeId();
		int cid = child.getNodeId();
		String pidString = String.valueOf(pid);
		String cidString = String.valueOf(cid);
		String query = "DELETE FROM Edges WHERE PNodeid=" + pidString + " AND CNodeId=" + cidString + ";";
		try {
			statement.execute(query);
			
		} catch (SQLException e) {
			System.err.println("SQLException in deleteEdge()");
			e.printStackTrace();
			System.exit(1);
			
		}
	}

}
