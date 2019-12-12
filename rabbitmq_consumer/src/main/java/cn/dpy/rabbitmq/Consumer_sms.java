package cn.dpy.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/12/12 14:54
 * @Version: 1.0
 */
public class Consumer_sms {

    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform";

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
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
            // ���н������Ͷ��а�
            /**
             * ������String queue, String exchange, String routingKey, Map<String, Object> arguments
             * ������ϸ��
             *  1��queue����������
             *  2��exchange������������
             *  3��routingKey��·��key�������ǽ���������·��key��ֵ����Ϣת����ָ���Ķ����У��ڷ�������ģʽ��Э��Ϊ���ַ���
             *  4��props����Ϣ������
             *  5��body����Ϣ����
             */
            // ��Ϣ����
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_FANOUT_INFORM, "");

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
                    String message = new String(body, "GBK");
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
            channel.basicConsume(QUEUE_INFORM_SMS, true, defaultConsumer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // �����߲��ùر����ӣ���Ϊ��Ҫһֱ����
//            connection.close();
        }


    }

}
