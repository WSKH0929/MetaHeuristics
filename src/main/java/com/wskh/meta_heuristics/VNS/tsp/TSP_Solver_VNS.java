package com.wskh.meta_heuristics.VNS.tsp;

import com.wskh.classes.tsp.TSP_Instance;
import com.wskh.classes.tsp.TSP_Solution;
import com.wskh.utils.TSP_Util;
import lombok.Data;

import java.util.*;

/**
 * @Author：WSKH
 * @ClassName：TSP_Solver_TS
 * @Description：
 * @Time：2023/5/17/9:38
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
@Data
public class TSP_Solver_VNS {
    // 随机数种子
    Long seed;
    // 迭代次数
    int epochs = 20000;
    // 局部搜索次数
    int localSearchCnt = 30;
    // VND参数
    int lMax = 2;
    // VNS参数
    int kMax = 2;

    // 构造函数
    public TSP_Solver_VNS(Long seed, int lMax, int kMax, int epochs, int localSearchCnt) {
        this.seed = seed;
        this.lMax = lMax;
        this.kMax = kMax;
        this.epochs = epochs;
        this.localSearchCnt = localSearchCnt;
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

    // 求解函数
    public TSP_Solution solve(TSP_Instance tspInstance) {
        long startTime = System.currentTimeMillis();
        // 初始化操作
        init(tspInstance);
        System.out.println("城市数量为: " + n);
        System.out.println("初始解为: " + bestSolution);
        // 变邻域搜索过程
        variableNeighborhoodSearch();
        // 输出结果
        System.out.println("最终找到的最优解为: " + bestSolution);
        System.out.println("求解用时: " + (System.currentTimeMillis() - startTime) / 1000d + " s");
        return bestSolution;
    }

    // 变邻域搜索过程
    public void variableNeighborhoodSearch() {
        for (int epoch = 0; epoch < epochs; epoch++) {
            int k = 1;
            while (k <= kMax) {
                // 震动过程（序列反转）
                TSP_Solution solution1 = shake(curSolution, k);
                // VND过程
                TSP_Solution solution2 = variableNeighborhoodDescent(solution1.copy());
                // 邻域切换过程
                if (solution2.getPathLen() < curSolution.getPathLen()) {
                    curSolution = solution2;
                    k = 1;
                    if (curSolution.getPathLen() < bestSolution.getPathLen()) {
                        bestSolution = curSolution.copy();
                    }
                } else {
                    k++;
                }
            }
        }
    }

    // VND过程
    public TSP_Solution variableNeighborhoodDescent(TSP_Solution solution) {
        int k = 1;
        while (k <= lMax) {
            TSP_Solution localBestSolution = solution.copy();
            // 局部搜索过程，搜索邻域k中的较优解
            for (int i = 0; i < localSearchCnt; i++) {
                int[] newX = null;
                if (k == 1) {
                    newX = neighborhoodOperator1(solution.getPath());
                } else if (k == 2) {
                    newX = neighborhoodOperator2(solution.getPath());
                } else {
                    throw new RuntimeException("超出neighborhood邻域集合长度: " + k);
                }
                double pathLen = TSP_Util.calcPathLen(newX, distances);
                if (pathLen < localBestSolution.getPathLen()) {
                    localBestSolution = new TSP_Solution(pathLen, newX);
                }
            }
            // 邻域切换过程
            if (localBestSolution.getPathLen() < solution.getPathLen()) {
                solution = localBestSolution;
                k = 1;
            } else {
                k++;
            }
        }
        return solution;
    }

    // 震动过程
    public TSP_Solution shake(TSP_Solution solution, int k) {
        int[] newX = null;
        if (k == 1) {
            newX = shakeOperator1(solution.getPath());
        } else if (k == 2) {
            newX = shakeOperator2(solution.getPath());
        } else {
            throw new RuntimeException("超出shake邻域集合长度: " + k);
        }
        double pathLen = TSP_Util.calcPathLen(newX, distances);
        return new TSP_Solution(pathLen, newX);
    }

    // 震动算子1
    private int[] shakeOperator1(int[] X) {
        return neighborhoodOperator1(X);
    }

    // 震动算子2
    private int[] shakeOperator2(int[] X) {
        return neighborhoodOperator2(X);
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
