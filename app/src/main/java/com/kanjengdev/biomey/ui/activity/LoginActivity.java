package com.kanjengdev.biomey.ui.activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kanjengdev.biomey.R;
import com.kanjengdev.biomey.databinding.ActivityLoginBinding;
import com.kanjengdev.biomey.db.DatabaseHelper;
import com.kanjengdev.biomey.utils.Functions;
import com.kanjengdev.biomey.utils.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    StringBuilder input_text;
    List<String> randomWords;

    String uid;
    String key;
    long press;
    long release;

    private static final int NUMBER_OF_WORDS = 6;

    private ActivityLoginBinding binding;

    private DatabaseHelper dbHelper;

    private Functions functions;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        functions = new Functions(this);
        sharedPreferences = new SharedPreferences(this);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        initComponents();
    }

    private void initComponents(){
        uid = sharedPreferences.loadUID();

        // Load words from JSON file
        List<String> words = loadWordsFromJson();

        // Generate 10 random words from the list
        randomWords = getRandomWords(words, NUMBER_OF_WORDS);

        // Display the random words in the TextView
        binding.corpusesText.setText(String.join(" ", randomWords));

        binding.reset.setEnabled(false);
        binding.next.setEnabled(false);

        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().isEmpty()){
                    binding.reset.setEnabled(false);
                    binding.next.setEnabled(false);
                }else{
                    binding.reset.setEnabled(true);
                    if(charSequence.toString().equals(binding.corpusesText.getText().toString())){
                        binding.next.setEnabled(true);
                    }else{
                        binding.next.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(binding.editText.getText()).clear();
                input_text = new StringBuilder();

                binding.press.setText("press");
                binding.release.setText("release");

                dbHelper.resetLogin();
            }
        });

        binding.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomWords = getRandomWords(words, NUMBER_OF_WORDS);

                // Display the random words in the TextView
                binding.corpusesText.setText(String.join(" ", randomWords));

                Objects.requireNonNull(binding.editText.getText()).clear();
                input_text = new StringBuilder();

                binding.press.setText("press");
                binding.release.setText("release");

                dbHelper.resetLogin();
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogLoading(false);
            }
        });

        keyboardButton();
    }

    private List<String> loadWordsFromJson() {
        List<String> words = new ArrayList<>();
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("kbbi.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                words.add(jsonArray.getString(i));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return words;
    }

    private List<String> getRandomWords(List<String> words, int numberOfWords) {
        Collections.shuffle(words, new Random()); // Shuffle the list
        return words.subList(0, Math.min(numberOfWords, words.size())); // Get the first 'numberOfWords' items
    }

    public void dialogLoading(Boolean finish) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_get_data);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Python py = Python.getInstance();
                PyObject pyObject = py.getModule("main");

                JSONArray jsonArray_database = dbHelper.getKeystrokeAsJson();
                String jsonString_train = jsonArray_database.toString();

                JSONArray jsonArray_login = dbHelper.getLoginAsJson();
                String jsonString_test = jsonArray_login.toString();

                PyObject convert_train = pyObject.callAttr("convert",jsonString_train);
                PyObject convert_test = pyObject.callAttr("convert",jsonString_test);

                PyObject adaptive_train = pyObject.callAttr("feature_extraction",convert_train);
                PyObject adaptive_test = pyObject.callAttr("feature_extraction",convert_test);

                PyObject distance = pyObject.callAttr("mahalanobis",adaptive_train,adaptive_test);
                PyObject fusion = pyObject.callAttr("predict",binding.slider.getValue(),distance);

                if (fusion.toString().equals("[[1]]")) {
                    functions.dialogGenuine(false);
                }else{
                    functions.dialogImpostor(false);
                }

                bottomSheetDialog.dismiss();
            }
        }, 1000L);

        bottomSheetDialog.show();

        if(finish){
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
        }
    }

    void keyboardButton(){
        // Ganti nama variabel search dengan input_text
        input_text = new StringBuilder();

        View.OnTouchListener keyboardTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Ganti semua referensi search dengan input_text
                if (view.getId() == R.id.a) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('a');
                        key = "a";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.b) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('b');
                        key = "b";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.c) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('c');
                        key = "c";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.d) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('d');
                        key = "d";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.e) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('e');
                        key = "e";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.f) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('f');
                        key = "f";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.g) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('g');
                        key = "g";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.h) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('h');
                        key = "h";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.i) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('i');
                        key = "i";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.j) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('j');
                        key = "j";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.k) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('k');
                        key = "k";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.l) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('l');
                        key = "l";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.m) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('m');
                        key = "m";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.n) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('n');
                        key = "n";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.o) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('o');
                        key = "o";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.p) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('p');
                        key = "p";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.q) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('q');
                        key = "q";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.r) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('r');
                        key = "r";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.s) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('s');
                        key = "s";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.t) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('t');
                        key = "t";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.u) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('u');
                        key = "u";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.v) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('v');
                        key = "v";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.w) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('w');
                        key = "w";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.x) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('x');
                        key = "x";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.y) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('y');
                        key = "y";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.z) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append('z');
                        key = "z";

                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                } else if (view.getId() == R.id.space) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        input_text.append(' ');
                        key = "SPACE";
                        press = System.currentTimeMillis();
                        binding.press.setText(String.valueOf(press));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        release = System.currentTimeMillis();
                        binding.release.setText(String.valueOf(release));
                    }
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Update the text field
                    binding.editText.setText(input_text.toString());
                    dbHelper.insertLogin(uid, 1, 1, press, release, key);
                }

                return false;
            }
        };

        // Mengatur OnTouchListener untuk setiap tombol
        binding.keyboard.a.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.b.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.c.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.d.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.e.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.f.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.g.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.h.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.i.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.j.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.k.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.l.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.m.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.n.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.o.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.p.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.q.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.r.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.s.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.t.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.u.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.v.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.w.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.x.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.y.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.z.setOnTouchListener(keyboardTouchListener);
        binding.keyboard.space.setOnTouchListener(keyboardTouchListener);

    }
}