package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sample.model.DetermineColour;
import sample.model.Killer;
import sample.model.Modifier;

import java.net.URL;
import java.util.*;

public class KillerController{
    @FXML
    private AnchorPane background;
    @FXML
    private GridPane gp;
    @FXML
    private TextField sum;
    private Button[][] array;
    private String[] colours;
    private int[][] chosen;
    private int[][] currentMod;
    private ArrayList<Modifier> alm;
    private Modifier current;
    private PlayController pc;
    public void setPc(PlayController pc){
        this.pc=pc;
    }
    public void init(){
        array=new Button[9][9];
        chosen=new int[9][9];
        currentMod=new int[9][9];
        Button c=new Button();
        c.setMaxSize(0,0);
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                chosen[i][j]=-1;
                currentMod[i][j]=-1;
                Button b=new Button();
                b.setMinSize(50,50);
                b.setOnAction(this::select);
                b.setAlignment(Pos.BOTTOM_RIGHT);
                array[i][j]=b;
                gp.add(b,j,i);
            }
        }
        colours=new String[6];
        colours[0] = "#378ad7";
        colours[1] = "#7819cb";
        colours[2] = "#19cbbc";
        colours[3] = "#1fcb19";
        colours[4] = "#bfcb19";
        colours[5] = "#cb6c19";
        alm=pc.getALM();
        int cnt=0;
        for (Modifier m:alm){
            for (int i:m.getPoints()){
                currentMod[i/9][i%9]=cnt;
            }
            cnt++;
        }
        editChosen();
        newSelect=true;
    }
    private int row,col;
    private boolean newSelect;
    public void select(ActionEvent e){
        Button b=(Button) e.getSource();
        findCoords(b);
        if (newSelect) { //check if we need to take the original killer
            for (Modifier m : alm) {
                for (int i : m.getPoints()) {
                    if (i == row * 9 + col) {
                        sum.setText(m.getSum() + "");
                        current = m;
                        break;
                    }
                }
            }
            newSelect=false;
            lightGrid();
            for (int i=0;i<9;i++){
                for (int j=0;j<9;j++){
                    array[i][j].setText("");
                }
            }
        } else{
            if (b.getStyle().equals("-fx-border-style: solid;-fx-background-color: #71b15e")){
                if (chosen[row][col]!=-1){
                    b.setStyle("-fx-border-style: solid;-fx-background-color: "+colours[chosen[row][col]]);
                }
                else{
                    b.setStyle("-fx-border-style: solid;-fx-background-color: #aabbcc");
                }
            }
            else{
                b.setStyle("-fx-border-style: solid;-fx-background-color: #71b15e");
            }
            // handle not click for the first time
            // check for adjacent
            // check for parting
        }
    }
    public void lightGrid(){
        editChosen();
        if (current!=null)
            for (int i:current.getPoints()){
            array[i/9][i%9].setStyle("-fx-border-style: solid;-fx-background-color: #71b15e");
            }
        else{
            array[row][col].setStyle("-fx-border-style: solid;-fx-background-color: #71b15e");
        }
    }
    public void findCoords(Button b){
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                if (array[i][j].equals(b)){
                    row=i;col=j;
                }
            }
        }
    }
    public boolean checkCut(){
        int[][] deepCurrentMod=new int[9][9];
        boolean[][] vis=new boolean[9][9];
        HashSet<Integer> set=new HashSet<>();
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                deepCurrentMod[i][j]=currentMod[i][j];
                if (array[i][j].getStyle().equals("-fx-border-style: solid;-fx-background-color: #71b15e"))
                    deepCurrentMod[i][j]=-1;
            }
        }
        int[] dx={0,1,0,-1};
        int[] dy={1,0,-1,0};
        Queue<Integer> q=new LinkedList<>();
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                if (vis[i][j] || deepCurrentMod[i][j]==-1) continue;
                vis[i][j]=true;
                q.add(i*9+j);
                int curr=deepCurrentMod[i][j];
                if (set.contains(curr)){
                    return false;
                } else{set.add(curr);}
                while (q.size()!=0){
                    int top=q.remove();
                    int x=top/9,y=top%9;
                    for (int k=0;k<4;k++){
                        int nx=x+dx[k],ny=y+dy[k];
                        if (nx<0 || nx>=9 || ny<0 || ny>=9) continue;
                        if (vis[nx][ny]) continue;
                        if (deepCurrentMod[nx][ny]!=curr) continue;
                        vis[nx][ny]=true;
                        q.add(nx*9+ny);
                    }
                }
            }
        }
        return true;
    }
    public void delete(ActionEvent e){
        if (!checkCut()){
            PlayController.invokeError("You cannot delete one or more of the chosen squares, as " +
                    "\nit would disrupt the current modifiers");
            return;
        }
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                if (array[i][j].getStyle().equals("-fx-border-style: solid;-fx-background-color: #71b15e")){
                    if (currentMod[i][j]!=-1){
                        alm.get(currentMod[i][j]).getPoints().remove((Integer) (i*9+j));
                        currentMod[i][j]=-1;
                        chosen[i][j]=-1;
                    }
                }
            }
        }
        newSelect=true;
        editChosen();
        pc.constructKiller();
    }
    public void add(ActionEvent e){
        if (!checkCut()){
            PlayController.invokeError("You cannot delete one or more of the chosen squares, as " +
                    "\nit would disrupt the current modifiers");
            return;
        }
        try{
            int intSum=Integer.parseInt(sum.getText());
            ArrayList<Integer> points=new ArrayList<>();
            for (int i=0;i<9;i++){
                for (int j=0;j<9;j++){
                    if (array[i][j].getStyle().equals("-fx-border-style: solid;-fx-background-color: #71b15e")){
                        points.add(i*9+j);
                        if (currentMod[i][j]!=-1){
                            alm.get(currentMod[i][j]).getPoints().remove((Integer) (i*9+j));
                            chosen[i][j]=-1;
                        }
                        currentMod[i][j]=alm.size();
                    }
                }
            }
            Modifier m=new Killer(points,intSum);
            alm.add(m);
        }catch(NumberFormatException ex){
            PlayController.invokeError("input a valid sum!");
        } finally {
            sum.setText("");
        }
        newSelect=true;
        editChosen();
        pc.constructKiller();
    }
    public void cancel(ActionEvent e){
        newSelect=true;
        editChosen();
    }
    public void editChosen(){
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                array[i][j].setStyle("-fx-border-style: solid;-fx-background-color: #aabbcc");
            }
        }
        int[] ans=DetermineColour.solve(alm);
        for (int i=0;i<alm.size();i++){
            for (int j:alm.get(i).getPoints()) {
                chosen[j/9][j%9]=ans[i];
                array[j/9][j%9].setStyle("-fx-border-style: solid;-fx-background-color: " + colours[ans[i]]);
                if (j==alm.get(i).getPoints().get(alm.get(i).getPoints().size()-1)){
                    array[j/9][j%9].setText(alm.get(i).getSum()+"");
                }
            }
        }
    }

}
