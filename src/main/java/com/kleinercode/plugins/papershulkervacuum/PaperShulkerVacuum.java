package com.kleinercode.plugins.papershulkervacuum;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Random;

public class PaperShulkerVacuum extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityPickupItem(final EntityPickupItemEvent event) {
        //getLogger().info("EntityPickupItemEvent called");
        if (!(event.getEntity() instanceof final Player player)) {
            //Not a player picking up the item, do nothing
            return;
        }
        ItemStack pickedUpItem = event.getItem().getItemStack();
        //int amtRemaining = pickedUpItem.getAmount();

        if (!(player.getInventory().getItemInOffHand().getItemMeta() instanceof BlockStateMeta offHandMeta)) {
            //Item in offhand has no BlockStateMeta, so it's not a shulker box
            return;
        }

        if (!(offHandMeta.getBlockState() instanceof ShulkerBox shulker)) {
            //Item in offhand isn't a shulker box, do nothing
            return;
        }

        Boolean shulkerModifiedFlag = Boolean.TRUE;

        if(pickedUpItem.getItemMeta() instanceof BlockStateMeta pickedUpMeta) {
            if(pickedUpMeta.getBlockState() instanceof ShulkerBox) {
                //getLogger().info("Skipping box, giving to player");
                shulkerModifiedFlag = Boolean.FALSE;
                final Map<Integer, ItemStack> extraBoxItems = player.getInventory().addItem(pickedUpItem);
                if (!extraBoxItems.isEmpty()) {
                    //getLogger().info("Dropping in world");
                    for (Map.Entry<Integer, ItemStack> entry3 : extraBoxItems.entrySet()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), entry3.getValue());
                    }
                }
            } else {
                final Map<Integer, ItemStack> extraItems = shulker.getInventory().addItem(pickedUpItem);
                //getLogger().info("Adding to Shulker");
                if (!extraItems.isEmpty()) {
                    for (Map.Entry<Integer, ItemStack> entry : extraItems.entrySet()) {
                        final Map<Integer, ItemStack> moreExtraItems = player.getInventory().addItem(entry.getValue());
                        //getLogger().info("Adding to Player Inventory");
                        if (!moreExtraItems.isEmpty()) {
                            //getLogger().info("Dropping in world");
                            for (Map.Entry<Integer, ItemStack> entry1 : moreExtraItems.entrySet()) {
                                player.getWorld().dropItemNaturally(player.getLocation(), entry1.getValue());
                            }
                        }
                    }
                }
            }
        } else {
            final Map<Integer, ItemStack> extraItems = shulker.getInventory().addItem(pickedUpItem);
            //getLogger().info("Adding to Shulker");
            if (!extraItems.isEmpty()) {
                for (Map.Entry<Integer, ItemStack> entry : extraItems.entrySet()) {
                    final Map<Integer, ItemStack> moreExtraItems = player.getInventory().addItem(entry.getValue());
                    //getLogger().info("Adding to Player Inventory");
                    if (!moreExtraItems.isEmpty()) {
                        //getLogger().info("Dropping in world");
                        for (Map.Entry<Integer, ItemStack> entry1 : moreExtraItems.entrySet()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), entry1.getValue());
                        }
                    }
                }
            }
        }

        if (shulkerModifiedFlag) {
            offHandMeta.setBlockState(shulker);
            ItemStack newShulker = player.getInventory().getItemInOffHand();
            newShulker.setItemMeta(offHandMeta);
            player.getInventory().setItemInOffHand(newShulker);
        }

        //Cancel original pickup event
        event.setCancelled(true);
        event.getItem().remove();

        Random random = new Random();
        player.getServer().playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.PLAYER, 0.25f, (random.nextFloat()*0.5f) + 1f), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

    }
}
