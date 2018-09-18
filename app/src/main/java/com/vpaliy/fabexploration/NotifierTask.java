package com.vpaliy.fabexploration;

import android.content.Context;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("WeakerAccess")
public class NotifierTask extends TimerTask {
  private volatile int count;
  private Callback callback;
  private final TextView target;
  private final Context context;
  private final String message;

  private NotifierTask(Starter starter) {
    this.count = starter.count;
    this.target = starter.target;
    this.callback = starter.callback;
    this.context = target.getContext();
    this.message = target.getText().toString();
  }

  @Override
  public void run() {
    if (count < 0) return;
    if (count == 0) {
      cancel();
      if (callback != null) {
        callback.onFinished();
      }
    } else notifyTarget();
    count--;
  }

  private void notifyTarget() {
    target.post(() -> target.setText(buildNotification()));
  }

  private String buildNotification() {
    return message + "\n" + context.
            getString(R.string.disappear_message) + ":" + count;
  }

  public static class Starter {
    private final TextView target;
    private final int count;
    private long delay;
    private long period;
    private Callback callback;

    public Starter(final TextView target, final int count) {
      this.target = target;
      this.count = count;
    }

    public Starter setDelay(long delay) {
      this.delay = delay;
      return this;
    }

    public Starter setCallback(Callback callback) {
      this.callback = callback;
      return this;
    }

    public Starter setPeriod(long period) {
      this.period = period;
      return this;
    }

    public NotifierTask start() {
      Timer timer = new Timer();
      final NotifierTask task = new NotifierTask(this);
      timer.schedule(task, delay, period);
      return task;
    }
  }

  interface Callback {
    void onFinished();
  }
}