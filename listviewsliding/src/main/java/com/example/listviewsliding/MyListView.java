package com.example.listviewsliding;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by taixiang on 2015/11/7.
 */
public class MyListView extends ListView implements View.OnTouchListener ,GestureDetector.OnGestureListener{


    /**
     * 手势识别类
     */
    private GestureDetector gestureDetector;

    /**
     * 滑动时出现的按钮
     */
    private View btnDelete;

    /**
     * listview的每一个item的布局
     */
    private ViewGroup viewGroup;
    /**
     * 选中的项
     */
    private int selectedItem;

    /**
     * 是否已经显示删除按钮
     */
    private boolean isDeleteShow;

    /**
     * 点击删除按钮时删除每一行的事件监听器
     */
    private OnItemDeleteListener onItemDeleteListener;

    /**
     * 构造函数，初始化手势监听器等
     * @param context
     * @param attrs
     */
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(getContext(),this);
        setOnTouchListener(this);
    }


    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //得到当前触摸的条目
        selectedItem = pointToPosition((int)event.getX(), (int)event.getY());
        //如果删除按钮已经显示，那么隐藏按钮，异常按钮在当前位置的绘制
        if (isDeleteShow){
            btnHide(btnDelete);
            viewGroup.removeView(btnDelete);
            btnDelete = null;
            isDeleteShow = false;
            return false;
        }
        else{
//            如果按钮没显示，则触发手势事件
//            由此去触发GestureDetector的事件，可以查看其源码得知，onTouchEvent中进行了手势判断，调用onFling
            return gestureDetector.onTouchEvent(event);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //得到当前触摸的条目
        if (!isDeleteShow) {
            selectedItem = pointToPosition((int)e.getX(), (int)e.getY());
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * 滑动删除的主要响应方法。
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        //如果删除按钮没有显示，并且手势滑动符合我们的条件
        //此处可以根据需要进行手势滑动的判断，如限制左滑还是右滑，我这里是左滑右滑都可以
        Log.i("》》》》  e1.getX()","  "+e1.getX());
        Log.i("》》》》  e2.getX()","  "+e2.getX());
        Log.i("》》》》  velocityX","  "+velocityX);
        Log.i("》》》》  velocityY","  "+velocityY);
        if (!isDeleteShow && Math.abs(velocityX) > Math.abs(velocityY) ) {
            if(e1.getX() > e2.getX()){
                //在当前布局上，动态添加我们的删除按钮，设置按钮的各种参数、事件，按钮的点击事件响应我们的删除项监听器
                btnDelete = LayoutInflater.from(getContext()).inflate(R.layout.layout_button, null);
                btnDelete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //btnHide(btnDelete);
                        viewGroup.removeView(btnDelete);
                        btnDelete = null;
                        isDeleteShow = false;
                        onItemDeleteListener.onItemDelete(selectedItem);
                    }
                });
                viewGroup = (ViewGroup)getChildAt(selectedItem - getFirstVisiblePosition());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                btnDelete.setLayoutParams(layoutParams);
                if(viewGroup != null &&btnDelete != null ){
                    viewGroup.addView(btnDelete);
                    btnShow(btnDelete);
                }
                isDeleteShow = true;

            }else if(e2.getX() > e1.getX()){
                if(viewGroup != null &&btnDelete != null ){
                    btnHide(btnDelete);
                    viewGroup.removeView(btnDelete);
                }
                btnDelete = null;
                isDeleteShow = false;
            }
        }else{
            setOnTouchListener(this);
        }
        return false;
    }

    /**
     * @类名称: OnItemDeleteListener
     * @描述: 删除按钮监听器
     * @throws
     * @author 徐纪伟
     * 2014年11月9日上午11:25:37
     */
    public interface OnItemDeleteListener{
        public void onItemDelete(int selectedItem);
    }

    /**
     * @方法名称: btnShow
     * @描述: 按钮显示时的动画
     * @param   @param v
     * @return void
     * @throws
     * @author 徐纪伟
     * 2014年11月9日 上午11:25:12
     */
    private void btnShow(View v){
        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_show));
    }

    /**
     * @方法名称: btnHide
     * @描述: 按钮隐藏时的动画
     * @param   @param v
     * @return void
     * @throws
     * @author 徐纪伟
     * 2014年11月9日 上午11:25:23
     */
    private void btnHide(View v){
        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.btn_hide));
    }
}
