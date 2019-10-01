package nolacola.discord.nolaDiscordBot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nolacola.discord.nolaDiscordBot.NolaBotException;
import nolacola.discord.nolaDiscordBot.dto.SummonerDto;
import nolacola.discord.nolaDiscordBot.enums.ErrorCodes;

public class FileUtil {
	
	private static final String DISCORD_MENTION_TO_SUMMONER_NAME_FILE = "summoners.txt";
	private static final String API_KEY_FILE = "keys.txt";
	private static final String API_KEY_FILE_WINDOWS_LOCAL = "C:\\Users\\Biome\\eclipse-workspace\\NolasDiscordBot\\src\\main\\resources\\keys.txt";
	private static final String VALUE_SEPARATOR = ",";
	Logger log = LogManager.getLogger(FileUtil.class);
	
	private List<SummonerDto> summoners = null;
	
	public SummonerDto loadSummoner(String userAsMention) throws FileNotFoundException, NolaBotException{
		if(userAsMention == null) {
			return null;
		}
		
		List<SummonerDto> summoners = null;
		summoners = getSummoners();
		
		Optional<SummonerDto> findFirst = summoners.stream().filter(p -> p.getAsMention().equals(userAsMention)).findFirst();
		
		return findFirst.orElse(null);
	}
	
	private List<SummonerDto> getSummoners() throws NolaBotException, FileNotFoundException{
		if(summoners != null) {
			return summoners;
		}
		
	    FileReader in = new FileReader(getSummonerFile());
		BufferedReader br = new BufferedReader(in);
	    List<SummonerDto> fileAsList = new ArrayList<SummonerDto>();
	    
	    String line = "";
	    try {
			while ((line = br.readLine()) != null) {
				SummonerDto summoner = new SummonerDto();
				String[] summonerIds = line.split(VALUE_SEPARATOR);
				summoner.setAsMention(summonerIds[0]);
				summoner.setName(summonerIds[1]);
				if(summonerIds.length >= 3) {
					summoner.setId(Long.valueOf(summonerIds[2]));
				}
				fileAsList.add(summoner);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    summoners = fileAsList;
	    return fileAsList;
	}
	
	public List<String> getAPIKeys() throws NolaBotException {
		try {
			return Files.readAllLines(FileSystems.getDefault().getPath(API_KEY_FILE));
		} catch (IOException e) {
			log.error("Error reading APIKeyFile", e);
		}
		try {
			return Files.readAllLines(new File(API_KEY_FILE_WINDOWS_LOCAL).toPath());
		} catch (IOException e) {
			log.error("Error reading APIKeyFile", e);
		}
		throw new NolaBotException(ErrorCodes.ERROR_READING_FILE);
	}
	
	/**
	 * @return
	 * @throws NolaBotException 
	 */ //use on unix
	private File getSummonerFile() throws NolaBotException {
		try {
			return FileSystems.getDefault().getPath(DISCORD_MENTION_TO_SUMMONER_NAME_FILE).toFile();
			
		} catch (Exception e) {
			throw new NolaBotException(ErrorCodes.ERROR_READING_FILE);
		}
	}
	
	/**
	 * @param newSummonerInfo
	 * @return
	 */
	private String generateEntryFromSummoner(SummonerDto newSummonerInfo) {
		return newSummonerInfo.getAsMention() + VALUE_SEPARATOR + newSummonerInfo.getName() + VALUE_SEPARATOR + newSummonerInfo.getId();
	}
	
	/**
	 * @param newSummoner
	 */
	public void saveSummoner(SummonerDto newSummoner) {
		try {
			log.info("Saving summoner...");
			
			List<SummonerDto> currentlySavedSummoners = getSummoners();
			Optional<SummonerDto> foundSumm = currentlySavedSummoners.stream().filter(o -> o.getAsMention().equalsIgnoreCase(newSummoner.getAsMention())).findFirst();
			
			if(foundSumm.isPresent() && foundSumm.get().getId() == -1) {
				log.debug("Summoner already present and id got updated");
				foundSumm.get().setId(newSummoner.getId());
			}else {
				log.debug("Summoner added");
				currentlySavedSummoners.add(newSummoner);
			}
			
			saveSummonerFile(currentlySavedSummoners);
		} catch (IOException | NolaBotException e) {
		}
	}

	/**
	 * @param newFileContents
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NolaBotException 
	 */
	private void saveSummonerFile(List<SummonerDto> newFileContents) throws FileNotFoundException, IOException, NolaBotException {
		summoners = newFileContents;
		
		FileOutputStream fileOut = new FileOutputStream(getSummonerFile());
		for (SummonerDto summonerDto : newFileContents) {
			fileOut.write(generateEntryFromSummoner(summonerDto).getBytes());
			fileOut.write("\r\n".getBytes());
		}
		fileOut.close();
	}
	
	/**
	 * @param userAsMention
	 * @param summonerName
	 * @throws NolaBotException 
	 * @throws FileNotFoundException 
	 */
	public void registerSummoner(String userAsMention, String summonerName) throws NolaBotException, FileNotFoundException{
		log.info("Registering...");
		
		SummonerDto loadSummoner = loadSummoner(userAsMention);
		if(loadSummoner != null) {
			throw new NolaBotException(ErrorCodes.SUMMONER_ALREADY_REGISTERED);
		}
		
		SummonerDto newSummonerInfo = new SummonerDto();
		newSummonerInfo.setAsMention(userAsMention);
		newSummonerInfo.setName(summonerName);
		saveSummoner(newSummonerInfo);
	}

	public void deleteSummoner(SummonerDto summoner) throws NolaBotException {
		try {
			List<SummonerDto> summonerList = getSummoners();
			SummonerDto foundSummoner = summonerList.stream().filter(s -> s.getAsMention().equals(summoner.getAsMention())).findFirst().get();
			summonerList.remove(foundSummoner);
			saveSummonerFile(summonerList);
		} catch (FileNotFoundException e) {
			log.error("deleteSummoner", e);
			throw new NolaBotException(ErrorCodes.ERROR_READING_FILE);
		} catch (IOException e) {
			log.error("deleteSummoner", e);
			throw new NolaBotException(ErrorCodes.ERROR_SAVING_SUMMONER);
		}
		
	}

	public static String getValueSeparator() {
		return VALUE_SEPARATOR;
	}
}
