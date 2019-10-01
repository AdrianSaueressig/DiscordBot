package nolacola.discord.nolaDiscordBot.dto;

public class SummonerDto{
	private String name;
	private String asMention;
	private long id = -1;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAsMention() {
		return asMention;
	}
	public void setAsMention(String asMention) {
		this.asMention = asMention;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Summoner name=" + getName() + " asMention=" + getAsMention() + " id=" + getId();
	}
}
