package nolacola.discord.nolaDiscordBot.commands;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.handler.bot.MessageHandler;
import nolacola.discord.nolaDiscordBot.handler.leagueoflegends.ChampHandler;

public class ChampionCommand extends Command {

	private static final Logger logger = LogManager.getLogger(ChampionCommand.class);
	private MessageHandler messageHandler = null;
	private ChampHandler champHandler = null;
	private List<String> roles = Arrays.asList("TOP", "MID", "JUNGLE", "SUPPORT", "ADC");
 	
	//used in the form of "[prefix]champ" or "[prefix]champ champName"
	public ChampionCommand() {
		this.name = "champ";
		this.help = "used with a champion name, the Bot will ask champion.gg for recommended builds/skillorder for that champion." +
		" Used without champ (only for registered users, see command register), the Bot will find the champion, that you are currently in a matchmade game with";
		this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
		this.arguments = "{championName} {role}";
		
		this.messageHandler = new MessageHandler();
		this.champHandler = new ChampHandler();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		String userAsMention = event.getAuthor().getAsMention();
		MessageChannel msgChannel = event.getChannel();
		logger.info("Champ called by userAsMention:" + userAsMention);
		
		String args = event.getArgs();
		if (!args.isEmpty()) {
			String[] argsSplitted = args.split(" ");
			
			//get role if last word is a role
			String role = roles.stream().filter(r -> r.equalsIgnoreCase(argsSplitted[argsSplitted.length-1])).findFirst().orElse(null); 
			
			if(argsSplitted.length == 1 && role != null) {
				try {
					champHandler.retrieveChampStuffAutomatically(userAsMention, role, msgChannel);
				} catch (FileNotFoundException | NolaBotException e) {
					logger.error("Error retrieving Champ Stuff automatically", e);
					messageHandler.sendErrorMessage(e, msgChannel);
				}
			}else {
				try {
					retrieveChampStuffManually(args, userAsMention, msgChannel, role);
				} catch (NolaBotException e) {
					logger.error("Error retrieving Champ Stuff manually", e);
					messageHandler.sendErrorMessage(e, msgChannel);
				}
			}
		}else {
			try {
				champHandler.retrieveChampStuffAutomatically(userAsMention, msgChannel);
			} catch (FileNotFoundException | NolaBotException e) {
				logger.error("Error retrieving Champ Stuff automatically", e);
				messageHandler.sendErrorMessage(e, msgChannel);
			}
			
		}
	}

	private void retrieveChampStuffManually(String args, String userAsMention, MessageChannel msgChannel, String role) throws NolaBotException {
		String champName = args;
		if(args.contains(" ") && role != null) {
			champName = args.substring(0, args.lastIndexOf(" ")).trim();
		}
		champHandler.retrieveChampStuffManually(userAsMention, champName, msgChannel, role);
	}

}
