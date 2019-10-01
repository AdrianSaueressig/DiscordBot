package nolacola.discord.nolaDiscordBot.handler.bot;

import java.io.FileNotFoundException;

import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;
import nolacola.discord.nolaDiscordBot.util.FileUtil;

public class RegistrationHandler {

	FileUtil fileUtil = null;
	
	public RegistrationHandler() {
		fileUtil = new FileUtil();
	}
	
	public String register(String userAsMention, String summonerName) throws NolaBotException{
		try {
			fileUtil.registerSummoner(userAsMention, summonerName);
		} catch (FileNotFoundException e) {
			throw new NolaBotException(ErrorCodes.ERROR_READING_FILE);
		}
		return "Summoner registered!";
	}

}
