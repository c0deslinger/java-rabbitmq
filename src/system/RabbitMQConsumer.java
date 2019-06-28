package system;

import com.rabbitmq.client.*;
import constant.Config;
import main.Main;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static constant.Config.*;

/**
 * @author Ahmed Yusuf
 */
public class RabbitMQConsumer {
    ConnectionFactory factory;
    Connection connection;
    Channel channel;
    static RabbitMQConsumer instance = null;

    public static RabbitMQConsumer getInstance() {
        if(instance == null){
            instance = new RabbitMQConsumer();
            PropertyConfigurator.configure("./config/config.conf");
            Main.config_path = "./config/config.conf";
        }
        return instance;
    }

    private void init(){
        factory = new ConnectionFactory();
        try {
            factory.setUri(Config.rabbitmqserver_uri);
            connection = factory.newConnection();
            channel = connection.createChannel();
            System.out.println("Running as Consumer");
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

    public void start(){
        if(factory == null){
            init();
        }
        try {
            boolean durable = true;
            channel.queueDeclare(Config.rabbitmqserver_queue_name, durable, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), "UTF-8");
                try {
                    doWork(content);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(!rabbitmqserver_autoACK)
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            channel.basicConsume(Config.rabbitmqserver_queue_name, rabbitmqserver_autoACK, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RabbitMQConsumer.getInstance().start();
    }

    /**
     * Handling queue
     * @param content
     */
    void doWork(String content) throws Exception {
        log.debug("Consume|"+Config.rabbitmqserver_queue_name+"|"+content);
    }
}
