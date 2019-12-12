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

    // ��������
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";
    private static final String ROUTINGKEY_EMAIL = "inform_email";
    private static final String ROUTINGKEY_SMS = "inform_sms";

    public static void main(String[] args) {
        // ͨ�����ӹ��������µ����Ӻ�mq��������
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("62.234.59.112");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");
        // �����������һ��mq����������ö���������ÿ��������൱��һ��������mq
        connectionFactory.setVirtualHost("/");
        // ����������
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            // �����Ựͨ���������ߺͷ������е�ͨ�Ŷ���Channel��
            channel = connection.createChannel();
            // �������У����������mq��û����Ҫ����
            /**
             * String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
             * ������ϸ
             *  1��queue ��������
             *  2��durable �Ƿ�־û�������־û���mq��������л���
             *  3��exclusive �Ƿ��ռ���ӣ�����ֻ�����ڸ������з��ʣ���ɫ��������ӹرն����Զ�ɾ��������˲�������true��������ʱ���еĴ���
             *  4��autoDelete �Զ�ɾ�������в���ʹ��ʱ�Ƿ��Զ�ɾ�����У�������˲�����exclusive��������Ϊtrue�Ϳ���ʵ����ʱ����(���в����˾��Զ�ɾ��)
             *  5��argument ��������������һ�����е���չ����������������ô��ʱ��....
             */
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            // ����������
            /**
             * String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments
             * ������ϸ
             *  1��exchange ������������
             *  2��type ������������
             *      fanout����Ӧ��rabbitmq�Ĺ���ģʽ�� publish/subscribe
             *      direct����Ӧ��Routing����ģʽ
             *      topic����Ӧ��Topics����ģʽ
             *      headers����Ӧ��headers����ģʽ
             */
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            // ���н������Ͷ��а�
            /**
             * ������String queue, String exchange, String routingKey, Map<String, Object> arguments
             * ������ϸ��
             *  1��queue����������
             *  2��exchange������������
             *  3��routingKey��·��key�������ǽ���������·��key��ֵ����Ϣת����ָ���Ķ����У��ڷ�������ģʽ��Э��Ϊ���ַ���
             *
             */
            // ��Ϣ����
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, "inform");
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, "inform");
            // ������Ϣ
            /**
             * String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body
             * ������ϸ
             * 1��exchange��������
             * 2��rotingKey��·��key������������·��key������Ϣת����ָ���Ķ��У����ʹ��Ĭ�Ͻ�������rotingKey����Ϊ���е�����
             * 3��props����Ϣ������
             * 4��body����Ϣ����
             */
//            for (int i = 0; i < 5; i++) {
//                // ��Ϣ����
//                // ������Ϣ��ʱ��ָ��routingKey
//                String message = new String("����·��ģʽemail��Ϣ".getBytes(), "GBK");;
//                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL, null, message.getBytes());
//                System.out.println("send to mq " + message);
//            }
//
//            for (int i = 0; i < 5; i++) {
//                // ��Ϣ����
//                // ������Ϣ��ʱ��ָ��routingKey
//                String message = new String("����·��ģʽsms��Ϣ".getBytes(), "GBK");;
//                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS, null, message.getBytes());
//                System.out.println("send to mq " + message);
//            }

            for (int i = 0; i < 5; i++) {
                // ��Ϣ����
                // ������Ϣ��ʱ��ָ��routingKey
                String message = new String("����·��ģʽ���ķ���inform��Ϣ".getBytes(), "GBK");;
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, "inform", null, message.getBytes());
                System.out.println("send to mq " + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // �ر�ͨ��
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
