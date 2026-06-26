package com.nhynhi.store;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhynhi.store.model.SCategory;
import com.nhynhi.store.model.SCustomer;
import com.nhynhi.store.model.SOrder;
import com.nhynhi.store.model.SOrderDetail;
import com.nhynhi.store.model.SProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Đọc toàn bộ dữ liệu cửa hàng từ Firebase Realtime Database trong một lần,
 * parse thủ công cho an toàn về kiểu dữ liệu (số/boolean).
 */
public class StoreRepository {

    /** Gói toàn bộ dữ liệu đã nạp + các map tra cứu nhanh theo id. */
    public static class StoreData {
        public final List<SCategory> categories = new ArrayList<>();
        public final List<SProduct> products = new ArrayList<>();
        public final List<SCustomer> customers = new ArrayList<>();
        public final List<SOrder> orders = new ArrayList<>();
        public final List<SOrderDetail> orderDetails = new ArrayList<>();

        public final Map<String, SCategory> categoryMap = new HashMap<>();
        public final Map<String, SProduct> productMap = new HashMap<>();
        public final Map<String, SCustomer> customerMap = new HashMap<>();
    }

    public interface DataCallback {
        void onData(StoreData data);
        void onError(String message);
    }

    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    /** Nạp dữ liệu một lần (single value event). */
    public void loadAll(final DataCallback cb) {
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    cb.onData(parse(snapshot));
                } catch (Exception e) {
                    cb.onError("Lỗi xử lý dữ liệu: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cb.onError(error.getMessage());
            }
        });
    }

    private StoreData parse(DataSnapshot root) {
        StoreData d = new StoreData();

        for (DataSnapshot c : root.child("categories").getChildren()) {
            SCategory cat = new SCategory(c.getKey(), str(c, "categoryName"), str(c, "description"));
            d.categories.add(cat);
            d.categoryMap.put(cat.getId(), cat);
        }

        for (DataSnapshot p : root.child("products").getChildren()) {
            SProduct prod = new SProduct();
            prod.setId(p.getKey());
            prod.setProductName(str(p, "productName"));
            prod.setCategoryId(str(p, "categoryId"));
            prod.setImageUrl(str(p, "imageUrl"));
            prod.setPrice(dbl(p, "price"));
            prod.setStock(intg(p, "stock"));
            prod.setActive(bool(p, "isActive", true));
            d.products.add(prod);
            d.productMap.put(prod.getId(), prod);
        }

        for (DataSnapshot c : root.child("customers").getChildren()) {
            SCustomer cus = new SCustomer();
            cus.setId(c.getKey());
            cus.setFullName(str(c, "fullName"));
            cus.setEmail(str(c, "email"));
            cus.setPhone(str(c, "phone"));
            cus.setAddress(str(c, "address"));
            d.customers.add(cus);
            d.customerMap.put(cus.getId(), cus);
        }

        for (DataSnapshot o : root.child("orders").getChildren()) {
            SOrder ord = new SOrder();
            ord.setId(o.getKey());
            ord.setCustomerId(str(o, "customerId"));
            ord.setEmployeeId(str(o, "employeeId"));
            ord.setOrderDate(str(o, "orderDate"));
            ord.setStatus(str(o, "status"));
            ord.setTotalAmount(dbl(o, "totalAmount"));
            d.orders.add(ord);
        }

        for (DataSnapshot od : root.child("orderDetails").getChildren()) {
            SOrderDetail det = new SOrderDetail();
            det.setId(od.getKey());
            det.setOrderId(str(od, "orderId"));
            det.setProductId(str(od, "productId"));
            det.setQuantity(intg(od, "quantity"));
            det.setUnitPrice(dbl(od, "unitPrice"));
            d.orderDetails.add(det);
        }

        return d;
    }

    // ── Helpers parse an toàn ──────────────────────────────────────────────
    private static String str(DataSnapshot d, String key) {
        Object o = d.child(key).getValue();
        return o == null ? "" : String.valueOf(o);
    }

    private static double dbl(DataSnapshot d, String key) {
        Object o = d.child(key).getValue();
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0; }
    }

    private static int intg(DataSnapshot d, String key) {
        return (int) dbl(d, key);
    }

    private static boolean bool(DataSnapshot d, String key, boolean def) {
        Object o = d.child(key).getValue();
        if (o instanceof Boolean) return (Boolean) o;
        if (o == null) return def;
        return "true".equalsIgnoreCase(String.valueOf(o));
    }
}
