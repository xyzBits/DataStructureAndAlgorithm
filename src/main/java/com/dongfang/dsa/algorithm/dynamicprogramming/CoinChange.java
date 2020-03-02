package com.dongfang.dsa.algorithm.dynamicprogramming;

import org.junit.Test;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.util.Arrays;

public class CoinChange {
    /**
     * 练习2--零钱兑换
     * 				假设有25分、5分、1分的硬币，现在要找给客户41分的零钱，如何办到硬币的个数最少？
     * 					贪心策略：每一次都优先选择面值最大的硬币
     * 						选择25分 剩余16分
     * 						选择10分 剩余6分
     * 						选择5分  剩余1分
     * 						选择一分
     * 						共需要4枚
     * 				最优解是错误的
     * 				face = 25
     *              face = 5
     *              face = 5
     *              face = 5
     *              face = 1
     *              coins = 5
     */
    @Test
    public void testCoinChange() {
        // 硬币面值，可以不断地进行重复选择，优先选择能选的面值里最大的
        Integer[] faces = {25, 20, 5, 1};
        // 传入比较器，从大到小排序
        Arrays.sort(faces, (o1, o2) -> o2 - o1);
        System.out.println("Arrays.toString(faces) = " + Arrays.toString(faces));
        // 要找的钱数，硬币数量
        int money = 41, coins = 0;
        for (Integer face : faces) {
            // 选一枚硬币，不断地重复选择，只要剩余的大于当前面值
            while (money >= face) {
                money -= face;
                coins++;
                System.out.println("face = " + face);
            }
        }
        System.out.println("coins = " + coins);
    }


    /**
     * 暴力递归，自顶向下的调用，出现了重叠子问题
     * 类似于斐波那切数列的递归版，会有大量的重复计算，时间复杂度较高
     * @param n
     * @return
     */
    public int coinChangeByBruteRecursive(int n) {
        if (n < 1) return Integer.MAX_VALUE;
        if (n == 25 || n == 20 || n == 5 || n == 1) return 1;
        int min1 = Math.min(coinChangeByBruteRecursive(n - 25), coinChangeByBruteRecursive(n - 20));
        int min2 = Math.min(coinChangeByBruteRecursive(n - 5), coinChangeByBruteRecursive(n - 1));
        return Math.min(min1, min2) + 1;
    }

    @Test
    public void testCoinChangeByRecursive() {
        int count = coinChangeByBruteRecursive(41);
        System.out.println("count = " + count);
        int res = coinChangeByBruteRecursiveOpt(19);
        System.out.println("res = " + res);
    }

    /**
     *      dp[16] 要凑够16分硬币所需要的最少硬币个数
     * 记忆化搜索（自顶向下的调用）
     * @param n
     * @return
     */
    public int coinChangeByBruteRecursiveOpt(int n) {
        if (n < 1) return -1;
        int[] dp = new int[n + 1];
        int[] faces = {1, 20, 5, 25};
        for (int face : faces) {
            // 有可能n比其中的面值小，把比所需要的钱数大的面值过滤
            if (n < face) continue;
            dp[face] = 1;
        }
        return helper(n, dp);
    }

    private int helper(int n, int[] dp) {
        if (n < 1) return Integer.MAX_VALUE;
        if (dp[n] == 0) {
            int min1 = Math.min(helper(n - 25, dp), helper(n - 20, dp));
            int min2 = Math.min(helper(n - 5, dp), helper(n - 1, dp));
            dp[n] = Math.min(min1, min2) + 1;
        }
        return dp[n];
    }

    /**
     * 使用递推，自底上向
     *      思路：
     *          1、求凑够n分的时候，先求 凑够 n-25 n-20 n-5 n-1最少要几个硬币，再取最小值
     *          2、所以要先求出凑够小的要几个硬币
     * @return
     */
    public int coinChangeByIterator(int n) {
        if (n < 1) return -1;
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
//            dp[i] = min{dp[i - 25], dp[i - 20], dp[i - 5], dp[i - 1]} + 1; 求dp[i]的公式
            int min = Integer.MAX_VALUE;
            // 不考虑数组越界，这样写 dp[0] = 0
            if (i >= 1) min = Math.min(min, dp[i - 1]);
            if (i >= 5) min = Math.min(min, dp[i - 5]);
            if (i >= 20) min = Math.min(min, dp[i - 20]);
            if (i >= 25) min = Math.min(min, dp[i - 25]);

            dp[i] = min + 1;
        }
        return dp[n];
    }

    /**
     * [0, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 20, 1, 1, 1, 1, 25, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 20, 1]
     * 凑1分，最后一次选择1分
     * 凑5分，最后一次选择5分
     * 凑21分，最后一次选择1分，，还剩20分，再往前一次选择20分
     * @param n
     * @return
     */
    public int coinChangeByIteratorVersion1(int n) {
        if (n < 1) return -1;
        int[] dp = new int[n + 1];
        // faces[i]是凑够i分时，最后选择的那个硬币的面值
        int[] faces = new int[dp.length];
        for (int i = 1; i <= n; i++) {
            int min = Integer.MAX_VALUE;
            if (i >= 1 && dp[i - 1] < min) {
                min = dp[i - 1];
                faces[i] = 1;
            }
            if (i >= 5 && dp[i - 5] < min) {
                min = dp[i - 5];
                faces[i] = 5;
            }
            if (i >= 20 && dp[i - 20] < min) {
                min = dp[i - 20];
                faces[i] = 20;
            }
            if (i >= 25 && dp[i - 25] < min) {
                min = dp[i - 25];
                faces[i] = 25;
            }

            dp[i] = min + 1;
            printFaces(faces, i);
        }

        return dp[n];
    }

    /**
     * 凑够 1分的选择是 [ 1  ]
     * 凑够 2分的选择是 [ 1 1  ]
     * 凑够 3分的选择是 [ 1 1 1  ]
     * 凑够 4分的选择是 [ 1 1 1 1  ]
     * 凑够 5分的选择是 [ 5  ]
     * 凑够 6分的选择是 [ 1 5  ]
     * 凑够 7分的选择是 [ 1 1 5  ]
     * 凑够 8分的选择是 [ 1 1 1 5  ]
     * 凑够 9分的选择是 [ 1 1 1 1 5  ]
     * 凑够 10分的选择是 [ 5 5  ]
     * 凑够 11分的选择是 [ 1 5 5  ]
     * 凑够 12分的选择是 [ 1 1 5 5  ]
     * 凑够 13分的选择是 [ 1 1 1 5 5  ]
     * 凑够 14分的选择是 [ 1 1 1 1 5 5  ]
     * 凑够 15分的选择是 [ 5 5 5  ]
     * 凑够 16分的选择是 [ 1 5 5 5  ]
     * 凑够 17分的选择是 [ 1 1 5 5 5  ]
     * 凑够 18分的选择是 [ 1 1 1 5 5 5  ]
     * 凑够 19分的选择是 [ 1 1 1 1 5 5 5  ]
     * 凑够 20分的选择是 [ 20  ]
     * 凑够 21分的选择是 [ 1 20  ]
     * 凑够 22分的选择是 [ 1 1 20  ]
     * 凑够 23分的选择是 [ 1 1 1 20  ]
     * 凑够 24分的选择是 [ 1 1 1 1 20  ]
     * 凑够 25分的选择是 [ 25  ]
     * 凑够 26分的选择是 [ 1 25  ]
     * 凑够 27分的选择是 [ 1 1 25  ]
     * 凑够 28分的选择是 [ 1 1 1 25  ]
     * 凑够 29分的选择是 [ 1 1 1 1 25  ]
     * 凑够 30分的选择是 [ 5 25  ]
     * 凑够 31分的选择是 [ 1 5 25  ]
     * 凑够 32分的选择是 [ 1 1 5 25  ]
     * 凑够 33分的选择是 [ 1 1 1 5 25  ]
     * 凑够 34分的选择是 [ 1 1 1 1 5 25  ]
     * 凑够 35分的选择是 [ 5 5 25  ]
     * 凑够 36分的选择是 [ 1 5 5 25  ]
     * 凑够 37分的选择是 [ 1 1 5 5 25  ]
     * 凑够 38分的选择是 [ 1 1 1 5 5 25  ]
     * 凑够 39分的选择是 [ 1 1 1 1 5 5 25  ]
     * 凑够 40分的选择是 [ 20 20  ]
     * 凑够 41分的选择是 [ 1 20 20  ]
     * @param faces
     * @param n
     */
    private void printFaces(int[] faces, int n) {
        // faces[n]是凑够n分，最后一次选择的硬币的面值
        System.out.print("凑够 " + n + "分的选择是 [ ");
        while (n > 0) {
            System.out.print(faces[n] + " ");
            // 已经选择了faces[n]还剩余 n - faces[n]
            n = n - faces[n];
        }
        System.out.println(" ]");

    }

    @Test
    public void testCoinChangeByIterator() {
        int res = coinChangeByIteratorVersion1(41);
        System.out.println("res = " + res);
    }


    public int coinChange(int[] coins, int amount) {
        if (amount < 1 || coins == null || coins.length == 0) return -1;
        int[] dp = new int[amount + 1];
        for (int i = 1; i <= amount; i++) {
            int min = Integer.MAX_VALUE;
            for (int coin : coins) {
                if (i >= coin) min = Math.min(dp[i - coin], min);
            }
            dp[i] = min + 1;
        }
        return dp[amount];
    }

    @Test
    public void testSelfDefineFaces() {
        int[] faces = {25, 20, 5, 1};
        int result = coinChange(faces, 41);
        System.out.println("result = " + result);
    }
}
