package src;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class PRIterator {
	/*
	 * It's used to implement PageRank Algorithm.
	 * Every time A node i's prValue is updated until the prValue converges.
	 * In my experiment, 1M entries' prValue converges after less than 100 
	 * rounds.
	 * 
	 * prValue is updated using this formula:
	 *      prValue(i) =  α * \sum(prValue(j) / len(exits(j)) + (1−α) * (1/N)
	 * 
	 * α is a damping factor, normally 0.85.
	 * exits(j) is the array of entris which j refers to.
	 * */
	
	Node[] nodes;
	double dampingFactor = 0.85;  // it's a 
	double dampingValue;
	double minDelta = 0.0001;
	int maxIteration = 1000;
	int nodesNum;
	
	PRIterator(NodesBuilder nodesBuilder){
		this.nodes = nodesBuilder.nodes;
		this.nodesNum = nodes.length;
		dampingValue = (1.0 - dampingFactor) / nodesNum;
		// dampingValue is "(1−α)/N" part in the formula
	}
	
	PRIterator(Node[] nodes){
		this.nodes = nodes;
		this.nodesNum = nodes.length;
		dampingValue = (1.0 - dampingFactor) / nodesNum;
	}
	
	public static void main(String[] args) throws IOException {
		// a small example
		Node[] nodes = new Node[5];
		nodes[0] = new Node("A", 0, 0.2);
		nodes[1] = new Node("B", 1, 0.2);
		nodes[2] = new Node("C", 2, 0.2);
		nodes[3] = new Node("D", 3, 0.2);
		nodes[4] = new Node("E", 4, 0.2);
		nodes[0].addExit(1); nodes[1].addIncident(0); // A->B
		nodes[0].addExit(2); nodes[2].addIncident(0); // A->C
		nodes[0].addExit(3); nodes[3].addIncident(0); // A->D
		nodes[1].addExit(3); nodes[3].addIncident(1); // B->D
		nodes[2].addExit(4); nodes[4].addIncident(2); // C->E
		nodes[3].addExit(4); nodes[4].addIncident(3); // D->E
		nodes[1].addExit(4); nodes[4].addIncident(1); // B->E
		nodes[4].addExit(0); nodes[0].addIncident(4); // E->A
		PRIterator prIterator = new PRIterator(nodes);
		prIterator.init();
		prIterator.iterate();
		prIterator.saveOutput("data/testOutput.txt");
	}
	
	void init() {
		System.out.println("PRIterator: starting init()");
		
		// init all nodes' prvalue, which are already done in nodesBuilder
		//for (int i = 0; i < nodesNum; i++) {
		//	nodes[i].setPrValue(initValue);
		//}
		
		// scan all nodes and if some nodes does not have exit nodes,
		// we add all nodes as its exit nodes.
		int count = 0;
		for (int i = 0; i < nodesNum; i++) {
			Node node= nodes[i];
			if (node.exits.size() == 0 && i % 100 == 0) {
				// monitor the process
				System.out.println("PRIterator: " + nodes[i].title + 
						" has no exit node , " + ++count + 
						"/" + i+  " handled.");
				for (Node nodep: nodes) {
					nodep.addIncident(node.index);
					node.addExit(nodep.index);
				}
			}
		}
	}
	
	void iterate() {
		System.out.println("PRIterator: starting iterate()");
		
		boolean flag = false;
		// begin iteration
		for (int iterate = 0; iterate < maxIteration; iterate++) {
			double delta = 0;
			for (Node node: nodes) {
				double prValue = 0;
				for (int index: node.incidents) {
					// the update formula goes here
					prValue += dampingFactor * (nodes[index].prValue) / 
							nodes[index].exits.size();				
				}
				prValue += dampingValue;
				delta += abs(node.prValue - prValue);
				node.setPrValue(prValue);	
			}
			
			if (delta < minDelta) {
				flag = true; // early stop
			}
			
			if (flag == false) {
				System.out.println("PRIterator: iteration = " + 
					iterate + " , delta = " + delta);
			}else {
				System.out.println("PRIterator: iterate() finished!");
				break;
			}
			
		}
	}
	
	void saveOutput(String path) throws IOException {
		/*
		 * Output are in this format:
		 * Title \t prValue \n
		 * 
		 * */
		
		System.out.println("PRIterator: starting saveOutput().");
		
		List<Node> list = new ArrayList<Node>();
		for (int i = 0; i < nodes.length; i++) {
			list.add(nodes[i]);
		}
		
		System.out.println("PRIterator: sorting.");
		Collections.sort(list);
		FileWriter out = new FileWriter(new File(path));
		for (Node node: list) {
			out.write(node.title + "\t" + node.prValue + "\n");
		}
		out.close();
		System.out.println("PRIterator: saveOutput() finished!");
	}
	
	double abs(double x) {
		return x > 0 ? x : (-x);
	}
}
