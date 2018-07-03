# HearthQueryStats
A command line Hearthstone application supporting execution of a user's SQL queries 
through connection with MySQL/JDBC, as well as providing aggregate statistics of cards
based on their various features.
## API / Credit
I developed this project using IntelliJ IDEA CE and through the MAC OS X command line.
The JSON data file containing the Hearthstone cards and their information was provided by 
[HearthstoneJSON](https://hearthstonejson.com/) (A Hearthstone API that uses JSON instead of raw files for card data).
## Setup
**Prerequisites**: [Git](https://git-scm.com/), [MySQL](https://www.mysql.com/), (Optional: [MySQL WorkBench](https://www.mysql.com/products/workbench/)), [JDK 1.8+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [JSONSimple](https://code.google.com/archive/p/json-simple/downloads), [MySQL Java Connector](https://dev.mysql.com/downloads/connector/j/)

1. Clone the repo with `git clone https://github.com/dilshanp/HearthQueryStats.git`
2. Navigate to the src folder. Open MySQL Workbench and add a new connection with the following:
* Enter a connection name and make sure the 'Connection' tab is selected.
* Select 'Standard (TCP/IP)' as the method to connect to the RDBMS.
* Under Parameters, make sure Hostname is localhost (127.0.0.1) and the port # (default 3306). Then set the username and a password
3. Setup a MySQL server (On Mac go to System Preferences -> MySQL -> Initialize Database)
4. Enter a password for user 'root' and select 'Use Legacy Password Encryption'
5. Click 'Start MySQL Server'. Then go ahead and import the card_table.sql file
* Note: At the top of .sql file, the database is called 'test', but feel free to change the name if you feel the need to. However, keep the same name in 'use test' line by replacing 'test' with your preferred name.
6. Save and execute the .sql script. Now the database is setup and it is time to enter the Query Parser.

**Execution of Parser:**
1. In terminal,  run `cd src`
2. Then run `javac -cp pathToJSONSimpleJarFile;pathToMySQLConnectorJavaJarFile QueryParser.java`
* Note: Both path variables are the absolute path to the JSON Simple jar file and the JDBC jar file you downloaded.
3. Run `java -cp pathToJSONSimpleJarFile;pathToMySQLConnectorJavaJarFile QueryParser`
4. You will be prompted to enter connection name, username, and password for the DB.
5. Now you can enter queries involving 'list_id' (as of now) to get back cards back based off conditions of other columns
in the card_table.sql file. Review the file for the entire schema.

**Execution of CardStats:**
1. Make sure you are in the `src` directory
2. Then run `javac -cp pathToJSONSimpleJarFile: CardStats.java`
3. Finally run `java -cp pathToJSONSimpleJarFile: CardStats`
4. Now you can choose from a list of commands to display some aggregated statistics 
and features of cards.

## Improvements
One thing I look to improve on is adding more features to the CardStats program as well as adding the ability
to support more types of queries. Currently, the user needs to select by 'list_id' because the id of the card is
mapped to all of its attributes. The positive is it allows the user to focus on the conditions and grouping of 
what cards are returned and the cards are then displayed cleanly on the command line. The next step is being able
to return the rendering of the card with all possible combinations of columns.
