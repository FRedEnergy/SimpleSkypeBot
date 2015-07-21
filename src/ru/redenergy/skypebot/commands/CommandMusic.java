package ru.redenergy.skypebot.commands;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

import de.voidplus.soundcloud.SoundCloud;
import de.voidplus.soundcloud.Track;

public class CommandMusic implements ICommand {

	private SoundCloud soundCloud;
	private static final String scClientID = "aa9bf5343352a36ee59e596b36a88b86";
	private static final String scClientSecret = "e73694e6fd54824713d44ef4395af0c8";
	
	public CommandMusic(){
		soundCloud = new SoundCloud(scClientID, scClientSecret);
	}
	
	@Override
	public String getName() {
		return "music";
	}

	@Override
	public void execute(User sender, String[] args) throws SkypeException {
		if(args.length == 0){
			ArrayList<Track> streamable_tracks = this.soundCloud.getTracks(ThreadLocalRandom.current().nextInt(100), ThreadLocalRandom.current().nextInt(100, 500));
			StringBuffer buffer = new StringBuffer();
			buffer.append(streamable_tracks.get(50).getTitle() + " : " + streamable_tracks.get(50).getPermalinkUrl());
			System.out.println(buffer.toString());
			sender.getChat().sendMessage(Message.fromHtml(buffer.toString()));
		}
		
	}

}
