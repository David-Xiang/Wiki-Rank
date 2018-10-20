package src;
import java.util.ArrayList;

class Node implements Comparable<Node>{
	/*
	 * A Node represents an entry which is a basic unit used in PageRank 
	 * Algorithm.
	 * 
	 * */
	
	String title;
	int index;
	double prValue;
	ArrayList<Integer> incidents, exits;
	
	Node(String title, int index, double prValue){
		this.title = title;
		this.index = index;
		this.incidents = new ArrayList<Integer>();
		this.exits = new ArrayList<Integer>();
		this.prValue = prValue;
	}
	
	void addIncident(int index) {
		incidents.add(index);
	}
	void addExit(int index) {
		exits.add(index);
	}
	
	void setPrValue(double prValue) {
		this.prValue = prValue;
	}
	public int compareTo(Node n) {
		/*
		 * Nodes will be sorted from ones with largest prValue to ones with
		 * smallest prValue.
		 * */
		
		if (this.prValue < n.prValue)
			return 1;
		else
			return -1;
	}
}