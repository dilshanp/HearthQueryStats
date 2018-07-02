import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

/**
 * @author Dilshan Pathirana
 * @version 1.0
 */
public class JSONArrayGenerator {

    /*
     * Generates a JSON Array object from the HS data.json file
     * and allows for use in analysis of its contents.
     */
    public static JSONArray parsedFile() {
        JSONParser parser = new JSONParser();
        try {
            String filePath = new File("").getAbsolutePath();
            if (!filePath.substring(filePath.length()-3, filePath.length()).equals("src")) {
                filePath = filePath.concat("/src");
            }
            filePath = filePath.concat("/cardData/data.json");
            FileReader file = new FileReader(filePath);
            Object parsedFile = parser.parse(file);
            return (JSONArray) parsedFile;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (ParseException p) {
            System.out.println("Caught a Parsing Exception" + p.getMessage());
            p.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
