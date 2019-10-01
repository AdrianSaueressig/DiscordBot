package nolacola.discord.nolaDiscordBot.caches;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.stelar7.api.l4j8.basic.APICredentials;
import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.impl.L4J8;
import no.stelar7.api.l4j8.impl.raw.StaticAPI;
import no.stelar7.api.l4j8.pojo.staticdata.item.Item;
import no.stelar7.api.l4j8.pojo.staticdata.item.ItemList;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.enums.APIKeys;

public class ItemCache {

	private static ItemCache cache;
	private Map<Integer, Item> items;
	private Logger log = LogManager.getLogger(ItemCache.class);
	
	private StaticAPI api = null;
	
	private ItemCache(){
		APICredentials creds;
		try {
			creds = new APICredentials(ApiKeyCache.getInstance().getKey(APIKeys.RIOT), null);
			L4J8 l4j8 = new L4J8(creds);
			api = l4j8.getStaticAPI();
		} catch (NolaBotException e) {
			log.error(e,e);
		}
	}
	
	public static synchronized ItemCache getInstance() {
		if(ItemCache.cache == null) {
			ItemCache.cache = new ItemCache();
		}
		return ItemCache.cache;
	}
	
	public Map<Integer, Item> getItems(){
		if(items == null) {
			items = new HashMap<Integer, Item>();
			try {
				renewCache();
			} catch (NolaBotException e) {
			}
		}
		return items;
	}

	public void setItems(Map<Integer, Item> items) {
		this.items = items;
	}

	public void addItem(Integer id, Item item) {
		getItems().put(id, item);
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
		ItemList itemList = api.getItems(Platform.EUW1, null, null, null);
		this.items = itemList.getData();
	}
}
