package com.nhynhi.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchMajorActivity extends AppCompatActivity {

    private static final int MAX_SUGGESTIONS = 8;
    private static final String MYUEL_URL =
            "https://myuel.uel.edu.vn/Default.aspx?ModuleId=f92f39b2-dea3-4185-8cbb-56c1c49c5226";

    EditText edtSearch;
    ImageButton btnMic;
    android.widget.Button btnSearch;
    ListView lvSuggestions;
    TextView txtSuggestLabel, txtResultLabel, txtResult;
    ProgressBar progressBar;
    ScrollView scrollResult;

    ArrayAdapter<String> suggestAdapter;
    ArrayList<String> suggestList = new ArrayList<>();

    // -----------------------------------------------------------------------
    // Toàn bộ danh sách ngành UEL
    // -----------------------------------------------------------------------
    private static final List<String> ALL_MAJORS = Arrays.asList(
            "Kinh tế học",
            "Kinh tế học chất lượng cao",
            "Kinh tế và Quản lý công",
            "Kinh tế và Quản lý công Chất lượng cao",
            "Kinh doanh quốc tế",
            "Kinh doanh quốc tế (Tiếng Anh)",
            "Kinh doanh quốc tế Chất lượng cao",
            "Kinh doanh quốc tế Chất lượng cao bằng tiếng Anh",
            "Kinh tế đối ngoại",
            "Kinh tế đối ngoại Chất lượng cao",
            "Kinh tế đối ngoại Chất lượng cao bằng tiếng Anh",
            "Kinh tế quốc tế",
            "Công nghệ tài chính",
            "Công nghệ tài chính (Chương trình Co-operative Education)",
            "Công nghệ tài chính Chất lượng cao",
            "Tài chính - Ngân hàng",
            "Tài chính - Ngân hàng (Tiếng Anh)",
            "Tài chính - Ngân hàng Chất lượng cao",
            "Tài chính - Ngân hàng Chất lượng cao bằng tiếng Anh",
            "Kế toán",
            "Kế toán (Tiếng Anh) (Tích hợp chứng chỉ quốc tế ICAEW)",
            "Kế toán Chất lượng cao",
            "Kế toán Chất lượng cao bằng tiếng Anh",
            "Kiểm toán",
            "Kiểm toán Chất lượng cao",
            "Hệ thống thông tin quản lý",
            "Hệ thống thông tin quản lý (Chương trình Co-operative Education)",
            "Hệ thống thông tin quản lý Chất lượng cao",
            "Kinh doanh số và Trí tuệ nhân tạo",
            "Kinh doanh số và Trí tuệ nhân tạo Chất lượng cao",
            "Thương mại điện tử",
            "Thương mại điện tử (Tiếng Anh)",
            "Thương mại điện tử Chất lượng cao",
            "Thương mại điện tử Chất lượng cao bằng tiếng Anh",
            "Digital Marketing",
            "Marketing",
            "Marketing (Tiếng Anh)",
            "Marketing Chất lượng cao",
            "Marketing Chất lượng cao bằng tiếng Anh",
            "Quản lý công",
            "Quản trị du lịch và lữ hành",
            "Quản trị kinh doanh",
            "Quản trị kinh doanh (Tiếng Anh)",
            "Quản trị kinh doanh Chất lượng cao",
            "Quản trị kinh doanh Chất lượng cao bằng tiếng Anh",
            "Luật Dân sự",
            "Luật Dân sự Chất lượng cao",
            "Luật dân sự Chất lượng cao bằng tiếng Anh",
            "Luật Tài chính - Ngân hàng",
            "Luật Tài chính - Ngân hàng chất lượng cao",
            "Luật Tài chính - Ngân hàng Chất lượng cao tăng cường tiếng Pháp",
            "Luật và Chính sách công",
            "Luật kinh doanh",
            "Luật kinh doanh Chất lượng cao",
            "Luật Kinh tế",
            "Luật thương mại quốc tế",
            "Luật Thương mại quốc tế (Tiếng Anh)",
            "Luật thương mại quốc tế Chất lượng cao",
            "Luật thương mại quốc tế Chất lượng cao bằng tiếng Anh",
            "Toán kinh tế",
            "Toán ứng dụng trong kinh tế, quản trị và tài chính",
            "Toán ứng dụng trong Kinh tế, Quản trị và Tài chính (Tiếng Anh)",
            "Toán ứng dụng trong kinh tế, quản trị và tài chính Chất lượng cao",
            "Toán ứng dụng trong kinh tế, quản trị và tài chính chất lượng cao bằng tiếng Anh",
            "Phân tích dữ liệu"
    );

    // -----------------------------------------------------------------------
    // Voice recognition launcher
    // -----------------------------------------------------------------------
    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData()
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        edtSearch.setText(matches.get(0));
                        runFuzzySearch(matches.get(0));
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_major);

        edtSearch      = findViewById(R.id.edtSearch);
        btnMic         = findViewById(R.id.btnMic);
        btnSearch      = findViewById(R.id.btnSearch);
        lvSuggestions  = findViewById(R.id.lvSuggestions);
        txtSuggestLabel= findViewById(R.id.txtSuggestLabel);
        txtResultLabel = findViewById(R.id.txtResultLabel);
        txtResult      = findViewById(R.id.txtResult);
        progressBar    = findViewById(R.id.progressBar);
        scrollResult   = findViewById(R.id.scrollResult);

        suggestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestList);
        lvSuggestions.setAdapter(suggestAdapter);

        // Xây index vector 1 lần duy nhất
        buildMajorIndex();

        // Gõ liên tục → fuzzy search ngay
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                runFuzzySearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Nhấn nút Tìm → scrape Jsoup
        btnSearch.setOnClickListener(v -> {
            String query = edtSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "Nhập tên ngành trước!", Toast.LENGTH_SHORT).show();
                return;
            }
            scrapeMyUEL(query);
        });

        // Chọn gợi ý → tự điền và scrape
        lvSuggestions.setOnItemClickListener((parent, view, position, id) -> {
            String selected = suggestList.get(position);
            edtSearch.setText(selected);
            edtSearch.setSelection(selected.length());
            scrapeMyUEL(selected);
        });

        // Mic button
        btnMic.setOnClickListener(v -> startVoiceRecognition());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ===================================================================
    // VECTOR SEARCH ENGINE — Character Trigram + Cosine Similarity
    // ===================================================================
    //
    // Ý tưởng:
    //   Mỗi chuỗi được biểu diễn thành vector trong không gian n-gram.
    //   Mỗi chiều của vector = tần suất xuất hiện của 1 trigram (3 ký tự).
    //
    //   Ví dụ "kinh te":
    //     trigrams = {"kin", "inh", "nh ", "h t", " te"}
    //     vector   = {"kin":1, "inh":1, "nh ":1, ...}
    //
    //   Cosine Similarity giữa vector A và B:
    //
    //         A · B
    //   cos = ───────────    ∈ [0, 1]
    //         |A| × |B|
    //
    //   = 1  → 2 chuỗi giống hệt nhau
    //   = 0  → không có trigram chung
    //
    //   Ưu điểm so với Levenshtein:
    //   - Không quan tâm thứ tự → "kinh te" khớp "Kế toán Kinh tế"
    //   - Chịu lỗi chính tả tốt hơn
    //   - Phức tạp O(k) với k = số trigram duy nhất, thay vì O(n*m)
    // ===================================================================

    // Chuyển chuỗi → Map<trigram, tần_suất>
    private Map<String, Integer> toTrigramVector(String text) {
        String s = text.toLowerCase(new Locale("vi")).replaceAll("\\s+", " ").trim();
        Map<String, Integer> vec = new HashMap<>();
        // Thêm padding để bắt đầu/cuối chuỗi cũng tạo trigram
        String padded = " " + s + " ";
        for (int i = 0; i <= padded.length() - 3; i++) {
            String trigram = padded.substring(i, i + 3);
            vec.put(trigram, vec.getOrDefault(trigram, 0) + 1);
        }
        return vec;
    }

    // Tích vô hướng (dot product) của 2 vector
    private double dotProduct(Map<String, Integer> a, Map<String, Integer> b) {
        double dot = 0;
        for (Map.Entry<String, Integer> entry : a.entrySet()) {
            Integer bVal = b.get(entry.getKey());
            if (bVal != null) dot += entry.getValue() * bVal;
        }
        return dot;
    }

    // Chuẩn (norm / magnitude) của vector
    private double norm(Map<String, Integer> v) {
        double sum = 0;
        for (int val : v.values()) sum += (double) val * val;
        return Math.sqrt(sum);
    }

    // Cosine Similarity ∈ [0, 1]
    private double cosineSimilarity(Map<String, Integer> a, Map<String, Integer> b) {
        double normA = norm(a), normB = norm(b);
        if (normA == 0 || normB == 0) return 0;
        return dotProduct(a, b) / (normA * normB);
    }

    // Cache vector của toàn bộ danh sách ngành (tính 1 lần lúc khởi tạo)
    private final Map<String, Map<String, Integer>> majorVectors = new HashMap<>();

    private void buildMajorIndex() {
        for (String major : ALL_MAJORS) {
            majorVectors.put(major, toTrigramVector(major));
        }
    }

    // -----------------------------------------------------------------------
    // Vector Search: query → trigram vector → cosine similarity với mọi ngành
    // -----------------------------------------------------------------------
    private void runFuzzySearch(String query) {
        suggestList.clear();
        if (query.trim().isEmpty()) {
            txtSuggestLabel.setVisibility(View.GONE);
            lvSuggestions.setVisibility(View.GONE);
            suggestAdapter.notifyDataSetChanged();
            return;
        }

        Map<String, Integer> queryVec = toTrigramVector(query);

        // Tính cosine similarity giữa query và từng ngành
        List<double[]> scores = new ArrayList<>(); // [index, score]
        for (int i = 0; i < ALL_MAJORS.size(); i++) {
            String major = ALL_MAJORS.get(i);
            double score = cosineSimilarity(queryVec, majorVectors.get(major));
            if (score > 0.15) {
                scores.add(new double[]{i, score});
            }
        }

        // Sort theo score giảm dần
        scores.sort((x, y) -> Double.compare(y[1], x[1]));

        // Lấy top MAX_SUGGESTIONS
        for (int k = 0; k < Math.min(scores.size(), MAX_SUGGESTIONS); k++) {
            suggestList.add(ALL_MAJORS.get((int) scores.get(k)[0]));
        }

        txtSuggestLabel.setVisibility(suggestList.isEmpty() ? View.GONE : View.VISIBLE);
        lvSuggestions.setVisibility(suggestList.isEmpty() ? View.GONE : View.VISIBLE);
        suggestAdapter.notifyDataSetChanged();
    }

    // -----------------------------------------------------------------------
    // Nhận dạng giọng nói
    // -----------------------------------------------------------------------
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói tên ngành bạn muốn tìm...");
        try {
            speechLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận dạng giọng nói", Toast.LENGTH_SHORT).show();
        }
    }

    // -----------------------------------------------------------------------
    // Jsoup scrape MyUEL — 2 bước:
    //   Bước 1: Fetch trang chính → tìm link <a href*=OlogyID> khớp tên ngành
    //           (link đã chứa đầy đủ OlogyID + DepartmentID + GraduateLevelID + StudyTypeID)
    //   Bước 2: Fetch URL đó → parse bảng chương trình đào tạo từng học kỳ
    // -----------------------------------------------------------------------
    private void scrapeMyUEL(String majorName) {
        progressBar.setVisibility(View.VISIBLE);
        txtResultLabel.setVisibility(View.GONE);
        scrollResult.setVisibility(View.GONE);
        txtResult.setText("Đang tìm ngành \"" + majorName + "\"...");

        new Thread(() -> {
            try {
                // ── Bước 1: Parse trang chính, lấy link đầy đủ ────────────
                Document homePage = Jsoup.connect(MYUEL_URL)
                        .timeout(15000)
                        .userAgent("Mozilla/5.0 (Android 11; Mobile)")
                        .get();

                Map<String, Integer> queryVec = toTrigramVector(majorName);
                String bestHref  = null;
                String bestLabel = null;
                double bestScore = 0;

                // Tất cả link có OlogyID= trong href đều là link ngành
                Elements links = homePage.select("a[href*=OlogyID]");
                for (Element link : links) {
                    String label = link.text().trim();
                    if (label.isEmpty()) continue;
                    double score = cosineSimilarity(queryVec, toTrigramVector(label));
                    if (score > bestScore) {
                        bestScore = score;
                        bestHref  = link.attr("abs:href");
                        bestLabel = label;
                    }
                }

                if (bestHref == null || bestScore < 0.3) {
                    showResult("Không tìm thấy ngành \"" + majorName + "\" trong hệ thống MyUEL.\n\n"
                            + "Gợi ý: chọn đúng tên ngành từ danh sách gợi ý bên trên.");
                    return;
                }

                final String foundLabel = bestLabel;
                final String fullUrl    = bestHref;

                // ── Bước 2: Fetch trang chương trình đào tạo ──────────────
                Document currPage = Jsoup.connect(fullUrl)
                        .timeout(15000)
                        .userAgent("Mozilla/5.0 (Android 11; Mobile)")
                        .get();

                String result = parseCurriculum(currPage, foundLabel, fullUrl);
                showResult(result);

            } catch (Exception e) {
                Log.e("JSOUP", "Lỗi: " + e.getMessage());
                showResult("Lỗi kết nối: " + e.getMessage() + "\n\nKiểm tra internet.");
            }
        }).start();
    }

    // -----------------------------------------------------------------------
    // Parse bảng chương trình đào tạo từ HTML trả về
    // Cấu trúc MyUEL: heading "Học kỳ X" + <table> STT/MãHP/Tên/TC/Loại
    // -----------------------------------------------------------------------
    private String parseCurriculum(Document doc, String majorLabel, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append("CHƯƠNG TRÌNH ĐÀO TẠO\n");
        sb.append("Ngành: ").append(majorLabel).append("\n");
        sb.append("─────────────────────────────\n\n");

        // Lấy thông tin tổng quan (hệ, loại hình, khoa, khóa...)
        Elements infoRows = doc.select("table tr");
        boolean foundInfo = false;
        for (Element row : infoRows) {
            Elements cells = row.select("td");
            if (cells.size() == 2) {
                String key = cells.get(0).text().trim();
                String val = cells.get(1).text().trim();
                if (!key.isEmpty() && !val.isEmpty()
                        && (key.contains("hệ") || key.contains("Hệ")
                            || key.contains("loại") || key.contains("Loại")
                            || key.contains("khoa") || key.contains("Khoa")
                            || key.contains("ngành") || key.contains("Ngành")
                            || key.contains("khóa") || key.contains("Khóa")
                            || key.contains("chương") || key.contains("Chương"))) {
                    sb.append(key).append(" ").append(val).append("\n");
                    foundInfo = true;
                }
            }
        }
        if (foundInfo) sb.append("\n");

        // Tìm các section học kỳ
        // MyUEL thường dùng heading text "Học kỳ X" rồi bảng bên dưới
        Elements allElements = doc.select("*");
        String currentSemester = "";
        int subjectCount = 0;

        for (Element el : allElements) {
            String tag  = el.tagName();
            String text = el.ownText().trim();

            // Phát hiện heading học kỳ
            if (text.startsWith("Học kỳ") || text.matches(".*[Hh]ọc\\s*[Kk]ỳ\\s*\\d+.*")) {
                if (!currentSemester.equals(text)) {
                    currentSemester = text;
                    sb.append("\n📚 ").append(text).append("\n");
                    sb.append(String.format("%-5s %-10s %-40s %5s  %s\n",
                            "STT", "Mã HP", "Tên học phần", "TC", "Loại"));
                    sb.append("─────────────────────────────────────────────────────\n");
                }
                continue;
            }

            // Parse hàng bảng môn học
            if (tag.equals("tr")) {
                Elements tds = el.select("> td");
                if (tds.size() >= 4) {
                    String stt  = tds.get(0).text().trim();
                    String code = tds.size() > 1 ? tds.get(1).text().trim() : "";
                    String name = tds.size() > 2 ? tds.get(2).text().trim() : "";
                    String tc   = tds.size() > 3 ? tds.get(3).text().trim() : "";
                    String type = tds.size() > 4 ? tds.get(4).text().trim() : "";

                    // Chỉ lấy hàng có STT là số
                    if (stt.matches("\\d+") && !code.isEmpty() && !name.isEmpty()) {
                        sb.append(String.format("%-5s %-10s %-40s %5s  %s\n",
                                stt, code, name, tc, type));
                        subjectCount++;
                    }
                }
            }
        }

        if (subjectCount == 0) {
            sb.append("\nTrang đã tải nhưng không tìm thấy bảng môn học.\n");
            sb.append("Có thể MyUEL yêu cầu đăng nhập hoặc chọn thêm bộ lọc.\n\n");
            sb.append("URL đã thử:\n").append(url);
        } else {
            sb.append("\n─────────────────────────────\n");
            sb.append("Tổng: ").append(subjectCount).append(" học phần");
        }

        return sb.toString();
    }

    private void showResult(String text) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            txtResultLabel.setVisibility(View.VISIBLE);
            scrollResult.setVisibility(View.VISIBLE);
            txtResult.setText(text);
            lvSuggestions.setVisibility(View.GONE);
            txtSuggestLabel.setVisibility(View.GONE);
        });
    }
}
