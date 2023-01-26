package HaroldHC.rp_chest_filter;


import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.LootContext;
import com.seedfinding.mcfeature.loot.MCLootTables;
import com.seedfinding.mcfeature.loot.item.ItemStack;
import com.seedfinding.mcfeature.structure.RuinedPortal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class rp_filter extends Thread{
    private Thread threading;
    ChunkRand rand = new ChunkRand();
    boolean debugging = false;          // ��������

    long seed_start;
    long seed_end;
    String target;
    int num;
    String thread_name;
    String save_dir;
    String save_name;

    rp_filter(long seed_start, long seed_end, String target, int num, String thread_name, String save_dir, String save_name){
        this.seed_start = seed_start;
        this.seed_end = seed_end;
        this.target = target;
        this.num = num;
        this.thread_name = thread_name;
        this.save_dir = save_dir;
        this.save_name = save_name;
    }

    public void start(){
        if(threading == null){
            threading = new Thread(this, thread_name);
            threading.start();
        }
    }

    // ������
    public void run() {
        StringBuilder output = new StringBuilder();     // �ļ�����

        long seed_now = seed_start;
        long times = 0L;
        while(seed_now< seed_end){
            times ++;
            //            System.out.println("Thread_"+thread_name+": "+seed_now);

            rp_filter run_test = this;   // (ʵ����)
            CPos[] rp = run_test.get_rp_list(seed_now); // �õ�һ�ѷ���
            CPos res = run_test.loot_chest_list(seed_now, rp, target, num);     // ɸ����

            boolean looted = res.getX() != Integer.MAX_VALUE && res.getZ() != Integer.MAX_VALUE;    // �Ƿ����
            if(looted){
                System.out.println(seed_now+" looted");
                BPos rp_position = res.toBlockPos();    // ����λ��
                System.out.println(rp_position);
                output.append(seed_now);
                output.append(' ');
                output.append(rp_position.toString());
                output.append('\n');
            }

            seed_now ++;
        }
        System.out.println("thread_"+thread_name+":"+times);

        StringBuilder file_name = new StringBuilder();
        try {
            // �����ļ������Ҿ��ÿ��Լ��һ�㣬����������
            file_name.append(save_dir);
            file_name.append("\\");
            file_name.append(save_name);
            file_name.append("_");
            file_name.append(thread_name);
            file_name.append(".txt");
            BufferedWriter write = new BufferedWriter(new FileWriter(file_name.toString()));
            write.write(output.toString());
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ��ȡ��ԭ������ķ���
    public CPos get_rp(long seed) {
        if(debugging){System.out.println("\ngetting ruined portal:");}
        RuinedPortal rp = new RuinedPortal(Dimension.OVERWORLD, MCVersion.v1_16_1);
        // ����4������
        CPos rp_1 = rp.getInRegion(seed, 0, 0, rand);
        CPos rp_2 = rp.getInRegion(seed, -1, 0, rand);
        CPos rp_3 = rp.getInRegion(seed, -1, -1, rand);
        CPos rp_4 = rp.getInRegion(seed, 0, -1, rand);

        List<CPos> rp_position_list = new ArrayList<>(Arrays.asList(rp_1, rp_2, rp_3, rp_4));

        double closest_dist = Integer.MAX_VALUE;
        CPos closest_rp = new CPos(0, 0);
        int times = 0;
        for (CPos rp_now : rp_position_list){
            times ++;
            double dist_now = rp_now.toBlockPos().distanceTo(new BPos(0, 0, 0), DistanceMetric.EUCLIDEAN);
            if(debugging){System.out.println("distance from (0, 0) to rp "+times+": "+dist_now);}

            if(dist_now<closest_dist){
                closest_dist = dist_now;
                closest_rp = rp_now;
            }
        }
        if(debugging){System.out.println("closest rp: "+closest_rp.toBlockPos());}
        System.out.println(closest_rp.getX()+" "+closest_rp.getZ());
        // filter the position
        if(closest_rp.getX()>16 || closest_rp.getZ()>16 || closest_rp.getX()<-16 || closest_rp.getZ()<-16){
            closest_rp = null;
        }

        return closest_rp;
    }

    // ����ԭ����Χ4������
    public CPos[] get_rp_list(long seed){
        if(debugging){System.out.println("\ngetting ruined portal:");}
        RuinedPortal rp = new RuinedPortal(Dimension.OVERWORLD, MCVersion.v1_16_1);
        CPos rp_1 = rp.getInRegion(seed, 0, 0, rand);
        CPos rp_2 = rp.getInRegion(seed, -1, 0, rand);
        CPos rp_3 = rp.getInRegion(seed, -1, -1, rand);
        CPos rp_4 = rp.getInRegion(seed, 0, -1, rand);

        return new CPos[]{rp_1, rp_2, rp_3, rp_4};
    }

    // ɸ����
    public boolean loot_chest(long seed, CPos rp_chest, String target, int num) {
        if(debugging){System.out.println("\nlooting chest");}
        if(debugging){System.out.println("rp_position"+rp_chest.toBlockPos());}

        // ��������
        rand.setDecoratorSeed(seed, rp_chest.getX() * 16, rp_chest.getZ() * 16, 40005, MCVersion.v1_16_1);
        LootContext a1 = new LootContext(rand.nextLong());

        // �õ�ս��Ʒ
        List<ItemStack> ItemList = MCLootTables.RUINED_PORTAL_CHEST.get().generate(a1);
        boolean is_looted = false;
        for (ItemStack itemStack : ItemList) {
            if(debugging){System.out.println(itemStack.getCount() + " " + itemStack.getItem().getName());}  // �������
            if(itemStack.getItem().getName().equals(target)){   // �����
                if(itemStack.getCount()>=num){      // �����
                    is_looted = true;
                }
            }
        }
        if(debugging){System.out.println("looting result: "+is_looted);}

        return is_looted;
    }

    public CPos loot_chest_list(long seed, CPos[] rp_list, String target, int num) {
        int times = 0;  // ѭ������
        for(CPos rp_chest : rp_list) {
            times ++;
            if(debugging){System.out.println("\nlooting rp_"+times);}
            if(debugging){System.out.println("looting chest");}
            if(debugging){System.out.println("rp_position"+rp_chest.toBlockPos());}

            // ��������
            rand.setDecoratorSeed(seed, rp_chest.getX() * 16, rp_chest.getZ() * 16, 40005, MCVersion.v1_16_1);
            LootContext a1 = new LootContext(rand.nextLong());

            // �õ�ս��Ʒ
            List<ItemStack> ItemList = MCLootTables.RUINED_PORTAL_CHEST.get().generate(a1);
            for (ItemStack itemStack : ItemList) {
                if(debugging){System.out.println(itemStack.getCount() + " " + itemStack.getItem().getName());}
                if(itemStack.getItem().getName().equals(target)){   // �����
                    if(itemStack.getCount()>=num){      // �����
                        return rp_chest;    // �ͷ��ط���λ��
                    }
                }
            }
        }
        return new CPos(Integer.MAX_VALUE, Integer.MAX_VALUE);  // ���򷵻�һ���ر�����ֵ
    }
}
