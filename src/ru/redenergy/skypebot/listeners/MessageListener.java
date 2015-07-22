package ru.redenergy.skypebot.listeners;

import java.util.Arrays;

import com.samczsun.skype4j.chat.ChatMessage;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.SkypeException;

import ru.redenergy.skypebot.SkypeBot;

public class MessageListener implements Listener {
	
	private SkypeBot bot;

	public MessageListener(SkypeBot bot){
		this.bot = bot;
	}
	
	@EventHandler
	public void onMessage(MessageReceivedEvent event) throws SkypeException{
		ChatMessage message = event.getMessage();
		if(message.getSender().getUsername().equalsIgnoreCase(bot.getSkype().getUsername())) return;
		if(message.getMessage().asPlaintext().startsWith("!")){
			String[] splittedMessage = message.getMessage().asPlaintext().split(" ");
			bot.onCommand(message.getSender(), splittedMessage[0].substring(1), Arrays.copyOfRange(splittedMessage, 1, splittedMessage.length));
		} else {
			if(message.getChat() instanceof GroupChat){
				bot.getLogger().info(String.format("%s @ %s: %s", ((GroupChat)message.getChat()).getTopic(),  message.getSender().getUsername(), message.getMessage().asHtml()));
			} else {
				bot.getLogger().info(String.format("%s: %s", message.getSender().getUsername(), message.getMessage().asHtml()));
			}
		}
	}	
	
}
