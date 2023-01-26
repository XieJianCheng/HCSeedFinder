package HaroldHC.ssg_filter;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.LootContext;
import com.seedfinding.mcfeature.loot.MCLootTables;
import com.seedfinding.mcfeature.loot.item.ItemStack;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcfeature.structure.RuinedPortal;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ow_rp_filter {
    private final boolean debugging = false;

    public CPos get_closest_rp(long seed, ChunkRand rand) {
        OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
        OverworldTerrainGenerator otg = new OverworldTerrainGenerator(obs);
        SpawnPoint sp = new SpawnPoint();
        BPos spawn_point = sp.getSpawnPoint(otg);
        if(debugging){System.out.println(spawn_point);}

        RuinedPortal rp = new RuinedPortal(Dimension.OVERWORLD, MCVersion.v1_16_1);
        // 生成4个废门
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
            double dist_now = rp_now.toBlockPos().distanceTo(spawn_point, DistanceMetric.EUCLIDEAN);
            if(debugging){System.out.println("distance from (0, 0) to rp "+times+": "+dist_now+" "+rp_now.toBlockPos());}
            if(dist_now<closest_dist){
                closest_dist = dist_now;
                closest_rp = rp_now;
            }
        }
        if(debugging){System.out.println("closest: "+closest_rp.toBlockPos()+" "+closest_dist);}

        String[] target_biomes = {"jungle", "plains", "birch_forest", "crimson_forest", "forest", "savanna"};
        boolean in_biome = false;
        for(String the_biome : target_biomes){
            if(obs.getBiome(closest_rp.toBlockPos()).getName().equals(the_biome)){
                in_biome = true;
            }
        }
        if(closest_dist<=120 && in_biome){
            return closest_rp;
        }else {
            return new CPos(256, 256);
        }
    }

    // 筛箱子
    public boolean loot_chest(long seed, ChunkRand rand, CPos rp_chest) {
        if(debugging){System.out.println("\nlooting chest");}
        if(debugging){System.out.println("rp_position"+rp_chest.toBlockPos());}

        // 加载箱子
        rand.setDecoratorSeed(seed, rp_chest.getX() * 16, rp_chest.getZ() * 16, 40005, MCVersion.v1_16_1);
        LootContext a1 = new LootContext(rand.nextLong());

        // 得到战利品
        List<ItemStack> ItemList = MCLootTables.RUINED_PORTAL_CHEST.get().generate(a1);
        boolean is_looted = false;
        int looted_count = 0;
        for (ItemStack itemStack : ItemList) {
            String name = itemStack.getItem().getName();
            int count = itemStack.getCount();
            if(debugging){System.out.println(name + " " + count);}

            if(name.equals("obsidian") && count>=3){
                looted_count ++;
            }else if((name.equals("fire_charge") && count>=2) || (name.equals("flint_and_steel") && count>=1)){
                looted_count ++;
            }else if(name.equals("golden_axe") && count>=1){
                looted_count ++;
            }else if(name.equals("golden_pickaxe") && count>=1){
                looted_count ++;
            }
        }
        if(looted_count>=4){
            is_looted = true;
        }
        if(debugging){System.out.println("looting result: "+is_looted);}
        return is_looted;
    }

}
