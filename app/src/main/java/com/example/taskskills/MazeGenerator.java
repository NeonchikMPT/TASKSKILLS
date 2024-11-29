package com.example.taskskills;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

public class MazeGenerator {
    private int[][] maze;
    private int rows, cols;
    private static final int WALL = 1;
    private static final int PATH = 0;
    private static final int START = 2;
    private static final int EXIT = 3;

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        generateMaze();
    }

    private void generateMaze() {
        maze = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = WALL;
            }
        }

        // Генерация лабиринта
        carvePath(1, 1);

        // Генерация начальной и конечной точек
        setStartAndExit();
    }

    private void carvePath(int x, int y) {
        maze[x][y] = PATH;
        List<int[]> directions = new ArrayList<>();
        directions.add(new int[]{0, 2});
        directions.add(new int[]{0, -2});
        directions.add(new int[]{2, 0});
        directions.add(new int[]{-2, 0});
        shuffle(directions);

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx > 0 && ny > 0 && nx < rows - 1 && ny < cols - 1 && maze[nx][ny] == WALL) {
                maze[x + dir[0] / 2][y + dir[1] / 2] = PATH;
                carvePath(nx, ny);
            }
        }
    }

    private void setStartAndExit() {
        Random random = new Random();
        int startX, startY, exitX = 0, exitY = 0;

        // Случайная начальная точка
        do {
            startX = random.nextInt(rows);
            startY = random.nextInt(cols);
        } while (maze[startX][startY] != PATH);

        maze[startX][startY] = START;

        // Поиск самой удалённой точки для выхода
        int[][] distances = calculateDistances(startX, startY);
        int maxDistance = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (distances[i][j] > maxDistance && maze[i][j] == PATH) {
                    maxDistance = distances[i][j];
                    exitX = i;
                    exitY = j;
                }
            }
        }

        // Проверяем, что между началом и выходом есть барьер
        if (!hasBarrierBetween(startX, startY, exitX, exitY)) {
            do {
                exitX = random.nextInt(rows);
                exitY = random.nextInt(cols);
            } while (maze[exitX][exitY] != PATH || !hasBarrierBetween(startX, startY, exitX, exitY));
        }

        maze[exitX][exitY] = EXIT;
    }

    private int[][] calculateDistances(int startX, int startY) {
        int[][] distances = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && ny >= 0 && nx < rows && ny < cols && !visited[nx][ny] && maze[nx][ny] == PATH) {
                    distances[nx][ny] = distances[x][y] + 1;
                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                }
            }
        }
        return distances;
    }

    private boolean hasBarrierBetween(int startX, int startY, int exitX, int exitY) {
        int dx = Math.abs(startX - exitX);
        int dy = Math.abs(startY - exitY);
        return dx > 1 || dy > 1;
    }

    private void shuffle(List<int[]> list) {
        Random random = new Random();
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    public int[][] getMaze() {
        return maze;
    }
}
