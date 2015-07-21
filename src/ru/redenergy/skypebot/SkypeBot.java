package ru.redenergy.skypebot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.user.User;

import ru.redenergy.skypebot.commands.ICommand;
import ru.redenergy.skypebot.listeners.MessageListener;
import ru.redenergy.skypebot.log.SkypeLoggerFormatter;

public class SkypeBot {
	
	private final String username, password;
	private Skype skype;
	private Logger logger = Logger.getLogger("[SkypeBot]");
	private HashMap<String, ICommand> commands = new HashMap<String, ICommand>();
	private HashMap<String, Long> lastCommandTime = new HashMap<String, Long>();
	private ExecutorService commandPool = Executors.newCachedThreadPool();
	
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
		registerEvents();
	}
	
	public List<ICommand> getCommandsList(){
		return Collections.unmodifiableList(new ArrayList<ICommand>(this.commands.values()));
	}
	
	private void registerEvents(){
		skype.getEventDispatcher().registerListener(new MessageListener(this));
	}
	
	public void onCommand(User sender, String command, String[] args){
		logger.info(String.format("Command %s recevied from %s with args %s", command, sender.getUsername(), Arrays.toString(args)));
		Runnable run = () -> {
			ICommand com = this.commands.get(command);
			if(com != null){
				if(!canExecuteCommand(sender)) return;
				try {
					com.execute(sender, args);
				} catch (Exception e) {e.printStackTrace();}
			}
		};
		this.commandPool.submit(run);
	}

	private boolean canExecuteCommand(User sender){
		long lastCommandTime = this.lastCommandTime.get(sender.getUsername()) != null ? this.lastCommandTime.get(sender.getUsername()) : 0;
		if(lastCommandTime != 0 && (System.currentTimeMillis() / 1000L - lastCommandTime) < 3){
			this.lastCommandTime.put(sender.getUsername(), System.currentTimeMillis() / 1000L);
			return false;
		} else {
			this.lastCommandTime.put(sender.getUsername(), System.currentTimeMillis() / 1000L);
			return true;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.commandPool.shutdown();
		super.finalize();
	}
	
	
	public Logger getLogger(){
		return logger;
	}
	
	public Skype getSkype(){
		return skype;
	}
	

}
