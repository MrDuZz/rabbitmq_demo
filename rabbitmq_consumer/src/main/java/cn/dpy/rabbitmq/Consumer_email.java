package cn.dpy.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/12/12 14:54
 * @Version: 1.0
 */
public class Consumer_email {

    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform";

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
        try {
            connection = connectionFactory.newConnection();
            // 创建会话通道，生产者和服务所有的通信都在Channel中
            Channel channel = connection.createChannel();
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
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
            // 进行交换机和队列绑定
            /**
             * 参数：String queue, String exchange, String routingKey, Map<String, Object> arguments
             * 参数明细：
             *  1、queue：队列名称
             *  2、exchange：交换机名称
             *  3、routingKey：路由key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中协调为空字符串
             *  4、props：消息的属性
             *  5、body：消息内容
             */
            // 消息内容
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_FANOUT_INFORM, "");

            // 实现消费方法
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){

                /**
                 * 当接收到消息后此方法将被调用
                 * @param consumerTag 消费者标签，用来标识消费者的，在监听队列时设置channel.basicConsume
                 * @param envelope 信封，通过envelop
                 * @param properties 消息属性
                 * @param body 消息内容
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    // 交换机
                    String exchange = envelope.getExchange();
                    // 消息id，mq在channel中用来标识消息的id；可用于确认消息已接收
                    long deliveryTag = envelope.getDeliveryTag();
                    // 消息内容
                    String message = new String(body, "GBK");
                    System.out.println("receive message：" + message);
                }
            };


            // 监听队列
            /**
             * 参数明细：
             * 1、queue 队列名称
             * 2、autoAck 自动回复，当消费者接收到消息后要告诉mq消息已接收，如果将此参数设置为true，表示自动回复mq，如果要设置为false要通过实现编程回复
             * 3、callback 当消费者接收到消息要执行的方法
             */
            channel.basicConsume(QUEUE_INFORM_EMAIL, true, defaultConsumer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 消费者不用关闭连接，因为需要一直监听
//            connection.close();
        }


    }

}
