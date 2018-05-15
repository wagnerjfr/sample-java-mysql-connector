package mysqlserver;

import java.io.File;
import java.io.IOException;

/**
 * SlaveMySQLServer wrapper extends MySQLServer.
 */
public class SlaveMySQLServer extends MySQLServer {

    private int server_id; 

    public SlaveMySQLServer(File baseDir, File installDir, int port, int server_id) {
        super(baseDir, installDir, port);
        this.server_id = server_id;
    }

    @Override
    public int start() throws IOException, InterruptedException {
        File mysqld = new File(baseDir, "bin/mysqld");
        File dataDir = new File(installDir, "data");
        File logFile = new File(installDir, "log.txt");

        return execute(mysqld.getAbsolutePath(), "--no-defaults", "--port=" + port,
            "--basedir=" + baseDir.getAbsolutePath(), "--datadir=" + dataDir.getAbsolutePath(), "--socket=socket",
            "--log-error=" + logFile.getAbsolutePath(), "--server-id=" + server_id);
    }
}
