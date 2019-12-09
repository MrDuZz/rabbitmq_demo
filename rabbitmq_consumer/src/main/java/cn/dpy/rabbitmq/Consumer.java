package cn.dpy.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/12/9 15:53
 * @Version: 1.0
 */
public class Consumer {

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
        try {
            connection = connectionFactory.newConnection();
            // �����Ựͨ���������ߺͷ������е�ͨ�Ŷ���Channel��
            Channel channel = connection.createChannel();
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

            // ʵ�����ѷ���
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){

                /**
                 * �����յ���Ϣ��˷�����������
                 * @param consumerTag �����߱�ǩ��������ʶ�����ߵģ��ڼ�������ʱ����channel.basicConsume
                 * @param envelope �ŷ⣬ͨ��envelop
                 * @param properties ��Ϣ����
                 * @param body ��Ϣ����
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    // ������
                    String exchange = envelope.getExchange();
                    // ��Ϣid��mq��channel��������ʶ��Ϣ��id��������ȷ����Ϣ�ѽ���
                    long deliveryTag = envelope.getDeliveryTag();
                    // ��Ϣ����
                    String message = new String(body, "utf-8");
                    System.out.println("receive message��" + message);
                }
            };


            // ��������
            /**
             * ������ϸ��
             * 1��queue ��������
             * 2��autoAck �Զ��ظ����������߽��յ���Ϣ��Ҫ����mq��Ϣ�ѽ��գ�������˲�������Ϊtrue����ʾ�Զ��ظ�mq�����Ҫ����ΪfalseҪͨ��ʵ�ֱ�̻ظ�
             * 3��callback �������߽��յ���ϢҪִ�еķ���
             */
            channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // �����߲��ùر����ӣ���Ϊ��Ҫһֱ����
//            connection.close();
        }


    }

}
