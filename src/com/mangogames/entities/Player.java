package com.mangogames.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.mangogames.main.Game;
import com.mangogames.world.Camera;
import com.mangogames.world.World;

public class Player extends Entity{
	
	public boolean right, up, left, down;
	public double speed = 1.4;
	
	private int rightDirection = 0, leftDirection = 1;
	
	private int curDirection = rightDirection;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	
	private BufferedImage[] rightPlayer, leftPlayer;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		
		for (int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(0 + (i * 16), 0, 16, 16);
		}
		
		for (int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(48 - (i*16), 16, 16, 16);
		}
		
	}
	
	public void tick() {
		moved = false;
		
		if (right && World.isFree((int) (x + speed), (int) this.getY())) {
			curDirection = rightDirection;
			this.x += speed;
			moved = true;
		} else if (left && World.isFree((int) (x - speed), (int) this.getY())) {
			curDirection = leftDirection;
			this.x -= speed;
			moved = true;
		}
		
		if (up && World.isFree((int) this.getX(), (int) (y - speed))) {
			this.y -= speed;
			moved = true;
		} else if (down && World.isFree((int) this.getX(), (int) (y + speed))) {
			this.y += speed;
			moved = true;
		}
		
		if (moved) {
			frames++;
			
			if (frames == maxFrames) {
				frames = 0;
				index++;
				
				if (index > maxIndex) {
					index = 0;
				}
			}
		}
		
		
		double camX = this.getX() - (Game.WIDTH/2);
		double camY = this.getY() - (Game.HEIGHT/2);
		
		camX = Camera.clamp((int) camX, 0, World.WIDTH * 16 - Game.WIDTH);
		camY = Camera.clamp((int) camY, 0, World.HEIGHT * 16 - Game.HEIGHT);
		
		Camera.x = (int) camX;
		Camera.y = (int) camY;
	}
	
	public void render(Graphics g) {
		if (curDirection == rightDirection) {
			g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else if (curDirection == leftDirection) {
			g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} 
	}
}
