package com.ifmo.chernyshov.onfishing;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by Andrey Chernyshov on 15.12.2016.
 */
public class MyNumberPicker extends NumberPicker {
    public MyNumberPicker(Context context) {
        super(context, null);
    }

    public MyNumberPicker(Context context, AttributeSet set) {
        super(context, set);
    }

    public void removeFilter() {
        Resources res = Resources.getSystem();
        int id = res.getIdentifier("numberpicker_input", "id", "android");
        EditText mInputText = (EditText) findViewById(id);
        mInputText.setFilters(new InputFilter[]{});
    }
}
