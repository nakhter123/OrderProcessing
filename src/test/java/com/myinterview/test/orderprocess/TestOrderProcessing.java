package com.myinterview.test.orderprocess;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import com.myinterview.orderprocess.events.OrderEvent;
import com.myinterview.orderprocess.events.OrderSideE;
import com.myinterview.orderprocess.matching.TradeMatchingEngine;
import com.myinterview.orderprocess.stream.order.EventFactory;
import com.myinterview.orderprocess.stream.order.StreamOrder;
import com.myinterview.orderprocess.stream.order.TradeOrders;

class TestOrderProcessing {

	@Before
	void setUp() throws Exception {
	}

	@Test
	void testValidateSymbolsAndQueue() {
		EventFactory  factory = new EventFactory("TestSymbols.csv");
		factory.init();
		
		assertTrue(factory.getActiveSymbolSet().contains("AMZN"));
		assertTrue(factory.getActiveSymbolSet().contains("AAPL"));
		assertFalse(factory.getActiveSymbolSet().contains("TSL"));
		assertTrue(factory.getEventQueue().size() == 0);
	}
	
	@Test
	void testValidOrders() {
		List<OrderEvent> validList = new ArrayList<>();
		List<OrderEvent> invalidList = new ArrayList<>();

		EventFactory factory = new EventFactory("TestSymbols.csv");;
		factory.loadSymbols();
		
		StreamOrder stream = new StreamOrder("TestOrders.csv");
		stream.setFactory(factory);
		stream.init();
		assertTrue(factory.getEventQueue().size() == 9);

		while(!factory.getEventQueue().isEmpty()) {
			try {
				OrderEvent event = factory.getEventQueue().take();
				if (factory.isEventValid(event))
					validList.add(event);
				else
					invalidList.add(event);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		assertTrue(validList.size() == 9);
		assertTrue(invalidList.size() == 0);
		assertTrue(factory.getEventQueue().size() == 0);
	}
	
	@Test
	void testMatchingEngine() {
		TradeMatchingEngine matchingEngine = new TradeMatchingEngine(1);
		matchingEngine.init();

		EventFactory factory = new EventFactory("TestSymbols.csv");
		factory.setTradeMatchingEngine(matchingEngine);
		factory.init();

		StreamOrder stream = new StreamOrder("TestOrders.csv");
		stream.setFactory(factory);
		stream.init();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, TradeOrders> orderMap = matchingEngine.getOrderMap();
		
		assertTrue(orderMap.get("AAPL").limitOrderMap.get(OrderSideE.BUY).size() == 0);
		assertTrue(orderMap.get("AAPL").limitOrderMap.get(OrderSideE.SELL).size() == 0);

		assertTrue(orderMap.get("TSLA").limitOrderMap.get(OrderSideE.BUY).size() == 0);
		assertTrue(orderMap.get("TSLA").limitOrderMap.get(OrderSideE.SELL).size() == 0);

		assertTrue(orderMap.get("AMZN").limitOrderMap.get(OrderSideE.BUY).size() == 0);
		assertTrue(orderMap.get("AMZN").limitOrderMap.get(OrderSideE.SELL).size() == 0);

		assertTrue(orderMap.get("AAPL").marketOrderMap.get(OrderSideE.BUY).size() == 0);
		assertTrue(orderMap.get("AAPL").marketOrderMap.get(OrderSideE.SELL).size() == 1);

		assertTrue(orderMap.get("TSLA").marketOrderMap.get(OrderSideE.BUY).size() == 0);
		assertTrue(orderMap.get("TSLA").marketOrderMap.get(OrderSideE.SELL).size() == 0);

		assertTrue(orderMap.get("AMZN").marketOrderMap.get(OrderSideE.BUY).size() == 0);
		assertTrue(orderMap.get("AMZN").marketOrderMap.get(OrderSideE.SELL).size() == 0);
		
		/*
		orderMap.forEach((key,value) -> {
			System.out.println("\n\nSymbol: "+key);

			value.limitOrderMap.forEach((orderSide,orderList) -> {
				if (orderList.size() > 0) {
					System.out.println(orderSide+" Umatched Limit Order:");
					orderList.forEach(order -> {
						System.out.println(order.toString());
					});
				}
			});

			value.marketOrderMap.forEach((orderSide,orderList) -> {
				if (orderList.size() > 0) {
					System.out.println(orderSide+" Umatched Market Order:");
					orderList.forEach(order -> {
						System.out.println(order.toString());
					});
				}
			});
		});
		*/
	}

}
