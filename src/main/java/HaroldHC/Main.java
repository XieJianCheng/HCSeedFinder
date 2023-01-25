package HaroldHC;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 读配置
        String option_dir = "rpf_opt.txt";
        Path path = Paths.get(option_dir);
        List<String> text = null;
        try {
            text = Files.readAllLines(path);

        } catch (IOException e) {
            e.printStackTrace();
        }

        assert text != null;
        int thread_num = Integer.parseInt(text.get(0));
        long seed_start = Integer.parseInt(text.get(1));
        long seeds_num = Integer.parseInt(text.get(2));
        String item_name = text.get(3);
        int item_count = Integer.parseInt(text.get(4));
        String save_dir = text.get(5);
        String save_name = text.get(6);

        System.out.println(thread_num);
        System.out.println(seed_start);
        System.out.println(seeds_num);
        System.out.println(item_name);
        System.out.println(item_count);

        long seed_thread = (seeds_num / thread_num);  // 每个线程筛几个种子

        // 循环变量
        int thread_now = 1;
        long seed_start_now = seed_start;
        long seed_end_now;
        // 多开
        while (thread_now <= thread_num) {
            seed_end_now = seed_start_now + seed_thread;  // 改变循环变量

            // 开始
            rp_filter runner = new rp_filter(seed_start_now, seed_end_now, item_name, item_count, String.valueOf(thread_now)
                    , save_dir, save_name);
            runner.start();

            // 改变循环变量
            seed_start_now += seed_thread;
            thread_now++;
        }
    }
}