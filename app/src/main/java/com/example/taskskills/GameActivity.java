package com.example.taskskills;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private MazeView mazeView;
    private int playerX, playerY;
    private MazeGenerator mazeGenerator;
    private int rows = 15, cols = 15;
    private final int VISIBILITY_RADIUS = 2;

    private final Handler movementHandler = new Handler();
    private Runnable movementRunnable;
    private int movementDx, movementDy;
    private boolean isMoving = false; // Флаг движения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mazeView = findViewById(R.id.mazeView);
        generateNewMaze();

        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);

        // Удержание кнопок для движения
        btnUp.setOnTouchListener((v, event) -> handleMovement(event, 0, -1));
        btnDown.setOnTouchListener((v, event) -> handleMovement(event, 0, 1));
        btnLeft.setOnTouchListener((v, event) -> handleMovement(event, -1, 0));
        btnRight.setOnTouchListener((v, event) -> handleMovement(event, 1, 0));
    }

    private boolean handleMovement(MotionEvent event, int dx, int dy) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isMoving) { // Начинаем движение только если не двигаемся
                    movementDx = dx;
                    movementDy = dy;
                    isMoving = true;
                    startContinuousMovement();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopContinuousMovement(); // Останавливаем движение при отпускании
                break;
        }
        return true;
    }

    private void startContinuousMovement() {
        movementRunnable = new Runnable() {
            @Override
            public void run() {
                if (isMoving) {
                    if (canMove(movementDx, movementDy)) {
                        movePlayer(movementDx, movementDy);
                        movementHandler.postDelayed(this, 150); // Интервал движения (мс)
                    } else {
                        stopContinuousMovement(); // Остановить, если движение невозможно
                    }
                }
            }
        };
        movementHandler.post(movementRunnable);
    }

    private void stopContinuousMovement() {
        isMoving = false; // Флаг для остановки
        movementHandler.removeCallbacks(movementRunnable);
    }

    private boolean canMove(int dx, int dy) {
        int[][] maze = mazeGenerator.getMaze();
        int newX = playerX + dx;
        int newY = playerY + dy;

        return newX >= 0 && newY >= 0 && newX < maze[0].length && newY < maze.length && maze[newY][newX] != 1;
    }

    private void movePlayer(int dx, int dy) {
        int[][] maze = mazeGenerator.getMaze();
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (canMove(dx, dy)) {
            playerX = newX;
            playerY = newY;
            mazeView.setPlayerPosition(playerX, playerY);

            if (maze[playerY][playerX] == 3) {
                levelCompleted();
            }
        }
    }

    private void levelCompleted() {
        Toast.makeText(this, "Уровень пройден!", Toast.LENGTH_SHORT).show();
        rows += 2;
        cols += 2;
        generateNewMaze();
    }

    private void generateNewMaze() {
        mazeGenerator = new MazeGenerator(rows, cols);

        // Находим стартовую точку
        int[][] maze = mazeGenerator.getMaze();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze[i][j] == 2) { // START
                    playerX = j;
                    playerY = i;
                }
            }
        }

        mazeView.setMaze(mazeGenerator.getMaze(), playerX, playerY, VISIBILITY_RADIUS);
    }
}
