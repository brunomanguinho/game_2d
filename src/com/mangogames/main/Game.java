package com.mangogames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mangogames.entities.Bullet;
import com.mangogames.entities.Entity;
import com.mangogames.entities.Player;
import com.mangogames.entities.Villain;
import com.mangogames.graphics.SpriteSheet;
import com.mangogames.graphics.UI;
import com.mangogames.world.World;


public class Game extends Canvas implements Runnable, KeyListener, MouseListener{
	private static final long serialVersionUID = 1L;
	
	enum GameState{
		MENU,
		RUNNING,
		GAME_OVER
	}

	//Frame properties
	public static final int encode = 1984;
	public static JFrame frame;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	private BufferedImage image; //background image
	public BufferedImage lightmap;
	public int[] lightmapPixels;

	public static BufferedImage minimap;
	public static int[] minimapPixels;
	//Game properties
	public static final String gameName = "Shoot Game";
	private Thread thread;
	private boolean isRunning = false;
	public static GameState state = GameState.MENU;
	
	//Game elements
	public static World world;
	public Menu menu;
	
	public static ArrayList<Entity> entities;
	public static ArrayList<Villain> villains;
	public static ArrayList<Bullet> bullets;
	public static SpriteSheet spritesheet; 
	public static Player player;
	
	//Game interface
	public static UI ui;
	private int[] pixels;
	
	//Game Options
	private final int maxLevels = 2;
	public static int level;
	
	//Game Frames
	private final int maxFramesRestart = 30;
	private int framesRestart = 0;
	private boolean printRestart = true;
	
	public Game() {
		//Sound.background.loop();
		level = 1;
		addKeyListener(this);
		addMouseListener(this);
		initFrame();
		loadGraphicElements();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // set the background image
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lightmapPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightmapPixels, 0, lightmap.getWidth());
		
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		menu = new Menu();
	}
	
	public static void loadGraphicElements() {
		spritesheet = new SpriteSheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(0, 0, 16, 16));
		entities = new ArrayList<Entity>();
		villains = new ArrayList<Villain>();
		bullets = new ArrayList<Bullet>();
		entities.add(player);
		world = new World(getMapLevel());
		createMiniMap();
		
		ui = new UI();
	}
	
	public static void createMiniMap() {
		minimap = new BufferedImage(world.WIDTH, world.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapPixels = ((DataBufferInt)minimap.getRaster().getDataBuffer()).getData();
	}
	
	public static void setGameOver() {
		state = GameState.GAME_OVER;
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
		switch (state) {
		case RUNNING:
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
			break;
		case GAME_OVER:
			framesRestart++;
			
			if (framesRestart == maxFramesRestart) {
				printRestart = !printRestart;
				framesRestart = 0;
			}
			break;
		case MENU:
				menu.tick();
			break;
			
		default:
			break;
		}
	}
	
	public void setNextLevel() {
		level ++;
		
		if (level > maxLevels) {
			level = 1;
		}
		
		loadGraphicElements();
	}
	
	public static void setLevel(int l) {
		level = l;
		System.out.println(level);
	}
	
	public static String getMapLevel() {
		return "/map" + level + ".png";
	}
	
	public void drawRectangleExample(int xoff, int yoff) {
		for(int x=0; x < 32; x++) {
			for (int y=0; y<32; y++) {
				int xOff = x + xoff;
				int yOff = y + yoff;
				
				if (xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT)
					continue;
				
				pixels[xOff + (yOff*WIDTH)] = 0xff0000;
			}
		}
	}

	public void applyLight() {
		for (int x = 0; x < 240; x++) {
			for (int y = 0; y < 160; y++) {
				int curPixel = x +(y * 240);
				if (lightmapPixels[curPixel] == 0xffffffff) {
					pixels[curPixel] = 0;
				} else continue;
			}
		}
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
		
		Collections.sort(entities, Entity.nodeSorter);
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for (int j = 0; j < bullets.size(); j++) {
			bullets.get(j).render(g);;
		}
		
		ui.render(g);
		//applyLight();
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		
		if (state == GameState.GAME_OVER) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial", Font.BOLD, 36));
			g.setColor(Color.white);
			g.drawString("GAME OVER", (WIDTH * SCALE)/2 -70, (HEIGHT * SCALE)/2 - 20);
			
			if (printRestart)
				g.drawString(">Press any key to restart<", (WIDTH * SCALE)/2 -160, (HEIGHT * SCALE)/2 + 40);
		} else if (state == GameState.MENU) {
			menu.render(g);
		}
		if (state == GameState.RUNNING) {
			World.renderMiniMap();
			g.drawImage(minimap, 617, 80, world.WIDTH * 5, world.HEIGHT * 5, null);	
		}

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
		switch (state) {
		case RUNNING:
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
			} else if ( (e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_S) ) {
				player.right = false; 
				player.left = false;
				player.up = false;
				player.down = true;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_Z) {
				player.shooting = true;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_X) {
				player.jump = true;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				state = GameState.MENU;
			}
			break;
		case GAME_OVER:
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				level = 1;
				loadGraphicElements();
				state = GameState.RUNNING;
			}	
			break;
		case MENU:
			if ( (e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_W)) {
				menu.upSelect = true;
			} else if ( (e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_S)) {
				menu.downSelect = true;
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (menu.currentOption == 0) {
					File file = new File("save.txt");
					if (file.exists()) {
						file.delete();
					}
					
					state = GameState.RUNNING;
					level = 1;
					loadGraphicElements();
				} else if (menu.currentOption == 1) {
					state = GameState.RUNNING;
				} else if (menu.currentOption == 2) {
					String[] params = {"level"};
					int[] values = {level};
					Menu.saveGame(params, values, encode);
				} else if (menu.currentOption == 3) {
					String loadParams = Menu.loadGame(encode);
					if (loadParams != "") {
						Menu.applySave(loadParams);
					}
				}
				
				else if (menu.currentOption == 4) {
					System.exit(1);
				}
			}
			break;

		default:
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (state) {
		case RUNNING:
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
			break;
		case MENU:
			if ( (e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_W)) {
				menu.upSelect = false;
			} else if ( (e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_S)) {
				menu.downSelect = false;
			}
			break;			
		default:
			break;
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