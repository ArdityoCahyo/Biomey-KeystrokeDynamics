package com.kanjengdev.biomey.ui.fragment.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.kanjengdev.biomey.databinding.FragmentHistoryBinding;
import com.kanjengdev.biomey.db.DatabaseHelper;
import com.kanjengdev.biomey.utils.SharedPreferences;

import org.json.JSONArray;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(getContext());
        sharedPreferences = new SharedPreferences(getContext());

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(getActivity()));
        }

        binding.tablePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray jsonArray = dbHelper.getKeystrokeAsJson();
                String jsonString = jsonArray.toString();

                binding.textView.setText(jsonString);
            }
        });

        binding.pythonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Python py = Python.getInstance();
                PyObject pyObject = py.getModule("main");

                JSONArray jsonArray = dbHelper.getKeystrokeAsJson();
                String jsonString = jsonArray.toString();

                PyObject obj = pyObject.callAttr("convert",jsonString);

                binding.textView.setText(obj.toString());
            }
        });

        binding.tableReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.saveSession("1");
                sharedPreferences.saveSentences("1");
                dbHelper.resetTemp();
                dbHelper.resetKeyDB();
            }
        });

        binding.extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Python py = Python.getInstance();
                PyObject pyObject = py.getModule("main");

                JSONArray jsonArray = dbHelper.getKeystrokeAsJson();
                String jsonString = jsonArray.toString();

                PyObject convert = pyObject.callAttr("convert",jsonString);
                PyObject adaptive = pyObject.callAttr("feature_extraction",convert);

                binding.textView.setText(adaptive.toString());

            }
        });

        binding.distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Python py = Python.getInstance();
                PyObject pyObject = py.getModule("main");

                JSONArray jsonArray = dbHelper.getKeystrokeAsJson();
                String jsonString = jsonArray.toString();

                PyObject convert = pyObject.callAttr("convert",jsonString);
                PyObject adaptive = pyObject.callAttr("feature_extraction",convert);
                PyObject distance = pyObject.callAttr("mahalanobis",adaptive,adaptive);

                binding.textView.setText(distance.toString());
            }
        });

        binding.fusion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Python py = Python.getInstance();
                PyObject pyObject = py.getModule("main");

                JSONArray jsonArray = dbHelper.getKeystrokeAsJson();
                String jsonString = jsonArray.toString();

                PyObject convert = pyObject.callAttr("convert",jsonString);
                PyObject adaptive = pyObject.callAttr("feature_extraction",convert);
                PyObject distance = pyObject.callAttr("mahalanobis",adaptive,adaptive);
                PyObject fusion = pyObject.callAttr("predict",5,distance);

                binding.textView.setText(fusion.toString());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}