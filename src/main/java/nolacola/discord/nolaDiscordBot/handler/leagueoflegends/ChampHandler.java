package nolacola.discord.nolaDiscordBot.handler.leagueoflegends;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import no.stelar7.api.l4j8.basic.APICredentials;
import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.impl.L4J8;
import no.stelar7.api.l4j8.impl.raw.SpectatorAPI;
import no.stelar7.api.l4j8.impl.raw.SummonerAPI;
import no.stelar7.api.l4j8.pojo.spectator.SpectatorGameInfo;
import no.stelar7.api.l4j8.pojo.spectator.SpectatorParticipant;
import no.stelar7.api.l4j8.pojo.staticdata.champion.StaticChampion;
import no.stelar7.api.l4j8.pojo.summoner.Summoner;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.caches.ApiKeyCache;
import nolacola.discord.nolaDiscordBot.caches.ChampionCache;
import nolacola.discord.nolaDiscordBot.dto.ChampionInfoDto;
import nolacola.discord.nolaDiscordBot.dto.SummonerDto;
import nolacola.discord.nolaDiscordBot.enums.APIKeys;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;
import nolacola.discord.nolaDiscordBot.handler.bot.MessageHandler;
import nolacola.discord.nolaDiscordBot.handler.leagueoflegends.champgg.ChampionGGConnection;
import nolacola.discord.nolaDiscordBot.util.FileUtil;

public class ChampHandler {
	
	ChampionGGConnection connection = null;
	FileUtil fileUtil = null;
	MessageHandler messageHandler = null;
	Logger logger = LogManager.getLogger(ChampHandler.class);
	
	private SummonerAPI summonerApi = null;
	private SpectatorAPI spectatorApi = null;
	
	private static final Map<String, String> roleToChampionGGRoles;
	static {
		roleToChampionGGRoles = new HashMap<String, String>();
		roleToChampionGGRoles.put("TOP", "TOP");
		roleToChampionGGRoles.put("MID", "MIDDLE");
		roleToChampionGGRoles.put("JUNGLE", "JUNGLE");
		roleToChampionGGRoles.put("ADC", "DUO_CARRY");
		roleToChampionGGRoles.put("SUPPORT", "DUO_SUPPORT");
	}
	
	public ChampHandler() {
		this.fileUtil = new FileUtil(); 
		this.connection = new ChampionGGConnection();
		this.messageHandler = new MessageHandler();
		
		APICredentials creds;
		try {
			creds = new APICredentials(ApiKeyCache.getInstance().getKey(APIKeys.RIOT), null);
			L4J8 l4j8 = new L4J8(creds);
			summonerApi = l4j8.getSummonerAPI();
			spectatorApi = l4j8.getSpectatorAPI();
		} catch (NolaBotException e) {
			logger.error(e,e);
		}
	}

	public void retrieveChampStuffManually(String userAsMention, String championName, MessageChannel channelToAnswerTo, String role) throws NolaBotException {
		StaticChampion champion = loadChampionManually(championName);
		if(champion == null){
			MessageEmbed generateEmbedErrorMessage = messageHandler.generateEmbedErrorMessage(ErrorCodes.CHAMP_DOESNT_EXIST.getDescription());
			messageHandler.sendMessage(generateEmbedErrorMessage, channelToAnswerTo);
		}
		List<ChampionInfoDto> championInfos = connection.sendGetChamp(champion.getId());
		championInfos = filterChampionInfosForGivenRole(role, championInfos);
		sendChampionInfos(channelToAnswerTo, champion, championInfos);
	}

	/**
	 * @param channelToAnswerTo
	 * @param champion
	 * @param championInfos
	 */
	private void sendChampionInfos(MessageChannel channelToAnswerTo, StaticChampion champion,
			List<ChampionInfoDto> championInfos) {
		try {
			if(championInfos.isEmpty()) {
				MessageEmbed warnMessage = messageHandler.generateEmbedWarnMessage(ErrorCodes.NO_CHAMPION_INFO_FOR_THAT_ROLE);
				messageHandler.sendMessage(warnMessage, channelToAnswerTo);
			}else {
				for (ChampionInfoDto championInfoDto : championInfos) {
					MessageEmbed champInfoMessage = messageHandler.generateChampionBuildAndSkillInfoMessage(championInfoDto, champion.getName(), champion.getKey());
					messageHandler.sendMessage(champInfoMessage, channelToAnswerTo);
				}
			}
		}catch(Exception e) {
			MessageEmbed generateEmbedErrorMessage = messageHandler.generateEmbedErrorMessage(ErrorCodes.UNPREDICTABLE_ERROR.getDescription());
			messageHandler.sendMessage(generateEmbedErrorMessage, channelToAnswerTo);
			logger.error(e,e);
		}
	}

	/**
	 * @param role
	 * @param championInfos
	 * @return
	 */
	private List<ChampionInfoDto> filterChampionInfosForGivenRole(String role, List<ChampionInfoDto> championInfos) {
		if(role != null) {
			championInfos = championInfos.stream().filter(c -> c._id.role.equalsIgnoreCase(roleToChampionGGRoles.get(role))).collect(Collectors.toList());
		}
		return championInfos;
	}

	
	public void retrieveChampStuffAutomatically(String userAsMention, String role, MessageChannel channelToAnswerTo) throws FileNotFoundException, NolaBotException {
		int champId = -1;
		try{
			champId = findChampIdForSummonerIngame(userAsMention);
		}catch(NolaBotException e) {
			MessageEmbed embedErrorMessage = messageHandler.generateEmbedErrorMessage(ErrorCodes.SUMMONER_NOT_REGISTERED.getDescription());
			messageHandler.sendMessage(embedErrorMessage, channelToAnswerTo);
		}
		
		if(champId == -1) {
			MessageEmbed embedErrorMessage = messageHandler.generateEmbedErrorMessage(ErrorCodes.ERROR_RETRIEVING_CHAMP_ID_FROM_RIOT.getDescription());
			messageHandler.sendMessage(embedErrorMessage, channelToAnswerTo);
		}
		StaticChampion champion = ChampionCache.getInstance().getChamps().get(champId);
		
		List<ChampionInfoDto> championInfos = connection.sendGetChamp(champId);
		filterChampionInfosForGivenRole(role, championInfos);
		sendChampionInfos(channelToAnswerTo, champion, championInfos);
	}
	
	public void retrieveChampStuffAutomatically(String userAsMention, MessageChannel messageChannel) throws FileNotFoundException, NolaBotException {
		retrieveChampStuffAutomatically(userAsMention, null, messageChannel);
	}

	/**
	 * @param userAsMention
	 * @return
	 * @throws FileNotFoundException
	 * @throws NolaBotException
	 */
	private int findChampIdForSummonerIngame(String userAsMention) throws FileNotFoundException, NolaBotException {
		SummonerDto savedSummoner = fileUtil.loadSummoner(userAsMention);
		if(savedSummoner == null) {
			throw new NolaBotException(ErrorCodes.SUMMONER_NOT_FOUND);
		}
		if(savedSummoner.getId() == -1) {
			savedSummoner.setId(loadSummonerIdFromRiotBySummonerName(savedSummoner.getName()));
			fileUtil.saveSummoner(savedSummoner);
		}
		
		int champId = getChampIdForSummonerIngame(savedSummoner.getId());
		return champId;
	}
	
	/**
	 * @param name
	 * @return
	 */
	private long loadSummonerIdFromRiotBySummonerName(String name) {
		Summoner summoner = summonerApi.getSummonerByName(Platform.EUW1, name);
		return summoner == null ? -1 : summoner.getSummonerId();
	}
	
	/**
	 * @param summonerId
	 * @return
	 */
	private int getChampIdForSummonerIngame(long summonerId) {
		SpectatorGameInfo currentGameInfo = spectatorApi.getCurrentGame(Platform.EUW1, summonerId);
		if(currentGameInfo == null) {
			return -1;
		}
		List<SpectatorParticipant> participants = currentGameInfo.getParticipants();
		for (SpectatorParticipant currentGameParticipant : participants) {
			if(currentGameParticipant.getSummonerId() == summonerId) {
				return currentGameParticipant.getChampionId();
			}
		}
		return -1;
	}
	
	public StaticChampion loadChampionAutomatically(String userAsMention) throws NolaBotException, FileNotFoundException{
		int champId = -1;
		try{
			champId = findChampIdForSummonerIngame(userAsMention);
		}catch(NolaBotException | FileNotFoundException e) {
			throw e;
		}
		if(champId == -1) {
			throw new NolaBotException(ErrorCodes.ERROR_RETRIEVING_CHAMP_ID_FROM_RIOT);
		}
		StaticChampion champion = ChampionCache.getInstance().getChamps().get(champId);
		return champion;
	}
	
	public StaticChampion loadChampionManually(String championName) {
		StaticChampion champion = null;
		
		if(ChampionCache.getInstance().getChamps().values().stream().filter(champ -> champ.getName().equalsIgnoreCase(championName)).findFirst().isPresent()) {
			champion = ChampionCache.getInstance().getChamps().values().stream().filter(champ -> champ.getName().equalsIgnoreCase(championName)).findFirst().get();
			logger.info("Champion with name " + championName + " found!");
		}else if(ChampionCache.getInstance().getChamps().values().stream().filter(champ -> champ.getKey().equalsIgnoreCase(championName)).findFirst().isPresent()){
			logger.info("Champion with key " + championName + " found!");
			champion = ChampionCache.getInstance().getChamps().values().stream().filter(champ -> champ.getKey().equalsIgnoreCase(championName)).findFirst().get();
		}
		return champion;
	}
}
