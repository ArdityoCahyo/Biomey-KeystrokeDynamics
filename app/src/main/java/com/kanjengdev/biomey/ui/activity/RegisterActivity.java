package com.kanjengdev.biomey.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kanjengdev.biomey.MainActivity;
import com.kanjengdev.biomey.databinding.ActivityRegisterBinding;
import com.kanjengdev.biomey.db.DatabaseHelper;
import com.kanjengdev.biomey.utils.SharedPreferences;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = new SharedPreferences(this);

        binding.register.setEnabled(false);

        binding.editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().isEmpty()){
                    binding.username.setText("Siapa ini?");
                    binding.register.setEnabled(false);
                }else{
                    binding.username.setText(charSequence);
                    if (Objects.requireNonNull(binding.editTextUid.getText()).toString().isEmpty()){
                        binding.register.setEnabled(false);
                    }else{
                        binding.register.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.editTextUid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().isEmpty()){
                    binding.uid.setVisibility(View.INVISIBLE);
                    binding.register.setEnabled(false);
                }else{
                    binding.uid.setVisibility(View.VISIBLE);
                    binding.uid.setText("("+charSequence+")");
                    if (Objects.requireNonNull(binding.editTextName.getText()).toString().isEmpty()){
                        binding.register.setEnabled(false);
                    }else{
                        binding.register.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (binding.username.getText().toString().equals("Siapa ini?")){
            binding.uid.setVisibility(View.INVISIBLE);
        }

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.saveDefaultUsername(Objects.requireNonNull(binding.editTextName.getText()).toString());
                sharedPreferences.saveDefaultUID(Objects.requireNonNull(binding.editTextUid.getText()).toString());
                sharedPreferences.saveSession("1");
                sharedPreferences.saveSentences("1");
                dbHelper.insertUser(Objects.requireNonNull(binding.editTextName.getText()).toString(), Objects.requireNonNull(binding.editTextUid.getText()).toString());
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

    }
}