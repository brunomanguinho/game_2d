package com.mangogames.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.mangogames.main.Game;
import com.mangogames.main.Sound;
import com.mangogames.world.Camera;
import com.mangogames.world.World;

public class Player extends Entity{
	
	public boolean right, up, left, down;
	private final double playerSpeed = 1.4;
	public double speed = playerSpeed;
	public double life = 100, maxLife = 100;
	public int ammo = 0;
	public boolean hitted = false;
	public int mouseX, mouseY;
	
	public boolean jump = false;
	public boolean armed = false;
	private boolean moved = false;
	
	public boolean shooting = false, mouseShooting = false, isJumping = false;
	private int jumpDirection = 1;
	
	private int rightDirection = 0, leftDirection = 1;
	private int curDirection = rightDirection;

	//move frames
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;

	//hit frames
	private int damageFrames = 10, damageCurrent = 0;
	
	//jump frames
	public int jumpFrames = 30, jumpCurFrames = 0, jumpSpeed = 2;
	
	private BufferedImage[] rightPlayer, leftPlayer;
	
	private BufferedImage playerDamaged;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		playerDamaged = Game.spritesheet.getSprite(0, 32, width, height);
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
		speed = playerSpeed;
		
		if (jump) {
			if (!isJumping) {
				jump = false;
				isJumping = true;
			}
		}
		
		if(isJumping) {
			System.out.println(jumpCurFrames);
			if (jumpDirection == 1) {
				jumpCurFrames += jumpSpeed;
				z = jumpCurFrames;
				
				if ( jumpCurFrames >= jumpFrames ) {
					jumpDirection *= -1;
				} 
			
			} else if (jumpDirection == -1) {
				jumpCurFrames -= jumpSpeed;
				z = jumpCurFrames;
				
				if (jumpCurFrames <= 0) {
					isJumping = false;
					jumpDirection *= -1;
					jumpCurFrames = 0;
				} 
			}
		}
		
		canMove();
		
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
		
		if (this.hitted) {
			Sound.hurtEffect.play();
			damageCurrent++;
			
			if (damageCurrent == damageFrames) {
				damageCurrent = 0;
				setHitted(false);
			}
		}
		
		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionWeapon();
		
		if (shooting || mouseShooting) {
			if (armed && ammo > 0 && shooting) {
				shooting = false;
				createShoot();
			} else if (armed && ammo > 0 && mouseShooting) {
				mouseShooting = false;
				createMouseShoot();
			}
		}
		
		double camX = this.getX() - (Game.WIDTH/2);
		double camY = this.getY() - (Game.HEIGHT/2);
		
		camX = Camera.clamp((int) camX, 0, World.WIDTH * 16 - Game.WIDTH);
		camY = Camera.clamp((int) camY, 0, World.HEIGHT * 16 - Game.HEIGHT);
		
		Camera.x = (int) camX;
		Camera.y = (int) camY;
	}
	
	public boolean canMove() {
		if (right && World.isFree((int) (x + speed), (int) this.getY(), z)) {
			curDirection = rightDirection;
			this.x += speed;
			moved = true;
			return true;
		} else if (left && World.isFree((int) (x - speed), (int) this.getY(), z)) {
			curDirection = leftDirection;
			this.x -= speed;
			moved = true;
			return true;
		}
		
		if (up && World.isFree((int) this.getX(), (int) (y - speed), z)) {
			this.y -= speed;
			moved = true;
			return true;
		} else if (down && World.isFree((int) this.getX(), (int) (y + speed), z)) {
			this.y += speed;
			moved = true;
			return true;
		}
		
		return false;
	}
	
	private void createMouseShoot() {
		ammo--;
		
		double angle = Math.atan2(mouseY - (this.getY()+8 - Camera.y), mouseX - (this.getX()+8 - Camera.x));
		
		double dx = Math.cos(angle);
		double dy = Math.sin(angle);
		int px = 0;
		int py = 8;
		
		if (curDirection == rightDirection) {
			px = 18;
		}else {
			px = -8;
		}
		
		Bullet bullet = new Bullet(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
		Game.bullets.add(bullet);
	}
	
	private void createShoot() {
		ammo--;
		
		int dx;
		int px = 0;
		int py = 6;
		if (curDirection == rightDirection) {
			dx = 1;
			px = 18;
		}else {
			dx = -1;
			px = -8;
		}
		
		Bullet bullet = new Bullet(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0);
		Game.bullets.add(bullet);
	}
	
	public void checkCollisionAmmo() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity en = Game.entities.get(i);
			
			if (en instanceof Ammo) {
				if (Entity.isColliding(this, en)) {
					Game.entities.remove(i);
					ammo += 20;
				}
			}
		}
	}
	
	public void checkCollisionLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity en = Game.entities.get(i);
			
			if (en instanceof LifePack) {
				if (Entity.isColliding(this, en)) {
					if (life < maxLife) {
						Game.entities.remove(i);
						if (life + 10 > maxLife) life = maxLife; else life += 10;
					}
				}
			}
		}
	}
	
	public void checkCollisionWeapon() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity en = Game.entities.get(i);
			
			if (en instanceof Weapon) {
				if (Entity.isColliding(this, en)) {
					if (life < maxLife) {
						Game.entities.remove(i);
						armed = true;
						ammo += 30;
					}
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if (!this.hitted) {
			if (curDirection == rightDirection) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if (armed) {
					g.drawImage(RIGHT_WEAPON, this.getX() + 8 - Camera.x, this.getY() - Camera.y - z, null);
				}
			} else if (curDirection == leftDirection) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if (armed) {
					g.drawImage(LEFT_WEAPON, this.getX() - 8 - Camera.x, this.getY() - Camera.y - z, null);
				}
			} 
		} else {
			g.drawImage(playerDamaged, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
		}
		
		if (isJumping) {
			g.setColor(Color.BLACK);
			g.fillOval(this.getX() - Camera.x + 8, this.getY() - Camera.y + 16, 8, 8);
		}
			
	}
	
	public void setHitted(boolean hit) {
		this.hitted = hit;

		if (this.hitted) {
			this.life--;
		}
		
		if (this.life <= 0) {
			Game.setGameOver();
		}
	}
	
	public boolean getHitted() {
		return this.hitted;
	}
}
