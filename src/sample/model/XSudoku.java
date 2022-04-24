package sample.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class XSudoku extends Sudoku {
    public XSudoku() {
        super();
    }

    public XSudoku(int[][] grid) {
        super(grid);
    }

    public XSudoku(XSudoku xs) {
        super(xs);
    }

    public XSudoku(File file) throws FileNotFoundException {
        super(file);
    }

    public XSudoku(Sudoku s) {
        super(s);
    }

    public boolean isSolvedValid() {
        int left = 0;
        int right = 0;
        for (int i = 0; i < 9; i++) {
            left += 1 << grid[i][i];
            right += 1 << grid[i][8 - i];
        }
        return left == 1022 && right == 1022 && super.isSolvedValid();
    }
    public String fileString(){
        return "x\n"+super.fileString();
    }
    public boolean isSquareValid(int i, int j, int[][] grid) {
        int[][] check = new int[2][10];
        if (i == j) {
            for (int k = 0; k < 9; k++) {
                check[0][grid[k][k]]++;
            }
            for (int k = 1; k <= 9; k++) {
                if (check[0][k] > 1 && grid[i][j] == k) {
                    return false;
                }
            }
        }
        if (i + j == 8) {
            for (int k = 0; k < 9; k++) {
                check[1][grid[k][8 - k]]++;
            }
            for (int k = 1; k <= 9; k++) {
                if (check[1][k] > 1 && grid[i][j] == k) {
                    return false;
                }
            }
        }
        return super.isSquareValid(i, j, grid);
    }
}
