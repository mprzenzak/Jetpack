package pl.gameplayOneFile;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Enemy {

	int x, y;
	int height, width;
	int speed;
	
	ImageView iv;
	Pane pane;
	
	long timer;
	boolean isMoving;

	public void move() {
		
	}
	
	public void disappear() {
		iv.setImage(null);
	}
	
	public int getRightBorder() {
		return x + width;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
}
