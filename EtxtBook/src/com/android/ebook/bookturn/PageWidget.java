    package com.android.ebook.bookturn;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class PageWidget extends View {

	@SuppressWarnings("unused")
	private static final String TAG = "Book_Turn";
	private int mWidth = 0;
	private int mHeight = 0;
	private int mCornerX = 0; // ����I���������}
	private int mCornerY = 0;
	private Path mPath0;
	private Path mPath1;
	Bitmap mCurPageBitmap = null; // ��e��
	Bitmap mNextPageBitmap = null;

	//PointF:PointF holds two float coordinates
	PointF mTouch = new PointF(); // // ����I
	public PointF getmTouch() {
		return mTouch;
	}

	PointF mBezierStart1 = new PointF(); // ���뺸���u�_�l�I
	PointF mBezierControl1 = new PointF(); // ���뺸���u�����I
	PointF mBeziervertex1 = new PointF(); // ���뺸���u���I
	PointF mBezierEnd1 = new PointF(); // ���뺸���u�����I

	PointF mBezierStart2 = new PointF(); // �t�@�����뺸���u
	PointF mBezierControl2 = new PointF();
	PointF mBeziervertex2 = new PointF();
	PointF mBezierEnd2 = new PointF();

	float mMiddleX;
	float mMiddleY;
	float mDegrees;
	float mTouchToCornerDis;
	ColorMatrixColorFilter mColorMatrixFilter;
	Matrix mMatrix;
	float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };

	boolean mIsRTandLB; // �O�_�ݩ�k�W���U
	float mMaxLength = (float) Math.hypot(mWidth, mHeight);
	int[] mBackShadowColors;
	int[] mFrontShadowColors;
	GradientDrawable mBackShadowDrawableLR;
	GradientDrawable mBackShadowDrawableRL;
	GradientDrawable mFolderShadowDrawableLR;
	GradientDrawable mFolderShadowDrawableRL;

	GradientDrawable mFrontShadowDrawableHBT;
	GradientDrawable mFrontShadowDrawableHTB;
	GradientDrawable mFrontShadowDrawableVLR;
	GradientDrawable mFrontShadowDrawableVRL;

	Paint mPaint;

	Scroller mScroller;

	@SuppressLint("InlinedApi")
	public PageWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		/**   
		 * Paint������   
		 *    
		 * Paint�Y�e���A�bø�ϹL�{���_��F���䭫�n���@�ΡA�e���D�n�O�s�F�C��A   
		 * �˦���ø��H���A���w�F�p��ø��奻�M�ϧΡA�e���ﹳ���ܦh�]�m��k�A   
		 * �j��W�i�H���������A�@���P�ϧ�ø������A�@���P�奻ø������C          
		 *    
		 * 1.�ϧ�ø��   
		 * setARGB(int a,int r,int g,int b);   
		 * �]�mø��C��Aa�N��z���סAr�Ag�Ab�N���C��ȡC   
		 *    
		 * setAlpha(int a);   
		 * �]�mø��ϧΪ��z���סC   
		 *    
		 * setColor(int color);   
		 * �]�mø��C��A�ϥ��C��ȨӪ�ܡA���C��ȥ]�A�z���שMRGB�C��C   
		 *    
		 * setAntiAlias(boolean aa);   
		 * �]�m�O�_�ϥΧܿ����\��A�|���Ӹ��j�귽�Aø��ϧγt�׷|�ܺC�C   
		 *    
		 * setDither(boolean dither);   
		 * �]�w�O�_�ϥιϹ��ݰʳB�z�A�|��ø��X�Ӫ��Ϥ��C���[���ƩM�����A�Ϲ���[�M��   
		 *    
		 * setFilterBitmap(boolean filter);   
		 * �p�G�Ӷ��]�m��true�A�h�Ϲ��b�ʵe�i�椤�|�o����Bitmap�Ϲ����u�ƾާ@�A�[�����   
		 * �t�סA���]�m���̿��dither�Mxfermode���]�m   
		 *    
		 * setMaskFilter(MaskFilter maskfilter);   
		 * �]�mMaskFilter�A�i�H�Τ��P��MaskFilter��{�o�誺�ĪG�A�p�o�ơA���鵥       *    
		 * setColorFilter(ColorFilter colorfilter);   
		 * �]�m�C��L�o���A�i�H�bø���C��ɹ�{�����C�⪺�ܴ��ĪG   
		 *    
		 * setPathEffect(PathEffect effect);   
		 * �]�mø����|���ĪG�A�p�I�e�u��   
		 *    
		 * setShader(Shader shader);   
		 * �]�m�Ϲ��ĪG�A�ϥ�Shader�i�Hø��X�U�غ��ܮĪG   
		 *   
		 * setShadowLayer(float radius ,float dx,float dy,int color);   
		 * �b�ϧΤU���]�m���v�h�A���ͳ��v�ĪG�Aradius�����v�����סAdx�Mdy�����v�bx�b�My�b�W���Z���Acolor�����v���C��   
		 *    
		 * setStyle(Paint.Style style);   
		 * �]�m�e�����˦��A��FILL�AFILL_OR_STROKE�A��STROKE   
		 *    
		 * setStrokeCap(Paint.Cap cap);   
		 * ��e���˦���STROKE��FILL_OR_STROKE�ɡA�]�m���ꪺ�ϧμ˦��A�p��μ˦�   
		 * Cap.ROUND,�Τ�μ˦�Cap.SQUARE   
		 *    
		 * setSrokeJoin(Paint.Join join);   
		 * �]�mø��ɦU�ϧΪ����X�覡�A�p���ƮĪG��   
		 *    
		 * setStrokeWidth(float width);   
		 * ��e���˦���STROKE��FILL_OR_STROKE�ɡA�]�m���ꪺ�ʲӫ�   
		 *    
		 * setXfermode(Xfermode xfermode);   
		 * �]�m�ϧέ��|�ɪ��B�z�覡�A�p�X�áA���涰�Ψö��A�g�`�ΨӨ�@��֪������ĪG   
		 *    
		 * 2.�奻ø��   
		 * setFakeBoldText(boolean fakeBoldText);   
		 * ������{�����r�A�]�m�b�p�r��W�ĪG�|�D�`�t   
		 *    
		 * setSubpixelText(boolean subpixelText);   
		 * �]�m�Ӷ���true�A�N���U��奻�bLCD�̹��W����ܮĪG   
		 *       
		 * setTextScaleX(float scaleX);   
		 * �]�mø���rx�b���Y���ҡA�i�H��{��r���Ԧ����ĪG   
		 *    
		 * setTextSkewX(float skewX);   
		 * �]�m�����r�AskewX���ɱש���   
		 *    
		 * setTypeface(Typeface typeface);   
		 * �]�mTypeface�ﹳ�A�Y�r�魷��A�]�A����A����H��Ũ�u��A�DŨ�u�鵥   
		 *    
		 * setUnderlineText(boolean underlineText);   
		 * �]�m�a���U���u����r�ĪG   
		 *    
		 * setStrikeThruText(boolean strikeThruText);   
		 * �]�m�a���R���u���ĪG   
		 *    
		 */    
		setScreen(mWidth, mHeight);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mPath0 = new Path();//Path���|�ﹳ 
		mPath1 = new Path();
		createDrawable();

		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);

		//�C��x�}�]ColorMatrix�^�M�����ܴ��x�}�]Matrix�^�A��Ϥ��i���ܴ��A�H�Ԧ��A�ᦱ��
		ColorMatrix cm = new ColorMatrix();

		//�C��x�}�A�C��x�}�O�@��5x4 ���x�}�A�i�H�ΨӤ�K���ק�Ϥ���RGBA�U���q���ȡA�C��x�}�H�@���Ʋժ��覡�s�x�p�U�G
		// [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ]�A�L�q�LRGBA�|�ӳq�D�Ӫ����ާ@�����C��
		float array[] = { 0.55f, 0, 0, 0, 80.0f, 0,
			         	0.55f, 0, 0, 80.0f, 0, 0, 
				      0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
		cm.set(array);
		//�C���o��A�N��QQ���b�u�M���u�Ϥ��A�P�@�i�Ϥ��q�L�C���o��B�z�A��ܤ��P���ĪG�A�i��ֹϤ��귽�[�J
		mColorMatrixFilter = new ColorMatrixColorFilter(cm);
		mMatrix = new Matrix();
		mScroller = new Scroller(getContext());

		mTouch.x = 0.01f; // ����x,y��0,�_�h�b�I�p��ɷ|�����D
		mTouch.y = 0.01f;
	}

	/**
	 * �p�����I�����������
	 */
	public void calcCornerXY(float x, float y) {
		//�N����̹������|�ӹ����A�P�_������b���ӹ�����
		if (x <= mWidth / 2)
			mCornerX = 0;
		else
			mCornerX = mWidth;
		if (y <= mHeight / 2)
			mCornerY = 0;
		else
			mCornerY = mHeight;

		//�p�G������b�Ĥ@�����βĤT�����A�]�N�O�k�W���Υ��U��
		if ((mCornerX == 0 && mCornerY == mHeight)
				|| (mCornerX == mWidth && mCornerY == 0))
			mIsRTandLB = true;
		else
			mIsRTandLB = false;
	}

	public boolean doTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			/* Android���ѤFInvalidate�MpostInvalidate��k��{�ɭ���s�A���OInvalidate���ઽ���b�u�{���եΡA�]���L�O�H�I�F��u�{�ҫ��G
			 * Android UI�ާ@�ä��O�u�{�w�����A�åB�o�Ǿާ@�����bUI�u�{���եΡC 
			 * invalidate()���եάO�⤧�e���ª�view�q�DUI�u�{���C��pop��
			 * ��postInvalidate()�b�u�@�̽u�{���Q�ե�
			 */
			this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			// calcCornerXY(mTouch.x, mTouch.y);
			// this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			//�O�_Ĳ�o½��
			if (canDragOvear()) {
				startAnimation(1200);
				
			} else {
				mTouch.x = mCornerX - 0.09f;//�p�G����½���N��mTouch��^�S���R��ɪ����A
				mTouch.y = mCornerY - 0.09f;//- 0.09f�O����mTouch = 800 ��mTouch= 0 ,�b�o�ǭȮɷ|�X�{BUG
			}

			this.postInvalidate();		
		}
		// return super.onTouchEvent(event);
		return true;
	}

	/**
	 * �D�Ѫ��uP1P2�M���uP3P4�����I����
	 */
	public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// �G����Ƴq���G y=ax+b
		float a1 = (P2.y - P1.y) / (P2.x - P1.x);
		float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

		float a2 = (P4.y - P3.y) / (P4.x - P3.x);
		float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
		CrossP.x = (b2 - b1) / (a1 - a2);
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}

	private void calcPoints() {
		mMiddleX = (mTouch.x + mCornerX) / 2;
		mMiddleY = (mTouch.y + mCornerY) / 2;
		mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
				* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
		mBezierControl1.y = mCornerY;
		mBezierControl2.x = mCornerX;
		mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
				* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

		mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
				/ 2;
		mBezierStart1.y = mCornerY;

		// ��mBezierStart1.x < 0�Ϊ�mBezierStart1.x > 480��
		// �p�G�~��½���A�|�X�{BUG�G�b������
		if (mTouch.x > 0 && mTouch.x < mWidth) {
			if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
				if (mBezierStart1.x < 0)
					mBezierStart1.x = mWidth - mBezierStart1.x;

				float f1 = Math.abs(mCornerX - mTouch.x);
				float f2 = mWidth * f1 / mBezierStart1.x;
				mTouch.x = Math.abs(mCornerX - f2);

				float f3 = Math.abs(mCornerX - mTouch.x)
						* Math.abs(mCornerY - mTouch.y) / f1;
				mTouch.y = Math.abs(mCornerY - f3);

				mMiddleX = (mTouch.x + mCornerX) / 2;
				mMiddleY = (mTouch.y + mCornerY) / 2;

				mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
						* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
				mBezierControl1.y = mCornerY;

				mBezierControl2.x = mCornerX;
				mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
						* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

				mBezierStart1.x = mBezierControl1.x
						- (mCornerX - mBezierControl1.x) / 2;
			}
		}
		mBezierStart2.x = mCornerX;
		mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
				/ 2;

		mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
				(mTouch.y - mCornerY));

		mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
				mBezierStart2);
		mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
				mBezierStart2);

		/*
		 * mBeziervertex1.x ����
		 * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 ��²������
		 * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
		 */
		mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
		mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
		mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
		mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
	}

	private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
		mPath0.reset();
		mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
				mBezierEnd1.y);
		mPath0.lineTo(mTouch.x, mTouch.y);
		mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
				mBezierStart2.y);
		mPath0.lineTo(mCornerX, mCornerY);
		mPath0.close();
		canvas.save();
		canvas.clipPath(path, Region.Op.XOR);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.restore();
	}

	private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
		mPath1.reset();
		mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.lineTo(mCornerX, mCornerY);
		mPath1.close();

		mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
				- mCornerX, mBezierControl2.y - mCornerY));
		int leftx;
		int rightx;
		GradientDrawable mBackShadowDrawable;
		if (mIsRTandLB) {
			leftx = (int) (mBezierStart1.x);
			rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
			mBackShadowDrawable = mBackShadowDrawableLR;
		} else {
			leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
			rightx = (int) mBezierStart1.x;
			mBackShadowDrawable = mBackShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
				(int) (mMaxLength + mBezierStart1.y));
		mBackShadowDrawable.draw(canvas);
		canvas.restore();
	}

	public void setBitmaps(Bitmap bm1, Bitmap bm2) {
		mCurPageBitmap = bm1;
		mNextPageBitmap = bm2;
	}

	public void setScreen(int w, int h) {
		mWidth = w;
		mHeight = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//deawBackBg(canvas);	
		canvas.drawColor(0xFFAAAAAA);
		calcPoints();
		drawCurrentPageArea(canvas, mCurPageBitmap, mPath0);
		drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
		drawCurrentPageShadow(canvas);
		drawCurrentBackArea(canvas, mCurPageBitmap);
	
	}

	/**
	 * �Ыس��v��GradientDrawable
	 */
	private void createDrawable() {

		/*
		 * GradientDrawable ����ϥκ��ܦ��ø��ϧΡA�q�`�i�H�Χ@Button�άO�I���ϧΡC

		 * GradientDrawable���\���wø��ϧΪ������GLINE�AOVAL�ARECTANGLE�άORING �A�C�⺥�ܤ��LINEAR_GRADIENT�ARADIAL_GRADIENT �M SWEEP_GRADIENT�C

		 * �䤤�b�ϥ�RECTANGLE�]�x�Ρ^�A�٤��\�]�m�x�Υ|�Ө����ꨤ�A�C�Ӷꨤ���b�|�i�H���O�]�m�G

		 * public void setCornerRadii(float[] radii)

		 * radii �Ʋդ��O���w�|�Ӷꨤ���b�|�A�C�Ө��i�H���w[X_Radius,Y_Radius]�A�|�Ӷꨤ�����Ǭ����W�A�k�W�A�k�U�A���U�C�p�GX_Radius,Y_Radius��0����٬O�����C

		 * �C�⺥�ܪ���V��GradientDrawable.Orientation�w�q,�@�K��

		 * GradientDrawable���c�y��ơGpublic GradientDrawable(GradientDrawable.Orientation orientation, int[] colors)

		 * orientation���w�F���ܪ���V�A���ܪ��C���colors�Ʋի��w�A�Ʋդ����C�ӭȬ��@���C��C

		 * ���ҩw�q�@�Ӻ��ܤ�V�q�ե��W��k�U�A�����C�⬰���A��A�ŤT��G

		 * mDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[] { 0xFFFF0000, 0xFF00FF00,0xFF0000FF });

		 * ���O�ϥ�Liner,Radial �MSweep�T�غ��ܼҦ��A�åi�t�X���w�x�Υ|�Ө��ꨤ�b�|
		 * */

		int[] color = { 0x333333, 0x333333 };

		//�q�k�V�����C��0x333333���ܬ�0x333333
		mFolderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		mFolderShadowDrawableRL
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);//�u�ʺ��ܡA "radial"�G�|�V���ܡA  "sweep" �G���׺���

		mFolderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		mFolderShadowDrawableLR
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowColors = new int[] { 0xff111111, 0x111111 };
		mBackShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
		mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
		mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		//mFrontShadowColors = new int[] { 0x80111111, 0x111111 };
		mBackShadowColors = new int[] { 0xff111111, 0x111111 };
		mFrontShadowDrawableVLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mFrontShadowDrawableVLR
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mFrontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
		mFrontShadowDrawableVRL
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
		mFrontShadowDrawableHTB
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
		mFrontShadowDrawableHBT
		.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}

	/**
	 * ø��½�_�������v
	 */
	public void drawCurrentPageShadow(Canvas canvas) {
		double degree;
		//�p����I���s�u���ɱר�.
		//�٥i������
		if (mIsRTandLB) {
			degree = Math.PI
					/ 4
					- Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x
							- mBezierControl1.x);
		} else {
			degree = Math.PI
					/ 4
					- Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x
							- mBezierControl1.x);
		}
		// ½�_�����v���I�Ptouch�I���Z��
		double d1 = (float) 25 * 1.414 * Math.cos(degree);
		double d2 = (float) 25 * 1.414 * Math.sin(degree);
		float x = (float) (mTouch.x + d1);
		float y;
		if (mIsRTandLB) {
			y = (float) (mTouch.y + d2);
		} else {
			y = (float) (mTouch.y - d2);
		}
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
		mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.close();
		float rotateDegrees;
		canvas.save();
		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		int leftx;
		int rightx;
		GradientDrawable mCurrentPageShadow;
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl1.x);
			rightx = (int) mBezierControl1.x + 25;
			mCurrentPageShadow = mFrontShadowDrawableVLR;
		} else {
			leftx = (int) (mBezierControl1.x - 25);
			rightx = (int) mBezierControl1.x + 1;
			mCurrentPageShadow = mFrontShadowDrawableVRL;
		}

		rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
				- mBezierControl1.x, mBezierControl1.y - mTouch.y));
		canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
		mCurrentPageShadow.setBounds(leftx,
				(int) (mBezierControl1.y - mMaxLength), rightx,
				(int) (mBezierControl1.y));
		mCurrentPageShadow.draw(canvas);
		canvas.restore();
        try{
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);	
		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        canvas.save();
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl2.y);
			rightx = (int) (mBezierControl2.y + 25);
			mCurrentPageShadow = mFrontShadowDrawableHTB;
		} else {
			leftx = (int) (mBezierControl2.y - 25);
			rightx = (int) (mBezierControl2.y + 1);
			mCurrentPageShadow = mFrontShadowDrawableHBT;
		}
		rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
				- mTouch.y, mBezierControl2.x - mTouch.x));
		canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
		float temp;
		if (mBezierControl2.y < 0)
			temp = mBezierControl2.y - mHeight;
		else
			temp = mBezierControl2.y;

		int hmg = (int) Math.hypot(mBezierControl2.x, temp);
		if (hmg > mMaxLength)
			mCurrentPageShadow
			.setBounds((int) (mBezierControl2.x - 25) - hmg, leftx,
					(int) (mBezierControl2.x + mMaxLength) - hmg,
					rightx);
		else
			mCurrentPageShadow.setBounds(
					(int) (mBezierControl2.x - mMaxLength), leftx,
					(int) (mBezierControl2.x), rightx);

		mCurrentPageShadow.draw(canvas);
		canvas.restore();
	}

	/**
	 * ø��½�_���I��
	 */
	private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
		int i = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
		float f1 = Math.abs(i - mBezierControl1.x);
		int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
		float f2 = Math.abs(i1 - mBezierControl2.y);
		float f3 = Math.min(f1, f2);
		mPath1.reset();
		mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath1.close();
		GradientDrawable mFolderShadowDrawable;
		int left;
		int right;
		if (mIsRTandLB) {
			left = (int) (mBezierStart1.x - 1);
			right = (int) (mBezierStart1.x + f3 + 1);
			mFolderShadowDrawable = mFolderShadowDrawableLR;
		} else {
			left = (int) (mBezierStart1.x - f3 - 1);
			right = (int) (mBezierStart1.x + 1);
			mFolderShadowDrawable = mFolderShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		mPaint.setColorFilter(mColorMatrixFilter);

		float dis = (float) Math.hypot(mCornerX - mBezierControl1.x,
				mBezierControl2.y - mCornerY);
		float f8 = (mCornerX - mBezierControl1.x) / dis;
		float f9 = (mBezierControl2.y - mCornerY) / dis;
		mMatrixArray[0] = 1 - 2 * f9 * f9;
		mMatrixArray[1] = 2 * f8 * f9;
		mMatrixArray[3] = mMatrixArray[1];
		mMatrixArray[4] = 1 - 2 * f8 * f8;
		mMatrix.reset();
		mMatrix.setValues(mMatrixArray);
		mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
		mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
		canvas.drawBitmap(bitmap, mMatrix, mPaint);
		mPaint.setColorFilter(null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
				(int) (mBezierStart1.y + mMaxLength));
		mFolderShadowDrawable.draw(canvas);
		canvas.restore();
	}

	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			float x = mScroller.getCurrX();
			float y = mScroller.getCurrY();
			mTouch.x = x;
			mTouch.y = y;
			postInvalidate();
		}
	}

	public void startAnimation(int delayMillis) {
		int dx, dy;
		// dx ������V�ưʪ��Z���A�t�ȷ|�Ϻu�ʦV���u��
		// dy ������V�ưʪ��Z���A�t�ȷ|�Ϻu�ʦV�W�u��
		if (mCornerX > 0) {
			dx = -(int) (mWidth + mTouch.x);
		} else {
			dx = (int) (mWidth - mTouch.x + mWidth);
		}
		if (mCornerY > 0) {
			dy = (int) (mHeight - mTouch.y);
		} else {
			dy = (int) (1 - mTouch.y); // // ����mTouch.y�̲��ܬ�0
		}
		//Start scrolling by providing a starting point and the distance to travel.
		mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
				delayMillis);
	}

	public void abortAnimation() {
		if (!mScroller.isFinished()) {
			//����ʵe�A�PforceFinished(boolean)�ۤϡAScroller�u�ʨ�̲�x�Py��m�ɤ���ʵe�C
			mScroller.abortAnimation();
		}
	}

	public boolean canDragOvear() {
		//�]�m�}�l½��������
		//		if (mTouchToCornerDis > mWidth / 10)
		if (mTouchToCornerDis>mWidth/5)
			return true;
		return false;
	}

	/**
	 * �O�_�q����½�V�k��
	 */
	public boolean DragToRight() {
		if (mCornerX > 0)
			return false;
		return true;
	}

}
