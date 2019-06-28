package constant;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static main.Main.config_path;

/**
 * @author Ahmed Yusuf
 */
public class Config {

    public final static Properties prop = new Properties();
    public static Logger log = Logger.getLogger("main");
    public static Config config = new Config(config_path);

    public static String rabbitmqserver_uri = prop.getProperty("rabbitmqserver_uri");
    public static String rabbitmqserver_queue_name = prop.getProperty("rabbitmqserver_queue_name", "queue-test");
    public static boolean rabbitmqserver_autoACK = Boolean.parseBoolean(prop.getProperty("rabbitmqserver_auto_ack", "true"));
    public static String rabbitmqserver_ip = prop.getProperty("rabbitmqserver_ip", "localhost");
    public static int rabbitmqserver_port = Integer.valueOf(prop.getProperty("rabbitmqserver_port", "15678"));
    public static String rabbitmqserver_username = prop.getProperty("rabbitmqserver_username", "user1");
    public static String rabbitmqserver_password = prop.getProperty("rabbitmqserver_password", "MyPassword");

    Config(String path) {
        try {
            if(path == null) {
                prop.load(new FileInputStream("./config/config-producer.conf"));
            }else{
                prop.load(new FileInputStream(path));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error(ex.toString());
        }
    }

}
