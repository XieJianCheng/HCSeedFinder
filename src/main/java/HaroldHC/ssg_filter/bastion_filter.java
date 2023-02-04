package HaroldHC.ssg_filter;

import com.seedfinding.mcbiome.source.NetherBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.BastionRemnant;

public class bastion_filter {
    public boolean is_in_area(long seed, ChunkRand rand, int sh_x, int sh_z) {
        BastionRemnant br = new BastionRemnant(MCVersion.v1_16_5);

        int region_x = sh_x>0?0:-1;
        int region_z = sh_z>0?0:-1;
        CPos br_position = br.getInRegion(seed, region_x, region_z, rand);
//        System.out.println("sh: "+sh_x+" "+sh_z);
//        System.out.println("region: "+region_x+" "+region_z);
//        System.out.println("bastion: "+br_position);
        boolean in_area = false;
        if(br_position != null){
            BPos br_block = br_position.toBlockPos();
            int br_x = br_block.getX(), br_z = br_block.getZ();
            if(((sh_x*2>0 && br_x<sh_x*2) || (sh_x*2<0 && br_x>sh_x*2))  && ((sh_z*2>0 && br_z<sh_z*2) || (sh_z*2<0 && br_z>sh_z*2))){
                in_area = true;
            }
        }
        return in_area;
    }

    public boolean check_biome(long seed, int sh_x, int sh_z){
        NetherBiomeSource nbs = new NetherBiomeSource(MCVersion.v1_16_5, seed);
        return nbs.getBiome(new BPos(sh_x*2, 32, sh_z*2)).getName().equals("nether_wastes") ||
                nbs.getBiome(new BPos(sh_x*2, 32, sh_z*2)).getName().equals("warped_forest");
    }
}
