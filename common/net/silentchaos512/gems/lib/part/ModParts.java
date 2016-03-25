package net.silentchaos512.gems.lib.part;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.api.lib.EnumMaterialTier;
import net.silentchaos512.gems.api.tool.part.ToolPartRegistry;
import net.silentchaos512.gems.item.ModItems;
import net.silentchaos512.gems.lib.EnumGem;
import net.silentchaos512.gems.lib.EnumTipUpgrade;
import net.silentchaos512.gems.lib.Names;

public class ModParts {

  public static void init() {

    // Head and deco
    for (EnumGem gem : EnumGem.values()) {
      ToolPartRegistry.putPart(new ToolPartGem(gem, false));
      ToolPartRegistry.putPart(new ToolPartGem(gem, true));
    }
    ToolPartRegistry.putPart(new ToolPartFlint());

    // Rods
    ToolPartRegistry.putPart(
        new ToolPartRodGems("RodWood", EnumMaterialTier.MUNDANE, "stickWood"));
    ToolPartRegistry.putPart(
        new ToolPartRodGems("RodBone", EnumMaterialTier.MUNDANE, new ItemStack(Items.bone)));
    ToolPartRegistry.putPart(new ToolPartRodGems("RodIron", EnumMaterialTier.REGULAR,
        ModItems.craftingMaterial.getStack(Names.STICK_IRON)));
    ToolPartRegistry.putPart(new ToolPartRodGems("RodGold", EnumMaterialTier.SUPER,
        ModItems.craftingMaterial.getStack(Names.ORNATE_STICK_GOLD)));
    ToolPartRegistry.putPart(new ToolPartRodGems("RodSilver", EnumMaterialTier.SUPER,
        ModItems.craftingMaterial.getStack(Names.ORNATE_STICK_SILVER)));

    // Tips
    ToolPartRegistry.putPart(new ToolPartTipGems("TipIron", EnumTipUpgrade.IRON));
    ToolPartRegistry.putPart(new ToolPartTipGems("TipGold", EnumTipUpgrade.GOLD));
    ToolPartRegistry.putPart(new ToolPartTipGems("TipDiamond", EnumTipUpgrade.DIAMOND));
    ToolPartRegistry.putPart(new ToolPartTipGems("TipEmerald", EnumTipUpgrade.EMERALD));

    // Wool
    for (EnumDyeColor color : EnumDyeColor.values()) {
      ToolPartRegistry.putPart(new ToolPartWool(color));
    }
  }
}