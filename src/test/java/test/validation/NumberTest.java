package test.validation;

import java.util.regex.*;

import org.junit.Test;

public class NumberTest {
	
	@Test
	public void repnumTest(){
//		String reg = "^JOB.*?(\\d).*?\\1.*?$";
//		System.out.println(Pattern.matches(reg, "JOB12345678"));
//		System.out.println(Pattern.matches(reg, "JOB123445678"));
		String reg = "^[/|\\\\]?[^\\*\\|\\<\\>\\?\"]+[/|\\\\]?$";  
		System.out.println(Pattern.matches(reg, "\\home/misJ/tmp/May/data/")); 
	}
}
