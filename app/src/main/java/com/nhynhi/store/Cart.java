package com.nhynhi.store;

import com.nhynhi.store.model.CartItem;
import com.nhynhi.store.model.SProduct;

import java.util.ArrayList;
import java.util.List;

/** Giỏ hàng dùng chung toàn ứng dụng (singleton trong bộ nhớ). */
public final class Cart {
    private static final Cart INSTANCE = new Cart();
    private final List<CartItem> items = new ArrayList<>();

    private Cart() { }

    public static Cart get() { return INSTANCE; }

    public List<CartItem> getItems() { return items; }

    /** Thêm sản phẩm vào giỏ; nếu đã có thì tăng số lượng. */
    public void add(SProduct product, int qty) {
        for (CartItem it : items) {
            if (it.getProduct().getId().equals(product.getId())) {
                it.setQuantity(it.getQuantity() + qty);
                return;
            }
        }
        items.add(new CartItem(product, qty));
    }

    public void remove(CartItem item) { items.remove(item); }

    public void increase(CartItem item) { item.setQuantity(item.getQuantity() + 1); }

    public void decrease(CartItem item) {
        if (item.getQuantity() <= 1) items.remove(item);
        else item.setQuantity(item.getQuantity() - 1);
    }

    public void clear() { items.clear(); }

    public int totalQuantity() {
        int n = 0;
        for (CartItem it : items) n += it.getQuantity();
        return n;
    }

    public double totalAmount() {
        double sum = 0;
        for (CartItem it : items) sum += it.getLineTotal();
        return sum;
    }

    public boolean isEmpty() { return items.isEmpty(); }
}
