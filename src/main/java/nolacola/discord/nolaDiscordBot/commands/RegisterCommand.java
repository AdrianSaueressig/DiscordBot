package nolacola.discord.nolaDiscordBot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;

import net.dv8tion.jda.core.entities.MessageChannel;
import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.handler.bot.MessageHandler;
import nolacola.discord.nolaDiscordBot.handler.bot.RegistrationHandler;

public class RegisterCommand extends Command {
	
	private static final Logger logger = LogManager.getLogger(RegisterCommand.class);
	private MessageHandler messageHandler = null;
	private RegistrationHandler registrationHandler = null;
	
	//used in the form of "[prefix]register" or "[prefix]register summonerName"
	public RegisterCommand() {
		this.name = "register";
		this.help = "used to register your discord user to your League of Legends SummonerName. This is important to be able to use !champ. Only EUW is supported";
		this.arguments = "[summonerName]";
		
		this.messageHandler = new MessageHandler();
		this.registrationHandler = new RegistrationHandler();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		String userAsMention = event.getAuthor().getAsMention();
		MessageChannel msgChannel = event.getChannel();
		logger.info("Register called by userAsMention:" + userAsMention);
		
		if (event.getArgs().isEmpty()) {
			messageHandler.sendMessage("Please enter your summoner name! e.g !register thisismysummoner", msgChannel);
			return;
		}

		try {
			messageHandler.sendMessage(registrationHandler.register(userAsMention, event.getArgs()), msgChannel);
		} catch (NolaBotException e) {
			messageHandler.sendErrorMessage(e, msgChannel);
		}
	}

}
