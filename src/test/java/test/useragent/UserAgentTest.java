package test.useragent;

import org.junit.Test;

import eu.bitwalker.useragentutils.UserAgent;

public class UserAgentTest {
	
	@Test
	public void parse(){
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0";
		UserAgent ua = UserAgent.parseUserAgentString(userAgent);
	    System.out.println(ua.getOperatingSystem());
	    if(ua.getOperatingSystem()!=null){
	    	System.out.println(ua.getOperatingSystem().getName());
	    	System.out.println(ua.getOperatingSystem().getGroup().getName());
	    }
	    //客户浏览器信息
	    if(ua.getBrowser()!=null){
	    	System.out.println(ua.getBrowser().getGroup().getName());
	    }
	    if (ua.getBrowserVersion() != null) {
	    	System.out.println(ua.getBrowserVersion().getVersion());
	    }
	}
}
