package nolacola.discord.nolaDiscordBot.dto;

public class ChampionInfoDto {
	public GeneralChampionInfo _id;
	
	public class GeneralChampionInfo{
		public int championId;
		public String role;
	}
	
	public String elo;
	public String patch;
	public String championId;
	public String winRate;
	public String playRate;
	public String gamesPlayed;
	public String percentRolePlayed;
	public String banRate;
	public String role;
	
	public ChampionInfoHashes hashes;
	
	public class ChampionInfoHashes{
		public Hashes finalitemshashfixed;
		public Hashes masterieshash;
		public Hashes skillorderhash;
		public Hashes summonershash;
		public Hashes trinkethash;
		public Hashes firstitemshash;
		public Hashes runehash;
		public Hashes evolveskillorder;
		
		public class Hashes{
			public Hash highestCount;
			public Hash highestWinrate;
			
			public class Hash{
				public int count;
				public int wins;
				public float winrate;
				public String hash;
			}
		}
	}
}
