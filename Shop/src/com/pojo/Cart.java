package com.pojo;

import java.util.HashMap;
import java.util.Map;

public class Cart {
	private double total;
	private Map<String, CartItem>	cartItems=new HashMap<String, CartItem>();
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public Map<String, CartItem> getCartItems() {
		return cartItems;
	}
	public void setCartItems(Map<String, CartItem> cartItems) {
		this.cartItems = cartItems;
	}
	
	
}
