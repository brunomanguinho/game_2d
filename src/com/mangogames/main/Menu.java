package com.mangogames.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.mangogames.main.Game.GameState;

public class Menu {
	
	public String[] options = {"New Game", "Resume Game", "Save Game", "Load Game", "Exit"};
	
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	public boolean upSelect, downSelect;
	
	public int framesOptions = 0;
	public final int maxFramesOptions = 20;
	public boolean showOption = true;
	
	private final int deltaY = 45;
	
	public void tick() {
		if (upSelect) {
			upSelect = false;
			if (currentOption == 0) {
				currentOption = maxOption;
			} else currentOption--;
		} else if (downSelect) {
			downSelect = false;
			if (currentOption == maxOption) {
				currentOption = 0;
			} else currentOption++;
		}
		
		framesOptions++;
		
		if (framesOptions == maxFramesOptions) {
			showOption = !showOption;
			framesOptions = 0;
		}
	}
	
	public void render(Graphics g) {

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		
		g.setColor(Color.RED);
		g.setFont(new Font("arial", Font.BOLD, 42));
		g.drawString(Game.gameName, (Game.WIDTH * Game.SCALE) / 2 - 100, (Game.HEIGHT * Game.SCALE) / 2 - 80);
		
		//Menu Options
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 24));

		int lastY = 20;
		int i = 0;
		
		for (String option : options) {
			if ( (i == currentOption && showOption) || (i !=currentOption) ) {
				g.drawString(option, (Game.WIDTH * Game.SCALE) / 2 - 60, (Game.HEIGHT * Game.SCALE) / 2 + (lastY + deltaY));
				
				if (i == currentOption)
					g.drawString("> ", (Game.WIDTH * Game.SCALE) / 2 - 80, (Game.HEIGHT * Game.SCALE) / 2 + (lastY + deltaY));
			}
			lastY += deltaY;
			i++;
		}
		
	}

	public static void saveGame(String[] params, int[] values, int encode) {
		//creates a new file for write the game values
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("save.txt"));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		// runs the params array
		for (int i = 0; i < params.length; i++) {
			String current = params[i];
			current += ":";
			
			//fetch the value of the param into an char array
			char[] value = Integer.toString(values[i]).toCharArray();
			
			// adds a key to encrypt the value 
			for (int j = 0; j < value.length; j++) {
				value[j] += encode;
				current += value[j];
			}
			
			//write on the file the params:value(encoded)
			try {
				writer.write(current);
				if (i < params.length - 1)
					writer.newLine();
			} catch(IOException e) {}
		}
		
		//ends and free the file
		try {
			writer.flush();
			writer.close();
		}catch (IOException e) {}
	}
	
	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		
		if (file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				
				try {
					singleLine = reader.readLine();
					
					while (singleLine != null) {
						String[] params = singleLine.split(":");
						char[] value = params[1].toCharArray();
						params[1] = "";
						
						for (int i = 0; i < value.length; i++) {
							value[i] -= encode;
							params[1] += value[i];
						}
						
						line += params[0] + ":" + params[1] + "/";
						
						singleLine = reader.readLine();
					}
				}catch(IOException e) {}
				
			}catch(FileNotFoundException e) {}
		}
		
		return line;
	}
	
	public static void applySave(String str) {
		String[] splitLine = str.split("/");
		for (int i = 0; i < splitLine.length; i++) {
			String[] splitParams = splitLine[i].split(":");
			
			switch (splitParams[0]) {
			case "level":
				Game.setLevel(Integer.parseInt(splitParams[1]));
				Game.loadGraphicElements();
				Game.state = GameState.RUNNING;
				break;

			default:
				break;
			}
		}
	}
}
