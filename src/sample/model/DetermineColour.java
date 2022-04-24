package sample.model;

import sample.controller.ModifierController;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DetermineColour {
    private static ArrayList<ArrayList<Integer>> adj;
    private static int[][] grid;
    private DetermineColour() {
    }

    public static int[] solve(ArrayList<Modifier> alm) {
        adj = new ArrayList<>();
        grid = new int[9][9];
        int[] ans = new int[alm.size()];
        int cnt = 0;
        for (Modifier m : alm) {
            adj.add(new ArrayList<>());
            for (int i : m.getPoints()) {
                grid[i / 9][i % 9] = cnt;
            }
            cnt++;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 8; j++) {
                if (grid[i][j] != grid[i][j + 1]) {
                    if (!adj.get(grid[i][j]).contains(grid[i][j + 1]))
                        adj.get(grid[i][j]).add(grid[i][j + 1]);
                    if (!adj.get(grid[i][j + 1]).contains(grid[i][j]))
                        adj.get(grid[i][j + 1]).add(grid[i][j]);
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 8; j++) {
                if (grid[j][i] != grid[j + 1][i]) {
                    if (!adj.get(grid[j][i]).contains(grid[j + 1][i]))
                        adj.get(grid[j][i]).add(grid[j + 1][i]);
                    if (!adj.get(grid[j + 1][i]).contains(grid[j][i]))
                        adj.get(grid[j + 1][i]).add(grid[j][i]);
                }
            }
        } // constructing the graph
        for (int j = 0; j < alm.size(); j++) {
            ans[j] = -1;
        }
        colour = new int[1000];
        if (alm.size()!=0) {
            for (int i=0;i<alm.size();i++) {
                if (ans[i]!=-1){
                    continue;
                }
                dfs(i);
            }
            for (int j = 0; j < alm.size(); j++) {
                if (ans[j]==-1 && colour[j]!=-1)
                    ans[j] = colour[j];
            }

        }
        return ans;
    }
    private static int[] colour;
    public static void dfs(int source) {
        ArrayList<Integer> arrayList=new ArrayList<>();
        boolean[] pos = new boolean[6];
        for (int i = 0; i < 6; i++) {
            pos[i] = true;
        }
        for (int i : adj.get(source)) {
            if (colour[i] != -1) {
                pos[colour[i]] = false;
            } else arrayList.add(i);
        }
        for (int i = 0; i < 6; i++) {
                if (pos[i]) {
                    colour[source] = i;
                    for (int next:arrayList) {
                        dfs(next);
                    }
                }
            }
    }
}
