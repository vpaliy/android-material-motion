package com.vpaliy.fabexploration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SeparatorView extends View {

  private Paint linePaint;

  public SeparatorView(Context context) {
    super(context);
    init();
  }

  public SeparatorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    linePaint = new Paint();
    linePaint.setAlpha(50);
    linePaint.setColor(Color.BLACK);
    linePaint.setStrokeWidth(10);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawLine(getWidth() / 10, 0, getWidth() / 10, getHeight(), linePaint);
  }
}
