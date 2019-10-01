package nolacola.discord.nolaDiscordBot.handler.leagueoflegends.champgg;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.caches.ApiKeyCache;
import nolacola.discord.nolaDiscordBot.dto.ChampionInfoDto;
import nolacola.discord.nolaDiscordBot.enums.APIKeys;

public class ChampionGGConnection {
	private static final String CHAMP_GG_TARGETURL = "http://api.champion.gg/v2/champions/";
	private static final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name(); 
	private static final Logger logger = LogManager.getLogger(ChampionGGConnection.class);
	
	public List<ChampionInfoDto> sendGetChamp(int championId) throws NolaBotException {
		String query = "champData="+"hashes"+ "&api_key=" + ApiKeyCache.getInstance().getKey(APIKeys.CHAMPGG); // "finalitems,skills"
		
		List<ChampionInfoDto> answer = null;
		HttpURLConnection connection;
		try {
		
			connection = (HttpURLConnection)new URL(CHAMP_GG_TARGETURL + championId + "?" + query).openConnection();

			connection.setRequestProperty("Accept-Charset", CHARSET);
			InputStream response = connection.getInputStream();
			int status = connection.getResponseCode();
			
			if(status >= 200 && status <300) {
				String delimitedResponse = printResponseToSystem(response);
				answer = convertToJavaObject(delimitedResponse);
			}else {
				logger.error("Query to champion.gg with champion id XXX failed. Response code was " + status);
			}

		} catch (IOException e) {
		}
		return answer;
	}


	private List<ChampionInfoDto> convertToJavaObject(String response) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<ChampionInfoDto> championInfoDto = null;
		try {
			championInfoDto = mapper.readValue(response, new TypeReference<List<ChampionInfoDto>>(){});
		} catch (IOException e) {
			logger.error("Error while mapping Champion.gg response. :" + e.getMessage());
		}
		
		return championInfoDto;
	}


	private String printResponseToSystem(InputStream response) {
		try (Scanner scanner = new Scanner(response)) {
		    String responseBody = scanner.useDelimiter("\\A").next();
		    logger.info(responseBody);
		    return responseBody;
		}
	}
}
