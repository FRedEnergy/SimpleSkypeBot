package ru.redenergy.skypebot.commands;

import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.user.User;

public interface ICommand {
	
	public String getName();
	
	public void execute(User sender, String[] args) throws SkypeException;
	
	
}
