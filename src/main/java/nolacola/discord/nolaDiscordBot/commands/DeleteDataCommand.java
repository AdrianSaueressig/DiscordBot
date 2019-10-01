package nolacola.discord.nolaDiscordBot.commands;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.dto.SummonerDto;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;
import nolacola.discord.nolaDiscordBot.handler.bot.MessageHandler;
import nolacola.discord.nolaDiscordBot.util.FileUtil;

public class DeleteDataCommand extends Command {
	private static final Logger logger = LogManager.getLogger(DeleteDataCommand.class);
	private MessageHandler messageHandler = null;
	private FileUtil fileUtil = null;

	// used in the form of "[prefix]deleteMyData"
	public DeleteDataCommand() {
		this.name = "deleteMyData";
		this.help = "used to delete all your data. To use !champ again, you need to reregister.";
		this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };

		this.messageHandler = new MessageHandler();
		this.fileUtil = new FileUtil();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		String userAsMention = event.getAuthor().getAsMention();
		MessageChannel msgChannel = event.getChannel();
		logger.info("DeleteData called by userAsMention:" + userAsMention);
		
		SummonerDto summoner = null;
		try {
			summoner = fileUtil.loadSummoner(userAsMention);
		} catch (FileNotFoundException | NolaBotException e) {
			messageHandler.sendErrorMessage(e, msgChannel);
			return;
		}
		if(summoner == null) {
			messageHandler.sendErrorMessage(ErrorCodes.UNABLE_TO_DEL_USER_DOESNT_EXIST, msgChannel);
		}else {
			try {
				fileUtil.deleteSummoner(summoner);
			} catch (NolaBotException e) {
				messageHandler.sendErrorMessage(e, msgChannel);
			}
			messageHandler.sendMessage("User successfully deleted!", msgChannel);
		}
	}

}
