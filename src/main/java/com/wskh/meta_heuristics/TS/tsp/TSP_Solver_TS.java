package com.wskh.meta_heuristics.TS.tsp;

import com.wskh.classes.tsp.TSP_Instance;
import com.wskh.classes.tsp.TSP_Solution;
import com.wskh.utils.TSP_Util;
import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

/**
 * @Author：WSKH
 * @ClassName：TSP_Solver_TS
 * @Description：
 * @Time：2023/5/17/9:38
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
@Data
public class TSP_Solver_TS {
    // 随机数种子
    Long seed;
    // 禁忌长度
    int tabuLen = 30;
    // 迭代次数
    int epochs = 50000;
    // 局部搜索次数
    int localSearchCnt = 100;

    // 构造函数
    public TSP_Solver_TS(Long seed, int tabuLen, int epochs, int localSearchCnt) {
        this.seed = seed;
        this.tabuLen = tabuLen;
        this.epochs = epochs;
        this.localSearchCnt = localSearchCnt;
    }

    // 城市数量
    int n;
    // 城市坐标
    double[][] locations;
    // 距离矩阵
    double[][] distances;
    // 禁忌表（存储序列的哈希值）
    LinkedList<Integer> tabuList;
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
        // 禁忌搜索过程
        for (int i = 0; i < epochs; i++) {
            // 在当前解进行邻域搜索，获得最佳邻域解
            Object[] localSearchResult = localSearch();
            int localBestHashValue = (int) localSearchResult[0];
            TSP_Solution localBestSolution = (TSP_Solution) localSearchResult[1];
            // 最佳邻域解不为null时，进行位置更新
            if (localBestSolution != null) {
                // 更新当前解
                curSolution = localBestSolution;
                // 更新禁忌表
                putInTabuList(localBestHashValue);
                // 更新全局最优解
                if (curSolution.getPathLen() < bestSolution.getPathLen()) {
                    bestSolution = curSolution;
                }
            }
        }
        // 输出结果
        System.out.println("最终找到的最优解为: " + bestSolution);
        System.out.println("求解用时: " + (System.currentTimeMillis() - startTime) / 1000d + " s");
        return bestSolution;
    }

    // 在当前解进行邻域搜索，获得最佳邻域解
    private Object[] localSearch() {
        TSP_Solution localBestSolution = null;
        int localBestHashValue = -1;
        for (int j = 0; j < localSearchCnt; j++) {
            // 随机使用两个邻域算子构造新解
            int[] newPath = random.nextInt(2) == 0 ? neighborhoodOperator1(curSolution.getPath()) : neighborhoodOperator2(curSolution.getPath());
            int newHashValue = Arrays.hashCode(newPath);
            if (!isInTabuList(newHashValue)) {
                double newPathLen = TSP_Util.calcPathLen(newPath, distances);
                if (localBestSolution == null || newPathLen < localBestSolution.getPathLen()) {
                    localBestSolution = new TSP_Solution(newPathLen, newPath);
                    localBestHashValue = newHashValue;
                }
            }
        }
        return new Object[]{localBestHashValue, localBestSolution};
    }

    // 将解向量X的hash值加入禁忌表
    private void putInTabuList(int hashValue) {
        if (tabuList.size() == tabuLen) {
            tabuList.removeFirst();
        }
        tabuList.add(hashValue);
    }

    // 判断解向量X的hash值是否在禁忌表中
    private boolean isInTabuList(int hashValue) {
        for (int tabuHashValue : tabuList) {
            if (tabuHashValue == hashValue) {
                return true;
            }
        }
        return false;
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
        tabuList = new LinkedList<>();
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
