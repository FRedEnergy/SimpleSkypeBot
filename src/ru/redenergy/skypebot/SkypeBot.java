package ru.redenergy.skypebot;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.chat.ChatMessage;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

import ru.redenergy.skypebot.log.SkypeLoggerFormatter;

public class SkypeBot {
	
	private final String username, password;
	private Skype skype;
	private Logger logger = Logger.getLogger("[SkypeBot]");
	
	public SkypeBot(String username, String password){
		this.username = username;
		this.password = password;
		setupLogger(logger);
	}
	
	private void setupLogger(Logger log){
		log.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new SkypeLoggerFormatter());
		log.addHandler(handler);
	}
	
	public void start() throws SkypeException, IOException {
		if(skype != null) return;
		skype = Skype.login(username, password);
		skype.subscribe();
		registerEvents(skype);
	}
	
	private void registerEvents(Skype sk){
		sk.getEventDispatcher().registerListener(new Listener(){
			@EventHandler
			public void onMessage(MessageReceivedEvent event){
				ChatMessage message = event.getMessage();
				if(message.getMessage().asPlaintext().startsWith("?@")){
					String[] splittedMessage = message.getMessage().asPlaintext().split(" ");
					onCommand(message.getSender(), splittedMessage[0], Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length));
				} else {
					logger.info(String.format("%s: %s", message.getSender().getUsername(), message.getMessage().asHtml()));
				}
				
			}
			
		});
	}
	
	public void onCommand(User sender, String command, String[] args){
		logger.info(String.format("Command %s recevied from %s with args %s", command, sender.getUsername(), Arrays.toString(args)));
		try {
			sender.getChat().sendMessage(Message.fromHtml("hello"));
		} catch (SkypeException e) {
			e.printStackTrace();
		}
	}
}
