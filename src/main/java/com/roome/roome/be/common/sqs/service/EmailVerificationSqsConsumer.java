package com.roome.roome.be.common.sqs.service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roome.roome.be.common.sqs.dto.request.EmailVerificationMessageRequest;
import com.roome.roome.be.domain.auth.util.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationSqsConsumer {

    private final AmazonSQSAsync amazonSQSAsync;
    private final ObjectMapper objectMapper;
    private final EmailSender emailSender;
    private final SqsMessageDeduplicator deduplicator;

    @Value("${app.sqs.email-verification-queue-url}")
    private String queueUrl;

    @Value("${app.sqs.poll.max-messages}")
    private int maxMessages;

    @Value("${app.sqs.poll.wait-time-seconds}")
    private int waitTimeSeconds;

    @Scheduled(fixedDelay = 3000)
    public void poll() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                .withMaxNumberOfMessages(maxMessages)
                .withWaitTimeSeconds(waitTimeSeconds);

        List<Message> messages = amazonSQSAsync.receiveMessage(receiveMessageRequest).getMessages();
        for(Message message : messages) {
            handleMessage(message);
        }
    }

    private void handleMessage(Message message) {
        String messageId = message.getMessageId();
        if (deduplicator.alreadyProcessed(messageId)) {
            amazonSQSAsync.deleteMessage(queueUrl, message.getReceiptHandle());
            return;
        }

        try{
            EmailVerificationMessageRequest payload =
                    objectMapper.readValue(
                            message.getBody(),
                            EmailVerificationMessageRequest.class
                    );

            emailSender.send(payload.getEmail(), payload.getVerificationCode());
            deduplicator.markProcessed(messageId);
            amazonSQSAsync.deleteMessage(new DeleteMessageRequest(queueUrl, message.getReceiptHandle()));
        }
        catch(Exception e) {
            log.error("SQS message processing failed", e);
        }
    }
}
