package applications;
/**
* The Application2 program implements an application that:
- Initialize a server instance (S1)
- Start S1 in a separate thread as replication master, passing the required additional options
- Initialize another server instance (S2)
- Start S2 in a separate thread as replication slave, passing the required additional options
- Set the master (S1) configuration in the slave (S2)
- Start replication in the slave (S2)
- Create a table in the master (S1)
- Wait 2 seconds (for replication to occur)
- Check that the table exists in the slave (S2)
- Stop S2 and S1
*
* @author  Wagner Jose Franchin
* @version 1.0
* @since   2015-03-28 
*/

import java.io.File;
import java.io.IOException;
import java.sql.*;

import mysqlserver.*;

public class Application2 {

    private static String connectionStringMaster;
    private static String connectionStringSlave;
    private static String username;
    private static String password;

    private static Connection connectionMaster, connectionSlave;
    private static Statement commandMaster, commandSlave;
    private static ResultSet data;

    private static final String USER_TABLE = "PERSON";

    public static void main(String[] args) throws SQLException, InterruptedException, IOException {

        // Change below path to point to your MySQL binaries
        File baseDir = new File(Utils.MYSQL_PATH); 

        File installDirMaster = new File("bdApp2Master"); 
        File installDirSlave = new File("bdApp2Slave"); 

        int master_port = 3306;
        int slave_port = 3307;

        connectionStringMaster = "jdbc:mysql://localhost:" + master_port + "/mysql" + Utils.CONNECT_PROPS;
        connectionStringSlave = "jdbc:mysql://localhost:" + slave_port + "/mysql" + Utils.CONNECT_PROPS;

        username = "root";
        password = "";

        final MySQLServer s1 = new MasterMySQLServer(baseDir, installDirMaster, master_port, 1, "mysql-bin");
        final MySQLServer s2 = new SlaveMySQLServer(baseDir, installDirSlave, slave_port, 2);

        try {

            /*Initialize a server instance (S1)
            Start S1 in a separate thread as replication master, passing the required additional options*/
            initAndStarServer(s1);

            /*Initialize a server instance (S2)
            Start S2 in a separate thread as replication master, passing the required additional options*/
            initAndStarServer(s2);

            //Test to verify if the server instances are ready
            while (s1.ping() != 0 || s2.ping() != 0) {
                System.out.println("wait...");
                Thread.sleep(500);
            }

            //Create DB connections
            connectionMaster = DriverManager.getConnection(connectionStringMaster, username, password);
            connectionSlave = DriverManager.getConnection(connectionStringSlave, username, password);

            commandMaster = connectionMaster.createStatement();
            commandSlave = connectionSlave.createStatement();

            //Set the master (S1) configuration in the slave (S2)
            commandSlave.execute("CHANGE MASTER TO MASTER_HOST='localhost', MASTER_PORT=" + master_port + ", MASTER_USER='" + username + "';");

            //Start replication in the slave (S2)
            commandSlave.execute("START SLAVE;");

            testReplication();

        }
        finally {

            if (data != null)
                data.close();

            if (commandMaster != null)
                commandMaster.close();
            if (commandSlave != null)
                commandSlave.close();

            if (connectionMaster != null)
                connectionMaster.close();
            if (connectionSlave != null)
                connectionSlave.close();

            //Stop S2
            s2.stop();
            System.out.println("S2 stopped!");

            //Stop S1
            s1.stop();
            System.out.println("S1 stopped!");
        }
    }

    private static void initAndStarServer(MySQLServer s) throws IOException, InterruptedException {

        //Server instance initialize
        if (s.init() == 0) {
            System.out.println("Server instance initialized!");
        }

        //Start S in a new thread
        new Thread(new RunnableServer(s)).start();
    }

    /**
     * Method to run the replication test
     * 
     * @throws SQLException
     * @throws InterruptedException
     */
    private static void testReplication() throws SQLException, InterruptedException {

        System.out.println("\nTEST Replication");

        try {
            //Check that the table exists in the slave (S2)
            executeQuery(commandSlave, "DESCRIBE " + USER_TABLE + ";", Kind.SLAVE);
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }

        //Create a table in the master (S1)
        String command = "CREATE TABLE " + USER_TABLE + " (ID INT NOT NULL, NAME VARCHAR(20), LASTNAME VARCHAR(20));";
        commandMaster.execute(command);
        System.out.println("[" + Kind.MASTER.getName() + "]# " + command);

        //Check that the table exists in the slave (S1)
        executeQuery(commandMaster, "DESCRIBE " + USER_TABLE + ";", Kind.MASTER);

        //Wait 2 seconds (for replication)
        Thread.sleep(2000);

        //Check that the table exists in the slave (S2)
        executeQuery(commandSlave, "DESCRIBE " + USER_TABLE + ";", Kind.SLAVE);
    }

    /**
     * Method to execute queries
     * 
     * @param command
     * @param script
     * @param kind
     * @throws SQLException
     */
    private static void executeQuery(Statement command, String script, Kind kind) throws SQLException {
        System.out.println("\n[" + kind.getName() + "]# " + script);
        data = command.executeQuery(script);

        while (data.next()) {
            System.out.println(data.getString(1) + " " + data.getString(2) + " " + data.getString(3));
        }
        System.out.println("*****************************");
    }

    private enum Kind {
        MASTER("Master"), SLAVE("Slave");

        private String name;
        private Kind(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
}
