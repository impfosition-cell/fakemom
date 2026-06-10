package com.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import java.util.List;
import java.util.Random;

public class ExampleMod implements ModInitializer {
    private static int timer = 0;
    private static final Random RANDOM = new Random();
    private static MinecraftServer activeServer = null;

    @Override
    public void onInitialize() {
        // Запоминаем сервер при старте мира
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            activeServer = server;
        });

        // Безопасный цикл тиков через стандартный планировщик задач Fabric
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            timer++;
            if (timer >= 24000) { // 20 минут (24000 тиков)
                timer = 0;
                if (server != null) {
                    server.getAllLevels().forEach(level -> {
                        List<ServerPlayer> players = level.players();
                        if (!players.isEmpty()) {
                            ServerPlayer target = players.get(RANDOM.nextInt(players.size()));
                            String targetName = target.getGameProfile().getName();
                            
                            int offsetX = (RANDOM.nextBoolean() ? 1 : -1) * (20 + RANDOM.nextInt(11));
                            int offsetZ = (RANDOM.nextBoolean() ? 1 : -1) * (20 + RANDOM.nextInt(11));
                            BlockPos spawnPos = target.blockPosition().offset(offsetX, 0, offsetZ);

                            if (level.isEmptyBlock(spawnPos)) {
                                Zombie mom = EntityType.ZOMBIE.create(level);
                                if (mom != null) {
                                    mom.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                                    mom.setCustomName(Component.literal(targetName + "_mom"));
                                    mom.setCustomNameVisible(true);
                                    level.addFreshEntity(mom);
                                    
                                    server.getPlayerList().broadcastSystemMessage(
                                        Component.literal("§e" + targetName + "_mom joined the game"), false
                                    );
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
