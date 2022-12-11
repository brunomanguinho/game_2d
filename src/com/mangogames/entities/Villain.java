package com.mangogames.entities;

import java.awt.image.BufferedImage;

import com.mangogames.main.Game;
import com.mangogames.world.World;

public class Villain extends Entity{

	private double speed = 0.6;
	
	public Villain(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	}
	
	public void tick() {
		if ( (int) x < Game.player.getX() && World.isFree((int)(x + speed), this.getY()) )
			x+=speed;
		else if ((int) x > Game.player.getX() && World.isFree((int)(x - speed), this.getY()) )
			x-=speed;
		
		if ( (int) y < Game.player.getY() && World.isFree(this.getX(), (int)(y + speed)))
			y+=speed;
		else if ((int) x > Game.player.getY() && World.isFree(this.getX(), (int)(y - speed)))
			y-=speed;
	}
	
}
