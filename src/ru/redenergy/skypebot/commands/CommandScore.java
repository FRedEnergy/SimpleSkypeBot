package ru.redenergy.skypebot.commands;

import java.util.HashMap;
import java.util.TreeMap;

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
			TreeMap<String, Integer> sortedScore = new TreeMap<String, Integer>();
			sortedScore.putAll(new HashMap<String, Integer>(Main.getBot().getScore()));
			sortedScore.forEach((key, value) -> {
				buffer.append(key + " - " + value + " points");
			});
			sender.getChat().sendMessage(Message.fromHtml(buffer.toString()));
		}
	}

}
