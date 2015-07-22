package ru.redenergy.skypebot.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

import de.voidplus.soundcloud.Track;

public class CommandMusic implements ICommand {

	private static final String scClientID = "aa9bf5343352a36ee59e596b36a88b86";
	@SuppressWarnings("unused")
	private static final String scClientSecret = "e73694e6fd54824713d44ef4395af0c8";
	
	public CommandMusic(){
	}
	
	@Override
	public String getName() {
		return "music";
	}

	@Override
	public void execute(User sender, String[] args) throws SkypeException {
		if(args.length == 0){
			ArrayList<Track> streamable_tracks = null;
			try {
				streamable_tracks = grabTracksByUrl(30, ThreadLocalRandom.current().nextInt(0, 200));
			} catch (IOException e) {e.printStackTrace();}
			StringBuffer buffer = new StringBuffer();
			Track track = streamable_tracks.get(ThreadLocalRandom.current().nextInt(0, streamable_tracks.size()));
			boolean shouldDisplayMonkey = ThreadLocalRandom.current().nextBoolean();
			buffer.append(track.getTitle().replaceAll("\\(.*?\\) ?", "") + " \n" + track.getPermalinkUrl() + (shouldDisplayMonkey ? "(monkey)" : ""));
//			System.out.println(buffer.toString().replaceAll("\\[.*?\\]","").replaceAll("\\<[^>]*>",""));
			sender.getChat().sendMessage(Message.fromHtml(buffer.toString().replaceAll("\\[.*?\\]","").replaceAll("\\<[^>]*>","").replaceAll("&", "")));
		}
		
	}
	
	private ArrayList<Track> grabTracksByUrl(int limit, int offset) throws ClientProtocolException, IOException{
		String jsonData = connectUrl("https://api-v2.soundcloud.com/explore/Popular+Music?tag=out-of-experiment&limit=" + limit + "&offset=" + offset + "&linked_partitioning=1&client_id=" + CommandMusic.scClientID + "&app_version=eed3f14");
		ArrayList<Track> tracks = new ArrayList<Track>();
		Gson gson = new Gson();
		JsonObject parsedJson = new JsonParser().parse(jsonData).getAsJsonObject();
		JsonArray trackArray = parsedJson.get("tracks").getAsJsonArray();
		trackArray.forEach(tr -> {
			JsonObject track = tr.getAsJsonObject();
			Track parsedTrack = gson.fromJson(track, Track.class);
			tracks.add(parsedTrack);
		});
		return tracks;
	}

	
	private String connectUrl(String url) throws ClientProtocolException, IOException{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", "Mozilla/5.0");
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
		StringBuffer response = new StringBuffer();
		reader.lines().forEach(line -> {
			response.append(line);
		});
		reader.close();
		httpClient.close();
		return response.toString();
	}	
}
