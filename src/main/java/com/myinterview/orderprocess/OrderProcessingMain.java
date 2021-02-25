package com.myinterview.orderprocess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Nadeem
 *
 */
@ImportResource("classpath:orderprocess.xml")
public class OrderProcessingMain {
	private static final Log logger = LogFactory.getLog(OrderProcessingMain.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try  {
			Class<?> aClass = Class.forName("com.myinterview.orderprocess.OrderProcessingMain");
			SpringApplication app = new SpringApplication(aClass);
			app.setWebApplicationType(WebApplicationType.NONE);
			app.setLogStartupInfo(true);
			app.run(args);
		}catch(Throwable ex) {
			logger.error(ex);
			Runtime.getRuntime().halt(-1);
		}
	}
}
