package net.silentchaos512.gems.item;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.config.Config;
import net.silentchaos512.gems.lib.EnumGem;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.util.NBTHelper;
import net.silentchaos512.gems.util.TeleportUtil;
import net.silentchaos512.lib.util.DimensionalPosition;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.PlayerHelper;

public class ItemReturnHome extends ItemChaosStorage {

  public static final String TEXT_BOUND_TO = "BoundTo";
  public static final String TEXT_NOT_BOUND = "NotBound";
  public static final String TEXT_NOT_ENOUGH_CHARGE = "NotEnoughCharge";
  public static final String TEXT_NOT_SANE = "NotSane";
  public static final String TEXT_NOT_SAFE = "NotSafe";

  public static final String NBT_READY = "IsReady";

  public ItemReturnHome() {

    super(EnumGem.values().length, Names.RETURN_HOME_CHARM, Config.RETURN_HOME_MAX_CHARGE);
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {

    // Is ctrl key down?
    boolean modifier = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
        || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);

    LocalizationHelper loc = SilentGems.instance.localizationHelper;

    // How to use
    list.addAll(loc.getItemDescriptionLines(itemName));

    // Display coordinates if modifier key is held.
    DimensionalPosition pos = getBoundPosition(stack);
    if (pos != null) {
      if (modifier) {
        list.add(loc.getItemSubText(itemName, TEXT_BOUND_TO, pos));
      } else {
        list.add(loc.getMiscText("PressCtrl"));
      }
    } else {
      list.add(loc.getItemSubText(itemName, TEXT_NOT_BOUND));
    }
  }

  @Override
  public void addRecipes() {

    for (EnumGem gem : EnumGem.values()) {
      ItemStack result = new ItemStack(this, 1, gem.ordinal());
      GameRegistry.addRecipe(new ShapedOreRecipe(result, " s ", "sgs", "ici", 's',
          ModItems.craftingMaterial.gildedString, 'g', gem.getItemOreName(), 'i', "ingotGold", 'c',
          new ItemStack(ModItems.chaosOrb, 1, ItemChaosOrb.Type.FRAGILE.ordinal())));
    }
  }

  @Override
  public void getSubItems(Item item, CreativeTabs tab, List list) {

    for (EnumGem gem : EnumGem.values()) {
      ItemStack stack = new ItemStack(item, 1, gem.ordinal());
      setCharge(stack, getMaxCharge(stack));
      list.add(stack);
    }
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {

    return EnumAction.BOW;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {

    return 133700;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {

    return NBTHelper.getTagBoolean(stack, NBT_READY);
  }

  @Override
  public String getNameForStack(ItemStack stack) {

    return itemName;
  }

  public DimensionalPosition getBoundPosition(ItemStack stack) {

    if (stack == null || !stack.hasTagCompound()) {
      return null;
    }

    DimensionalPosition pos = DimensionalPosition.readFromNBT(stack.getTagCompound());
    if (pos.equals(DimensionalPosition.ZERO)) {
      return null;
    }
    return pos;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player,
      EnumHand hand) {

    DimensionalPosition pos = getBoundPosition(stack);
    if (pos != null) {
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, stack);
    } else {
//      PlayerHelper.addChatMessage(player,
//          SilentGems.instance.localizationHelper.getItemSubText(itemName, TEXT_NOT_BOUND));
      return new ActionResult(EnumActionResult.PASS, stack);
    }
  }

  @Override
  public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {

    if (player.worldObj.isRemote) {
      int timeUsed = getMaxItemUseDuration(stack) - count;
      if (timeUsed >= Config.RETURN_HOME_USE_TIME) {
        NBTHelper.setTagBoolean(stack, NBT_READY, true);
      }
    }
  }

  @Override
  public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot,
      boolean isSelected) {

    if (!isSelected && NBTHelper.getTagBoolean(stack, NBT_READY)) {
      NBTHelper.setTagBoolean(stack, NBT_READY, false);
    }
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity,
      int timeLeft) {

    if (!(entity instanceof EntityPlayer)) {
      return;
    }
    EntityPlayer player = (EntityPlayer) entity;

    if (world.isRemote) {
      NBTHelper.setTagBoolean(stack, NBT_READY, false);
    } else {
      int timeUsed = getMaxItemUseDuration(stack) - timeLeft;

      // Did player use item long enough?
      if (timeUsed < Config.RETURN_HOME_USE_TIME) {
        return;
      }

      LocalizationHelper loc = SilentGems.instance.localizationHelper;
      DimensionalPosition pos = getBoundPosition(stack);

      // Enough charge?
      if (getCharge(stack) < getTeleportCost(stack, player)) {
        PlayerHelper.addChatMessage(player, loc.getItemSubText(itemName, TEXT_NOT_ENOUGH_CHARGE));
        return;
      }

      // Is the destination sane? (ie, y > 0)
      if (pos.y <= 0) {
        PlayerHelper.addChatMessage(player, loc.getItemSubText(itemName, TEXT_NOT_SANE));
        return;
      }

      // Is the destination safe? (ie, no solid block at head level)
      WorldServer worldServer = player.getServer().worldServerForDimension(pos.dim);
      int height = (int) Math.ceil(player.eyeHeight);
      BlockPos target = pos.toBlockPos().up(height);

      //SilentGems.instance.logHelper.debug(pos, worldServer.getBlockState(target));
      if (worldServer.isBlockNormalCube(target, true)) {
        PlayerHelper.addChatMessage(player, loc.getItemSubText(itemName, TEXT_NOT_SAFE));
        return;
      }

      // It should be safe to teleport.
      // Reset fall distance then teleport.
      player.fallDistance = 0.0f;
      teleportPlayer(stack, player, pos);
      // Play sounds
      float soundPitch = 0.8f + 0.3f * SilentGems.instance.random.nextFloat();
      for (BlockPos p : new BlockPos[] { player.getPosition(), pos.toBlockPos() }) {
        world.playSound(null, p, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0f,
            soundPitch);
      }
    }
  }

  public int getTeleportCost(ItemStack stack, EntityPlayer player) {

    // Currently a flat cost, but could be changed to consider distance.
    return player.capabilities.isCreativeMode ? 0 : Config.RETURN_HOME_USE_COST;
  }

  protected void teleportPlayer(ItemStack stack, EntityPlayer player, DimensionalPosition pos) {

    if (player instanceof EntityPlayerMP) {
      TeleportUtil.teleportPlayerTo((EntityPlayerMP) player, pos);
    }

    extractCharge(stack, getTeleportCost(stack, player), false);
  }
}
