import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @author Dilshan Pathirana
 * @version 1.0
 * Separating the calculation of percentages from the rest
 * of the calculations done in CardStats.java
 */
public class Percentage {
    private JSONArray arr;
    private int freeCount;
    private int commonCount;
    private int rareCount;
    private int epicCount;
    private DecimalFormat df;
    private int legendaryCount;
    private double freePercent;
    private double commonPercent;
    private double rarePercent;
    private double epicPercent;
    private double legendaryPercent;
    private Map<String, Integer> wordMap;
    private int cardCount;

    public Percentage() {
        arr = JSONArrayGenerator.parsedFile();
        df = new DecimalFormat("##.##");
        wordMap = new HashMap<>();
    }

    private void incrementRarity(String rarity) {
        switch (rarity) {
            case "FREE":
                this.freeCount++;
                break;
            case "COMMON":
                this.commonCount++;
                break;
            case "RARE":
                this.rareCount++;
                break;
            case "EPIC":
                this.epicCount++;
                break;
            case "LEGENDARY":
                this.legendaryCount++;
                break;
        }
    }

    private void calcCountRarity() {
        //hero and armor case is for new hero DK armor gain cards, + hagatha from WW
        for (Object o : arr) {
            JSONObject card = (JSONObject) o;
            if (card.containsKey("rarity") && card.containsKey("type")) {
                String rarity = (String) card.get("rarity");
                String type = (String) card.get("type");
                if (card.containsKey("armor")) {
                    Long armor = (Long) card.get("armor");
                    if ((!type.equals("HERO") && armor != 5L) || (type.equals("HERO") && armor == 5L)) {
                        incrementRarity(rarity);
                    }
                }
                if (!type.equals("HERO")) {
                    incrementRarity(rarity);
                }
            }
        }
    }

    private void calcPercentRarity() {
        cardCount = freeCount + commonCount + rareCount + epicCount + legendaryCount;
        freePercent = (double) freeCount * 100 / cardCount;
        commonPercent = (double) commonCount * 100 / cardCount;
        rarePercent = (double) rareCount * 100 / cardCount;
        epicPercent = (double) epicCount * 100 / cardCount;
        legendaryPercent = (double) legendaryCount * 100 / cardCount;
    }

    public void calcRarity() {
        calcCountRarity();
        calcPercentRarity();
    }

    public void displayRarity() {
        System.out.println("There are a total of " + cardCount + " cards that have a certain rarity. Some card are excluded " +
                "because cards (like 'Garrosh') have rarity but are considered heroes and not collectible cards.");
        System.out.println(freeCount + " of them are Free Cards or " + df.format(freePercent) + "%");
        System.out.println(commonCount + " of them are Common Cards or " + df.format(commonPercent) + "%");
        System.out.println(rareCount + " of them are Rare Cards or " + df.format(rarePercent) + "%");
        System.out.println(epicCount + " of them are Epic Cards or " + df.format(epicPercent) + "%");
        System.out.println(legendaryCount + " of them are Legendary Cards or " + df.format(legendaryPercent) + "%");
    }

    public void calcKeyWord() {
        wordMap.clear();
        for (Object o : arr) {
            JSONObject card = (JSONObject) o;
            if (card.containsKey("text")) {
                String text = (String) card.get("text");
                if (text.toLowerCase().contains("magnetic")) {
                    if (wordMap.containsKey("Magnetic")) {
                        wordMap.put("Magnetic", wordMap.get("Magnetic") + 1);
                    } else {
                        wordMap.put("Magnetic", 1);
                    }
                }
                if (card.containsKey("referencedTags")) {
                    List<String> tags = (List<String>) card.get("referencedTags");
                    for (String s : tags) {
                        String lower = s.toLowerCase();
                        boolean hasKeyWord = text.toLowerCase().contains(lower);
                        if (hasKeyWord) {
                            if (wordMap.containsKey(lower)) {
                                wordMap.put(lower, wordMap.get(lower) + 1);
                            } else {
                                wordMap.put(lower, 1);
                            }
                        }
                    }
                }
                if (card.containsKey("mechanics")) {
                    List<String> tags = (List<String>) card.get("mechanics");
                    for (String s : tags) {
                        String lower = s.toLowerCase();
                        boolean hasKeyWord = text.toLowerCase().contains(lower);
                        if (hasKeyWord) {
                            if (wordMap.containsKey(lower)) {
                                wordMap.put(lower, wordMap.get(lower) + 1);
                            } else {
                                wordMap.put(lower, 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void displayKeyWords() {
        int mapValueCount = countValues(wordMap);
        System.out.println(Colors.BOLD + "Here are 23 Keywords and their corresponding percentages:" + Colors.ANSI_RESET);
        System.out.println(" ");
        for (Map.Entry<String, Integer> pair : wordMap.entrySet()) {
            String keyWord = pair.getKey();
            Integer count = pair.getValue();
            double percent = (double) count * 100 / mapValueCount;
            String copy = keyWord.toUpperCase();
            int keyWordLength = keyWord.length();
            System.out.println("           " + count + " cards with: " + Colors.BOLD + Colors.ANSI_CYAN_BACKGROUND +
                    copy.charAt(0) + keyWord.substring(1, keyWordLength) + Colors.ANSI_RESET + " or " + df.format(percent) + "%");
            System.out.println(" ");
        }
    }

    private int countValues(Map<String, Integer> hMap) {
        int count = 0;
        for (Integer i : hMap.values()) {
            count += i;
        }
        return count;
    }
}