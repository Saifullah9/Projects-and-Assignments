package ca.mcgill.ecse223.quoridor.view;

import java.awt.Color;

public class TileComponent extends RectComponent{
	public static final int tileW=40;
	
	public TileComponent() {
		super(tileW, tileW, Color.ORANGE);
	}
}
