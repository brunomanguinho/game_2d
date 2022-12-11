package com.mangogames.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.mangogames.main.Game;
import com.mangogames.world.Camera;
import com.mangogames.world.World;

public class Entity {
	
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(80, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(80, 16, 16, 16);
	public static BufferedImage AMMO_EN = Game.spritesheet.getSprite(96, 0, 16, 16);
	public static BufferedImage VILLAIN_EN = Game.spritesheet.getSprite(96, 16, 16, 16);
	
	protected double x, y;
	protected int width, height;
	private BufferedImage sprite;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
	}
	
	public int getX() {
		return (int) this.x;
	}
	
	public int getY() {
		return (int) this.y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void tick() {
		
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		Rectangle ent1 = new Rectangle(e1.getX(), e1.getY(), World.TILE_SIZE, World.TILE_SIZE);
		Rectangle ent2 = new Rectangle(e2.getX(), e2.getY(), World.TILE_SIZE, World.TILE_SIZE);
		
		return ent1.intersects(ent2);
	}
}
