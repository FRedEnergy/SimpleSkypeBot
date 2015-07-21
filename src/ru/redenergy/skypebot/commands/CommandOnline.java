package ru.redenergy.skypebot.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

public class CommandOnline implements ICommand {

	
	@Override
	public String getName() {
		return "online";
	}

	@Override
	public void execute(User sender, String[] args) throws SkypeException {
		JsonObject parsedJson = null;
		try {
			String monitorJson = connectUrl("http://sky-mine.ru/monitor/server.json");
			parsedJson = new JsonParser().parse(monitorJson).getAsJsonObject();
		} catch (IOException e) {e.printStackTrace();}
		
		if(parsedJson != null){
			if(args.length == 0){
				executeOnlineCommand(parsedJson, sender, args);
				return;
			}
			if("top".equals(args[0])){
				executeTopCommand(parsedJson, sender, args);
				return;
			}
		}
	}

	private void executeOnlineCommand(JsonObject parsedJson, User sender, String[] args) throws SkypeException{
		JsonArray servers = parsedJson.get("server").getAsJsonArray();
		StringBuffer buffer = new StringBuffer();
		buffer.append("Online: \n");
		servers.forEach(server -> {
			JsonObject serverObj = server.getAsJsonObject();
			int currentOnline = serverObj.get("min").getAsInt();
			int maxOnline = serverObj.get("max").getAsInt();
			String serverTitle = clearServerTitle(serverObj.get("name").toString());
			boolean enabled = (maxOnline != 0);
			if(enabled){
				buffer.append(String.format("<u>%s</u>: %d / %d \n", serverTitle, currentOnline, maxOnline));
			} else {
				buffer.append(String.format("<u>%s</u>: <font color=\"#ff0000\">Offline</font> \n", serverTitle));
			}
		});
		JsonObject records = parsedJson.get("online").getAsJsonObject();
		buffer.append("Top online for today - " + records.get("recordToday").toString() + "\n");
		buffer.append("Top online for all time - " + records.get("recordForAll").toString() + "\n");
		sender.getChat().sendMessage(Message.fromHtml(buffer.toString()));
	}
	
	private String clearServerTitle(String name){
		return name.replaceAll("\\<[^>]*>","").replaceAll("\\[.*?\\]","").replaceAll("\"", ""); 
	}
	
	private void executeTopCommand(JsonObject parsedJson, User sender, String[] args) throws SkypeException{
		JsonArray servers = parsedJson.get("server").getAsJsonArray();
		List<JsonObject> listOfServers = new ArrayList<JsonObject>();
		servers.forEach(server -> listOfServers.add(server.getAsJsonObject()));
		Collections.sort(listOfServers, new Comparator<JsonObject>() {
			@Override
			public int compare(JsonObject o1, JsonObject o2) {
				return -((Integer)o1.get("min").getAsInt()).compareTo(((Integer)o2.get("min").getAsInt()));
			}
		});
		if(!listOfServers.isEmpty()){
			String serverTitle = clearServerTitle(listOfServers.get(0).get("name").toString());
			int online = listOfServers.get(0).get("min").getAsInt();
			sender.getChat().sendMessage(Message.fromHtml(String.format("Top server - %s with online %d", serverTitle, online)));
		}
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
