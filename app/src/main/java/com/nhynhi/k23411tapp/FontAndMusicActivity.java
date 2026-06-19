package com.nhynhi.k23411tapp;

import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class FontAndMusicActivity extends AppCompatActivity {
    Button btnPlayAudio1, btnPlayAudio2;
    TextView txtFont;
    ListView lvFonts;

    ArrayList<String> fonts;
    ArrayAdapter<String> adapterfonts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_font_and_music);
        addViews();
        addEvents();
        loadFonts();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFonts() {
        try {
            String[] fontFiles = getAssets().list("Font");
            fonts.clear();
            if (fontFiles != null) {
                for (String fontFile : fontFiles) {
                    fonts.add(fontFile);
                }
                adapterfonts.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e("FontError", "Lỗi tải font: " + e.getMessage());
        }
    }

    private void addEvents() {
        btnPlayAudio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio("Music/audio1.mp3");
            }
        });

        btnPlayAudio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio("Music/audio2.mp3");
            }
        });

        // Sửa lại thành setOnItemClickListener và gọi đúng biến position
        lvFonts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeFont(position);
            }
        });
    }

    private void changeFont(int position) {
        try {
            String selectedFont = fonts.get(position);
            txtFont.setTypeface(Typeface.createFromAsset(getAssets(), "Font/" + selectedFont));
            adapterfonts.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("FontError", "Lỗi đổi font: " + e.getMessage());
        }
    }

    public void playAudio(String audioFile) {
        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(audioFile);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(
                    assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength()
            );
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });

        } catch (Exception e) {
            Log.e("AudioPlayer", "Lỗi phát nhạc: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addViews() {
        btnPlayAudio1 = findViewById(R.id.btnPlayAudio1);
        btnPlayAudio2 = findViewById(R.id.btnPlayAudio2);
        txtFont = findViewById(R.id.txtFont);
        lvFonts = findViewById(R.id.lvFonts);

        fonts = new ArrayList<>();
        adapterfonts = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fonts);
        lvFonts.setAdapter(adapterfonts);
    }
}