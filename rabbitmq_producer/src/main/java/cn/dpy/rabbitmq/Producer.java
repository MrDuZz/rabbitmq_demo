package cn.dpy.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/12/9 14:55
 * @Version: 1.0
 */
public class Producer {

    private final static String QUEUE_NAME = "hello";

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
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // ������Ϣ
            /**
             * String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body
             * ������ϸ
             * 1��exchange��������
             * 2��rotingKey��·��key������������·��key������Ϣת����ָ���Ķ��У����ʹ��Ĭ�Ͻ�������rotingKey����Ϊ���е�����
             * 3��props����Ϣ������
             * 4��body����Ϣ����
             */
            // ��Ϣ����
            String message = "hello world";

            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("send to mq " + message);
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
