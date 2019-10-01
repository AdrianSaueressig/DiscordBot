package nolacola.discord.nolaDiscordBot.caches;

import java.util.List;

import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.enums.APIKeys;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;
import nolacola.discord.nolaDiscordBot.util.FileUtil;

public class ApiKeyCache {
	private static String CHAMP_GG_API_KEY = null;
	private static String RIOT_API_KEY = null;
	
	private static ApiKeyCache cache = null;
	private List<String> unusedKeys = null;
	
	private ApiKeyCache() {
		
	}
	
	public static synchronized ApiKeyCache getInstance() {
		if(ApiKeyCache.cache == null) {
			ApiKeyCache.cache = new ApiKeyCache();
		}
		return ApiKeyCache.cache;
	}
	
	public String getKey(APIKeys keyType) throws NolaBotException {
		if(keyType == null) {
			return null;
		}
		switch (keyType) {
			case RIOT:{
				if(RIOT_API_KEY == null) {
					reloadCache();
				}
				return RIOT_API_KEY;
			}
			case CHAMPGG:{
				if(CHAMP_GG_API_KEY == null) {
					reloadCache();
				}
				return CHAMP_GG_API_KEY;
			}
			default:
				return null;
		}
	}
	
	public void reloadCache() throws NolaBotException {
		FileUtil fileutil = new FileUtil();
		try {
			this.unusedKeys = fileutil.getAPIKeys();
			for (String key : unusedKeys) {
				String[] keyAndValue = key.split(FileUtil.getValueSeparator());
				if(keyAndValue.length <=1) {
					throw new NolaBotException(ErrorCodes.ERROR_IN_CACHE_FILE);
				}
				if(APIKeys.valueOf(keyAndValue[0]).equals(APIKeys.RIOT)) {
					RIOT_API_KEY = keyAndValue[1];
				}else if(APIKeys.valueOf(keyAndValue[0]).equals(APIKeys.CHAMPGG)) {
					CHAMP_GG_API_KEY = keyAndValue[1];
				}
			}
		} catch (NolaBotException e) {
			throw e;
		}
	}
}
