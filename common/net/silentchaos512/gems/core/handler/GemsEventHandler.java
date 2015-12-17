package net.silentchaos512.gems.core.handler;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.achievement.GemsAchievement;
import net.silentchaos512.gems.block.GlowRose;
import net.silentchaos512.gems.block.ModBlocks;
import net.silentchaos512.gems.core.registry.SRegistry;
import net.silentchaos512.gems.core.util.InventoryHelper;
import net.silentchaos512.gems.core.util.PlayerHelper;
import net.silentchaos512.gems.core.util.ToolHelper;
import net.silentchaos512.gems.enchantment.ModEnchantments;
import net.silentchaos512.gems.item.ChaosGem;
import net.silentchaos512.gems.item.CraftingMaterial;
import net.silentchaos512.gems.item.EnchantToken;
import net.silentchaos512.gems.item.Gem;
import net.silentchaos512.gems.item.ModItems;
import net.silentchaos512.gems.item.TorchBandolier;
import net.silentchaos512.gems.item.tool.GemSword;
import net.silentchaos512.gems.lib.EnumGem;
import net.silentchaos512.gems.lib.HolidayCheer;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.lib.Strings;
import net.silentchaos512.gems.material.ModMaterials;
import net.silentchaos512.gems.network.MessageSetFlight;

public class GemsEventHandler {

  private Random random = new Random();

  @SubscribeEvent
  public void onItemCraftedEvent(ItemCraftedEvent event) {

    if (event.craftMatrix instanceof InventoryCrafting) {
      InventoryCrafting inv = (InventoryCrafting) event.craftMatrix;
      EntityPlayer player = event.player;
      World world = player.worldObj;

      if (event.crafting.getItem() instanceof TorchBandolier) {
        // Torch bandolier achievement
        player.addStat(GemsAchievement.torchBandolier, 1);
      }
      if (event.crafting.getItem() instanceof EnchantToken) {
        // Enchantment token achievement
        player.addStat(GemsAchievement.enchantToken, 1);
      }

      if (TorchBandolier.matchesRecipe(inv, world)) {
        // Decorate a newly crafted Torch Bandolier with the gem used.
        ItemStack gem = inv.getStackInRowAndColumn(1, 1);
        if (gem != null) {
          int k = gem.getItemDamage();
          if (event.crafting.stackTagCompound == null) {
            event.crafting.stackTagCompound = new NBTTagCompound();
          }
          event.crafting.stackTagCompound.setByte(Strings.TORCH_BANDOLIER_GEM, (byte) k);
          event.crafting.stackTagCompound.setBoolean(Strings.TORCH_BANDOLIER_AUTO_FILL, true);
        }
      } else if (ModItems.recipeTorchBandolierExtract.matches(inv, world)) {
        // Give player the extracted torches.
        ItemStack oldBandolier = null;
        ItemStack newBandolier;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
          oldBandolier = inv.getStackInSlot(i);
          if (oldBandolier != null && oldBandolier.getItem() instanceof TorchBandolier) {
            break;
          }
        }

        if (oldBandolier != null) {
          newBandolier = event.crafting;
          int count = newBandolier.getItemDamage() - oldBandolier.getItemDamage();
          PlayerHelper.addItemToInventoryOrDrop(player, new ItemStack(Blocks.torch, count));
        }
      }

      if (InventoryHelper.isGemTool(event.crafting)) {
        // First tool achievement
        player.addStat(GemsAchievement.firstTool, 1);
        // Iron-Tipped achievement
        int tip = ToolHelper.getToolHeadTip(event.crafting);
        if (tip == 1 || tip == 2) {
          player.addStat(GemsAchievement.ironTipped, 1);
        }
        // Diamond-Tipped achievement
        if (tip == 2) {
          player.addStat(GemsAchievement.diamondTipped, 1);
        }
      }
      if (ModItems.recipeDecorateTool.matches(inv, world)) {
        // Redecorate achievement
        player.addStat(GemsAchievement.redecorated, 1);
      }
    }
  }

  @SubscribeEvent
  public void onItemPickup(ItemPickupEvent event) {

    ItemStack stack = event.pickedUp.getEntityItem();
    Item item = stack.getItem();
    if (item instanceof Gem) {
      event.player.addStat(GemsAchievement.acquireGems, 1);
    } else if (Block.getBlockFromItem(item) == ModBlocks.chaosOre) {
      event.player.addStat(GemsAchievement.acquireChaos, 1);
    } else if (CraftingMaterial.doesStackMatch(stack, Names.IRON_POTATO)) {
      event.player.addStat(GemsAchievement.ironPotato, 1);
    }
  }

  @SubscribeEvent
  public void onUseBonemeal(BonemealEvent event) {

    if (event.block == Blocks.grass) {
      if (!event.world.isRemote) {
        // Spawn some Glow Roses?
        int k = random.nextInt(6) - 1;
        int x, y, z, m;
        GlowRose flower = (GlowRose) SRegistry.getBlock(Names.GLOW_ROSE);
        for (int i = 0; i < k; ++i) {
          x = event.x + random.nextInt(9) - 4;
          y = event.y + 1;
          z = event.z + random.nextInt(9) - 4;
          // Get rid of tall grass, it seems to spawn first.
          if (event.world.getBlock(x, y, z) == Blocks.tallgrass) {
            event.world.setBlockToAir(x, y, z);
          }
          if (event.world.isAirBlock(x, y, z) && flower.canBlockStay(event.world, x, y, z)) {
            m = random.nextInt(EnumGem.all().length);
            event.world.setBlock(x, y, z, flower, m, 2);
          }
        }
      }
    }
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    
    if (event.phase != Phase.START) {
      return;
    }

    // Some things I only want to do once per second, not every tick.
    boolean isSecond = event.player.worldObj.getTotalWorldTime() % 20 == 0;

    if (event.side == Side.CLIENT) {
    } else {
      HolidayCheer.tryGiveCandy(event.player);
      // Chaos gem flight removal
      GemsExtendedPlayer properties = GemsExtendedPlayer.get(event.player);
      if (properties != null) {
        if (properties.tickFlightTime()) {
          // This prevents the lingering effects when a chaos gem is removed from the player's inventory to another.
          SilentGems.instance.network.sendTo(new MessageSetFlight(false),
              (EntityPlayerMP) event.player);
          ChaosGem.removeFlight(event.player);
        }
      }

      // Mending (main)
      for (ItemStack stack : event.player.inventory.mainInventory) {
        if (isSecond && stack != null) {
          ModEnchantments.mending.tryActivate(event.player, stack);
        }
      }
      // Mending (armor)
      for (ItemStack stack : event.player.inventory.armorInventory) {
        if (isSecond && stack != null) {
          ModEnchantments.mending.tryActivate(event.player, stack);
        }
      }
    }
  }
}
