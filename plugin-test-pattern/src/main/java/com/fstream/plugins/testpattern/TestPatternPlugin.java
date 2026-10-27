package com.fstream.plugins.testpattern;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.Surface;
import com.fstream.plugin.sdk.*;

/** Minimal independent plugin: no host classes, third-party libraries, camera, or network. */
public final class TestPatternPlugin implements VideoInputPlugin {
    @Override public VideoInputSession createSession(PluginHost host, String sourceId, String config) {
        if (!"bars".equals(sourceId)) throw new IllegalArgumentException("Unknown test pattern");
        return new Session();
    }
    private static final class Session implements VideoInputSession {
        private volatile boolean running;
        private Thread thread;
        @Override public void start(Surface surface, int width, int height, int fps, VideoInputEvents events) {
            if (thread != null) throw new IllegalStateException("Already started");
            running = true;
            thread = new Thread(() -> {
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                int[] colors = {Color.WHITE, Color.YELLOW, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLUE};
                long frame = 0;
                long origin = SystemClock.elapsedRealtime();
                try {
                    while (running) {
                        Canvas canvas = surface.lockCanvas(null);
                        try {
                            int w = canvas.getWidth(), h = canvas.getHeight();
                            for (int i = 0; i < colors.length; i++) {
                                paint.setColor(colors[i]);
                                canvas.drawRect(i * w / 7f, 0, (i + 1) * w / 7f, h, paint);
                            }
                            paint.setColor(Color.BLACK);
                            canvas.drawRect(0, h * .72f, w, h, paint);
                            paint.setColor(Color.WHITE);
                            paint.setTextSize(Math.max(20, h / 18f));
                            canvas.drawText("fStream plugin / frame " + frame, w * .04f, h * .85f, paint);
                            canvas.drawCircle((frame * 5) % Math.max(1, w), h * .55f, h * .04f, paint);
                        } finally { surface.unlockCanvasAndPost(canvas); }
                        if (frame++ == 0 && running) events.onFirstFrame();
                        long due = origin + frame * 1000 / Math.max(1, fps);
                        long delay = due - SystemClock.elapsedRealtime();
                        if (delay > 0) Thread.sleep(delay);
                        else if (delay < -1000) origin = SystemClock.elapsedRealtime() - frame * 1000 / Math.max(1, fps);
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } catch (Exception error) {
                    if (running) events.onError("Test pattern rendering failed");
                }
            }, "fstream-test-pattern");
            thread.start();
        }
        @Override public void close() {
            running = false;
            Thread producer = thread;
            if (producer != null && producer != Thread.currentThread()) {
                producer.interrupt();
                boolean interrupted = false;
                while (producer.isAlive()) {
                    try { producer.join(); } catch (InterruptedException e) { interrupted = true; }
                }
                if (interrupted) Thread.currentThread().interrupt();
            }
        }
    }
}
