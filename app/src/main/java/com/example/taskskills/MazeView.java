package com.example.taskskills;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MazeView extends View {
    private int[][] maze;
    private int playerX, playerY;
    private int visibilityRadius;
    private Paint wallPaint, pathPaint, playerPaint;

    public MazeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        pathPaint = new Paint();
        pathPaint.setColor(Color.WHITE);
        playerPaint = new Paint();
        playerPaint.setColor(Color.BLUE);
    }

    public void setMaze(int[][] maze, int playerX, int playerY, int visibilityRadius) {
        this.maze = maze;
        this.playerX = playerX;
        this.playerY = playerY;
        this.visibilityRadius = visibilityRadius;
        invalidate();
    }

    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maze == null) return;

        int cellSize = getWidth() / maze[0].length;

        // Рисуем весь лабиринт
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                Paint paint;

                if (maze[i][j] == 1) {
                    paint = wallPaint; // Стены
                } else if (maze[i][j] == 2) {
                    paint = new Paint();
                    paint.setColor(Color.GREEN); // Точка старта
                } else if (maze[i][j] == 3) {
                    paint = new Paint();
                    paint.setColor(Color.RED); // Точка выхода
                } else {
                    paint = pathPaint; // Проходимый путь
                }

                canvas.drawRect(
                        j * cellSize,
                        i * cellSize,
                        (j + 1) * cellSize,
                        (i + 1) * cellSize,
                        paint
                );
            }
        }

        // Рисуем туман
        Bitmap fogBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fog_texture); // Добавьте текстуру тумана в папку drawable
        Paint fogPaint = new Paint();
        fogPaint.setAlpha(-1); // Регулируйте прозрачность тумана

        // Создаем новый слой для тумана
        canvas.saveLayer(0, 0, getWidth(), getHeight(), null);

        // Заполняем экран текстурой тумана
        for (int i = 0; i < getWidth(); i += fogBitmap.getWidth()) {
            for (int j = 0; j < getHeight(); j += fogBitmap.getHeight()) {
                canvas.drawBitmap(fogBitmap, i, j, fogPaint);
            }
        }

        // Убираем круг вокруг игрока
        fogPaint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
        canvas.drawCircle(
                playerX * cellSize + cellSize / 2.0f, // Центр игрока X
                playerY * cellSize + cellSize / 2.0f, // Центр игрока Y
                2 * cellSize, // Радиус круга (2 клетки)
                fogPaint
        );

        // Восстанавливаем холст
        fogPaint.setXfermode(null);
        canvas.restore();

        // Рисуем игрока поверх лабиринта
        Bitmap playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chelovek); // Убедитесь, что "chelovek.png" добавлен в res/drawable
        Bitmap scaledPlayerBitmap = Bitmap.createScaledBitmap(playerBitmap, cellSize, cellSize, false);
        canvas.drawBitmap(
                scaledPlayerBitmap,
                playerX * cellSize,
                playerY * cellSize,
                null
        );
    }

}
