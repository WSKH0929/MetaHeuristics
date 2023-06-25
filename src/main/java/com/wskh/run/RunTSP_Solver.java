package com.wskh.run;

import com.wskh.classes.tsp.TSP_Instance;
import com.wskh.meta_heuristics.ALNS.tsp.TSP_Solver_ALNS;
import com.wskh.meta_heuristics.ILS.tsp.TSP_Solver_ILS;
import com.wskh.meta_heuristics.SA.tsp.TSP_Solver_SA;
import com.wskh.meta_heuristics.TS.tsp.TSP_Solver_TS;
import com.wskh.meta_heuristics.VNS.tsp.TSP_Solver_VNS;
import com.wskh.utils.TSP_Util;

import java.io.IOException;

/**
 * @Author：WSKH
 * @ClassName：RunTSP
 * @Description：
 * @Time：2023/5/13/22:32
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
public class RunTSP_Solver {
    public static void main(String[] args) throws IOException {
        // 读取tsp数据
        TSP_Instance tspInstance = TSP_Util.readTSP_Instance("data/tsp/att48.tsp");
        // 固定使用随机数种子
        Long seed = 2023L;
        System.out.println("------------------------- 模拟退火算法求解TSP问题 -----------------------------");
        new TSP_Solver_SA(seed, 0.1, 1.1, 0.9, 5000, 100, 10).solve(tspInstance);
        System.out.println("------------------------- 禁忌搜索算法求解TSP问题 -----------------------------");
        new TSP_Solver_TS(seed, 30, 5000, 200).solve(tspInstance);
        System.out.println("------------------------- 变邻域搜索算法求解TSP问题 -----------------------------");
        new TSP_Solver_VNS(seed, 2, 2, 20000, 40).solve(tspInstance);
        System.out.println("------------------------- 自适应大邻域搜索算法求解TSP问题 -----------------------------");
        new TSP_Solver_ALNS(seed, 0.6, 1.5, 1.2, 0.8, 0.1, 100000, 100).solve(tspInstance);
        System.out.println("------------------------- 迭代局部搜索算法求解TSP问题 -----------------------------");
        new TSP_Solver_ILS(seed, 40000, 100, 6).solve(tspInstance);
    }
}
