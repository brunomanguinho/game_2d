package com.mangogames.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.mangogames.main.Game;
import com.mangogames.world.Camera;
import com.mangogames.world.Node;
import com.mangogames.world.Vector2i;
import com.mangogames.world.World;

public class Entity {
	
	private BufferedImage sprite;
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(80, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(80, 16, 16, 16);
	public static BufferedImage AMMO_EN = Game.spritesheet.getSprite(96, 0, 16, 16);
	public static BufferedImage VILLAIN_EN = Game.spritesheet.getSprite(96, 16, 16, 16);
	public static BufferedImage VILLAIN_DAMAGE = Game.spritesheet.getSprite(144, 16, 16, 16);
	public static BufferedImage RIGHT_WEAPON = Game.spritesheet.getSprite(80, 32, 16, 16);
	public static BufferedImage LEFT_WEAPON = Game.spritesheet.getSprite(80, 48, 16, 16);
	
	protected List<Node> path;
	
	protected double x, y;
	protected int z = 0;
	protected int width, height;

	public int depth;

	
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
	
	public boolean isColliding(int nextX, int nextY) {
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
	
	public static boolean isColliding(Entity e1, Entity e2, boolean ownerProps) {
		if (!ownerProps) {
			return isColliding(e1, e2);
		}else {
			Rectangle ent1 = new Rectangle(e1.getX(), e1.getY(), e1.width, e1.height);
			Rectangle ent2 = new Rectangle(e2.getX(), e2.getY(), e2.width, e2.height);
			
			return ent1.intersects(ent2);
		}
	}
	
	public double calculateDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt( (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	public void followPath(List<Node> path) {
		if (path!=null) {
			if (path.size() > 0) {
				Vector2i target = path.get(path.size() - 1).tile;
				
				if ( (x < target.x * 16) /*&& (!isColliding(this.getX() + 1, this.getY())) */){
					x++;
				} else if ( (x > target.x * 16) /*&& (!isColliding(this.getX() - 1, this.getY()))*/ ){
					x--;
				}
				
				if ( (y < target.y * 16) /*&& (!isColliding(this.getX(), this.getY() + 1)) */ ) {
					y++;
				} else if ( (y > target.y * 16) /*&& (!isColliding(this.getX(), this.getY() - 1)) */ ) {
					y--;
				}
				
				if (x == target.x * 16 && y == target.y * 16) {
					path.remove(path.size() - 1);
				}
				
			}
		}
	}
	
	public static Comparator<Entity> nodeSorter = new Comparator<Entity>() {
		@Override
		public int compare(Entity n0, Entity n1) {
			if(n1.depth < n0.depth)
				return + 1;
			if (n1.depth > n0.depth)
				return - 1;
			
			return 0;
		}
	};
}
