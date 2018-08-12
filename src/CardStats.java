import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.text.DecimalFormat;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Objects;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Scanner;
import java.lang.Math;
import java.util.Map.Entry;

/**
 * @author Dilshan Pathirana
 * @version 1.0
 * A command line tool offering interesting statistical
 * evidence about Hearthstone cards.
 */
public class CardStats {
    private JSONArray arr;
    private List<JSONObject> maxAttackCards;
    private List<JSONObject> minAttackCards;
    private int numCards;
    private Map<String, List<String>> artistMap;
    private DecimalFormat df;

    private CardStats() {
        arr = JSONArrayGenerator.parsedFile();
        maxAttackCards = new ArrayList<>();
        minAttackCards = new ArrayList<>();
        df = new DecimalFormat("##.##");
        artistMap =  new HashMap<>();
    }

    private JSONArray getStatsArray() {
        return this.arr;
    }

    private List<JSONObject> getMaxAttackCards() {
        return this.maxAttackCards;
    }

    private List<JSONObject> getMinAttackCards() {
        return this.minAttackCards;
    }

    private void findHighestAttack(JSONArray arr) {
        for (Object o: arr) {
            JSONObject card = (JSONObject) o;
            if (card.containsKey("attack")) {
                Long attack = (Long) card.get("attack");
                if (attack == 12L) {
                    maxAttackCards.add(card);
                }
            }
        }
    }

    private void findLowestAttack(JSONArray arr) {
        for (Object o: arr) {
            JSONObject card = (JSONObject) o;
            if (card.containsKey("attack")) {
                Long attack = (Long) card.get("attack");
                if (attack == 0L) {
                    minAttackCards.add(card);
                }
            }
        }
    }

    private void displayRandomCard() {
        for (Object o : arr) {
            int randIndex = (int) Math.floor(Math.random() * 1614);
            List<JSONObject> singletonList = Collections.singletonList((JSONObject) arr.get(randIndex));
            JSONObject sample = singletonList.get(0);
            if (sample.containsKey("cost")) {
                displayCardFromJSONList(singletonList);
                ImageUrl.displayImageFromUrl((String) sample.get("id"));
                break;
            }
        }
    }

    private void displayCardFromJSONList(List<JSONObject> lst) {
        System.out.println("Number of cards found: " + lst.size());
        for (JSONObject obj : lst) {
            displayCardByID(obj);
        }
    }

    public static void displayCardByID(JSONObject obj) {
        if (obj.containsKey("cost")) {
            System.out.println(" ");
            System.out.println(Colors.BOLD + "Name of card: " + Colors.ANSI_RESET + (String) obj.get("name"));
            System.out.println(Colors.BOLD + "Mana Cost: " + Colors.ANSI_RESET + (Long) obj.get("cost"));
            System.out.println(Colors.BOLD + "Attack Damage: " + Colors.ANSI_RESET + (obj.containsKey("attack") ? (Long) obj.get("attack") : "None"));
            System.out.println(Colors.BOLD + "Health: " + Colors.ANSI_RESET + (obj.containsKey("health") ? (Long) obj.get("health") : "None"));
            if (obj.containsKey("text")) {
                String text = (String) obj.get("text");
                text = text.replaceAll("<b>", "");
                text = text.replaceAll("</b>", "");
                text = text.replaceAll("\\[x\\]", "");
                text = text.replaceAll("\n", " ");
                text = text.replaceAll("\\$", "");
                text = text.replaceAll("<i>", "");
                text = text.replaceAll("</i>", "");
                System.out.println(Colors.BOLD + "Text: " + Colors.ANSI_RESET + text);
            }
            System.out.println(" ");
            System.out.println("-------------------------------------------------------" +
                    "----------------------------------------------------------------------");
        }
    }

    private int getNumCards() {
        int count = 0;
        for (Object o : arr) {
            JSONObject card = (JSONObject) o;
            if (card.containsKey("cost")) {
                count++;
            }
        }
        return count;
    }

    private void setNumCards(int num) {
        this.numCards = num;
    }

    private void displayTotalNumCards() {
        System.out.println(Colors.ANSI_GREEN + "Hearthstone has a total of " + getNumCards() + " collectible cards." + Colors.ANSI_RESET);
    }

    private void calculateFactionPercent() {
        int allianceCount = 0;
        int hordeCount = 0;
        int factionCount = 0;
        for (Object o: arr) {
            JSONObject card = (JSONObject) o;
            if (card.containsKey("faction")) {
                factionCount++;
                String faction = (String) card.get("faction");
                if (faction.equals("ALLIANCE")) {
                    allianceCount++;
                }
                if (faction.equals("HORDE")) {
                    hordeCount++;
                }
            }
        }
        double percentAlliance = (double) allianceCount * 100 / factionCount;
        double percentHorde = (double) (hordeCount * 100) / factionCount;
        System.out.println("There are a total of " + factionCount + " cards that have a faction");
        System.out.println(allianceCount + " of them belong to the Alliance or " + df.format(percentAlliance) + "%");
        System.out.println(hordeCount + " of them belong to the Horde or " + df.format(percentHorde) + "%");
    }

    private void setArtists() {
        for (Object o : arr) {
            JSONObject card = (JSONObject) o;
            if (card.containsKey("artist") && card.containsKey("name")) {
                String artistName = (String) card.get("artist");
                String cardName = (String) card.get("name");
                if (artistMap.containsKey(artistName)) {
                    List<String> lst = new ArrayList<>(artistMap.get(artistName));
                    lst.add(cardName);
                    artistMap.put(artistName, lst);
                } else {
                    artistMap.put(artistName, Collections.singletonList(cardName));
                }
            }
        }
    }

    private void displayArtists() {
        List<Integer> sizes = new ArrayList<>();
        Map<String, Integer> newMap = new HashMap<>();
        for (Map.Entry<String, List<String>> pair : artistMap.entrySet()) {
            int size = pair.getValue().size();
            newMap.put(pair.getKey(), size);
            sizes.add(size);
        }
        Collections.sort(sizes, Collections.reverseOrder());
        Set<Integer> noDuplicates = new LinkedHashSet<>(sizes);
        List<Integer> nums = new ArrayList<>(noDuplicates);
        int position = 1;
        for(int i = 0; i < 10 && position < 11; i++) {
            Set<String> result = getKeysByValue(newMap, nums.get(i));
            String nameList = "";
            if (result.size() == 1) {
                String nameOfArtist = result.iterator().next();
                nameList = nameList.concat(position + ". " + nameOfArtist + " has illustrated " + nums.get(i) + " cards.");
                System.out.println(nameList + " E.g. " + Colors.ANSI_BLACK_BACKGROUND +
                        Colors.ANSI_GREEN + artistMap.get(nameOfArtist).subList(0, 3) + Colors.ANSI_RESET);
                position++;
            } else {
                nameList = position + ". ";
                if (result.iterator().hasNext()) {
                    String nameOfArtist = result.iterator().next();
                    for (String ser : result) {
                        nameList = nameList.concat(ser + ", ");
                    }
                    nameList = nameList.concat("have illustrated " + nums.get(i) + " cards.");
                    System.out.println(nameList + " E.g. " + Colors.ANSI_BLACK_BACKGROUND +
                            Colors.ANSI_GREEN + artistMap.get(nameOfArtist).subList(0, 3) + Colors.ANSI_RESET);
                    position++;
                }
            }
        }
    }

    private Set<String> getKeysByValue(Map<String, Integer> map, Integer val) {
        Set<String> keys = new HashSet<String>();
        for (Entry<String, Integer> entry : map.entrySet()) {
            if (Objects.equals(val, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    private void displayToolCommands() {
        System.out.println(" ");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "maxAttack:" + Colors.ANSI_RESET + " Displays card info on cards that have the highest attack (including weapons).");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "minAttack:" + Colors.ANSI_RESET + " Displays card info on cards that have the lowest attack (including weapons).");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "total:" + Colors.ANSI_RESET + " Number of total collectible Hearthstone cards. It's a lot.");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "faction:" + Colors.ANSI_RESET + " Count and Percentage of cards with Horde/Alliance Faction.");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "random:" + Colors.ANSI_RESET + " Displays a randomly chosen Hearthstone card.");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "artists:" + Colors.ANSI_RESET + " Shows the top 10 artists (ordered by # of cards illustrated) and 3 cards they have drawn.");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "rarity:" + Colors.ANSI_RESET + " % breakdown of cards by their rarity.");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "keyword:" + Colors.ANSI_RESET + " % breakdown of cards by 22 different keywords.");
        System.out.println("     " + Colors.BOLD + Colors.ANSI_GREEN + Colors.ANSI_BLACK_BACKGROUND + "exit:" + Colors.ANSI_RESET + " To exit out of the tool. Goodbye.");
        System.out.println(" ");
    }

    public static void main(String[] args) {
        //Initial Setup
        Scanner input = new Scanner(System.in);
        CardStats cardStats = new CardStats();
        JSONArray statsArr = cardStats.getStatsArray();
        cardStats.findHighestAttack(statsArr);
        cardStats.findLowestAttack(statsArr);
        Percentage p = new Percentage();
        p.calcRarity();
        p.calcKeyWord();
        int numCards = cardStats.getNumCards();
        cardStats.setNumCards(numCards);
        cardStats.setArtists();
        System.out.print("Welcome to CardStats, an interactive command line tool for observing aggregate " +
                "statistics\n" +"and features I found interesting in Hearthstone Cards.\n");
        System.out.println(" ");
        System.out.print("Here are some commands you can type in to learn some information about certain cards:\n");
        String choice = "";
        while (!choice.equals("exit")) {
            cardStats.displayToolCommands();
            choice = input.next();
            switch (choice) {
                case "maxAttack":
                    System.out.println(" ");
                    cardStats.displayCardFromJSONList(cardStats.getMaxAttackCards());
                    break;
                case "exit":
                    System.exit(0);
                case "minAttack":
                    System.out.println(" ");
                    cardStats.displayCardFromJSONList(cardStats.getMinAttackCards());
                    break;
                case "total":
                    System.out.println(" ");
                    cardStats.displayTotalNumCards();
                    break;
                case "faction":
                    System.out.println(" ");
                    cardStats.calculateFactionPercent();
                    break;
                case "random":
                    System.out.println(" ");
                    System.out.println(Colors.BOLD + "A window with the card info and picture will pop up for 10 seconds." + Colors.ANSI_RESET);
                    System.out.println(" ");
                    cardStats.displayRandomCard();
                    break;
                case "artists":
                    System.out.println(" ");
                    cardStats.displayArtists();
                    break;
                case "rarity":
                    System.out.println(" ");
                    p.displayRarity();
                    break;
                case "keyword":
                    System.out.println(" ");
                    p.displayKeyWords();
                    break;
                default:
                    System.out.println("'" + choice + "'" + " is not a valid command. Please choose a command from the list above.");
                    break;
            }
        }
    }
}
