package ru.redenergy.skypebot.commands;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

import ru.redenergy.skypebot.Main;

public class CommandHelp implements ICommand {

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public void execute(User sender, String[] args) throws SkypeException {
		Chat chat = sender.getChat();
		StringBuffer strBuffer = new StringBuffer("List of available commands:  ");
		Main.getBot().getCommandsList().forEach(command -> {
			strBuffer.append(" <i>!" + command.getName() + "</i>");
			if(command != Main.getBot().getCommandsList().get(Main.getBot().getCommandsList().size() - 1)){
				strBuffer.append(",");
			}
		});
		chat.sendMessage(Message.fromHtml(strBuffer.toString()));
	}

}
