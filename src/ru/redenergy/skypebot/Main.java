package ru.redenergy.skypebot;

import java.io.IOException;

import com.samczsun.skype4j.exceptions.SkypeException;

import ru.redenergy.skypebot.commands.CommandHelp;
import ru.redenergy.skypebot.commands.CommandOnline;

public class Main {
	
	private static SkypeBot bot;
	
	public static void main(String[] args){
		bot = new SkypeBot("poison1x", "918273645Danil");
		bot.registerCommand(new CommandHelp());
		bot.registerCommand(new CommandOnline());
		try {
			bot.start();
		} catch (IOException | SkypeException e) {
			e.printStackTrace();
		}
	}
	
	public static SkypeBot getBot(){
		return bot;
	}
}
