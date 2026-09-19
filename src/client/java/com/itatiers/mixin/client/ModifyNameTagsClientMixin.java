package com.itatiers.mixin.client;

import com.itatiers.ItaTiersClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class ModifyNameTagsClientMixin {
    @Shadow
    public abstract String getScoreboardName();

    @ModifyReturnValue(at = @At("RETURN"), method = "getDisplayName")
    private Component getDisplayName(Component originalNameText) {
        if (ItaTiersClient.toggleMod)
            return ItaTiersClient.getModifiedNametag(this.getScoreboardName(), originalNameText);
        return originalNameText;
    }
}