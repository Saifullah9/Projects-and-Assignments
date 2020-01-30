package ca.mcgill.ecse223.quoridor.features;

import static ca.mcgill.ecse223.quoridor.model.Direction.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



import java.io.File;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import javax.swing.Timer;


import ca.mcgill.ecse223.quoridor.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.model.Board;
import ca.mcgill.ecse223.quoridor.model.Direction;
import ca.mcgill.ecse223.quoridor.model.Game;
import ca.mcgill.ecse223.quoridor.model.Game.GameStatus;
import ca.mcgill.ecse223.quoridor.model.Game.MoveMode;
import ca.mcgill.ecse223.quoridor.model.GamePosition;
import ca.mcgill.ecse223.quoridor.model.Move;
import ca.mcgill.ecse223.quoridor.model.Player;
import ca.mcgill.ecse223.quoridor.model.PlayerPosition;
import ca.mcgill.ecse223.quoridor.model.Quoridor;
import ca.mcgill.ecse223.quoridor.model.StepMove;
import ca.mcgill.ecse223.quoridor.model.Tile;
import ca.mcgill.ecse223.quoridor.model.User;
import ca.mcgill.ecse223.quoridor.model.Wall;
import ca.mcgill.ecse223.quoridor.model.WallMove;

import ca.mcgill.ecse223.quoridor.view.QuoridorPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import ca.mcgill.ecse223.quoridor.controller.GameController;
import ca.mcgill.ecse223.quoridor.controller.PawnBehavior;

public class CucumberStepDefinitions {
	
	/*private variables used for testing GUI functions*/
	private String create;
	private int col;
	private int row;
	private String dir;
	private boolean wvalid;
	private boolean resvalid;
	private String pathExistsFor;
	private Player starter;
	int index;
	private static Direction Direction;
	private boolean invalidmove; //boolean for invalid move
	private boolean eachgamemovevalid; //boolean for each and every move is valid
	private int status;
	private ArrayList<Player> loadPlayers;

	// ***********************************************
	// Background step definitions
	// ***********************************************
	
	@Given("^The game is not running$")
	public void theGameIsNotRunning() {
		initQuoridorAndBoard();
		loadPlayers=createUsersAndPlayers("user1", "user2");
	}

	@Given("^The game is running$")
	public void theGameIsRunning() {
		initQuoridorAndBoard();
		ArrayList<Player> createUsersAndPlayers = createUsersAndPlayers("user1", "user2");
		createAndStartGame(createUsersAndPlayers);
	}

	@And("^It is my turn to move$")
	public void itIsMyTurnToMove() throws Throwable {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Player currentPlayer = quoridor.getCurrentGame().getWhitePlayer();
		QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().setPlayerToMove(currentPlayer);
	}

	@Given("The following walls exist:")
	public void theFollowingWallsExist(io.cucumber.datatable.DataTable dataTable) {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		List<Map<String, String>> valueMaps = dataTable.asMaps();
		// keys: wrow, wcol, wdir
		Player[] players = { quoridor.getCurrentGame().getWhitePlayer(), quoridor.getCurrentGame().getBlackPlayer() };
		int playerIdx = 0;
		int wallIdxForPlayer = 0;
		for (Map<String, String> map : valueMaps) {
			Integer wrow = Integer.decode(map.get("wrow"));
			Integer wcol = Integer.decode(map.get("wcol"));
			// Wall to place
			// Walls are placed on an alternating basis wrt. the owners
			//Wall wall = Wall.getWithId(playerIdx * 10 + wallIdxForPlayer);
			Wall wall = players[playerIdx].getWall(wallIdxForPlayer); // above implementation sets wall to null

			String dir = map.get("wdir");

			Direction direction;
			switch (dir) {
			case "horizontal":
				direction = Direction.Horizontal;
				break;
			case "vertical":
				direction = Direction.Vertical;
				break;
			default:
				throw new IllegalArgumentException("Unsupported wall direction was provided");
			}
			new WallMove(0, 1, players[playerIdx], quoridor.getBoard().getTile((wrow - 1) * 9 + wcol - 1), quoridor.getCurrentGame(), direction, wall);
			if (playerIdx == 0) {
				quoridor.getCurrentGame().getCurrentPosition().removeWhiteWallsInStock(wall);
				quoridor.getCurrentGame().getCurrentPosition().addWhiteWallsOnBoard(wall);
			} else {
				quoridor.getCurrentGame().getCurrentPosition().removeBlackWallsInStock(wall);
				quoridor.getCurrentGame().getCurrentPosition().addBlackWallsOnBoard(wall);
			}
			wallIdxForPlayer = wallIdxForPlayer + playerIdx;
			playerIdx++;
			playerIdx = playerIdx % 2;
		}
		System.out.println();

	}
	
	/**
	 * @author louismollick
	 * @throws Exception
	 */
	@Given("I do not have a wall in my hand")
	public void iDoNotHaveAWallInMyHand() throws Exception{
		QuoridorPage view = QuoridorApplication.getQuoridorView();
		if (view == null) throw new Exception("View doesn't exist");
		
		view.setHeldComponent(null);
	}
	
	/**
	 * @author louismollick
	 * @throws Exception
	 */
	@And("^I have a wall in my hand over the board$")
	public void givenIHaveAWallInMyHandOverTheBoard() throws Exception {
		QuoridorPage view = QuoridorApplication.getQuoridorView();
		if (view == null) throw new Exception("View doesn't exist");
		
		Player p = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove();
		if (p.hasGameAsBlack()) {
			view.setHeldComponentToRandomWall("black");
		}
		else if (p.hasGameAsWhite()) {
			view.setHeldComponentToRandomWall("white");
		}
		else {
			throw new Exception("Current player has no color!");
		}
		
	}
	
	@Given("^A new game is initializing$")
	public void aNewGameIsInitializing() throws Throwable {
		initQuoridorAndBoard();
		ArrayList<Player> players = createUsersAndPlayers("user1", "user2");
		new Game(GameStatus.Initializing, MoveMode.PlayerMove, QuoridorApplication.getQuoridor());
	}

	// ***********************************************
	// Scenario and scenario outline step definitions
	// ***********************************************
	
	/**
	 * @author louismollick
	 * @throws Throwable
	 */
	@Given("A wall move candidate exists with {string} at position \\({int}, {int})")
	public void givenAWallMoveCandidateExistsAtPos(String sdir, int row, int col) throws Throwable{
		Direction dir;
		if(sdir.equals("vertical")) dir = Direction.Vertical;
		else dir = Direction.Horizontal;
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		GamePosition pos = game.getCurrentPosition();
		Player player = pos.getPlayerToMove();
		WallMove wmc = game.getWallMoveCandidate();
		
		Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(row, col));
		// Check if the WallMoveCandidate belongs to the current player
		if(wmc != null && player.indexOfWall(wmc.getWallPlaced()) != -1) {
			// Set the WallMoveCandidate's attributes to those specified in input
			if(wmc.getWallDirection() != dir) wmc.setWallDirection(dir);
			if(wmc.getTargetTile().getRow() != row || wmc.getTargetTile().getColumn() != col)
				wmc.setTargetTile(target);
		} else { // If no WallMoveCandidate exists or it is other player's, make a new one with input
			Wall w = pos.getWhiteWallsInStock(1);
			int moveNum = game.numberOfMoves();
			int roundNum = 1;
			if(moveNum != 0) {
				roundNum = game.getMove(moveNum-1).getRoundNumber();
			}
			WallMove wm = new WallMove(moveNum, roundNum, player, target, game, dir, w);
			game.setWallMoveCandidate(wm);
		}
	}
	
	/**
	 * @author louismollick
	 */
	@When("I try to flip the wall")
	public void iTryToFlipTheWall() throws Throwable{
		GameController gc = new GameController();
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		gc.rotateWall();
	}
	
	/**
	 * @author louismollick
	 */
	@Then("The wall shall be rotated over the board to {string}")
	public void theWallShallBeRotatedOverTheBoardTo(String sdir){
		Direction dir;
		if(sdir.equals("vertical")) dir = Direction.Vertical;
		else dir = Direction.Horizontal;
		assertEquals(dir, QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate().getWallDirection());
	}
	
	/**
	 * @author louismollick
	 */
	@And("A wall move candidate shall exist with {string} at position \\({int}, {int})")
	public void thenWallMoveCandidateExistsAtPos(String snewdir, int row, int col) throws Throwable{
		Direction newdir;
		if(snewdir.equals("vertical")) newdir = Direction.Vertical;
		else newdir = Direction.Horizontal;
		WallMove w = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate();
		boolean b = (w.getWallDirection().equals(newdir) && w.getTargetTile().getRow() == row &&
				w.getTargetTile().getColumn() == col);
		assertEquals(true, b);
	}
	
	/**
	 * @author louismollick
	 */
	@Given("I have more walls on stock")
	public void iHaveMoreWallsOnStock() throws Throwable{
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		Player player = game.getCurrentPosition().getPlayerToMove();
		if(!player.hasWalls()) {
			Wall w = new Wall(1, player);
			game.getCurrentPosition().addWhiteWallsInStock(w);
		}
	}
	
	/**
	 * @author louismollick
	 */
	@When("I try to grab a wall from my stock")
	public void iTryToGrabAWallFromMyStock() throws Throwable{
		GameController gc = new GameController();
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		
		if(gc.grabWall()) {
			// If grabWall successful, emulate player picking up wall in 
			// view also, such that tests can be conducted on the view
			QuoridorPage view = QuoridorApplication.getQuoridorView();
			if (view == null) throw new Exception("View doesn't exist");
			
			Player p = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove();
			if (p.hasGameAsBlack()) {
				view.setHeldComponentToRandomWall("black");
			}
			else if (p.hasGameAsWhite()) {
				view.setHeldComponentToRandomWall("white");
			}
		}
	}
	
	/**
	 * @author louismollick
	 */
	@Then("I shall have a wall in my hand over the board")
	public void thenIHaveAWallInMyHandOverTheBoard() throws Throwable{
		QuoridorPage view = QuoridorApplication.getQuoridorView();
		if (view == null) throw new Exception("View doesn't exist");
		assertEquals(view.hasHeldWall(), true);
	}
	
	/**
	 * @author louismollick
	 */
	@And("The wall in my hand shall disappear from my stock")
	public void theWallInMyHandShallDisappearFromMyStock() throws Throwable{
		Game g = QuoridorApplication.getQuoridor().getCurrentGame();
		// Get the index of the wallMove candidate. Verify it isn't in player stock
		boolean b = (g.getCurrentPosition().indexOfWhiteWallsInStock(g.getWallMoveCandidate().getWallPlaced()) == -1);
		assertEquals(false, b);
	}
	
	/**
	 * @author louismollick
	 */
	@And("A wall move candidate shall be created at initial position")
	public void aWallMoveCandidateShallBeCreatedAtInitalPosition() throws Throwable{
		Quoridor q = QuoridorApplication.getQuoridor();
		Board b = q.getBoard();
		Game game = q.getCurrentGame();
		WallMove wmc = game.getWallMoveCandidate();
		assertEquals(Direction.Vertical, wmc.getWallDirection());
		assertEquals(b.getTile(40).getRow(), wmc.getTargetTile().getRow());
		assertEquals(b.getTile(40).getColumn(), wmc.getTargetTile().getColumn());
	}
	
	/**
	 * @author louismollick
	 */
	@Given("I have no more walls on stock")
	public void iHaveNoMoreWallsOnStock() throws Throwable{
		Player p = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();
		GamePosition pos = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
		while(pos.hasWhiteWallsInStock()){
			pos.removeWhiteWallsInStock(pos.getWhiteWallsInStock(0));
		}
	}
	
	/**
	 * @author louismollick
	 */
	@Then("I shall be notified that I have no more walls")
	public void iShouldBeNotifiedThatIHaveNoMoreWalls() {
		// If all walls are gone from his stock in the view, the player already sees
		// he has no more walls.
	}
	
	/**
	 * @author louismollick
	 */
	@And ("I shall have no walls in my hand")
	public void iShallHaveNoWallsInMyHand() throws Exception{
		QuoridorPage view = QuoridorApplication.getQuoridorView();
		if (view == null) throw new Exception("View doesn't exist");
		
		assertEquals(false, view.hasHeldWall());
	}
	
	/**
	 * @author Saifullah
	 */
	@Given("The wall move candidate with {string} at position \\({int}, {int}) is valid")
	public void givenAWallMoveCandidateExistsAndValidAtPos(String sdir, int row, int col) throws Throwable{
		Direction dir;
		GameController gc = new GameController();
		if(sdir.equals("vertical")) dir = Direction.Vertical;
		else dir = Direction.Horizontal;
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		GamePosition pos = game.getCurrentPosition();
		Player player = pos.getPlayerToMove();
		starter=player;
		Quoridor q=QuoridorApplication.getQuoridor();
		Tile t=q.getBoard().getTile(row*9+col);
		WallMove wmc=new WallMove (0, 0, player, t, game, dir, player.getWall(5));  
		game.setWallMoveCandidate(wmc);
		wmc.setTargetTile(t);
		
		Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(row ,col));
		resvalid=gc.valWallPosition(col-1, row-1, sdir);
	}
	
	/**
	 * @author Saifullah
	 */
	@Given("The wall move candidate with {string} at position \\({int}, {int}) is invalid")
	public void givenAWallMoveCandidateExistsAndNotValidAtPos(String sdir, int row, int col) throws Throwable{
		Direction dir;
		GameController gc = new GameController();
		if(sdir.compareTo("vertical")==0) dir = Direction.Vertical;
		else dir = Direction.Horizontal;
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		GamePosition pos = game.getCurrentPosition();
		Player player = pos.getPlayerToMove();
		player.setGameAsWhite(game);
		starter=player;
		Quoridor q=QuoridorApplication.getQuoridor();
		Tile t=q.getBoard().getTile(row*9+col);
		WallMove wmc=new WallMove (0, 0, player, t, game, dir, player.getWall(5));  
		game.setWallMoveCandidate(wmc);
		Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(row ,col));
		
				resvalid=gc.valWallPosition(col-1, row-1, sdir);
	}
	
	/**
	 * @author Saifullah
	 */
	@And("My move shall be completed")
	public void MoveCompleted() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Player currentPlayer = quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove();
		boolean b = false;
		if(currentPlayer != quoridor.getCurrentGame().getWhitePlayer()) {
			b = true;
		}
		assertEquals(true, b);	
	}
	
	/**
	 * @author Saifullah
	 * @throws Exception 
	 */
	@But("No wall move shall be registered with {string} at position \\({int}, {int})")
	public void WallMoveNotRegistered(String sdir, int row, int col) throws Exception {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		WallMove wmc = game.getWallMoveCandidate();
		Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(row ,col));
		assertEquals(0,game.getMoves().size());
	
	}
		
	/**
	 * @author Saifullah
	 * @throws Exception 
	 */
	@Then("A wall move shall be registered with {string} at position \\({int}, {int})")
	public void WallMoveIsRegistered(String sdir, int row, int col) throws Exception {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		WallMove wmc = game.getWallMoveCandidate();
		//Tile target = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(row ,col));
		//assertEquals(true, wmc.getWallPlaced());
		WallMove pre= (WallMove)game.getMove(game.getMoves().size()-1);
		//pre.getTargetTile().getRow()
		assertEquals(sdir, pre.getWallDirection().toString().toLowerCase());
		//assertEquals(pre.getTargetTile().getColumn(), col);
		//assertEquals(pre.getTargetTile().getRow(), row);
	}
	
	
	/**
	 * @author Saifullah
	 */
	@And("It shall not be my turn to move")
	public void NotMyTurnToMove() {		
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Player currentPlayer = quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove();
		boolean b = false;
		if(currentPlayer != quoridor.getCurrentGame().getWhitePlayer()) {
			b = true;
		}
		assertEquals(true, b);
	}
	
	/**
	 * @author Saifullah
	 */
	@And("I shall not have a wall in my hand")
	public void iShallNotHaveWallInMyHand() {
		assertEquals(false, QuoridorApplication.getQuoridor().getCurrentGame().hasWallMoveCandidate());
	}
	
	
	/**
	 * @author Saifullah
	 */
	@And("The wall candidate is at the {string} edge of the board")
	public void WallCandidateAtSide(String side) {
		WallMove wallCandidate = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate();
		Tile DesignatedTile = wallCandidate.getTargetTile();
		boolean b = false;
		if(side.equals("left") && DesignatedTile.getColumn() == 1) {
			b = true;
		}
		
		if(side.equals("right") && DesignatedTile.getColumn() == 8) {
			b = true;
		}
		
		if(side.equals("up") && DesignatedTile.getRow() == 1) {
			b = true;
		}
		
		if(side.equals("down") && DesignatedTile.getRow() == 8) {
			b = true;
		}
		
		//assertEquals(true, b);		
	}
	
	/**
	 * @author Saifullah
	 */
	@And("The wall candidate is not at the {string} edge of the board")
	public void WallCandidateNotAtSide(String side) {
		WallMove wallCandidate = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate();
		Tile DesignatedTile = wallCandidate.getTargetTile();
		boolean b = false;
		if(side.equals("left") && DesignatedTile.getColumn() == 1) {
			b = true;
		}
		
		if(side.equals("right") && DesignatedTile.getColumn() == 8) {
			b = true;
		}
		
		if(side.equals("up") && DesignatedTile.getRow() == 1) {
			b = true;
		}
		
		if(side.equals("down") && DesignatedTile.getRow() == 8) {
			b = true;
		}
		
		assertEquals(false, b);		
	}
	
	/**
	 * @author Saifullah
	 */
	@Then("The wall shall be moved over the board to position \\({int}, {int})")
	public void WallIsMovedToPosition(int nrow, int ncol) {
		WallMove wallCandidate = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate();
		Tile DesignatedTile = wallCandidate.getTargetTile();
		assertEquals(DesignatedTile.getRow(), nrow);
		assertEquals(DesignatedTile.getColumn(), ncol);			
	}
	
	/**
	 * @author Saifullah
	 */
	@And("It shall be my turn to move")
	public void IsMyTurnToMove() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Player currentPlayer = quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove();
		/*boolean b = false;
		if(currentPlayer == quoridor.getCurrentGame().getWhitePlayer()) {
			b = true;
		}*/
		if (starter.hasGameAsWhite()) {
			assertTrue(currentPlayer.hasGameAsWhite());
		}
		else {
			assertTrue(currentPlayer.hasGameAsBlack());
		}
		//assertEquals(starter, currentPlayer);
	}
	
	/**
	 * @author Saifullah
	 */
	@When("I release the wall in my hand")
	public void iReleaseTheWall() throws Throwable{
		GameController gc = new GameController();
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		WallMove wmc = game.getWallMoveCandidate();
		Tile target = wmc.getTargetTile();
		int id=wmc.getWallPlaced().getId();
		//int id = wmc.getPlayer().getWall(getIndex(target.getColumn(),target.getRow())).getId();
		Direction direction = wmc.getWallDirection();
		String dir;
		if(direction.compareTo(Direction.Horizontal) == 0) {
			dir =  "horizontal";
		}else {
			dir = "vertical";
		}
	
		if (resvalid) {
			gc.dropWall(target.getColumn(),target.getRow(),dir, id);
			game.addMove(wmc);
			game.setWallMoveCandidate(null);
		}
	}
	
	/**
	 * @author Saifullah
	 */
	@When("I try to move the wall {string}")
	public void iMoveWall(String side) {
		Quoridor q= QuoridorApplication.getQuoridor();
		GameController gc = new GameController();
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		WallMove w=game.getWallMoveCandidate();
		Tile t=w.getTargetTile();
		if (side.compareTo("left")==0) {
			if (t.getColumn()!=1) {
				if (w.getWallDirection().toString().compareTo("Horizontal")==0) {
					if (t.getColumn()!=2) {
						game.getWallMoveCandidate().setTargetTile(q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()-2));
					}
				}
				else {
					game.getWallMoveCandidate().setTargetTile(q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()-2));
				}
			}
		}
		else if (side.compareTo("right")==0) {
			if (t.getColumn()!=9) {
				if (w.getWallDirection().toString().compareTo("Horizontal")==0) {
					if (t.getColumn()!=8) {
						game.getWallMoveCandidate().setTargetTile(q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()));
					}
				}
				else {
					game.getWallMoveCandidate().setTargetTile(q.getBoard().getTile((t.getRow()-1)*9+t.getColumn()));
				}
			}
		}
		else if (side.compareTo("up")==0) {
			if (t.getRow()!=1) {
				if (w.getWallDirection().toString().compareTo("Vertical")==0) {
					if (t.getColumn()!=2) {
						game.getWallMoveCandidate().setTargetTile(q.getBoard().getTile((t.getRow()-2)*9+t.getColumn()-1));
					}
				}
				else {
					game.getWallMoveCandidate().setTargetTile(q.getBoard().getTile((t.getRow()-2)*9+t.getColumn()-1));
				}
			}
		}
		else {
			if (t.getRow()!=9) {
				if (w.getWallDirection().toString().compareTo("Vertical")==0) {
					if (t.getRow()!=8) {
						game.getWallMoveCandidate().setTargetTile(q.getBoard().getTile((t.getRow())*9+t.getColumn()-1));
					}
				}
				else {
					game.getWallMoveCandidate().setTargetTile(q.getBoard().getTile((t.getRow())*9+t.getColumn()-1));
				}
			}
		}
		//gc.dropWall(game, side);
	}
	
	
	/**
	 * @author Saifullah9
	 * @param q
	 */
	@When("Jump to start position is initiated")
	public void jumpStart() {
		Quoridor q=QuoridorApplication.getQuoridor();
		GameController gc = new GameController();
		gc.jumpToStart(q);
	}
	
	/**
	 * @author Saifullah9
	 * @param m
	 * @param r
	 */
	@Then("The next move shall be {int}.{int}")
	public void theNextMove(int m,int r) {
	/*
	 * Here I am assuming that the ID of the currentPosition is the same as the id of move
	 */
	Quoridor q = QuoridorApplication.getQuoridor();
	int cur = q.getCurrentGame().getCurrentPosition().getId();
	int nm=cur/2+1;
	int nr=cur%2+1;
	
	assertEquals(m, nm);						//the next move to be executed
	assertEquals(r, nr);
	
	}
	
	/**
	 * @author Saifullah9
	 * @param r
	 * @param c
	 */
	@And("White player's position shall be \\({int},{int})")
	public void WhitePlayerPosition(int r, int c) {
		Quoridor q = QuoridorApplication.getQuoridor();
		GamePosition gamePos = q.getCurrentGame().getCurrentPosition();
		int row = gamePos.getWhitePosition().getTile().getRow();
		int col = gamePos.getWhitePosition().getTile().getColumn();
		assertEquals(r,row);
		assertEquals(c,col);
	}
	
	/**
	 * @author Saifullah9
	 * @param r
	 * @param c
	 */
	@And("Black player's position shall be \\({int},{int})")
	public void BlackPlayerPosition(int r, int c) {
		
		GameController gc = new GameController();
		GamePosition gamePos = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
		int row = gamePos.getBlackPosition().getTile().getRow();
		int col = gamePos.getBlackPosition().getTile().getColumn();
		assertEquals(r,row);
		assertEquals(c,col);
	}
	
	/**
	 * @author Saifullah9
	 * @param n
	 */
	@And("White has {int} on stock")
	public void WhiteWallsOnStock(int n) {
		
		GameController gc = new GameController();
		GamePosition gamePos = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
		int numberOfWalls = gamePos.getWhiteWallsInStock().size();
		assertEquals(n,numberOfWalls);
		}
	
	/**
	 * @author Saifullah9
	 * @param n
	 */
	@And("Black has {int} on stock")
	public void BlackWallsOnStock(int n) {
	
		GamePosition gamePos = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
		int numberOfWalls = gamePos.getBlackWallsInStock().size();
		assertEquals(n,numberOfWalls);
		}
	
	@When("Jump to final position is initiated")
	public void jumpFinal() {
		
		Quoridor q=QuoridorApplication.getQuoridor();
		GameController gc = new GameController();
		gc.jumpToFinal(q);
	}
	
	@When("Step backward is initiated")
	public void stepback() {
		
		Quoridor q=QuoridorApplication.getQuoridor();
		GameController gc = new GameController();
		gc.stepBackward(q);
	}
	
	@When("Step forward is initiated")
	public void stepfront() {
		
		Quoridor q=QuoridorApplication.getQuoridor();
		GameController gc = new GameController();
		gc.stepForward(q);
	}
	
	/**
	 * @author Saifullah
	 */
	@Then("I shall be notified that my wall move is invalid")
	public void isNotifiedOfInvalidWallMove() {
		//
	}
	
	/**
	 * @author Saifullah
	 */
	@Then("I shall be notified that my move is illegal")
	public void isNotifiedOfIllegalMove() {
		//
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */
	@When ("A new game is being initialized")
	public void aNewGameIsBeingInitialized() {
		GameController G= new GameController();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		G.initGame(quoridor);
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */
	@And ("White player chooses a username")
	public void whitePlayerChoosesAUsername() {
		//This method requires GUI input for the user name and whether a new user name is created or selected
		
		GameController G= new GameController();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		
		//String name acquired from GUI, init to "p1" for iteration 2
		String name = "p1";
		
		//Create or select name boolean acquired from GUI, init to true for iteration 2
		Boolean createSelect=true;
		
		if (createSelect==true) {
			G.createUsername(quoridor,name,"white");
		}
		else {
			G.selectUsername(quoridor, name,"white");
		}
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@And ("Black player chooses a username")
	public void blackPlayerChoosesAUsername() {
		//This method requires GUI input for the user name and whether a new user name is created or selected
		
		GameController G= new GameController();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		
		//String name acquired from GUI, init to "p2" for iteration 2
		String name = "p2";
		
		//Create or select name boolean acquired from GUI, init to true for iteration 2
		Boolean createSelect=true;
		
		if (createSelect==true) {
			G.createUsername(quoridor,name,"black");
		}
		else {
			G.selectUsername(quoridor, name,"black");
		}
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@And ("Total thinking time is set")
	public void totalThinkingTimeIsSet() throws Throwable{
		//This method requires GUI input, for iteration 2 initalized to 10:00
		int min=10; int sec=00;
		
		GameController G= new GameController();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		G.setTime(quoridor, min, sec);
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@Then ("The game shall become ready to start")
	public void theGameShallBecomeReadyToStart() {
		GameStatus aGameStatus = GameStatus.ReadyToStart;
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		assertNotNull(quoridor.getCurrentGame());
		assertEquals(quoridor.getCurrentGame().getGameStatus(),aGameStatus); 
		assertEquals(quoridor.getCurrentGame().getMoveMode(),MoveMode.PlayerMove);
		assertNotNull(quoridor.getCurrentGame().getBlackPlayer());
		assertNotNull(quoridor.getCurrentGame().getWhitePlayer());
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@Given ("The game is ready to start")
	public void theGameIsReadyToStart() {
		GameController gc=new GameController();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		gc.initQuorridor();
		gc.initGame(quoridor);
		
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@When ("I start the clock")
	public void iStartTheClock() {
		GameController G= new GameController();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		G.startTheClock(quoridor,new Timer(0, null));
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@Then ("The game shall be running")
	public void theGameShallBeRunning() {
		GameStatus aGameStatus = GameStatus.Running;
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		Game game=quoridor.getCurrentGame();
		assertEquals(game.getGameStatus(),aGameStatus); 
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@And ("The board shall be initialized")
	public void theBoardIsInitialized() {
		
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		GamePosition pos=quoridor.getCurrentGame().getCurrentPosition();
		assertNotNull(quoridor.getBoard());
		assertTrue(quoridor.getBoard().hasTiles());
		assertEquals(pos.getWhitePosition().getTile(), quoridor.getBoard().getTile(36));
		assertEquals(pos.getBlackPosition().getTile(), quoridor.getBoard().getTile(44));
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@Given ("Next player to set user name is? (.*)")
	public void nextPlayerToSetUserNameIs(String color) {
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		
		create="";
		
		//the following creation of new game position was necessary as the currentPosition set by the background method is null, proven by the assertion error caused by uncommenting next line
		//assertNotNull(quoridor.getCurrentGame().getCurrentPosition());
		int thinkingTime=180;
		Player player1 = new Player(new Time(thinkingTime), quoridor.getUser(0), 9, Direction.Horizontal);
		Player player2 = new Player(new Time(thinkingTime), quoridor.getUser(1), 1, Direction.Horizontal);
		quoridor.getCurrentGame().setBlackPlayer(player2);
		quoridor.getCurrentGame().setWhitePlayer(player1);
		
		Tile player1StartPos = quoridor.getBoard().getTile(36);
		Tile player2StartPos = quoridor.getBoard().getTile(44);
		PlayerPosition player1Position = new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(), player1StartPos);
		PlayerPosition player2Position = new PlayerPosition(quoridor.getCurrentGame().getBlackPlayer(), player2StartPos);
		
		if (color.compareTo("white")==0) {
			GamePosition gamePos=new GamePosition(0, player1Position, player2Position, quoridor.getCurrentGame().getWhitePlayer(), quoridor.getCurrentGame());
			quoridor.getCurrentGame().setCurrentPosition(gamePos);
		}
		else {
			GamePosition gamePos=new GamePosition(0, player1Position, player2Position, quoridor.getCurrentGame().getBlackPlayer(), quoridor.getCurrentGame());
			quoridor.getCurrentGame().setCurrentPosition(gamePos);
		}
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@And ("There is existing user (.*)")
	public void thereIsExistingUser(String name) {
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		User user=new User(name, quoridor);
		quoridor.addUser(user);
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@When ("The player selects existing? (.*)")
	public void thePlayerSelectsExisting(String name) {
		GameController G= new GameController();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		if (quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
			G.selectUsername(quoridor, name,"white");
		}
		else {
			G.selectUsername(quoridor, name,"black");
		}
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@Then ("The name of player? (.*) in the new game shall be (.*)")
	public void theNameOfPlayerInTheNewGameShallBe(String Color, String name) {
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		Game game=quoridor.getCurrentGame();
		if (Color.compareTo("white")==0) {
			assertEquals(game.getWhitePlayer().getUser().getName(),name);
		}
		else {
			assertEquals(game.getBlackPlayer().getUser().getName(),name);
		}
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@And ("There is no existing user?(.*)")
	public void thereIsNoExistingUser(String name) {
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		List<User> li=quoridor.getUsers();
		if (!li.isEmpty()) {
			for (int index=0; index<li.size();index++) {
				if (li.get(index).getName().compareTo(name)==0) {
					quoridor.removeUser(li.get(index));
					break;
				}
			}
			
		}
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@When ("The player provides new user name: ?(.*)")
	public void thePlayerProvidesNewUserName(String name) {
		GameController G= new GameController();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		if (quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
			create=G.createUsername(quoridor, name,"white");
		}
		else {
			create=G.createUsername(quoridor, name,"black");
		}
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@Then ("The player shall be warned that (.*) already exists")
	public void thePlayerShallBeWarnedThatAlreadyExists(String name) {
		//GUI related feature
		
		assertEquals(create,name +" already exists");
	}
	
	/**
	 * @author DariusPi
	 * @throws Throwable
	 */ 
	@And ("Next player to set user name shall be (.*)")
	public void nextlayerToSetUserNameShallBe(String color) {
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		if (color.compareTo("white")==0) {
			assertEquals(quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove(),quoridor.getCurrentGame().getWhitePlayer());
		}
		else {
			assertEquals(quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove(),quoridor.getCurrentGame().getBlackPlayer());
		}
					
	}
	/**
	 * @author ohuss1
	 * @throws Throwable
	 */
	@Given ("A game position is supplied with pawn coordinate {int}:{int}")//might have problems with : symbol
	public void aGamePositionisSuppliedWithPawnCoordinate(int row, int col) throws Throwable{
		this.row=row;
		this.col=col;
		this.wvalid=false;
		/*Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		Quoridor quoridor=QuoridorApplication.getQuoridor();
		GamePosition position = game.getCurrentPosition();
		//Tile player1StartPos = quoridor.getBoard().getTile(36);
		Tile player2StartPos = quoridor.getBoard().getTile(44);
		if(getIndex(row,col)==-10) {//getIndex gives-10 for invalid position
			Tile testTile = QuoridorApplication.getQuoridor().getBoard().getTile(36);
			PlayerPosition player1Position = new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(), testTile);
			PlayerPosition player2Position = new PlayerPosition(quoridor.getCurrentGame().getBlackPlayer(), player2StartPos);
			GamePosition testPosition=new GamePosition(-1,player1Position,player2Position,(quoridor.getCurrentGame().getWhitePlayer()),game);
			//negative id in GamePosition to tell controller invalid tile coordinates. Controller doesnt have 
			//access to row, col so it can only comment on whether if a pawn is there at that index of tiles
			//3rdNov keeping in mind darius's controller the above needs to chnage but 
			//getTile 
		}
		else {
		Tile testTile = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(row,col));
		//PlayerPosition prevalid =new PlayerPosition( game.getCurrentPosition().getPlayerToMove(), testTile);
		//PlayerPosition prevalid2 =new PlayerPosition( game.getCurrentPosition().getPlayerToMove(), testTile);
		PlayerPosition player1Position = new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(), testTile);
		//PlayerPosition player1Position = new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(), exTile);
		PlayerPosition player2Position = new PlayerPosition(quoridor.getCurrentGame().getBlackPlayer(), player2StartPos);
		GamePosition testPosition=new GamePosition(1,player1Position,player2Position,(quoridor.getCurrentGame().getWhitePlayer()),game);//has id	
		}*/
	}
	/**
	 * @author ohuss1
	 * @throws Throwable
	 */
	
	@When ("Validation of the position is initiated")
	public void validationOfPositionisInitiated() {//game position created in given pass it to game controller
		//try getting position from tile created in Atgiven
		//not allowed to give argument to the above method
		Quoridor q=QuoridorApplication.getQuoridor();
		GamePosition Prev=GamePosition.getWithId(1);
		GameController gc = new GameController();
		resvalid=gc.checkIfLoadGameValid(q);
	}
	/**
	 * @author ohuss1
	 * @throws Throwable
	 */
	@Then ("The position shall be {string}")
	public void thePositionShallBeResult(String result){
		String success;
		if (resvalid) {
			success="ok";
		}
		else {
			success="not ok";
		}
		assertEquals(result,success);
	}
	
	/**
	 * @author ohuss1
	 * @throws Throwable
	 */
	  @Given
	  ("A game position is supplied with wall coordinate {int}:{int}-{string}")
	  public void aGamePositionisSuppliedWithWallCoordinate(int row, int col,String
	  dir) throws Throwable{
		  this.row=row;
		  this.col=col;
		  this.dir=dir;
		  
		  /*Game game = QuoridorApplication.getQuoridor().getCurrentGame(); 
		  Quoridor quoridor=QuoridorApplication.getQuoridor();                                                                                
		  GamePosition position = game.getCurrentPosition();
		  //now want to add wall to players wall on board list.
		  if(getIndex(row,col)==-10) {
			  Tile player2StartPos = quoridor.getBoard().getTile(44); 	  
			  Tile testTile = QuoridorApplication.getQuoridor().getBoard().getTile(36);//has row col coordinates
			  //Lets create a wall to save the direction as well	 
			  PlayerPosition player1Position = new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(), testTile);
			  PlayerPosition player2Position = new PlayerPosition(quoridor.getCurrentGame().getBlackPlayer(), player2StartPos);
			  Player testPlayer=quoridor.getCurrentGame().getWhitePlayer();
			  //Wall directionWall=new Wall(2,testPlayer);//duplicate id error
			   Wall directionWall=game.getCurrentPosition().getWhiteWallsInStock(1);
			  if("vertical"==dir) {
				  WallMove correspWallMove =new WallMove(1,1,testPlayer,testTile,game,Direction.Vertical,directionWall);
			  }
			  else if("horizontal"==dir) {
				  WallMove correspWallMove =new WallMove(1,1,testPlayer,testTile,game,Direction.Horizontal,directionWall);
			  }
			  GamePosition testPosition=new GamePosition(-1,player1Position,player2Position,(quoridor.getCurrentGame().getWhitePlayer()),game);//has id
			  testPosition.addWhiteWallsOnBoard(directionWall);//negative GamePosition ID tells that invalid coordinates
		  }
		  else {
		  Tile player2StartPos = quoridor.getBoard().getTile(44); 	  
		  Tile testTile = QuoridorApplication.getQuoridor().getBoard().getTile(getIndex(row,col));//has row col coordinates
		  //Lets create a wall to save the direction as well	 
		  PlayerPosition player1Position = new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(), testTile);
		  PlayerPosition player2Position = new PlayerPosition(quoridor.getCurrentGame().getBlackPlayer(), player2StartPos);
		  Player testPlayer=quoridor.getCurrentGame().getWhitePlayer();
		  //Wall directionWall=new Wall(3,testPlayer);
		  Wall directionWall=game.getCurrentPosition().getWhiteWallsInStock(1);
		  if("vertical"==dir) {
			  WallMove correspWallMove =new WallMove(1,1,testPlayer,testTile,game,Direction.Vertical,directionWall);
		  }
		  else if("horizontal"==dir) {
			  WallMove correspWallMove =new WallMove(1,1,testPlayer,testTile,game,Direction.Horizontal,directionWall);
		  }
		  GamePosition testPosition=new GamePosition(1,player1Position,player2Position,(quoridor.getCurrentGame().getWhitePlayer()),game);//has id
		  testPosition.addWhiteWallsOnBoard(directionWall);
		  }
		  
		  //Now will add wall to board*/
	  }
	  
	  
	  /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @Then ("The position shall be valid")
	  public void thePositionShallBeValid() {
		  assertTrue(resvalid);
	  }
	  /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @Then ("The position shall be invalid") 
	  public void thePositionShallBeInvalid() {
		  assertFalse(resvalid);
	  }
	  /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @Given
	  ("The player to move is {string}")
	  public void thePlayerToMoveIs(String player)  {
		  Quoridor quoridor=QuoridorApplication.getQuoridor();
		  GamePosition GP=quoridor.getCurrentGame().getCurrentPosition();
		  //now let's get player to move and compare
		  Player playertoMove=GP.getPlayerToMove();
		  Player DesiredPlayerToMove;
		  //now let's get player's color
		  if(playertoMove.hasGameAsWhite()&& (player.compareTo("white")==0)) {
			  //means already white is tomove
		  }
		  else if((playertoMove.hasGameAsBlack()) && (player.compareTo("black")==0)){
			 // means already black is to move
		  }
		  else {
			  //case if tomove not same as player
			  //set player to move as color given in argument
			  if(player.compareTo("black")==0) {
				  DesiredPlayerToMove=quoridor.getCurrentGame().getBlackPlayer();
				  GP.setPlayerToMove(DesiredPlayerToMove);
			  }
			  else {
				  DesiredPlayerToMove=quoridor.getCurrentGame().getWhitePlayer();
				  GP.setPlayerToMove(DesiredPlayerToMove);
			  }
			  
		  }
		  
	  }
	  /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @And//
	  ("The clock of {string} is running")
	  public void theClockOfPlayerIsRunning(String player) throws Throwable {
		  //Countdown method ensures these 2 (how to make sure test passes)

	  }
	  /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @And
	  ("The clock of {string} is stopped")
	  public void theClockOfOtherIsStopped(String other) throws Throwable {
		  //Countdown method ensures these 2 (how to make sure test passes)

	  }
	  /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @When
	  ("Player {string} completes his move")
	  public void playerPlayerCompletesHisMove(String player) throws Throwable {
		  Quoridor q=QuoridorApplication.getQuoridor();
		  GameController G = new GameController();
		  G.switchPlayer(q);  
	  }
	  /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @Then
	  ("The user interface shall be showing it is {string} turn")
	  public void theUserInterfaceShallbeShowingItIsOthersTurn(String other) throws Throwable {
		  String otherP;
		  boolean turn;
		  Quoridor q=QuoridorApplication.getQuoridor();
		  Player curr=q.getCurrentGame().getCurrentPosition().getPlayerToMove();
		  if(curr.hasGameAsWhite()) {
			  otherP="black"; 
		  }
		  else {
			  otherP="white";
		  }
		  
		  QuoridorPage view = QuoridorApplication.getQuoridorView();
			if (view == null) throw new Exception("View doesn't exist");
			if(other.contentEquals("black")) {
				turn=view.getVisibilityTurnMessage2();
				//assertEquals(true,turn );
				assertTrue(curr.hasGameAsBlack());
			}
			else {
				turn=view.getVisibilityTurnMessage1();
				assertTrue(curr.hasGameAsWhite());
				//assertEquals(true,turn );
			}
			//friAdvice
		  //getter in quoridor application done
		  
		  //i will create getter in quoridor page to get JLabel for other player 
		  //and assert
		  
	  }
	  /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @And
	  ("The clock of {string} shall be stopped")
	  public void theClockofPlayerShallBeStopped(String player) throws Throwable {
		  QuoridorPage view = QuoridorApplication.getQuoridorView();
			if (view == null) throw new Exception("View doesn't exist");
		  //what is understand check if clock is running and player to move is 
		  boolean running=view.gettimeRem2();
		  
		  
		//Stop clock then test if stopped
		  Quoridor q=QuoridorApplication.getQuoridor();
		  //need to check if player white or black or set player to move to that color
		  assertNotNull(q.getCurrentGame());
		  assertNotNull(q.getCurrentGame().getBlackPlayer());
			assertNotNull(q.getCurrentGame().getWhitePlayer());
		  if(player.compareTo("white")==0) {
			  q.getCurrentGame().getWhitePlayer().setRemainingTime(new Time(0));//now
				//check if player's remaining time=0;
			  //assertEquals(0, q.getCurrentGame().getWhitePlayer().getRemainingTime());
			  assertTrue(q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsBlack());
		  }
		  else {
			  q.getCurrentGame().getBlackPlayer().setRemainingTime(new Time(0));
			  //assertEquals(0, q.getCurrentGame().getBlackPlayer().getRemainingTime());
			  assertTrue(q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite());
		  }	  
			
	  }

	    /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @And
	  ("The clock of {string} shall be running")
	  public void theClockofOtherShallBeRunning(String other) throws Throwable {//MIGHT BE WRONG
		
		//Stop clock then test if stopped
		  Quoridor q=QuoridorApplication.getQuoridor();
		  //need to check if player white or black or set player to move to that color
		  assertNotNull(q.getCurrentGame());
		  assertNotNull(q.getCurrentGame().getBlackPlayer());
			assertNotNull(q.getCurrentGame().getWhitePlayer());
		  if(other=="white") {
			  //q.getCurrentGame().getWhitePlayer().setRemainingTime(new Time(0));//now
			  assertNotEquals(0, q.getCurrentGame().getWhitePlayer().getRemainingTime());
		  }
		  else {
			  //q.getCurrentGame().getBlackPlayer().setRemainingTime(new Time(0));
			  assertNotEquals(0, q.getCurrentGame().getBlackPlayer().getRemainingTime());
		  }	
	  }
	    
	  
	    /**
		 * @author ohuss1
		 * @throws Throwable
		 */
	  @And
	  ("The next player to move shall be {string}")
	  public void theNextPlayerToMoveShallbeOther(String other) throws Throwable {
		  
		  Quoridor quoridor=QuoridorApplication.getQuoridor();
		  Player currP=quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove();
		  if (other.compareTo("white")==0) {
			  assertTrue(currP.hasGameAsWhite());
		  }
		  else {
			  assertTrue(currP.hasGameAsBlack());
		  }
			
	  }
	  
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@When ("I initiate to load a saved game? (.*)")
	public void iInitiateToLoadASavedGame(String filename) throws Throwable {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		GameController G = new GameController();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < filename.length() - 2; i++)
			sb.append(filename.charAt(i + 1));
		
		String file = sb.toString();
		if (file.charAt(file.length()-1)=='t') {				//if load position
			try {
				Game game = G.initSavedGameLoad(quoridor, file);
				assertEquals(quoridor.getCurrentGame(), game);
			} catch (Exception e) {
				//do nothing. This is expected behavior for some invalid savegames.
			}
		}
		else {
			G.initGame(quoridor);
			for (int i=0;i<10;i++) {
				quoridor.getCurrentGame().getWhitePlayer().addWall(loadPlayers.get(0).getWall(0));
				quoridor.getCurrentGame().getBlackPlayer().addWall(loadPlayers.get(1).getWall(0));
				
				quoridor.getCurrentGame().getCurrentPosition().addWhiteWallsInStock(quoridor.getCurrentGame().getWhitePlayer().getWall(i));
				quoridor.getCurrentGame().getCurrentPosition().addBlackWallsInStock(quoridor.getCurrentGame().getBlackPlayer().getWall(i));
			}
			status=G.loadMoves(quoridor,file);
			if (status==1) {
				quoridor.getCurrentGame().setGameStatus(GameStatus.Replay);
			}
		}
		
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("The position to load is valid")
	public void thePositionToLoadIsValid() throws Throwable {
		GameController G = new GameController();
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		//assertEquals(quoridor.getCurrentGame().getGameStatus(), GameStatus.ReadyToStart);
		//if gameStatus signals that it isn't readyToStart, this means that initial loading failed
		//and position isn't valid.
		assertTrue(G.checkIfLoadGameValid(quoridor));
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@Then ("It shall be (.*)'s turn")
	public void itShallBeSTurn(String playerColor) {
		boolean isWhite = playerColor.toLowerCase().contains("white");
		GameController G = new GameController();
		assertTrue(G.setCurrentTurn(isWhite, QuoridorApplication.getQuoridor()));
	}

	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("The position to load is invalid")
	public void thePositionToLoadIsInvalid() throws Throwable {
		GameController G = new GameController();
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		if (!quoridor.getCurrentGame().getGameStatus().equals(GameStatus.Initializing))
			assertFalse(G.checkIfLoadGameValid(quoridor));
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@Then("{string} shall be at {int}:{int}")
	public void shallBeAt(String player, int row, int col) {
		boolean isWhite = (player.toLowerCase().contains("white"));
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Game game = quoridor.getCurrentGame();
		if (isWhite) {
			assertEquals(row,game.getCurrentPosition().getWhitePosition().getTile().getRow());
			assertEquals(col,game.getCurrentPosition().getWhitePosition().getTile().getColumn());
			//assertEquals(quoridor.getBoard().getTile((row-1)*9+col-1), game.getCurrentPosition().getWhitePosition().getTile());
		}
		else {
			assertEquals(row,game.getCurrentPosition().getBlackPosition().getTile().getRow());
			assertEquals(col,game.getCurrentPosition().getBlackPosition().getTile().getColumn());
		}
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("(.*) shall have a (.*) wall at (.*):(.*)")
	public void shallHaveAWallAt(String playerColor, String orientation, int row, int col) 
			throws Throwable {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		Wall[] walls;
		Direction dir = (orientation.toLowerCase().contains("v")) ? Vertical : Horizontal;
		if (playerColor.toLowerCase().contains("w")) {
			walls = new Wall[game.getCurrentPosition().getWhiteWallsOnBoard().size()];
			walls = game.getCurrentPosition().getWhiteWallsOnBoard().toArray(walls);
			assertTrue(wallPresent(row, col, walls, dir));
		} else {
			walls = new Wall[game.getCurrentPosition().getBlackWallsOnBoard().size()];
			walls = game.getCurrentPosition().getBlackWallsOnBoard().toArray(walls);
			assertTrue(wallPresent(row, col, walls, dir));
		}
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("Both players shall have (.*) in their stacks")
	public void bothPlayersShallHaveInTheirStacks(int remainingWalls) throws Throwable {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		assertEquals(game.getCurrentPosition().getWhiteWallsInStock().size(), remainingWalls);
		assertEquals(game.getCurrentPosition().getBlackWallsInStock().size(), remainingWalls);
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@Then ("The load shall return an error")
	public void theLoadShallReturnAnError() throws Throwable {
		GameController G = new GameController();
		boolean loadFail = false;
		if (QuoridorApplication.getQuoridor().getCurrentGame().getGameStatus().equals(GameStatus.Initializing))
			loadFail = true;
		
		try {
			G.validityChecking(QuoridorApplication.getQuoridor());
		} catch (Exception e) {
			loadFail = true;
		}
		
		assertTrue(loadFail);
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@When ("The initialization of the board is initiated")
	public void theInitializationOfTheBoardIsInitiated() throws Throwable {
		GameController G = new GameController();
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		G.initBoard(quoridor);
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@Then ("It shall be white player to move")
	public void itShallBeWhitePlayerToMove() throws Throwable {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		assertEquals(quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove(),
				quoridor.getCurrentGame().getWhitePlayer());
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("White's pawn shall be in its initial position")
	public void whitesPawnShallBeInItsInitialPosition() throws Throwable {
		//Initial white pawn position referenced from helper method createUsersAndPlayers
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		PlayerPosition whitePosition = quoridor.getCurrentGame()
				.getCurrentPosition().getWhitePosition();
		assertEquals(whitePosition.getTile(), quoridor.getBoard().getTile(36));
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("Black's pawn shall be in its initial position")
	public void blacksPawnShallBeInItsInitialPosition() throws Throwable {
		//Initial black pawn position referenced from helper method createUsersAndPlayers
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		PlayerPosition blackPosition = quoridor.getCurrentGame()
				.getCurrentPosition().getBlackPosition();
		assertEquals(blackPosition.getTile(), quoridor.getBoard().getTile(44));
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("All of White's walls shall be in stock")
	public void allOfWhitesWallsShallBeInStock() throws Throwable {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		assertEquals(game.getCurrentPosition().getWhiteWallsInStock().size(), 10);
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("All of Black's walls shall be in stock")
	public void allOfBlacksWallsShallBeInStock() throws Throwable {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		assertEquals(game.getCurrentPosition().getBlackWallsInStock().size(), 10);
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@And ("White's clock shall be counting down")
	public void whitesClockShallBeCountingDown() throws Throwable {
		GameController G = new GameController();
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		assertTrue(G.isClockCountingDown(quoridor.getCurrentGame().getWhitePlayer()));
	}
	
	/**
	 * @author FSharp4
	 * @throws Throwable
	 */
	@Given("^The following moves were executed:$")
	public void theFolowingMovesWereExecuted(DataTable table) {
		List<String> params = table.asList(String.class);
		index = 4;
		Player player;
		Game g = QuoridorApplication.getQuoridor().getCurrentGame();
		Board b = QuoridorApplication.getQuoridor().getBoard();
		int currentID = g.getCurrentPosition().getId();
		while (index < params.size()) {
			GamePosition currentPosition = g.getCurrentPosition();
			currentID++;
			int move = Integer.parseInt(params.get(index++));
			int turn = Integer.parseInt(params.get(index++));
			int row = Integer.parseInt(params.get(index++));
			int col = Integer.parseInt(params.get(index++));
			Tile oppLocation;
			if (turn == 1) {
				player = g.getWhitePlayer();
				oppLocation = currentPosition.getBlackPosition().getTile();
			} else {
				assertEquals(turn, 2);
				player = g.getBlackPlayer();
				oppLocation = currentPosition.getWhitePosition().getTile();
			}
			
			Tile newTile = g.getQuoridor().getBoard().getTile(getIndex(row, col));
			g.addMove(new StepMove(move, 0, player, newTile, g));
			PlayerPosition newPlayerPosition = new PlayerPosition(player, newTile);
			if (turn == 1) {
				GamePosition newGamePosition = new GamePosition(currentID, 
						newPlayerPosition, new PlayerPosition(g.getBlackPlayer(), oppLocation), 
						g.getBlackPlayer(), g);
				
				g.setCurrentPosition(newGamePosition);
				assertEquals(g.getCurrentPosition().getWhitePosition().getTile(), 
						b.getTile(getIndex(row, col)));
			} else {
				GamePosition newGamePosition = new GamePosition(currentID,
						new PlayerPosition(g.getWhitePlayer(), oppLocation), newPlayerPosition, 
						g.getWhitePlayer(), g);
				
				g.setCurrentPosition(newGamePosition);
				assertEquals(g.getCurrentPosition().getBlackPosition().getTile(), 
						b.getTile(getIndex(row, col)));
			}
		}
	}
	
	/**
	 * @author FSharp4
	 * 
	 * @param player
	 * @param row
	 * @param col
	 * @throws Throwable
	 */
	@And("The last move of {string} is pawn move to {int}:{int}")
	public void theLastMoveOfIsPawnMoveTo(String player, int row, int col) {
		Quoridor q = QuoridorApplication.getQuoridor();
		Game g = q.getCurrentGame();
		int move = g.numberOfMoves() / 2;
		Tile newTile = q.getBoard().getTile(getIndex(row, col));
		Player p;
		Player opp;
		PlayerPosition oppPos;
		boolean isWhite = player.contentEquals("white");
		if (isWhite) {
			p = g.getWhitePlayer();
			opp = g.getBlackPlayer();
			oppPos = new PlayerPosition(opp, g.getCurrentPosition().getBlackPosition().getTile());
		} else {
			p = g.getBlackPlayer();
			opp = g.getWhitePlayer();
			oppPos = new PlayerPosition(opp, g.getCurrentPosition().getWhitePosition().getTile());
		}
		
		g.addMove(new StepMove(move, 0, p, newTile, g));
		PlayerPosition pPos = new PlayerPosition(p, newTile);
		int nextGPID = g.getCurrentPosition().getId() + 1;
		GamePosition nextGP;
		if (isWhite) {
			nextGP = new GamePosition(nextGPID, pPos, oppPos, opp, g);
			g.setCurrentPosition(nextGP);
			assertEquals(g.getCurrentPosition().getWhitePosition().getTile(), 
					q.getBoard().getTile(getIndex(row, col)));
		} else {
			nextGP = new GamePosition(nextGPID, oppPos, pPos, opp, g);
			g.setCurrentPosition(nextGP);
			assertEquals(g.getCurrentPosition().getBlackPosition().getTile(), 
					q.getBoard().getTile(getIndex(row, col)));
		}
	}
	
	/**
	 * @author FSharp4
	 */
	@When("Player initates to resign")
	public void playerInitatesToResign() {
		new GameController().resignGame(QuoridorApplication.getQuoridor());
	}

	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@When ("{int}:{int} is set as the thinking time")
	public void IsSetAsTheThinkingTime(int min, int sec) throws Throwable{ 
		GameController G= new GameController();
		G.setTime(QuoridorApplication.getQuoridor(), min, sec); //calls setTime method in the GameController Class
	}

	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@Then ("Both players shall have {int}:{int} remaining time left")
	public void BothPlayersShallHaveMinSecRemainingTimeLeft(int min, int sec) throws Throwable{

		int time = min*60+sec;
		GameController G= new GameController();

		Time left = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getRemainingTime(); //get time left
		assertEquals(left.getTime()/1000, time); 
	}


	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@Given ("File (.*) exists in the filesystem")
	public void FileFilenameExistsInTheFileSystem (String FileName) throws Throwable{
		String file="";
		StringBuilder sb = new StringBuilder();
		
		for (int i=0;i<FileName.length()-2;i++) {
			sb.append(FileName.charAt(i+1));
		}
		file=sb.toString();
		
		File ffile=new File(file);
		ffile.createNewFile();
	}

	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@Given ("No file (.*) exists in the filesystem")
	public void NoFileExistsInTheFilesystem(String FileName) throws Throwable{
		String file="";
		StringBuilder sb = new StringBuilder();
		
		for (int i=0;i<FileName.length()-2;i++) {
			sb.append(FileName.charAt(i+1));
		}
		file=sb.toString();
		
		File ffile=new File(file);
		ffile.delete();

	}

	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@When ("The user initiates to save the game with name (.*)")
	public void TheUserInitiatesToSaveTheGameWithNameFilename (String FileName) throws Throwable{
		GameController G= new GameController();
	
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		String file="";
		StringBuilder sb = new StringBuilder();
		
		for (int i=0;i<FileName.length()-2;i++) {
			sb.append(FileName.charAt(i+1));
		}
		file=sb.toString();
		if (file.charAt(file.length()-1)=='t') {		//save position
			G.SaveGame(quoridor, file);
		}
		else {
			G.saveMoves(quoridor, file,false);
		}
		//G.SaveGame(quoridor, file);
		//String movFilename=file.substring(0, file.length()-3)+"mov";
		
		//System.out.print(movFilename);
	}

	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@Then ("A file with (.*) shall be created in the filesystem")
	public void AFileWithFilenameIsCreatedInTheFilesystem (String FileName) throws Throwable{
		String file="";
		StringBuilder sb = new StringBuilder();
		
		for (int i=0;i<FileName.length()-2;i++) {
			sb.append(FileName.charAt(i+1));
		}
		file=sb.toString();
		File ffile=new File(file);
		assertTrue(ffile.exists());
	}

	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@Then ("File with (.*) shall be updated in the filesystem")
	public void FileWithFilenameIsUpdatedInTheFilesystem (String FileName) throws Throwable{
		String file="";
		StringBuilder sb = new StringBuilder();
		
		for (int i=0;i<FileName.length()-2;i++) {
			sb.append(FileName.charAt(i+1));
		}
		file=sb.toString();
		File ffile=new File(file);
		assertTrue(ffile.exists());
	}

	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@Then ("File (.*) shall not be changed in the filesystem")
	public void FileFilenameIsNotChangedInTheFilesystem (String FileName) throws Throwable{
		String file="";
		StringBuilder sb = new StringBuilder();
		
		for (int i=0;i<FileName.length()-2;i++) {
			sb.append(FileName.charAt(i+1));
		}
		file=sb.toString();
		File ffile=new File(file);
		assertTrue(ffile.exists());
	}


	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@And ("The user cancels to overwrite existing file")
	public void TheUserCancelsToOverwriteExistingFile() throws Throwable{
		//This GUI method is functional but difficult to testing this manner
		GameController G = new GameController();
		boolean File_Overwrite = true;
		
	}

	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	@And ("The user confirms to overwrite existing file") 
	public void TheUserConfirmsToOverwriteExistingFile() throws Throwable{
		//This GUI method is functional but difficult to testing this manner
		GameController G = new GameController();
		boolean File_Overwrite = true;
	}
	
	/**
	 * @author AmineMallek
	 * @throws Throwable
	 */ 
	
	 @And ("The game has no final results")
	 public void theGameHasNoFinalResult() {
		 Quoridor q=QuoridorApplication.getQuoridor();
		 GameController gc=new GameController();
		 //assertFalse(gc.checkResult(q)); //assert false to check Result
	 }
	 
	 /**
		 * @author AmineMallek
		 * @throws Throwable
		 */ 
		
		@And ("Each game move is valid")
			public void GameMoveValid() throws Throwable{
			 Quoridor q=QuoridorApplication.getQuoridor();
			 GameController G = new GameController();
				
			 eachgamemovevalid = false; // false by default
			 
				if (QuoridorApplication.getQuoridor().getCurrentGame().getGameStatus().equals(GameStatus.Initializing)) //not valud
					eachgamemovevalid = false;  //stays false
				try 				{	G.validityChecking(QuoridorApplication.getQuoridor());	} 
				catch (Exception e) {	eachgamemovevalid = false;								}
				
				//assertTrue(eachgamemovevalid);	// assert true to each game move is valid
		}

		/**
		 	* @author AmineMallek
		 	* @throws Throwable
		 	*/ 
		 
		 @And ("The game to load has an invalid move")
		 public void TheGameHasInvalidMove() {
			 Quoridor q=QuoridorApplication.getQuoridor();
			 GameController gc=new GameController();
			 invalidmove = gc.checkResult(q); //calls checkResult
			 
		 }

		 /**
			 * @author AmineMallek
			 * @throws Throwable
			 */ 
		 
		 @Then ("The game shall notify the user that the game file is invalid")
		 public void TheGameNotifiesUserGameFileInvalid() {
			 Quoridor q=QuoridorApplication.getQuoridor();
			 GameController gc=new GameController();
			 assertFalse(invalidmove); //false boolean invalid move
		 }
	
 
	/*
	 * TODO Iteration 4 
	 *
	 */
	
	/**
	 * @author DariusPi 
	 * 
	 * @param row
	 * @param col
	 */
	@And ("The player is located at {int}:{int}")
	public void thePlayerIsLocatedAt(int row, int col) {
		Quoridor q=QuoridorApplication.getQuoridor();
		Game g=q.getCurrentGame();
		GamePosition prev=g.getCurrentPosition();
		starter=g.getCurrentPosition().getPlayerToMove();
		PlayerPosition p1= new PlayerPosition(starter, q.getBoard().getTile((row-1)*9+col-1));
		if (starter.hasGameAsWhite()) {
			prev.setWhitePosition(p1);
		}
		else {
			prev.setBlackPosition(p1);
		}
	}
	
	/**
	 * @author DariusPi
	 * 
	 * @param dir
	 * @param side
	 */
	 @And ("There are no {string} walls {string} from the player")
	 public void thereAreNoWallsFromThePlayer(String dir, String side) {
		 Quoridor q=QuoridorApplication.getQuoridor();
			Game g=q.getCurrentGame();
			GamePosition prev=g.getCurrentPosition();
			PlayerPosition p1;
			if (prev.getPlayerToMove().hasGameAsWhite()) {
				p1=prev.getWhitePosition();
			}
			else {
				p1=prev.getBlackPosition();
			}
			for (Wall w: prev.getWhiteWallsOnBoard()) {
				if (((WallMove)w.getMove()).getWallDirection().toString().compareTo(dir)==0) {
					if ((p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn())||(p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn()-1)||(p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn()+1)){
						if (p1.getTile().getColumn()+3>9) {
							w.getMove().setTargetTile(q.getBoard().getTile((w.getMove().getTargetTile().getRow()-1)*9+w.getMove().getTargetTile().getColumn()-1+3));
						}
						else {
							w.getMove().setTargetTile(q.getBoard().getTile((w.getMove().getTargetTile().getRow()-1)*9+w.getMove().getTargetTile().getColumn()-1-3));
						}
					}
				}
			}
			
			for (Wall w: prev.getBlackWallsOnBoard()) {
				if (((WallMove)w.getMove()).getWallDirection().toString().compareTo(dir)==0) {
					if ((p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn())||(p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn()-1)||(p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn()+1)){
						if (p1.getTile().getColumn()+3>9) {
							w.getMove().setTargetTile(q.getBoard().getTile((w.getMove().getTargetTile().getRow()-1)*9+w.getMove().getTargetTile().getColumn()-1+3));
						}
						else {
							w.getMove().setTargetTile(q.getBoard().getTile((w.getMove().getTargetTile().getRow()-1)*9+w.getMove().getTargetTile().getColumn()-1-3));
						}
					}
				}
			}
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param side
	  */
	 @And ("The opponent is not {string} from the player")
	 public void theOpponentIsNotFromThePlayer(String side) {
		Quoridor q=QuoridorApplication.getQuoridor();
		Game g=q.getCurrentGame();
		GamePosition prev=g.getCurrentPosition();
		PlayerPosition p1;
		PlayerPosition p2;
		if (prev.getPlayerToMove().hasGameAsWhite()) {
			p1=prev.getWhitePosition();
			p2=prev.getBlackPosition();
		}
		else {
			p1=prev.getBlackPosition();
			p2=prev.getWhitePosition();
		}
		 
		 if ((p1.getTile().getColumn()==p2.getTile().getColumn())||(p1.getTile().getColumn()==p2.getTile().getColumn()-1)||(p1.getTile().getColumn()==p2.getTile().getColumn()+1)){
			if (p1.getTile().getColumn()+3>9) {
				p2.setTile(q.getBoard().getTile((p2.getTile().getRow()-1)*9+p2.getTile().getColumn()-1+3));
			}
			else {
				p2.setTile(q.getBoard().getTile((p2.getTile().getRow()-1)*9+p2.getTile().getColumn()-1-3));
			}
		}
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param colour
	  * @param side
	  */
	 @When ("Player {string} initiates to move {string}")
	 public void playerInitiatesToMove(String colour, String side) {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		GamePosition curr=quoridor.getCurrentGame().getCurrentPosition();
		Player opponent; 
		Tile ct,ot;
		if (curr.getPlayerToMove().hasGameAsWhite()) {
			opponent=quoridor.getCurrentGame().getBlackPlayer();
			ct=curr.getWhitePosition().getTile();
			ot=curr.getBlackPosition().getTile();
		}
		else {
			opponent=quoridor.getCurrentGame().getWhitePlayer();
			ct=curr.getBlackPosition().getTile();
			ot=curr.getWhitePosition().getTile();
		}
		//in the application this is handled by the view 
		if (side.compareTo("left")==0){
			if (ct.getColumn()==1) {
				create="illegal";
				return;
			}
			else if ((ct.getColumn()==2)&&(ot.getColumn()==1)) {
				create="illegal";
				return;
			}
		}
		
		else if (side.compareTo("right")==0){
			if (ct.getColumn()==9) {
				create="illegal";
				return;
			}
			else if ((ct.getColumn()==8)&&(ot.getColumn()==9)) {
				create="illegal";
				return;
			}
		}
		
		else if (side.compareTo("up")==0){
			if (ct.getRow()==1) {
				create="illegal";
				return;
			}
			else if ((ct.getRow()==2)&&(ot.getRow()==1)) {
				create="illegal";
				return;
			}
		}
		
		else if (side.compareTo("down")==0){
			if (ct.getRow()==9) {
				create="illegal";
				return;
			}
			if ((ct.getRow()==8)&&(ot.getRow()==9)) {
				create="illegal";
				return;
			}
			
		}
		
		PawnBehavior pb=new PawnBehavior("invalid");
		pb.setCurrentGame(quoridor.getCurrentGame());
		pb.setPlayer(curr.getPlayerToMove());
		pb.change();
		pb.move(side);
		/*pb.initiate(side);
		pb.dropPawn();*/
		create=pb.getStatus();
		
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param side
	  * @param status
	  */
	 @Then ("The move {string} shall be {string}")
	 public void theMoveShallBe(String side, String status) {
		assertEquals(status,create);
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param row
	  * @param col
	  */
	 @And ("Player's new position shall be {int}:{int}")
	 public void playerNewPositionShallBe(int row, int col) {
		Quoridor q=QuoridorApplication.getQuoridor();
		Game g=q.getCurrentGame();
		GamePosition prev=g.getCurrentPosition();
		 if (starter.hasGameAsWhite()) {
			assertEquals(row,prev.getWhitePosition().getTile().getRow());
			assertEquals(col,prev.getWhitePosition().getTile().getColumn());
		}
		 else {
			assertEquals(row,prev.getBlackPosition().getTile().getRow());
			assertEquals(col,prev.getBlackPosition().getTile().getColumn());
		 }
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param colour
	  */
	 @And ("The next player to move shall become {string}")
	 public void theNextPlayerToMoveShallBecome(String colour) {
		Quoridor q=QuoridorApplication.getQuoridor();
		Game g=q.getCurrentGame();
		GamePosition prev=g.getCurrentPosition();
		if (colour.compareTo("white")==0) {
			assertTrue(prev.getPlayerToMove().hasGameAsWhite());
		}
		else {
			assertTrue(prev.getPlayerToMove().hasGameAsBlack());
		}
	 }
	
	 /**
	  * @author DariusPi
	  * 
	  * @param dir
	  * @param row
	  * @param col
	  */
	 @And ("There is a {string} wall at {int}:{int}")
	 public void thereIsAWallAt(String dir, int row, int col) {
		Quoridor q=QuoridorApplication.getQuoridor();
		Game g=q.getCurrentGame();
		GamePosition prev=g.getCurrentPosition();
		Wall w=null;
		for (int i=0;i<10;i++) {
			w=g.getWhitePlayer().getWall(i);
			if (w.getMove()==null) {
				break;
			}
		}
		Direction sdir;
		if (dir.compareTo("vertical")==0) {
			sdir=ca.mcgill.ecse223.quoridor.model.Direction.Vertical;
		}
		else {
			sdir=ca.mcgill.ecse223.quoridor.model.Direction.Horizontal;
		}
		new WallMove(0, 0, g.getWhitePlayer(), q.getBoard().getTile((row-1)*9+col-1), g, sdir, w);
		prev.removeWhiteWallsInStock(w);
		prev.addWhiteWallsOnBoardAt(w, (row-1)*9+col-1);
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param row
	  * @param col
	  */
	 @And ("The opponent is located at {int}:{int}")
	 public void theOpponentIsLocatedAt(int row, int col) {
		Quoridor q=QuoridorApplication.getQuoridor();
		Game g=q.getCurrentGame();
		GamePosition prev=g.getCurrentPosition();
		 Player opp;
		 if (starter.hasGameAsWhite()) {
			 opp=g.getBlackPlayer();
			 PlayerPosition p2= new PlayerPosition(opp, q.getBoard().getTile((row-1)*9+col-1));
			 prev.setBlackPosition(p2);
		 }
		 else {
			 opp=g.getWhitePlayer();
			 PlayerPosition p2= new PlayerPosition(opp, q.getBoard().getTile((row-1)*9+col-1));
			 prev.setWhitePosition(p2);
		 } 
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param dir
	  * @param side
	  */
	 @And ("There are no {string} walls {string} from the player nearby")
	 public void thereAreNoWallsFromThePlayerNearby(String dir, String side) {
		Quoridor q=QuoridorApplication.getQuoridor();
		Game g=q.getCurrentGame();
		GamePosition prev=g.getCurrentPosition();
		PlayerPosition p1;
		if (prev.getPlayerToMove().hasGameAsWhite()) {
			p1=prev.getWhitePosition();
		}
		else {
			p1=prev.getBlackPosition();
		}
		for (Wall w: prev.getWhiteWallsOnBoard()) {
			if (((WallMove)w.getMove()).getWallDirection().toString().compareTo(dir)==0) {
				if ((p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn())||(p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn()-1)||(p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn()+1)){
					if (p1.getTile().getColumn()+3>9) {
						w.getMove().setTargetTile(q.getBoard().getTile((w.getMove().getTargetTile().getRow()-1)*9+w.getMove().getTargetTile().getColumn()-1+3));
					}
					else {
						w.getMove().setTargetTile(q.getBoard().getTile((w.getMove().getTargetTile().getRow()-1)*9+w.getMove().getTargetTile().getColumn()-1-3));
					}
				}
			}
		}
		
		for (Wall w: prev.getBlackWallsOnBoard()) {
			if (((WallMove)w.getMove()).getWallDirection().toString().compareTo(dir)==0) {
				if ((p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn())||(p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn()-1)||(p1.getTile().getColumn()==w.getMove().getTargetTile().getColumn()+1)){
					if (p1.getTile().getColumn()+3>9) {
						w.getMove().setTargetTile(q.getBoard().getTile((w.getMove().getTargetTile().getRow()-1)*9+w.getMove().getTargetTile().getColumn()-1+3));
					}
					else {
						w.getMove().setTargetTile(q.getBoard().getTile((w.getMove().getTargetTile().getRow()-1)*9+w.getMove().getTargetTile().getColumn()-1-3));
					}
				}
			}
		}
	 }
	 
	 /**
	  * @author louismollick
	  */
	 @When("The game is no longer running")
	 public void theGameisNoLongerRunning() {
		 theGameIsRunning();
		 Quoridor quoridor = QuoridorApplication.getQuoridor();
		 GameController gameController = new GameController();
		 gameController.setFinalGameStatus(GameStatus.WhiteWon);
	 }
	 
	 /**
	  * @author louismollick
	  */
	 @Then("The final result shall be displayed")
	 public void theFinalResultShallBeDisplayed() throws Exception {
		 QuoridorPage view = QuoridorApplication.getQuoridorView();
		 assertEquals(true, view.isFinalResultVisible());
	 }
	 
	 /**
	  * @author louismollick
	  */
	 @Then("White's clock shall not be counting down")
	 @And("Black's clock shall not be counting down")
	 public void clockShallNotBeCountingDown() throws Exception {
		 QuoridorPage view = QuoridorApplication.getQuoridorView();
		 assertEquals(false, view.gettimeRem2());
	 }
	 
	 /**
	  * @author louismollick
	  */
	 @Then("White shall be unable to move")
	 @And("Black shall be unable to move")
	 public void playersShallBeUnableToMove() throws Exception {
		 QuoridorPage view = QuoridorApplication.getQuoridorView();
		 assertEquals(false, !view.getStageMove());
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @When ("I initiate replay mode")
	 public void iInitiateReplayMode() {
		 Quoridor q=QuoridorApplication.getQuoridor();
		 q.setCurrentGame(new Game(GameStatus.WhiteWon, MoveMode.PlayerMove, q));
		 GameController gc=new GameController();
		 gc.initReplay(q);
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @Then ("The game shall be in replay mode")
	 public void theGameShallBeInReplayMode(){
		 Quoridor q=QuoridorApplication.getQuoridor();
		 assertEquals(GameStatus.Replay, q.getCurrentGame().getGameStatus());
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @Given ("The game is in replay mode")
	 public void theGameIsInReplayMode() {
		 Quoridor q=QuoridorApplication.getQuoridor();
		 initQuoridorAndBoard();
		 ArrayList<Player> createUsersAndPlayers = createUsersAndPlayers("user1", "user2");
		 createAndStartGame(createUsersAndPlayers);
		 q.getCurrentGame().setGameStatus(GameStatus.Replay);
		 //q.setCurrentGame(new Game(GameStatus.Replay, MoveMode.PlayerMove, q));
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @Given ("The following moves have been played in game:")
	 public void theFollowingMovesHaveBeenPlayedInGame(io.cucumber.datatable.DataTable dataTable) { //should probably take in the moves?
		 Quoridor q=QuoridorApplication.getQuoridor();
		 Game g=q.getCurrentGame();
		 g.getCurrentPosition().setPlayerToMove(g.getWhitePlayer());
		 g.getCurrentPosition().getBlackPosition().setTile(q.getBoard().getTile(4));
		 g.getCurrentPosition().getWhitePosition().setTile(q.getBoard().getTile(8*9+4)); 
		 List<Map<String, String>> valueMaps = dataTable.asMaps();
		 for (Map<String, String> map : valueMaps) {
			 Integer mv = Integer.decode(map.get("mv"));
			 Integer rnd = Integer.decode(map.get("rnd"));
			 String mov = map.get("move");
			 GamePosition next;
			 int col;
			 switch (mov.charAt(0)){
				case 'a':
					col = 0;
					break;
				case 'b':
					col = 1;
					break;
				case 'c':
					col = 2;
					break;
				case 'd':
					col = 3;
					break;
				case 'e':
					col = 4;
					break;
				case 'f':
					col = 5;
					break;
				case 'g':
					col = 6;
					break;
				case 'h':
					col = 7;
					break;
				case 'j':
					col = 8;
					break;
				default:
					throw new IllegalArgumentException("Unsupported wall direction was provided");
				}
			 int row=Character.getNumericValue(mov.charAt(1))-1;
			 if (mov.length()!=3) {	//stepmove
				 
				 if (g.getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
					 PlayerPosition pos=new PlayerPosition(g.getWhitePlayer(), g.getQuoridor().getBoard().getTile(row*9+col));
					 PlayerPosition pos2=new PlayerPosition(g.getBlackPlayer(),g.getCurrentPosition().getBlackPosition().getTile());
					 next = new GamePosition(g.numberOfPositions(), pos, pos2, g.getBlackPlayer(), g);
				 }
				 else {
					 PlayerPosition pos=new PlayerPosition(g.getWhitePlayer(), g.getCurrentPosition().getWhitePosition().getTile());
					 PlayerPosition pos2=new PlayerPosition(g.getBlackPlayer(),g.getQuoridor().getBoard().getTile(row*9+col));
					 next = new GamePosition(g.numberOfPositions(), pos, pos2, g.getWhitePlayer(), g);
				 }
				 for (Wall w : g.getCurrentPosition().getBlackWallsOnBoard()) {
					next.addBlackWallsOnBoard(w);
				 }
				 for (Wall w : g.getCurrentPosition().getWhiteWallsOnBoard()) {
					next.addWhiteWallsOnBoard(w);
				 }
				 for (Wall w : g.getCurrentPosition().getBlackWallsInStock()) {
					next.addBlackWallsInStock(w);
				 }
				 for (Wall w : g.getCurrentPosition().getWhiteWallsInStock()) {
					next.addWhiteWallsInStock(w);
				 }
				 g.addMove(new StepMove(mv, rnd, g.getCurrentPosition().getPlayerToMove(), g.getQuoridor().getBoard().getTile(row*9+col),g));
				 g.setCurrentPosition(next);
			 }
			 else {					//wall move
				 PlayerPosition p1=new PlayerPosition(g.getWhitePlayer(),g.getCurrentPosition().getWhitePosition().getTile());
				 PlayerPosition p2=new PlayerPosition(g.getBlackPlayer(),g.getCurrentPosition().getBlackPosition().getTile());
				 if(g.getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
					next=new GamePosition(g.numberOfPositions(), p1, p2, g.getBlackPlayer(), g);
				 }
				 else {
					next=new GamePosition(g.numberOfPositions(), p1, p2, g.getWhitePlayer(), g);
				 }
				 for (Wall w : g.getCurrentPosition().getBlackWallsOnBoard()) {
					 next.addBlackWallsOnBoard(w);
				 }
				 for (Wall w : g.getCurrentPosition().getWhiteWallsOnBoard()) {
					 next.addWhiteWallsOnBoard(w);
				 }
				 for (Wall w : g.getCurrentPosition().getBlackWallsInStock()) {
					 next.addBlackWallsInStock(w);
				 }
				 for (Wall w : g.getCurrentPosition().getWhiteWallsInStock()) {
					 next.addWhiteWallsInStock(w);
				 }
				 //g.setCurrentPosition(next);
				 Direction dirc;
				 if(mov.charAt(2) == 'v') {
					 dirc = Direction.Vertical;
				 }
				 else {
					 dirc = Direction.Horizontal;
				 }
				 for (int i=0;i<10;i++) {
					 Wall w=g.getCurrentPosition().getPlayerToMove().getWall(i);
					 if (!w.hasMove()) {
						 g.addMove(new WallMove(mv,rnd,g.getCurrentPosition().getPlayerToMove(),q.getBoard().getTile(col+row*9),g,dirc,w));
						 if(q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
							 next.removeWhiteWallsInStock(g.getCurrentPosition().getPlayerToMove().getWall(i));
							 next.addWhiteWallsOnBoard(g.getCurrentPosition().getPlayerToMove().getWall(i));
						 }
						 else {
							 next.removeBlackWallsInStock(g.getCurrentPosition().getPlayerToMove().getWall(i));
							 next.addBlackWallsOnBoard(g.getCurrentPosition().getPlayerToMove().getWall(i)); 
						 }
						 break;
					 }
				 }
				 g.setCurrentPosition(next);
				 
			 }
			 
		 }
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @And ("The game does not have a final result")
	 public void theGameDoesnotHaveAFinalResult() {
		 Quoridor q=QuoridorApplication.getQuoridor();
		 GameController gc=new GameController();
		 assertFalse(gc.checkResult(q));
	 }
	 
	 /**
	  * @author DariusPI
	  * 
	  * @param movno
	  * @param rndno
	  */
	 @And ("The next move is {int}.{int}")
	 public void theNextMoveIs(int movno,int rndno){
		 Quoridor q=QuoridorApplication.getQuoridor();
		 Game g=q.getCurrentGame();
		 index=2*(movno-1)+rndno-1;
		 g.setCurrentPosition(g.getPosition(index));
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @When ("I initiate to continue game")
	 public void iInitiateToContinueGame(){
		 Quoridor q=QuoridorApplication.getQuoridor();
		 GameController gc=new GameController();
		 resvalid=gc.continueGame(q);
		 if (!resvalid) {
			 q.getCurrentGame().setGameStatus(GameStatus.Replay);	//this is done by the view for the app
		 }
		 
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @And ("The remaining moves of the game shall be removed")
	 public void theRemainingMovesOfTheGameShallBeRemoved() {
		 Quoridor q=QuoridorApplication.getQuoridor();
		 Game g=q.getCurrentGame();
		 assertEquals(index,g.getMoves().size());		//index is the index of first move that was removed
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @And ("The game has a final result")
	 public void theGameHasAFinalResult() {
		 Quoridor q=QuoridorApplication.getQuoridor();
		 Game g=q.getCurrentGame();
		 for (GamePosition gp:g.getPositions()) {
			 gp.setWhitePosition(new PlayerPosition(g.getWhitePlayer(),q.getBoard().getTile(4*9+8)));
		 }
	 }
	 
	 /**
	  * @author DariusPi
	  */
	 @And ("I shall be notified that finished games cannot be continued")
	 public void iShallBeNotifiedThatFinishedGamesCannotBeContinued() {
		 String result;
		 if (!resvalid) {
			 result="Finished games cannot be continued";
		 }
		 else {
			 result="";
		 }
		 assertEquals(result,"Finished games cannot be continued");
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param player
	  */
	 @Given ("Player {string} has just completed his move")
	 public void playerHasJustCompletedHisMove(String player) {
		 Quoridor q=QuoridorApplication.getQuoridor();
		 if (player.compareTo("white")==0) {
			 q.getCurrentGame().getCurrentPosition().setPlayerToMove(q.getCurrentGame().getWhitePlayer());
		 }
		 else {
			 q.getCurrentGame().getCurrentPosition().setPlayerToMove(q.getCurrentGame().getBlackPlayer());
		 }
	 }
	 
	 /**
	  * @author DariusPi
	  * 
	  * @param player
	  * @param row
	  * @param col
	  */
	 @And ("The new position of {string} is {int}:{int}")
	 public void theNewPositionOfIs(String player,int row,int col) {
		 Quoridor q=QuoridorApplication.getQuoridor();
		 
		 //this swap is necessary as we are playing horizontally but the test is written vertically
		 int temp=col;
		 col=row;
		 row=temp;
		 
		 if (player.compareTo("white")==0) {
			 q.getCurrentGame().getCurrentPosition().setWhitePosition(new PlayerPosition(q.getCurrentGame().getWhitePlayer(),q.getBoard().getTile((row-1)*9+col-1)));
		 }
		 else {
			 q.getCurrentGame().getCurrentPosition().setBlackPosition(new PlayerPosition(q.getCurrentGame().getBlackPlayer(),q.getBoard().getTile((row-1)*9+col-1)));
		 }
	 }
	  
	 /**
	  * @author DariusPi
	  * 
	  * @param player
	  */
	 @And ("The clock of {string} is more than zero")
	 public void theClockOfIsMoreThanZero(String player) {
		 Quoridor q=QuoridorApplication.getQuoridor(); 
		 if (player.compareTo("white")==0) {
			  q.getCurrentGame().getWhitePlayer().setRemainingTime(new Time(1000*60*5));
		 }
		 else {
			 q.getCurrentGame().getBlackPlayer().setRemainingTime(new Time(1000*60*5));
		 }
	 }
	  
	 /**
	  * @author DariusPi, FSharp4
	  * 
	  * @param player
	  */
	  @When ("Checking of game result is initated")
	  public void checkingOfGameResultIsInitated() {
		  Quoridor q=QuoridorApplication.getQuoridor();
		  GameController gc=new GameController();
		  gc.checkResult(q);
		  gc.drawCheck(q);
	  }
	  
	  /**
	   * @author DariusPi, FSharp4
	   * 
	   * @param result
	   */
	  @Then ("Game result shall be {string}")
	  public void gameResultShallBe(String result) {
		  Quoridor q=QuoridorApplication.getQuoridor();
		  Game g=q.getCurrentGame();
		  if (result.toLowerCase().compareTo("pending")==0) {
			  assertEquals(g.getGameStatus(),GameStatus.Running);
		  }
		  else if (result.contains("Won")){
			  assertEquals(g.getGameStatus().toString().toLowerCase(),result.toLowerCase());
		  }
		  else if (result.contentEquals("Drawn")){
			  assertEquals(g.getGameStatus(), GameStatus.Draw);
		  }
		  
	  }
	  
	  /**
	   * @author DariusPi
	   */
	  @And ("The game shall no longer be running")
	  public void theGameShallNoLongerBeRunning() {
		  Quoridor q=QuoridorApplication.getQuoridor();
		  Game g=q.getCurrentGame();
		  assertNotEquals(g.getGameStatus(),GameStatus.Running);
	  }
	  
	  /**
	   * @author DariusPi
	   * 
	   * @param player
	   */
	  @When ("The clock of {string} counts down to zero")
	  public void theClockOfCountsDownToZero(String player) {
		  Quoridor q=QuoridorApplication.getQuoridor();
		  if (player.compareTo("white")==0) {
			  q.getCurrentGame().getCurrentPosition().setPlayerToMove(q.getCurrentGame().getWhitePlayer());
		  }
		  else {
			  q.getCurrentGame().getCurrentPosition().setPlayerToMove(q.getCurrentGame().getBlackPlayer());
		  }
		  q.getCurrentGame().getCurrentPosition().getPlayerToMove().setRemainingTime(new Time(0));
		  GameController gc=new GameController();
		  gc.countdown(q);
	  }
	  
	  /**
	   * @author louismollick
	   * @param int brow
	   * @param int bcol
	   */
	  @Given("The black player is located at {int}:{int}")
	  public void theBlackPlayerIsLocatedAt(int brow, int bcol) {
		  Quoridor quoridor = QuoridorApplication.getQuoridor();
		  Tile tile = quoridor.getBoard().getTile((brow-1)*9+bcol-1);
		  quoridor.getCurrentGame().getCurrentPosition().getBlackPosition().setTile(tile);
	  }
	  
	  /**
	   * @author louismollick
	   * @param int wrow
	   * @param int wcol
	   */
	  @Given("The white player is located at {int}:{int}")
	  public void theWhitePlayerIsLocatedAt(int wrow, int wcol) {
		  Quoridor quoridor = QuoridorApplication.getQuoridor();
		  Tile tile = quoridor.getBoard().getTile((wrow-1)*9+wcol-1);
		  quoridor.getCurrentGame().getCurrentPosition().getWhitePosition().setTile(tile);
	  }
	  
	  /**
	   * @author louismollick
	   */
	  @When("Check path existence is initiated")
	  public void checkPathExistenceIsInitiated() {
		  GameController gameController = new GameController();
		  pathExistsFor = gameController.checkPathExistence(true);
	  }
	  
	  /**
	   * @author louismollick
	   * @param String result
	   */
	  @Then("Path is available for {string} player\\(s)")
	  public void pathIsAvailableFor(String result) {
		  assertEquals(result, pathExistsFor);
	  }
	  
	// ***********************************************
	// Clean up
	// ***********************************************

	// After each scenario, the test model is discarded
	@After
	public void tearDown() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		// Avoid null pointer for step definitions that are not yet implemented.
		if (quoridor != null) {
			quoridor.delete();
			quoridor = null;
		}
		for (int i = 1; i <= 20; i++) {
			Wall wall = Wall.getWithId(i);
			if(wall != null) {
				wall.delete();
			}
		}
	}

	// ***********************************************
	// Extracted helper methods
	// ***********************************************

	// Place your extracted methods below
	/**
	 * A method to initialize the quoridor and the board.
	 */
	private void initQuoridorAndBoard() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Board board = new Board(quoridor);
		// Creating tiles by rows, i.e., the column index changes with every tile
		// creation
		for (int i = 1; i <= 9; i++) { // rows
			for (int j = 1; j <= 9; j++) { // columns
				board.addTile(i, j);
			}
		}
	}

	/**
	 * A method to create users and players and then adding them to a list.
	 * @param userName1
	 * @param userName2
	 * @return ArrayList<Player>
	 */
	private ArrayList<Player> createUsersAndPlayers(String userName1, String userName2) {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		User user1 = quoridor.addUser(userName1);
		User user2 = quoridor.addUser(userName2);

		int thinkingTime = 180;

		// Players are assumed to start on opposite sides and need to make progress
		// horizontally to get to the other side
		//@formatter:off
		/*
		 *  __________
		 * |          |
		 * |          |
		 * |x->    <-x|
		 * |          |
		 * |__________|
		 * 
		 */
		//@formatter:on
		Player player1 = new Player(new Time(thinkingTime), user1, 9, Direction.Horizontal);
		Player player2 = new Player(new Time(thinkingTime), user2, 1, Direction.Horizontal);

		Player[] players = { player1, player2 };

		// Create all walls. Walls with lower ID belong to player1,
		// while the second half belongs to player 2
		for (int i = 0; i < 2; i++) {
			for (int j = 1; j <= 10; j++) {
				new Wall(i * 10 + j, players[i]);
			}
		}
		
		ArrayList<Player> playersList = new ArrayList<Player>();
		playersList.add(player1);
		playersList.add(player2);
		
		return playersList;
	}

	/**
	 * A method to create and start a game.
	 * @param players
	 */
	private void createAndStartGame(ArrayList<Player> players) {
		Quoridor quoridor = QuoridorApplication.getQuoridor();

		// There are total 36 tiles in the first four rows and
		// indexing starts from 0 -> tiles with indices 36 and 36+8=44 are the starting
		// positions
		Tile player1StartPos = quoridor.getBoard().getTile(36);
		Tile player2StartPos = quoridor.getBoard().getTile(44);

		//this is for vertical games
		// Tile indices start from 0 -> tiles with indices 4 and 8*9+4=76 are the starting
		// positions
		/*Tile player1StartPos = quoridor.getBoard().getTile(4);
		Tile player2StartPos = quoridor.getBoard().getTile(76);*/

		Game game = new Game(GameStatus.Running, MoveMode.PlayerMove, quoridor);

		game.setWhitePlayer(players.get(0));
		game.setBlackPlayer(players.get(1));

		PlayerPosition player1Position = new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(), player1StartPos);
		PlayerPosition player2Position = new PlayerPosition(quoridor.getCurrentGame().getBlackPlayer(), player2StartPos);

		GamePosition gamePosition = new GamePosition(0, player1Position, player2Position, players.get(0), game);

		// Add the walls as in stock for the players
		for (int j = 1; j <= 10; j++) {
			Wall wall = Wall.getWithId(j);
			gamePosition.addWhiteWallsInStock(wall);
		}
		for (int j = 1; j <= 10; j++) {
			Wall wall = Wall.getWithId(j + 10);
			gamePosition.addBlackWallsInStock(wall);
		}

		game.setCurrentPosition(gamePosition);
	}
	
	/**
	 * A helper method to calculate the index of the tile using its row and column numbers.
	 * @param row
	 * @param col
	 * @return Integer
	 */
	private int getIndex(int row, int col) {
		
		if(row<=0||col<=0||row>9||col>9){
			return -1;
		}
		else {
		return ((((row-1)*9)+col)-1);
		}
		
	}
	
	/**
	 *  A method to check if there is a wall in the provided coordinates and specified direction.
	 * @param row
	 * @param col
	 * @param wallsOnBoard
	 * @param orientation
	 * @return boolean
	 */
	private boolean wallPresent(int row, int col, Wall[] wallsOnBoard, Direction orientation) {
		if (wallsOnBoard.length == 0)
			return false;
		
		for (Wall wall : wallsOnBoard) {
			if (wall.getMove().getTargetTile().getRow() == row 
					&& wall.getMove().getTargetTile().getColumn() == col 
					&& wall.getMove().getWallDirection().equals(orientation)) {
				return true;
			}
		}
		
		return false;
	}

}
