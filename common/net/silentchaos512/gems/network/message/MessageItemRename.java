package net.silentchaos512.gems.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.network.Message;
import net.silentchaos512.gems.util.ToolHelper;
import net.silentchaos512.lib.util.LogHelper;

/**
 * Used to generate tool/armor names on the client-side. Sends the new display name to the server and removes the
 * temporary part list.
 * 
 * @author Silent
 *
 */
public class MessageItemRename extends Message {

  public String playerName;
  public int slot;
  public String newItemName = "Something went wrong!";

  public MessageItemRename() {

  }

  public MessageItemRename(String playerName, int slot, String newItemName) {

    this.playerName = playerName;
    this.slot = slot;
    this.newItemName = newItemName;
  }

  @Override
  // @SideOnly(Side.SERVER)
  public IMessage handleMessage(MessageContext context) {

    LogHelper log = SilentGems.logHelper;

    if (context.side != Side.SERVER) {
      log.warning("Wrong side!");
      return null;
    }

    EntityPlayer player = context.getServerHandler().playerEntity;
    ItemStack stack = player.inventory.getStackInSlot(slot);

    if (player.getName().equals(playerName)) {

      // TODO: Remove debug info.
      log.info("MessageItemRename.handleMessage");
      log.info("    Player Name = " + player.getName());
      log.info("    Slot = " + slot + ", ItemStack = " + stack);

      if (stack == null) {
        log.warning("MessageItemRename.handleMessage: ItemStack is null!");
        return null;
      }

      log.info("    Old name = " + stack.getDisplayName());
      log.info("    New name = " + newItemName);

      stack.setStackDisplayName(newItemName);

      // Cleanup the temporary part list.
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey(ToolHelper.NBT_TEMP_PARTLIST))
        stack.getTagCompound().removeTag(ToolHelper.NBT_TEMP_PARTLIST);
    }

    return null;
  }
}
