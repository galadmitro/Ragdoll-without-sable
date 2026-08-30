package com.example.ragdoll.client;

import com.example.ragdoll.CustomRagdollMod;
import com.example.ragdoll.physics.RagdollNode;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = CustomRagdollMod.MODID, value = Dist.CLIENT)
public class RagdollClientHandler {

    private static final Map<UUID, RagdollStructure> ACTIVE_RAGDOLLS = new HashMap<>();

    public record RagdollStructure(RagdollNode head, RagdollNode torso, RagdollNode feet) {}

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        LivingEntity entity = event.getEntity();

        // Trigger ragdoll state when entity dies
        if (entity.isDeadOrDying()) {
            UUID id = entity.getUUID();
            RagdollStructure ragdoll = ACTIVE_RAGDOLLS.computeIfAbsent(id, k -> {
                Vec3 p = entity.getPosition(event.getPartialTick());
                RagdollNode torso = new RagdollNode(p.add(0, 1.0, 0));
                RagdollNode head = new RagdollNode(p.add(0, 1.8, 0));
                RagdollNode feet = new RagdollNode(p.add(0, 0.2, 0));
                return new RagdollStructure(head, torso, feet);
            });

            // Physics step
            ragdoll.torso().update(entity.level());
            ragdoll.head().update(entity.level());
            ragdoll.feet().update(entity.level());

            RagdollNode.solveDistanceConstraint(ragdoll.torso(), ragdoll.head(), 0.8);
            RagdollNode.solveDistanceConstraint(ragdoll.torso(), ragdoll.feet(), 0.8);

            // Apply transformations to the rendering PoseStack
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            Vec3 torsoPos = ragdoll.torso().pos.subtract(entity.getPosition(event.getPartialTick()));
            poseStack.translate(torsoPos.x, torsoPos.y, torsoPos.z);

            // Calculate torso tilt angle based on head position relative to torso
            Vec3 dir = ragdoll.head().pos.subtract(ragdoll.torso().pos).normalize();
            float pitch = (float) Math.toDegrees(Math.asin(-dir.y));
            float yaw = (float) Math.toDegrees(Math.atan2(dir.x, dir.z));

            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));

            // Custom pose applied to player/humanoid models
            if (event.getRenderer().getModel() instanceof PlayerModel<?> playerModel) {
                playerModel.rightArm.xRot = (float) Math.toRadians(45);
                playerModel.leftArm.xRot = (float) Math.toRadians(-30);
                playerModel.rightLeg.xRot = (float) Math.toRadians(-15);
                playerModel.leftLeg.xRot = (float) Math.toRadians(20);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.getEntity().isDeadOrDying()) {
            event.getPoseStack().popPose();
        }
    }
}