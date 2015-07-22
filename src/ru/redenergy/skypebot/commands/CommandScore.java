package ru.redenergy.skypebot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

import ru.redenergy.skypebot.Main;

public class CommandScore implements ICommand {

	@Override
	public String getName() {
		return "score";
	}

	@Override
	public void execute(User sender, String[] args) throws SkypeException {
		if(args.length == 0){
			sender.getChat().sendMessage(Message.fromHtml("You score: " + Main.getBot().getScoreOf(sender.getUsername())));
		} else if("top".equals(args[0])){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Top score:\n");
			ArrayList<Entry<String, Integer>> scores = new ArrayList<Entry<String, Integer>>(Main.getBot().getScore().entrySet());
			Collections.sort(scores, new Comparator<Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return -o1.getValue().compareTo(o2.getValue());
				}
			});
			scores.stream().forEachOrdered(entry -> {buffer.append(entry.getKey() + " - " + entry.getValue() + " points \n");});
			sender.getChat().sendMessage(Message.fromHtml(buffer.toString()));
		}
	}
}
