package net.silentchaos512.gems.api.tool.part;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.api.lib.EnumMaterialTier;
import net.silentchaos512.gems.api.lib.EnumPartPosition;


public class ToolPartGrip extends ToolPart {

  public ToolPartGrip(String key, ItemStack craftingStack) {

    super(key, craftingStack);
  }

  public ToolPartGrip(String key, ItemStack craftingStack, String oreName) {

    super(key, craftingStack, oreName);
  }

  @Override
  public int getDurability() {

    return 0;
  }

  @Override
  public float getHarvestSpeed() {

    return 0.0f;
  }

  @Override
  public int getHarvestLevel() {

    return 0;
  }

  @Override
  public float getMeleeDamage() {

    return 0.0f;
  }

  @Override
  public float getMagicDamage() {

    return 0.0f;
  }

  @Override
  public int getEnchantability() {

    return 0;
  }

  @Override
  public float getMeleeSpeed() {

    return 1.0f;
  }

  @Override
  public float getChargeSpeed() {

    return 0;
  }

  @Override
  public final float getProtection() {

    return 0;
  }

  @Override
  public boolean validForPosition(EnumPartPosition pos) {

    return pos == EnumPartPosition.ROD_GRIP;
  }

  @Override
  public boolean validForToolOfTier(EnumMaterialTier toolTier) {

    return true;
  }
}
