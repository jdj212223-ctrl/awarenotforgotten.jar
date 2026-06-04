package com.awarenotforgotten.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;

/**
 * No_One - A stalking entity from the Wonderland dimension.
 * It knows you're there. It always knows.
 */
public class NoOneEntity extends Monster {
    
    private int stalkerTicks = 0;
    private int awarenessLevel = 0;
    private Player targetPlayer = null;

    public NoOneEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 50;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void tick() {
        super.tick();

        if(!this.level().isClientSide) {
            stalkerTicks++;
            
            // Find the nearest player
            Player player = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 128.0D, false);
            
            if(player != null) {
                this.targetPlayer = player;
                this.awarenessLevel = Math.min(100, this.awarenessLevel + 2);
                
                // Stalk the player - always know where they are
                this.lookAt(player, 180F, 180F);
                
                // Every 10 seconds, send a chilling message
                if(stalkerTicks % 200 == 0) {
                    player.displayClientMessage(
                        Component.literal("§c§l[No_One] I'm here... You can't escape me..."),
                        false
                    );
                }
                
                // Chase the player relentlessly if close enough
                if(this.distanceTo(player) < 32.0D) {
                    this.getNavigation().moveTo(player, 1.3D);
                }
            } else {
                this.awarenessLevel = Math.max(0, this.awarenessLevel - 1);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Awareness", this.awarenessLevel);
        tag.putInt("StalkerTicks", this.stalkerTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.awarenessLevel = tag.getInt("Awareness");
        this.stalkerTicks = tag.getInt("StalkerTicks");
    }

    @Override
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose pPose, net.minecraft.world.phys.shapes.VoxelShape pSize) {
        return 1.8F; // Human-like height
    }

    public int getAwarenessLevel() {
        return this.awarenessLevel;
    }
}
