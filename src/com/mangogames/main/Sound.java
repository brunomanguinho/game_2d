package com.mangogames.main;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {

	private AudioClip clip;
	public static final Sound background = new Sound("/music.wav");
	public static final Sound hurtEffect = new Sound("/hurt.wav");
	
	private Sound(String path) {
		try {
			clip = Applet.newAudioClip(getClass().getResource(path));
		}catch(Throwable e) {
			
		}
	}
	
	public void play() {
		try {
			new Thread() {
				public void run() {
					clip.play();
				}
			}.start();
		}catch(Throwable e) {
			
		}
	}
	
	public void loop() {
		try {
			new Thread() {
				public void run() {
					clip.loop();
				}
			}.start();
		}catch(Throwable e) {
			
		}
	}
}
