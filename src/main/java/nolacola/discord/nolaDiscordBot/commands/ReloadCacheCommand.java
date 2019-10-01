package nolacola.discord.nolaDiscordBot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.caches.ApiKeyCache;
import nolacola.discord.nolaDiscordBot.handler.bot.MessageHandler;

public class ReloadCacheCommand extends Command {
	private static final Logger logger = LogManager.getLogger(ReloadCacheCommand.class);
	private MessageHandler messageHandler = null;

	public ReloadCacheCommand() {
		this.name = "reloadCache";
		this.help = "reload caches for champions & items. Only usable by the bot's owner";
		this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
		this.ownerCommand = true;
		
		this.messageHandler = new MessageHandler();
	}
		
	@Override
	protected void execute(CommandEvent event) {
		String userAsMention = event.getAuthor().getAsMention();
		MessageChannel msgChannel = event.getChannel();
		logger.info("ReloadCache called by userAsMention:" + userAsMention);
		
		try {
			ApiKeyCache.getInstance().reloadCache();
			messageHandler.sendMessage("Successful!", msgChannel);
		} catch (NolaBotException e) {
			messageHandler.sendErrorMessage(e, msgChannel);
		}
	}

}
