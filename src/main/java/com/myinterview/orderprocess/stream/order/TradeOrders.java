package com.myinterview.orderprocess.stream.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.myinterview.orderprocess.events.LimitOrder;
import com.myinterview.orderprocess.events.MarketOrder;
import com.myinterview.orderprocess.events.OrderEvent;
import com.myinterview.orderprocess.events.OrderSideE;

public class TradeOrders {
	private static final Log logger = LogFactory.getLog(TradeOrders.class);

	public List<OrderEvent> orderExecutedList = new ArrayList<>();
	public Map<OrderSideE,List<LimitOrder>> limitOrderMap = new HashMap<>();
	public Map<OrderSideE,List<MarketOrder>> marketOrderMap = new HashMap<>();
	
	public TradeOrders() {
		limitOrderMap.put(OrderSideE.BUY, new ArrayList<LimitOrder>());
		limitOrderMap.put(OrderSideE.SELL, new ArrayList<LimitOrder>());
		marketOrderMap.put(OrderSideE.BUY, new ArrayList<MarketOrder>());
		marketOrderMap.put(OrderSideE.SELL, new ArrayList<MarketOrder>());
	}
	
	public void buy(MarketOrder order) {
		List<MarketOrder> sellList = marketOrderMap.get(OrderSideE.SELL);
		List<MarketOrder> buyList = marketOrderMap.get(OrderSideE.BUY);

		synchronized(sellList) {
			if (sellList.size() > 0) {
				sellList.remove(0);
				//logger.debug("MATCHED:"+order);
				orderExecutedList.add(order);
				return;
			}
		}

		synchronized(buyList) {
			buyList.add(order);
		}
	}
	
	public void sell(MarketOrder order) {
		List<MarketOrder> buyList = marketOrderMap.get(OrderSideE.BUY);
		List<MarketOrder> sellList = marketOrderMap.get(OrderSideE.SELL);

		synchronized(buyList) {
			if (buyList.size() > 0) {
				buyList.remove(0);
				//logger.debug("MATCHED:"+order);
				orderExecutedList.add(order);
				return;
			}
		}

		synchronized(sellList) {
			sellList.add(order);
		}
	}

	public void buy(LimitOrder order) {
		List<LimitOrder> sellList = limitOrderMap.get(OrderSideE.SELL);
		List<LimitOrder> buyList = limitOrderMap.get(OrderSideE.BUY);

		synchronized(sellList) {
			if ((sellList.size() > 0) && (sellList.get(0).getPrice() <= order.getPrice())) {
			  sellList.remove(0);
			  //logger.debug("MATCHED:"+order);
			  orderExecutedList.add(order);
			  return;
			}
		}
		
		synchronized(buyList) {
				buyList.add(order);
				Collections.sort(buyList,new Comparator<LimitOrder>() {
				    @Override
				    public int compare(LimitOrder a, LimitOrder b) {
				        return (int) (a.getPrice() - b.getPrice());
				    }
				});
		}
	}
	
	public void sell(LimitOrder order) {
		List<LimitOrder> buyList = limitOrderMap.get(OrderSideE.BUY);
		List<LimitOrder> sellList = limitOrderMap.get(OrderSideE.SELL);

		synchronized(buyList) {
			if ((buyList.size() > 0) && (buyList.get(buyList.size() - 1).getPrice() >= order.getPrice())) {
				buyList.remove(buyList.size() - 1);
				//logger.debug("MATCHED:"+order);
				orderExecutedList.add(order);
				return;
			}
		}

		synchronized(sellList) {
			sellList.add(order);
			Collections.sort(sellList,new Comparator<LimitOrder>() {
				@Override
				public int compare(LimitOrder a, LimitOrder b) {
					return (int) (a.getPrice() - b.getPrice());
				}
			});
		}			
	}

	public List<OrderEvent> getOrderExecutedList() {
		return orderExecutedList;
	}

}
