package morseCode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class MorseDecoder {
	
	private Map<Character, String> charsAndMorseCodes = new HashMap<Character, String>();
	Set<String> contextDictionary = new HashSet<String>();
	List<String> morseCodeQueries = new ArrayList<String>();
	
	// START: Execution starts from here
	public static void main(String args[]) throws Exception {
		MorseDecoder decoder = new MorseDecoder();
		
		// Loads inputs from file, parses data segments & makes data available in respective charsAndMorseCodes, contextDictionary, queries instance variables
		decoder.loadInputs();
		
		decoder.doIt();
	}
	
	private void doIt() {
		// Encode all contextDictionary to morseCodes
		Map<String, List<String>> morseCodeAndContextWords = new HashMap<>();
		for(String word : contextDictionary) {
			String encoded = getEncodedString(word);
			List<String> words = morseCodeAndContextWords.getOrDefault(encoded, new ArrayList<String>());
			words.add(word);
			morseCodeAndContextWords.put(encoded, words);
		}
		
		for(String morseCode : morseCodeQueries) {
			System.out.println(getCloselyMatchedContextWord(morseCode, morseCodeAndContextWords, true));
		}
	}
	
	// Gets the decoded message from context for given morseCode
	private String getCloselyMatchedContextWord(String morseCode, Map<String, List<String>> morseCodeAndContextWords, boolean isExactMatch) {
		if(!morseCodeAndContextWords.containsKey(morseCode)) {
			return getMaxLengthMatchedContextWord(morseCode, morseCodeAndContextWords);
		}
		
		List<String> contextWords = morseCodeAndContextWords.get(morseCode);
		int i=0;
		int size=contextWords.get(0).length();
		for(int j=1; j<contextWords.size(); j++) {	// If we have more than one context word for same morse code, find min length word
			if(contextWords.get(j).length() < size) {
				i = j;
				size = contextWords.get(j).length();
			}
		}
		
		StringBuilder output = new StringBuilder(contextWords.get(i)).append((contextWords.size()>1 ? "!" : "")).append(isExactMatch ? "" : "?");
		return output.toString();
	}
	
	// If we didn't find exact message for given morseCode, we'll try to get the nearest matching word from context
	private String getMaxLengthMatchedContextWord(String morseCode, Map<String, List<String>> morseCodeAndContextWords) {
		int length=0;
		String matchedKey = null;
		for(String key : morseCodeAndContextWords.keySet()) {
			if(key.startsWith(morseCode) && key.length() > length) {	// Matched with greater word length
				matchedKey = key;
				length = key.length();
			}
		}
		
		return getCloselyMatchedContextWord(matchedKey, morseCodeAndContextWords, false);
	}
	
	// Given string will be encoded to morse code
	private String getEncodedString(String alphaNumericString) {
		StringBuilder morseCodeString = new StringBuilder();
		for(char alphaNumericChar : alphaNumericString.toCharArray()) {
			morseCodeString.append(charsAndMorseCodes.get(alphaNumericChar));
		}
		return morseCodeString.toString();
	}
	
	// Load inputs from file: Each line as a string
	private void loadInputs() throws FileNotFoundException {
		String path = Paths.get(System.getProperty("user.dir"), "src", "morseCode", "input.txt").toString();
		
		List<String> inputs = new ArrayList<String>();
		try(Scanner sc = new Scanner(new File(path))) {
			while(sc.hasNextLine()) {
				inputs.add(sc.nextLine());
			}
		}
		
		prepareInputs(inputs);
	}
	
	// Parse & store inputs for usage (line by line)
	private void prepareInputs(List<String> inputs) {
		int starCount = 0;
		for(String input : inputs) {
			String[] inputElements = input.trim().split("\\s+");
			if(inputElements.length == 1 && inputElements[0].charAt(0) == '*') {
				starCount++;
				continue;
			}
			if(starCount == 0) {
				charsAndMorseCodes.put(inputElements[0].charAt(0), inputElements[1].trim());
			} else if(starCount == 1) {
				contextDictionary.add(inputElements[0].trim());
			} else {
				for(String query : inputElements) {
					morseCodeQueries.add(query.trim());
				}
			}
		}
	}
}