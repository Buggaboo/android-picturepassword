package com.android.facelock;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PicturePasswordView extends ImageView
{
	private int mSeed;
	
	private final boolean DEBUG = false;
	
	private float mScrollX = 0;
	private float mScrollY = 0;
	
	private float mFingerX;
	private float mFingerY;
	
	private static final int GRID_SIZE = 10;
	private static final int FONT_SIZE = 20;
	
	private Rect mTextBounds;
	
	private Paint mPaint;
	
	private int getNumberForXY( int x, int y )
	{
		// TODO: bad
		
		return Math.abs( ( ( mSeed ^ ( ( x ^ 0x7FAF9385 ) + 32 * 0x445FEED ) ) * ( y + 1 * 0x0F00B48F ) ) % 10 );
	}

	public PicturePasswordView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		
		Random rnd = new Random();
		mSeed = rnd.nextInt();
		
		final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		final float shadowOff = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics );
		
		mPaint = new Paint( Paint.LINEAR_TEXT_FLAG );
		
		mPaint.setColor( Color.WHITE );
		
		mPaint.setShadowLayer( 10, shadowOff, shadowOff, Color.BLACK );
		mPaint.setTextSize( TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, FONT_SIZE, displayMetrics ) );
		
		mPaint.setAntiAlias( true );
		
		mTextBounds = new Rect();
		mPaint.getTextBounds( "8", 0, 1, mTextBounds );
	}

	@Override
	protected void onDraw( Canvas canvas )
	{
		super.onDraw( canvas );
		
		final float cellSize = canvas.getWidth() / GRID_SIZE;
		
		float drawX = -cellSize / 1.5F;
		
		for ( int x = -1; x < GRID_SIZE + 1; x++ )
		{
			float drawY = -mTextBounds.bottom + cellSize / 1.5F - cellSize;
			
			for ( int y = -1; y < GRID_SIZE + 1; y++ )
			{
				if ( DEBUG )
				{
					if ( x == -1 || y == -1 || x == GRID_SIZE || y == GRID_SIZE )
					{
						mPaint.setColor( Color.RED );
					}
					else
					{
						mPaint.setColor( Color.WHITE );
					}
				}
				
				int cellX = ( int ) ( x - mScrollX / cellSize );
				int cellY = ( int ) ( y - mScrollY / cellSize );
				
				if ( mScrollX / cellSize <= 0 && cellX != 0 ) cellX--;
				if ( mScrollY / cellSize <= 0 && cellY != 0 ) cellY--;
			
				Integer number = getNumberForXY( cellX, cellY );
				
				canvas.drawText( number.toString(), drawX + mScrollX % cellSize, drawY + mScrollY % cellSize, mPaint );
				drawY += cellSize;
			}
			
			drawX += cellSize;
		}
		
		if ( DEBUG )
		{
			canvas.drawText( mScrollX / cellSize + "," + mScrollY / cellSize, 0, mTextBounds.bottom * 26.5f, mPaint );
		}
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		float x = event.getX();
		float y = event.getY();
		
		
		switch( event.getAction() )
		{
			case MotionEvent.ACTION_DOWN:
				mFingerX = x;
				mFingerY = y;
				break;
				
			case MotionEvent.ACTION_MOVE:
				float diffx = x - mFingerX;
				float diffy = y - mFingerY;

				mScrollX += diffx;
				mScrollY += diffy;
				
				mFingerX = x;
				mFingerY = y;
				
				invalidate();
				break;
		}

		return true; // super.onTouchEvent( event );
	}
}
