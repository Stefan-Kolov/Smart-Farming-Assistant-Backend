package com.smartfarmingassistant.sfa.service.domain.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

@Slf4j
@Service
public class EmailService {

    @Value("${google.client.id}") private String clientId;
    @Value("${google.client.secret}") private String clientSecret;
    @Value("${google.refresh.token}") private String refreshToken;
    @Value("${google.sender.email}") private String fromAddress;

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
        try {
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

            TokenResponse tokenResponse = new TokenResponse().setRefreshToken(refreshToken);
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(transport)
                    .setJsonFactory(jsonFactory)
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setFromTokenResponse(tokenResponse);

            Gmail service = new Gmail.Builder(transport, jsonFactory, credential)
                    .setApplicationName("SmartFarmingAssistant")
                    .build();

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(fromAddress));
            email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
            email.setSubject("⚠️ Smart Farming Alert: " + riskType + " on " + farmName);

            String body = buildEmailBody(userName, farmName, cropName, riskType, recommendation);
            email.setText(body);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

            Message message = new Message();
            message.setRaw(encodedEmail);

            service.users().messages().send("me", message).execute();
            log.info("[GMAIL API] Alert sent to {} for farm '{}'", toEmail, farmName);

        } catch (Exception e) {
            log.error("[GMAIL API] Failed to send email: {}", e.getMessage());
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
        sb.append("Stay safe,\nSmart Farming Assistant\n");
        return sb.toString();
    }
}