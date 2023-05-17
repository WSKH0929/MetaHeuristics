package com.wskh.meta_heuristics.SA.tsp;

import com.wskh.classes.tsp.TSP_Instance;
import com.wskh.classes.tsp.TSP_Solution;
import com.wskh.utils.TSP_Util;
import lombok.Data;

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
public class TSP_Solver_SA {

    // 随机数种子
    Long seed;
    // 初始温度 一般设置为一个大于0但是很接近0的数
    double c0 = 0.1;
    // 预热升温系数：一定要大于 1
    double alphaHeatingUp = 1.1;
    // 冷却降温系数：一般取 0.8 ~ 0.99
    double alphaCooling = 0.9;
    // 迭代次数
    int epochs = 5000;
    // 最大链长
    int maxChainLen = 100;
    // 最小链长
    int minChainLen = 10;

    // 构造函数
    public TSP_Solver_SA(Long seed, double c0, double alphaHeatingUp, double alphaCooling, int epochs, int maxChainLen, int minChainLen) {
        this.seed = seed;
        this.c0 = c0;
        this.alphaHeatingUp = alphaHeatingUp;
        this.alphaCooling = alphaCooling;
        this.epochs = epochs;
        this.maxChainLen = maxChainLen;
        this.minChainLen = minChainLen;
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
    // 当前温度
    double c;

    // 求解函数
    public TSP_Solution solve(TSP_Instance tspInstance) {
        long startTime = System.currentTimeMillis();
        // 初始化操作
        init(tspInstance);
        System.out.println("城市数量为: " + n);
        System.out.println("初始解为: " + bestSolution);
        // 模拟退火过程
        // 1. 预热过程
        preheatingProcess();
        System.out.println("预热过程找到的最优解为: " + bestSolution);
        // 2. 冷却过程
        coolingProcess();
        // 输出结果
        System.out.println("最终找到的最优解为: " + bestSolution);
        System.out.println("求解用时: " + (System.currentTimeMillis() - startTime) / 1000d + " s");
        return bestSolution;
    }

    // 预热过程
    private void preheatingProcess() {
        double acceptRate = 0d;
        // 预热，直到当前温度下的接受率接近 1
        while (1 - acceptRate > 1e-04) {
            acceptRate = localSearch(maxChainLen);
            c *= alphaHeatingUp;
        }
    }

    // 冷却过程
    private void coolingProcess() {
        for (int k = 0; k < epochs; k++) {
            // 计算当前链长
            double a = (double) k / epochs;
            int curChainLen = (int) Math.round(a * minChainLen + (1 - a) * maxChainLen);
            localSearch(curChainLen);
            c *= alphaCooling;
        }
    }

    // 局部搜索过程，给定链长，返回接受率
    private double localSearch(int chainLen) {
        double acceptCnt = 0d;
        for (int i = 0; i < chainLen; i++) {
            // 随机使用两个邻域算子构造新解
            int[] newPath = random.nextInt(2) == 0 ? neighborhoodOperator1(curSolution.getPath()) : neighborhoodOperator2(curSolution.getPath());
            double newPathLen = TSP_Util.calcPathLen(newPath, distances);
            if (newPathLen < curSolution.getPathLen() || random.nextDouble() <= Math.exp((curSolution.getPathLen() - newPathLen) / c)) {
                curSolution = new TSP_Solution(newPathLen, newPath);
                if (newPathLen < bestSolution.getPathLen()) {
                    bestSolution = curSolution.copy();
                }
                acceptCnt++;
            }
        }
        return acceptCnt / chainLen;
    }

    // 邻域算子1：在当前解向量 X 中随机交换两个位置
    private int[] neighborhoodOperator1(int[] X) {
        int[] newX = X.clone();
        int i = random.nextInt(n);
        int j = random.nextInt(n);
        while (i == j) {
            j = random.nextInt(n);
        }
        // 采用位运算交换 i 和 j 处的两个元素
        newX[j] = newX[i] ^ newX[j];
        newX[i] = newX[i] ^ newX[j];
        newX[j] = newX[i] ^ newX[j];
        return newX;
    }

    // 邻域算子2：交换两个随机选择的索引 (i,j) 之间的所有位置
    private int[] neighborhoodOperator2(int[] X) {
        int[] newX = X.clone();
        int i = random.nextInt(n);
        int j = random.nextInt(n);
        while (i == j) {
            j = random.nextInt(n);
        }
        // 确保 i < j
        if (i > j) {
            j = i ^ j;
            i = i ^ j;
            j = i ^ j;
        }
        // 交换 (i,j) 之间的所有位置
        int sum = i + j;
        int maxI = sum / 2;
        if (sum % 2 == 0) {
            maxI--;
        }
        for (; i <= maxI; i++) {
            newX[sum - i] = newX[i] ^ newX[sum - i];
            newX[i] = newX[i] ^ newX[sum - i];
            newX[sum - i] = newX[i] ^ newX[sum - i];
        }
        return newX;
    }

    // 初始化操作
    private void init(TSP_Instance tspInstance) {
        c = c0;
        n = tspInstance.getN();
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

}
