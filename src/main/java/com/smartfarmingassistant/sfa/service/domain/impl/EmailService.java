package com.smartfarmingassistant.sfa.service.domain.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    /**
     * Sends a risk alert email to the farm owner.
     *
     * @param toEmail         recipient email address
     * @param userName        first name of the user
     * @param farmName        name of the farm
     * @param cropName        name of the affected crop (may be null)
     * @param riskType        short risk label e.g. "Drought Risk", "Disease Risk"
     * @param recommendation  the full AI recommendation text
     */
    public void sendRiskAlert(String toEmail, String userName, String farmName,
                              String cropName, String riskType, String recommendation) {
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("[EMAIL] MAIL_USERNAME not configured — skipping risk alert to {}", toEmail);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("⚠️ Smart Farming Alert: " + riskType + " Detected on " + farmName);
            message.setText(buildEmailBody(userName, farmName, cropName, riskType, recommendation));
            mailSender.send(message);
            log.info("[EMAIL] Risk alert sent to {} for farm '{}' — {}", toEmail, farmName, riskType);
        } catch (MailException e) {
            log.error("[EMAIL] Failed to send risk alert to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildEmailBody(String userName, String farmName, String cropName,
                                  String riskType, String recommendation) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(userName).append(",\n\n");
        sb.append("Our Smart Farming Assistant has detected a potential risk on your farm:\n\n");
        sb.append("  Farm:  ").append(farmName).append("\n");
        if (cropName != null && !cropName.isBlank()) {
            sb.append("  Crop:  ").append(cropName).append("\n");
        }
        sb.append("  Risk:  ").append(riskType).append("\n\n");
        sb.append("--- AI Recommendation ---\n\n");
        sb.append(recommendation).append("\n\n");
        sb.append("Please review your crops and take appropriate action as soon as possible.\n\n");
        sb.append("Stay safe and farm well,\n");
        sb.append("Smart Farming Assistant\n");
        return sb.toString();
    }
}
