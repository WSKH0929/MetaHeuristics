package com.wskh.utils;

import com.wskh.classes.tsp.TSP_Instance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Author：WSKH
 * @ClassName：TSP_Util
 * @Description：
 * @Time：2023/5/13/22:15
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
public class TSP_Util {
    // 读取tsp数据
    public static TSP_Instance readTSP_Instance(String path) throws IOException {
        TSP_Instance tspInstance = new TSP_Instance();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        String line = null;
        int row = 1;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("NAME")) {
                tspInstance.setName(line.split(" : ")[1]);
            } else if (line.contains("DIMENSION")) {
                tspInstance.setN(Integer.parseInt(line.split(" : ")[1]));
                tspInstance.setLocations(new double[tspInstance.getN()][2]);
            } else if (row >= 7 && row < 7 + tspInstance.getN()) {
                String[] split = line.split(" ");
                int index = Integer.parseInt(split[0]) - 1;
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                tspInstance.getLocations()[index][0] = x;
                tspInstance.getLocations()[index][1] = y;
            }
            row++;
        }
        bufferedReader.close();
        return tspInstance;
    }

    // 计算两点之间的欧式距离
    public static double calcDistance(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(Math.pow(p1x - p2x, 2) + Math.pow(p1y - p2y, 2));
    }

}