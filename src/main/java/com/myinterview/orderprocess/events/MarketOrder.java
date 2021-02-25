package com.myinterview.orderprocess.events;

public class MarketOrder implements OrderEvent {
	private Double price;
	private String symbol;
	private OrderSideE orderType;
	private String timeStamp;

	public MarketOrder(Double price, String symbol, OrderSideE orderType,String timeStamp) {
		this.price = price;
		this.symbol = symbol;
		this.orderType = orderType;
		this.timeStamp = timeStamp;
	}
	
	@Override
	public double getPrice() {
		// TODO Auto-generated method stub
		return price;
	}

	@Override
	public OrderSideE getOrderSide() {
		// TODO Auto-generated method stub
		return orderType;
	}

	@Override
	public String getSymbol() {
		// TODO Auto-generated method stub
		return symbol;
	}

	@Override
	public String getTimeStamp() {
		return timeStamp;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

		MarketOrder limit = (MarketOrder)obj;
        if ((this.symbol == null) ? (limit.symbol != null) : !this.symbol.equals(limit.symbol)) {
            return false;
        }

        return true;
	}
	
	@Override
	public int hashCode() {
		return this.symbol.hashCode();
	}

	@Override
	public String toString() {
		return "MarketOrder: Price="+getPrice()
			+" Symbol="+getSymbol()
			+" OrderType="+getOrderSide()
			+" timeStamp="+getTimeStamp();
	}
}
