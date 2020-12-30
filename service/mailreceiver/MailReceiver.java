
package io.collapp.service.mailreceiver;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.function.Consumer;

public interface MailReceiver {

    void receive(Consumer<MimeMessage[]> consumer) throws MessagingException;
}
