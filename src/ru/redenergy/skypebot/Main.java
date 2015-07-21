package ru.redenergy.skypebot;

import java.io.IOException;

import com.samczsun.skype4j.exceptions.SkypeException;

public class Main {
	
	public static void main(String[] args){
		SkypeBot bot = new SkypeBot("***", "***");
		try {
			bot.start();
		} catch (IOException | SkypeException e) {
			e.printStackTrace();
		}
	}
}
