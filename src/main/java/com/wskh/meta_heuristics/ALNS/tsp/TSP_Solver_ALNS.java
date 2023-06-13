package com.wskh.meta_heuristics.ALNS.tsp;

import com.wskh.classes.tsp.TSP_Instance;
import com.wskh.classes.tsp.TSP_Solution;
import com.wskh.utils.TSP_Util;
import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * @Author：WSKH
 * @ClassName：TSP_Solver_SA
 * @Description：
 * @Time：2023/5/13/22:21
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
@Data
public class TSP_Solver_ALNS {

    // 随机数种子
    Long seed;
    // 0~1 控制权重对破坏和修复方法的性能变化的敏感程度的衰减参数
    double lambda = 0.6;
    // 分数(w1>w2>w3>w4>0)
    double w1, w2, w3, w4;
    // 迭代次数
    int epochs = 5000;
    // 温度
    double c = 100d;
    // 冷却降温系数：一般取 0.8 ~ 0.99
    double alphaCooling = 0.9;

    // 构造函数
    public TSP_Solver_ALNS(Long seed, double lambda, double w1, double w2, double w3, double w4, int epochs, double c) {
        this.seed = seed;
        this.lambda = lambda;
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
        this.w4 = w4;
        this.epochs = epochs;
        this.c = c;
    }

    // 城市数量
    int n;
    // 城市坐标
    double[][] locations;
    // 距离矩阵
    double[][] distances;
    // 随机数生成对象
    Random random;
    // 当前解
    TSP_Solution curSolution;
    // 最优解
    TSP_Solution bestSolution;
    // 销毁算子和修复算子的分数数组
    double[] destroyScores;
    double[] repairScores;

    // 求解函数
    public TSP_Solution solve(TSP_Instance tspInstance) {
        long startTime = System.currentTimeMillis();
        // 初始化操作
        init(tspInstance);
        System.out.println("城市数量为: " + n);
        System.out.println("初始解为: " + bestSolution);
        // 自适应大邻域搜索过程
        for (int epoch = 0; epoch < epochs; epoch++) {
            // 轮盘赌选择销毁算子对当前解进行破坏
            int destroyIndex = roulette(destroyScores);
            PartX partX = null;
            switch (destroyIndex) {
                case 0:
                    partX = destroyOperator1(curSolution.getPath());
                    break;
                case 1:
                    partX = destroyOperator2(curSolution.getPath());
                    break;
                case 2:
                    partX = destroyOperator3(curSolution.getPath());
                    break;
                default:
                    break;
            }
            assert partX != null;
            // 轮盘赌选择修复算子对部分解进行修复
            int repairIndex = roulette(repairScores);
            int[] newX = null;
            switch (repairIndex) {
                case 0:
                    newX = repairOperator1(partX);
                    break;
                case 1:
                    newX = repairOperator2(partX);
                    break;
                case 2:
                    newX = repairOperator3(partX);
                    break;
                default:
                    break;
            }
            assert newX != null;
            // 评价新序列
            double newPathLen = TSP_Util.calcPathLen(newX, distances);
            // 更新解和分数
            if (newPathLen < curSolution.getPathLen()) {
                destroyScores[destroyIndex] = lambda * destroyScores[destroyIndex] + (1 - lambda) * w2;
                repairScores[repairIndex] = lambda * repairScores[repairIndex] + (1 - lambda) * w2;
                curSolution = new TSP_Solution(newPathLen, newX);
                // 更新全局最优解
                if (newPathLen < bestSolution.getPathLen()) {
                    destroyScores[destroyIndex] = lambda * destroyScores[destroyIndex] + (1 - lambda) * w1;
                    repairScores[repairIndex] = lambda * repairScores[repairIndex] + (1 - lambda) * w1;
                    bestSolution = new TSP_Solution(newPathLen, newX);
                }
            } else {
                // 接受准则(模拟退火 Metropolis 准则)
                if (random.nextDouble() <= Math.exp((curSolution.getPathLen() - newPathLen) / c)) {
                    destroyScores[destroyIndex] = lambda * destroyScores[destroyIndex] + (1 - lambda) * w3;
                    repairScores[repairIndex] = lambda * repairScores[repairIndex] + (1 - lambda) * w3;
                    curSolution = new TSP_Solution(newPathLen, newX);
                } else {
                    destroyScores[destroyIndex] = lambda * destroyScores[destroyIndex] + (1 - lambda) * w4;
                    repairScores[repairIndex] = lambda * repairScores[repairIndex] + (1 - lambda) * w4;
                }
            }
            c *= alphaCooling;
        }
        // 输出结果
        System.out.println("destroyScores: " + Arrays.toString(destroyScores));
        System.out.println("repairScores: " + Arrays.toString(repairScores));
        System.out.println("最终找到的最优解为: " + bestSolution);
        System.out.println("求解用时: " + (System.currentTimeMillis() - startTime) / 1000d + " s");
        return bestSolution;
    }

    // 轮盘赌：传入权重（分数）向量，根据权重随机一个索引并返回（flag：true，销毁；否则，修复）
    private int roulette(double[] scores) {
        double sum = 0;
        for (double s : scores) {
            sum += s;
        }
        double[] cumulativeProbabilityArr = new double[scores.length];
        for (int i = 0; i < cumulativeProbabilityArr.length; i++) {
            cumulativeProbabilityArr[i] = scores[i] / sum;
            if (i - 1 >= 0) {
                cumulativeProbabilityArr[i] += cumulativeProbabilityArr[i - 1];
            }
        }
        double r = random.nextDouble();
        for (int i = 0; i < cumulativeProbabilityArr.length; i++) {
            if (r <= cumulativeProbabilityArr[i]) {
                return i;
            }
        }
        return -1;
    }

    // 销毁算子1：随机删除k个点
    private PartX destroyOperator1(int[] X) {
        PartX partX = new PartX();
        int k = random.nextInt(X.length);
        boolean[] removed = new boolean[X.length];
        for (int i = k; i > 0; i--) {
            int r = random.nextInt(X.length);
            while (removed[r]) {
                r = random.nextInt(X.length);
            }
            removed[r] = true;
        }
        for (int i = 0; i < removed.length; i++) {
            if (removed[i]) {
                partX.removeIndexList.add(i);
            } else {
                partX.indexList.add(i);
            }
        }
        return partX;
    }

    // 销毁算子2：随机删除序列中的一个片段
    private PartX destroyOperator2(int[] X) {
        PartX partX = new PartX();
        int left = random.nextInt(X.length);
        int right = random.nextInt(X.length);
        // 确保 left <= right
        if (left > right) {
            right = left ^ right;
            left = left ^ right;
            right = left ^ right;
        }
        for (int i = 0; i < X.length; i++) {
            if (i >= left && i <= right) {
                partX.removeIndexList.add(i);
            } else {
                partX.indexList.add(i);
            }
        }
        return partX;
    }

    // 销毁算子3：破坏一对交叉的边
    private PartX destroyOperator3(int[] X) {
        PartX partX = new PartX();
        for (int left1 = 0; left1 < n; left1++) {
            int right1 = left1 + 1 >= n ? 0 : left1 + 1;
            for (int left2 = right1 + 1; left2 < n; left2++) {
                int right2 = left2 + 1 >= n ? 0 : left2 + 1;
                // 判断两个边(线段)是否相交
                if (isIntersect(locations[left1], locations[right1], locations[left2], locations[right2])) {
                    for (int i = 0; i < n; i++) {
                        if (i == left1 || i == left2 || i == right1 || i == right2) {
                            partX.removeIndexList.add(i);
                        } else {
                            partX.indexList.add(i);
                        }
                    }
                    return partX;
                }
            }
        }
        // 如果没有交叉的边，那就 destroyOperator1
        return destroyOperator1(X);
    }

    // 判断两个边(线段)是否相交
    private boolean isIntersect(double[] line1_p1, double[] line1_p2, double[] line2_p1, double[] line2_p2) {
        // 判断两个线段是否相交（共线，顶点相交的情况不算，如果要考虑，只需要把跨立实验的>=改为>即可）
        // 快速排斥实验
        if ((Math.max(line1_p1[0], line1_p2[0])) < (Math.min(line2_p1[0], line2_p2[0])) ||
                (Math.max(line1_p1[1], line1_p2[1])) < (Math.min(line2_p1[1], line2_p2[1])) ||
                (Math.max(line2_p1[0], line2_p2[0])) < (Math.min(line1_p1[0], line1_p2[0])) ||
                (Math.max(line2_p1[1], line2_p2[1])) < (Math.min(line1_p1[1], line1_p2[1]))) {
            return false;
        }
        // 跨立实验
        if ((((line1_p1[0] - line2_p1[0]) * (line2_p2[1] - line2_p1[1]) - (line1_p1[1] - line2_p1[1]) * (line2_p2[0] - line2_p1[0])) *
                ((line1_p2[0] - line2_p1[0]) * (line2_p2[1] - line2_p1[1]) - (line1_p2[1] - line2_p1[1]) * (line2_p2[0] - line2_p1[0]))) >= 0 ||
                (((line2_p1[0] - line1_p1[0]) * (line1_p2[1] - line1_p1[1]) - (line2_p1[1] - line1_p1[1]) * (line1_p2[0] - line1_p1[0])) *
                        ((line2_p2[0] - line1_p1[0]) * (line1_p2[1] - line1_p1[1]) - (line2_p2[1] - line1_p1[1]) * (line1_p2[0] - line1_p1[0]))) >= 0) {
            return false;
        }
        return true;
    }

    // 修复算子1：随机插入修复
    private int[] repairOperator1(PartX partX) {
        for (int removeIndex : partX.removeIndexList) {
            int randomInsertIndex = random.nextInt(partX.indexList.size() + 1);
            partX.indexList.add(randomInsertIndex, removeIndex);
        }
        int[] X = new int[n];
        int i = 0;
        for (int index : partX.indexList) {
            X[i++] = index;
        }
        return X;
    }

    // 修复算子2：启发式插入修复
    private int[] repairOperator2(PartX partX) {
        for (int removeIndex : partX.removeIndexList) {
            if (partX.indexList.isEmpty()) {
                partX.indexList.add(removeIndex);
                continue;
            }
            // 由于插入到第一个和最后一个是等效的，所以 insertIndex < partX.indexList.size() 即可
            int bestInsertIndex = -1;
            double bestObj = 0;
            for (int insertIndex = 0; insertIndex < partX.indexList.size(); insertIndex++) {
                int left = (insertIndex - 1 < 0 ? n - 1 : insertIndex - 1);
                int right = insertIndex;
                double obj = 0d;
                obj += TSP_Util.calcDistance(locations[left], locations[removeIndex]);
                obj += TSP_Util.calcDistance(locations[removeIndex], locations[right]);
                if (bestInsertIndex < 0 || bestObj > obj) {
                    bestInsertIndex = insertIndex;
                    bestObj = obj;
                }
            }
            partX.indexList.add(bestInsertIndex, removeIndex);
        }
        int[] X = new int[n];
        int i = 0;
        for (int index : partX.indexList) {
            X[i++] = index;
        }
        return X;
    }

    // 修复算子3：直接接到后面
    private int[] repairOperator3(PartX partX) {
        int[] X = new int[n];
        int i = 0;
        for (int index : partX.indexList) {
            X[i++] = index;
        }
        for (int index : partX.removeIndexList) {
            X[i++] = index;
        }
        return X;
    }

    // 初始化操作
    private void init(TSP_Instance tspInstance) {
        n = tspInstance.getN();
        destroyScores = new double[3];
        repairScores = new double[3];
        Arrays.fill(destroyScores, 1);
        Arrays.fill(repairScores, 1);
        locations = tspInstance.getLocations();
        random = seed == null ? new Random() : new Random(seed);
        // 计算距离矩阵
        distances = new double[n][n];
        for (int i = 0; i < distances.length; i++) {
            for (int j = i + 1; j < distances.length; j++) {
                // i到j等于j到i
                distances[i][j] = TSP_Util.calcDistance(locations[i], locations[j]);
                distances[j][i] = distances[i][j];
            }
        }
        // 生成初始解，为简单起见，我们考虑使用字典顺序的生成初始解决方案
        curSolution = new TSP_Solution();
        curSolution.setPath(new int[n]);
        for (int i = 0; i < n; i++) {
            curSolution.getPath()[i] = i;
        }
        curSolution.setPathLen(TSP_Util.calcPathLen(curSolution.getPath(), distances));
        bestSolution = curSolution.copy();
    }

    // 部分序列对象
    static class PartX {
        LinkedList<Integer> indexList = new LinkedList<>();
        LinkedList<Integer> removeIndexList = new LinkedList<>();
    }
}
