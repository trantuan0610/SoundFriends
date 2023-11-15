package com.example.soundfriends.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ToggleInputFocus {
    // Hàm ẩn bàn phím
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Hiển thị bàn phím và focus vào EditText
    public static void showKeyboard(Context context, EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    // Hàm unfocus (mất focus) và ẩn bàn phím
    public static void unfocusAndHideKeyboard(Context context, EditText editText) {
        editText.clearFocus(); // Mất focus
        hideKeyboard(context, editText); // Ẩn bàn phím
    }
}
