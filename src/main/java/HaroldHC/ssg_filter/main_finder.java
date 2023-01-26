package HaroldHC.ssg_filter;

import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;

import java.util.Objects;

public class main_finder {
    public static void main(String[] args) {
        HaroldHC.ssg_filter.ow_rp_filter orf = new HaroldHC.ssg_filter.ow_rp_filter();

        long seed_now = 5001L;
        long seed_target = 15000L;
        ChunkRand rand = new ChunkRand();
        while (seed_now <= seed_target) {
            CPos rp = orf.get_closest_rp(seed_now, rand);
            if (!Objects.equals(rp.toBlockPos(), new BPos(4096, 0, 4096))) {
                System.out.println(seed_now + " " + rp.toBlockPos());
                Boolean is_looted = orf.loot_chest(seed_now, rand, rp);
                System.out.println(is_looted);
            }
            seed_now++;
//        OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed_now);
//        System.out.println(obs.getBiome(115111112, 64, 921111111).getName());
        }
    }
}
