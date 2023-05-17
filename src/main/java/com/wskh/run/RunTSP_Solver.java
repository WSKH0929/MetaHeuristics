package com.wskh.run;

import com.wskh.classes.tsp.TSP_Instance;
import com.wskh.meta_heuristics.SA.tsp.TSP_Solver_SA;
import com.wskh.meta_heuristics.TS.tsp.TSP_Solver_TS;
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
    }
}
