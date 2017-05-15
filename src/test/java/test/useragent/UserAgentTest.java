package test.useragent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

public class UserAgentTest {
	
	@Test
	public void parse(){
		//String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0";
		String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36";
//		userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) ";
//		userAgent = "Mozilla/5.0 (Linux; Android 4.4.2; HUAWEI P6 S-U06 Build/HuaweiP6S-U06) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36 AliApp(TB/5.1.3) WindVane/5.6.6 TBANDROID/227200@taobao_android_5.1.3 720X1184  QQMusic 4050102(android 4.3)  ";
//		userAgent = "Mozilla/5.0 (Linux; U; Android 6.0.1; zh-cn; MI 4LTE Build/MMB29M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.146 Mobile Safari/537.36 XiaoMi/MiuiBrowser/8.6.5";
//		UserAgent ua = UserAgent.parseUserAgentString(userAgent);
//		OperatingSystem os = ua.getOperatingSystem();
//	    if(os != null){
//	    	System.out.println(os.name());//访问设备系统
//	    	System.out.println(os.getDeviceType().name());//访问设备类型
//	    	System.out.println(os.getGroup().getName());
//	    	System.out.println(os.getManufacturer().name());//访问设备制造厂商
//	    }
//	    //客户浏览器信息
//	    if(ua.getBrowser()!=null){
//	    	System.out.println(ua.getBrowser().getGroup().getName());
//	    	System.out.println(ua.getBrowser().getBrowserType().getName());
//	    }
//	    if (ua.getBrowserVersion() != null) {
//	    	System.out.println(ua.getBrowserVersion().getVersion());
//	    }
		
		String ss = parse(userAgent);
		System.out.println(ss);
	}
	
	public static String getFromRegrex(String str,String pattern,int index){  
	    Pattern p = Pattern.compile(pattern);  
	    Matcher matcher = p.matcher(str);  
	    while(matcher.find())  
	        str = matcher.group(index);  
	    return str;  
	}  
	public static String parse(String userAgent){  
	    String pattern1 = "(.*) AppleWebKit";  
	    String pattern2 = ".* ";  
	    String str = getFromRegrex(userAgent,pattern1,1);  
	    String params[] = str.split(";");  
	    return getFromRegrex(params[params.length-1],pattern2,0).trim();  
	}
}
