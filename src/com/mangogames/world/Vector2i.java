package com.mangogames.world;

public class Vector2i {
	public int x, y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Object obj) {
		Vector2i vec = (Vector2i) obj;
		
		return (vec.x == this.x && vec.y == this.y);
	}
}
