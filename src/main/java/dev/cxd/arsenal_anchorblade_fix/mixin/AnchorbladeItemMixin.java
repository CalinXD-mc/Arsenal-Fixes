package dev.cxd.arsenal_anchorblade_fix.mixin;

import dev.doctor4t.arsenal.entity.AnchorbladeEntity;
import dev.doctor4t.arsenal.index.ArsenalEnchantments;
import dev.doctor4t.arsenal.item.AnchorbladeItem;
import dev.doctor4t.arsenal.util.AnchorOwner;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnchorbladeItem.class)
public class AnchorbladeItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void arsenal$fixMidAirRecall(World world, PlayerEntity user, Hand hand,
                                         CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (world.isClient) return;
        if (!(user instanceof AnchorOwner owner)) return;

        ItemStack stack = user.getStackInHand(hand);
        boolean reeling = EnchantmentHelper.getLevel(ArsenalEnchantments.REELING, stack) > 0;

        AnchorbladeEntity anchor = owner.arsenal$getAnchor(hand, reeling);
        if (anchor == null) anchor = owner.arsenal$getAnchor(hand, !reeling);

        if (anchor != null && anchor.isAlive()) {
            anchor.setRecalled(true);
            cir.setReturnValue(TypedActionResult.fail(stack));
        }
    }
}