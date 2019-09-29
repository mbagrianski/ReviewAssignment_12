/*
Misha Bagrianski - ICS4U1 - 9/28/2019
Instructor: Mr. Radulovich
Review Assignment


 */


package sample;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {

    private boolean functionType = true ; //false == f(x), true == file

    public static void main (String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ArrayList<Double>linesX = new ArrayList<Double>();
        ArrayList<Double>linesY = new ArrayList<Double>();
        ArrayList<Double> drawnF = Methods.loadFunction(System.getProperty("user.dir") + "/src/function.txt");
        ArrayList<Double> drawnPoints = new ArrayList<Double>();

        int in = -100; //initial
        int fi = 100;

        double deltaT;

        if(functionType){
            deltaT = 1.0 / (drawnF.size() / 2.0);
        }else{
            deltaT = 1.0/600;
        }

        double x;
        double y;

        for(int f = in; f <= fi; f++) {
            int j = 1;
            double cfx = 0;
            double cfy = 0;
            for(double t = 0; t <= 1; t+= deltaT) {
                if(functionType){
                    x = drawnF.get(j-1);
                    y = drawnF.get(j);
                    j+=2;
                }else{
                    x = j - 300;
                    y = Methods.loadFunction().get(j);
                    j++;
                }

                //System.out.println(x + y);
                cfx += (x*Math.cos(2*Math.PI*f*t)+ y*Math.sin(2*Math.PI*f*t))*deltaT;
                cfy -= (x*Math.sin(2*Math.PI*f*t)- y*Math.cos(2*Math.PI*f*t))*deltaT;

                if((j>= 600 && !functionType) | (j >= drawnF.size() && functionType)) {
                    break;
                }
            }
            linesX.add(cfx);
            linesY.add(cfy);
        }

        System.out.println(linesX);
        System.out.println(linesY);

        Group Axis = new Group();

        int length = 600;//set window length
        int width = 600;//set window width

        primaryStage.setScene(new Scene(Axis, length, width));

        Line Xaxis = new Line(0, width /2, length, width /2); //create x axis
        Line Yaxis = new Line(length /2, 0, length /2, width); //create y axis

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate;

            @Override
            public void start() {
                lastUpdate = System.nanoTime();
                super.start();
            }

            double t = 0;

            @Override
            public void handle(long now) {
                long elapsedNanoSeconds = now - lastUpdate;
                double elapsedSeconds = elapsedNanoSeconds / 1_000_000_000.0;

                Axis.getChildren().clear();
                Axis.getChildren().add(Xaxis);
                Axis.getChildren().add(Yaxis);
                double x = 0;
                double y = 0;
                double prevX = 0;
                double prevY = 0;
                if (!functionType) {
                    Methods.drawFunction(Axis, Methods.loadFunction());
                } else {
                    for (int i = 1; i < drawnF.size(); i += 2) {
                        Circle point = new Circle(drawnF.get(i - 1) + 300, drawnF.get(i) + 300, 1);
                        point.setFill(Color.BLUE);
                        Axis.getChildren().add(point);
                        if (i >= 3) {
                            Line connection = new Line(drawnF.get(i - 3) + 300, drawnF.get(i - 2) + 300, drawnF.get(i - 1) + 300, drawnF.get(i) + 300);
                            Axis.getChildren().add(connection);
                        }
                    }
                }

                for (int i = 0; i < linesX.size(); i++) {

                    int f = i + in;
                    double cfx = linesX.get(i);
                    double cfy = linesY.get(i);

                    x += cfx * Math.cos(2 * Math.PI * f * t) - cfy * Math.sin(2 * Math.PI * f * t);
                    y += cfx * Math.sin(2 * Math.PI * f * t) + cfy * Math.cos(2 * Math.PI * f * t);

                    Circle point = new Circle(x + 300, y + 300, 2);

                    if (i + 1 == linesX.size()) {

                        point.setFill(Color.RED);

                        drawnPoints.add(x+300);
                        drawnPoints.add(y+300);

                        for(int j = 1; j < drawnPoints.size(); j+=2) {
                            if(j >=3){
                                Line drawnConnection = new Line(drawnPoints.get(j - 3), drawnPoints.get(j - 2), drawnPoints.get(j - 1), drawnPoints.get(j));
                                drawnConnection.setStroke(Color.RED);
                                Axis.getChildren().add(drawnConnection);
                            }
                        }

                    } else {
                        point.setFill(Color.BLUE);
                    }
                    Axis.getChildren().add(point);

                    Line connection = new Line(prevX + 300, prevY + 300, x + 300, y + 300);
                    prevX = x;
                    prevY = y;
                    Axis.getChildren().add(connection);

                }
                t += elapsedSeconds*0.1;
                if(t > 1){
                    this.stop();
                }
                lastUpdate= now;
            }
        };

        timer.start();
        primaryStage.show();
    }
}