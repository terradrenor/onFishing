package com.ifmo.chernyshov.onfishing.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ifmo.chernyshov.onfishing.R;
import com.ifmo.chernyshov.onfishing.database.DataStorage;

import java.util.ArrayList;

/**
 * Created by Andrey Chernyshov on 17.12.2016.
 */
public class AddNewPlaceDialog extends Dialog {
    private EditText text;
    private Button ok, cancel;
    private Spinner sp;
    private DataStorage ds;

    public AddNewPlaceDialog(Context context, DataStorage ds, Spinner sp) {
        super(context);
        this.sp = sp;
        this.ds = ds;

        setContentView(R.layout.add_new_place_dialog);
        setTitle("Add new place");

        text = (EditText) findViewById(R.id.place_enter);
        ok = (Button) findViewById(R.id.add_place_button);
        cancel = (Button) findViewById(R.id.cancel_place_button);

        addListeners();
    }

    private void addListeners() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        text.requestFocus();

        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String s = text.getText().toString();
                    if (!s.isEmpty()) {
                        updateDatabaseAndSpinner(text.getText().toString());
                        dismiss();
                        handled = true;
                    }
                }
                return handled;
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = text.getText().toString();
                if (!s.isEmpty()) {
                    updateDatabaseAndSpinner(text.getText().toString());
                    dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void updateDatabaseAndSpinner(String values) {
        ArrayList<String> places = ds.getPlaces();
        places.add(values);
        ds.putPlace(values);
        sp.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, places));
    }
}
