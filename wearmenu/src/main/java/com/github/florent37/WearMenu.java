package com.github.florent37;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * Created by florentchampigny on 31/03/15.
 */
public class WearMenu extends FrameLayout implements View.OnClickListener {

    private static final int POSITION_TOP_LEFT = 0;
    private static final int POSITION_TOP_RIGHT = 1;
    private static final int POSITION_BOTTOM_LEFT = 2;
    private static final int POSITION_BOTTOM_RIGHT = 3;

    private View menuView;
    private GridViewPager mGridViewPager;
    private int position = POSITION_TOP_LEFT;

    private int mListSelectedColor = Color.parseColor("#2955C5");

    private int duration = 500;
    private boolean open = false;
    private boolean animating = false;

    private boolean enableClick = true;
    private boolean previousDown = false; //used for click detection

    private WearMenuListener mWearMenuListener;

    public WearMenu(Context context) {
        super(context);
    }

    protected View findViewByClass(View view, Class classToSearch) {
        if (view.getClass().equals(classToSearch)) {
            return view;
        }

        if (view instanceof ViewGroup) {
            final int len = getChildCount();

            for (int i = 0; i < len; i++) {
                View v = getChildAt(i);

                View result = findViewByClass(v, classToSearch);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private GridViewPager findGridViewPager() {
        GridViewPager viewPager = (GridViewPager) findViewByClass(this, GridViewPager.class);
        return viewPager;
    }

    private void handleAttributes(Context context, AttributeSet attrs) {
        setClickable(true);
        setOnClickListener(this);
        try {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.WearMenu);
            {
                int viewLayoutId = styledAttrs.getResourceId(R.styleable.WearMenu_layout, -1);
                if (viewLayoutId != -1) {
                    setContentView(viewLayoutId);
                }
            }
            {
                int position = styledAttrs.getInt(R.styleable.WearMenu_position, -1);
                if (position != -1)
                    this.position = position;

            }
            {
                int color = styledAttrs.getColor(R.styleable.WearMenu_selectedColor, -1);
                if (color != -1)
                    this.mListSelectedColor = color;

            }
            styledAttrs.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setContentView(int layoutId){
        if(menuView != null){
            menuView.setVisibility(GONE);
            removeView(menuView);
            menuView = null;
        }

        menuView = LayoutInflater.from(getContext()).inflate(layoutId, this, false);
        menuView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        menuView.setVisibility(GONE);
        addView(menuView);
    }

    public WearMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(context, attrs);
    }

    public WearMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttributes(context, attrs);
    }

    public WearMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        handleAttributes(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (menuView != null)
            menuView.bringToFront();

        mGridViewPager = findGridViewPager();
        if (mGridViewPager != null) {
            mGridViewPager.setClickable(true);
            mGridViewPager.setOnClickListener(this);
        }
    }

    private int getPositionX() {
        switch (position) {
            case POSITION_TOP_LEFT:
            case POSITION_BOTTOM_LEFT:
                return 0;
            case POSITION_TOP_RIGHT:
            case POSITION_BOTTOM_RIGHT:
                return getWidth();
        }
        return 0;
    }

    private int getPositionY() {
        switch (position) {
            case POSITION_TOP_LEFT:
            case POSITION_TOP_RIGHT:
                return 0;
            case POSITION_BOTTOM_LEFT:
            case POSITION_BOTTOM_RIGHT:
                return getHeight();
        }
        return 0;
    }


    public void toggle() {
        if (!animating) {
            final int radius = (int)Math.sqrt(Math.pow(this.getWidth(),2)+Math.pow(this.getHeight(),2));
            if (open) {
                Animator animator = ViewAnimationUtils.createCircularReveal(menuView, getPositionX(), getPositionY(), radius, 0).setDuration(duration);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        menuView.setVisibility(INVISIBLE);
                        open = !open;
                        animating = false;
                    }
                });
                animating = true;
                enableClick = true;
                animator.start();
            } else {
                Animator animator = ViewAnimationUtils.createCircularReveal(menuView, getPositionX(), getPositionY(), 0, radius).setDuration(duration);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        open = !open;
                        animating = false;
                    }
                });
                animating = true;
                enableClick = false;
                menuView.setVisibility(VISIBLE);
                animator.start();
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (enableClick)
            toggle();
    }

    public WearMenuListener getWearMenuListener() {
        return mWearMenuListener;
    }

    public void setWearMenuListener(WearMenuListener mWearMenuListener) {
        this.mWearMenuListener = mWearMenuListener;
    }

    public void enableOpening() {
        enableClick = true;
    }

    public void disableOpening() {
        enableClick = false;
    }

    public boolean isClickEnabled() {
        if(mGridViewPager != null)
            return enableClick && mGridViewPager.getCurrentItem().x == 0;
        else
            return enableClick;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousDown = true;
                break;
            case MotionEvent.ACTION_UP:
                if (previousDown && isClickEnabled()) {
                    toggle();
                    return true;
                }
            default:
                previousDown = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setMenuElements(String[] titles){
        if(titles != null)
            setMenuElements(Arrays.asList(titles));
    }

    public void setMenuElements(String[] titles, Drawable[] drawables){
        if(titles != null)
            setMenuElements(Arrays.asList(titles),Arrays.asList(drawables));
    }

    public void setMenuElements(List<String> titles) {
        this.setMenuElements(titles,null);
    }


    public void setMenuElements(List<String> titles, List<Drawable> drawables){
        setContentView(R.layout.wearmenu_list);

        WearableListView listView = (WearableListView) findViewById(R.id.wearmenu_listview);
        listView.setAdapter(new WearMenuListListViewAdapter(getContext(), titles, drawables, mListSelectedColor));
        listView.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                if(mWearMenuListener != null)
                    mWearMenuListener.onWearMenuListClicked(viewHolder.getPosition());
                toggle();
            }

            @Override
            public void onTopEmptyRegionClick() {
                toggle();
            }
        });
    }

    public interface WearMenuListener{
        public void onWearMenuListClicked(int position);
    }
}
