package mysqlserver;

import java.io.IOException;

/**
 * RunnableServer class
 */
public class RunnableServer implements Runnable {

    private MySQLServer mySQLServer;

    public RunnableServer(MySQLServer mySQLServer) {
        this.mySQLServer = mySQLServer;
    }

    @Override
    public void run() {

        try {
            mySQLServer.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
