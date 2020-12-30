package io.collapp.service.mailreceiver;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;

import org.springframework.integration.mail.MailReceiver;
import org.springframework.util.Assert;


public class Pop3MailReceiver extends AbstractMailReceiver {

    public Pop3MailReceiver() {
        super();
        this.setProtocol("pop3");
    }

    public Pop3MailReceiver(String url) {
        super(url);
        if (url != null) {
            Assert.isTrue(url.startsWith("pop3"), "url must start with 'pop3'");
        }
        else {
            this.setProtocol("pop3");
        }
    }

    public Pop3MailReceiver(String host, String username, String password) {
        // -1 indicates default port
        this(host, -1, username, password);
    }

    public Pop3MailReceiver(String host, int port, String username, String password) {
        super(new URLName("pop3", host, port, "INBOX", username, password));
    }


    @Override
    protected Message[] searchForNewMessages() throws MessagingException {
        int messageCount = this.getFolder().getMessageCount();
        if (messageCount == 0) {
            return new Message[0];
        }
        return this.getFolder().getMessages();
    }

    /**
     * Deletes the given messages from this receiver's folder, and closes it to expunge deleted messages.
     *
     * @param messages the messages to delete
     * @throws MessagingException in case of JavaMail errors
     */
    @Override
    protected void deleteMessages(Message[] messages) throws MessagingException {
        super.deleteMessages(messages);
        // expunge deleted mails, and make sure we've retrieved them before closing the folder
        for (int i = 0; i < messages.length; i++) {
            new MimeMessage((MimeMessage) messages[i]);
        }
    }
}
