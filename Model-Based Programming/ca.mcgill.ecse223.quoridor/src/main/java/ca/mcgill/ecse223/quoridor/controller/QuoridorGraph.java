package ca.mcgill.ecse223.quoridor.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import ca.mcgill.ecse223.quoridor.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.model.Board;
import ca.mcgill.ecse223.quoridor.model.Direction;
import ca.mcgill.ecse223.quoridor.model.GamePosition;
import ca.mcgill.ecse223.quoridor.model.PlayerPosition;
import ca.mcgill.ecse223.quoridor.model.Tile;
import ca.mcgill.ecse223.quoridor.model.Wall;

public class QuoridorGraph {
	HashMap<Tile, Vertex> vertexList;
	GamePosition gamePosition;
	
	public QuoridorGraph (GamePosition pos) {
		this.gamePosition = pos;
		this.vertexList = new HashMap<Tile, Vertex>();
		
		buildPosition(); // Make position
	}
	
	
	class Vertex {
		private int row;
	    private int col;
		private ArrayList<Tile> links;
		private boolean visited;
		
		/*
		 *  Creates a vertex given a tile coordinate.
		 *  This vertex has no edges yet. 
		 */
		Vertex (int row, int col) {
			this.row = row;
			this.col = col;
			this.links = new ArrayList<Tile>();
			this.visited = false;
		}
		
		boolean addEdge(Tile key) {
			if (!this.links.contains(key)) {
				this.links.add(key);
				return true;
			}
			return false;
		}
		
		boolean removeEdge(Tile key) {
			if (this.links.contains(key)) {
				this.links.remove(key);
				return true;
			}
			return false;
		}
		
		public void setVisited(boolean visit) {
			this.visited = visit;
		}
		
		public boolean getVisited() {
			return this.visited;
		}
		
	    ArrayList<Tile> getNeighbors() {
	        return this.links;
	    } 
	    
	    boolean containsEdge(Tile k) {
	    	return this.links.contains(k);
	    }
		
		public String toString() {
			return this.row + "\t" + this.col + "\t" + this.visited;
		}
		
	}
	
	public class Edge {
		public Edge(Vertex start, Vertex end) {
			this.start = start;
			this.end = end;
		}
	    public Vertex start;
	    public Vertex end;
	}
	
	/*
	 * adds a vertex given a row and col
	 * returns true if the graph has changed as a result of this operation
	 * false otherwise. 
	 */
	public boolean addVertex(int row, int col) {
		Tile key = QuoridorApplication.getQuoridor().getBoard().getTile(getTileId(row,col));
		// add a vertex to the graph if it's not there yet
		if (!vertexList.containsKey(key)) {
			Vertex v = new Vertex(row, col);
			vertexList.put(key, v);
			return true;
		}
		return false;	
	}
	
	/*
	 * add an edge between two vertices.
	 * returns true if the graph has changed as a result of this operation
	 * false otherwise. 
	 */
	public boolean addEdge(int row1, int col1, int row2, int col2) {
		Tile key1 = QuoridorApplication.getQuoridor().getBoard().getTile(getTileId(row1,col1));
		Tile key2 = QuoridorApplication.getQuoridor().getBoard().getTile(getTileId(row2,col2));
		if (vertexList.containsKey(key1) && vertexList.containsKey(key2)) {
			Vertex v1 = vertexList.get(key1);
			Vertex v2 = vertexList.get(key2);
			return v1.addEdge(key2) && v2.addEdge(key1);
		}
		return false;
	}
	
	/**
	 * Method which populates the graph with edges where there are not walls
	 * @param b
	 */
	public void buildPosition() {
		Tile prev;
		
		// Get all walls on board
		List<Wall> bWalls2 = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getBlackWallsOnBoard();
		List<Wall> bWalls = this.gamePosition.getBlackWallsOnBoard();
		List<Wall> wWalls = this.gamePosition.getWhiteWallsOnBoard();
		ArrayList<Wall> allWalls = new ArrayList<Wall>(wWalls);
		allWalls.addAll(bWalls);
		
		// Add wall move candidate
		allWalls.add(QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate().getWallPlaced());
		
		// Place edges along X axis
		for (int i = 1; i <= 9; i++) { // rows
			prev = null;
			for (int j = 1; j <= 9; j++) { // columns
				addVertex(i,j); 
				
				// Make edges from n to n-1 th column (ie at col=1, we don't make an edge to col=0 -- which doesn't exist)
				if (j >= 2) { 
					boolean isBlocking = false;
					// Find if wall exists in front of this tile
					for (Wall w : allWalls) {
						Direction dir = w.getMove().getWallDirection();
						Tile tile = w.getMove().getTargetTile();
						isBlocking = (i == tile.getRow() || i-1 == tile.getRow()) && (j-1 == tile.getColumn()) && (dir == Direction.Vertical);
						if (isBlocking) break; // If there's one blocking it, stop looking and break loop
					}
					if (!isBlocking) addEdge(prev.getRow(), prev.getColumn(), i, j); // Make edges along reach row
				}
				
				prev = QuoridorApplication.getQuoridor().getBoard().getTile(getTileId(i,j));
			}
		}
		
		// Place edges along Y axis
		for (int j = 1; j <= 9; j++) { // columns
			prev = null;
			for (int i = 1; i <= 9; i++) { // rows
				if (i >= 2) {
					boolean isBlocking = false;
					// Find if wall exists in front of this tile
					for (Wall w : allWalls) {
						Direction dir = w.getMove().getWallDirection();
						Tile tile = w.getMove().getTargetTile();
						isBlocking = (j == tile.getColumn() || j-1 == tile.getColumn()) && (i-1 == tile.getRow()) && (dir == Direction.Horizontal);
						if (isBlocking) break;
					}	
					if(!isBlocking) addEdge(prev.getRow(), prev.getColumn(), i, j); // Make edges along reach row
				}
				prev = QuoridorApplication.getQuoridor().getBoard().getTile(getTileId(i,j));
			}
		}
	}
	/*
	 * forWhite specifies the player for white the search will start
	 * forGherkin is to comply with the view, which has columns and rows reversed
	 */
	public boolean checkPathForPlayer(boolean forWhite, boolean forGherkin) {
		boolean pathExists = false;
		
		// Get starting vertex for search
		Tile playerKey;
		int target;
		if (forWhite) { 
			playerKey = this.gamePosition.getWhitePosition().getTile();
			if(forGherkin) target = 1;
			else target = 9;
		} else {
			playerKey = this.gamePosition.getBlackPosition().getTile();
			if(forGherkin) target = 9;
			else target = 1;
		}
		
		// Start depth first traversal using a stack
		for (Vertex v : this.vertexList.values()) { // Reset visited
			v.setVisited(false);
		}
				
		Stack<Tile> stack = new Stack<Tile>();
		
		this.vertexList.get(playerKey).setVisited(true);
		stack.push(playerKey);
		
		while(!stack.isEmpty()) {
			Tile key = stack.pop();
			for (Tile link : this.vertexList.get(key).getNeighbors()) {
				Vertex v = this.vertexList.get(link);
				if (!v.getVisited()) { // If we haven't visited already
					v.setVisited(true);
					
					// If at goal, break and return true
					if ((forGherkin && link.getRow() == target) || (!forGherkin && link.getColumn() == target)) { 
						pathExists = true;
						break;
					}
					stack.push(link);
				}
			}
			if (pathExists) break; // break again to escape while
		}
		
		return pathExists;
	}
	
	
	private int getTileId(int row, int col) {
		return (row-1)*9+col-1;
	}
	
	public HashMap<Tile,Vertex> getVertexList(){
		return this.vertexList;
	}
	
	public GamePosition getGamePosition() {
		return this.gamePosition;
	}
}


