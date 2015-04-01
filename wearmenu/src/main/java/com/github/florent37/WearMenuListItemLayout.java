package com.github.florent37;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by florentchampigny on 01/04/15.
 */
public class WearMenuListItemLayout extends LinearLayout
        implements WearableListView.OnCenterProximityListener {

    private ImageButton mImageButton;
    private TextView mTitle;

    private final long mAnimationDuration;
    private final float mFadedTextAlpha;
    private final int mFadedCircleColor;
    private int mSelectedCircleColor;
    private final float mChosenCircleScale;

    private AnimatorSet centerAnimator;
    private AnimatorSet nonCenterAnimator;

    public WearMenuListItemLayout(Context context) {
        this(context, null);
    }

    public WearMenuListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearMenuListItemLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);

        mAnimationDuration = 150;
        mFadedTextAlpha = 0.4f;
        mChosenCircleScale = 1.4f;
        mFadedCircleColor = getResources().getColor(R.color.grey);
        mSelectedCircleColor = getResources().getColor(R.color.blue);
    }

    // Get references to the icon and text in the item layout definition
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // These are defined in the layout file for list items
        // (see next section)
        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        mTitle = (TextView) findViewById(R.id.text);

        centerAnimator = new AnimatorSet().setDuration(mAnimationDuration);
        centerAnimator.playTogether(
                ObjectAnimator.ofFloat(mImageButton, "scaleX", mChosenCircleScale),
                ObjectAnimator.ofFloat(mImageButton, "scaleY", mChosenCircleScale),
                ObjectAnimator.ofFloat(mTitle, "alpha", mFadedTextAlpha)
        );

        nonCenterAnimator = new AnimatorSet().setDuration(mAnimationDuration);
        nonCenterAnimator.playTogether(
                ObjectAnimator.ofFloat(mImageButton, "scaleX", 1f),
                ObjectAnimator.ofFloat(mImageButton, "scaleY", 1f),
                ObjectAnimator.ofFloat(mTitle, "alpha", 1f)
        );

    }

    @Override
    public void onCenterPosition(boolean animate) {
        mImageButton.setScaleX(mChosenCircleScale);
        mImageButton.setScaleY(mChosenCircleScale);
        mTitle.setAlpha(1f);
        ((GradientDrawable) mImageButton.getBackground()).setColor(mSelectedCircleColor);
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        mImageButton.setScaleX(1f);
        mImageButton.setScaleY(1f);
        mTitle.setAlpha(mFadedTextAlpha);
        ((GradientDrawable) mImageButton.getBackground()).setColor(mFadedCircleColor);
    }

    public void setListSelectedColor(int listSelectedColor) {
        this.mSelectedCircleColor = listSelectedColor;
    }
}