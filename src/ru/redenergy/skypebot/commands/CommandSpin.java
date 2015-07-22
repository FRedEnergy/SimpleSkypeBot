package ru.redenergy.skypebot.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

import ru.redenergy.skypebot.Main;

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
			String value = String.format("%s got: \n %s - %s - %s \n", sender.getUsername(), spinnedSmiles[0], spinnedSmiles[1], spinnedSmiles[2]);
			String result = "";
			boolean won = Arrays.stream(spinnedSmiles).allMatch(s -> s.equals(spinnedSmiles[0]));
			if(won){
				result = "...and  won! Take 3 points!";
				Main.getBot().setScoreOf(sender.getUsername(), Main.getBot().getScoreOf(sender.getUsername()) + 3);
			} else {
				result = "...and lost!";
			}
			spinTime.put(sender.getUsername(), System.currentTimeMillis() / 1000L);
			sender.getChat().sendMessage(Message.fromHtml(value + result));
		} else {
			sender.getChat().sendMessage(Message.fromHtml(String.format("%s need to wait %d seconds", sender.getUsername(),  cooldown - (System.currentTimeMillis() / 1000L - spinTime.get(sender.getUsername())) )));
		}
	}
	
	private String getRandomSmile(){
		return smiles[ThreadLocalRandom.current().nextInt(0, smiles.length)]; //ThreadLocalRandom too hard :D
//		return smiles[new Random().nextInt(2)];
	}

}
