package HaroldHC.ssg_filter;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class main_finder {
    public void main_runner() {
        long seed_now = 15001L;
        long seed_target = 25000L;
        ChunkRand rand = new ChunkRand();

        HaroldHC.ssg_filter.ow_rp_filter orf = new HaroldHC.ssg_filter.ow_rp_filter();
        while (seed_now <= seed_target) {
            CPos rp = orf.get_closest_rp(seed_now, rand);
            if (!Objects.equals(rp.toBlockPos(), new BPos(4096, 0, 4096))) {
                boolean is_looted_1 = orf.loot_chest(seed_now, rand, rp, "obsidian", 3);
                boolean is_looted_2 = orf.loot_chest(seed_now, rand, rp, "gold_pickaxe", 1);
                boolean is_looted_3 = orf.loot_chest(seed_now, rand, rp, "golden_axe", 1);
                boolean is_looted_4 = orf.loot_chest(seed_now, rand, rp, "flint_and_steel", 1);
                boolean is_looted_5 = orf.loot_chest(seed_now, rand, rp, "fire_charge", 2);
                if(is_looted_1 && is_looted_2 && is_looted_3 && (is_looted_4 || is_looted_5)){
                    System.out.println(seed_now + " " + rp.toBlockPos());
                }
            }
            seed_now++;
        }
    }
}
