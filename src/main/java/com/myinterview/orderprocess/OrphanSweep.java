package com.myinterview.orderprocess;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.myinterview.orderprocess.matching.TradeMatchingEngine;
import com.myinterview.orderprocess.stream.order.EventFactory;
import com.myinterview.orderprocess.stream.order.TradeOrders;

public class OrphanSweep {
	private EventFactory eventFactory;
	private TradeMatchingEngine matchingEngine;
	private BufferedWriter rejectedWriter;
	private BufferedWriter tradeExecutedWriter;
	private BufferedWriter orphanTradesWriter;
	private int orphanSweepDelayTime;
	private String dirPath = "/tmp/";
	
	public OrphanSweep() {
	}
	
	public OrphanSweep(int orphanSweepDelayTime) {
		this.orphanSweepDelayTime = orphanSweepDelayTime;
		createOutputFiles();
	}
	
	public void init() {
		process();
	}

	private void createOutputFiles() {
		File rejectedFile = new File(dirPath+"RejectedOrders.txt");
		File tradeExecutedFile = new File(dirPath+"TradeExecuted.txt");
		File orphanTradesFile = new File(dirPath+"OrphanTrades.txt");

		try {
			rejectedWriter = new BufferedWriter(new FileWriter(rejectedFile));
			tradeExecutedWriter = new BufferedWriter(new FileWriter(tradeExecutedFile));
			orphanTradesWriter = new BufferedWriter(new FileWriter(orphanTradesFile));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void process() {
		try {
			Thread.sleep(orphanSweepDelayTime);
			eventFactory.getInvalidEventList().forEach(invalidOrder -> {
				try {
					rejectedWriter.write(invalidOrder.toString()+"\n");
					//System.out.print(invalidOrder.toString()+"\n");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			rejectedWriter.close();
			matchingEngine.getOrderMap().values().forEach(tradeOrder -> {
				tradeOrder.getOrderExecutedList().forEach(executedOrder -> {
					try {
						if (executedOrder != null) {
							tradeExecutedWriter.write(executedOrder.toString()+"\n");
						    //System.out.print(executedOrder.toString()+"\n");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			});

			Map<String, TradeOrders> orderMap = matchingEngine.getOrderMap();
			orderMap.forEach((key,value) -> {
				value.limitOrderMap.forEach((orderSide,orderList) -> {
					if (orderList.size() > 0) {
						orderList.forEach(order -> {
							try {
								orphanTradesWriter.write(order.toString()+"\n");
								//System.out.print(order.toString()+"\n");

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
					}
				});

				value.marketOrderMap.forEach((orderSide,orderList) -> {
					if (orderList.size() > 0) {
						orderList.forEach(order -> {
							try {
								orphanTradesWriter.write(order.toString()+"\n");
								//System.out.print(order.toString()+"\n");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
					}
				});
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				rejectedWriter.close();
				tradeExecutedWriter.close();
				orphanTradesWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setFactory(EventFactory eventFactory) {
		this.eventFactory = eventFactory;
	}

	public void setTradeMatchingEngine(TradeMatchingEngine matchingEngine) {
		this.matchingEngine = matchingEngine;
	}
}
