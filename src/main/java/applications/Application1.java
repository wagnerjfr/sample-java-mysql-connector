package applications;
/**
* The Application1 program implements an application that:
- Initialize server instance (let's call it S1)
- Start S1 in a separate thread
- Connect to S1 (Tip: connect to the "mysql" database, the "test" database is no longer provided)
- Fetch the server version by executing "SELECT VERSION()" and print it to stdout
- Stop S1
*
* @author  Wagner Jose Franchin
* @version 1.0
* @since   2015-03-28 
*/

import java.io.File;
import java.io.IOException;
import java.sql.*;

import mysqlserver.MySQLServer;
import mysqlserver.RunnableServer;
import mysqlserver.Utils;

public class Application1 {

    private static String connectionString;
    private static String username;
    private static String password;

    private static Connection connection;
    private static Statement command;
    private static ResultSet data;

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {

        File baseDir = new File(Utils.MYSQL_PATH); 
        File installDir = new File("bdApp1"); 
        int port = 3306;

        connectionString = "jdbc:mysql://localhost:" + port + "/mysql" + Utils.CONNECT_PROPS;
        username = "root";
        password = "";

        final MySQLServer s1 = new MySQLServer(baseDir, installDir, port);

        try {

            //Server instance initialize
            if (s1.init() == 0) {
                System.out.println("Server instance initialized!");
            }

            //Start S1 in a new thread
            new Thread(new RunnableServer(s1)).start();

            //Test to verify if the server instance is done
            while (s1.ping() != 0) {
                System.out.println("wait...");
                Thread.sleep(500);
            }

            //Create a DB connection
            connection = DriverManager.getConnection(connectionString, username, password);
            command = connection.createStatement();

            //Create Query
            data = command.executeQuery("SELECT VERSION();");

            //Print result
            if (data.next()) {
                System.out.println("*****************************");
                System.out.println("MySQL Version: " + data.getString(1));
                System.out.println("*****************************");
            }

        } finally {
            
            if (data != null)
                data.close();
            if (command != null)
                command.close();
            if (connection != null)
                connection.close();

            //Stop S1
            s1.stop();
            System.out.println("S1 stopped!");
        }
    }
}
