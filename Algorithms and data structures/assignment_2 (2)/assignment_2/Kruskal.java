import java.util.*;

public class Kruskal{

    public static WGraph kruskal(WGraph g){

        /* Fill this method (The statement return null is here only to compile) */
        WGraph x = new WGraph();
        DisjointSets s = new DisjointSets(g.getNbNodes());
    	ArrayList<Edge> gs = g.listOfEdgesSorted();
    	
    	for(Edge e: gs) {
    		if(IsSafe(s,e)) {
    			x.addEdge(e);
    		}
    		else {
    			continue;
    		}
    	}
        return x;
    }

    public static Boolean IsSafe(DisjointSets p, Edge e){

        /* Fill this method (The statement return 0 is here only to compile) */
       int x = e.nodes[0];
       int y = e.nodes[1];
       int rX = p.find(x);
       int rY = p.find(y);
       
       if(rX == rY) {
    	   return false;
       } 
       
       	p.union(x, y);
    	return true;
    
    }

    public static void main(String[] args){

        String file = args[0];
        WGraph g = new WGraph(file);
        WGraph t = kruskal(g);
        System.out.println(t);

   } 
}
