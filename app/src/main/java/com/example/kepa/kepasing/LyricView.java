package com.example.kepa.kepasing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by ASUS on 2017/10/26.
 */

public class LyricView extends View implements ILrcView {
    private List<LrcRow> mLrcRows;
    private Paint mPaint;
    private String mLoadingLrcTip = "暂无歌词";
    private int mHighlight = getResources().getColor(R.color.colorSpringRed);
    private int mOrdinary = Color.WHITE;
    private int mLrcFontSize = 30;
    private int mHighlightFontSize = 35;
    private int mHighlightRow = 0;
    private int mPaddingY = 30;
    private int mPaddingcurrentY = 35;
    private ILrcViewListener mLrcViewListener;
    //拖动歌词
    public final static int DISPLAY_MODE_NORMAL = 0;//正常播放
    public final static int DISPLAY_MODE_SEEK = 1;//拖动歌词
    private int mDisplayMode = DISPLAY_MODE_NORMAL;//歌词模式
    private int mMinSeekOffset = 25;//最小拖动距离
    private int mSeekLineColor = getResources().getColor(R.color.colorSpringYellow);
    private int mSeekLineTextColor = getResources().getColor(R.color.colorSpringOrange);
    private int mSeekLineTextSize = 15;
    private int mSeekLinePaddingX = 0;

    public LyricView(Context context, AttributeSet attr){
        super(context, attr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mLrcFontSize);
    }

    public void setListener(ILrcViewListener l)
    {
        mLrcViewListener = l;
    }

    public void setLoadingTipText(String text){
        mLoadingLrcTip = text;
    }

    //draw the canvas
    @Override
    protected void onDraw(Canvas canvas){
        final int height = getHeight();
        final int width = getWidth();
        if(mLrcRows == null ||mLrcRows.size()==0){
            if(mLoadingLrcTip!=null){
                mPaint.setColor(mHighlight);
                mPaint.setTextSize(mLrcFontSize);
                mPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(mLoadingLrcTip,width/2,height/2-mHighlightFontSize,mPaint);
            }
            return;
        }
        int rowY;
        final int rowX = width/2;
        int rowNum;

        //当前歌词
        String highlightText = mLrcRows.get(mHighlightRow).content;
        int highlightRowY = height/2-mLrcFontSize;
        mPaint.setColor(mHighlight);
        mPaint.setTextSize(mHighlightFontSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(highlightText,rowX,highlightRowY,mPaint);

        if(mDisplayMode==DISPLAY_MODE_SEEK){
            mPaint.setColor(mSeekLineColor);
            canvas.drawLine(mSeekLinePaddingX,highlightRowY+mPaddingcurrentY,width-mSeekLinePaddingX,highlightRowY+mPaddingcurrentY,mPaint);
            mPaint.setColor(mSeekLineTextColor);
            mPaint.setTextSize(mSeekLineTextSize);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(mLrcRows.get(mHighlightRow).strTime,0,highlightRowY,mPaint);
        }

        //前面的歌词
        mPaint.setColor(mOrdinary);
        mPaint.setTextSize(mLrcFontSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        rowNum = mHighlightRow-1;
        rowY = highlightRowY-mPaddingcurrentY-mLrcFontSize;

        while(rowY>-mLrcFontSize && rowNum>=0)
        {
            String text = mLrcRows.get(rowNum).content;
            canvas.drawText(text,rowX,rowY,mPaint);
            rowY -=(mPaddingY+mLrcFontSize);
            rowNum--;
        }

        //后面的歌词
        rowNum = mHighlightRow+1;
        rowY = highlightRowY+mPaddingcurrentY+mLrcFontSize;

        while(rowY<height && rowNum<mLrcRows.size()){
            String text = mLrcRows.get(rowNum).content;
            canvas.drawText(text,rowX,rowY,mPaint);
            rowY+=(mPaddingY+mLrcFontSize);
            rowNum++;
        }
    }

    public void seekLrc(int position, boolean cb){
        if(mLrcRows == null || position < 0 || position>mLrcRows.size()){
            return;
        }
        LrcRow lrcRow = mLrcRows.get(position);
        mHighlightRow = position;
        invalidate();
        if(mLrcViewListener!=null && cb)
        {
            mLrcViewListener.onLrcSeeked(position,lrcRow);
        }
    }

    private float mLastMotionY;
    private boolean mIsFirstMove = false;

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(mLrcRows == null||mLrcRows.size()==0){
            return super.onTouchEvent(event);
        }
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getY();
                mIsFirstMove = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                doSeek(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                if(mDisplayMode==DISPLAY_MODE_SEEK){
                    seekLrc(mHighlightRow,true);
                }
                mDisplayMode = DISPLAY_MODE_NORMAL;
                invalidate();
                break;
        }
        return true;
    }

    private void doSeek(MotionEvent event){
        float y = event.getY();
        float offsetY = y-mLastMotionY;
        if(Math.abs(offsetY)<mMinSeekOffset){
            return;
        }
        mDisplayMode = DISPLAY_MODE_SEEK;
        int rowOffset = Math.abs((int)offsetY/mLrcFontSize);
        if(offsetY<0){
            mHighlightRow+=rowOffset;
        }
        else if(offsetY>0){
            mHighlightRow-=rowOffset;
        }
        mHighlightRow = Math.max(0,mHighlightRow);
        mHighlightRow = Math.min(mHighlightRow,mLrcRows.size()-1);
        if(rowOffset>0){
            mLastMotionY = y;
            invalidate();
        }
    }

    public void setLrc(List<LrcRow> lrcRows){
        mLrcRows = lrcRows;
        invalidate();
    }

    public void seekLrcToTime(long time){
        if(mLrcRows == null || mLrcRows.size()==0){
            return;
        }
        for(int i = 0;i<mLrcRows.size();i++){
            LrcRow current = mLrcRows.get(i);
            LrcRow next = null;
            if((i+1)!=mLrcRows.size())
            {
                next = mLrcRows.get(i+1);
            }
            if((time>=current.time && next!=null && time<next.time) || (time>current.time&& next==null)){
                seekLrc(i,false);
                return;
            }
        }
    }

}
