package nolacola.discord.nolaDiscordBot.enums;

public enum ErrorCodes {
	  SUMMONER_NOT_FOUND(0, "An error occurred while reading summoner data from savefile."),
	  ERROR_RETRIEVING_CHAMP_ID_FROM_RIOT(1, "Sorry, there was a problem retrieving your champion from Riot. Please try again later (make sure you are ingame)"),
	  ERROR_RETRIEVING_CHAMP_INFO_AUTOMATICALLY(2, "Sorry, we weren't able to retrieve the champion information."),
	  ERROR_READING_FILE(3, "Error reading a file."),
	  SUMMONER_ALREADY_REGISTERED(4, "Summoner already exists. Please use !editRegisteredData (to be implemented. Annoy Nola if you need to use this)"),
	  ERROR_SAVING_SUMMONER(5, "There was an error saving your summonerdata."),
	  ERROR_IN_CACHE_FILE(6, "There is an error in the cachefile."),
	  CHAMP_DOESNT_EXIST(7, "Your champion does not seem to exist. Please check your spelling!"),
	  UNABLE_TO_DEL_USER_DOESNT_EXIST(8, "User does not exist. Therefore can't be deleted."),
	  SUMMONER_NOT_REGISTERED(9,"To use !champ you need to be registered first. Please use !champ champName or register by using !register summoner_name. This is only supported for EUW"),
	  UNPREDICTABLE_ERROR(10, "An unpredictable error occurred. (Or maybe it was bad coding? Blame Nola)"),
	  NO_CHAMPION_INFO_FOR_THAT_ROLE(11, "There is no information for your champion in this role. Try searching for another role or without a role at all!"),
	  NO_PROPERTY_KEY_IN_MESSAGE(12, "There was no property key supplied."),
	  ERROR_STORING_PROPERTIES(13, "An error occurred while saving properties."),
	  ERROR_LOADING_PROPERTIES(14, "An error occurred while loading properties."),
	  NO_PROPERTY_WITH_GIVEN_NAME(15, "No property with the given name exists");
	
	  private final int code;
	  private final String description;
	
	  private ErrorCodes(int code, String description) {
	    this.code = code;
	    this.description = description;
	  }
	
	  public String getDescription() {
	     return description;
	  }
	
	  public int getCode() {
	     return code;
	  }
	
	  @Override
	  public String toString() {
	    return code + ": " + description;
	  }
}
