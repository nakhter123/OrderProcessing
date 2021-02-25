package com.myinterview.orderprocess.stream.order;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.myinterview.orderprocess.events.LimitOrder;
import com.myinterview.orderprocess.events.OrderEvent;
import com.myinterview.orderprocess.matching.TradeMatchingEngine;

public class EventFactory {
	private final String FIELD_DELIMITER = ",";
	private String symbolFile = "symbols.csv";
	private TradeMatchingEngine tradeMatchingEngine;
	private Map<String,OrderTimeGap> lastOrderTimeMap = new HashMap<>();
	private Set<String> activeSymbolSet = new HashSet<>();
	private LinkedBlockingQueue<OrderEvent> eventQueue = new LinkedBlockingQueue<OrderEvent>();
	private List<OrderEvent> invalidEventList = new ArrayList<OrderEvent>();
	
	public EventFactory() {
		
	}
	
	public EventFactory(String symbolFile) {
		this.symbolFile = symbolFile;
	}
	
	public void init() {
		loadSymbols();
		startTradeListener();
	}
	
	public void loadSymbols() {
		BufferedReader buffReader = null;
	    try {
	    	ClassLoader classLoader = getClass().getClassLoader();
	    	File file = new File(classLoader.getResource(symbolFile).getFile());
	    	
			buffReader = new BufferedReader(new FileReader(file));
			String line = buffReader.readLine();
	    	
	    	while ((line = buffReader.readLine()) != null) {
	    		String[] values = line.split(FIELD_DELIMITER);
			    String symbol = values[0];
			    boolean isHalted = Boolean.valueOf(values[1]);
			    
			    if (!isHalted)
			    	activeSymbolSet.add(symbol);
			}
		} catch (IOException e) {
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

	private void startTradeListener() {
		new Thread(() ->  {
			OrderEvent event = null;
			
			while(true) {
				try {
				event = eventQueue.take();
				if (isEventValid(event))
					tradeMatchingEngine.getOrderQueue().add(event);
				else
					invalidEventList.add(event);
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}
	
	public boolean isEventValid(OrderEvent event) {
		if (!activeSymbolSet.contains(event.getSymbol()))
			return false;
		
		OrderTimeGap lastOrderTime = lastOrderTimeMap.get(event.getSymbol());
		if (lastOrderTime == null) {
			lastOrderTimeMap.put(event.getSymbol(), new OrderTimeGap(new Double(event.getTimeStamp())));
		} else {
			if (!lastOrderTime.isTimeGapValid(new Double(event.getTimeStamp())))
				return false;
		}
		
		if (event instanceof LimitOrder)
			return event.getPrice() > 0.0;
			
		return true;
	}
	
	public Date getDateFromEpoc(String epocDate) {
		long num = new Double(epocDate).longValue();
		
		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(num, 0, ZoneOffset.UTC);
		return Date.from(dateTime.toInstant(ZoneOffset.ofHours(0)));
	}
	
	public List<OrderEvent> getInvalidEventList() {
		return invalidEventList;
	}

	public LinkedBlockingQueue<OrderEvent> getEventQueue() {
		return eventQueue;
	}
	
	public void setTradeMatchingEngine(TradeMatchingEngine tradeMatchingEngine) {
		this.tradeMatchingEngine = tradeMatchingEngine;
	}

	public void setActiveSymbolSet(Set<String> activeSymbolSet) {
		this.activeSymbolSet = activeSymbolSet;
	}
	
	public Set<String> getActiveSymbolSet() {
		return activeSymbolSet;
	}
	
	public class OrderTimeGap {
		public int count;
		public double epocTimeStamp;
		
		public OrderTimeGap(double epocTimeStamp) {
			count = 1;
			this.epocTimeStamp = epocTimeStamp;
		}
		
		public boolean isTimeGapValid(double newEpocTimeStamp) {
			double gap = Math.abs(newEpocTimeStamp - epocTimeStamp);
			if (gap < 1) {
				count++;
				epocTimeStamp = newEpocTimeStamp;
			}else {
				count = 1;
				epocTimeStamp = newEpocTimeStamp;
			}
			
			return count <= 3;
		}
		
	}
}
