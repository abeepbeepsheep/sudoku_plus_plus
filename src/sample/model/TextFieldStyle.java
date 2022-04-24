package sample.model;

public class TextFieldStyle {
    private String backgroundColour;
    private String textColour;

    public TextFieldStyle() {
        backgroundColour = "-fx-background-color: transparent";
        textColour = "-fx-text-fill: #000000";
    }

    public void setBackgroundColour(String backgroundColour) {
        this.backgroundColour = "-fx-background-color: " + backgroundColour;
    }

    public void setTextColour(String textColour) {
        this.textColour = "-fx-text-fill: " + textColour;
    }

    public String toString() {
        return backgroundColour + ";" + textColour + ";" + "-fx-display-caret: false; -fx-border-color: #43ac2b;" +
                "-fx-font-family: 'Trebuchet MS'; -fx-font-size: 22.3;";
    }
}
