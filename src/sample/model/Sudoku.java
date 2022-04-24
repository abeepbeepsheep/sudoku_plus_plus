package sample.model;

import sample.controller.PlayController;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//to set out what the grid is, the base puzzle
//to future self: 0-indexing!
public class Sudoku extends PlayController {
    protected int[][] grid;
    protected ArrayList<Modifier> mods;
    private int[][] bannedGrid;
    private String author;
    private String description;
    private String comment;
    private Date published;
    private String source;

    public Sudoku() {
        grid = new int[9][9];
        bannedGrid = new int[9][9];
        mods = new ArrayList<>();
        setInfo("","","",new Date(),"");
    }

    public Sudoku(int[][] grid) {
        this();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    public Sudoku(File file) throws FileNotFoundException {
        this();
        Scanner cin = new Scanner(file);
        cin.nextLine();
        try {
            this.setInfo(cin.nextLine(), cin.nextLine(), cin.nextLine(), new SimpleDateFormat("dd/MM/yyyy").parse(cin.nextLine()), cin.nextLine());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        for (int i = 0; i < 9; i++) {
            String s = cin.nextLine();
            for (int j = 0; j < 9; j++) {
                grid[i][j] = Integer.parseInt(s.charAt(j) + "");
            }
        }
        while (cin.hasNext()){
            ArrayList<Integer> points=new ArrayList<>();
            String token[]=cin.nextLine().split("[,]");
            try {
                for (int i = 0; i < token.length - 1; i++) {
                    points.add(Integer.parseInt(token[i]));
                }
                Modifier m=new Killer(points,Integer.parseInt(token[token.length-1]));
                mods.add(m);
            }catch (NumberFormatException ex){
                PlayController.invokeError("file has been corrupted and killer cannot be formed");
            }
        }
        cin.close();
    }

    public Sudoku(Sudoku k) {
        this(k.grid);
        mods.addAll(k.getMods());
        setInfo(k.getAuthor(), k.getDescription(), k.getComment(), k.getPublished(), k.getSource());
    }

    public Sudoku(int[][] grid, ArrayList<Modifier> m) {
        this(grid);
        mods.addAll(m);
    }

    public boolean isSolvedValid() {
        int[][] check = new int[3][9];
        // row 1: row check
        // row 2: col check
        // row 3: 3x3 check
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                check[0][i] ^= 1 << grid[i][j];
                check[1][j] ^= 1 << grid[i][j];
                check[2][(i / 3) * 3 + j / 3] ^= 1 << grid[i][j];
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                if (check[i][j] != (1 << 10) - 2) {
                    return false;
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!isSquareValid(i, j, grid)) return false;
            }
        }
        return true;
    } //this is some voodoo magic, basically using bitmasks to check everything, to save memory/code

    public boolean isSquareValid(int row, int col, int[][] grid) {
        int[][] count = new int[3][10];
        for (int i = 0; i < 9; i++) {
            count[0][grid[row][i]]++;
        }
        for (int i = 0; i < 9; i++) {
            count[1][grid[i][col]]++;
        }
        for (int i = row / 3 * 3; i < row / 3 * 3 + 3; i++) {
            for (int j = col / 3 * 3; j < col / 3 * 3 + 3; j++) {
                count[2][grid[i][j]]++;
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j <= 9; j++) {
                if (count[i][j] > 1 && grid[row][col] == j) {
                    return false;
                }
            }
        }
        return checkModValid(row * 9 + col);
    }

    public int[][] getGrid() {
        if (grid == null) return null;
        int[][] out = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                out[i][j] = grid[i][j];
            }
        }
        return out;
    }
    public void clearMods(){
        mods.clear();
    }
    public void setGrid(int[][] grid) {
        if (this.grid == null) this.grid = new int[9][9];
        if (grid == null) return;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    public int getSquare(int row, int col) {
        return grid[row][col];
    }

    public void setSquare(int val, int row, int col) {
        grid[row][col] = val;
    }

    public void createSudokuFromScratch() {
        mods.clear();
        setGrid(genSolvedSudoku(0, new int[9][9]));
    }

    public void solveSudoku() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!isSquareValid(i, j, getGrid())) {
                    grid = null;
                    return;
                }
            }
        }
        grid = recursiveSolve(0, grid);
    }

    // assumes that we have a valid puzzle
    private int[][] recursiveSolve(int pos, int[][] grid) {
        if (pos == 81) {
            if (new Sudoku(bannedGrid).equals(new Sudoku(grid))) {
                return null;
            }
            return grid;
        }
        if (grid[pos / 9][pos % 9] != 0) {
            return recursiveSolve(pos + 1, grid);
        }
        ArrayList<Integer> ar = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            ar.add(i + 1);
        }
        int[][] newGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                newGrid[i][j] = grid[i][j];
            }
        }
        for (int val : ar) {
            newGrid[pos / 9][pos % 9] = val;
            if (isSquareValid(pos / 9, pos % 9, newGrid)) {
                int[][] answer = recursiveSolve(pos + 1, newGrid);
                if (answer != null) return answer;
            }
        }
        return null;
    }

    public int[][] genSolvedSudoku(int pos, int[][] grid) {
        if (pos == 81) {
            return grid;
        }
        ArrayList<Integer> ar = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            ar.add(i + 1);
        }
        int[][] newGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                newGrid[i][j] = grid[i][j];
            }
        }
        Collections.shuffle(ar);
        for (int val : ar) {
            newGrid[pos / 9][pos % 9] = val;
            if (isSquareValid(pos / 9, pos % 9, newGrid)) {
                int[][] answer = genSolvedSudoku(pos + 1, newGrid);
                if (answer != null) return answer;
            }
        }
        return null;
    }

    public void genPuzzle(int clues, Sudoku placeholder) {
        placeholder.createSudokuFromScratch();
        ArrayList<Integer> order = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            order.add(i);
        }
        Collections.shuffle(order);
        int cnt = 0;
        for (int i : order) {
            if (clues == 81 - cnt) {
                grid = placeholder.getGrid();
                return;
            }
            int oldVal = placeholder.getSquare(i / 9, i % 9);
            placeholder.setSquare(0, i / 9, i % 9);
            int[][] solution1 = recursiveSolve(0, placeholder.getGrid());
            bannedGrid = solution1;
            int[][] solution2 = recursiveSolve(0, placeholder.getGrid());
            if (solution2 != null) {
                placeholder.setSquare(oldVal, i / 9, i % 9);
            } else cnt++;
            bannedGrid = new int[9][9];
        }
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                s += grid[i][j];
                if (j == 2 || j == 5) s += "|";
                else s += " ";
            }
            s += '\n';
            if (i == 2 || i == 5) {
                for (int j = 0; j < 17; j++) {
                    if (j == 5 || j == 11) {
                        s += "+";
                        continue;
                    }
                    s += "—";
                }
                s += '\n';
            }
        }
        return s;
    }

    public String fileString() {
        String s = "";
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                s += grid[i][j];
            }
            s += '\n';
        }
        return infoToFile() + s + modString();
    }
    public String modString(){
        String s="";
        for (Modifier m:mods){
            if (m.getPoints().size()!=0)
                s+= m +"\n";
        }
        return s;
    }
    public boolean equals(Sudoku s) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!(this.grid[i][j] == s.grid[i][j]))
                    return false;
            }
        }
        return true;
    }

    public void addMod(Modifier m) {
        mods.add(m);
    }

    public ArrayList<Modifier> getMods() {
        ArrayList<Modifier> newArrayList = new ArrayList<>();
        newArrayList.addAll(mods);
        return newArrayList;
    }

    public void setMods(ArrayList<Modifier> m) {
        for (Modifier k:m){
            if (!mods.contains(k)){
                mods.add(k);
            }
        }
    }

    public boolean checkModValid(int pos) {
        if (mods==null) return true;
        for (Modifier m : mods) {
            int ans = 0;
            for (Integer i : m.getPoints()) {
                if (i == pos) {
                    for (Integer j : m.getPoints()) {
                        if (getSquare(j / 9, j % 9) == 0)return true;
                        ans += getSquare(j / 9, j % 9);
                    }
                    return ans == m.getSum();
                }
            }
        }
        return true;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getComment() {
        return comment;
    }

    public Date getPublished() {
        return published;
    }

    public String getSource() {
        return source;
    }

    public void setInfo(String author, String description, String comment, Date published, String source) {
        this.author = author;
        this.description = description;
        this.comment = comment;
        this.published = published; //shallow
        this.source = source;
    }

    public String infoToFile() {
        String out = "";
        out += author + '\n' + description + '\n' + comment + '\n' + new SimpleDateFormat("dd/MM/yyyy").format(published) + '\n' + source + '\n';
        return out;
    }

    public String puzzleInfo() {
        return "Author: " + author + '\n' + "Date Published: " +
                new SimpleDateFormat("dd/MM/yyyy").format(published) + '\n' + "Source: " + source + '\n'
                + description + '\n' + comment;
    }
}