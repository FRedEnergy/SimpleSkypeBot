package ru.redenergy.skypebot.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

public class CommandSpin implements ICommand {

	private static final String[] smiles = new String[]{"(^)", "(pi)", "(*)"};
	private HashMap<String, Long> spinTime = new HashMap<String, Long>();
	private static final int cooldown = 60;

	@Override
	public String getName() {
		return "spin";
	}

	@Override
	public void execute(User sender, String[] args) throws SkypeException {
		if(spinTime.get(sender.getUsername()) == null || (System.currentTimeMillis() / 1000L - spinTime.get(sender.getUsername()) > cooldown) ){
			String[] spinnedSmiles = new String[3];
			spinnedSmiles[0] = getRandomSmile();
			spinnedSmiles[1] = getRandomSmile();
			spinnedSmiles[2] = getRandomSmile();
			String value = String.format("You got: \n %s - %s - %s \n", spinnedSmiles[0], spinnedSmiles[1], spinnedSmiles[2]);
			String result = "";
			if(Arrays.stream(spinnedSmiles).allMatch(s -> s.equals(spinnedSmiles[0]))){
				result = "...and you won! (party)";
			} else {
				result = "...and you've lost!";
			}
			spinTime.put(sender.getUsername(), System.currentTimeMillis() / 1000L);
			sender.getChat().sendMessage(Message.fromHtml(value + result));
		} else {
			sender.getChat().sendMessage(Message.fromHtml("You need to way a bit..."));
		}
	}
	
	private String getRandomSmile(){
		return smiles[ThreadLocalRandom.current().nextInt(0, smiles.length)];
	}

}
