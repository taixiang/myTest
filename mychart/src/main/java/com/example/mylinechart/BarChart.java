package com.example.mylinechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by taixiang on 2015/11/19.
 */
public class BarChart extends View {

    private final int barsNum = 5;
    Map<String, Integer> PositionLengths = new HashMap<>();
    //边线颜色
    protected int mBorderColor;
    public static final int DEFAULT_BORDER_COLOR = Color.GRAY;
    //水平线绘制
    private Paint linePaint;
    //绘制字符串
    private Paint titlePaint;
    //绘制字符串
    private Paint barsPaint;
    //控件宽高
    private int viewHight;
    private int viewWidth;
    //字体柱形距离边的宽度
    private int margin;
    //柱体率
    private float barRate;
    //柱体最大数值
    private float barBiggest;
    private float positive_num;
    private float negetive_num;
    //柱体之间的间距
    private float dataSpacing;
    //柱体位置
    private float dataPosition;
    //柱体位置
    private float textPosition;
    //柱体高度
    private float perBarHeight;

    private List<Float> data = new ArrayList<>();

    public BarChart(Context context) {
        super(context);
        init();
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint();
        titlePaint = new Paint();
        barsPaint = new Paint();
        margin = 100;
        barBiggest = 0;
        mBorderColor = DEFAULT_BORDER_COLOR;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewHight = getHeight();
        viewWidth = getWidth();
        barRate = (float) (0.5 * viewHight / barBiggest);
        dataSpacing = (float) (viewWidth - 2 * margin) / 9;

        drawBars(canvas);

        drawTitles(canvas);
    }

    private void drawBars(Canvas canvas) {
        //绘制水平线
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.BLACK);
        canvas.drawLine(30, viewHight - negetive_num * barRate - 100, viewWidth - 30, viewHight - negetive_num * barRate - 100, titlePaint);
        //绘制标注
        Rect r = new Rect();
        String dataText;
        titlePaint.setTextSize(35);
        titlePaint.setColor(Color.BLACK);
        //绘制柱体
        barsPaint.setStrokeWidth((viewWidth - 2 * margin) / 9);
        barsPaint.setColor(Color.RED);
        for (int i = 0; i < data.size(); i++) {
            dataPosition = (float) (1.5 * margin + dataSpacing * 2 * i);
            textPosition = margin + dataSpacing * 2 * i;
            perBarHeight = data.get(i) * barRate;
            dataText = data.get(i).toString();
            titlePaint.getTextBounds(dataText, 0, dataText.length(), r);
            if (perBarHeight > 0) {
                canvas.drawText(dataText, (float) (dataPosition - 0.5 * r.width()), viewHight - negetive_num * barRate - 110 - perBarHeight, titlePaint);
            } else {
                canvas.drawText(dataText, (float) (dataPosition - 0.5 * r.width()), viewHight - negetive_num * barRate - 110, titlePaint);
            }
            canvas.drawLine(dataPosition, viewHight - negetive_num * barRate - 100, dataPosition, viewHight - negetive_num * barRate - 100 - perBarHeight, barsPaint);
        }
    }
    String first_year ;
    String second_year ;
    String third_year ;
    String fourth_year ;
    String fifth_year ;

    private void drawTitles(Canvas canvas) {
        int fontHeight;
        Rect r = new Rect();
        String titile = "每股收益一致预期（EPS）";
        String date = "统计截止日2015-11-18";

        titlePaint.setTextSize(40);
        titlePaint.setColor(Color.GRAY);


        titlePaint.getTextBounds(first_year, 0, first_year.length(), r);
        titlePaint.getTextBounds(second_year, 0, second_year.length(), r);
        titlePaint.getTextBounds(third_year, 0, third_year.length(), r);
        titlePaint.getTextBounds(fourth_year, 0, fourth_year.length(), r);
        titlePaint.getTextBounds(fifth_year, 0, fifth_year.length(), r);
        titlePaint.getTextBounds(titile, 0, titile.length(), r);
        titlePaint.getTextBounds(date, 0, date.length(), r);
        fontHeight = r.height();

        canvas.drawText(first_year, margin, viewHight - fontHeight, titlePaint);
        canvas.drawText(second_year, margin + 2 * dataSpacing, viewHight - fontHeight, titlePaint);
        canvas.drawText(third_year, margin + 4 * dataSpacing, viewHight - fontHeight, titlePaint);
        canvas.drawText(fourth_year, margin + 6 * dataSpacing, viewHight - fontHeight, titlePaint);
        canvas.drawText(fifth_year, margin + 8 * dataSpacing, viewHight - fontHeight, titlePaint);
        titlePaint.setTextSize(50);
        canvas.drawText(titile, 0, fontHeight * 2, titlePaint);
        titlePaint.setTextSize(35);
        canvas.drawText(date, (float) (viewWidth - r.width()), fontHeight * 3, titlePaint);
    }

    public void setBarsData(ArrayList<Float> list,String first,String second,String third,String fourth,String fifth) {
        if (list.size() > 0) {
            this.data.addAll(list);
            this.first_year = first;
            this.second_year = second;
            this.third_year = third;
            this.fourth_year = fourth;
            this.fifth_year = fifth;
            for (int i = 0; i < list.size(); i++) {
                float num = list.get(i);
                if (num > 0) {
                    positive_num = positive_num > num ? positive_num : num;
                } else {
                    negetive_num = negetive_num > Math.abs(num) ? negetive_num : Math.abs(num);
                }
            }
            barBiggest = positive_num + negetive_num;
        }

    }

}
