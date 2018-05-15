package mysqlserver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * MySQL server wrapper.
 */
public class MySQLServer {

    protected final File baseDir;
    protected final File installDir;
    protected final int port;

    /**
     * Create wrapper for server instance.
     *
     * @param baseDir Dir with MySQL (extracted) bundle
     * @param installDir Dir for installing server instance
     * @param port Port number for running server instance
     */
    public MySQLServer(File baseDir, File installDir, int port) {
        if (!baseDir.isDirectory()) {
            throw new RuntimeException(String.format("%s not found", baseDir.getAbsolutePath()));
        }
        this.baseDir = baseDir;
        this.installDir = installDir;
        this.port = port;
    }

    /**
     * Initialize server instance. Creates a new installDir or cleans up an existing one.
     * Creates user 'root' with no password and full permissions.
     *
     * @return Exit code (0 means success)
     * @throws IOException
     * @throws InterruptedException
     */
    public int init() throws IOException, InterruptedException {
        if (!installDir.isDirectory() && !installDir.mkdir()) {
            throw new RuntimeException(String.format("Unable to create %s", installDir.getAbsolutePath()));
        }
        File dataDir = new File(installDir, "data");
        if (dataDir.isDirectory() && !delete(dataDir)) {
            throw new RuntimeException(String.format("Unable to delete %s", dataDir.getAbsolutePath()));
        }
        File mysqld = new File(baseDir, "bin/mysqld");
        return execute(mysqld.getAbsolutePath(), "--no-defaults", "--initialize-insecure",
            "--basedir=" + baseDir.getAbsolutePath(), "--datadir=" + dataDir.getAbsolutePath());
    }

    /**
     * Start server instance.
     *
     * @return Exit code (0 means success)
     * @throws IOException
     * @throws InterruptedException
     */
    public int start() throws IOException, InterruptedException {
        File mysqld = new File(baseDir, "bin/mysqld");
        File dataDir = new File(installDir, "data");
        File logFile = new File(installDir, "log.txt");
        return execute(mysqld.getAbsolutePath(), "--no-defaults", "--port=" + port,
            "--basedir=" + baseDir.getAbsolutePath(), "--datadir=" + dataDir.getAbsolutePath(), "--socket=socket",
            "--log-error=" + logFile.getAbsolutePath());
    }

    /**
     * Ping server instance.
     *
     * @return 0 if the instance is running, 1 if it is not
     * @throws InterruptedException
     * @throws IOException
     */
    public int ping() throws InterruptedException, IOException {
        File mysqladmin = new File(baseDir, "bin/mysqladmin");
        return execute(mysqladmin.getAbsolutePath(), "--protocol=TCP", "--port=" + port, "--user=root", "ping");
    }

    /**
     * Stop server instance.
     *
     * @return Exit code (0 means success)
     * @throws IOException
     * @throws InterruptedException
     */
    public int stop() throws IOException, InterruptedException {
        File mysqladmin = new File(baseDir, "bin/mysqladmin");
        return execute(mysqladmin.getAbsolutePath(), "--protocol=TCP", "--port=" + port, "--user=root", "shutdown");
    }

    protected int execute(String... command) throws IOException, InterruptedException {
        System.out.println(Arrays.asList(command));
        return (new ProcessBuilder(command)).start().waitFor();
    }

    protected boolean delete(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            for (File f : fileOrDir.listFiles()) {
                delete(f);
            }
        }
        return fileOrDir.delete();
    }
}
