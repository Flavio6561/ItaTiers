package com.itatiers.mixin.client;

import com.itatiers.ItaTiersClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListEntry.class)
public abstract class ModifyTabClientMixin {
    @Shadow
    public abstract GameProfile getProfile();

    @Inject(at = @At(value = "TAIL"), method = "<init>")
    private void onConstruct(GameProfile profile, boolean secureChatEnforced, CallbackInfo ci) {
        if (ItaTiersClient.toggleMod)
            ItaTiersClient.addGetPlayer(profile.name(), false);
    }

    @ModifyReturnValue(at = @At("RETURN"), method = "getDisplayName")
    private Text modifyPlayerName(Text original) {
        if (!ItaTiersClient.toggleMod || original == null)
            return original;

        return ItaTiersClient.getModifiedNametag(getProfile().name(), original);
    }
}