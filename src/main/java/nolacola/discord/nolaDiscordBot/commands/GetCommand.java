package nolacola.discord.nolaDiscordBot.commands;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.handler.bot.MessageHandler;
import nolacola.discord.nolaDiscordBot.manager.PropertyManager;

public class GetCommand extends Command {
	private static final Logger logger = LogManager.getLogger(ChampionCommand.class);

	private MessageHandler messageHandler = null;
	private PropertyManager propManager = null;

	public GetCommand() {
		this.name = "get";
		this.help = "The Bot will get a or all properties";
		this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
		this.arguments = "{settingName}";
		this.ownerCommand = true;

		this.messageHandler = new MessageHandler();
		this.propManager = new PropertyManager();
	}

	@Override
	protected void execute(CommandEvent event) {
		String args = event.getArgs();
		MessageChannel msgChannel = event.getChannel();
		Properties props;

		try {
			props = propManager.loadProperties();
			if (!args.isEmpty()) {
				String[] argsSplitted = args.split(" ");
				// print property with name from arg
			} else {
				// messageHandler; send all properties
			}
		} catch (NolaBotException e) {
			messageHandler.sendErrorMessage(e.getErrorCode(), msgChannel);
		}

	}

}
