package pl.schibsted.spid.elasticmq.util;

import java.util.List;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SqsQueue {

    private final AmazonSQSClient client;

    private final String queueUrl;

    public void send(Message toSend) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, toSend.getBody());
        sendMessageRequest.setMessageAttributes(toSend.getMessageAttributes());
        client.sendMessage(sendMessageRequest);
    }

    public List<Message> read(int maxMessages) {
        ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl);
        request.setMaxNumberOfMessages(maxMessages);
        ReceiveMessageResult receiveMessage = client.receiveMessage(request);
        return receiveMessage.getMessages();
    }

    public void purge() {
        client.purgeQueue(new PurgeQueueRequest(queueUrl));
    }

}
