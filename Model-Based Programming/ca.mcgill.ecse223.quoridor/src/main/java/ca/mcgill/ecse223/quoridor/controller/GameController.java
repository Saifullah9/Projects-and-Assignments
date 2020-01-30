package ca.mcgill.ecse223.quoridor.controller;

import java.io.BufferedReader;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.Scanner;
import java.util.StringTokenizer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


import javax.swing.Timer;
import javax.swing.text.html.HTMLDocument.Iterator;

import ca.mcgill.ecse223.quoridor.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.model.Board;
import ca.mcgill.ecse223.quoridor.model.Direction;
import ca.mcgill.ecse223.quoridor.model.Game;
import ca.mcgill.ecse223.quoridor.model.Player;
import ca.mcgill.ecse223.quoridor.model.PlayerPosition;
import ca.mcgill.ecse223.quoridor.model.Quoridor;
import ca.mcgill.ecse223.quoridor.model.StepMove;
import ca.mcgill.ecse223.quoridor.model.Tile;
import ca.mcgill.ecse223.quoridor.model.User;
import ca.mcgill.ecse223.quoridor.model.Wall;
import ca.mcgill.ecse223.quoridor.model.WallMove;
import ca.mcgill.ecse223.quoridor.model.Game.GameStatus;
import ca.mcgill.ecse223.quoridor.model.Game.MoveMode;
import ca.mcgill.ecse223.quoridor.model.GamePosition;
import ca.mcgill.ecse223.quoridor.model.Move;

public class GameController {
	
	/**Helper method to be called when app starts to initialize board and default users
	 * 
	 * @author DariusPi
	 * 
	 */
	public void initQuorridor(){
		Quoridor q=QuoridorApplication.getQuoridor();
		
		if (q.getBoard()==null) {
			Board board = new Board(q);
			// Creating tiles by rows, i.e., the column index changes with every tile creation
			for (int i = 1; i <= 9; i++) { // rows
				for (int j = 1; j <= 9; j++) { // columns
					board.addTile(i, j);
				}
			}
		}
		
		new User("usera",q);
		new User("userb",q);
	}
	
	/**
	 * Helper method called by the view during new game creation to create walls for users 
	 * 
	 * @author DariusPi
	 * 
	 */
	public void addWalls() {
		Quoridor q=QuoridorApplication.getQuoridor();
		GamePosition pos=q.getCurrentGame().getCurrentPosition();
		for (int j = 0; j < 10; j++) {
			pos.addWhiteWallsInStock(new Wall(j, q.getCurrentGame().getWhitePlayer()));
			//new Wall(j+10, q.getCurrentGame().getBlackPlayer());
			pos.addBlackWallsInStock(new Wall(j+10, q.getCurrentGame().getBlackPlayer()));
		}
	}
	
	/**
	 * For Start New Game feature 
	 * initializes a game with null parameters
	 * 
	 * @author DariusPi
	 * @param Quoridor q
	 * @return
	 */
	public void initGame(Quoridor q){
		
		Player p1=new Player(new Time(10), q.getUser(0), 9, Direction.Horizontal);
		Player p2 = new Player(new Time(10), q.getUser(1), 1, Direction.Horizontal);
		 new Game(GameStatus.Initializing, MoveMode.PlayerMove,q);
		
		q.getCurrentGame().setWhitePlayer(p1);
		q.getCurrentGame().setBlackPlayer(p2);
		
		Tile player1StartPos = q.getBoard().getTile(36);
		Tile player2StartPos = q.getBoard().getTile(44);
		PlayerPosition player1Position = new PlayerPosition(q.getCurrentGame().getWhitePlayer(), player1StartPos);
		PlayerPosition player2Position = new PlayerPosition(q.getCurrentGame().getBlackPlayer(), player2StartPos);
		
		GamePosition gamePos=new GamePosition(0, player1Position, player2Position, q.getCurrentGame().getWhitePlayer(), q.getCurrentGame());
		q.getCurrentGame().setCurrentPosition(gamePos);
		
	}

	
	/**
	 * * For Start New Game feature
	 * Initilizes game clocks to be used for player time limits
	 * 
	 * @author DariusPi
	 * 
	 * @param Quoridor q, Timer t
	 */
	public void startTheClock(Quoridor q, Timer t){
		t.start();
		q.getCurrentGame().setGameStatus(GameStatus.Running);
	}
	
	/**
	 * Helper method to count down clock, returns true if time out, else false
	 * 
	 * @author Darius Piecaitis
	 * @param Quoridor q
	 * @return boolean
	 */
	public boolean countdown(Quoridor q) {
		long tb=q.getCurrentGame().getCurrentPosition().getPlayerToMove().getRemainingTime().getTime();
		long ta=tb-1000;
		if (ta<=0) {
			if (q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
				q.getCurrentGame().setGameStatus(GameStatus.BlackWon);
			}
			else {
				q.getCurrentGame().setGameStatus(GameStatus.WhiteWon);
			}
			
			return true;
		}
		q.getCurrentGame().getCurrentPosition().getPlayerToMove().setRemainingTime(new Time(ta));
		return false;
	}
	
	/**
	 * For Provide Or Select User Name feature
	 * Selects user name and assigns to colour, returns name if correct, else error message
	 * 
	 * @author DariusPi
	 * 
	 * @param Quoridor q
	 * @param String name
	 * @param String colour
	 * @return
	 */
	public String selectUsername(Quoridor q, String name, String colour) {
		//throw new UnsupportedOperationException();
		int i=doesUserExist(q,name);
		if (i==-1) {
			String msg= name +" does not exist";
			return msg;
		}
		else {
			User u =q.getUser(i);
			int thinkingTime=180;
			if (colour.compareTo("white")==0) {
				if (q.getCurrentGame().getWhitePlayer()!=null) {
					q.getCurrentGame().getWhitePlayer().setUser(u);
				}
				else {
					Player player1 = new Player(new Time(thinkingTime), q.getUser(i), 9, Direction.Horizontal);
					q.getCurrentGame().setWhitePlayer(player1);  //(new Player(null, q.getUser(i), null));
				}
				
				
			}
			else {
				if (q.getCurrentGame().getWhitePlayer().getUser().getName().compareTo(name)==0) {
					return "User Already Selected";
				}
				
				if (q.getCurrentGame().getBlackPlayer()!=null) {
					q.getCurrentGame().getBlackPlayer().setUser(u);
				}
				else {
					Player player2 = new Player(new Time(thinkingTime), q.getUser(i), 1, Direction.Horizontal);
					q.getCurrentGame().setBlackPlayer(player2);
				}
			}
			return name;
			
		}
		
	}
	
	/**
	 *  * For Provide Or Select User Name feature
	 * Creates new user, adds it to quoridor, assigns to colour and returns its name if valid, error message if invalid
	 * 
	 * @author DariusPi
	 * 
	 * @param Quoridor q
	 * @param String name
	 * @param String colour
	 * @return String name or error message
	 */
	public String createUsername(Quoridor q, String name, String colour) {
		//throw new UnsupportedOperationException();
		int i=doesUserExist(q,name);
		if (name==null || name.length()==0) {
			return "Invalid Input";
		}
		if (i==-1) {
			User u=new User(name, q);
			q.addUser(u);
			int thinkingTime=180;
			
			if (colour.compareTo("white")==0) {
				if (q.getCurrentGame().getWhitePlayer()!=null) {
					q.getCurrentGame().getWhitePlayer().setUser(u);
				}
				else {
					Player player1 = new Player(new Time(thinkingTime), u, 9, Direction.Horizontal);
					q.getCurrentGame().setWhitePlayer(player1);  //(new Player(null, q.getUser(i), null));
				}
				
				
			}
			else {
				if (q.getCurrentGame().getBlackPlayer()!=null) {
					q.getCurrentGame().getBlackPlayer().setUser(u);
				}
				else {
					Player player2 = new Player(new Time(thinkingTime), u, 1, Direction.Horizontal);
					q.getCurrentGame().setBlackPlayer(player2);
				}
			}
			
			return name;
		}
		else {
			String msg= name +" already exists";
			return  msg;
		}
		
	}
	
	/**
	 * * For Provide Or Select User Name feature
	 * Determines if a user with the given name has already been created, if so return index else return -1
	 * 
	 * @author DariusPi
	 * 
	 * @param Quoridor q
	 * @param String name
	 * @return int index
	 */
	public int doesUserExist(Quoridor q, String name){
		for (int i=0; i<q.numberOfUsers();i++) {
			if (q.getUser(i).getName().contentEquals(name)) {
				return i;
			}
		}
		return -1;
		
	}
	
	/**
	 * For Validate Position Feature
	 * Checks if wall move was valid and returns the result, takes in the wall's highest anchor point, its direction and its id
	 * 
	 * @author DariusPi
	 * 
	 * @param int x1
	 * @param int y1
	 * @param String dir 
	 * @param int id
	 * @return boolean
	 */
	public boolean valWallPosition(int x1,int y1, String dir) {
		Quoridor q =QuoridorApplication.getQuoridor();
		GamePosition curr= q.getCurrentGame().getCurrentPosition();
		
		List<Wall> wWall = curr.getWhiteWallsOnBoard();
		List<Wall> bWall = curr.getBlackWallsOnBoard();
		Direction dirc;
		int col =x1;
		
		int row=y1;
		if (dir.compareTo("vertical")==0) {
			dirc=Direction.Vertical;
			for(Wall pos: wWall){
				if (pos.getMove().getWallDirection().toString().compareTo("Vertical")==0) {
					if (((pos.getMove().getTargetTile().getRow()+1==row+1)||(pos.getMove().getTargetTile().getRow()-1==row+1)||(pos.getMove().getTargetTile().getRow()==row+1)) 
							&&(pos.getMove().getTargetTile().getColumn()==col+1)) {
						return false;
					}
				}
				else {
					if ((pos.getMove().getTargetTile().getColumn()==col+1)&&(pos.getMove().getTargetTile().getRow()==row+1)) {
						return false;
					}
				}
			}
			
			for(Wall pos: bWall){
				if (pos.getMove().getWallDirection().toString().compareTo("Vertical")==0) {
					if (((pos.getMove().getTargetTile().getRow()+1==row+1)||(pos.getMove().getTargetTile().getRow()-1==row+1)||(pos.getMove().getTargetTile().getRow()==row+1)) 
							&&(pos.getMove().getTargetTile().getColumn()==col+1)) {
						return false;
					}
				}
				else {
					if ((pos.getMove().getTargetTile().getColumn()==col+1)&&(pos.getMove().getTargetTile().getRow()==row+1)) {
						return false;
					}
				}
			}	
		}
		else {
			dirc=Direction.Horizontal;
			for(Wall pos: wWall){
				if (pos.getMove().getWallDirection().toString().compareTo("Vertical")==0) {
					if ((pos.getMove().getTargetTile().getColumn()==col+1)&&(pos.getMove().getTargetTile().getRow()==row+1)) {
						return false;
					}
				}
				else {
					if (((pos.getMove().getTargetTile().getColumn()+1==col+1)||(pos.getMove().getTargetTile().getColumn()-1==col+1)||(pos.getMove().getTargetTile().getColumn()==col+1)) 
							&&(pos.getMove().getTargetTile().getRow()==row+1)) {
						return false;
					}
				}
			}
			
			for(Wall pos: bWall){
				if (pos.getMove().getWallDirection().toString().compareTo("Vertical")==0) {
					if ((pos.getMove().getTargetTile().getColumn()==col+1)&&(pos.getMove().getTargetTile().getRow()==row+1)) {
						return false;
					}
				}
				else {
					if (((pos.getMove().getTargetTile().getColumn()+1==col+1)||(pos.getMove().getTargetTile().getColumn()-1==col+1)||(pos.getMove().getTargetTile().getColumn()==col+1)) 
							&&(pos.getMove().getTargetTile().getRow()==row+1)) {
						return false;
					}
				}
			}	
			
		}
		
		return true;
	}
	
	
	
	/**
	 * For Validate Position Feature
	 * Checks if pawn position is within bounds of board
	 * 
	 * @author DariusPi
	 * 
	 * @param Quoridor q
	 * @param int x1
	 * @param int y1
	 * @return
	 */
	public boolean valPawnPosition(Quoridor q,int x1,int y1) {
		
		if (x1>0 && x1<11 && y1>0 && y1<11) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * For Load Position Feature
	 * Checks the position validity of the set of every wall on the board.
	 * Specifically, checks that the walls are in-track and checks for overlap.
	 *  
	 * @author FSharp4
	 * @param q Quoridor
	 * @return if Position is valid
	 */
	public boolean validateAllWalls(Quoridor q) {
		GamePosition testedPosition = q.getCurrentGame().getCurrentPosition();
		List<Wall> whiteWallsOnBoard = testedPosition.getWhiteWallsOnBoard();
		List<Wall> blackWallsOnBoard = testedPosition.getBlackWallsOnBoard();
		ArrayList<Wall> wallsOnBoard = new ArrayList<Wall>();
		
		//constructs a list with all walls on board
		for (Wall wall : whiteWallsOnBoard) {
			wallsOnBoard.add(wall);
		}
		for (Wall wall : blackWallsOnBoard) {
			wallsOnBoard.add(wall); 
		}
		
		for (Wall wall : wallsOnBoard) {
			//wallsOnBoard.remove(wall);
			int row = wall.getMove().getTargetTile().getRow();
			int col = wall.getMove().getTargetTile().getColumn();
			//Bounds check!
			if (row <= 0 || row >= 9 || col <= 0 || col >= 9)
				return false;
			
			Direction direction = wall.getMove().getWallDirection();
			
			/*
			 * There are two cases to check for when a wall is vertical and when a wall is 
			 * horizontal.
			 * 
			 * The first case is the same whether the wall is vertical or horizontal:
			 * 	-	If there exists a wall with the same location as this wall, then the two walls
			 * 		overlap and the set of walls is invalid.
			 * 
			 * The other case depends on the orientation of the wall
			 * 	-	If the wall is horizontal, and there exists another wall with the same row as this
			 * 		wall and a column value differing by one from this wall, then the walls
			 * 		overlap, and the set of walls is invalid.
			 * 	-	If the wall is vertical, and there exists another wall with the same column as
			 * 		this wall and a row value differing by one from this wall, then the walls
			 * 		overlap, and the set of walls is invalid.
			 * 
			 */
			if (direction.equals(Direction.Vertical)) {
				for (Wall checkedWall : wallsOnBoard) {
					if (!(checkedWall.getId() == wall.getId())) {
						int c_row = checkedWall.getMove().getTargetTile().getRow();
						int c_col = checkedWall.getMove().getTargetTile().getColumn();
						Direction c_direction = checkedWall.getMove().getWallDirection();
						if (row == c_row && col == c_col)
							return false;
					
						if (c_direction.equals(Direction.Vertical)) {
							if (col == c_col && (row == c_row + 1 || col == c_row - 1))
								return false;
						}
					}
				}
			} else {
				for (Wall checkedWall : wallsOnBoard) {
					if (!(checkedWall.getId() == wall.getId())) {
						int c_row = checkedWall.getMove().getTargetTile().getRow();
						int c_col = checkedWall.getMove().getTargetTile().getColumn();
						Direction c_direction = checkedWall.getMove().getWallDirection();
						if (row == c_row && col == c_col)
							return false;
						
						if (c_direction.equals(Direction.Horizontal)) {
							if (row == c_row && (col  == c_col + 1 || col == c_col - 1))
								return false;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	
	
	/**
	 * For setTotalThinkingTime feature, sets the thinking time and gives ready to start signal
	 * 
	 * @author AmineMallek
	 * 
	 * @param min (number of)
	 * 
	 * @param sec (number of)
	 * 
	 * 
	 * @throws Exception if time is null
	 * 
	 * 
	 */

	public void setTime (Quoridor q, int min, int sec) throws Exception {

		if (min==0&&sec==0) {
			throw new Exception();
		}
		
		else if ((min<0)||(sec<0)) {
			throw new Exception();
		}
		long time=(min*60+sec)*1000;		//time takes in ms
		
		q.getCurrentGame().getBlackPlayer().setRemainingTime(new Time(time));
		q.getCurrentGame().getWhitePlayer().setRemainingTime(new Time(time));
		q.getCurrentGame().setGameStatus(GameStatus.ReadyToStart);
	}


	/**
	 * For savePosition feature, saves the position, accounting for who starts in the way given in overview
	 * 
	 * @author AmineMallek
	 * 
	 * @param FileName
	 * 
	 * @returns false when no filename is there, true else
	 */

	public Boolean filename_exists (String FileName) {
		File filename = new File(FileName);
		if(filename.exists()) return true;
		else return false;
	}
	
	/**
	 *  The save game method is responsible for saving the current game and its moves in a file. 
	 * @author amine
	 * @param q
	 * @param FileName
	 * @throws IOException
	 * @returns game data file
	 */
	public void SaveGame(Quoridor q, String FileName) throws IOException {
	
		File file=new File(FileName);		//Our file created
		
		file.setWritable(true);
		file.createNewFile();
		PrintWriter writer;
		try {
			writer = new PrintWriter(file, "UTF-8");
			//if white playing, white should be on the first line
			if(q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite())	//if it's the white player's turn
			{
				int rowW = q.getCurrentGame().getCurrentPosition().getWhitePosition().getTile().getRow(); //gets row number
				int columnW = q.getCurrentGame().getCurrentPosition().getWhitePosition().getTile().getColumn();	// gets column number
				
				String ColumnW = "";  //needs conversion to letter, using switch cases
				switch(columnW) {
				
				case 1:  ColumnW = "a";
					break;
				
				case 2:  ColumnW = "b";
					break;
					
				case 3:   ColumnW = "c";
					break;
					
				case 4:   ColumnW = "d";
					break;
					
				case 5:  ColumnW = "e";
					break;
					
				case 6:  ColumnW = "f";
					break;
					
				case 7:  ColumnW = "g";
					break;
					
				case 8:  ColumnW = "h";
					break;
					
				case 9: ColumnW = "i";
					
				}
				writer.print("W: " + ColumnW+rowW);//print white player position
				
				List<Wall> wWall	= q.getCurrentGame().getCurrentPosition().getWhiteWallsOnBoard(); //wall objects
				
				for(Wall pos: wWall)
				{ 
				
					int WallColumnWhite = pos.getMove().getTargetTile().getColumn();//store column number to be converted to int
					String WallColumnLetterWhite = "";
					
					switch(WallColumnWhite) {
					
					case 1:  WallColumnLetterWhite = "a";
						break;
					
					case 2:  WallColumnLetterWhite = "b";
						break;
						
					case 3:   WallColumnLetterWhite = "c";
						break;
						
					case 4:   WallColumnLetterWhite = "d";
						break;
						
					case 5:  WallColumnLetterWhite = "e";
						break;
						
					case 6:  WallColumnLetterWhite = "f";
						break;
						
					case 7:  WallColumnLetterWhite = "g";
						break;
						
					case 8:  WallColumnLetterWhite = "h";
						break;
						
					case 9: WallColumnLetterWhite = "i";
						
					}					
					writer.print("," + WallColumnLetterWhite + pos.getMove().getTargetTile().getRow() + pos.getMove().getWallDirection().toString().charAt(0));

				}
				writer.println("");
				int rowB = q.getCurrentGame().getCurrentPosition().getBlackPosition().getTile().getRow();		//black player row		
				int columnB = q.getCurrentGame().getCurrentPosition().getBlackPosition().getTile().getColumn(); //white player column
				String ColumnB = "";			//string column to make it a letter
				switch(columnB) {
				
				case 1:  ColumnB = "a";
					break;
				
				case 2:  ColumnB = "b";
					break;
					
				case 3:   ColumnB = "c";
					break;
					
				case 4:   ColumnB = "d";
					break;
					
				case 5:  ColumnB = "e";
					break;
					
				case 6:  ColumnB = "f";
					break;
					
				case 7:  ColumnB = "g";
					break;
					
				case 8:  ColumnB = "h";
					break;
					
				case 9: ColumnB = "i";
					
				}

				writer.print("B: " + ColumnB+rowB); //prints black player's position
			
				
				List<Wall> bWall	= q.getCurrentGame().getCurrentPosition().getBlackWallsOnBoard();	//wall objects list	
				for(Wall pos1: bWall)
				{ 
					int WallColumnBlack = pos1.getMove().getTargetTile().getColumn(); //gets column number to convert it to letter
					String WallColumnLetterBlack = "";
					
					switch(WallColumnBlack) {		//letter switch
					
					case 1:  WallColumnLetterBlack = "a";
						break;
					
					case 2:  WallColumnLetterBlack = "b";
						break;
						
					case 3:   WallColumnLetterBlack = "c";
						break;
						
					case 4:   WallColumnLetterBlack = "d";
						break;
						
					case 5:  WallColumnLetterBlack = "e";
						break;
						
					case 6:  WallColumnLetterBlack = "f";
						break;
						
					case 7:  WallColumnLetterBlack = "g";
						break;
						
					case 8:  WallColumnLetterBlack = "h";
						break;
						
					case 9: WallColumnLetterBlack = "i";
						
					}
				
					writer.print("," + WallColumnLetterBlack + pos1.getMove().getTargetTile().getRow() + pos1.getMove().getWallDirection().toString().charAt(0));
				}

					writer.close();	
			}
			
			//if black playing, black should be on the first line
			else if (q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsBlack())	//if it's black player's turn
			{
				
				int rowB = q.getCurrentGame().getCurrentPosition().getBlackPosition().getTile().getRow(); //int  row
				int columnB = q.getCurrentGame().getCurrentPosition().getBlackPosition().getTile().getColumn(); //int column to letter
				String ColumnB = ""; //String that'll contain the column letter
				switch(columnB) {	//switch case
				
				case 1:  ColumnB = "a";
					break;
				
				case 2:  ColumnB = "b";
					break;
					
				case 3:   ColumnB = "c";
					break;
					
				case 4:   ColumnB = "d";
					break;
					
				case 5:  ColumnB = "e";
					break;
					
				case 6:  ColumnB = "f";
					break;
					
				case 7:  ColumnB = "g";
					break;
					
				case 8:  ColumnB = "h";
					break;
					
				case 9: ColumnB = "i";
					
				}

				writer.print("B: " + ColumnB+ rowB);//print player's position


				List<Wall> bWall	= q.getCurrentGame().getCurrentPosition().getBlackWallsOnBoard(); //wall object list
				for(Wall pos1: bWall) //enhanced for loop
				{ 
				
					int WallColumnBlack = pos1.getMove().getTargetTile().getColumn(); //column int
					String WallColumnLetterBlack = ""; //column letter string
					
					switch(WallColumnBlack) { //switch
					
					case 1:  WallColumnLetterBlack = "a";
						break;
					
					case 2:  WallColumnLetterBlack = "b";
						break;
						
					case 3:   WallColumnLetterBlack = "c";
						break;
						
					case 4:   WallColumnLetterBlack = "d";
						break;
						
					case 5:  WallColumnLetterBlack = "e";
						break;
						
					case 6:  WallColumnLetterBlack = "f";
						break;
						
					case 7:  WallColumnLetterBlack = "g";
						break;
						
					case 8:  WallColumnLetterBlack = "h";
						break;
						
					case 9: WallColumnLetterBlack = "i";
						
					}
					//print walls, column, row, direction
					writer.print("," + WallColumnLetterBlack + pos1.getMove().getTargetTile().getRow() + pos1.getMove().getWallDirection().toString().charAt(0));
				}
				writer.println("");//new line
				int rowW = q.getCurrentGame().getCurrentPosition().getWhitePosition().getTile().getRow();//int row
				int columnW = q.getCurrentGame().getCurrentPosition().getWhitePosition().getTile().getColumn();//int 
				String ColumnW = ""; //string column
				switch(columnW) {
				
				case 1:  ColumnW = "a";
					break;
				
				case 2:  ColumnW = "b";
					break;
					
				case 3:   ColumnW = "c";
					break;
					
				case 4:   ColumnW = "d";
					break;
					
				case 5:  ColumnW = "e";
					break;
					
				case 6:  ColumnW = "f";
					break;
					
				case 7:  ColumnW = "g";
					break;
					
				case 8:  ColumnW = "h";
					break;
					
				case 9: ColumnW = "i";
					
				}

				writer.print("W: " + ColumnW  + rowW); //string print player position

	
				List<Wall> wWall	= q.getCurrentGame().getCurrentPosition().getWhiteWallsOnBoard(); //object wall
		
				for(Wall pos: wWall)
				{ 
					int WallColumnWhite = pos.getMove().getTargetTile().getColumn(); //column int
					String WallColumnLetterWhite = "";	//column string to be filled with a letter
					
					switch(WallColumnWhite) {	//switch case
						case 1:  WallColumnLetterWhite = "a";
							break;
						
						case 2:  WallColumnLetterWhite = "b";
							break;
							
						case 3:   WallColumnLetterWhite = "c";
							break;
							
						case 4:   WallColumnLetterWhite = "d";
							break;
							
						case 5:  WallColumnLetterWhite = "e";
							break;
							
						case 6:  WallColumnLetterWhite = "f";
							break;
							
						case 7:  WallColumnLetterWhite = "g";
							break;
							
						case 8:  WallColumnLetterWhite = "h";
							break;
							
						case 9: WallColumnLetterWhite = "i";	
					}
				
					//print wall column, row, direction
					writer.print("," + WallColumnLetterWhite + pos.getMove().getTargetTile().getRow() + pos.getMove().getWallDirection().toString().charAt(0));
				
				}
				 writer.close();	//close writer
			}
			    
		} catch (FileNotFoundException e) {
			// empty catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// empty catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * * For Rotate Wall feature 
	 * Attempts to rotate the current WallMove candidate's Direction
	 * 
	 * @author louismollick
	 * 
	 * @param game
	 * @throws Exception
	 */
	public void rotateWall() throws Exception{
		WallMove wmc = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate();
		if (wmc != null) {
			Direction dir = wmc.getWallDirection();
			if (dir.equals(Direction.Horizontal)) dir = Direction.Vertical;
			else dir = Direction.Horizontal;
			wmc.setWallDirection(dir);
		}else {
			throw new Exception("No WallMove Candidate!");
		}
	}
	
	/**
	 *  * For Grab Wall feature 
	 * Attempts to set current WallMove candidate using a Wall in current player's stock
	 * 
	 * @author louismollick
	 * 
	 */
	public boolean grabWall(){
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Game game = quoridor.getCurrentGame();
		Player playerToMove = game.getCurrentPosition().getPlayerToMove();
		Tile initialPos = quoridor.getBoard().getTile(40);
		
		if(playerToMove == null) return false;
		int moveNum = game.numberOfMoves();
		int roundNum = 1;
		if(moveNum != 0) roundNum = game.getMove(moveNum-1).getRoundNumber();
		
		if(playerToMove.hasGameAsBlack()) {
			if(game.getCurrentPosition().hasBlackWallsInStock()) {
				int size = game.getCurrentPosition().getBlackWallsInStock().size();
				Wall w = game.getCurrentPosition().getBlackWallsInStock(size-1);
				w.setMove(new WallMove(moveNum, roundNum, playerToMove, initialPos, game, Direction.Vertical, w));
				return game.setWallMoveCandidate(w.getMove());
			} 
			else return false;
		}
		else {
			if(game.getCurrentPosition().hasWhiteWallsInStock()) {
				int size = game.getCurrentPosition().getWhiteWallsInStock().size();
				Wall w = game.getCurrentPosition().getWhiteWallsInStock(size-1);
				w.setMove(new WallMove(moveNum, roundNum, playerToMove, initialPos, game, Direction.Vertical, w));
				return game.setWallMoveCandidate(w.getMove());
			} 
			else return false;
		}
	}
	
	/**
	 * For Move Wall feature
	 * Attempts to move the wall between possible rows and columns of the board 
	 * 
	 * @author Saifullah
	 * 
	 * @param game
	 * @param side
	 * @throws UnsupportedOperationException
	 */
	public void moveWall(Game game, String side) throws UnsupportedOperationException{
		WallMove wmc = game.getWallMoveCandidate();
		Tile originalTile = wmc.getTargetTile();
		if(!validatePosition(game)){
			throw new UnsupportedOperationException("The position is invalid");
		}
		if(side.equals("left") && originalTile.getColumn() == 1) { // Check if the wall is at the left edge
			throw new UnsupportedOperationException("The wall is at the left edge of the board");
		}
		
		if(side.equals("right") && originalTile.getColumn() == 8) { // Check if the wall is at the right edge
			throw new UnsupportedOperationException("The wall is at the right edge of thr board");
		}
		
		if(side.equals("up") && originalTile.getRow() == 1) { // Check if the wall is at the top edge
			throw new UnsupportedOperationException("The wall is at the top edge of the board");	
		}
		
		if(side.equals("down") && originalTile.getRow() == 8 ) { // Check if the wall is at the bottom edge
			throw new UnsupportedOperationException("The wall is at the bottom edge of the board");
			
		}
		
		/*
		 * Below, are the operations that are executed for each side.
		 */
		if(side.equals("left")){
			Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(originalTile.getRow(),originalTile.getColumn()-1));
			wmc.setTargetTile(target);
		}
		
		if(side.equals("right")){
			Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(originalTile.getRow(),originalTile.getColumn()+1));
			wmc.setTargetTile(target);
		}
		
		if(side.equals("up")){
			Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(originalTile.getRow()-1,originalTile.getColumn()));
			wmc.setTargetTile(target);
		}
		
		if(side.equals("down")){
			Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(originalTile.getRow()+1,originalTile.getColumn()));
			wmc.setTargetTile(target);
		}


	}
	
	/**
	 * For Drop Wall feature
	 * Attempts to drop the wall (place the wall) between possible rows and columns of the board 
	 * 
	 * @author Saifullah, credit for DariusPi for changing the entire method and making it work for the application.
	 * 
	 * @param game
	 * @throws UnsupportedOperationException
	 */
	public boolean dropWall(int col, int row, String dir, int id) {		
		Quoridor q = QuoridorApplication.getQuoridor();
		Game g=q.getCurrentGame();
		Direction dirc;
		if(dir.compareTo("vertical") == 0) {
			dirc = Direction.Vertical;
		}
		else {
			dirc = Direction.Horizontal;
		}
		
		GamePosition curr=g.getCurrentPosition();
		WallMove wmc;
		if(id<10) {
			Wall w = g.getWhitePlayer().getWall(id);
			if (w.getMove()!=null) {
				w.getMove().delete();
			}
			wmc = new WallMove(g.numberOfPositions()-1,1,g.getWhitePlayer(),q.getBoard().getTile(col+row*9),g,dirc,w);
		} else {
			Wall w=g.getBlackPlayer().getWall(id-10);
			if (w.getMove()!=null) {
				w.getMove().delete();
			}
			wmc = new WallMove(g.numberOfPositions()-1,1,g.getBlackPlayer(),q.getBoard().getTile(col+row*9),g,dirc,w);
		}
		
		if (!(checkPathExistence(false).compareTo("both") == 0)) {
			return false;
		}
		
		GamePosition next;
		PlayerPosition p1=new PlayerPosition(g.getWhitePlayer(),curr.getWhitePosition().getTile());
		PlayerPosition p2=new PlayerPosition(g.getBlackPlayer(),curr.getBlackPosition().getTile());
		if(curr.getPlayerToMove().hasGameAsWhite()) {
			next=new GamePosition(g.numberOfPositions(), p1, p2, g.getBlackPlayer(), g);
		}
		else {
			next=new GamePosition(g.numberOfPositions(), p1, p2, g.getWhitePlayer(), g);
		}
		for (Wall w : curr.getBlackWallsOnBoard()) {
			next.addBlackWallsOnBoard(w);
		}
		for (Wall w : curr.getWhiteWallsOnBoard()) {
			next.addWhiteWallsOnBoard(w);
		}
		for (Wall w : curr.getBlackWallsInStock()) {
			next.addBlackWallsInStock(w);
		}
		for (Wall w : curr.getWhiteWallsInStock()) {
			next.addWhiteWallsInStock(w);
		}
		g.setCurrentPosition(next);
		if(id<10) {
			g.addMove(wmc);
			
			g.getCurrentPosition().removeWhiteWallsInStock(g.getWhitePlayer().getWall(id));
			g.getCurrentPosition().addWhiteWallsOnBoard(g.getWhitePlayer().getWall(id));
		}
		else {
			g.addMove(wmc);
			g.getCurrentPosition().removeBlackWallsInStock(g.getBlackPlayer().getWall(id-10));
			g.getCurrentPosition().addBlackWallsOnBoard(g.getBlackPlayer().getWall(id-10));
		}
		
		return true;

//		Boolean path=true;
//		if (!checkPath(q)) {
//			g.setCurrentPosition(g.getPosition(g.getCurrentPosition().getId()-1));
//			g.getPosition(g.getPositions().size()-1).delete();
//			
//			Move m=g.getMove(g.getMoves().size()-1);
//			m.delete();
//			g.removeMove(m);
//			path=false;
//		}
//		return path;
//		
//		//
	}
	
	
	/**
	 * Testing method validatePosition
	 * 
	 *I just added this for the sake of testing till the actual method is done
	 * @author Saifullah
	 * @param game
	 * @throws UnsupportedOperationException
	 */
	public boolean validatePosition(Game game) throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * For Load Position feature
	 * Loads a saved game. Currently does not at all work.
	 * @throws Exception 
	 * 
	 */
	public void loadGame(Quoridor quoridor, String filename) throws Exception {
		initSavedGameLoad(quoridor, filename);
		validityChecking(quoridor);
	}
	
	/**
	 * For Load Position feature.
	 * Throws an exception when the load game is invalid but the initial loading was successful
	 * 
	 * @author FSharp4
	 * @param quoridor
	 * @throws Exception
	 */
	public void validityChecking(Quoridor quoridor) throws Exception {
		if (!checkIfLoadGameValid(quoridor)) {
			throw new Exception("Load failed: Position is invalid!");
		}
	}
	
	/**
	 * * For Load Position feature
	 * Initiates loading a saved game
	 * 
	 * @author FSharp4
	 * @param quoridor
	 * @param filename
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedOperationException
	 */
	public Game initSavedGameLoad(Quoridor quoridor, String filename) throws Exception {
		if (quoridor.getCurrentGame()==null) {
			initGame(quoridor);
		}
		Game game = quoridor.getCurrentGame();
		Board board = quoridor.getBoard();
		
		//initialize scanning on file with position data
		//assume that file is well formed even if invalid
		File file = new File(filename);
		Scanner fileSC = null;
		try {
			fileSC = new Scanner(file);
		} catch (FileNotFoundException e) {
			throw new Exception("File does not exist!");
		}
		
		//Call Tokenizers
		StringTokenizer s1 = new StringTokenizer(fileSC.nextLine());
		StringTokenizer s2 = new StringTokenizer(fileSC.nextLine());
		fileSC.close();
		
		//Set Players
		String playerOneString = s1.nextToken();
		s2.nextToken(); //iterate past playerstring; we can infer this info from the first line
		
		boolean isPlayerOneWhite = (playerOneString.contentEquals("W:")) ? true : false;
		Player playerOne, playerTwo;
		int playerOneWallID = 0; 		//wall ID within player stock
		int playerOneAbsoluteWallID = 1;//wall ID within list of wallsByID in Wall.class
		int playerTwoWallID = 0;		//these are both necessary because we don't know which
		int playerTwoAbsoluteWallID = 1;//player is white when method is called
		if (isPlayerOneWhite) {
			playerOne = game.getWhitePlayer();
			playerTwo = game.getBlackPlayer();
			playerTwoAbsoluteWallID += 10;
		} else {
			playerOne = game.getBlackPlayer();
			playerTwo = game.getWhitePlayer();
			playerOneAbsoluteWallID += 10;
		}
		
		if (!Wall.hasWithId(1)) {
			for (int i = 1; i < 11; i++) {
				new Wall(i, game.getWhitePlayer());
				new Wall(i + 10, game.getBlackPlayer());
			}
		}
		
		PlayerPosition playerOnePosition = null;
		PlayerPosition playerTwoPosition = null;
		
		/*
		 * Get playerPositions for setting the currentPosition
		 * This needs to be done before loading the wall positions, as that process needs a valid
		 * game position to work.
		 */
		{
			//current player
			String playerOnePositionString = s1.nextToken(","); //grabs position in string form
			if (playerOnePositionString.charAt(0) == ' ')
				playerOnePositionString = playerOnePositionString.substring(1); //remove whitespace
			
			String playerTwoPositionString = s2.nextToken(","); //repeat for up-next player
			if (playerTwoPositionString.charAt(0) == ' ')
				playerTwoPositionString = playerTwoPositionString.substring(1); 
			
			//current player
			int col = playerOnePositionString.charAt(0) - 'a' + 1;
			int row = playerOnePositionString.charAt(1) - '0';
			Tile tile = board.getTile(getIndex(row, col));
			try {
				playerOnePosition = new PlayerPosition(playerOne, tile);
			} catch (Exception e) {
				throw new Exception("Load failed: Current player's pawn has invalid position");
			}
			
			//up-next player
			col = playerTwoPositionString.charAt(0) - 'a' + 1;
			row = playerTwoPositionString.charAt(1) - '0';
			tile = board.getTile(getIndex(row, col));
			try {
				playerTwoPosition = new PlayerPosition(playerTwo, tile);
			} catch (Exception e) {
				throw new Exception("Load failed: Up-next player's pawn has invalid position");
			}
			
		}
		
		//GamePosition gp = makeInitialGamePosition(game, playerOnePosition, playerTwoPosition, playerOne, isPlayerOneWhite);
		//game.setCurrentPosition(gp);
		if (isPlayerOneWhite) {
			game.getCurrentPosition().setWhitePosition(playerOnePosition);
			game.getCurrentPosition().setBlackPosition(playerTwoPosition);
		}
		else {
			game.getCurrentPosition().setWhitePosition(playerTwoPosition);
			game.getCurrentPosition().setBlackPosition(playerOnePosition);
		}
		game.getCurrentPosition().setPlayerToMove(playerOne);
		GamePosition gp=game.getCurrentPosition();
		/*	
		 * 	Puts freshly-initialized walls in the stocks of both players.
		 * 	White has walls 1-10, black has walls 11-20
		 */
		for (int i = 1; i <= 10; i++) {
			Wall whiteWall = Wall.getWithId(i);
			gp.addWhiteWallsInStock(whiteWall);
			Wall blackWall = Wall.getWithId(i + 10);
			gp.addBlackWallsInStock(blackWall);
		}
		
		while (s1.hasMoreTokens() || s2.hasMoreTokens()) {
			if (s1.hasMoreTokens()) {
				String move = s1.nextToken(",");
				if (move.charAt(0) == ' ' || move.charAt(0) == ',')
					move = move.substring(1);
				
				int col = move.charAt(0) - 'a' + 1;
				int row = move.charAt(1) - '0';
				if (col == 9 || row == 9) 
					throw new Exception("Load failed: Wall is out-of-track! "
							+ "(Edge case: value 9 given for row or column)");
					//walls can't exist on the last row/column because they would be out of track
				
				Tile tile = null;
				try {
					tile = board.getTile(getIndex(row, col));
				} catch (Exception e) {
					throw new Exception("Load failed: Wall is out-of-track! (General case)");
				}
				
				Direction dir = ((move.charAt(2) == 'h')||(move.charAt(2) == 'H')) 
						? Direction.Horizontal : Direction.Vertical;
				Wall wall = Wall.getWithId(playerOneAbsoluteWallID);
				wall.setMove(new WallMove(game.numberOfMoves(), 1, playerOne, tile, game, dir, 
						wall));
				if (!addOrMoveWallsOnBoard(gp, wall, isPlayerOneWhite))
					throw new Exception("Unable to move wall from stock to board for player " 
							+ "one");

				playerOneWallID++;
				playerOneAbsoluteWallID++;
			}
			
			if (s2.hasMoreTokens()) {
				String move = s2.nextToken(",");
				if (move.charAt(0) == ' ' || move.charAt(0) == ',')
					move = move.substring(1);
				
				int col = move.charAt(0) - 'a' + 1;
				int row = move.charAt(1) - '0';
				if (col == 9 || row == 9) 
					throw new Exception("Load failed: Wall is out-of-track! "
							+ "(Edge case: value 9 given for row or column)");
				
				Tile tile = null;
				try {
					tile = board.getTile(getIndex(row, col));
				} catch (Exception e) {
					throw new Exception("Load failed: Wall is out-of-track! (General case)");
				}
				
				Direction dir = ((move.charAt(2) == 'h')||(move.charAt(2) == 'H')) 
						? Direction.Horizontal : Direction.Vertical;
				Wall wall = Wall.getWithId(playerTwoAbsoluteWallID);
				wall.setMove(new WallMove(game.numberOfMoves(), 1, playerTwo, tile, game, dir, 
						wall));
				if (!addOrMoveWallsOnBoard(gp, wall, !isPlayerOneWhite))
					throw new Exception("Unable to move wall from stock to board for player " 
							+ "two");

				playerTwoWallID++;
				playerTwoAbsoluteWallID++;
			}
		}
		
		gp.setPlayerToMove(playerOne);
		quoridor.getCurrentGame().setGameStatus(GameStatus.ReadyToStart);
		//game is not ready to start, but this signals to load position testing that this
		//was successful
		return game;
	}

	/**
	 *  A helper method for load position where it constructs an initial game position data and
	 *   player color data for a given game.
	 * @param game
	 * @param playerOnePosition
	 * @param playerTwoPosition
	 * @param playerOne
	 * @param isPlayerOneWhite
	 * @return GamePosition
	 */
	private static GamePosition makeInitialGamePosition(Game game, PlayerPosition playerOnePosition, 
			PlayerPosition playerTwoPosition, Player playerOne, boolean isPlayerOneWhite) {
		GamePosition aNewGamePosition;
		if (isPlayerOneWhite) {
			aNewGamePosition = new GamePosition(0, playerOnePosition, playerTwoPosition, playerOne, 
					game);
		} else {
			aNewGamePosition = new GamePosition(0, playerTwoPosition, playerOnePosition, playerOne,
					game);
		}
		return aNewGamePosition;
	}
	
	
	/*
	 * A helper method to calculate the index of the tile using its row and column numbers.
	 */
	private static int getIndex(int row, int col) {
		
		if(row <= 0 || col <= 0 || row > 9 || col > 9) {
			return -10; 
		}
		else {
		return ((((row-1)*9)+col)-1);
		}
		
	}
	
	/**
	 * Color-agnostic combined addOrMoveWallsOnBoard and removeWallsInStock for loadPosition feature
	 * @param gp
	 * @param wall
	 * @param isWhite
	 * @return boolean
	 */
	private static boolean addOrMoveWallsOnBoard(GamePosition gp, Wall wall, boolean isWhite) {
		boolean didRemove = false;
		boolean didAdd = false;
		if (isWhite) {
			didRemove = gp.removeWhiteWallsInStock(wall);
			if (!didRemove) {
				return false;
			}
			
			didAdd = gp.addOrMoveWhiteWallsOnBoardAt(wall, gp.getWhiteWallsOnBoard().size());
			return didAdd;
		} else {
			didRemove = gp.removeBlackWallsInStock(wall);
			if (!didRemove) {
				return false;
			}
			didAdd = gp.addOrMoveBlackWallsOnBoardAt(wall, gp.getBlackWallsOnBoard().size());
			return didAdd;
		}
	}
	
	
	
	/**
	 *  * For Load Position feature
	 *  
	 *  Checks position information stored in current game. returns an error if position is invalid.
	 *  Specifically, checks for pawn overlap and wall position validity for the set of walls on the
	 *  board (via validateAllWalls). Invalid pawn and wall posiitons (such as out-of-track) are
	 *  handled by initSavedGameLoad (it is pretty easy to immediately know that these occur)
	 * 
	 * @author FSharp4
	 * @param quoridor
	 */
	public boolean checkIfLoadGameValid(Quoridor quoridor) {
		  //throws UnsupportedOperationException, IOException {
		
		/*
		 * Check for pawn overlap (invalid pawn positions otherwise should have triggered an
		 * exception in initSavedGameLoad())
		 */
		GamePosition currentPosition = quoridor.getCurrentGame().getCurrentPosition();
		Tile whitePos = currentPosition.getWhitePosition().getTile();
		Tile blackPos = currentPosition.getBlackPosition().getTile();
		if (whitePos.getRow() == blackPos.getRow() && whitePos.getColumn() == blackPos.getColumn())
			return false;
		
		return validateAllWalls(quoridor);
	}
	
	/**
	 * * For Load Position feature
	 * Sets current turn, returns true if this is successful
	 * 
	 * @author FSharp4
	 * @param player
	 * @param quoridor
	 * @return
	 * @throws UnsupportedOperationException
	 */
	public boolean setCurrentTurn(boolean isWhite, Quoridor quoridor)  {
		Player player;
		if (isWhite) {
			player = quoridor.getCurrentGame().getWhitePlayer();
		} else {
			player = quoridor.getCurrentGame().getWhitePlayer();
		}
		return quoridor.getCurrentGame().getCurrentPosition().setPlayerToMove(player);
	}
	
	/**
	 *  * For InitializeBoard Feature
	 * Checks if Board Initialization is Initiated (such as when user selects new game and has 
	 * entered in the necessary info (i.e. game is ready to start)
	 * 
	 * @author FSharp4
	 * @param quoridor
	 * @return
	 * @throws UnsupportedOperationException
	 */
	public boolean isBoardInitializationInitiated(Quoridor quoridor) throws UnsupportedOperationException {
		//Check if board going to be initialized @GUI
		throw new UnsupportedOperationException();
	}
	
	/**
	 * * For InitializeBoard Feature
	 * Checks if player's clock is counting down
	 * 
	 * @param player
	 * @return
	 * @throws UnsupportedOperationException
	 */
	
	public boolean isClockCountingDown(Player player) throws UnsupportedOperationException {
		//This interacts with the clock Time object and checks GUI to see if a countdown is shown
		return true;
		//throw new UnsupportedOperationException();
	}
	
	/**
	 * For ValidatePosition Feature 
	 * Checks if if pawn position is valid or not
		
	 * 
	 * @author ohuss1
	 * 
	 * @throws UnsupportedOperationException
	 */
	public Boolean validatePos(GamePosition posToValidate) {//commented out for merge 
		
		//Checks if pawn position overlaps with another pawn or a wall position overlaps with a wall or out of track pawn or wall
		//if yes returns error, if no returns ok
		if(posToValidate.getId()==-1) {//handles walls and pawn out of board
			return false;
		}
		//cond1 same pawn pos
		//cond2 out of board pos
		//cond3 same wall
		//cond4 if closed wall hard to implement
		
		//else we know id is 1.
		//so
		//now check if walls overlap or pawns overlap
		//get game position player1position it has tile position which has row and col
		GamePosition Prev=GamePosition.getWithId(1);
		PlayerPosition Test=Prev.getWhitePosition();//want to get awhiteposition
		Tile TestTile=Test.getTile();
		int player1Row=TestTile.getRow();
		int player1Col=TestTile.getColumn();
		//now check if anything else at this place
		//check blackposition if same
		PlayerPosition Test2=Prev.getBlackPosition();
		int player2Row=Test2.getTile().getRow();
		int player2Col=Test2.getTile().getColumn();
		if((player1Row==player2Row)&&(player1Col==player2Col)) {
			return false;
		}
		
		//For Walls 
		//
		return true; //3rd nov
	}

	/**
	 * For Initialize Board feature
	 * Prepares the board populated with tiles for the given quoridor
	 * 
	 * @author FSharp4
	 * @param q
	 * @return new Board
	 * @throws UnsupportedOperationException
	 */
	public void initBoard(Quoridor q) {
		addWalls();
		for (int i=0;i<10;i++) {
			q.getCurrentGame().getCurrentPosition().addOrMoveWhiteWallsInStockAt(q.getCurrentGame()
					.getWhitePlayer().getWall(i), i);
			q.getCurrentGame().getCurrentPosition().addOrMoveBlackWallsInStockAt(q.getCurrentGame()
					.getBlackPlayer().getWall(i), i);
		}
	}
	
	/**
	 * For Draw Game feature (IdentifyIfGameDrawn)
	 * Checks for three-fold repetition of moves. This is only possible with step moves.
	 * Returns true if the game was a draw (and indicates such in the model if the game is so).
	 * 
	 * @author FSharp4
	 * @param q
	 * @return
	 */
	public boolean drawCheck(Quoridor q) {

		Game g = q.getCurrentGame();
		if (g.getGameStatus() == GameStatus.BlackWon || g.getGameStatus() == GameStatus.WhiteWon)
			return false;
		
		if (g.numberOfMoves() < 9) //Need at least this many moves for there to be a draw
			return false;
		
		Move[] p1Lastmoves = new Move[5];
		Move[] p2Lastmoves = new Move[4];
		for (int i = 0; i < 5; i++) {
			p1Lastmoves[i] = g.getMove(g.numberOfMoves() - 1 - 2 * i);
			if (!(p1Lastmoves[i] instanceof StepMove))
				return false;
			if (i != 4) {
				p2Lastmoves[i] = g.getMove(g.numberOfMoves() - 2 - 2 * i);
				if (!(p2Lastmoves[i] instanceof StepMove))
					return false;
			}
		}

		//Checks that moves are equal 2 at a time
		if (!movesAreIdentical(p1Lastmoves[0], p1Lastmoves[2]))
			return false;
		if (!movesAreIdentical(p1Lastmoves[0], p1Lastmoves[4]))
			return false;
		if (!movesAreIdentical(p1Lastmoves[1], p1Lastmoves[3]))
			return false;
		if (!movesAreIdentical(p2Lastmoves[0], p2Lastmoves[2]))
			return false;
		if (!movesAreIdentical(p2Lastmoves[1], p2Lastmoves[3]))
			return false;
		
		//If we are here, game is a draw
		g.setGameStatus(GameStatus.Draw);
		return true;
	}
	
	/*
	 * Helper method
	 * Checks to see if the player and destination of the move are identical for draw checking
	 */
	private boolean movesAreIdentical(Move move1, Move move2) {
		if (move1.getPlayer() == move2.getPlayer()) {
			if (move1.getTargetTile() == move2.getTargetTile()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * For ReportFinalResult feature
	 * @param q
	 */
	public void setFinalGameStatus(GameStatus status) {
		QuoridorApplication.getQuoridor().getCurrentGame().setGameStatus(status);
		QuoridorApplication.getQuoridorView().setStageMove(true);
		QuoridorApplication.getQuoridorView().finishGame("White wins!");
	}
	
	public void deleteGame(Quoridor q) {
		q.getCurrentGame().delete();
	}
	
	public void switchPlayer(Quoridor q) {
		if (q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
			q.getCurrentGame().getCurrentPosition().setPlayerToMove(q.getCurrentGame().getBlackPlayer());
		}
		else {
			q.getCurrentGame().getCurrentPosition().setPlayerToMove(q.getCurrentGame().getWhitePlayer());
		}
	}
	
	/**
	 * For View, used in QuoridorMouseListener
	 * Helper function which returns the Color of the current player
	 * 
	 * @author louismollick
	 */
	public Color getCurrentPlayerColor() {
		Player p = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove();
		if (p.hasGameAsBlack()) {
			return Color.BLACK;
		}
		else if (p.hasGameAsWhite()) {
			return Color.WHITE;
		}
		return null;
	}
	
	/*Iteration 5*/
	
	/**
	 * For EnterReplayModeFeature
	 * 
	 * User attempts to continue a game while in replay mode
	 * 
	 * @author DariusPi
	 * 
	 * @param q
	 * @returns true for possible, or false for impossible
	 */
	public Boolean continueGame(Quoridor q) {
		
		boolean isOver=checkResult(q);
		if(isOver) {
			q.getCurrentGame().setGameStatus(GameStatus.Replay);
			return false;
		}
		else {
			Game g=q.getCurrentGame();
			GamePosition curr=g.getCurrentPosition();
			int i=curr.getId();
			int j=g.getPositions().size()-i-1; //number of positions to delete
			for (int k=0;k<j;k++) {
				g.getPosition(g.getPositions().size()-1).delete();
				
				
				Move m=g.getMove(g.getMoves().size()-1);
				m.delete();
				g.removeMove(m);
			}
			
			q.getCurrentGame().setGameStatus(GameStatus.Running);
			return true;
		}
	}
	
	/**
	 * For EnterReplayModeFeature
	 * 
	 * Switches the model to be in replay mode
	 * 
	 * @author DariusPi
	 * 
	 * @param q
	 */
	public void initReplay(Quoridor q) {
		q.getCurrentGame().setGameStatus(GameStatus.Replay);
	}
	
	/**
	 * For checkIfGameWon feature
	 * 
	 * Checks whether the move results in a victory
	 * 
	 * @author DariusPi
	 * 
	 * @param q
	 * @return true if game is over, false if not
	 */
	public boolean checkResult(Quoridor q) {
		PlayerPosition pw=q.getCurrentGame().getCurrentPosition().getWhitePosition();
		PlayerPosition pb=q.getCurrentGame().getCurrentPosition().getBlackPosition();
		if (pw.getTile().getColumn()==9) {
			q.getCurrentGame().setGameStatus(GameStatus.WhiteWon);
			return true;
		}
		else if (pb.getTile().getColumn()==1) {
			q.getCurrentGame().setGameStatus(GameStatus.BlackWon);
			return true;
		}
		else {
			q.getCurrentGame().setGameStatus(GameStatus.Running);
			return false;
		}
	}
	
	
	/**
	 * For SaveGame Feature
	 * 
	 * Saves the current moves including possibly a finish string 0-1 indicating a finished game
	 * 
	 * @author DariusPi 
	 * @param q
	 * @param filename
	 * @param finished
	 * @throws IOException
	 */
	public void saveMoves(Quoridor q, String filename, boolean finished) throws IOException {
		File file=new File(filename);		//Our file created
		
		file.setWritable(true);
		file.createNewFile();
		PrintWriter writer;
		
		writer = new PrintWriter(file, "UTF-8");
		int mvn=0;
		for (Move mov :q.getCurrentGame().getMoves()) {
			int row=mov.getTargetTile().getRow();
			int col=mov.getTargetTile().getColumn();
			String Column = "";  //needs conversion to letter, using switch cases
			switch(col) {
				case 1:  Column = "a";
					break;
				
				case 2:  Column = "b";
					break;
					
				case 3:   Column = "c";
					break;
					
				case 4:   Column = "d";
					break;
					
				case 5:  Column = "e";
					break;
					
				case 6:  Column = "f";
					break;
					
				case 7:  Column = "g";
					break;
					
				case 8:  Column = "h";
					break;
					
				case 9: Column = "i";
					
			}
			if (mvn%2==0) {		
				writer.print(Column+row);
			}
			else {
				writer.print(" "+Column+row);
			}
			
			if (mov instanceof WallMove) {
				writer.print(((WallMove)mov).getWallDirection().toString().toLowerCase().charAt(0));
			}
			
			if (mvn%2!=0) {		
				writer.println();
			}
			mvn++;
		}
		if (finished) {
			if (mvn%2==0) {
				writer.print("0-1");	//indicates terminated game
			}
			else {
				writer.print(" 0-1");	//indicates terminated game
			}
		}
		writer.close();		
	}
	
	/**
	 * For loadGame feature
	 * 
	 * Method that loads moves into model from file, if any are invalid then return false, make sure no moves are loaded in this case
	 * 
	 * @author DariusPi
	 * @param q
	 * @param filename
	 * @return 0 for valid and unfinished, 1 for finished and valid, -1 for invalid
	 * @throws Exception
	 */
	public int loadMoves(Quoridor q, String filename) throws Exception {	//three states, valid, invalid, finished
		int finished=0;
		int wwalls=0;
		int bwalls=10;
		if (q.getCurrentGame()==null) {
			initGame(q); //maybe start game as well
			addWalls();
		}
		
		//initialize scanning on file with move data
		//assume that file is well formatted even if invalid
		File file = new File(filename);
		Scanner fileSC = null;
		try {
			fileSC = new Scanner(file);
		} catch (FileNotFoundException e) {
			throw new Exception("File does not exist!");
		}
			
		//String line= fileSC.nextLine();	
		while (fileSC.hasNextLine()) {
			String line= fileSC.nextLine();
			String []movs=line.split(" ");
			for (String mov: movs) {
				Game currentGame=q.getCurrentGame();
				GamePosition curr= currentGame.getCurrentPosition();
				GamePosition next;
				Player player=curr.getPlayerToMove();
			
				int valid=checkMove(mov,player.hasGameAsWhite());
				if(valid==-1){
					fileSC.close();
					return -1;
				}
				else if (valid==1) {
					if (!checkIfLoadGameValid(q)) {
						fileSC.close();
						return -1;
					}
					else {
						fileSC.close();
						return 1;
					}
				}
				int row=Character.getNumericValue(mov.charAt(1))-1;
				char Column=mov.charAt(0);
				int col=0;  //needs conversion to letter, using switch cases
				switch(Column) {
					case 'a':  col = 0;
						break;
					
					case 'b':  col = 1;
						break;
						
					case 'c':   col =2;
						break;
						
					case 'd':   col =3;
						break;
						
					case 'e':  col = 4;
						break;
						
					case 'f':  col = 5;
						break;
						
					case 'g':  col = 6;
						break;
						
					case 'h':  col = 7;
						break;
						
					case 'i': col = 8;
						
				}
				if (mov.length()==3) {
					Character d=mov.charAt(2);
					String dir;
					int id;
					if (d=='h') {
						dir="horizontal";
					}
					else {
						dir="vertical";
					}
					
					if (player.hasGameAsWhite()) {
						if (wwalls==10) {	//trying to place 11 walls
							fileSC.close();
							return -1;
						}
						id=wwalls;
						wwalls++;
					}
					else {
						if (wwalls==20) {	//trying to place 11 walls
							fileSC.close();
							return -1;
						}
						id=bwalls;
						bwalls++;
					}
					
					if (!dropWall(col,row,dir,id)) {
						fileSC.close();
						return -1;						//if no path
					}
				}
				else {
					if (player.hasGameAsWhite()) {
						PlayerPosition pos=new PlayerPosition(player, q.getBoard().getTile(row*9+col));
						PlayerPosition pos2=new PlayerPosition(currentGame.getBlackPlayer(),curr.getBlackPosition().getTile());
						next = new GamePosition(currentGame.numberOfPositions(), pos, pos2, currentGame.getBlackPlayer(), currentGame);
						
						for (Wall w : curr.getBlackWallsOnBoard()) {
							next.addBlackWallsOnBoard(w);
						}
						for (Wall w : curr.getWhiteWallsOnBoard()) {
							next.addWhiteWallsOnBoard(w);
						}
						for (Wall w : curr.getBlackWallsInStock()) {
							next.addBlackWallsInStock(w);
						}
						for (Wall w : curr.getWhiteWallsInStock()) {
							next.addWhiteWallsInStock(w);
						}
						currentGame.setCurrentPosition(next);
						currentGame.addMove(new StepMove(currentGame.numberOfPositions()-2, 0, player, q.getBoard().getTile(row*9+col),currentGame));
					}
					else {
						PlayerPosition pos=new PlayerPosition(player, q.getBoard().getTile(row*9+col));
						PlayerPosition pos2=new PlayerPosition(currentGame.getWhitePlayer(),curr.getWhitePosition().getTile());
						next = new GamePosition(currentGame.numberOfPositions(), pos2, pos, currentGame.getWhitePlayer(), currentGame);
						for (Wall w : curr.getBlackWallsOnBoard()) {
							next.addBlackWallsOnBoard(w);
						}
						for (Wall w : curr.getWhiteWallsOnBoard()) {
							next.addWhiteWallsOnBoard(w);
						}
						for (Wall w : curr.getBlackWallsInStock()) {
							next.addBlackWallsInStock(w);
						}
						for (Wall w : curr.getWhiteWallsInStock()) {
							next.addWhiteWallsInStock(w);
						}
						currentGame.setCurrentPosition(next);
						currentGame.addMove(new StepMove(currentGame.numberOfPositions()-2, 1, player, q.getBoard().getTile(row*9+col),currentGame));
					}
				}
				if (valid==2) {
					fileSC.close();
					return 1;
				}
			}
		}
		fileSC.close();
		if (!checkIfLoadGameValid(q)) {
			return -1;
		}
		q.getCurrentGame().setGameStatus(GameStatus.ReadyToStart);
		return finished;	
	}
	
	/**
	 * Helper method for loadMoves, checks if move is in correct format or if finish game string
	 * 
	 * @author DariusPi
	 * 
	 * @param mov
	 * @return 0 if valid, -1 if invalid, 1 if finished string, 2 if finishing move
	 */
	public int checkMove(String mov, boolean white) {
		if (mov.length()>3) {
			return -1;
		}
		if (mov.compareTo("0-1")==0) {		//in case of timeout or resigned or drawn games
			return 1;
		}
		if ((mov.charAt(0)>='a')&&(mov.charAt(0)<='i')){
			int row=Character.getNumericValue(mov.charAt(1));
			if (row>=1&&row<=9) {
				if (mov.length()==3) {
					if ((mov.charAt(2)=='v')||mov.charAt(2)=='h') {
						return 0;
					}
				}
				else {
					if (white&&(mov.charAt(0)=='i')) {
						return 2;
					}
					else if(!white &&mov.charAt(0)=='a') {
						return 2;
					}
					return 0;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Previous wrapper for check if path exists, deprecated and should not be used
	 * @deprecated
	 * @author DariusPi
	 * @param q
	 * @return
	 */
	public boolean checkPath(Quoridor q) {
		Game g=q.getCurrentGame();
		PawnBehavior pb=new PawnBehavior("invalid");
		pb.setCurrentGame(g);
		Player p;
		Player real=g.getCurrentPosition().getPlayerToMove();
		Tile t;
		String forward, backward;
		boolean white;
		int i=g.getCurrentPosition().getId();
		int f; int b;
		if (!g.getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
			forward="right";
			backward="left";
			t=g.getCurrentPosition().getWhitePosition().getTile();
			white =true;
			f=9;
			b=1;
			p=g.getWhitePlayer();
		}
		else {
			backward="right";
			forward="left";
			t=g.getCurrentPosition().getBlackPosition().getTile();
			white =false;
			f=1;
			b=9;
			p=g.getBlackPlayer();
		}
		pb.setPlayer(p);
		pb.change();
		g.getCurrentPosition().setPlayerToMove(p);
		
		ArrayList<Tile> list=new ArrayList<Tile>();
		list.add(getTile(q,white));
		
		Boolean result=checker(q,pb,forward,backward,p,white,f,b,"",list);
		g.setCurrentPosition(g.getPosition(i));
		g.getCurrentPosition().setPlayerToMove(real);
		if (white) {
			g.getCurrentPosition().getWhitePosition().setTile(t);
		}
		else {
			g.getCurrentPosition().getBlackPosition().setTile(t);
		}
		int j=g.getPositions().size()-i-1; //number of positions to delete
		for (int k=0;k<j;k++) {
			g.getPosition(g.getPositions().size()-1).delete();
			
			Move m=g.getMove(g.getMoves().size()-1);
			m.delete();
			g.removeMove(m);
		}
		return result;
	}
	
	/**
	 * Previous method to check if path exists, depreciated and should not be used
	 * @deprecated
	 * @author DariusPi
	 * 
	 * @param q
	 * @param pb
	 * @param forw
	 * @param back
	 * @param p
	 * @param white
	 * @param f
	 * @param b
	 * @param side
	 * @param list
	 * @return
	 */
	public boolean checker(Quoridor q,PawnBehavior pb, String forw, String back,Player p,boolean white,int f,int b,String side,ArrayList<Tile> list) {
		Tile t=getTile(q,white);
		System.out.print(" on tile"+t.getRow()+t.getColumn());
		int dist=-1;
		if (white) {
			dist=1;
		}
		if (!list.contains(q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()-1+dist))) {
			pb.change();
			if(pb.isLegalStep(forw)) {
				if (white) {
					q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()-1+dist)));
				}
				else {
					q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()-1+dist)));
				}
				System.out.println("FORWARD");
				list.add(getTile(q,white));
				Tile t1=getTile(q,white);
				q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
				if (t1.getColumn()==f) {
					System.out.println("FOUND	 path ");
					pb.change();
					//pb.move(back);
					if (white) {
						q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1-dist)));
					}
					else {
						q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1-dist)));
					}
					q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
					return true;
				}
				else {
					if (checker(q,pb,forw,back,p,white,f,b,forw,list)) {
						pb.change();
						if (white) {
							q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1-dist)));
						}
						else {
							q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1-dist)));
						}
						q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
						return true;
					}
					else {
						pb.change();
						if (white) {
							q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1-dist)));
						}
						else {
							q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1-dist)));
						}
						System.out.println("BACKWARD");
						q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
					}
				}
			}
		}
		
		
		if ((t.getRow()!=1)&&(!list.contains(q.getBoard().getTile((t.getRow()-2)*9+t.getColumn()-1)))) {
			pb.change();
			if (pb.isLegalStep("up")) {
				if (white) {
					q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t.getRow()-2)*9+t.getColumn()-1)));
				}
				else {
					q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t.getRow()-2)*9+t.getColumn()-1)));
				}
				
				System.out.println("UP");
				list.add(getTile(q,white));
				Tile t1=getTile(q,white);
				q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
				if (checker(q,pb,forw,back,p,white,f,b,"up",list)) {
					pb.change();
					//pb.move("down");
					if (white) {
						q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow())*9+t1.getColumn()-1)));
					}
					else {
						q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow())*9+t1.getColumn()-1)));
					}
					return true;
				}
				else {
					pb.change();
					if (white) {
						q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow())*9+t1.getColumn()-1)));
					}
					else {
						q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow())*9+t1.getColumn()-1)));
					}
					System.out.println("DOWN");
					q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
				}
				
			}
		}
		
		if ((t.getRow()!=9)&&(!list.contains(q.getBoard().getTile((t.getRow())*9+t.getColumn()-1)))) {
			pb.change();
			if (pb.isLegalStep("down")) {
				if (white) {
					q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t.getRow())*9+t.getColumn()-1)));
				}
				else {
					q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t.getRow())*9+t.getColumn()-1)));
				}
				
				list.add(getTile(q,white));
				System.out.println("DOWN");
				q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
				Tile t1=getTile(q,white);
				if (checker(q,pb,forw,back,p,white,f,b,"down",list)) {
					pb.change();
					if (white) {
						q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-2)*9+t1.getColumn()-1)));
					}
					else {
						q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-2)*9+t1.getColumn()-1)));
					}
					return true;
				}
				else {
					pb.change();
					if (white) {
						q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-2)*9+t1.getColumn()-1)));
					}
					else {
						q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-2)*9+t1.getColumn()-1)));
					}
					
					System.out.println("UP");
					q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
				}
				
			}
		}
		
		if ((t.getColumn()!=b)&&(!list.contains(q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()-1-dist)))) {
			pb.change();
			if (pb.isLegalStep(back)) {
				if (white) {
					q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()-1-dist)));
				}
				else {
					q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()-1-dist)));
				}
				
				list.add(getTile(q,white));
				System.out.println("BACKWARD");
				q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
				Tile t1=getTile(q,white);
				if (checker(q,pb,forw,back,p,white,f,b,back,list)) {
					pb.change();
					if (white) {
						q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1+dist)));
					}
					else {
						q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1+dist)));
					}
					return true;
				}
				else {
					pb.change();
					if (white) {
						q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1+dist)));
					}
					else {
						q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(p,q.getBoard().getTile((t1.getRow()-1)*9+t1.getColumn()-1+dist)));
					}
					System.out.println("FORWARD");
					q.getCurrentGame().getCurrentPosition().setPlayerToMove(p);
				}
			}
		}
		return false;
		
	}
	
	public Tile getTile(Quoridor q,boolean white) {
		Tile t;
		if (white) {
			t=q.getCurrentGame().getCurrentPosition().getWhitePosition().getTile();
		}
		else {
			t=q.getCurrentGame().getCurrentPosition().getBlackPosition().getTile();
		}
		return t;
	}
	
	public void resignGame(Quoridor q) {
		if (q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
			q.getCurrentGame().setGameStatus(GameStatus.BlackWon);
		}
		else {
			q.getCurrentGame().setGameStatus(GameStatus.WhiteWon);
		}
	}
	
	/**
	 * In replay mode, this method is used to traverse to the previous move in the game.
	 * @param q
	 * @return boolean
	 */
	public boolean stepBackward(Quoridor q) {
		int cur=q.getCurrentGame().getCurrentPosition().getId();
		if (cur==0) {	//if at beginning, do nothing
			return false;
		}
		else {
			q.getCurrentGame().setCurrentPosition(q.getCurrentGame().getPosition(cur-1));
			return true;
		}
	}
	
	/**
	 * In replay mode, this method is used to traverse to the next move in the game.
	 * @param q
	 * @return boolean
	 */
	public boolean stepForward(Quoridor q) {
		int cur=q.getCurrentGame().getCurrentPosition().getId();
		if (cur==q.getCurrentGame().numberOfPositions()-1) {	//if at final, do nothing
			return false;
		}
		else {
			q.getCurrentGame().setCurrentPosition(q.getCurrentGame().getPosition(cur+1));
			return true;
		}
	}
	
	/**
	 * In replay mode, this method is used to traverse to the very beginning of the game.
	 * @author Saifullah9
	 * @param q
	 * @return boolean
	 */
	public boolean jumpToStart(Quoridor q) {
		int cur = q.getCurrentGame().getCurrentPosition().getId();
		if(cur == 0) {
			return false;
		}else {
			q.getCurrentGame().setCurrentPosition(q.getCurrentGame().getPosition(0));
			return true;
		}
	}
	
	/**
	 * In replay mode, this method is used to traverse to the very end of the game.
	 * @author Saifullah9
	 * @param q
	 * @return boolean
	 */
	public boolean jumpToFinal(Quoridor q) {
		int cur = q.getCurrentGame().getCurrentPosition().getId();
		if(cur == q.getCurrentGame().numberOfPositions()-1) {
			return false;
		}else {
			q.getCurrentGame().setCurrentPosition(q.getCurrentGame().getPosition(q.getCurrentGame().numberOfPositions()-1));
			return true;
		}
	}
	
	
	
	/**
	 * * For CheckifPathExists feature
	 * 
	 * @author louismollick
	 * @return String result : white, black, none, both
	 * @param boolean forGherkin : sets the targets to rows instead of cols
	 */
	public String checkPathExistence(boolean forGherkin) {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		String result = "none";
		
		// Create graph representing the current position (no edges where there are walls)
		QuoridorGraph graph = new QuoridorGraph(quoridor.getCurrentGame().getCurrentPosition());
		
		// Check path for White
		boolean whitePath = graph.checkPathForPlayer(true, forGherkin);
		
		// Check path for Black
		boolean blackPath = graph.checkPathForPlayer(false, forGherkin);
		
		if (whitePath && blackPath) {
			result = "both";
		} else if (whitePath) {
			result = "white";
		} else if (blackPath) {
			result = "black";
		}
		
		System.out.println("PATH RESULT : " + result);
		return result;
	}
}


