package com.mangogames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.mangogames.main.Game;
import com.mangogames.world.Camera;

public class Bullet extends Entity {

	private double dx, dy;
	private double speed = 4;
	
	public Bullet(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		
		this.dx = dx;
		this.dy = dy;
 	}
	
	public void tick() {
		x += dx*speed;
		y += dy*speed;
		
		if (x < 10 || y < 0 || x > Game.WIDTH * Game.SCALE || y > Game.HEIGHT * Game.SCALE) {
			Game.bullets.remove(this);
			return;
		}
		
		checkCollisionEnemy();
	}
	
	private void checkCollisionEnemy() {
		for (int i = 0; i < Game.entities.size(); i++) {
			if (Game.entities.get(i) instanceof Villain) {
				if (isColliding(this, Game.entities.get(i), true)) {
					((Villain)Game.entities.get(i)).setHitted();
					Game.bullets.remove(this);
				}
			}
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillOval(getX() - Camera.x, getY() - Camera.y, width, height);
	}

}
