package com.kanjengdev.biomey.ui.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kanjengdev.biomey.R;
import com.kanjengdev.biomey.adapter.ContributorAdapter;
import com.kanjengdev.biomey.databinding.ActivityAboutBinding;
import com.kanjengdev.biomey.databinding.ActivityAboutNewBinding;
import com.kanjengdev.biomey.databinding.ActivitySplashBinding;
import com.kanjengdev.biomey.model.Contributor;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutNewBinding binding;

    List<Contributor> item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initComponents();
    }

    private void initComponents(){
        dataUs();
    }

    private void dataUs(){
        item = new ArrayList<>();
        item.add(new Contributor("Prasti Eko Y. S.Kom. M.Kom.", "Penggagas Ide", R.drawable.gppras));
        item.add(new Contributor("Imam Rafiif A. S.Kom.", "Pengembang Ide I", 0));
        item.add(new Contributor("I Wayan Adi W. S.Kom.", "Pengembang Ide I", 0));
        item.add(new Contributor("R. Ardityo Cahyo P. H. S.Kom.", "Pengembang Ide II", R.drawable.ardityoc));

        ContributorAdapter contributorAdapter = new ContributorAdapter(this, item);
        binding.contributor.setAdapter(contributorAdapter);
        binding.contributor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
}