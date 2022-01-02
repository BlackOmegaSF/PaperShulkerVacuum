package com.floogoobooq.blackomega.papershulkervacuum;

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

public class PaperShulkerVacuum extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityPickupItem(final EntityPickupItemEvent event) {
        //getLogger().info("EntityPickupItemEvent called");
        if(event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            ItemStack pickedUpItem = event.getItem().getItemStack();
            //int amtRemaining = pickedUpItem.getAmount();

            //if(player.getInventory().getItemInOffHand() != null) {
                if (player.getInventory().getItemInOffHand().getItemMeta() instanceof BlockStateMeta) {
                    BlockStateMeta offHandMeta = (BlockStateMeta) player.getInventory().getItemInOffHand().getItemMeta();
                    if (offHandMeta.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) offHandMeta.getBlockState();
                        Boolean shulkerModifiedFlag = Boolean.TRUE;

                        if(pickedUpItem.getItemMeta() instanceof BlockStateMeta) {
                            BlockStateMeta pickedUpMeta = (BlockStateMeta) pickedUpItem.getItemMeta();
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

                        event.setCancelled(true);
                        event.getItem().remove();

                    }
                }
            //}
        }

    }
}
