package com.wskh.meta_heuristics.ILS.tsp;

import com.wskh.classes.tsp.TSP_Instance;
import com.wskh.classes.tsp.TSP_Solution;
import com.wskh.utils.TSP_Util;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author：WSKH
 * @ClassName：Run
 * @Description：
 * @Time：2023/5/14/11:52
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
public class RunAndPlot extends javafx.application.Application {

    // 可视化区域的宽度
    int stageW = 1000;
    // 可视化区域的高度
    int stageH = 1000;
    // 坐标偏移
    int offsetX = 100;
    int offsetY = 100;

    // 主要函数
    @Override
    public void start(Stage primaryStage) throws Exception {

        // 初始化画布和面板
        AnchorPane pane = new AnchorPane();
        Canvas canvas = initCanvas(stageW - offsetX * 2, stageH - offsetY * 2);
        canvas.relocate(offsetX, offsetY);
        pane.getChildren().add(canvas);

        // 读取tsp数据
        TSP_Instance tspInstance = TSP_Util.readTSP_Instance("data/tsp/att48.tsp");
        // 固定使用随机数种子
        Long seed = 2023L;
        System.out.println("------------------------- 迭代局部搜索算法求解TSP问题 -----------------------------");
        TSP_Solution solution = new TSP_Solver_ILS(seed, 40000, 100, 6).solve(tspInstance);

        // 获取最佳路径
        int[] bestPath = solution.getPath().clone();

        // 按照屏幕尺寸等比例调整城市坐标
        List<double[]> fitLocationList = fitLocation(tspInstance.getLocations());

        // 绘制城市
        HashMap<Integer, Circle> map = new HashMap<>();
        List<Circle> circleList = new ArrayList<>();
        for (double[] position : fitLocationList) {
            Circle circle = new Circle(position[0], position[1], 5);
            pane.getChildren().add(circle);
            circleList.add(circle);
        }

        // 添加播放按钮
        Button button = new Button("播放");
        final int[] pCnt = {0};
        button.setOnAction(new EventHandler<ActionEvent>() {
            // 按钮点击事件
            @Override
            public void handle(ActionEvent event) {
                // 路径 动画
                Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (pCnt[0] < bestPath.length - 1) {
                            int cur = bestPath[pCnt[0]];
                            int next = bestPath[pCnt[0] + 1];
                            double[] p1 = fitLocationList.get(cur);
                            double[] p2 = fitLocationList.get(next);
                            Path path = new Path();
                            path.getElements().add(new MoveTo(p1[0], p1[1]));
                            path.getElements().add(new LineTo(p2[0], p2[1]));
                            pane.getChildren().add(path);
                            pCnt[0]++;
                        } else if (pCnt[0] == bestPath.length - 1) {
                            int cur = bestPath[pCnt[0]];
                            int next = bestPath[0];
                            double[] p1 = fitLocationList.get(cur);
                            double[] p2 = fitLocationList.get(next);
                            Path path = new Path();
                            path.getElements().add(new MoveTo(p1[0], p1[1]));
                            path.getElements().add(new LineTo(p2[0], p2[1]));
                            pane.getChildren().add(path);
                            pCnt[0]++;
                        }
                    }
                }));
                animation.setCycleCount(Timeline.INDEFINITE);
                animation.play();
            }
        });
        pane.getChildren().add(button);

        // 一切就绪，准备展示
        primaryStage.setTitle("TSP路径可视化");
        primaryStage.setScene(new Scene(pane, stageW, stageH));
        primaryStage.show();
    }

    private List<double[]> fitLocation(double[][] locations) {
        List<double[]> locationList = new ArrayList<>();
        for (double[] location : locations) {
            locationList.add(location.clone());
        }
        // 获取宽度和高度方向上的最大值
        double maxX = locationList.get(0)[0];
        double maxY = locationList.get(0)[1];
        for (double[] position : locationList) {
            maxX = Math.max(maxX, position[0]);
            maxY = Math.max(maxY, position[1]);
        }
        // 计算缩放比例
        double rateX = (stageW - 2 * offsetX) / maxX;
        double rateY = (stageH - 2 * offsetY) / maxY;
        // 按照比例自适应调整
        List<double[]> fitLocationList = new ArrayList<>();
        for (double[] position : locationList) {
            fitLocationList.add(new double[]{position[0] * rateX + offsetX, position[1] * rateY + offsetY});
        }
        return fitLocationList;
    }

    // 初始化画布对象
    private Canvas initCanvas(double l, double w) {
        Canvas canvas = new Canvas(l, w);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 边框
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, l, w);
        // 填充
        gc.setFill(new Color(127 / 255d, 255 / 255d, 170 / 255d, 1d));
        gc.fillRect(0, 0, l, w);
        return canvas;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
