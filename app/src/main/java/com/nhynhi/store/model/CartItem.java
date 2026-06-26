package com.nhynhi.store.model;

import java.io.Serializable;

/** Một dòng trong giỏ hàng (giữ trong bộ nhớ). */
public class CartItem implements Serializable {
    private final SProduct product;
    private int quantity;

    public CartItem(SProduct product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public SProduct getProduct() { return product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getLineTotal() { return product.getPrice() * quantity; }
}
