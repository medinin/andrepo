package com.medinin.medininapp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

public class PinEntryEditText extends EditText {
    private static final String XML_NAMESPACE_ANDROID = "";
    float mSpace = 24; //24 dp by default
    float mCharSize = 0;
    float mNumChars = 4;
    float mLineSpacing = 8; //8dp by default
    int mNumeOfChars = 4;
    public PinEntryEditText(Context context) {
        super(context);
    }

    public PinEntryEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PinEntryEditText(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PinEntryEditText(Context context, AttributeSet attrs,
                            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setBackgroundResource(0);

        float multi = context.getResources().getDisplayMetrics().density;
        mSpace = multi * mSpace; //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing; //convert to pixels
        int mMaxLength = attrs.getAttributeIntValue(
                XML_NAMESPACE_ANDROID, "maxLength", mNumeOfChars);
        mNumChars = mMaxLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        int availableWidth =
                getWidth() - getPaddingRight() - getPaddingLeft();
        if (mSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        } else {
            mCharSize =
                    (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars;
        }

        int startX = getPaddingLeft();
        int bottom = getHeight() - getPaddingBottom();

        //Text Width
        Editable text = getText();
        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(getText(), 0, textLength, textWidths);

        for (int i = 0; i < mNumChars; i++) {
            canvas.drawLine(
                    startX, bottom, startX + mCharSize, bottom, getPaint());

            if (getText().length() > i) {
                float middle = startX + mCharSize / 2;
                canvas.drawText(text,
                        i,
                        i + 1,
                        middle - textWidths[0] / 2,
                        bottom - mLineSpacing,
                        getPaint());
            }

            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
            }
        }
    }

