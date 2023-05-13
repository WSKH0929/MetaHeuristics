package com.wskh.classes.tsp;

import lombok.Data;
import lombok.ToString;

/**
 * @Author：WSKH
 * @ClassName：TSP_Instance
 * @Description：
 * @Time：2023/5/13/22:20
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
@Data
@ToString
public class TSP_Instance {
    // 实例名
    String name;
    // 城市数量
    int n;
    // 每个城市的坐标
    double[][] locations;
}
