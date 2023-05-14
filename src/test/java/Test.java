import java.util.Arrays;

/**
 * @Author：WSKH
 * @ClassName：Test
 * @Description：
 * @Time：2023/5/14/10:31
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
public class Test {
    public static void main(String[] args) {
        int[] arr = {0, 1, 2, 3, 4, 5, 6, 7};
        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.toString(neighborhoodOperator2(0, 4, arr)));
    }

    private static int[] neighborhoodOperator2(int i, int j, int[] X) {
        int[] newX = X.clone();
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

}
