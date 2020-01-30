package ca.mcgill.ecse223.quoridor.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.sql.Time;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import ca.mcgill.ecse223.quoridor.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.GameController;

import ca.mcgill.ecse223.quoridor.model.Game;
import ca.mcgill.ecse223.quoridor.model.Game.GameStatus;
import ca.mcgill.ecse223.quoridor.model.GamePosition;
import ca.mcgill.ecse223.quoridor.model.Move;
import ca.mcgill.ecse223.quoridor.model.Direction;

import ca.mcgill.ecse223.quoridor.model.Quoridor;
import ca.mcgill.ecse223.quoridor.model.Wall;
import ca.mcgill.ecse223.quoridor.model.WallMove;

public class QuoridorPage extends JFrame{ 

	/**
	 * default 
	 */
	private static final long serialVersionUID = 1L;
	
	public JLabel gameVicotryStatus;
	
	public JLabel errorMessage;
	private String error = null;
	
	private JLabel turnMessage1;
	private JLabel turnMessage2;
	
	private JLabel title;
	private JLabel bannerMessage;
	private String banner = "Main Menu";
	
	private JTextField p1NameField;
	private JLabel p1Name;
	
	private JTextField p2NameField;
	private JLabel p2Name;
	
	private JTextField minField;
	private JTextField secField;
	private JLabel timeRem1;
	private JLabel timeRem2;
	
	private JTextField saveField;
	private JTextField loadField;
	
	
	private JButton newGameButton;
	private JButton createP1Button;
	private JButton createP2Button;
	private JButton selectP1Button;
	private JButton selectP2Button;
	private JButton timeSetButton;
	
	
	private JButton saveGameButton;
	private JButton overwriteButton;
	private JButton cancelButton;
	private JButton loadGameButton;
	private JButton saveFileButton;
	private JButton loadFileButton;
	
	private JButton resignGameButton;
	private JButton drawGameButton;
	
	private JButton acceptDrawButton;
	private JButton declineDrawButton;
	
	private JButton replayGameButton;
	private JButton continueButton;
	private JButton stepForwardButton;
	private JButton stepBackwardButton;
	private JButton jumpStartButton;
	private JButton jumpEndButton;
	private JButton quitButton;
	
	private JButton endTurnButton;
	
	private final int buttonH=30;
	private final int buttonW=125;
	
	private boolean stageMove;
	
	private boolean currPlayer;	//true for white, false for black
	
	Timer timer;
	private boolean finished; // If game is over, result is shown
	
	private Quoridor q;
	private GameController gc;
	
	private QuoridorMouseListener listener;
	
	private TileComponent [][] tiles;
	public WallComponent [] bwalls;
	public WallComponent [] wwalls;
	private TileComponent[][] sq;
	private TileComponent[][] sq2;
	private Point [][] points;
	private Point [][] points2;
	
	private PawnComponent wPawn;
	private PawnComponent bPawn;
	
	private boolean isLoad;
	
	public QuoridorPage(){
		q=QuoridorApplication.getQuoridor();
		gc=new GameController();
		gc.initQuorridor();
		
		listener = new QuoridorMouseListener(this,gc);
        this.getContentPane().addMouseListener(listener);
        this.getContentPane().addMouseMotionListener(listener);
		initComponents();
		refreshData();
	}

	private void initComponents() {
		
		setSize(650, 800);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		
		stageMove=false; // players can move
		finished = false; // game is not over
		
		points=new Point[8][9];
		sq = new TileComponent [8][9];
		for (int i=0;i<8;i++) {
			for (int j=0;j<9;j++) {
				points[i][j]= new Point(192+i*50,232+j*50);
				sq[i][j]=new TileComponent();
				sq[i][j].setBounds((int)points[i][j].getX(), (int)points[i][j].getY(), 5, 5);
				add(sq[i][j]);
			}
		}
		
		points2=new Point[9][8];
		sq2 = new TileComponent [9][8];
		for (int i=0;i<9;i++) {
			for (int j=0;j<8;j++) {
				points2[i][j]= new Point(172+i*50,252+j*50);
				sq2[i][j]=new TileComponent();
				sq2[i][j].setBounds((int)points2[i][j].getX(), (int)points2[i][j].getY(), 5, 5);
				add(sq2[i][j]);
			}
		}
		
		currPlayer=true;
		
		turnMessage1= new JLabel();
		turnMessage1.setForeground(Color.RED);
		turnMessage1.setText("YOUR TURN!");
		turnMessage1.setVisible(false);
		
		turnMessage2= new JLabel();
		turnMessage2.setForeground(Color.RED);
		turnMessage2.setText("YOUR TURN!");
		turnMessage2.setVisible(false);
		
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);
		errorMessage.setText("");
		
		p1NameField=new JTextField();
		p1NameField.setVisible(false);
		p1Name=new JLabel();
		p1Name.setText("");
		p1Name.setVisible(false);
		
		p2NameField=new JTextField();
		p2NameField.setVisible(false);
		p2Name=new JLabel();
		p2Name.setText("");
		p2Name.setVisible(false);
		
		minField=new JTextField();
		minField.setVisible(false);
		secField=new JTextField();
		secField.setVisible(false);
		timeRem1=new JLabel();
		timeRem1.setText("");
		timeRem1.setVisible(false);
		timeRem2=new JLabel();
		timeRem2.setText("");
		timeRem2.setVisible(false);
		
		saveField=new JTextField();
		saveField.setVisible(false);
		loadField=new JTextField();
		loadField.setVisible(false);
		
		title = new JLabel();
		title.setFont(new Font("Calibri",Font.PLAIN,40));
		title.setText("Quoridor");
		
		bannerMessage = new JLabel();
		bannerMessage.setFont(new Font("Calibri",Font.PLAIN,28));
		bannerMessage.setForeground(Color.BLUE);
		bannerMessage.setBorder(BorderFactory.createLineBorder(new Color(47,194,4), 5));
		bannerMessage.setText(banner);
		
		initButtons();
		addListners();
		
		ActionListener count=new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boolean over=gc.countdown(q);
				if (over) {
					if(gc.getCurrentPlayerColor() == Color.BLACK) {
						finishGame("White wins!");
					}else {
						finishGame("Black wins!");
					}
				}
				else {
					if (q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsBlack()) {
						Time t=q.getCurrentGame().getBlackPlayer().getRemainingTime();
						timeRem2.setText(convT2S(t));
					}
					else {
						Time t=q.getCurrentGame().getWhitePlayer().getRemainingTime();
						timeRem1.setText(convT2S(t));
					}
				}
			}
		};
		timer=new Timer(1000,count);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Quoridor");
		
		tiles= new TileComponent[9][9];
		for (int i=0;i<9;i++) {
			for (int j=0;j<9;j++) {
				tiles[i][j]=new TileComponent();
			}
		}
		bwalls= new WallComponent[10];
		for (int i=0;i<10;i++) {
			bwalls[i]=new WallComponent(Color.BLACK,i+10);
		}
		
		wwalls= new WallComponent[10];
		for (int i=0;i<10;i++) {
			wwalls[i]=new WallComponent(Color.WHITE,i);
		}
		
		wPawn=new PawnComponent(Color.WHITE,this);
		bPawn=new PawnComponent(Color.BLACK,this);
		
		wPawn.setBounds(157, 417, 25, 25);
		bPawn.setBounds(557, 417, 25, 25);
		add(wPawn);
		add(bPawn);
		wPawn.setVisible(true);
		bPawn.setVisible(true);
		
		toggleBoard(false);
		getContentPane().setLayout(null);
		int y=260;
		
		title.setBounds(10, 10, 600, 40);
		add(title);
		bannerMessage.setBounds(10, 60, 600, 40);
		add(bannerMessage);
		errorMessage.setBounds(10, 110, 600, 15);
		add(errorMessage);
		p1Name.setBounds(10, 125, 300, 15);
		add(p1Name);
		timeRem1.setBounds(10, 140, 300, 15);
		add(timeRem1);
		turnMessage1.setBounds(10, 165, 100, 15);
		add(turnMessage1);
		
		newGameButton.setBounds(10, y, buttonW, buttonH);
		add(newGameButton);
		loadGameButton.setBounds(10, y+30, buttonW, buttonH);
		add(loadGameButton);
		saveGameButton.setBounds(10, y, buttonW, buttonH);
		add(saveGameButton);
		resignGameButton.setBounds(10, y+60, buttonW, buttonH);
		add(resignGameButton);
		drawGameButton.setBounds(10, y+90, buttonW, buttonH);
		add(drawGameButton);
		
		continueButton.setBounds(10, y-30, buttonW, buttonH);
		add(continueButton);
		stepForwardButton.setBounds(10, y, buttonW, buttonH);
		add(stepForwardButton);
		stepBackwardButton.setBounds(10, y+30, buttonW, buttonH);
		add(stepBackwardButton);
		jumpStartButton.setBounds(10, y+60, buttonW, buttonH);
		add(jumpStartButton);
		jumpEndButton.setBounds(10, y+90, buttonW, buttonH);
		add(jumpEndButton);
		quitButton.setBounds(10, y+120, buttonW, buttonH);
		add(quitButton);
		
		acceptDrawButton.setBounds(10, y, buttonW, buttonH);
		add(acceptDrawButton);
		declineDrawButton.setBounds(10, y+30, buttonW, buttonH);
		add(declineDrawButton);
		
		replayGameButton.setBounds(10, y+30, buttonW, buttonH);
		add(replayGameButton);
		
		endTurnButton.setBounds(10, y-30, buttonW, buttonH);		//could be placed better
		add(endTurnButton);
		
		p1NameField.setBounds(10, 160, 200, buttonH);
		add(p1NameField);
		
		p2NameField.setBounds(10, 160, 200, buttonH);
		add(p2NameField);
		
		minField.setBounds(10, 160, buttonW, buttonH);
		add(minField);
		
		secField.setBounds(140, 160, buttonW, buttonH);
		add(secField);
		
		createP1Button.setBounds(270, 160, buttonW, buttonH);
		add(createP1Button);
		
		selectP1Button.setBounds(400, 160, buttonW, buttonH);
		add(selectP1Button);
		
		createP2Button.setBounds(270, 160, buttonW, buttonH);
		add(createP2Button);
		
		selectP2Button.setBounds(400, 160, buttonW, buttonH);
		add(selectP2Button);
		
		timeSetButton.setBounds(270, 160, buttonW, buttonH);
		add(timeSetButton);
		
		saveField.setBounds(10, 160, 200, buttonH);
		add(saveField);
		
		saveFileButton.setBounds(270, 160, buttonW, buttonH);
		add(saveFileButton);
		
		overwriteButton.setBounds(10, y, buttonW, buttonH);
		add(overwriteButton);
		
		cancelButton.setBounds(10, y+30, buttonW, buttonH);
		add(cancelButton);
		
		loadField.setBounds(10, 160, 200, buttonH);
		add(loadField);
		
		loadFileButton.setBounds(270, 160, buttonW, buttonH);
		add(loadFileButton);
		
		
		p2Name.setBounds(10, 675, 300, 15);
		add(p2Name);
		timeRem2.setBounds(10, 690, 300, 15);
		add(timeRem2);
		turnMessage2.setBounds(10, 715, 100, 15);
		add(turnMessage2);
		
		for (int i=0;i<10;i++) {
			wwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 125, WallComponent.wallW, WallComponent.wallH);
			add(wwalls[i]);
			bwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 675, WallComponent.wallW, WallComponent.wallH);
			add(bwalls[i]);
		}
		
		for (int i=0;i<9;i++) {
			for (int j=0;j<9;j++) {
				tiles[i][j].setBounds(150+50*i,210+50*j, 40, 40);
				add(tiles[i][j]);
			}
		}
		
		
	}

	private void refreshData() {
		errorMessage.setText(error);
		bannerMessage.setText(banner);
		if (error == null || error.length() == 0) {
			//update
		}
		
	}
	
	/**
	 * Helper method to end game and display a result
	 * @param result
	 */
	public void finishGame(String result) {
		finished = true; // Indicate the result is being shown
		stageMove = true; // Prevent player from moving
		// Set screen to results
		banner = result;
		
		timer.stop();
		timeRem1.setVisible(false);
		timeRem2.setVisible(false);
		
		newGameButton.setVisible(false);
		saveGameButton.setVisible(true);
		loadGameButton.setVisible(false);
		resignGameButton.setVisible(false);
		drawGameButton.setVisible(false);
		
		acceptDrawButton.setVisible(false);
		declineDrawButton.setVisible(false);
		
		replayGameButton.setVisible(true);
		quitButton.setVisible(true);
		
		endTurnButton.setVisible(false);
		turnMessage1.setVisible(false);
		turnMessage2.setVisible(false);
		toggleBoard(false);
		error=q.getCurrentGame().getGameStatus().toString();
		refreshData();
	}
	
	private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = "";
		isLoad = false;
		
		newGameButton.setVisible(false);
		loadGameButton.setVisible(false);
		
		// call the controller
		gc.initGame(q);
		
		p1NameField.setVisible(true);
		createP1Button.setVisible(true);
		selectP1Button.setVisible(true);
		
		for (int i=0;i<10;i++) {
			if (wwalls[i].getDirection().compareTo("horizontal")==0) {
				wwalls[i].rotate();
			}
			
			if (bwalls[i].getDirection().compareTo("horizontal")==0) {
				bwalls[i].rotate();
			}
			
			wwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 125, WallComponent.wallW, WallComponent.wallH);
			bwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 675, WallComponent.wallW, WallComponent.wallH);
		}
		
		wPawn.setBounds(157, 417, 25, 25);
		bPawn.setBounds(557, 417, 25, 25);
		
		gc.addWalls();
		stageMove=false;
		
		banner="New Game";
		
		refreshData();
	}
	
	private void createP1ButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error="";
		String success= gc.createUsername(q,p1NameField.getText(),"white");
		if (success.compareTo(p1NameField.getText())==0) {
			p1NameField.setVisible(false);
			p2NameField.setVisible(true);
			createP1Button.setVisible(false);
			createP2Button.setVisible(true);
			selectP1Button.setVisible(false);
			selectP2Button.setVisible(true);
			
			p1Name.setText(success);
			refreshData();
		}
		else {
			error=success;
			p1NameField.setText("");
			refreshData();
		}
	}
	
	private void createP2ButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error="";
		String success= gc.createUsername(q,p2NameField.getText(),"black");
		if (success.compareTo(p2NameField.getText())==0) {
			p2NameField.setVisible(false);
			createP2Button.setVisible(false);
			selectP2Button.setVisible(false);
			minField.setVisible(true);
			secField.setVisible(true);
			timeSetButton.setVisible(true);
			
			p2Name.setText(success);
			refreshData();
			
		}
		else {
			error=success;
			p2NameField.setText("");
			refreshData();
		}
	}
	
	private void selectP1ButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error="";
		String success= gc.selectUsername(q,p1NameField.getText(),"white");
		if (success.compareTo(p1NameField.getText())==0) {
			p1NameField.setVisible(false);
			p2NameField.setVisible(true);
			createP1Button.setVisible(false);
			createP2Button.setVisible(true);
			selectP1Button.setVisible(false);
			selectP2Button.setVisible(true);
			
			p1Name.setText(success);
			refreshData();
			
		}
		else {
			error=success;
			p1NameField.setText("");
			refreshData();
		}
	}
	
	private void selectP2ButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error="";
		String success= gc.selectUsername(q,p2NameField.getText(),"black");
		if (success.compareTo(p2NameField.getText())==0) {
			p2NameField.setVisible(false);
			createP2Button.setVisible(false);
			selectP2Button.setVisible(false);
			minField.setVisible(true);
			secField.setVisible(true);
			timeSetButton.setVisible(true);
			
			p2Name.setText(success);
			refreshData();
			
		}
		else {
			error=success;
			p2NameField.setText("");
			refreshData();
		}
	}
	
	private void timeSetButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error="";
		try {
			gc.setTime(q,Integer.parseInt(minField.getText()),Integer.parseInt(secField.getText()));
		} catch (Exception e) {
			error ="invalid input";
		}
		
		if (error.compareTo("")==0) {
			minField.setVisible(false);
			secField.setVisible(false);
			timeSetButton.setVisible(false);
			
			newGameButton.setVisible(false);
			
			toggleMainButtons(true);
			toggleBoard(true);
			banner="GamePlay";
			
			Time t=q.getCurrentGame().getBlackPlayer().getRemainingTime();
			timeRem1.setText(convT2S(t));
			timeRem2.setText(convT2S(t));
			if (currPlayer) {
				timeRem1.setVisible(true);
				timeRem2.setVisible(false);
				turnMessage1.setVisible(true);
				turnMessage2.setVisible(false);
			}
			else {
				timeRem1.setVisible(false);
				timeRem2.setVisible(true);
				turnMessage1.setVisible(false);
				turnMessage2.setVisible(true);
			}
		
			//timeRem1.setVisible(true);
			
			p1Name.setVisible(true);
			p2Name.setVisible(true);
			
			wPawn.init(true);
			bPawn.init(false);
			finished = false; 
			gc.startTheClock(q,timer);
			
			refreshData();
			System.out.println("Positions");
			List<GamePosition> gp=q.getCurrentGame().getPositions();
			for (GamePosition pos: gp) {
				System.out.println(pos.toString());
			}
			
		}
		else {
			secField.setText("");
			minField.setText("");
			refreshData();
		}
	}
	
	
	private void saveGameButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		if (finished) {
			listener.returnObject();
			error="";
			toggleBoard(false);
			toggleMainButtons(false);
			
			saveFileButton.setVisible(true);
			saveField.setVisible(true);
			banner = "Save Game";
		}
		
		else if (stageMove) {
			error="Must End Turn Before Performing Action";
		}
		else {
			listener.returnObject();
			error="";
			toggleBoard(false);
			toggleMainButtons(false);
			
			saveFileButton.setVisible(true);
			saveField.setVisible(true);
			banner = "Save Game";
		}
		refreshData();
	}
	
	private void saveFileButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
		// clear error message
		error = "";	
		String filename=saveField.getText();
		if (filename.length()<5) {
			error="FileName must be non empty and have a .dat extension";
		}
		else if (filename.substring(filename.length()-4).compareTo(".dat")!=0){
			error="FileName must be non empty and have a .dat extension";
		}
		else {
			Boolean fileExist=gc.filename_exists(saveField.getText());
			
			saveFileButton.setVisible(false);
			saveField.setVisible(false);
			
			if (fileExist) {
				overwriteButton.setVisible(true);
				cancelButton.setVisible(true);
				error = "File already exists, overwrite?";
				refreshData();
			}
			else {
				//call the save game controller method
				error="";
				gc.SaveGame(q, saveField.getText());
				
				String movFilename=saveField.getText().substring(0, saveField.getText().length()-3)+"mov";
				gc.saveMoves(q, movFilename,finished);
				
				if (finished) {
					banner="Game Over";
					quitButton.setVisible(true);
					replayGameButton.setVisible(true);
					saveGameButton.setVisible(true);
					error=q.getCurrentGame().getGameStatus().toString();
					//toggleBoard(true);
				}
				else {
					banner = "GamePlay"; 
					toggleMainButtons(true);
					toggleBoard(true);
				}
				// update visuals
			}
		}
		refreshData();
	}
	
	private void overwriteButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
		error = "";
		gc.SaveGame(q, saveField.getText());
		
		String movFilename=saveField.getText().substring(0, saveField.getText().length()-3)+"mov";
		
		gc.saveMoves(q, movFilename,finished);
		// update visuals
		banner = "GamePlay"; 
		overwriteButton.setVisible(false);
		cancelButton.setVisible(false);
		if (finished) {
			banner = "Game Over"; 
			quitButton.setVisible(true);
			replayGameButton.setVisible(true);
			saveGameButton.setVisible(true);
			error=q.getCurrentGame().getGameStatus().toString();
		}
		else {
			toggleMainButtons(true);
			toggleBoard(true);
		}
		refreshData();		
	}
	
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// update visuals
		error = "";
		cancelButton.setVisible(false);
		overwriteButton.setVisible(false);
		if (finished) {
			banner = "Game Over"; 
			quitButton.setVisible(true);
			replayGameButton.setVisible(true);
			saveGameButton.setVisible(true);
			error=q.getCurrentGame().getGameStatus().toString();
		}
		else {
			banner = "GamePlay"; 
			toggleMainButtons(true);
			toggleBoard(true);
		}
		
		refreshData();		
	}
	
	private void loadGameButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		newGameButton.setVisible(false);
		toggleBoard(false); 
		toggleMainButtons(false);
		loadGameButton.setVisible(false);
		loadFileButton.setVisible(true);
		loadField.setVisible(true);
		
		// update visuals
		banner = "Load Game"; 
		refreshData();
	}
	
	private void loadFileButtonActionPerformed(java.awt.event.ActionEvent evt) throws Exception {
		gc= new GameController();
		error = "";
		for (int i=0;i<10;i++) {
			if (wwalls[i].getDirection().compareTo("horizontal")==0) {
				wwalls[i].rotate();
			}
			
			if (bwalls[i].getDirection().compareTo("horizontal")==0) {
				bwalls[i].rotate();
			}
			
			wwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 125, WallComponent.wallW, WallComponent.wallH);
			bwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 675, WallComponent.wallW, WallComponent.wallH);
		}
		
		timer.stop();
		String filename = loadField.getText();
		if (filename.length()<5) {
			error="FileName must be non empty and have a .dat extension";
		}
		else if (filename.substring(filename.length()-4).compareTo(".dat")!=0){
			error="FileName must be non empty and have a .dat extension";
		}
		else {
			String movFilename=filename.substring(0, filename.length()-3)+"mov";
			int load=gc.loadMoves(q, movFilename);
			//gc.loadGame(QuoridorApplication.getQuoridor(), filename);
			
			if (load==-1) {
				error="Invalid File";
				q.getCurrentGame().setCurrentPosition(q.getCurrentGame().getPosition(0));
				int j=q.getCurrentGame().getPositions().size()-1; //number of positions to delete
				for (int k=0;k<j;k++) {
					q.getCurrentGame().getPosition(q.getCurrentGame().getPositions().size()-1).delete();
					Move m=q.getCurrentGame().getMove(q.getCurrentGame().getMoves().size()-1);
					m.delete();
					q.getCurrentGame().removeMove(m);
				}
			}
			else if (load==1) {
				currPlayer=q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite();
				changeBoard();
				loadFileButton.setVisible(false);
				loadField.setVisible(false);
				toggleBoard(true);
				toggleReplayButtons(true);
				banner="Replay Mode";
				finished=true;
				stageMove=true;
			}
			else {
				currPlayer=q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite();
				changeBoard();
			
				loadFileButton.setVisible(false);
				loadField.setVisible(false);
				p1NameField.setVisible(true);
				createP1Button.setVisible(true);
				selectP1Button.setVisible(true);
				banner="New Game";
			}
		}
		refreshData();
	}
	
	private void resignGameButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (stageMove) {
			error="Must End Turn Before Performing Action";
		}
		else {
			error="";
			listener.returnObject();
			gc.resignGame(q);
			if(gc.getCurrentPlayerColor() == Color.BLACK) {
				finishGame("White wins!");
			}else {
				finishGame("Black wins!");
			}
		}
		refreshData();
	}
	
	private void drawGameButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (stageMove) {
			error="Must End Turn Before Performing Action";
		}
		else {
			listener.returnObject();
			// update visuals
			toggleBoard(false);		
			error = "";
			
			toggleMainButtons(false);
			acceptDrawButton.setVisible(true);
			declineDrawButton.setVisible(true);
			banner = "Draw Proposal";
		}
		refreshData();
	}
	
	private void acceptDrawButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = "";
		
		q.getCurrentGame().setGameStatus(GameStatus.Draw);
		error = "accept draw"; 
		finishGame("The game was a draw!");
	}
	
	private void declineDrawButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// update visuals	
		toggleBoard(true);
		error = "";
		toggleMainButtons(true);
		acceptDrawButton.setVisible(false);
		declineDrawButton.setVisible(false);
		banner = "GamePlay"; //for testing
	}
	
	
	private void replayGameButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (finished) {
			// clear error message		
			listener.returnObject();
			toggleBoard(true);
			error = "";
			timer.stop();
			toggleMainButtons(false);
			toggleReplayButtons(true);
			replayGameButton.setVisible(false);
			gc.initReplay(q);
			stageMove=true;
			banner = "Replay Mode";
		}
		else if (stageMove) {
			error="Must End Turn Before Performing Action";
		}
		else {
			// clear error message		
			listener.returnObject();
			toggleBoard(true);
			error = "";
			timer.stop();
			toggleMainButtons(false);
			toggleReplayButtons(true);
			replayGameButton.setVisible(false);
			gc.initReplay(q);
			stageMove=true;
			banner = "Replay Mode";
		}
		refreshData();
	}
	
	private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message		
		error = "";
		if (finished) {
			error="Finished games cannot be continued";
		}
		else {
			boolean canContinue=gc.continueGame(q);
			if (canContinue) {
				toggleReplayButtons(false);
				
				toggleMainButtons(true);
				
				timeRem1.setVisible(true);
				timeRem2.setVisible(true);
				
				if (q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite()) {
					turnMessage1.setVisible(true);
				}
				else {
					turnMessage2.setVisible(true);
				}
				Time t=q.getCurrentGame().getBlackPlayer().getRemainingTime();
				timeRem1.setText(convT2S(t));
				timeRem2.setText(convT2S(t));
				stageMove=false;
				timer.start();
				banner = "GamePlay";
			}
			else {
				error="Finished games cannot be continued";
			}
			
		}
	
		refreshData();
	}
	
	private void stepForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message		
		error = "";
		boolean success=gc.stepForward(q);
		if(success) {
			for (int i=0;i<10;i++) {
				if (wwalls[i].getDirection().compareTo("horizontal")==0) {
					wwalls[i].rotate();
				}
				
				if (bwalls[i].getDirection().compareTo("horizontal")==0) {
					bwalls[i].rotate();
				}
				
				wwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 125, WallComponent.wallW, WallComponent.wallH);
				bwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 675, WallComponent.wallW, WallComponent.wallH);
			}
			currPlayer=q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite();
			changeBoard();
		}
		else {
			error="Already at final position";
		}
		
		refreshData();
	}
	
	private void stepBackwardButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message		
		error = "";
		
		boolean success=gc.stepBackward(q);
		if(success) {
			for (int i=0;i<10;i++) {
				if (wwalls[i].getDirection().compareTo("horizontal")==0) {
					wwalls[i].rotate();
				}
				
				if (bwalls[i].getDirection().compareTo("horizontal")==0) {
					bwalls[i].rotate();
				}
				
				wwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 125, WallComponent.wallW, WallComponent.wallH);
				bwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 675, WallComponent.wallW, WallComponent.wallH);
			}
			currPlayer=q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite();
			changeBoard();
		}
		else {
			error="Already at first position";
		}
		refreshData();
	}
	
	private void jumpStartButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message		
		error = "";
		boolean success=gc.jumpToStart(q);
		if(success) {
			for (int i=0;i<10;i++) {
				if (wwalls[i].getDirection().compareTo("horizontal")==0) {
					wwalls[i].rotate();
				}
				
				if (bwalls[i].getDirection().compareTo("horizontal")==0) {
					bwalls[i].rotate();
				}
				
				wwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 125, WallComponent.wallW, WallComponent.wallH);
				bwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 675, WallComponent.wallW, WallComponent.wallH);
			}
			currPlayer=q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite();
			changeBoard();
		}
		else {
			error="Already at first position";
		}
		
		refreshData();
	}
	
	private void jumpEndButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message	
		error = "";
		boolean success=gc.jumpToFinal(q);
		if(success) {
			for (int i=0;i<10;i++) {
				if (wwalls[i].getDirection().compareTo("horizontal")==0) {
					wwalls[i].rotate();
				}
				
				if (bwalls[i].getDirection().compareTo("horizontal")==0) {
					bwalls[i].rotate();
				}
				
				wwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 125, WallComponent.wallW, WallComponent.wallH);
				bwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 675, WallComponent.wallW, WallComponent.wallH);
			}
			currPlayer=q.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsWhite();
			changeBoard();
		}
		else {
			error="Already at final position";
		}
		
		refreshData();
	}
	
	private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {	
		listener.returnObject();
		toggleBoard(false);
		
		error = "";
		timer.stop();
		newGameButton.setVisible(true);
		loadGameButton.setVisible(true);
		saveGameButton.setVisible(false);
		resignGameButton.setVisible(false);
		drawGameButton.setVisible(false);
		
		p1Name.setVisible(false);
		p2Name.setVisible(false);
		timeRem1.setVisible(false);
		timeRem2.setVisible(false);
		
		turnMessage1.setVisible(false);
		turnMessage2.setVisible(false);
		
		toggleReplayButtons(false);
		
		endTurnButton.setVisible(false);
		stageMove=false;
		// update visuals
		banner = "Main Menu"; //for testing
		currPlayer=true;
		q.getCurrentGame().delete();
		refreshData();
	}
	
	private void endTurnButtonActionPerformed(java.awt.event.ActionEvent evt) {	//this is the same as switch player
		error = "";
		if (!stageMove) {
			error = "Must perform a move before ending turn";
			refreshData();
			return;
		}
		boolean isOver=gc.checkResult(q);
		boolean isDraw = gc.drawCheck(q);
		
		List<Move> mov=q.getCurrentGame().getMoves();
		System.out.println("end of turn");
		System.out.println("Moves: "+mov.size());
		for (Move m:mov) {
			if (m instanceof WallMove) {
				System.out.println("wall  destination:"+m.getTargetTile().getRow()+","+m.getTargetTile().getColumn());
			}
			else {
				System.out.println("destination:"+m.getTargetTile().getRow()+","+m.getTargetTile().getColumn());
			}
		}
		System.out.println("Postiions");
		List<GamePosition> gp=q.getCurrentGame().getPositions();
		for (GamePosition pos: gp) {
			System.out.println("pos id "+pos.getId());
			System.out.println("white walls on board "+pos.getWhiteWallsOnBoard().size());
			System.out.println("black walls on board "+pos.getBlackWallsOnBoard().size());
		}
		if (!isOver) {
			if (isDraw) {
				finishGame("Game ended in a draw!");
				return;
			}
			currPlayer=!currPlayer;
			//gc.switchPlayer(q);
			if (currPlayer) {
				timeRem1.setVisible(true);
				timeRem2.setVisible(false);
				turnMessage1.setVisible(true);
				turnMessage2.setVisible(false);
			}
			else {
				timeRem1.setVisible(false);
				timeRem2.setVisible(true);
				turnMessage1.setVisible(false);
				turnMessage2.setVisible(true);
			}
			stageMove=false;
			timer.start();
			refreshData();
			
		}
		else {
			if (currPlayer) {
				finishGame("White Wins!");
			}
			else {
				finishGame("Black Wins!");
			}
			
		}
		
		
	}
	
	private void initButtons() {
		
		newGameButton = new JButton();
		newGameButton.setText("New Game");
		
		createP1Button= new JButton();
		createP1Button.setText("Create P1");
		createP1Button.setVisible(false);
		
		createP2Button= new JButton();
		createP2Button.setText("Create P2");
		createP2Button.setVisible(false);
		
		selectP1Button= new JButton();
		selectP1Button.setText("Select P1");
		selectP1Button.setVisible(false);
		
		selectP2Button= new JButton();
		selectP2Button.setText("Select P2");
		selectP2Button.setVisible(false);
		
		timeSetButton= new JButton();
		timeSetButton.setText("Set Time");
		timeSetButton.setVisible(false);
		
		saveGameButton = new JButton();
		saveGameButton.setText("Save Game");
		saveGameButton.setVisible(false);
		
		saveFileButton=new JButton();
		saveFileButton.setText("Save");
		saveFileButton.setVisible(false);
		
		overwriteButton=new JButton();
		overwriteButton.setText("Overwrite");
		overwriteButton.setVisible(false);
		
		cancelButton=new JButton();
		cancelButton.setText("Cancel");
		cancelButton.setVisible(false);
		
		loadGameButton = new JButton();
		loadGameButton.setText("Load Game");
		
		loadFileButton=new JButton();
		loadFileButton.setText("Load");
		loadFileButton.setVisible(false);
		
		resignGameButton = new JButton();
		resignGameButton.setText("Resign Game");
		resignGameButton.setVisible(false);
		
		drawGameButton = new JButton();
		drawGameButton.setText("Offer Draw");
		drawGameButton.setVisible(false);
		
		acceptDrawButton = new JButton();
		acceptDrawButton.setText("Accept");
		acceptDrawButton.setVisible(false);
		
		declineDrawButton = new JButton();
		declineDrawButton.setText("Decline");
		declineDrawButton.setVisible(false);
		
		//replay button
		replayGameButton = new JButton();
		replayGameButton.setText("Replay Mode");
		replayGameButton.setVisible(false);
		
		//replay mode buttons
		continueButton = new JButton();
		continueButton.setText("Continue");
		continueButton.setVisible(false);
		
		stepForwardButton = new JButton();
		stepForwardButton.setText("Step Forward");
		stepForwardButton.setVisible(false);
		
		stepBackwardButton = new JButton();
		stepBackwardButton.setText("Step Backward");
		stepBackwardButton.setVisible(false);
		
		jumpStartButton = new JButton();
		jumpStartButton.setText("Jump Start");
		jumpStartButton.setVisible(false);
		
		jumpEndButton = new JButton();
		jumpEndButton.setText("Jump End");
		jumpEndButton.setVisible(false);
		
		quitButton = new JButton();
		quitButton.setText("Quit");
		quitButton.setVisible(false);
		
		endTurnButton= new JButton();
		endTurnButton.setText("End Turn");
		endTurnButton.setVisible(false);
		
	}
	
	private void addListners() {
		newGameButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newGameButtonActionPerformed(evt);
			}
		});
		
		createP1Button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				createP1ButtonActionPerformed(evt);
			}
		});
		
		createP2Button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				createP2ButtonActionPerformed(evt);
			}
		});
		
		selectP1Button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectP1ButtonActionPerformed(evt);
			}
		});
		
		selectP2Button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectP2ButtonActionPerformed(evt);
			}
		});
		
		timeSetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				timeSetButtonActionPerformed(evt);
			}
		});
		
		
		saveGameButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveGameButtonActionPerformed(evt);
			}
		});
		
		saveFileButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					saveFileButtonActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		overwriteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					overwriteButtonActionPerformed(evt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
		
		loadGameButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadGameButtonActionPerformed(evt);
			}
		});
		
		loadFileButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					loadFileButtonActionPerformed(evt);
				} catch (Exception e) {
					//This allows for user to know exactly *which* error occurred in a concise 
					//manner
					//if (e.getMessage.contentEquals("Unable to create wall due to owner")) {
					//	try  {
					//		loadFileActionPerformed(evt)
					//	} catch (Exception e1) {
					//		error = e.getMessage();
					//		timer.stop();
					//		QuoridorApplication.getQuoridor().getCurrentGame().delete
					//	}
					//}
					error = e.getMessage();
					timer.stop();
					try {
						QuoridorApplication.getQuoridor().getCurrentGame().delete();
					} catch (NullPointerException e1) {
						try {
							loadFileButtonActionPerformed(new java.awt.event.ActionEvent(null, buttonH, banner));
						} catch (Exception e2) {
							
							e2.printStackTrace();
						}
						//do nothing
					}
					
					refreshData();
				}
			}
		});
		
		resignGameButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resignGameButtonActionPerformed(evt);
			}
		});
		
		drawGameButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				drawGameButtonActionPerformed(evt);
			}
		});
		
		acceptDrawButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				acceptDrawButtonActionPerformed(evt);
			}
		});
		
		declineDrawButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				declineDrawButtonActionPerformed(evt);
			}
		});
		
		replayGameButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				replayGameButtonActionPerformed(evt);
			}
		});
		
		continueButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				continueButtonActionPerformed(evt);
			}
		});
		
		stepForwardButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stepForwardButtonActionPerformed(evt);
			}
		});
		
		stepBackwardButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				stepBackwardButtonActionPerformed(evt);
			}
		});
		
		jumpStartButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jumpStartButtonActionPerformed(evt);
			}
		});
		
		jumpEndButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jumpEndButtonActionPerformed(evt);
			}
		});
		
		quitButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				quitButtonActionPerformed(evt);
			}
		});
		
		endTurnButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				endTurnButtonActionPerformed(evt);
			}
		});
		
	}
	
	
	
	private void toggleBoard(boolean vis) {
		//boolean vis=!tiles[0][0].isVisible();
		for (int i=0;i<9;i++) {
			for (int j=0;j<9;j++) {
				tiles[i][j].setVisible(vis);
			}
			wwalls[i].setVisible(vis);
			bwalls[i].setVisible(vis);
		}
		wwalls[9].setVisible(vis);
		bwalls[9].setVisible(vis);
		
		wPawn.setVisible(vis);
		bPawn.setVisible(vis);
		
		for (int i=0;i<8;i++) {
			for (int j=0;j<9;j++) {
				sq[i][j].setVisible(vis);
			}
		}
		
		for (int i=0;i<9;i++) {
			for (int j=0;j<8;j++) {
				sq2[i][j].setVisible(vis);
			}
		}
		
	}
	
	private void toggleMainButtons(boolean vis) {
		quitButton.setVisible(vis);
		saveGameButton.setVisible(vis);
		//loadGameButton.setVisible(vis);
		resignGameButton.setVisible(vis);
		drawGameButton.setVisible(vis);
		endTurnButton.setVisible(vis);
		replayGameButton.setVisible(vis);
		if (currPlayer) {
			turnMessage1.setVisible(vis);
		}
		else {
			turnMessage2.setVisible(vis);
		}
	}
	
	private void toggleReplayButtons(boolean vis) {
		continueButton.setVisible(vis);
		replayGameButton.setVisible(vis);
		stepForwardButton.setVisible(vis);
		stepBackwardButton.setVisible(vis);
		jumpStartButton.setVisible(vis);
		jumpEndButton.setVisible(vis);
		quitButton.setVisible(vis);
		
	}
	
	/**
	 * Method converts Time to string
	 * 
	 * @author DariusPi
	 */
	private String convT2S(Time t) {
		long tt=t.getTime();
		int min=(int)(tt/1000)/60;
		int sec=(int)(tt/1000)%60;
		return("Time Remainning: "+min+":"+sec);
	}
	
	/**
	 * Method sets whether a wall move or player move was performed to block further ones
	 * 
	 * @author DariusPi
	 */
	public void setStageMove(boolean moved) {
		stageMove=moved;
	}
	
	/**
	 * Method returns whether a wall move or player move was performed 
	 * 
	 * @author DariusPi
	 */
	public boolean getStageMove() {
		return stageMove;
	}
	
	/*
	 * Method gets currently held component by player
	 * @author louismollick
	 */
	public HoldableComponent getHeldComponent() {
		return this.listener.getHeldComponent();
	}
	
	/*
	 * Method sets currently heldComponent
	 * @author louismollick
	 */
	public void setHeldComponent(HoldableComponent hold) {
		this.listener.setHeldComponent(hold);
	}
	
	/*
	 * Method sets currently heldComponent to random wall, for Step definition
	 * @author louismollick
	 */
	public void setHeldComponentToRandomWall(String color) throws Exception{
		WallComponent w;
		if (color.contentEquals("white")) {
			if(wwalls != null) w = wwalls[5]; // take any Wall
			else throw new Exception("There are no white walls");
		} else {
			if(bwalls != null) w = bwalls[5];
			else throw new Exception("There are no white walls");
		}
		this.listener.setHeldComponent(w);
	}
	
	/*
	 * Method returns whether the player has a wall in his hand
	 * @author louismollick
	 */
	public boolean hasHeldWall() {
		return this.listener.hasHeldWall();
	}
	/**
	 * Method returns 2d array of tiles forming the board
	 * 
	 * @author louismollick
	 */
	public TileComponent[][] getTiles(){
		return this.tiles;
	}
	
	/**
	 * * Helper method returning if the final result is being displayed
	 * @author louismollick
	 */
	public boolean isFinalResultVisible() {
		return finished;
	}
	
	//helper
	public boolean gettimeRem2() {
		return(timer.isRunning());
	}
	public boolean getVisibilityTurnMessage1() {
		return(turnMessage1.isVisible());	
	}
	public boolean getVisibilityTurnMessage2() {
		return(turnMessage2.isVisible());	
	}
	//helper
	
	public Quoridor getQ() {
		return q;
	}
	
	public void changeBoard() {
		int xb=q.getCurrentGame().getCurrentPosition().getBlackPosition().getTile().getColumn();
		int yb=q.getCurrentGame().getCurrentPosition().getBlackPosition().getTile().getRow();
		int xw=q.getCurrentGame().getCurrentPosition().getWhitePosition().getTile().getColumn();
		int yw=q.getCurrentGame().getCurrentPosition().getWhitePosition().getTile().getRow();
		
		wPawn.setBounds(107+xw*50, 167+yw*50, 25, 25);
		bPawn.setBounds(107+xb*50, 167+yb*50, 25, 25);
		
		int width,height,x,y; boolean vert;
		
		GamePosition cur=q.getCurrentGame().getCurrentPosition();
		for (int i=0;i<10;i++) {
			boolean there=false;
			for (Wall w:cur.getWhiteWallsOnBoard()) {
				if(w.equals(q.getCurrentGame().getWhitePlayer().getWall(i))) {
					there=true;
					break;
				}
			}
			if (there) {
			//if (q.getCurrentGame().getWhitePlayer().getWall(i).getMove()!=null) {
				if (q.getCurrentGame().getWhitePlayer().getWall(i).getMove().getWallDirection()==Direction.Horizontal) {
					vert=false;
					width=WallComponent.wallH;
					height=WallComponent.wallW;
					
				}
				else {
					vert=true;
					width=WallComponent.wallW;
					height=WallComponent.wallH;
				}
				x=q.getCurrentGame().getWhitePlayer().getWall(i).getMove().getTargetTile().getColumn();
				y=q.getCurrentGame().getWhitePlayer().getWall(i).getMove().getTargetTile().getRow();
				
				if(vert) {
					wwalls[i].setBounds((int)points[x-1][y-1].getX(),(int)points[x-1][y-1].getY(),width,height);
				}
				else {
					wwalls[i].rotate();
					wwalls[i].setBounds((int)points2[x-1][y-1].getX(),(int)points2[x-1][y-1].getY(),width,height);
				}
			}
			else {
				wwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 125, WallComponent.wallW, WallComponent.wallH);
			}
			
			there=false;
			for (Wall w:cur.getBlackWallsOnBoard()) {
				if(w.equals(q.getCurrentGame().getBlackPlayer().getWall(i))) {
					there=true;
					break;
				}
			}
			if (there) {
				if (q.getCurrentGame().getBlackPlayer().getWall(i).getMove().getWallDirection()==Direction.Horizontal) {
					vert=false;
					width=WallComponent.wallH;
					height=WallComponent.wallW;
				}
				else {
					vert=true;
					width=WallComponent.wallW;
					height=WallComponent.wallH;
				}
				x=q.getCurrentGame().getBlackPlayer().getWall(i).getMove().getTargetTile().getColumn();
				y=q.getCurrentGame().getBlackPlayer().getWall(i).getMove().getTargetTile().getRow();
				
				if(vert) {
					bwalls[i].setBounds((int)points[x-1][y-1].getX(),(int)points[x-1][y-1].getY(),width,height);
				}
				else {
					bwalls[i].rotate();
					bwalls[i].setBounds((int)points2[x-1][y-1].getX(),(int)points2[x-1][y-1].getY(),WallComponent.wallH,WallComponent.wallW);
				}
			}
			else {
				bwalls[i].setBounds(380+(WallComponent.wallW+10)*i, 675, WallComponent.wallW, WallComponent.wallH);
			}
		}
	}
}