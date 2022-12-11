package com.mangogames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.mangogames.main.Game;
import com.mangogames.world.Camera;

public class Bullet extends Entity {

	private int dx, dy;
	private double speed = 4;
	
	public Bullet(int x, int y, int width, int height, BufferedImage sprite, int dx, int dy) {
		super(x, y, width, height, sprite);
		
		this.dx = dx;
		this.dy = dy;
 	}
	
	public void tick() {
		x += dx*speed;
		y += dy*speed;
		
		if (x < 10 || y < 0 || x > Game.WIDTH || y > Game.HEIGHT) {
			Game.bullets.remove(this);
			return;
		}
			
	}
	
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillOval(getX() - Camera.x, getY() - Camera.y, width, height);
	}

}
