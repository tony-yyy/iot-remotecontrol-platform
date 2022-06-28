package com.tony;

import com.tony.domain.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

public class BookServiceTest {
    // 查书
    @Test
    public void test01(){
/*        ApplicationContext cxac = new ClassPathXmlApplicationContext("applicationContext.xml");
        RecipeService recipeService = (RecipeService)cxac.getBean("RecipeServiceImpl");
        User user = new User();
        user.setId(1);
        RecipeDetail recipeDetailById = recipeService.getRecipeDetailById(1, user);
        System.out.println(recipeDetailById);*/

//        RecipeDetail recipeDetail = new RecipeDetail();
    }
    /*
    *              * 1
    *             ***  3(2+1)
    *            *****  5(2+2+1)
    *           *******  7(2+2+2+1)
    *          *********  9 ...
    *   2 * (行数 - 1) + 1 = *个数
    *   (最后一行*数 - 当前行*数) / 2 = 当前行空格数
    * */
    @Test
    public void test02(){
//        Scanner scanner = new Scanner(System.in);
//        int x = scanner.nextInt();
        int x = 10;
        int max = (2*(x-1)+1);
        System.out.println();
        for (int i=1; i<=x; i++){
            for (int j=0; j<(max-(2*(i-1)+1))/2; j++){
                System.out.print(" ");
            }
            for (int j=0; j<(2*(i-1)+1); j++){
                System.out.print("*");
            }
            System.out.println();
        }
    }

    @Test
    public void test03(){
        User[] users = new User[2];
        users[0] = new User();
        users[0].setId(1);
        users[0].setPassword("asd");
        users[0].setUsername("dasdsa");
        System.out.println(users[0]);
    }
    @Test
    public void test04(){
        int[] arr = {10,20,66,44,50,55,67,88,98,105,145,455,650,751,845,941,1012};
        int[] newArr = new int[arr.length + 1];
        int num = 500;
        int p1 = 0;
        int p2 = 0;
        boolean flag = false;
        while (p1 != newArr.length-1){
            if (arr[p1]>num && flag==false){
                flag = true;
                newArr[p2++] = num;
            }
            newArr[p2] = arr[p1];
            p1++;
            p2++;
        }
        System.out.println(Arrays.toString(newArr));
    }

    /**
     * 二分法：[10, 20, 66, 44, 50, 55, 67, 88, 98, 105, 145, 455, 650, 751, 845, 941, 1012]
     *
     */
    @Test
    public void test05(){
        int[] arr = {10,20,66,44,50,55,67,88,98,105,145,455,650,751,845,941,1012};
        int num = 550;
        compare(arr, 0, arr.length-1, num);
    }
    public static void compare(int[] arr, int start, int end, int num){
        int center = start + (end-start)/2; // 中间数
        // 如果中间数以及中间的下一个数的区间是否包括：我要的数
        // 如果我要的数比区间大，往右边找
        // 如果我要的数比区间小，往左边找

        if (arr[center]<=num && arr[center+1]>=num && end-start==1){
            // 在区间内：闭区间
            System.out.println("the num"+ num +" between index:" + start + "("+ arr[start] + ")" + " and index:" + end + "("+arr[end]+")");
            return;
        }else if (arr[center]<num){
            // 区间比num小，往右找
            compare(arr, center, end, num);
        }else if (arr[center]>num){
            // 区间比num大，往左找
            compare(arr, start, center, num);
        }
        else if (arr[center]==num || arr[center+1]==num ){
            System.out.println("the num"+ num +" between index:" + start + "("+ arr[start] + ")" + " and index:" + end + "("+arr[end]+")");
            return;
        }
    }

    /**
     * 冒泡排序
     */
    @Test
    public void test06(){
//        int[] arr = {5, 56, 156, 15, 156, 1, 156, 123, 478, 13};
        int[] arr = {9,8,7,6,5,4,3,2,1,0};
        for (int j = 0; j < arr.length-1; j++) {
            for (int i = 0; i < arr.length-1-j; i++) {
                if (arr[i] > arr[i+1]){
                    // 当前数比后一个数大，交换
                    arr[i] = arr[i] ^ arr[i+1];
                    arr[i+1] = arr[i] ^ arr[i+1];
                    arr[i] = arr[i] ^ arr[i+1];
                }
            }
        }
        System.out.println(Arrays.toString(arr));
    }
}
