package nolacola.discord.nolaDiscordBot.caches;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.stelar7.api.l4j8.basic.APICredentials;
import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.basic.constants.flags.ChampDataFlags;
import no.stelar7.api.l4j8.impl.L4J8;
import no.stelar7.api.l4j8.impl.raw.StaticAPI;
import no.stelar7.api.l4j8.pojo.staticdata.champion.StaticChampion;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.enums.APIKeys;

public class ChampionCache {

	private static ChampionCache cache;
	private Map<Integer, StaticChampion> champs; 
	
	private Logger log = LogManager.getLogger(ChampionCache.class);
	private StaticAPI api = null;
	
	private ChampionCache(){
		APICredentials creds;
		try {
			creds = new APICredentials(ApiKeyCache.getInstance().getKey(APIKeys.RIOT), null);
			L4J8 l4j8 = new L4J8(creds);
			api = l4j8.getStaticAPI();
		} catch (NolaBotException e) {
			log.error(e,e);
		}
	}
	
	public static synchronized ChampionCache getInstance() {
		if(ChampionCache.cache == null) {
			ChampionCache.cache = new ChampionCache();
		}
		return ChampionCache.cache;
	}
	
	public Map<Integer, StaticChampion> getChamps() {
		if(champs == null) {
			champs = new HashMap<Integer, StaticChampion>();
			try {
				renewCache();
			} catch (NolaBotException e) {
			}
		}
		return champs;
	}

	public void setChamps(Map<Integer, StaticChampion> champs) {
		this.champs = champs;
	}

	public void addChampionToCache(Integer id, StaticChampion champ) {
		getChamps().put(id, champ);
	}
	
	/**
	 * renews the Cache
	 * @throws NolaBotException 
	 */
	private void renewCache() throws NolaBotException {
//		try (RiotApi api = RiotApi.newInstance(ApiKeyCache.getInstance().getKey(APIKeys.RIOT))) {
//			ChampionList champions = api.staticData.getChampionList(Region.EUW, Arrays.asList("allytips", "enemytips"), true);
//			Map<String, Champion> championMap = champions.data;
//			champs = championMap.values();
//        } catch (IOException e) {
//		}
		EnumSet<ChampDataFlags> dataFlags = EnumSet.of(ChampDataFlags.ALLYTIPS, ChampDataFlags.ENEMYTIPS);
		this.champs = api.getChampions(Platform.EUW1, dataFlags, null, null);
	}
}
