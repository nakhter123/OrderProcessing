package com.myinterview.orderprocess.events;

public interface OrderEvent {
	public double getPrice();
	public OrderSideE getOrderSide();
	public String getSymbol();
	public String getTimeStamp();
}
