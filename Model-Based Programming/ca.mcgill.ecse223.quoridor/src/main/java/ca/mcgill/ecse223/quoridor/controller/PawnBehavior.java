/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.0.4181.a593105a9 modeling language!*/

package ca.mcgill.ecse223.quoridor.controller;
import java.util.List;
import ca.mcgill.ecse223.quoridor.model.*;

// line 5 "../../../../../PawnStateMachine.ump"
public class PawnBehavior
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //PawnBehavior Attributes
  private boolean fals;
  private boolean tru;
  private String status;

  //PawnBehavior State Machines
  public enum StatusSM { NextToBorderOrWall, NextToPlayer, NextToBorderOrWallAndPlayer, Default }
  private StatusSM statusSM;

  //PawnBehavior Associations
  private Game currentGame;
  private Player player;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public PawnBehavior(String aStatus)
  {
    fals = false;
    tru = true;
    status = aStatus;
    setStatusSM(StatusSM.NextToBorderOrWall);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setFals(boolean aFals)
  {
    boolean wasSet = false;
    fals = aFals;
    wasSet = true;
    return wasSet;
  }

  public boolean setTru(boolean aTru)
  {
    boolean wasSet = false;
    tru = aTru;
    wasSet = true;
    return wasSet;
  }

  public boolean setStatus(String aStatus)
  {
    boolean wasSet = false;
    status = aStatus;
    wasSet = true;
    return wasSet;
  }

  public boolean getFals()
  {
    return fals;
  }

  public boolean getTru()
  {
    return tru;
  }

  public String getStatus()
  {
    return status;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isFals()
  {
    return fals;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isTru()
  {
    return tru;
  }

  public String getStatusSMFullName()
  {
    String answer = statusSM.toString();
    return answer;
  }

  public StatusSM getStatusSM()
  {
    return statusSM;
  }

  public boolean move(String cside)
  {
    boolean wasEventProcessed = false;
    
    StatusSM aStatusSM = statusSM;
    switch (aStatusSM)
    {
      case NextToBorderOrWall:
        if (!(isAJump(cside))&&!(isDiag(cside))&&isLegalStep(cside)&&!(isWOrBAdjacent(getFals(),cside,getFals()))&&(isOpponentAdjacent(getFals(),cside,getFals()).compareTo("no")!=0))
        {
        // line 16 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&isLegalStep(cside)&&isWOrBAdjacent(getFals(),cside,getFals())&&(isOpponentAdjacent(getFals(),cside,getFals()).compareTo("no")!=0))
        {
        // line 20 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&isLegalStep(cside)&&!(isWOrBAdjacent(getFals(),cside,getFals()))&&(isOpponentAdjacent(getFals(),cside,getFals()).compareTo("no")==0))
        {
        // line 24 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.Default);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&isLegalStep(cside)&&isWOrBAdjacent(getFals(),cside,getFals())&&(isOpponentAdjacent(getFals(),cside,getFals()).compareTo("no")==0))
        {
        // line 28 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&!(isLegalStep(cside)))
        {
        // line 32 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        if (isAJump(cside)||isDiag(cside))
        {
        // line 35 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        break;
      case NextToPlayer:
        if (!(isAJump(cside))&&!(isDiag(cside))&&isWOrBAdjacent(getFals(),cside,getFals()))
        {
        // line 45 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&!(isWOrBAdjacent(getFals(),cside,getFals())))
        {
        // line 49 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.Default);
          wasEventProcessed = true;
          break;
        }
        if (isAJump(cside)&&!(isDiag(cside))&&isLegalJump(cside)&&!(isWOrBAdjacent(getTru(),cside,getFals())))
        {
        // line 55 "../../../../../PawnStateMachine.ump"
          legalMove(true,cside);
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isAJump(cside)&&!(isDiag(cside))&&isLegalJump(cside)&&isWOrBAdjacent(getTru(),cside,getFals()))
        {
        // line 59 "../../../../../PawnStateMachine.ump"
          legalMove(true,cside);
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isAJump(cside)&&!(isDiag(cside))&&!(isLegalJump(cside)))
        {
        // line 63 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isDiag(cside)&&isLegalJumpDiag(cside)&&!(isWOrBAdjacent(getTru(),cside,getFals())))
        {
        // line 68 "../../../../../PawnStateMachine.ump"
          legalMove(true,cside);
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isDiag(cside)&&isLegalJumpDiag(cside)&&isWOrBAdjacent(getTru(),cside,getFals()))
        {
        // line 72 "../../../../../PawnStateMachine.ump"
          legalMove(true,cside);
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isDiag(cside)&&!(isLegalJumpDiag(cside)))
        {
        // line 77 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        break;
      case NextToBorderOrWallAndPlayer:
        if (!(isAJump(cside))&&!(isDiag(cside))&&isLegalStep(cside)&&isWOrBAdjacent(getFals(),cside,getFals()))
        {
        // line 86 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&isLegalStep(cside)&&!(isWOrBAdjacent(getFals(),cside,getFals())))
        {
        // line 90 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.Default);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&!(isLegalStep(cside)))
        {
        // line 94 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isAJump(cside)&&!(isDiag(cside))&&isLegalJump(cside)&&isWOrBAdjacent(getTru(),cside,getFals()))
        {
        // line 100 "../../../../../PawnStateMachine.ump"
          legalMove(true,cside);
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isAJump(cside)&&!(isDiag(cside))&&isLegalJump(cside)&&!(isWOrBAdjacent(getTru(),cside,getFals())))
        {
        // line 104 "../../../../../PawnStateMachine.ump"
          legalMove(true,cside);
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isAJump(cside)&&!(isDiag(cside))&&!(isLegalJump(cside)))
        {
        // line 109 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isDiag(cside)&&isLegalJumpDiag(cside)&&isWOrBAdjacent(getTru(),cside,getFals()))
        {
        // line 114 "../../../../../PawnStateMachine.ump"
          legalMove(true,cside);
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isDiag(cside)&&isLegalJumpDiag(cside)&&!(isWOrBAdjacent(getTru(),cside,getFals())))
        {
        // line 118 "../../../../../PawnStateMachine.ump"
          legalMove(true,cside);
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isDiag(cside)&&!(isLegalJumpDiag(cside)))
        {
        // line 122 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        break;
      case Default:
        if (!(isAJump(cside))&&!(isDiag(cside))&&!(isWOrBAdjacent(getFals(),cside,getFals()))&&(isOpponentAdjacent(getFals(),cside,getFals()).compareTo("no")!=0))
        {
        // line 131 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&isWOrBAdjacent(getFals(),cside,getFals())&&(isOpponentAdjacent(getFals(),cside,getFals()).compareTo("no")!=0))
        {
        // line 135 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&!(isWOrBAdjacent(getFals(),cside,getFals()))&&(isOpponentAdjacent(getFals(),cside,getFals()).compareTo("no")==0))
        {
        // line 139 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.Default);
          wasEventProcessed = true;
          break;
        }
        if (!(isAJump(cside))&&!(isDiag(cside))&&isWOrBAdjacent(getFals(),cside,getFals())&&(isOpponentAdjacent(getFals(),cside,getFals()).compareTo("no")==0))
        {
        // line 143 "../../../../../PawnStateMachine.ump"
          legalMove(false,cside);
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        if (isAJump(cside)||isDiag(cside))
        {
        // line 148 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setStatusSM(StatusSM.Default);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean change()
  {
    boolean wasEventProcessed = false;
    
    StatusSM aStatusSM = statusSM;
    switch (aStatusSM)
    {
      case NextToBorderOrWall:
        if (!(isWOrBAdjacent(getFals(),"",getTru()))&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")!=0))
        {
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isWOrBAdjacent(getFals(),"",getTru())&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")!=0))
        {
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (!(isWOrBAdjacent(getFals(),"",getTru()))&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")==0))
        {
          setStatusSM(StatusSM.Default);
          wasEventProcessed = true;
          break;
        }
        break;
      case NextToPlayer:
        if (isWOrBAdjacent(getFals(),"",getTru())&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")==0))
        {
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        if (isWOrBAdjacent(getFals(),"",getTru())&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")!=0))
        {
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        if (!(isWOrBAdjacent(getFals(),"",getTru()))&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")==0))
        {
          setStatusSM(StatusSM.Default);
          wasEventProcessed = true;
          break;
        }
        break;
      case NextToBorderOrWallAndPlayer:
        if (isWOrBAdjacent(getFals(),"",getTru())&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")==0))
        {
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        if (!(isWOrBAdjacent(getFals(),"",getTru()))&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")!=0))
        {
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (!(isWOrBAdjacent(getFals(),"",getTru()))&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")==0))
        {
          setStatusSM(StatusSM.Default);
          wasEventProcessed = true;
          break;
        }
        break;
      case Default:
        if (isWOrBAdjacent(getFals(),"",getTru())&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")==0))
        {
          setStatusSM(StatusSM.NextToBorderOrWall);
          wasEventProcessed = true;
          break;
        }
        if (!(isWOrBAdjacent(getFals(),"",getTru()))&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")!=0))
        {
          setStatusSM(StatusSM.NextToPlayer);
          wasEventProcessed = true;
          break;
        }
        if (isWOrBAdjacent(getFals(),"",getTru())&&(isOpponentAdjacent(getFals(),"",getTru()).compareTo("no")!=0))
        {
          setStatusSM(StatusSM.NextToBorderOrWallAndPlayer);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void setStatusSM(StatusSM aStatusSM)
  {
    statusSM = aStatusSM;
  }
  /* Code from template association_GetOne */
  public Game getCurrentGame()
  {
    return currentGame;
  }

  public boolean hasCurrentGame()
  {
    boolean has = currentGame != null;
    return has;
  }
  /* Code from template association_GetOne */
  public Player getPlayer()
  {
    return player;
  }

  public boolean hasPlayer()
  {
    boolean has = player != null;
    return has;
  }
  /* Code from template association_SetUnidirectionalOptionalOne */
  public boolean setCurrentGame(Game aNewCurrentGame)
  {
    boolean wasSet = false;
    currentGame = aNewCurrentGame;
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetUnidirectionalOptionalOne */
  public boolean setPlayer(Player aNewPlayer)
  {
    boolean wasSet = false;
    player = aNewPlayer;
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    currentGame = null;
    player = null;
  }


  /**
   * 
   * Returns the current row number of the pawn
   * @return integer
   */
  // line 162 "../../../../../PawnStateMachine.ump"
  public int getCurrentPawnRow(){
    GamePosition pos = currentGame.getCurrentPosition();
    	if (player.hasGameAsWhite()){
	    	return pos.getWhitePosition().getTile().getRow();
	    }
	    else{
	    	return pos.getBlackPosition().getTile().getRow();
	    }
  }


  /**
   * 
   * Returns the current column number of the pawn
   * @return integer
   */
  // line 177 "../../../../../PawnStateMachine.ump"
  public int getCurrentPawnColumn(){
    GamePosition pos=currentGame.getCurrentPosition();
    	if (player.hasGameAsWhite()){
	    	return pos.getWhitePosition().getTile().getColumn();
	    }
	    else{
	    	return pos.getBlackPosition().getTile().getColumn();
	    }
  }


  /**
   * 
   * Returns the current row number of the pawn (opponent)
   * @return integer
   */
  // line 191 "../../../../../PawnStateMachine.ump"
  public int getOpponentPawnRow(){
    GamePosition pos=currentGame.getCurrentPosition();
    	if (player.hasGameAsWhite()){
	    	return pos.getBlackPosition().getTile().getRow();
	    }
	    else{
	    	return pos.getWhitePosition().getTile().getRow();
	    }
  }


  /**
   * 
   * Returns the current column number of the pawn (opponent)
   * @return integer
   */
  // line 207 "../../../../../PawnStateMachine.ump"
  public int getOpponentPawnColumn(){
    GamePosition pos=currentGame.getCurrentPosition();
    	if (player.hasGameAsWhite()){
	    	return pos.getBlackPosition().getTile().getColumn();
	    }
	    else{
	    	return pos.getWhitePosition().getTile().getColumn();
	    }
  }


  /**
   * 
   * Returns if it is legal to step in the given direction
   * @param cside
   * @return boolean
   */
  // line 223 "../../../../../PawnStateMachine.ump"
  public boolean isLegalStep(String cside){
    int curRow = getCurrentPawnRow();
		int curCol = getCurrentPawnColumn();
    	
    	Boolean walla= isWallBlocking(curRow,curCol,cside,true);  //if adjacent wall
    	if (walla){
    		return false;
    	}
    	
    	return true;
  }


  /**
   * 
   * Returns if it is legal to jump in the given direction
   * @param cside
   * @return boolean
   */
  // line 241 "../../../../../PawnStateMachine.ump"
  public boolean isLegalJump(String cside){
    int curRow = getCurrentPawnRow();
		int curCol = getCurrentPawnColumn();
		
		String opSide = isOpponentAdjacent(false,"",true);
    	Boolean wallb= isWallBlocking(curRow,curCol,opSide,false); 	//wall adjacent to opponent
    	Boolean walla= isWallBlocking(curRow,curCol,cside,true);  //if adjacent wall
		System.out.print(" opside="+opSide);
		System.out.print(" wallb="+wallb);
    	System.out.print(" walla="+walla);
		if ((wallb)||(walla)){
    		return false;
    	}
		else if(cside.compareTo(opSide)==0){
			return true;
		} 
		
		else {
			return false;
		}
  }


  /**
   * 
   * Determines if the diagonal jump is legal or not
   * @param cside
   * @return boolean
   */
  // line 269 "../../../../../PawnStateMachine.ump"
  public boolean isLegalJumpDiag(String cside){
    int curRow = getCurrentPawnRow();
    	int curCol = getCurrentPawnColumn();
    	int opRow = getOpponentPawnRow();
    	int opCol = getOpponentPawnColumn();
    	
    	String opSide = isOpponentAdjacent(false,"",true);
    	String otherSide= cside.replace(opSide,"");
    	
    	Boolean wallb= isWallBlocking(curRow,curCol,opSide,false); //if wall blocking straight jump
    	Boolean walla= isWallBlocking(curRow,curCol,opSide,true);  //if adjacent wall
    	Boolean wallc= isWallBlocking(opRow,opCol,otherSide,true);  //if wall blocking second half of diagonal jump 
    	Boolean border= isBorderBlocking(opRow,opCol,opSide);
    	System.out.print(" opside="+opSide);
    	System.out.print(" border="+border);
    	System.out.print(" wallb="+wallb);
    	System.out.print(" walla="+walla);
    	if ((!border)&&(!wallb)){
    		return false;
    	}
    	else if ((walla)||(wallc)){
    		return false;
    	}
		else if(cside.compareTo("upleft")==0){
			if((opSide.compareTo("up")==0) || opSide.compareTo("left")==0){
				return true;
			}
		} 
		
		else if(cside.compareTo("upright")==0){
			if((opSide.compareTo("up")==0) || opSide.compareTo("right")==0){
				return true;
			}
		} 
		else if(cside.compareTo("downleft")==0){
			if((opSide.compareTo("down")==0) || opSide.compareTo("left")==0){
				return true;
			}
		} 
		else if(cside.compareTo("downright")==0){
			if((opSide.compareTo("down")==0) || opSide.compareTo("right")==0){
				return true;
			}
		}
		
    	return false;
  }


  /**
   * 
   * Determines if there is a wall or directly next to the player in any direction
   * @author DariusPi
   * @param isJump
   * @param cside
   * @param current
   * @return boolean
   */
  // line 325 "../../../../../PawnStateMachine.ump"
  public Boolean isWOrBAdjacent(Boolean isJump, String cside, Boolean current){
    int curRow;
    	int curCol;
		if (current){
			curRow = getCurrentPawnRow();
    		curCol = getCurrentPawnColumn();
		}
		else {
			curRow = getNextRow(isJump,cside);
    		curCol = getNextCol(isJump,cside);
		}
    	
    	if (isWallBlocking(curRow,curCol,"up",true)){
    		return true;
    	}
    	
    	else if (isWallBlocking(curRow,curCol,"left",true)){
    		return true;
    	}
    	
    	else if (isWallBlocking(curRow,curCol,"right",true)){
    		return true;
    	}
    	
    	else if (isWallBlocking(curRow,curCol,"down",true)){
    		return true;
    	}
    	
    	else if (isBorderBlocking(curRow,curCol,"up")){
    		return true;
    	}
    	
    	else if (isBorderBlocking(curRow,curCol,"left")){
    		return true;
    	}
    	
    	else if (isBorderBlocking(curRow,curCol,"right")){
    		return true;
    	}
    	
    	else if (isBorderBlocking(curRow,curCol,"down")){
    		return true;
    	}
    	return false;
  }


  /**
   * 
   * Determines if there is a wall either next to the player or 2 away from the player for jumps
   * @param curRow
   * @param curCol
   * @param cside
   * @param isStep
   * @return boolean
   */
  // line 379 "../../../../../PawnStateMachine.ump"
  public boolean isWallBlocking(int curRow, int curCol, String cside, boolean isStep){
    int distance;
    	if(isStep){
    		distance=1;
    	}
    	else {
    		distance = 2;
    	}
    	GamePosition curr= currentGame.getCurrentPosition();
		
		List<Wall> wWall = curr.getWhiteWallsOnBoard();
		List<Wall> bWall = curr.getBlackWallsOnBoard();
		
    	if(cside.equals("up")){
			for(Wall w: wWall){
				if (w.getMove().getWallDirection().toString().compareTo("Horizontal")==0){
					if (w.getMove().getTargetTile().getRow()==curRow-distance){
						if ((w.getMove().getTargetTile().getColumn()==curCol)||(w.getMove().getTargetTile().getColumn()==curCol-1)){
							return true;
						}
					}
				}
			}
			for(Wall w: bWall){
				if (w.getMove().getWallDirection().toString().compareTo("Horizontal")==0){
					if (w.getMove().getTargetTile().getRow()==curRow-distance){
						if ((w.getMove().getTargetTile().getColumn()==curCol)||(w.getMove().getTargetTile().getColumn()==curCol-1)){
							return true;
						}
					}
				}
			}
		}
		else if(cside.equals("right")){
			for(Wall w: wWall){
				if (w.getMove().getWallDirection().toString().compareTo("Vertical")==0){
					if (w.getMove().getTargetTile().getColumn()==curCol-1+distance){
						if ((w.getMove().getTargetTile().getRow()==curRow)||(w.getMove().getTargetTile().getRow()==curRow-1)){
							return true;
						}
					}
				}
			}
			for(Wall w: bWall){
				if (w.getMove().getWallDirection().toString().compareTo("Vertical")==0){
					if (w.getMove().getTargetTile().getColumn()==curCol-1+distance){
						if ((w.getMove().getTargetTile().getRow()==curRow)||(w.getMove().getTargetTile().getRow()==curRow-1)){
							return true;
						}
					}
				}
			}
		} 
		else if(cside.equals("left")){
			for(Wall w: wWall){
				if (w.getMove().getWallDirection().toString().compareTo("Vertical")==0){
					if (w.getMove().getTargetTile().getColumn()==curCol-distance){
						if ((w.getMove().getTargetTile().getRow()==curRow)||(w.getMove().getTargetTile().getRow()==curRow-1)){
							return true;
						}
					}
				}
			}
			for(Wall w: bWall){
				if (w.getMove().getWallDirection().toString().compareTo("Vertical")==0){
					if (w.getMove().getTargetTile().getColumn()==curCol-distance){
						if ((w.getMove().getTargetTile().getRow()==curRow)||(w.getMove().getTargetTile().getRow()==curRow-1)){
							return true;
						}
					}
				}
			}
		} 
		else if(cside.equals("down")){
			for(Wall w: wWall){
				if (w.getMove().getWallDirection().toString().compareTo("Horizontal")==0){
					if (w.getMove().getTargetTile().getRow()==curRow-1+distance){
						if ((w.getMove().getTargetTile().getColumn()==curCol)||(w.getMove().getTargetTile().getColumn()==curCol-1)){
							return true;
						}
					}
				}
			}
			for(Wall w: bWall){
				if (w.getMove().getWallDirection().toString().compareTo("Horizontal")==0){
					if (w.getMove().getTargetTile().getRow()==curRow-1+distance){
						if ((w.getMove().getTargetTile().getColumn()==curCol)||(w.getMove().getTargetTile().getColumn()==curCol-1)){
							return true;
						}
					}
				}
			}
		}
		
		return false;
  }


  /**
   * 
   * Guard returns side that opponent is on if any
   * @author DariusPi
   * @param isJump
   * @param cside
   * @param current
   * @return String
   */
  // line 484 "../../../../../PawnStateMachine.ump"
  public String isOpponentAdjacent(Boolean isJump, String cside, boolean current){
    int oR = getOpponentPawnRow();
    	int oC = getOpponentPawnColumn();
    	
    	int curR;
    	int curC;
		if (current){
			curR = getCurrentPawnRow();
    		curC = getCurrentPawnColumn();
		}
		else {
			curR = getNextRow(isJump,cside);
    		curC = getNextCol(isJump,cside);
		}
    	
    	if ((curR==oR)&&(curC==oC+1)){
    		return "left";
    	}
    	else if ((curR==oR)&&(curC==oC-1)){
    		return "right";
    	}
    	
    	else if ((curC==oC)&&(curR==oR+1)){
    		return "up";
    	}
    	else if ( (curC==oC)&&(curR==oR-1)){
    		return "down";
    	}
    	else {
    		return "no";
    	}
  }


  /**
   * 
   * Guard returns if board border is blocking move
   * @author DariusPi
   * @param opRow
   * @param opCol
   * @param opSide
   * @return boolean
   */
  // line 525 "../../../../../PawnStateMachine.ump"
  public Boolean isBorderBlocking(int opRow, int opCol, String opSide){
    if ((opSide.compareTo("up")==0)&&(opRow==1)){
    		return true;
    	}
    	
    	else if ((opSide.compareTo("down")==0)&&(opRow==9)){
    		return true;
    	}
    	
    	else if ((opSide.compareTo("left")==0)&&(opCol==1)){
    		return true;
    	}
    	
    	else if ((opSide.compareTo("right")==0)&&(opCol==9)){
    		return true;
    	}
    	else {
    		return false;
    	}
  }


  /**
   * Action to be called when an illegal move is attempted
   */
  // line 547 "../../../../../PawnStateMachine.ump"
  public void illegalMove(){
    status="illegal";
  }


  /**
   * 
   * Guard returns if desired step move is legal
   * @author DariusPi
   * @param isJump
   * @param cside
   */
  // line 557 "../../../../../PawnStateMachine.ump"
  public void legalMove(boolean isJump, String cside){
    status="success";
    	GamePosition curr= currentGame.getCurrentPosition();
		GamePosition next;
		int nT=getNextTile(isJump,cside);
		
		if (player.hasGameAsWhite()) {
			PlayerPosition pos=new PlayerPosition(player, currentGame.getQuoridor().getBoard().getTile(nT));
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
			//curr.setWhitePosition(new PlayerPosition(player, currentGame.getQuoridor().getBoard().getTile(nT)));
			if (isJump){
				currentGame.addMove(new JumpMove(currentGame.numberOfPositions()-2, 0, player, currentGame.getQuoridor().getBoard().getTile(nT),currentGame));
			}
			else {
				currentGame.addMove(new StepMove(currentGame.numberOfPositions()-2, 0, player, currentGame.getQuoridor().getBoard().getTile(nT),currentGame));
			}
		}
		else {
			PlayerPosition pos=new PlayerPosition(player, currentGame.getQuoridor().getBoard().getTile(nT));
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
			//curr.setBlackPosition(new PlayerPosition(player, currentGame.getQuoridor().getBoard().getTile(nT)));
			if (isJump){
				currentGame.addMove(new JumpMove(currentGame.numberOfPositions()-2, 1, player, currentGame.getQuoridor().getBoard().getTile(nT),currentGame));
			}
			else {
				currentGame.addMove(new StepMove(currentGame.numberOfPositions()-2, 1, player, currentGame.getQuoridor().getBoard().getTile(nT),currentGame));
			}
		}
		
		//change();
  }


  /**
   * 
   * Guard returns if desired move is a straight jump
   * @author DariusPi
   * @param cside
   * @return boolean
   */
  // line 625 "../../../../../PawnStateMachine.ump"
  public Boolean isAJump(String cside){
    String opSide=isOpponentAdjacent(false,"",true);
		if (cside.compareTo(opSide)==0){
			return true;
		}
		return false;
  }


  /**
   * 
   * Guard returns if desired move is a diagonal jump
   * @author DariusPi
   * @param cside
   * @return boolean
   */
  // line 640 "../../../../../PawnStateMachine.ump"
  public Boolean isDiag(String cside){
    if(cside.compareTo("upleft")==0){
			return true;
		} 
		
		else if(cside.compareTo("upright")==0){
			return true;
		} 
		else if(cside.compareTo("downleft")==0){
			return true;
		} 
		else if(cside.compareTo("downright")==0){
			return true;
		}
		return false;
  }


  /**
   * 
   * Method to get next tile
   * @param isJump
   * @param cside
   * @return integer
   */
  // line 663 "../../../../../PawnStateMachine.ump"
  public int getNextTile(boolean isJump, String cside){
    int curRow = getCurrentPawnRow()-1;
    	int curCol = getCurrentPawnColumn()-1;
		int r=curRow,j=curCol;
		int distance=2;
		if (!isJump){
			distance=1;
		}
		
		if(cside.compareTo("up")==0){
			r=curRow-distance;
		} 
		
		else if(cside.compareTo("right")==0){
			j=curCol+distance;
		} 
		else if(cside.compareTo("left")==0){
			j=curCol-distance;
		} 
		else if(cside.compareTo("down")==0){
			r=curRow+distance;
		}
		
		else if(cside.compareTo("upleft")==0){
			r=curRow-1;
			j=curCol-1;
		} 
		
		else if(cside.compareTo("upright")==0){
			r=curRow-1;
			j=curCol+1;
		} 
		else if(cside.compareTo("downleft")==0){
			r=curRow+1;
			j=curCol-1;
		} 
		else if(cside.compareTo("downright")==0){
			r=curRow+1;
			j=curCol+1;
		}
		
		return r*9+j;
  }


  /**
   * 
   * Method to get next row
   * @param isJump
   * @param cside
   * @return integer
   */
  // line 713 "../../../../../PawnStateMachine.ump"
  public int getNextRow(boolean isJump, String cside){
    return getNextTile(isJump,cside)/9;
  }


  /**
   * 
   * Method to get next column
   * @param isJump
   * @param cside
   * @return integer
   */
  // line 723 "../../../../../PawnStateMachine.ump"
  public int getNextCol(boolean isJump, String cside){
    return getNextTile(isJump,cside)%9;
  }


  public String toString()
  {
    return super.toString() + "["+
            "fals" + ":" + getFals()+ "," +
            "tru" + ":" + getTru()+ "," +
            "status" + ":" + getStatus()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "currentGame = "+(getCurrentGame()!=null?Integer.toHexString(System.identityHashCode(getCurrentGame())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "player = "+(getPlayer()!=null?Integer.toHexString(System.identityHashCode(getPlayer())):"null");
  }  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 729 "../../../../../PawnStateMachine.ump"
  enum MoveDirection 
  {
    East, South, West, North;
  }

  
}