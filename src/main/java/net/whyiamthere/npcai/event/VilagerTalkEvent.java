package net.whyiamthere.npcai.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

@net.minecraftforge.eventbus.api.Cancelable
public class VilagerTalkEvent extends LivingEvent
{

    public VilagerTalkEvent(LivingEntity entity)
    {
        super(entity);
    }



}
