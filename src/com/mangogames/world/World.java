package com.mangogames.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mangogames.main.Game;
import com.mangogames.entities.*;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			//store every pixel from a image (map) into an array
			int[] pixels = new int[WIDTH * HEIGHT];
			
			tiles = new Tile[pixels.length];
			
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					int index = x + (y * WIDTH);
					int pixel = pixels[index];
					
					tiles[index] = new FloorTile(x*16, y*16, Tile.TILE_FLOOR);
					
					switch (pixel) {
					case 0xFF000000:
							//floor;
						tiles[index] = new FloorTile(x*16, y*16, Tile.TILE_FLOOR);
						break;
					case 0xFFFFFFFF:
						//wall
						tiles[index] = new WallTile(x*16, y*16, Tile.TILE_WALL);
						break;
					case 0xFF0026FF:
						//player
						Game.player.setX(x * 16);
						Game.player.setY(y * 16);
						break;
					case 0xFFFF0000:
						Villain villain = new Villain(x * 16, y * 16, 16, 16, Entity.VILLAIN_EN);
						Game.entities.add(villain);
						Game.villains.add(villain);
						break;
					case 0xFFFF6363:
						//life
						LifePack lifePack = new LifePack(x * 16, y * 16, 16, 16, Entity.LIFEPACK_EN);
						Game.entities.add(lifePack);
						break;
					case 0xFF4CFF00:
						//weapon;
						Weapon weapon = new Weapon(x * 16, y * 16, 16, 16, Entity.WEAPON_EN);
						Game.entities.add(weapon);
						break;
					case 0xFFFFD800:
						//ammo
						Ammo ammo = new Ammo(x * 16, y * 16, 16, 16, Entity.AMMO_EN);
						Game.entities.add(ammo);
						break;
					
					default:
						tiles[index] = new FloorTile(x*16, y*16, Tile.TILE_FLOOR);
						break;
					}
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isFree(int xNext, int yNext) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;
		
		int x2 = (xNext + (TILE_SIZE - 1)) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;
		
		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + (TILE_SIZE - 1)) / TILE_SIZE;
		
		int x4 = (xNext + (TILE_SIZE - 1)) / TILE_SIZE;
		int y4 = (yNext + (TILE_SIZE - 1)) / TILE_SIZE;
		
		return !(
					(tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) ||
					(tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) ||
					(tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) ||
					(tiles[x4 + (y4*World.WIDTH)] instanceof WallTile)
				);
	}
	
	public void render(Graphics g) {
		int xstart = Camera.x/16;
		int ystart = Camera.y/16;
		
		int xfinal = xstart + (Game.WIDTH / 16);
		int yfinal = ystart + (Game.HEIGHT / 16);
		
		for (int x = xstart; x <= xfinal; x++) {
			for (int y = ystart; y <= yfinal; y++) {
				if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
					continue;
				Tile tile = tiles[x + (y * WIDTH)];
				tile.render(g);
			}
		}
	}
}
