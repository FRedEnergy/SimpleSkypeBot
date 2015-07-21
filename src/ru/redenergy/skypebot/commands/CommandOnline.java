package ru.redenergy.skypebot.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

public class CommandOnline implements ICommand {

	
	@Override
	public String getName() {
		return "online";
	}

	@Override
	public void execute(User sender, String[] args) throws SkypeException {
		String monitorJson = "";
		try {
			monitorJson = connectUrl("http://sky-mine.ru/monitor/server.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JsonObject jelement = new JsonParser().parse(monitorJson).getAsJsonObject();
		JsonArray servers = jelement.get("server").getAsJsonArray();
		if(args.length == 0){
			StringBuffer buffer = new StringBuffer();
			buffer.append("Online: \n");
			servers.forEach(server -> {
				JsonObject serverObj = server.getAsJsonObject();
				int maxOnline = serverObj.get("max").getAsInt();
				String value = maxOnline != 0 ? serverObj.get("min").getAsInt() + " / " + maxOnline : "Offline"; 
				buffer.append("<u>" + serverObj.get("name").toString().replaceAll("\\<[^>]*>","").replaceAll("\\[.*?\\]","").replaceAll("\"", "") + "</u> : " + value + "\n");
			});
			JsonObject records = jelement.get("online").getAsJsonObject();
			buffer.append("Top online for today - " + records.get("recordToday").toString() + "\n");
			buffer.append("Top online for all time - " + records.get("recordForAll").toString() + "\n");
			sender.getChat().sendMessage(Message.fromHtml(buffer.toString()));
			return;
		}
		
		if("top".equals(args[0])){
			JsonObject records = jelement.get("online").getAsJsonObject();
			StringBuffer buffer = new StringBuffer();
			buffer.append("Top online for today - " + records.get("recordToday").toString() + "\n");
			buffer.append("Top online for all time - " + records.get("recordForAll").toString() + "\n");
			sender.getChat().sendMessage(Message.fromHtml(buffer.toString()));
			return;
		}
	}

	private String connectUrl(String url) throws ClientProtocolException, IOException{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("User-Agent", "Mozilla/5.0");
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
	 
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				httpResponse.getEntity().getContent()));
	 
		StringBuffer response = new StringBuffer();
	 
		reader.lines().forEach(line -> {
			response.append(line);
		});
		reader.close();
		httpClient.close();
		return response.toString();
	}	
}
