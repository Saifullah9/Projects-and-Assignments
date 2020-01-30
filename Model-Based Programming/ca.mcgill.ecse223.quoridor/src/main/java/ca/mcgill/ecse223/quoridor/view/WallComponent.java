package ca.mcgill.ecse223.quoridor.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import ca.mcgill.ecse223.quoridor.controller.GameController;

public class WallComponent extends HoldableComponent{
	public static final int wallH=70;
	public static final int wallW=12;
	private String dir;
	public int wallId; 	//0-9 are white, 10-19 are black
	private Point [][] points; // Possible vertical wall locations
	private Point [][] points2; // Possible horizontal wall locations
	private int x1;
	private int y1;
	
	public WallComponent(Color c, int id) {
		super(wallW, wallH, c);
		this.dir = "vertical";
		this.wallId=id;
		
		x1=-1;
		y1=-1;
		
		this.points=new Point[8][9];	//for vertical placement
		for (int i=0;i<8;i++) {
			for (int j=0;j<9;j++) {
				this.points[i][j]= new Point(192+i*50,232+j*50);
			}
		}
		this.points2=new Point[9][9];
		for (int i=0;i<9;i++) {
			for (int j=0;j<9;j++) {
				this.points2[i][j]= new Point(172+i*50,252+j*50);
			}
		}
	}
	public String rotate() {
		if (this.dir.contentEquals("vertical")) {
			this.setBounds(this.getX(), this.getY(), wallH, wallW);
			this.dir = "horizontal";
		} else {
			this.setBounds(this.getX(), this.getY(), wallW, wallH);
			this.dir = "vertical";
		}
		return this.dir;
	}
	public String getDirection() {
		return this.dir;
	}
	public void setDirection(String dir) {
		this.setBounds(this.getX(), this.getY(), wallW, wallH);
		this.dir = dir;
	}
	
	/**
	 * Method returns if a wall is dropped onto an acceptable point based on its position and direction and if so sets the posX and posY positions
	 * 
	 * @param String dir
	 * @author DariusPi
	 */
	public boolean dropWall() {
		boolean first=true;
		if (this.dir.compareTo("vertical")==0) {
			for (int i=0;i<8;i++) {
				for (int j=0;j<9;j++) {
					if (this.getBounds().contains(points[i][j])) {
						if (first) {
							x1=i;
							y1=j;
							first=false;
						}
						else {
							GameController gc= new GameController();
							Boolean valid=gc.valWallPosition(x1,y1, "vertical");
							if (valid) {
								if(gc.dropWall(x1,y1, "vertical",this.wallId)) {
								// Snap to position
									this.setLocation((int)points[x1][y1].getX()-wallW/2+5/2,(int)points[x1][y1].getY()-(wallH-50)/2+5/2);
								}
								else {
									valid=false;
								}
							}
							return valid;
						}
					}
				}
			}
		}
		else {
			for (int i=0;i<9;i++) {
				for (int j=0;j<8;j++) {
					if (this.getBounds().contains(points2[i][j])) {
						if (first) {
							x1=i;
							y1=j;
							first=false;
						}
						else {
							GameController gc= new GameController();
							Boolean valid=gc.valWallPosition(x1,y1, "horizontal");
							if (valid) {
								if (gc.dropWall(x1,y1, "horizontal", this.wallId)) {
								// Snap to position
									this.setLocation((int)points2[x1][y1].getX()-(wallH-50)/2+5/2,(int)points2[x1][y1].getY()-wallW/2+5/2);
								}
								else {
									valid=false;
								}
							}
							
							return valid;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if(this.dir.contentEquals("horizontal")) {
			((Graphics2D) g).rotate(Math.toRadians(-90));
			((Graphics2D) g).translate(-wallW,0);
        }
        super.paintComponent(g);
    }
	
}