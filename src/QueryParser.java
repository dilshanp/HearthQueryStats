import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * @author Dilshan Pathirana
 * @version 1.0
 *
 * Currently only supports MySQL select statements by list_id
 * e.g 'select list_id from ... where ... group by ... having ...
 * order by ... limit ...
 *
 * This was done to support queries that are differentiated in the fields
 * from the 'where' condition onwards in the example above.
 * Print's a card's basic information (name, mana, attack, health, text)
 * from the generated query.
 */
public class QueryParser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Scanner line = new Scanner(System.in);
        Set<String> queryIDs = new HashSet<>();
        Map<String, JSONObject> map = new HashMap<>();
        JSONArray array = JSONArrayGenerator.parsedFile();
        for (Object obj : array) {
            JSONObject jsonObject = (JSONObject) obj;
            map.put((String) jsonObject.get("id"), jsonObject);
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Statement statement = null;
        ResultSet resultSet = null;
        String query = " ";
        System.out.println("Enter the connection name associated with your JDBC MySQL connection:");
        String name = scanner.next();
        System.out.println("Enter the username associated with your JDBC MySQL connection:");
        String username = scanner.next();
        System.out.println("Enter the password associated with your JDBC MySQL connection:");
        String password = scanner.next();
        try {
            //Ask for username and PW to DB and add it to getConnection
            Connection connection =
                    DriverManager.getConnection("jdbc:mysql://localhost/" + name + "?user=" + username + "&password=" + password +
                            "&useUnicode=true&useSSL=false" +
                            "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
            statement = connection.createStatement();
            System.out.println(" ");
            while (!query.equals("exit")) {
                System.out.println(Colors.BOLD + "Please enter your query (refer to the card_table.sql file for schema): " + Colors.ANSI_RESET);
                System.out.println(" ");
                query = line.nextLine();
                if (query.equals("exit")) {
                    System.exit(0);
                } else if (statement.execute(query)) {
                    resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        String s = resultSet.getString("list_id");
                        if (s != null && !queryIDs.contains(s)) {
                            queryIDs.add(s);
                            CardStats.displayCardByID(map.get(s));
                        }
                    }
                } else {
                    System.out.println("Invalid query.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqlEx) {sqlEx.printStackTrace(); }
            }
        }
    }
}
