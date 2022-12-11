package com.mangogames.world;

public class Camera {
	public static int x, y;
	
	public static int clamp(int posAtual, int posMin, int posMax) {
		if (posAtual < posMin)
			posAtual = posMin;
		
		if (posAtual > posMax)
			posAtual = posMax;
		
		return posAtual;
	}
}
