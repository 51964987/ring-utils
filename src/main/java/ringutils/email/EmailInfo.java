package ringutils.email;

import javax.mail.internet.InternetAddress;

public class EmailInfo {
	/**
	 * IP
	 */
	private String host;
	/**
	 * 端口
	 */
	private String port;
	/**
	 * 发送人地址
	 */
	private String from;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 发送的主题
	 */
	private String subject;
	/**
	 * 发送的内容
	 */
	private String conentText;
	/**
	 * 收件人地址
	 */
	private InternetAddress[] toAddrs;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getConentText() {
		return conentText;
	}
	public void setConentText(String conentText) {
		this.conentText = conentText;
	}
	public InternetAddress[] getToAddrs() {
		return toAddrs;
	}
	public void setToAddrs(InternetAddress[] toAddrs) {
		this.toAddrs = toAddrs;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
