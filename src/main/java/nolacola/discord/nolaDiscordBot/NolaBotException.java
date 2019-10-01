package nolacola.discord.nolaDiscordBot;

import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;

public class NolaBotException extends Exception {

	private static final long serialVersionUID = 5678060739174943132L;
	private ErrorCodes errorCode = null;
	
	public NolaBotException(ErrorCodes errorCode) {
		this.setErrorCode(errorCode);
	}

	public ErrorCodes getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCodes errorCode) {
		this.errorCode = errorCode;
	}
	
}
