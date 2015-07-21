package ru.redenergy.skypebot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.chat.ChatMessage;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

import ru.redenergy.skypebot.commands.ICommand;
import ru.redenergy.skypebot.log.SkypeLoggerFormatter;

public class SkypeBot {
	
	private final String username, password;
	private Skype skype;
	private Logger logger = Logger.getLogger("[SkypeBot]");
	private HashMap<String, ICommand> commands = new HashMap<String, ICommand>();
	private Gson gson = new Gson();
	
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
	
	public void registerCommand(ICommand command){
		this.commands.put(command.getName(), command);
	}
	
	public void start() throws SkypeException, IOException {
		if(skype != null) return;
		skype = Skype.login(username, password);
		skype.subscribe();
		registerEvents(skype);
	}
	
	public List<ICommand> getCommandsList(){
		return Collections.unmodifiableList(new ArrayList<ICommand>(this.commands.values()));
	}
	
	private void registerEvents(Skype sk){
		sk.getEventDispatcher().registerListener(new Listener(){
			@EventHandler
			public void onMessage(MessageReceivedEvent event){
				ChatMessage message = event.getMessage();
				if(message.getSender().getUsername().equalsIgnoreCase(skype.getUsername())) return;
				if(message.getMessage().asPlaintext().startsWith("!")){
					String[] splittedMessage = message.getMessage().asPlaintext().split(" ");
					onCommand(message.getSender(), splittedMessage[0].substring(1), Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length));
				} else {
					if(message.getChat() instanceof GroupChat){
						logger.info(String.format("%s @ %s: %s", ((GroupChat)message.getChat()).getTopic(),  message.getSender().getUsername(), message.getMessage().asHtml()));
					} else {
						logger.info(String.format("%s: %s", message.getSender().getUsername(), message.getMessage().asHtml()));
					}
				}
				
			}
			
		});
	}
	
	public void onCommand(User sender, String command, String[] args){
		if(sender.getUsername().equals("firkys")) {
			return;
		}
		logger.info(String.format("Command %s recevied from %s with args %s", command, sender.getUsername(), Arrays.toString(args)));
		try {
			ICommand com = this.commands.get(command);
			if(com != null){
				com.execute(sender, args);
			}
		} catch (SkypeException e) {
			e.printStackTrace();
		}
	}
}
