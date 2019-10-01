package sample;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Methods {
    public double sin(double a, double f, double t) { //sin function (first part of assignment)
        double x = a*Math.sin(Math.toRadians(t *(360*f)/(2*Math.PI)));
        return x;
    }

    public double cos(double a, double f, double t) { //cos function (first part of assignment)
        double x = a*Math.cos(Math.toRadians(t*(360*f)/(2*Math.PI)));
        return x;
    }

    public static ArrayList<Double> loadFunction() { //load the function from a hardcoded function, f(x)
        ArrayList<Double> coordinates = new ArrayList<Double>();
        for (int i = 0; i < 600; i++) {
            coordinates.add(i, f(i));
        }
        return coordinates;
    }

    public static ArrayList<Double> loadFunction(String file) throws IOException { //load the function from a file
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        ArrayList<Double> coordinates = new ArrayList<Double>();
        raf.seek(0);
        String str = raf.readLine();
        while(str!=null) {
            String Xy[]	= str.split(",");
            double x = Double.parseDouble(Xy[0]);
            double y = Double.parseDouble(Xy[1]);
            coordinates.add(x);
            coordinates.add(-y);
            str = raf.readLine();
        }
        return coordinates;
    }

    public static void drawFunction(Group group, ArrayList<Double> list) {
        for (int i = 0; i < 600; i++) {
            Line point = new Line(i, list.get(i)+300, i, list.get(i)+300);
            group.getChildren().add(point);
            if(i > 0) {
                Line line2 = new Line((i-1), list.get(i-1)+300, i, list.get(i)+300);
                line2.setStroke(Color.GREEN);
                group.getChildren().add(line2);
            }
        }
    }


    public static double f(double x) {
            x -= 300;
            double y = 100*Math.sin(0.025*x); //hardcoded function
                return -y;

    }
}
