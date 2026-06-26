package com.nhynhi.store.model;

/** Một dòng thống kê dùng cho danh sách Top (sản phẩm / khách hàng). */
public class StatRow {
    public final int rank;
    public final String title;
    public final String subtitle;
    public final String value;     // cột phải, đã định dạng
    public final String imageUrl;  // có thể null

    public StatRow(int rank, String title, String subtitle, String value, String imageUrl) {
        this.rank = rank;
        this.title = title;
        this.subtitle = subtitle;
        this.value = value;
        this.imageUrl = imageUrl;
    }
}
