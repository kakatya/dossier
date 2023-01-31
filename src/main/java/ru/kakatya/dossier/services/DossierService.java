package ru.kakatya.dossier.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.kakatya.dossier.dtos.EmailMessageDto;

@Service
public class DossierService {
    @Autowired
    public JavaMailSender emailSender;

    @KafkaListener(topics = "FINISH_REGISTRATION", groupId = "conveyorGroup")
    public void sendFinishRegistrationEmail(String emailMessageDto) {
        EmailMessageDto messageDto = createEmailDto(emailMessageDto);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(messageDto.getAddress());
        simpleMailMessage.setSubject(messageDto.getTheme().name());
        simpleMailMessage.setText(String.format("Dear client,  your application #%S. For the final registration we need your full data.", messageDto.getApplicationId()));
        emailSender.send(simpleMailMessage);
    }

    @KafkaListener(topics = "CREATE_DOCUMENTS", groupId = "conveyorGroup")
    public void sendCreateDocumentEmail(String email) {
        EmailMessageDto emailMessageDto = createEmailDto(email);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailMessageDto.getAddress());
        simpleMailMessage.setSubject(emailMessageDto.getTheme().name());
        simpleMailMessage.setText(String.format("Dear client,  your application #%S passed all checking. Now you should send creating document request.", emailMessageDto.getApplicationId()));
        emailSender.send(simpleMailMessage);
    }

    @KafkaListener(topics = "SEND_DOCUMENTS", groupId = "conveyorGroup")
    public void sendLoanDocuments(String email) {
        EmailMessageDto emailMessageDto = createEmailDto(email);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailMessageDto.getAddress());
        simpleMailMessage.setSubject(emailMessageDto.getTheme().name());
        simpleMailMessage.setText(String.format("Dear client,  your application #%S. Here are your documents.", emailMessageDto.getApplicationId()));
        emailSender.send(simpleMailMessage);
    }

    @KafkaListener(topics = "SEND_SES", groupId = "conveyorGroup")
    public void sendSesCode(String email) {
        EmailMessageDto emailMessageDto = createEmailDto(email);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailMessageDto.getAddress());
        simpleMailMessage.setSubject(emailMessageDto.getTheme().name());
        simpleMailMessage.setText(String.format("Dear client,  your application #%S. Here are your ses-code:.", emailMessageDto.getApplicationId()));
        emailSender.send(simpleMailMessage);
    }

    @KafkaListener(topics = "CREDIT_ISSUED", groupId = "conveyorGroup")
    public void sendIssuedCreditEmail(String email) {
        EmailMessageDto emailMessageDto = createEmailDto(email);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailMessageDto.getAddress());
        simpleMailMessage.setSubject(emailMessageDto.getTheme().name());
        simpleMailMessage.setText(String.format("Dear client,  your application #%S. You got a loan, thank you.", emailMessageDto.getApplicationId()));
        emailSender.send(simpleMailMessage);
    }

    @KafkaListener(topics = "APPLICATION_DENIED", groupId = "conveyorGroup")
    public void sendApplicationDeniedEmail(String email) {
        EmailMessageDto emailMessageDto = createEmailDto(email);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailMessageDto.getAddress());
        simpleMailMessage.setSubject(emailMessageDto.getTheme().name());
        simpleMailMessage.setText(String.format("Dear client,  your application #%S has been rejected.", emailMessageDto.getApplicationId()));
        emailSender.send(simpleMailMessage);
    }

    private EmailMessageDto createEmailDto(String email) {
        ObjectMapper objectMapper = new ObjectMapper();
        EmailMessageDto emailMessageDto;
        try {
            emailMessageDto = objectMapper.readValue(email, EmailMessageDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return emailMessageDto;
    }
}
