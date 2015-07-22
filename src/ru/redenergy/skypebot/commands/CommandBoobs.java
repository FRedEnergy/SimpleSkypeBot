package ru.redenergy.skypebot.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samczsun.skype4j.exceptions.SkypeException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.user.User;

public class CommandBoobs implements ICommand {

	@Override
	public String getName() {
		return "boobs";
	}

	@Override
	public void execute(User sender, String[] args) throws SkypeException {
		if(args.length == 0){
			StringBuffer buffer = new StringBuffer();
			getRandomBoobsPicture(1).forEach(line -> buffer.append(line + "\n"));
			sender.getChat().sendMessage(Message.fromHtml(buffer.toString()));
		} else {
			int amount = Integer.parseInt(args[0]);
			StringBuffer buffer = new StringBuffer();
			getRandomBoobsPicture(amount).forEach(line -> buffer.append(line + "\n"));
			sender.getChat().sendMessage(Message.fromHtml(buffer.toString()));
		}
	}
	
	private ArrayList<String> getRandomBoobsPicture(int amount){
		ArrayList<String> boobsUrls = new ArrayList<String>();
		String jsonData = "";
		try {
			jsonData = connectUrl("http://api.oboobs.ru/noise/" + amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonArray parsedBoobs = new JsonParser().parse(jsonData).getAsJsonArray();
		parsedBoobs.forEach(boob -> {
			JsonObject boobObj = boob.getAsJsonObject();
			boobsUrls.add("http://media.oboobs.ru/" + boobObj.get("preview").toString().replaceAll("\"", ""));
		});
		return boobsUrls;
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
