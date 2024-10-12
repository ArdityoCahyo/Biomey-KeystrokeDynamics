package com.kanjengdev.biomey.ui.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.kanjengdev.biomey.databinding.FragmentHomeBinding;
import com.kanjengdev.biomey.databinding.FragmentHomeNewBinding;
import com.kanjengdev.biomey.db.DatabaseHelper;
import com.kanjengdev.biomey.ui.activity.AboutActivity;
import com.kanjengdev.biomey.ui.activity.LoginActivity;
import com.kanjengdev.biomey.utils.SharedPreferences;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    private FragmentHomeNewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeNewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(getContext());
        sharedPreferences = new SharedPreferences(getContext());

        binding.username.setText(sharedPreferences.loadUsername());
        binding.uid.setText("("+sharedPreferences.loadUID()+")");
        binding.session.setText(sharedPreferences.loadSession());
        binding.sentences.setText(sharedPreferences.loadSentences());

        binding.textMarquee1.setSelected(true);
        binding.textMarquee2.setSelected(true);

        binding.subsave.setText("Status File belum lengkap ("+sharedPreferences.loadSentences()+" kalimat /"+sharedPreferences.loadSession()+" sesi)");
        binding.subshare.setText("Status File belum lengkap ("+sharedPreferences.loadSentences()+" kalimat /"+sharedPreferences.loadSession()+" sesi)");

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray jsonArray = dbHelper.getKeystrokeAsJson();
                String jsonString = jsonArray.toString();
                saveJsonToFile(jsonString);
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareFile();
            }
        });

        binding.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });

        return root;
    }

    private void saveJsonToFile(String jsonString) {
        String fileName = sharedPreferences.loadUID()+".json";

        try {
            FileOutputStream fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();

            Toast.makeText(getContext(), "File berhasil disimpan di internal storage", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Gagal menyimpan file", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareFile() {
        String fileName = sharedPreferences.loadUID()+".json";
        File file = new File(getActivity().getFilesDir(), fileName);

        if (!file.exists()) {
            Toast.makeText(getContext(), "File tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".provider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/json");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Bagikan file menggunakan"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}