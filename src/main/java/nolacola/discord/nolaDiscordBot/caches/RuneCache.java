package nolacola.discord.nolaDiscordBot.caches;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.stelar7.api.l4j8.basic.APICredentials;
import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.impl.L4J8;
import no.stelar7.api.l4j8.impl.raw.StaticAPI;
import no.stelar7.api.l4j8.pojo.staticdata.perk.StaticPerk;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.enums.APIKeys;

public class RuneCache {

	private static RuneCache cache;
	private List<StaticPerk> runes;
	private Logger log = LogManager.getLogger(RuneCache.class);
	
	private StaticAPI api = null;
	
	private RuneCache(){
		APICredentials creds;
		try {
			creds = new APICredentials(ApiKeyCache.getInstance().getKey(APIKeys.RIOT), null);
			L4J8 l4j8 = new L4J8(creds);
			api = l4j8.getStaticAPI();
		} catch (NolaBotException e) {
			log.error(e,e);
		}
	}
	
	public static synchronized RuneCache getInstance() {
		if(RuneCache.cache == null) {
			RuneCache.cache = new RuneCache();
		}
		return RuneCache.cache;
	}
	
	public List<StaticPerk> getRunes(){
		if(runes == null) {
			runes = new ArrayList<StaticPerk>();
			try {
				renewCache();
			} catch (NolaBotException e) {
			}
		}
		return runes;
	}

	public void setRunes(List<StaticPerk> runes) {
		this.runes = runes;
	}

	public void addRune(StaticPerk rune) {
		getRunes().add(rune);
	}
	
	/**
	 * renews the Cache
	 * @throws NolaBotException 
	 */
	private void renewCache() throws NolaBotException {
//		try (RiotApi api = RiotApi.newInstance(ApiKeyCache.getInstance().getKey(APIKeys.RIOT))) {
//			ItemList items = api.staticData.getItemList(Region.EUW);
//			Map<Integer, Item> championMap = items.data;
//			this.items = championMap.values();
//        } catch (IOException e) {
//		}
		runes = api.getPerks(Platform.EUW1, null, null);
	}
}
