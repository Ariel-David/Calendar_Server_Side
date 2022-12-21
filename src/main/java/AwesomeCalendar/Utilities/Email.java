package AwesomeCalendar.Utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class Email {
    @Autowired
    public JavaMailSender mailSender;
    private String from;
    private String to;
    private String subject;
    private String content;

    public static class Builder {

        private String from = "startgooglproject@gmail.com";
        private String to;
        private String subject;
        private String content;

        public Builder(){}

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Email build() {
            return new Email(this);
        }
    }

    private Email(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.subject = builder.subject;
        this.content = builder.content;
    }

    public SimpleMailMessage convertIntoMessage(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        return message;
    }
}
