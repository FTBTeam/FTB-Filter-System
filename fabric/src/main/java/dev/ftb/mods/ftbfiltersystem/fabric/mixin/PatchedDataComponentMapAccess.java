package dev.ftb.mods.ftbfiltersystem.fabric.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(PatchedDataComponentMap.class)
public interface PatchedDataComponentMapAccess {
    @Accessor
    Reference2ObjectMap<DataComponentType<?>, Optional<?>> getPatch();
}
