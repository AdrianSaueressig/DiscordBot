package nolacola.discord.nolaDiscordBot.handler.bot;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import no.stelar7.api.l4j8.pojo.staticdata.champion.StaticChampion;
import no.stelar7.api.l4j8.pojo.staticdata.item.Item;
import no.stelar7.api.l4j8.pojo.staticdata.perk.StaticPerk;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.caches.ItemCache;
import nolacola.discord.nolaDiscordBot.caches.RuneCache;
import nolacola.discord.nolaDiscordBot.dto.ChampionInfoDto;
import nolacola.discord.nolaDiscordBot.enums.EmbeddedMessageType;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;

public class MessageHandler {
	
	private static final String CHAMPION_ICON_URL = "http://ddragon.leagueoflegends.com/cdn/8.6.1/img/champion/";
	private static final String CHAMPION_GG_URL = "http://champion.gg/champion/";
	private static final String ERROR_ICON_URL = "https://upload.wikimedia.org/wikipedia/commons/c/c4/Icon_Error.png";
	private static final String FOOTER_ICON = "https://images-ext-2.discordapp.net/external/-_tMg4YQ3IerHWfLejy1cs1E_7l9sp5_y9bBgdIAjA0/https/avatars1.githubusercontent.com/u/24436369";
	private static final String WARNING_ICON_URL = "http://www.stickpng.com/assets/thumbs/5a81af7d9123fa7bcc9b0793.png";
	
	private static final Color COLOR_ERROR = Color.RED;
	private static final Color COLOR_OK = Color.GREEN;
	private static final Color COLOR_WARN = Color.YELLOW;
		
	private static final String TIPS_TYPE_ALLY = "ALLY";
	private static final String TIPS_TYPE_ENEMY = "ENEMY";
	
	/**
	 * actually sends the message
	 * @param message message to send
	 * @param channel channel to send it to
	 */
	public void sendMessage(String message, MessageChannel channel) {
		channel.sendMessage(message).queue();
	}	
	
	/**
	 * actually sends the message
	 * @param message message to send
	 * @param channel channel to send it to
	 */
	public void sendMessage(MessageEmbed message, MessageChannel channel) {
		channel.sendMessage(message).queue();
	}
	
	public void sendErrorMessage(Exception e, MessageChannel channel) {
		if(e instanceof NolaBotException) {
			NolaBotException nolaEx = (NolaBotException) e;
			sendMessage(generateEmbedErrorMessage(e == null ? "No Error lul" : "An error occurred: " + nolaEx.getErrorCode().getDescription()), channel);
		}else {
			sendMessage(generateEmbedErrorMessage(e == null ? "No Error lul" : "An error occurred: " + e.getMessage()), channel);
		}
	}
	
	public void sendErrorMessage(ErrorCodes errorCode, MessageChannel channel) {
		sendMessage(generateEmbedErrorMessage(errorCode.getDescription()), channel);
	}
	
	private String generateItembuildAndSkillorder(ChampionInfoDto championInfoDto) {
		String result = "";
		Map<Integer, Item> items = ItemCache.getInstance().getItems();
		result +="**__Itembuild: __** ";
		for (String itemFromBuild : championInfoDto.hashes.finalitemshashfixed.highestCount.hash.substring(6).split("-")) {
			result += items.get(Integer.valueOf(itemFromBuild)).getName() + " → ";
		}
		result = result.substring(0, result.length()-3);
		result += "\r\n";
		result += "**__Skillorder:__** " + championInfoDto.hashes.skillorderhash.highestCount.hash.substring(6).replaceAll("-", "→");
		if(championInfoDto.hashes.evolveskillorder != null) {
			result += "\r\n";
			result += "**__Evolve Skillorder:__** " + championInfoDto.hashes.evolveskillorder.highestCount.hash.substring(7).replaceAll("-", "→");
		}
		result += "\r\n__**Runes:**__ ";
		
		List<StaticPerk> runes = RuneCache.getInstance().getRunes();
		int i = 1;
		for (String runeFromBuild : championInfoDto.hashes.runehash.highestCount.hash.split("-")) {
			if(i == 1 || i == 6) {
				result += "\r\n *" + runes.stream().filter(r -> r.getRunePathId().equals(runeFromBuild)).findFirst().get().getRunePathName() + "*: ";
			}else {
				result += runes.stream().filter(r -> r.getId() == Integer.valueOf(runeFromBuild)).findFirst().get().getName();
				if(i != 5 && i != 8) {
					result += " → ";
				}
			}
			
			i++;
		}
		return result;
	}
	
	public MessageEmbed generateChampionBuildAndSkillInfoMessage(ChampionInfoDto championInfo, String championName, String championKey) {
		EmbedBuilder builder = generateBasicEmbed(championKey, EmbeddedMessageType.OK);
		builder.addField(new Field("Here is your requested information about "+ championName + ":", "[Go to " + championName + "'s champion.gg page](" + CHAMPION_GG_URL + championKey +")", true));
		builder.addField(new Field(championName + " "+ championInfo.role , generateItembuildAndSkillorder(championInfo), false));
		return builder.build();
	}

	/**
	 * @param championKey
	 * @return
	 */
	private EmbedBuilder generateBasicEmbed(String championKey, EmbeddedMessageType embedType) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Brought to you by Nola's Discord Bot", FOOTER_ICON);
		if(EmbeddedMessageType.OK.equals(embedType)) {
			builder.setThumbnail(CHAMPION_ICON_URL + championKey +".png");
			builder.setColor(COLOR_OK);
		}else if(EmbeddedMessageType.ERROR.equals(embedType)) {
			builder.setThumbnail(ERROR_ICON_URL);
			builder.setColor(COLOR_ERROR);
		}else if(EmbeddedMessageType.WARN.equals(embedType)) {
			builder.setThumbnail(WARNING_ICON_URL);
			builder.setColor(COLOR_WARN);
		}
		return builder;
	}
	
	public MessageEmbed generateEmbedErrorMessage(String errorMessage) {
		EmbedBuilder builder = generateBasicEmbed(null, EmbeddedMessageType.ERROR);
		builder.addField(new Field("An error occurred:", errorMessage, true));
		return builder.build();
	}
	
	public MessageEmbed generateEmbedWarnMessage(String errorMessage) {
		EmbedBuilder builder = generateBasicEmbed(null, EmbeddedMessageType.WARN);
		builder.addField(new Field("An error occurred:", errorMessage, true));
		return builder.build();
	}
	
	public MessageEmbed generateEmbedWarnMessage(ErrorCodes errorCode) {
		EmbedBuilder builder = generateBasicEmbed(null, EmbeddedMessageType.WARN);
		builder.addField(new Field("An error occurred:", errorCode.getDescription(), true));
		return builder.build();
	}

	public void sendTipsMessages(StaticChampion champion, MessageChannel msgChannel) {
		sendTipsMessage(TIPS_TYPE_ALLY, champion, msgChannel);
		sendTipsMessage(TIPS_TYPE_ENEMY, champion, msgChannel);
	}

	private void sendTipsMessage(String tipsType, StaticChampion champ, MessageChannel channel) {
		List<String> tips = null;
		EmbedBuilder builder = null;

		if(TIPS_TYPE_ALLY.equals(tipsType)) {
			builder = generateBasicEmbed(champ.getKey(), EmbeddedMessageType.OK);
			tips = champ.getAllytips();
			String tipsForEmbed = generateTipsMessage(tips);
			builder.addField(new Field("Tips for playing as " + champ.getName() + " :", tipsForEmbed, false));
		}else if(TIPS_TYPE_ENEMY.equals(tipsType)) {
			builder = generateBasicEmbed(champ.getKey(), EmbeddedMessageType.ERROR);
			tips = champ.getEnemytips();
			String tipsForEmbed = generateTipsMessage(tips);
			builder.addField(new Field("Tips for playing against " + champ.getName() + " :", tipsForEmbed, false));
		}else {
			return;
		}
		this.sendMessage(builder.build(), channel);
	}

	private String generateTipsMessage(List<String> tips) {
		String result = "";
		for (String tip : tips) {
			result += "→" + tip + "\r\n \r\n";
		}
		return result;
	}
}
