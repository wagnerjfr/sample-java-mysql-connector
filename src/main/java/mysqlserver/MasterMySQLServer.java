package mysqlserver;

import java.io.File;
import java.io.IOException;

/**
 * MasterMySQLServer wrapper extends MySQLServer.
 */
public class MasterMySQLServer extends MySQLServer {

    private int server_id; 
    private String log_bin;

    public MasterMySQLServer(File baseDir, File installDir, int port, int server_id, String log_bin) {
        super(baseDir, installDir, port);
        this.server_id = server_id;
        this.log_bin = log_bin;
    }

    @Override
    public int start() throws IOException, InterruptedException {
        File mysqld = new File(baseDir, "bin/mysqld");
        File dataDir = new File(installDir, "data");
        File logFile = new File(installDir, "log.txt");
        File logBin = new File(installDir, log_bin);

        return execute(mysqld.getAbsolutePath(), "--no-defaults", "--port=" + port,
            "--basedir=" + baseDir.getAbsolutePath(), "--datadir=" + dataDir.getAbsolutePath(), "--socket=socket",
            "--log-error=" + logFile.getAbsolutePath(), "--server-id=" + server_id, "--log-bin=" + logBin.getAbsolutePath());
    }
}
