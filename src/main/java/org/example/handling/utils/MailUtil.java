package org.example.handling.utils;

import org.example.data.accounts.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class MailUtil {

    private final JavaMailSender sender;

    private final String confirmationLink;

    private BlockingDeque<SimpleMailMessage> messageQueue;

    private final ScheduledExecutorService scheduler;

    private static final String confirmationSubject = "Confirmation of registration";
    private static final String confirmationMail = """
            Dear %s,
            You had been registration process in our service Spoad.
            Please, confirm it with redirect to follow link below:
            %s%s
            """;

    public MailUtil(@Autowired JavaMailSender sender,
                    @Value("${mail.confirmationLink}") String confirmationLink) {
        this.sender = sender;
        this.confirmationLink = confirmationLink;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                () -> {

                    if(messageQueue.isEmpty())
                        return;

                    var arrMessage = new SimpleMailMessage[messageQueue.size()];

                    for (var i = 0; i < arrMessage.length; i++)
                        arrMessage[i] = messageQueue.poll();

                    sender.send(arrMessage);
                },
                0, 30, TimeUnit.SECONDS);
        messageQueue = new LinkedBlockingDeque<>();
    }

    public void send(String subject, String body, String...to) {

        var message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        sender.send(message);
    }

    public void addToMessageQueue(Account recipient) throws InterruptedException {

        if(recipient.getCode() == null)
            throw new IllegalStateException("Confirmation code can't be null");

        var message = new SimpleMailMessage();
        message.setTo(recipient.getEmail());
        message.setSubject(confirmationSubject);
        message.setText(String.format(confirmationMail, recipient.getEmail(), confirmationLink, recipient.getCode()));

        messageQueue.put(message);
    }
}
