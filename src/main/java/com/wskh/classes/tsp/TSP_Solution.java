package com.wskh.classes.tsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Arrays;

/**
 * @Author：WSKH
 * @ClassName：TSP_Solution
 * @Description：
 * @Time：2023/5/13/22:52
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TSP_Solution {
    // 路程长度
    double pathLen;
    // 路径
    int[] path;

    public TSP_Solution copy() {
        return new TSP_Solution(pathLen, path.clone());
    }

    @Override
    public String toString() {
        return "pathLen = " + pathLen + " , path = " + Arrays.toString(path);
    }
}
