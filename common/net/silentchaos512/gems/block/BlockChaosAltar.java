package net.silentchaos512.gems.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.client.gui.GuiHandlerSilentGems;
import net.silentchaos512.gems.item.ModItems;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.tile.TileChaosAltar;
import net.silentchaos512.lib.block.BlockContainerSL;

public class BlockChaosAltar extends BlockContainerSL {

  public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.0f, 0.0f, 0.0f, 1.0f, 0.75f,
      1.0f);

  public BlockChaosAltar() {

    super(1, SilentGems.MOD_ID, Names.CHAOS_ALTAR, Material.IRON);
    setHardness(12.0f);
    setResistance(6000.0f);
  }

  @Override
  public TileEntity createNewTileEntity(World world, int meta) {

    return new TileChaosAltar();
  }

  @Override
  public void addRecipes() {

    ItemStack result = new ItemStack(this);
    ItemStack chaosCore = ModItems.craftingMaterial.chaosCore;
    GameRegistry.addRecipe(new ShapedOreRecipe(result, " e ", "dod", "ooo", 'e', chaosCore, 'o',
        Blocks.OBSIDIAN, 'd', "gemDiamond"));
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
      EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

    if (world.isRemote) {
      return true;
    }

    TileEntity tile = world.getTileEntity(pos);

    if (tile instanceof TileChaosAltar) {
      player.openGui(SilentGems.instance, GuiHandlerSilentGems.ID_ALTAR, world, pos.getX(),
          pos.getY(), pos.getZ());
    }

    return true;
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {

    TileChaosAltar tileAltar = (TileChaosAltar) world.getTileEntity(pos);

    if (tileAltar != null) {
      for (int i = 0; i < tileAltar.getSizeInventory(); ++i) {
        ItemStack stack = tileAltar.getStackInSlot(i);

        if (stack != null) {
          float f = SilentGems.instance.random.nextFloat() * 0.8F + 0.1F;
          float f1 = SilentGems.instance.random.nextFloat() * 0.8F + 0.1F;
          float f2 = SilentGems.instance.random.nextFloat() * 0.8F + 0.1F;

          while (stack.stackSize > 0) {
            int j1 = SilentGems.instance.random.nextInt(21) + 10;

            if (j1 > stack.stackSize) {
              j1 = stack.stackSize;
            }

            stack.stackSize -= j1;
            EntityItem entityitem = new EntityItem(world, (double) ((float) pos.getX() + f),
                (double) ((float) pos.getY() + f1), (double) ((float) pos.getZ() + f2),
                new ItemStack(stack.getItem(), j1, stack.getItemDamage()));

            if (stack.hasTagCompound()) {
              entityitem.getEntityItem()
                  .setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
            }

            float f3 = 0.05F;
            entityitem.motionX = (double) (SilentGems.instance.random.nextGaussian() * f3);
            entityitem.motionY = (double) (SilentGems.instance.random.nextGaussian() * f3 + 0.2F);
            entityitem.motionZ = (double) (SilentGems.instance.random.nextGaussian() * f3);
            world.spawnEntityInWorld(entityitem);
          }
        }
      }
    }
  }

  @Override
  public boolean hasComparatorInputOverride(IBlockState state) {

    return true;
  }

  @Override
  public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {

    TileEntity tile = world.getTileEntity(pos);
    if (tile != null && tile instanceof TileChaosAltar) {
      TileChaosAltar altar = (TileChaosAltar) tile;
      float storedRatio = (float) altar.getCharge() / altar.getMaxCharge();
      return (int) (15 * storedRatio);
    }
    return 0;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {

    return false;
  }

  @Override
  public boolean isBlockNormalCube(IBlockState state) {

    return false;
  }

  @Override
  public boolean isFullCube(IBlockState state) {

    return false;
  }

  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {

    return EnumBlockRenderType.MODEL;
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    return BOUNDING_BOX;
  }
}
