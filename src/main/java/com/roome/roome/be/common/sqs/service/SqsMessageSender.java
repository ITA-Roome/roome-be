package com.roome.roome.be.common.sqs.service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqsMessageSender {

    private final AmazonSQSAsync amazonSQSAsync;
    private final ObjectMapper objectMapper;

    public void publish(String queueUrl, Object payload) {
        try {
            String body = objectMapper.writeValueAsString(payload);

            amazonSQSAsync.sendMessage(
                    new SendMessageRequest(queueUrl, body)
            );
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SEND_VERIFICATION_CODE_EMAIL_INTERNAL_SERVER_ERROR);
        }
    }
}
