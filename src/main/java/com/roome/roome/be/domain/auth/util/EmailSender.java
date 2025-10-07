package com.roome.roome.be.domain.auth.util;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.sender-address}")
    private String senderAddress;

    // 이메일 인증 코드 발송
    public void send(String recipientEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderAddress);
            helper.setTo(recipientEmail);
            helper.setSubject("[Roome] 이메일 인증 코드입니다.");
            helper.setText(buildHtmlContent(verificationCode), true);

            mailSender.send(message);

            log.info("이메일 전송 완료 → {}", recipientEmail);
        } catch (Exception e) {
            log.error("이메일 전송 실패 → {}", recipientEmail, e);
            throw new GeneralException(ErrorStatus.SEND_VERIFICATION_CODE_EMAIL_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * HTML 템플릿 구성
     */
    private String buildHtmlContent(String code) {
        Context context = new Context();
        context.setVariable("verificationCode", code);
        return templateEngine.process("mail/verificationEmail", context);
    }
}
