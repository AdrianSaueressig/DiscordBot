package nolacola.discord.nolaDiscordBot.manager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;

public class PropertyManager {
	
	private static final String PROPERTIES_FILE = "settings.properties";
	private Properties sysProps = null;
	private OutputStream out;
	private InputStream in;
	
	public Properties loadProperties() throws NolaBotException{
		sysProps = new Properties();
		in = PropertyManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);

		try {
			sysProps.load(in);
		} catch (IOException e) {
			throw new NolaBotException(ErrorCodes.ERROR_LOADING_PROPERTIES);
		}
		
		return sysProps;
	}
	
	public void setProperty(String key, String value) throws NolaBotException{
		if(sysProps == null) {
			loadProperties();
		}
		sysProps.setProperty(key, value);
		
		try {
			out = new FileOutputStream(PROPERTIES_FILE);
			sysProps.store(out, null);
		} catch (IOException e) {
			throw new NolaBotException(ErrorCodes.ERROR_STORING_PROPERTIES);
		}
	}
}
