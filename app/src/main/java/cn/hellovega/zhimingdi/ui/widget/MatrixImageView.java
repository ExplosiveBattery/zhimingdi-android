package cn.hellovega.zhimingdi.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import cn.hellovega.zhimingdi.ui.activity.ImageActivity;


/**
 * Created by Battery on 2017/9/22.
 */

public class MatrixImageView extends AppCompatImageView {

    private GestureDetector mGestureDetector;
    /**   最大（双击就会放到最大）、最小缩放级别*/
    private float mMaxScale=2.0f;
    private float mMinScale=0.85f;
    /** 限制setImageDrawable()只执行一次*/
    private int limitSetImageDrawable=1;

    public MatrixImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        MatrixTouchListener mListener=new MatrixTouchListener();
        setOnTouchListener(mListener);
        mGestureDetector=new GestureDetector(getContext(), new GestureListener(mListener));
        //背景设置为black   设置背景是图片
        setBackgroundColor(Color.BLACK);
        //将缩放类型设置为MATRIX，并放大到指定位置查看
        setScaleType(ScaleType.MATRIX);


    }


    //这个函数会被3次调用
    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if(limitSetImageDrawable==1) {
            Matrix initMatrix = new Matrix();
            initMatrix.set(getImageMatrix());
            //对窗口放大到最大倍数显示
            initMatrix.postScale(mMaxScale, mMaxScale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(initMatrix);
            --limitSetImageDrawable;
        }
    }


    public class MatrixTouchListener implements OnTouchListener{

        private int mMode = 0;
        /** 拖拉照片模式 */
        private static final int MODE_DRAG = 1;
        /** 放大缩小照片模式 */
        private static final int MODE_ZOOM = 2;
        /**   Matrix、float数组缓存*/
        private Matrix mCurMatrix = new Matrix();
        float[] mValues = new float[9];
        /**  缩放开始时的手指间距 */
        private float mDis;
        /** 用于记录开始时候的坐标位置 */
        private PointF startPoint = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    //设置拖动模式
                    mMode=MODE_DRAG;
                    startPoint.set(event.getX(), event.getY());
                    mCurMatrix.set(getImageMatrix());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //去除因为大小与位置不对产生的黑边
                    clearBlank();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mMode == MODE_ZOOM) {
                        setZoomMatrix(event);
                    }else if (mMode==MODE_DRAG) {
                        setDragMatrix(event);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    //设置缩放模式
                    mMode=MODE_ZOOM;
                    mDis = distance(event);
                    break;
                default:
                    break;
            }
            return mGestureDetector.onTouchEvent(event);
        }

        //重新设置图片，将因为大小变小了漏出背景去除
        private  void clearBlank() {
            mCurMatrix.getValues(mValues);
            if(mValues[Matrix.MSCALE_X]<1.0f) {
                mCurMatrix.set(getMatrix());
                setImageMatrix(mCurMatrix);
            }
        }

        //在一段移动中，这一个函数不是一直被调用，也是消息机制，过一会就会被调用一次
        private void setDragMatrix(MotionEvent event) {
            float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
            float dy = event.getY() - startPoint.y; // 得到y轴的移动距离
            //避免和双击冲突,大于10f才算是拖动
            if(Math.sqrt(dx*dx+dy*dy)>10f){
                startPoint.set(event.getX(), event.getY());
                mCurMatrix.getValues(mValues);
                if(mValues[Matrix.MTRANS_X]+dx>0 || mValues[Matrix.MTRANS_X]+dx<getWidth()*(1-mValues[Matrix.MSCALE_X]))
                    dx =0;
                if(mValues[Matrix.MTRANS_Y]+dy>0 || mValues[Matrix.MTRANS_Y]+dy<getHeight()*(1-mValues[Matrix.MSCALE_Y]))
                    dy =0;
                mCurMatrix.postTranslate(dx, dy);
                setImageMatrix(mCurMatrix);
            }
        }





        /**
         *  设置缩放Matrix
         *  @param event
         */
        private void setZoomMatrix(MotionEvent event) {
            if(event.getPointerCount()<2) return;
            float dis = distance(event);
            if (dis > 10f) {
                // 得到缩放倍数
                float scale = dis / mDis;
                mDis=dis;
                //限制不能超过最小与最大的倍数
                mCurMatrix.getValues(mValues);
                if(scale*mValues[Matrix.MSCALE_X]>mMaxScale)
                    scale=mMaxScale/mValues[Matrix.MSCALE_X];
                else if(scale*mValues[Matrix.MSCALE_X]<mMinScale)
                    scale = mMinScale / mValues[Matrix.MSCALE_X];
                //开始变换
                mCurMatrix.postScale(scale, scale,getWidth()/2,getHeight()/2);
                setImageMatrix(mCurMatrix);

            }
        }



        /**
         *  计算两个手指间的距离，输出结果保证不为负
         *  @param event
         *  @return
         */
        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            return (float) Math.sqrt(dx*dx + dy*dy);
        }

        /**
         *   双击时触发
         */
        public void onDoubleClick(){
            mCurMatrix.getValues(mValues);
            if(mValues[Matrix.MSCALE_X]!=1.0f) {
                mCurMatrix.set(getMatrix());
                setImageMatrix(mCurMatrix);
            }else {
                mCurMatrix.postScale(mMaxScale,mMaxScale);
                setImageMatrix(mCurMatrix);
            }
        }
    }

    private class  GestureListener extends GestureDetector.SimpleOnGestureListener{
        private final MatrixTouchListener listener;
        public GestureListener(MatrixTouchListener listener) {
            this.listener=listener;
        }
        @Override
        public boolean onDown(MotionEvent e) {
            //捕获Down事件
            return true;
        }
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //触发双击事件
            listener.onDoubleClick();
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            ((ImageActivity)getContext()).changeButtonState();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // TODO Auto-generated method stub

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub
            super.onShowPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // TODO Auto-generated method stub
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // TODO Auto-generated method stub
            return super.onSingleTapConfirmed(e);
        }

    }

}

