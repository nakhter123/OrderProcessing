package com.myinterview.orderprocess.stream.order;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

import com.myinterview.orderprocess.events.LimitOrder;
import com.myinterview.orderprocess.events.MarketOrder;
import com.myinterview.orderprocess.events.OrderEvent;
import com.myinterview.orderprocess.events.OrderSideE;
import com.myinterview.orderprocess.events.OrderTypeE;

public class StreamOrder {
	private static final String FIELD_DELIMITER = ",";
	private String orderInputFileName = "orders.csv"; 
	private BufferedReader buffReader;
	private EventFactory factory;
	
	public StreamOrder() {
	}
	
	public StreamOrder(String orderInputFileName) {
		this.orderInputFileName = orderInputFileName;
	}
	
	public void init() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
	    	File file = new File(classLoader.getResource(orderInputFileName).getFile());
	    	
			buffReader = new BufferedReader(new FileReader(file));
		    buffReader.readLine();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
		listen();
	}
	
	public void listen() {
		String line = null;
		
		try {
			while ((line = buffReader.readLine()) != null) {
				OrderEvent event = null;
				
			    String[] values = line.split(FIELD_DELIMITER);
			    double price = StringUtils.isEmpty(values[3]) ? 0.0 : new Double(values[3]);
			    OrderSideE orderSide = OrderSideE.BUY.name().equals(values[1].toUpperCase()) ? OrderSideE.BUY:OrderSideE.SELL;
			    
			    if (OrderTypeE.LIMIT.name().equalsIgnoreCase(values[2])) {
			    	event = new LimitOrder(price, values[0],orderSide, values[4]);
			    } else {
			    	event = new MarketOrder(price, values[0],orderSide,values[4]);
			    }
			    
			    factory.getEventQueue().add(event);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				buffReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setFactory(EventFactory factory) {
		this.factory = factory;
	}
	
}
