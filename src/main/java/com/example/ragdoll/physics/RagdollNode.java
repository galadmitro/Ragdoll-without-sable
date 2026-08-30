package com.example.ragdoll.physics;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RagdollNode {
    public Vec3 pos;
    public Vec3 oldPos;
    public Vec3 accel;
    public boolean isPinned;

    public RagdollNode(Vec3 initialPos) {
        this.pos = initialPos;
        this.oldPos = initialPos;
        this.accel = new Vec3(0, -0.08, 0); // Vanilla Minecraft gravity
        this.isPinned = false;
    }

    public void update(Level level) {
        if (isPinned) return;

        Vec3 velocity = pos.subtract(oldPos).scale(0.98); // 0.98 air resistance dampening
        oldPos = pos;
        pos = pos.add(velocity).add(accel);

        // Simple terrain collision check against world block hitboxes
        AABB box = new AABB(pos.x - 0.1, pos.y - 0.1, pos.z - 0.1, pos.x + 0.1, pos.y + 0.1, pos.z + 0.1);
        if (!level.noCollision(box)) {
            pos = new Vec3(pos.x, oldPos.y, pos.z); // Resolve vertical collision
        }
    }

    public static void solveDistanceConstraint(RagdollNode a, RagdollNode b, double targetDistance) {
        Vec3 delta = b.pos.subtract(a.pos);
        double currentDistance = delta.length();
        if (currentDistance == 0) return;

        double error = (currentDistance - targetDistance) / currentDistance;
        Vec3 correction = delta.scale(0.5 * error);

        if (!a.isPinned) a.pos = a.pos.add(correction);
        if (!b.isPinned) b.pos = b.pos.subtract(correction);
    }
}