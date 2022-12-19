import java.awt.*;

public class Helpers {
    private int width;
    private int height;
    private int x;
    private int y;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private String name;

    public String getName() {
        return name;
    }

    public static Color hexToColor(String input){
        Character[] characters = new Character[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        int red = 0;
        int green = 0;
        int blue = 0;
        input = input.substring(1);
        for (int i = 0; i < input.length(); i++) {

            for (int j = 0; j < characters.length; j++) {
                if(input.charAt(i) == characters[j]){
                    if(i==0 || i==1){
                        red += j*Math.pow(16,1-i);
                        System.out.println(red);
                    } else if(i==2 || i==3){
                        green += j*Math.pow(16,3-i);
                        System.out.println(green);
                    }else if(i==4 || i==5){
                        blue += j*Math.pow(16,5-i);
                        System.out.println(blue);
                    }
                    break;
                }
            }
        }
        Color out = new Color(red, green, blue);

        return out;
    }
}
