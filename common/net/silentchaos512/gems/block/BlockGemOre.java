package net.silentchaos512.gems.block;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.gems.item.ModItems;
import net.silentchaos512.gems.lib.EnumGem;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.util.ModRecipeHelper;

public class BlockGemOre extends BlockGemSubtypes {

  public BlockGemOre(boolean dark) {

    super(dark, dark ? Names.GEM_ORE_DARK : Names.GEM_ORE);

    setHardness(3.0f);
    setResistance(15.0f);
    setSoundType(SoundType.STONE);
    setHarvestLevel("pickaxe", 2);
  }

  @Override
  public void addRecipes() {

    ItemStack ore, item;
    for (int i = 0; i < 16; ++i) {
      EnumGem gem = getGem(i);
      ore = gem.getOre();
      item = gem.getItem();
      // Smelting
      GameRegistry.addSmelting(ore, item, 0.5f);
      // SAG Mill
      ModRecipeHelper.addSagMillRecipe(gem.getGemName() + "Ore", ore, item,
          isDark ? "netherrack" : "cobblestone", 3000);
    }
  }

  @Override
  public void addOreDict() {

    for (int i = 0; i < 16; ++i) {
      EnumGem gem = getGem(i);
      OreDictionary.registerOre(gem.getOreOreName(), gem.getOre());
    }
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {

    return ModItems.gem;
  }

  @Override
  public int damageDropped(IBlockState state) {

    return getMetaFromState(state) + (isDark ? 16 : 0);
  }

  @Override
  public int quantityDroppedWithBonus(int fortune, Random random) {

    if (fortune > 0) {
      int j = random.nextInt(fortune + 2) - 1;

      if (j < 0) {
        j = 0;
      }

      return quantityDropped(random) * (j + 1);
    } else {
      return quantityDropped(random);
    }
  }

  @Override
  public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {

    Item item = getItemDropped(world.getBlockState(pos), RANDOM, fortune);
    if (item != Item.getItemFromBlock(this)) {
      return 1 + RANDOM.nextInt(5);
    }
    return 0;
  }
}
