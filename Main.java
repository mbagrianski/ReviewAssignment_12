/*
Misha Bagrianski - ICS4U1 - 9/28/2019
Instructor: Mr. Radulovich
Review Assignment
Draws one of the following with spinning lines: A function f(x) or a relation of points,
created by tracing a cursor on the application opened through running the Draw.java class.

 */


package sample;

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

        ArrayList<Double>linesX = new ArrayList<Double>(); //initializes arrayList of cfx values
        ArrayList<Double>linesY = new ArrayList<Double>(); //initializes arrayList of cfy values

        //initializes arrayList of points of any function that may have been drawn with Draw.java
        ArrayList<Double> drawnF = Methods.loadFunction(System.getProperty("user.dir") + "/src/function.txt");

        //initializes arrayList of points traced by spinning lines. Unlike other lists, this one contains both x and y
        //in format {x1, y1, x2, y2, ...}- this works well enough and was a result of earlier development
        ArrayList<Double> drawnPoints = new ArrayList<Double>();

        int in; //initialize inITIAL and fiNAL frequency values
        int fi;
        double deltaT; //initialize deltaT

        //int in = -300; //comment in this block to manually specify a range of frequencies
        //int fi = 300; //otherwise it is done automatically to optimize the approximation


        if(functionType){ //Specifies an ideal number of frequencies based on number of sampled points
            in = -drawnF.size() / 4;
            fi = drawnF.size() / 4;

            deltaT = 1 / ((drawnF.size()-1) / 2.0);
        }else{
            in = -300;
            fi = 300;

            deltaT = 1.0/600;
        }

        double x;
        double y;

        for(int f = in; f <= fi; f++) { //sum cfx and cfy coefficients; add them to arraylists linesX and linesY
            int j = 1;
            double cfx = 0;
            double cfy = 0;
            for(double t = 0; t <= 1; t+= deltaT) { // go through all values of t from 0 to 1 by increments of deltaT
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

                if((j>= 600 && !functionType) | (j >= drawnF.size() && functionType)) { //exit loop if all
                    // points have been used
                    break;
                }
            }
            linesX.add(cfx);
            linesY.add(cfy);
        }

        //System.out.println(linesX); //comment in for debugging
        //System.out.println(linesY);

        Group Axis = new Group();

        int length = 600;//set window length
        int width = 600;//set window width

        primaryStage.setScene(new Scene(Axis, length, width));

        Line Xaxis = new Line(0, width / 2.0, length, width / 2.0); //create an x axis
        Line Yaxis = new Line(length / 2.0, 0, length / 2.0, width); //create a y axis

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
                double elapsedSeconds = elapsedNanoSeconds / 1_000_000_000.0; //calculate the value of time that has
                //elapsed during the run of one cycle of the animationTimer. lastUpdate is initialized at the
                // end of the handle. the variable now is the time the animation has been
                // running in nanoseconds since startup.

                Axis.getChildren().clear();
                Axis.getChildren().add(Xaxis);
                Axis.getChildren().add(Yaxis);
                double x = 0; //initialize x, y, previous x and previous y values
                double y = 0;
                double prevX = 0;
                double prevY = 0;

                if (!functionType) {
                    Methods.drawFunction(Axis, Methods.loadFunction()); //if drawing f(x),
                    // draw using the loadFUnction method
                } else { //otherwise draw from file here
                    for (int i = 1; i < drawnF.size(); i += 2) {
                        Circle point = new Circle(drawnF.get(i - 1) + width/2, drawnF.get(i) + width/2, 1);
                        point.setFill(Color.BLUE);
                        Axis.getChildren().add(point);
                        if (i >= 3) {
                            Line connection = new Line(drawnF.get(i - 3) + width/2, drawnF.get(i - 2) + width/2,
                                    drawnF.get(i - 1) + width/2, drawnF.get(i) + width/2);
                            Axis.getChildren().add(connection); //draws points and connecting lines
                            // (points are those points specified in function.txt)
                        }
                    }
                }

                for (int i = 0; i < linesX.size(); i++) {

                    int f = i + in;
                    double cfx = linesX.get(i);
                    double cfy = linesY.get(i);

                    x += cfx * Math.cos(2 * Math.PI * f * t) - cfy * Math.sin(2 * Math.PI * f * t); //perform summation
                    y += cfx * Math.sin(2 * Math.PI * f * t) + cfy * Math.cos(2 * Math.PI * f * t);

                    Circle point = new Circle(x + 300, y + 300, 2); //draw

                    if (i + 1 == linesX.size()) {

                        point.setFill(Color.RED);

                        drawnPoints.add(x+300); //add points that have been drawn to 'trace' the final spinning line's path
                        drawnPoints.add(y+300);

                        for(int j = 1; j < drawnPoints.size(); j+=2) { //draw connection lines between traced points
                            if(j >=3){
                                Line drawnConnection = new Line(drawnPoints.get(j - 3), drawnPoints.get(j - 2),
                                        drawnPoints.get(j - 1), drawnPoints.get(j));
                                drawnConnection.setStroke(Color.RED);
                                Axis.getChildren().add(drawnConnection);
                            }
                        }

                    } else {
                        point.setFill(Color.BLUE); //final point is red, otherwise they are blue
                    }
                    Axis.getChildren().add(point);

                    Line connection = new Line(prevX + width/2, prevY + width/2, x + width/2, y + width/2);
                    prevX = x;
                    prevY = y;
                    Axis.getChildren().add(connection); //draw one more line for the points;
                    // set prevX to x and same with y

                }
                t += elapsedSeconds*0.1; //increment time by elapsedSeconds. Multiplying by 0.1 slows down the animation
                // and makes it nice to look at

                if(t > 1){ //stop animation after 1 cycle
                    this.stop();
                }
                lastUpdate= now;
            }
        };

        timer.start();
        primaryStage.show();
    }
}
