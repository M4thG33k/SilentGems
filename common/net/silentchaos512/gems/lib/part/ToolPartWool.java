package net.silentchaos512.gems.lib.part;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.api.lib.EnumPartPosition;
import net.silentchaos512.gems.api.tool.part.ToolPartGrip;
import net.silentchaos512.lib.registry.IRegistryObject;

public class ToolPartWool extends ToolPartGrip {

  Map<String, ModelResourceLocation> modelMap = Maps.newHashMap();

  public final EnumDyeColor color;

  public ToolPartWool(EnumDyeColor color) {

    super(SilentGems.MOD_ID + ":Wool" + color.getMetadata(),
        new ItemStack(Blocks.WOOL, 1, color.getMetadata()));
    this.color = color;
  }

  @Override
  public ModelResourceLocation getModel(ItemStack tool, EnumPartPosition pos) {

    String name = ((IRegistryObject) tool.getItem()).getName();
    name = SilentGems.MOD_ID + ":" + name.toLowerCase() + "/" + name + "Wool" + color.getMetadata();

    if (modelMap.containsKey(name)) {
      return modelMap.get(name);
    }

    ModelResourceLocation model = new ModelResourceLocation(name, "inventory");
    modelMap.put(name, model);
    return model;
  }

  @Override
  public float getHarvestSpeed() {

    return 1.0f;
  }

  @Override
  public float getMeleeSpeed() {

    return 1.05f;
  }
}
