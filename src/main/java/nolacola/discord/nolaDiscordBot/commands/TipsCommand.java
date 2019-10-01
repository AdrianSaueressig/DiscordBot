package nolacola.discord.nolaDiscordBot.commands;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import no.stelar7.api.l4j8.pojo.staticdata.champion.StaticChampion;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;
import nolacola.discord.nolaDiscordBot.handler.bot.MessageHandler;
import nolacola.discord.nolaDiscordBot.handler.leagueoflegends.ChampHandler;

public class TipsCommand extends Command {
	private static final Logger logger = LogManager.getLogger(TipsCommand.class);
	private MessageHandler messageHandler = null;
	private ChampHandler champHandler = null;
	
	// used in the form of "[prefix]deleteMyData"
	public TipsCommand() {
		this.name = "tips";
		this.help = "displays tips for playing as or against that champion. Used like [prefix]tips the bot will try to determine the champ that you are currently ingame with. If you dont want that use [prefix]tips [champname]";
		this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
		this.arguments = "[champName]";
		
		this.champHandler = new ChampHandler();
		this.messageHandler = new MessageHandler();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		String userAsMention = event.getAuthor().getAsMention();
		MessageChannel msgChannel = event.getChannel();
		logger.info("Tips called by userAsMention:" + userAsMention);
		StaticChampion champion = null;
		
		if (!event.getArgs().isEmpty()) {
			champion = champHandler.loadChampionManually(event.getArgs());
		}else {
			try {
				champion = champHandler.loadChampionAutomatically(userAsMention);
			} catch (FileNotFoundException | NolaBotException e) {
				logger.error("Error retrieving Champ Stuff automatically", e);
				messageHandler.sendErrorMessage(e, msgChannel);
				return;
			}
			
		}
		
		if(champion == null) {
			messageHandler.sendErrorMessage(ErrorCodes.CHAMP_DOESNT_EXIST, msgChannel);
		}else {
			messageHandler.sendTipsMessages(champion, msgChannel);
		}
		
	}
}
