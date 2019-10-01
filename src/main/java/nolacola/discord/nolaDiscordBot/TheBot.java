package nolacola.discord.nolaDiscordBot;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commandclient.CommandClient;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import nolacola.discord.nolaDiscordBot.commands.ChampionCommand;
import nolacola.discord.nolaDiscordBot.commands.DeleteDataCommand;
import nolacola.discord.nolaDiscordBot.commands.RegisterCommand;
import nolacola.discord.nolaDiscordBot.commands.ReloadCacheCommand;
import nolacola.discord.nolaDiscordBot.commands.TipsCommand;

public class TheBot{
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(TheBot.class);
	
    public static void main( String[] args ) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException{
    	String token = ""; //@TODO load from properties
    	String ownerId = ""; // @TODO load from properties
    	JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT).setToken(token);
    	
    	CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
    	commandClientBuilder.setOwnerId(ownerId);
    	String prefix = "?";
		commandClientBuilder.setPrefix(prefix);
    	commandClientBuilder.setGame(Game.playing("use " + prefix +"help to get a list of commands"));
    	commandClientBuilder.addCommands(new ChampionCommand(), new RegisterCommand(), new DeleteDataCommand(), new ReloadCacheCommand(), new TipsCommand());
    	CommandClient commandClient = commandClientBuilder.build();
    	
    	jdaBuilder.addEventListener(commandClient);
    	@SuppressWarnings("unused")
		JDA jdaBot = jdaBuilder.buildBlocking();
    }
    
}
