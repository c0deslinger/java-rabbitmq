package main;

import org.apache.log4j.PropertyConfigurator;
import system.RabbitMQConsumer;
import system.RabbitMQProducer;

import static constant.Config.log;
import static constant.Config.rabbitmqserver_ip;

/**
 * @author Ahmed Yusuf
 */
public class Main {
    public static String config_path = null;
    public static void main(String[] args) {
        try{
            if(args.length > 0 && args[0].equals("consumer")){
                RabbitMQConsumer.getInstance().start();
            }else if(args.length > 0 && args[0].equals("producer")){
                RabbitMQProducer.getInstance();
                if(args.length > 1 && Integer.parseInt(args[1]) > 0){
                    RabbitMQProducer.getInstance().broadcast(Integer.parseInt(args[1]));
                }
            }else{
                System.out.println("1st arguments should be followed with consumer/producer");
                System.exit(0);
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Add argument 'consumer' or 'producer'");
        }
    }
}
