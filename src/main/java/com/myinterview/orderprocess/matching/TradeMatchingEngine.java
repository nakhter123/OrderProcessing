package com.myinterview.orderprocess.matching;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.myinterview.orderprocess.events.LimitOrder;
import com.myinterview.orderprocess.events.MarketOrder;
import com.myinterview.orderprocess.events.OrderEvent;
import com.myinterview.orderprocess.stream.order.TradeOrders;


public class TradeMatchingEngine {
	private static final Log logger = LogFactory.getLog(TradeMatchingEngine.class);

	private Map<String,TradeOrders> orderMap = new ConcurrentHashMap<>();
	private BlockingQueue<OrderEvent> orderQueue = new LinkedBlockingQueue<>();
	private ExecutorService executor;
	private int maximumPoolSize = 2;

	public TradeMatchingEngine() {

	}

	public TradeMatchingEngine(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public void init() {
		executor = Executors.newFixedThreadPool(maximumPoolSize);
		process();
	}

	private void process() {

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						OrderEvent order = orderQueue.take();
						executor.execute(new ExecuteOrder(order));
					} catch (Exception ex) {
						logger.error(ex);
					}
				}
			}
		}).start();		
	}
	public Queue<OrderEvent> getOrderQueue() {
		return orderQueue;
	}

	public void processOrder(OrderEvent order) {
		orderQueue.add(order);
	}
	
	public Map<String,TradeOrders> getOrderMap() {
		return orderMap;
	}
	
	public class ExecuteOrder implements Runnable {
		OrderEvent order;
		
		public ExecuteOrder(OrderEvent order) {
			this.order = order;
		}
		
		@Override
		public void run() {
			TradeOrders tradeOrder = null;

			// TODO Auto-generated method stub
			if (orderMap.get(order.getSymbol()) == null) {
				tradeOrder = new TradeOrders();
				orderMap.put(order.getSymbol(), tradeOrder);
			} else {
				tradeOrder = orderMap.get(order.getSymbol());
			}
			
			processOrder(tradeOrder);
		}
		
		private void processOrder(TradeOrders tradeProcess) {
			switch(order.getOrderSide()) {
			case BUY:
				if (order instanceof LimitOrder)
					tradeProcess.buy((LimitOrder)order);
				else
					tradeProcess.buy((MarketOrder)order);
				break;
			case SELL:
				if (order instanceof LimitOrder)
					tradeProcess.sell((LimitOrder)order);
				else
					tradeProcess.sell((MarketOrder)order);
				break;
			default:
				logger.error("Got Unknown event");
			}
		}
	}
}