package ru.kakatya.dossier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.kakatya.dossier.dtos.EmailMessageDto;

@Service
public class DossierService {
    @Autowired
    private JavaMailSender emailSender;

    @KafkaListener(topics = "${topics.finish-registr}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendFinishRegistrationEmail(EmailMessageDto emailMessageDto) {
        String ms = "Dear client,  your application #%S. For the final registration we need your full data.";
        sendMessage(emailMessageDto, ms);
    }

    @KafkaListener(topics = "${topics.create-doc}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendCreateDocumentEmail(EmailMessageDto emailMessageDto) {
        String ms = "Dear client,  your application #%S passed all checking. Now you should send creating document request.";
        sendMessage(emailMessageDto, ms);
    }

    @KafkaListener(topics = "${topics.send-doc}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendLoanDocuments(EmailMessageDto emailMessageDto) {
        String ms = "Dear client,  your application #%S. Here are your documents.";
        sendMessage(emailMessageDto, ms);
    }

    @KafkaListener(topics = "${topics.send-ses}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendSesCode(EmailMessageDto emailMessageDto) {
        String ms = "Dear client,  your application #%S. Here are your ses-code:.";
        sendMessage(emailMessageDto, ms);
    }

    @KafkaListener(topics = "${topics.credit-issd}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendIssuedCreditEmail(EmailMessageDto emailMessageDto) {
        String ms = "Dear client,  your application #%S. You got a loan, thank you.";
        sendMessage(emailMessageDto, ms);
    }

    @KafkaListener(topics = "${topics.appl-denied}", groupId = "${spring.kafka.consumer.group-id}")
    public void sendApplicationDeniedEmail(EmailMessageDto emailMessageDto) {
        String ms = "Dear client,  your application #%S has been rejected.";
        sendMessage(emailMessageDto, ms);
    }

    private void sendMessage(EmailMessageDto emailMessageDto, String emailText) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailMessageDto.getAddress());
        simpleMailMessage.setSubject(emailMessageDto.getTheme().name());
        simpleMailMessage.setText(String.format(emailText, emailMessageDto.getApplicationId()));
        emailSender.send(simpleMailMessage);
    }

}
