package cityissue.tracker.murestrack.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender){
        this.emailSender = emailSender;
    }

    private void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sikomark.sl@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendWelcomeNewUser(String username, String password) {
        this.sendSimpleMessage("sikomark.sl@gmail.com", "Welcome to MuresTracker!", "This is your registration email:\nYour username is:\n" +username + "\nYour password is:\n" + password);
    }
}
