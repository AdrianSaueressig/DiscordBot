package nolacola.discord.nolaDiscordBot.commands;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageChannel;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;
import nolacola.discord.nolaDiscordBot.handler.bot.MessageHandler;
import nolacola.discord.nolaDiscordBot.manager.PropertyManager;

public class SetCommand extends Command {
	private static final Logger logger = LogManager.getLogger(ChampionCommand.class);
	
	private MessageHandler messageHandler = null;
	private PropertyManager propManager = null;
 	
	public SetCommand() {
		this.name = "set";
		this.help = "The Bot will set the new value of a property";
		this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
		this.arguments = "{settingName} {value}";
		this.ownerCommand = true;
		
		this.messageHandler = new MessageHandler();
		this.propManager = new PropertyManager();
	}
	@Override
	protected void execute(CommandEvent event){
		String args = event.getArgs();
		MessageChannel msgChannel = event.getChannel();
		if (!args.isEmpty()) {
			String[] argsSplitted = args.split(" ");
			Properties props = null;
			try {
				props = propManager.loadProperties();
				if(props.containsKey(argsSplitted[0])) {
					propManager.setProperty(argsSplitted[0], argsSplitted[1]);
					messageHandler.sendMessage("Property successfully updated.", msgChannel);
				}else {
					messageHandler.sendErrorMessage(ErrorCodes.NO_PROPERTY_WITH_GIVEN_NAME, msgChannel);
				}
			} catch (NolaBotException e) {
				messageHandler.sendErrorMessage(e.getErrorCode(), msgChannel);
			}
		}else {
			messageHandler.sendErrorMessage(ErrorCodes.NO_PROPERTY_KEY_IN_MESSAGE, msgChannel);
		}
	}

}
