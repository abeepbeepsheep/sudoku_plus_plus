package sample.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class KingSudoku extends Sudoku {
    private final int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
    private final int[] dy = {1, 1, 1, 0, 0, -1, -1, -1};

    public KingSudoku() {
        super();
    }

    public KingSudoku(int[][] grid) {
        super(grid);
    }

    public KingSudoku(File file) throws FileNotFoundException {
        super(file);
    }
    public String fileString(){
        return "k\n"+super.fileString();
    }
    public KingSudoku(Sudoku s) {
        super(s);
    }

    public KingSudoku(KingSudoku ks) {
        super(ks);
    }

    public boolean isSquareValid(int i, int j, int[][] grid) {
        for (int a = 0; a < 8; a++) {
            int ni = dx[a] + i;
            int nj = dy[a] + j;
            if (ni < 9 && ni >= 0 && nj < 9 && nj >= 0) {
                if (grid[ni][nj] != 0 && grid[ni][nj] == grid[i][j])
                    return false;
            }
        }
        return super.isSquareValid(i, j, grid);
    }
}
