package com.reactlibrary;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.textinput.ReactEditText;

public class WPReactEditText extends ReactEditText {

    public WPReactEditText(Context context) {
        super(context);

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // Check if the external listener has consumed the enter pressed event
                    // In that case stop the execution
                    emitEnterKeyDetected();
                    return true;
                }
                return false;
            }
        });

        addTextChangedListener(new TextWatcher() {
            private StringBuilder textBefore;
            private int start = 0;
            private int selStart;
            private int selEnd;
            private boolean shouldDelteEnter = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBefore = new StringBuilder(s);
                this.start = start;
                selStart = getSelectionStart();
                selEnd = getSelectionEnd();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textBefore.length() == s.length() - 1) {
                    if (s.charAt(this.start) == '\n') {
                        StringBuilder newTextCopy = new StringBuilder(s);
                        ReactContext reactContext = (ReactContext) getContext();
                        EventDispatcher eventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
                        eventDispatcher.dispatchEvent(
                                new WPReactEnterEvent(getId(), newTextCopy.replace(this.start, this.start+1, "").toString(), selStart, selEnd, true, incrementAndGetEventCounter()));
                        shouldDelteEnter = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (shouldDelteEnter && s.length() > 0 && this.start < s.length() && s.charAt(this.start) == '\n') {
                    s.replace(start, start + 1, "");
                    shouldDelteEnter = false;
                }
            }
        });
    }

    private void emitEnterKeyDetected() {
        String content = this.getText().toString();
        int cursorPositionStart = getSelectionStart();
        int cursorPositionEnd = getSelectionEnd();
        ReactContext reactContext = (ReactContext) getContext();
        EventDispatcher eventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
        eventDispatcher.dispatchEvent(
                new WPReactEnterEvent(getId(), content, cursorPositionStart, cursorPositionEnd, false, incrementAndGetEventCounter()));
    }
}