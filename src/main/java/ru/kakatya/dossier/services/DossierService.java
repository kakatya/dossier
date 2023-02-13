package ru.kakatya.dossier.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.kakatya.dossier.dtos.ApplicationDto;
import ru.kakatya.dossier.dtos.EmailMessageDto;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DossierService {
    private static final Logger LOGGER = LogManager.getLogger(DossierService.class);
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private DealServiceFeignClient dealServiceFeignClient;

    @KafkaListener(topics = "${topics.finish-registr}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendFinishRegistrationEmail(EmailMessageDto emailMessageDto) {
        LOGGER.info("send \"finish registration\" email.");
        String ms = "Dear client,  your application #%S. For the final registration we need your full data. Follow this link: http://127.0.0.1:8084/swagger-ui/#/application/finishRegistrationUsingPOST";
        sendMessage(emailMessageDto, ms);
    }

    @KafkaListener(topics = "${topics.create-doc}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendCreateDocumentEmail(EmailMessageDto emailMessageDto) {
        LOGGER.info("send \"create document request\" email.");
        String ms = "Dear client,  your application #%S passed all checking. Now you should send creating document request. Follow this link: http://127.0.0.1:8084/swagger-ui/#/document/requestDocumentsUsingPOST";
        sendMessage(emailMessageDto, ms);
    }

    @KafkaListener(topics = "${topics.send-doc}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendLoanDocuments(EmailMessageDto emailMessageDto) {
        try {
            LOGGER.info("send get application request to the deal service");
            ApplicationDto applicationDto = dealServiceFeignClient.getApplication(emailMessageDto.getApplicationId()).getBody();
            String ms = String.format("Dear client,  your application #%s. Here are your documents. Follow this link for request sign documents: http://127.0.0.1:8084/swagger-ui/#/document/requestSesCodeUsingPOST",
                    emailMessageDto.getApplicationId().toString());
            LOGGER.info("send \"send documents\" email.");
            if (applicationDto != null)
                createDocuments(applicationDto, emailMessageDto, ms);
        } catch (NullPointerException e) {
            LOGGER.error(e.getMessage());
        }

        dealServiceFeignClient.changeApplicationStatus(emailMessageDto.getApplicationId());
    }

    @KafkaListener(topics = "${topics.send-ses}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendSesCode(EmailMessageDto emailMessageDto) throws NullPointerException {
        try {
            if (dealServiceFeignClient.getApplication(emailMessageDto.getApplicationId()).hasBody()) {
                String sesCode = dealServiceFeignClient.getApplication(emailMessageDto.getApplicationId()).getBody().getSesCode();
                String ms = String.format("Dear client,  your application #%s. Here are your ses-code: %s. Follow this link for sign documents: http://127.0.0.1:8084/swagger-ui/#/document/sendSesCodeUsingPOST",
                        emailMessageDto.getApplicationId(), sesCode);
                LOGGER.info("send \"sescode\" email.");
                sendMessage(emailMessageDto, ms);
            } else throw new NullPointerException("application not found");
        } catch (NullPointerException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "${topics.credit-issd}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendIssuedCreditEmail(EmailMessageDto emailMessageDto) {
        LOGGER.info("send \"credit issued\" email.");
        String ms = "Dear client,  your application #%S. You got a loan, thank you.";
        sendMessage(emailMessageDto, ms);
    }

    @KafkaListener(topics = "${topics.appl-denied}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendApplicationDeniedEmail(EmailMessageDto emailMessageDto) {
        String ms = "Dear client,  your application #%S has been rejected.";
        LOGGER.info("send \"application denied\" email.");
        sendMessage(emailMessageDto, ms);
    }

    private void sendMessage(EmailMessageDto emailMessageDto, String emailText) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailMessageDto.getAddress());
        simpleMailMessage.setSubject(emailMessageDto.getTheme().name());
        simpleMailMessage.setText(String.format(emailText, emailMessageDto.getApplicationId()));
        LOGGER.info("send email");
        emailSender.send(simpleMailMessage);
    }

    private void createDocuments(ApplicationDto applicationDto, EmailMessageDto emailMessageDto, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String creditData = String.format("Application #%s from %s \n" +
                            "%s\n",
                    applicationDto.getId().toString(), applicationDto.getCreationDate().toString(), applicationDto.getCredit());
            Path creditFile = Files.createTempFile("creditData", ".txt");
            Path clientFile = Files.createTempFile("clientData", ".txt");
            Path paymentScheduleFile = Files.createTempFile("paymentScheduleData", ".txt");
            Files.write(creditFile, creditData.getBytes(StandardCharsets.UTF_8));
            Files.write(clientFile, applicationDto.getClient().toString().getBytes(StandardCharsets.UTF_8));
            Files.write(paymentScheduleFile, applicationDto.getCredit().getPaymentSchedule().toString().getBytes(StandardCharsets.UTF_8));
            helper.setTo(emailMessageDto.getAddress());
            helper.setText(text);
            helper.setSubject(emailMessageDto.getTheme().name());
            helper.addAttachment("clientData", clientFile.toFile());
            helper.addAttachment("creditData", creditFile.toFile());
            helper.addAttachment("paymentSchedule", paymentScheduleFile.toFile());
            LOGGER.info("send email with attachment");
            emailSender.send(message);
        } catch (IOException | MessagingException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
