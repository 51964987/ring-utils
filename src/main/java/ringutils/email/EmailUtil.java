package ringutils.email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtil {
	
	private static Logger log = LoggerFactory.getLogger(EmailUtil.class);
	
	/**
	 * 发送
	 * @param info 
	 * @author ring
	 * @date 2017年3月16日 上午11:01:20
	 * @version V1.0
	 */
	public static void send(final EmailInfo info){
		try {
			//设置邮件服务器属性
			Properties properties = new Properties();
			properties.setProperty("mail.smtp", "true");
			properties.setProperty("mail.smtp.starttls.enable", "true");
			properties.setProperty("mail.smtp.host",info.getHost());
			properties.setProperty("mail.smtp.port",info.getPort());
			
			//构建session
			Session session = Session.getInstance(properties, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication(){
					return new PasswordAuthentication(info.getUsername(), info.getPassword());
				}
			});
			
			session.setDebug(true);//DEBU模式
			
			//消息对象
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(info.getFrom()));
			message.setRecipients(Message.RecipientType.TO, info.getToAddrs());
			message.setSubject(info.getSubject());
			
			//邮件文本内容
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(info.getConentText());
			
			//邮件附件
			Multipart multipart = new MimeMultipart();
			multipart.setParent(messageBodyPart);
			
			//保存邮件
			message.saveChanges();
			
			//发送邮件
			Transport.send(message);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage(),e);
			throw new RuntimeException("发送邮件出错",e);
		}
		
	}
}

