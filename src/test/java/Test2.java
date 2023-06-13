import java.util.LinkedList;

/**
 * @Author：WSKH
 * @ClassName：Test2
 * @Description：
 * @Time：2023/6/13/10:16
 * @Email：1187560563@qq.com
 * @Blog：wskh0929.blog.csdn.net
 */
public class Test2 {
    public static void main(String[] args) {
        LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(1);
        linkedList.add(2);
        linkedList.add(3);
        linkedList.add(4);
        System.out.println(linkedList);
        test(0, new LinkedList<>(linkedList));
        test(1, new LinkedList<>(linkedList));
        test(2, new LinkedList<>(linkedList));
        test(3, new LinkedList<>(linkedList));
        test(4, new LinkedList<>(linkedList));
    }

    private static void test(int insertIndex, LinkedList<Integer> linkedList) {
        linkedList.add(insertIndex, 0);
        System.out.println(linkedList);
    }

}
