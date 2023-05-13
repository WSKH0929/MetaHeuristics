package com.wskh.run;

import com.wskh.classes.tsp.TSP_Instance;
import com.wskh.meta_heuristics.simulated_annealing.tsp.TSP_Solver_SA;
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
        // 测试不同启发式算法
        System.out.println("------------------------- 模拟退火算法求解TSP问题 -----------------------------");
        new TSP_Solver_SA().solve(tspInstance);
    }
}
