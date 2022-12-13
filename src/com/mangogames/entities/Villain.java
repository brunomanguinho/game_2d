package com.mangogames.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.mangogames.main.Game;
import com.mangogames.world.Camera;
import com.mangogames.world.World;

public class Villain extends Entity{

	private double speed = 0.6;
	
	private BufferedImage[] villains = new BufferedImage[3];
	
	private int frames = 0, maxFrames = 10, index = 0;
	
	private int life = 5;
	
	private boolean hitted = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public Villain(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		villains[0] = Game.spritesheet.getSprite(96, 16, 16, 16);
		villains[1] = Game.spritesheet.getSprite(112, 16, 16, 16);
		villains[2] = Game.spritesheet.getSprite(128, 16, 16, 16);
	}
	
	public void tick() {
		if (!hitPlayer()) {
			if ( ( (int) x < Game.player.getX() ) && 
				 ( World.isFree((int)(x + speed), this.getY()) ) &&
				 ( !isColliding((int)(x + speed), this.getY()) ) 
			   )
				x+=speed;
			else if ( ( (int) x > Game.player.getX() ) && 
					  ( World.isFree((int)(x - speed), this.getY()) ) &&
					  ( !isColliding((int)(x - speed), this.getY()) )
					)
				x-=speed;
			
			if ( ( (int) y < Game.player.getY() ) && 
				 ( World.isFree(this.getX(), (int)(y + speed)) ) &&
				 ( !isColliding(this.getX(), (int)(y + speed)) )
						 )
				y+=speed;
			else if ( ( (int) x > Game.player.getY() ) && 
					  ( World.isFree(this.getX(), (int)(y - speed)) ) &&
					  ( !isColliding(this.getX(), (int)(y - speed)) ))
				y-=speed;
		} else if (!Game.player.getHitted()){
			Game.player.setHitted(true);
		}
		
		frames++;
		
		if (frames == maxFrames) {
			frames = 0;
			index ++;
			
			if (index > this.villains.length - 1) 
				index = 0;
		}
		
		if (hitted) {
			damageCurrent++;
			
			if (damageCurrent == damageFrames) {
				damageCurrent = 0;
				hitted = false;
			}
		}
		
	}
	
	private boolean isColliding(int nextX, int nextY) {
		Rectangle curVillain = new Rectangle(nextX, nextY, World.TILE_SIZE, World.TILE_SIZE);
		
		for (int i = 0; i < Game.villains.size(); i++) {
			Villain v = Game.villains.get(i);
			
			if (v == this) continue;
			
			Rectangle otherVillain = new Rectangle(v.getX(), v.getY(), World.TILE_SIZE, World.TILE_SIZE);
			
			if (curVillain.intersects(otherVillain)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hitPlayer() {
		Rectangle curVillain = new Rectangle(this.getX(), this.getY(), World.TILE_SIZE, World.TILE_SIZE);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), World.TILE_SIZE, World.TILE_SIZE);
	
		return curVillain.intersects(player);
	}
	
	public void setHitted() {
		this.life--;
		this.hitted = true;
		
		if (this.life == 0) {
			Game.entities.remove(this);
			Game.villains.remove(this);
		}
	}
	
	@Override
	public void render(Graphics g) {
		if (!hitted) {
			g.drawImage(villains[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else {
			g.drawImage(Entity.VILLAIN_DAMAGE, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}
}
