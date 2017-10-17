package sushchak.bohdan.curabitur.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import sushchak.bohdan.curabitur.R;

public class CollapsingMoveBehavior extends CoordinatorLayout.Behavior<View> {
    protected final static int X = 0;
    protected final static int Y = 1;

    protected int mTargetId;
    private int[] mView;
    private int[] mTarget;

    public CollapsingMoveBehavior() {
    }

    public CollapsingMoveBehavior(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CollapsingImageBehavior);
            mTargetId = a.getResourceId(R.styleable.CollapsingImageBehavior_collapsedTarget, 0);
            a.recycle();
        }

        if (mTargetId == 0) {
            throw new IllegalStateException("collapsedTarget attribute not specified on view for behavior");
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        setup(parent, child);

        AppBarLayout appBarLayout = (AppBarLayout) dependency;

        int range = appBarLayout.getTotalScrollRange();
        float factor = -appBarLayout.getY() / range;

        int left = mView[X] + (int) (factor * (mTarget[X] - mView[X]));
        int top = mView[Y] + (int) (factor * (mTarget[Y] - mView[Y]));

        child.setX(left);
        child.setY(top);

        return true;
    }

    private void setup(CoordinatorLayout parent, View child) {

        if (mView != null) return;

        mView = new int[2];
        mTarget = new int[2];

        mView[X] = (int) child.getX();
        mView[Y] = (int) child.getY();


        View target = parent.findViewById(mTargetId);
        if (target == null) {
            throw new IllegalStateException("target view not found");
        }

        View view = target;
        while (view != parent) {
            mTarget[X] += (int) view.getX();
            mTarget[Y] += (int) view.getY();
            view = (View) view.getParent();
        }
    }
}
