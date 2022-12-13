package com.mangogames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.mangogames.entities.Bullet;
import com.mangogames.entities.Entity;
import com.mangogames.entities.Player;
import com.mangogames.entities.Villain;
import com.mangogames.graphics.SpriteSheet;
import com.mangogames.graphics.UI;
import com.mangogames.world.Camera;
import com.mangogames.world.World;


public class Game extends Canvas implements Runnable, KeyListener, MouseListener{
	private static final long serialVersionUID = 1L;
	
	//Frame properties
	public static JFrame frame;
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 3;
	private BufferedImage image; //background image
	
	//Game properties
	private final String gameName = "Zelda Clone";
	private Thread thread;
	private boolean isRunning = false;
	
	//Game elements
	public static World world;
	
	public static ArrayList<Entity> entities;
	public static ArrayList<Villain> villains;
	public static ArrayList<Bullet> bullets;
	public static SpriteSheet spritesheet; 
	public static Player player;
	
	public static UI ui;
	
	private final int maxLevels = 2;
	public static int level = 1;
	
	public Game() {
		addKeyListener(this);
		addMouseListener(this);
		initFrame();
		loadGraphicElements();
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // set the background image
	}
	
	public static void loadGraphicElements() {
		spritesheet = new SpriteSheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(0, 0, 16, 16));
		entities = new ArrayList<Entity>();
		villains = new ArrayList<Villain>();
		bullets = new ArrayList<Bullet>();
		entities.add(player);
		world = new World(getMapLevel());
		ui = new UI();
	}
	
	public static void main(String args[]) {
		Game game = new Game();

		game.start();
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//method to update the game logic
	public void tick() {
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.tick();
		}
		
		for (int j = 0; j < bullets.size(); j++) {
			bullets.get(j).tick();
		}
		
		if (villains.size() == 0) {
			setNextLevel();
		}
	}
	
	public void setNextLevel() {
		level ++;
		
		if (level > maxLevels) {
			level = 1;
		}
		
		loadGraphicElements();
	}
	
	public static String getMapLevel() {
		return "/map" + level + ".png";
	}

	// method to render the game
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
		
		world.render(g);
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for (int j = 0; j < bullets.size(); j++) {
			bullets.get(j).render(g);;
		}
		
		ui.render(g);
		
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		bs.show();
	}
	
	
	public void initFrame() {
		this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE)); //init a screen dimensions		
		
		frame = new JFrame(gameName); //init a frame;
		//set the frame properties
		frame.add(this); 
		frame.setResizable(false); 
		frame.pack();
		frame.setLocationRelativeTo(null); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frame.setVisible(true); 
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime(); //actual time from pc in nano seconds
		double amountOfTicks = 60.0; //FPS
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
				
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while (isRunning) {
			long now = System.nanoTime();
			
			delta += (now-lastTime) / ns;
			
			lastTime = now;
			
			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta = 0;
			} else 
			
//			Testing if the FPS is equals 60 FPS
			if (System.currentTimeMillis() - timer >= 1000) {
//				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ( (e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_D)) {
			player.right = true; 
			player.left = false;
			player.up = false;
			player.down = false;
		} else if ( (e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_A)) {
			player.right = false; 
			player.left = true;
			player.up = false;
			player.down = false;
		}  
		
		if ( (e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_W)) {
			player.right = false; 
			player.left = false;
			player.up = true;
			player.down = false;
		} else if ( (e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_S)) {
			player.right = false; 
			player.left = false;
			player.up = false;
			player.down = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_Z) {
			player.shooting = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ( (e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_D)) {
			player.right = false; 
		} else if ( (e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_A)) {
			player.left = false;
		}  
		
		if ( (e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_W)) {
			player.up = false;
		} else if ( (e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_S)) {
			player.down = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShooting = true;
		player.mouseX = ( e.getX() / SCALE );
		player.mouseY = ( e.getY() / SCALE );
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}