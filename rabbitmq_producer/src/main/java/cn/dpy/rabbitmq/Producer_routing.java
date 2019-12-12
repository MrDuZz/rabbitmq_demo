package cn.dpy.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/12/12 16:16
 * @Version: 1.0
 */
public class Producer_routing {

    // 队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";
    private static final String ROUTINGKEY_EMAIL = "inform_email";
    private static final String ROUTINGKEY_SMS = "inform_sms";

    public static void main(String[] args) {
        // 通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("62.234.59.112");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");
        // 设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        // 建立新连接
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            // 创建会话通道，生产者和服务所有的通信都在Channel中
            channel = connection.createChannel();
            // 声明队列，如果队列在mq中没有则要创建
            /**
             * String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
             * 参数明细
             *  1、queue 队列名称
             *  2、durable 是否持久化，如果持久化，mq重启后队列还在
             *  3、exclusive 是否独占连接，队列只允许在该连接中访问，特色：如果连接关闭队列自动删除，如果此参数设置true可用于临时队列的创建
             *  4、autoDelete 自动删除，队列不再使用时是否自动删除队列，如果将此参数和exclusive参数设置为true就可以实现临时队列(队列不用了就自动删除)
             *  5、argument 参数，可以设置一个队列的扩展参数，如果，可设置存活时间....
             */
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            // 声明交换机
            /**
             * String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments
             * 参数明细
             *  1、exchange 交换机的名称
             *  2、type 交换机的类型
             *      fanout：对应的rabbitmq的工作模式是 publish/subscribe
             *      direct：对应的Routing工作模式
             *      topic：对应的Topics工作模式
             *      headers：对应的headers工作模式
             */
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            // 进行交换机和队列绑定
            /**
             * 参数：String queue, String exchange, String routingKey, Map<String, Object> arguments
             * 参数明细：
             *  1、queue：队列名称
             *  2、exchange：交换机名称
             *  3、routingKey：路由key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中协调为空字符串
             *
             */
            // 消息内容
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, "inform");
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, "inform");
            // 发送消息
            /**
             * String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body
             * 参数明细
             * 1、exchange：交换机
             * 2、rotingKey：路由key，交换机根据路由key来将消息转发到指定的队列，如果使用默认交换机，rotingKey设置为队列的名称
             * 3、props：消息的属性
             * 4、body：消息内容
             */
//            for (int i = 0; i < 5; i++) {
//                // 消息内容
//                // 发送消息的时候指定routingKey
//                String message = new String("发送路由模式email消息".getBytes(), "GBK");;
//                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL, null, message.getBytes());
//                System.out.println("send to mq " + message);
//            }
//
//            for (int i = 0; i < 5; i++) {
//                // 消息内容
//                // 发送消息的时候指定routingKey
//                String message = new String("发送路由模式sms消息".getBytes(), "GBK");;
//                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS, null, message.getBytes());
//                System.out.println("send to mq " + message);
//            }

            for (int i = 0; i < 5; i++) {
                // 消息内容
                // 发送消息的时候指定routingKey
                String message = new String("发送路由模式订阅发布inform消息".getBytes(), "GBK");;
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, "inform", null, message.getBytes());
                System.out.println("send to mq " + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭通道
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
