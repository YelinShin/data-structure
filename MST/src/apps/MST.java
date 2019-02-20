package apps;

import structures.*;

import java.io.IOException;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		PartialTreeList L = new PartialTreeList();
		
		for (int i = 0; i < graph.vertices.length; i++){
			PartialTree T = new PartialTree(graph.vertices[i]);
			MinHeap<PartialTree.Arc> arcs = T.getArcs();
			for (structures.Vertex.Neighbor n = graph.vertices[i].neighbors; n != null ; n = n.next){
				arcs.insert(new apps.PartialTree.Arc(graph.vertices[i],n.vertex,n.weight));
			}
			L.append(T);
		}
		return L;
		
		
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		
		ArrayList<PartialTree.Arc> finalmst = new ArrayList<PartialTree.Arc>();
		
		while(ptlist.size() > 1){
			
			PartialTree temp = ptlist.remove();
			MinHeap<PartialTree.Arc> tempHeap = temp.getArcs();
			
			boolean sameRoot = true;
			Vertex searchVert = null;
			
			while(sameRoot){
				
				PartialTree.Arc tempArc = tempHeap.deleteMin();
				
				if(!(tempArc.v1.getRoot().name.equals(tempArc.v2.getRoot().name))){
					finalmst.add(tempArc);
					searchVert = tempArc.v2;
					sameRoot = false;
				}
			
			}
			
			PartialTree resultTree = ptlist.removeTreeContaining(searchVert);
			temp.merge(resultTree);
			ptlist.append(temp);
			
		}
		
		return finalmst;
	}
	
	public static void main(String[] args) 
	throws IOException {
		Graph currGraph = new Graph ("graph1.txt");
		PartialTreeList myTreeList = initialize(currGraph);
		
		ArrayList<PartialTree.Arc> myList = execute(myTreeList);
		int len = myList.size();
		for (int i = 0; i<len; i++){
			System.out.println(myList.get(i).toString());
		}
	}
}
