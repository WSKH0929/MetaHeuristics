package com.wskh.meta_heuristics.simulated_annealing.tsp;

import com.wskh.classes.tsp.TSP_Instance;
import lombok.Data;

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



    // 构造函数
    public TSP_Solver_SA() {

    }

    // 城市数量
    int n;
    // 城市坐标
    double[][] locations;

    // 求解函数
    public void solve(TSP_Instance tspInstance) {
        long startTime = System.currentTimeMillis();
        // 初始化操作
        init(tspInstance);
        // 输出结果
        System.out.println("求解用时: " + (System.currentTimeMillis() - startTime) / 1000d + " s");
    }

    // 初始化操作
    private void init(TSP_Instance tspInstance) {
        n = tspInstance.getN();
        locations = tspInstance.getLocations();
    }

}
