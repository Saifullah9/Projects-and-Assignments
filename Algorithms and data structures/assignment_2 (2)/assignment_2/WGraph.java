import java.io.*;
import java.util.*;

class Edge{
    
    public int[] nodes = new int[2]; /*The nodes connected by the edge*/
    public Integer weight; /*Integer so we can use Comparator*/
    
    Edge(int i, int j, int w){
        this.nodes[0] = i;
        this.nodes[1] = j;
        this.weight = w;
    }
}

public class WGraph{

    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private ArrayList<Integer> nodes = new ArrayList<Integer>();
    private int nb_nodes = 0;

    WGraph() {
    }

    WGraph(String file) throws RuntimeException {
        try {
            Scanner f = new Scanner(new File(file));
            int number_nodes = Integer.parseInt(f.nextLine()); /*first line is the number of nodes*/
            while (f.hasNext()){
                String[] line = f.nextLine().split("\\s+");
                /*Make sure there is 3 elements on the line*/
                if (line.length != 3){
                    continue;
                }
                int i = Integer.parseInt(line[0]);
                int j = Integer.parseInt(line[1]);
                int w = Integer.parseInt(line[2]);
                Edge e = new Edge(i, j, w);
                this.addEdge(e);
            }
            f.close();

            /*Sanity checks*/
            if (number_nodes != this.nb_nodes){
                throw new RuntimeException("There are " + this.nb_nodes + " nodes while the file specifies " + number_nodes);
            }
            for (int i = 0; i < this.nodes.size(); i++){
                if ((this.nodes.get(i) >= this.nb_nodes) || (this.nodes.get(i) < 0)){
                    throw new RuntimeException("The node " + this.nodes.get(i) + " is outside the range of admissible values, between 0 and " + this.nb_nodes + "-1");
                }
            }

        }
        catch (FileNotFoundException e){
            System.out.println("File not found!");
            System.exit(1);
        }


    }

    public void addEdge(Edge e) throws RuntimeException{
        /*Ensures that it is a new edge if both nodes already in the graph*/
        int n1 = e.nodes[0];
        int n2 = e.nodes[1];
        if (this.nodes.indexOf(n1) >= 0 && this.nodes.indexOf(n2) >= 0){
            for (int z = 0; z < this.edges.size(); z++){
                int[] n = this.edges.get(z).nodes;
                if ((n1 == n[0] && n2 == n[1]) || (n1 == n[1] && n2 == n[0])){
                    throw new RuntimeException("The edge (" + n1 + ", " + n2 + ") already exists");
                }
            }
        }

        /*Update nb_nodes if necessary*/
        if (this.nodes.indexOf(n1) == -1){
            this.nodes.add(n1);
            this.nb_nodes += 1;
        }
        if (this.nodes.indexOf(n2) == -1){
            this.nodes.add(n2);
            this.nb_nodes += 1;
        }

        this.edges.add(e);
    }

    public ArrayList<Edge> listOfEdgesSorted(){
        ArrayList<Edge> edges = new ArrayList<Edge>(this.edges);
        Collections.sort(edges, new Comparator<Edge>() {
            public int compare(Edge  e1, Edge  e2) 
            {   
                return  e1.weight.compareTo(e2.weight);
            }   
        }); 
        return edges;
    }

    public int getNbNodes(){
        return this.nb_nodes;
    }

    public String toString(){
        String out = Integer.toString(this.nb_nodes);
        for (int i = 0; i < this.edges.size(); i++){
            Edge e = edges.get(i);
            out += "\n" + e.nodes[0] + " " + e.nodes[1] + " " + e.weight;
        }
        return out;
    }
}
