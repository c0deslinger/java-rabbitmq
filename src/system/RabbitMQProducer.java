package system;

import com.rabbitmq.client.*;
import constant.Config;
import main.Main;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static constant.Config.*;

/**
 * @author Ahmed Yusuf
 */
public class RabbitMQProducer {
    ExecutorService executor = Executors.newFixedThreadPool(100);
    ConnectionFactory factory;
    Connection connection;
    static RabbitMQProducer instance;

    public static RabbitMQProducer getInstance() {
        if(instance == null)
            instance = new RabbitMQProducer();
        return instance;
    }

    public RabbitMQProducer() {
        PropertyConfigurator.configure("./config/config.conf");
        Main.config_path = "./config/config.conf";
    }

    void init(){
        factory = new ConnectionFactory();
        try {
            factory.setUri(Config.rabbitmqserver_uri);
            connection = factory.newConnection();
            System.out.println("Running as Producer");
            System.out.println("Connected to: "+Config.rabbitmqserver_uri);
            System.out.println("Queue name: "+Config.rabbitmqserver_queue_name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(int limit){
        init();
        try {
            for (int i = 0; i < limit; i++) {
                int finalI = i;
                executor.execute(() -> {
                    try {
                        String msisdn = "62810000000";
                        boolean durable = true;
                        Channel channel = connection.createChannel();
                        //handle queue
                        channel.queueDeclare(Config.rabbitmqserver_queue_name, durable, false, false, null);
                        String message = String.valueOf(Long.parseLong(msisdn)+ finalI);
                        channel.basicPublish("", Config.rabbitmqserver_queue_name, null, message.getBytes());
                        log.info("Publish|"+Config.rabbitmqserver_queue_name+"|"+msisdn);
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                });
            }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
