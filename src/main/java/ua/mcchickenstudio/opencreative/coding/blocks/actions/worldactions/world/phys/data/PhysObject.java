/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.data;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.millennium.math.*;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec2;
import ua.mcchickenstudio.opencreative.utils.millennium.vectors.Vec3;

import java.util.*;

public class PhysObject {

    private static final double BOX_SIZE = 0.5;
    private final World world;
    private final Particle mainParticle;
    private final Particle subParticle;
    private final Particle hitParticle;

    private final int count;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;
    private final int hitCount;
    private final Location location;
    private final List<PotionEffect> potionEffect;
    public double speed;
    public double weight;
    public final double speedAccel;
    public final double speedLimit;
    public final double weightAccel;
    public final double weightLimit;
    public final double damage;
    public final double explosion;
    public final double shockwaveRadius;
    public final double shockwavePower;
    public int timeExist = 0;
    private boolean living = true;

    // Made by pawsashatoy :)
    public PhysObject(final World world, final List<?> visual, final List<?> motion, final List<?> settings) {
        this.world = world;
        { // visual
            this.mainParticle = (Particle) visual.get(0);
            this.subParticle = (visual.get(1) instanceof Particle) ? (Particle) visual.get(1) : null;
            this.count = ((Number) visual.get(2)).intValue();
            this.offsetX = ((Number) visual.get(3)).intValue();
            this.offsetY = ((Number) visual.get(4)).intValue();
            this.offsetZ = ((Number) visual.get(5)).intValue();
            this.hitParticle = (Particle) visual.get(6);
            this.hitCount = ((Number) visual.get(7)).intValue();
        }
        { // motion
            this.location = (Location) motion.get(0);
            this.speed = ((Number) motion.get(1)).doubleValue();
            this.weight = ((Number) motion.get(2)).doubleValue();
            this.speedAccel = ((Number) motion.get(3)).doubleValue();
            this.speedLimit = ((Number) motion.get(4)).doubleValue();
            this.weightAccel = ((Number) motion.get(5)).doubleValue();
            this.weightLimit = ((Number) motion.get(6)).doubleValue();
        }
        { // settings
            this.damage = ((Number) settings.get(0)).doubleValue();
            this.explosion = ((Number) settings.get(1)).doubleValue();
            this.potionEffect = (settings.get(2) instanceof ItemStack) ? getPotionEffects(((ItemStack) settings.get(2))) : null;
            this.shockwaveRadius = ((Number) settings.get(3)).doubleValue();
            this.shockwavePower = ((Number) settings.get(4)).doubleValue();
        }
    }

    private static List<Entity> getEntitiesAroundPoint(Location location, double radius) {
        List<Entity> entities = new ArrayList<>();
        World world = location.getWorld();
        int smallX = FastMath.floor((location.getX() - radius) / 16.0D);
        int bigX = FastMath.floor((location.getX() + radius) / 16.0D);
        int smallZ = FastMath.floor((location.getZ() - radius) / 16.0D);
        int bigZ = FastMath.floor((location.getZ() + radius) / 16.0D);

        for (int x = smallX; x <= bigX; x++) {
            for (int z = smallZ; z <= bigZ; z++) {
                if (world.isChunkLoaded(x, z)) {
                    entities.addAll(Arrays.asList(world.getChunkAt(x, z).getEntities()));
                }
            }
        }
        entities.removeIf(entity -> entity.getLocation().distanceSquared(location) > radius * radius);
        return entities;
    }

    private static List<PotionEffect> getPotionEffects(ItemStack item) {
        if (item == null || item.getType() != Material.POTION) return List.of();
        if (!(item.getItemMeta() instanceof PotionMeta potionMeta)) return List.of();
        return potionMeta.getCustomEffects();
    }

    public void tick() {
        this.timeExist++;
        if (timeExist > 800) living = false;
        if (!living) return;
        { // logic
            final Location old = location.clone();
            applyMotion();
            if (checkBlockCollision(old) || checkEntityCollision(old)) {
                living = false;
            } else {
                world.spawnParticle(mainParticle, location, count, offsetX, offsetY, offsetZ);
                if (subParticle != null)
                    world.spawnParticle(subParticle, location, count, offsetX, offsetY, offsetZ);
            }
        }
    }

    public World getWorld() {
        return world;
    }

    public boolean isLiving() {
        return living;
    }

    public void applyMotion() {
        { // apply
            final float yaw = location.getYaw(), pitch = location.getPitch();
            double yawRad = Math.toRadians(yaw),
                    pitchRad = Math.toRadians(pitch),
                    x = -Math.cos(pitchRad) * Math.sin(yawRad),
                    y = -Math.sin(pitchRad),
                    z = Math.cos(pitchRad) * Math.cos(yawRad);
            location.add(new Vector(x, y, z).multiply(speed));
        }
        location.add(0, -weight, 0);
        speed += speedAccel;
        if (speed > speedLimit) speed = speedLimit;
        else if (speed < 0) speed = 0;
        weight += weightAccel;
        if (weight > weightLimit) weight = weightLimit;
        else if (weight < 0) weight = 0;
    }

    public boolean checkEntityCollision(final Location from) {
        final List<Entity> entities = getEntitiesAroundPoint(location, speed + 0.5);
        final Vec3 fromVec = new Vec3(from.getX(), from.getY(), from.getZ());
        double closest = 999;
        LivingEntity closestEntity = null;
        Vec3 closestHit = new Vec3(0, 0, 0);
        for (final Entity entity : entities) {
            final Location l = entity.getLocation();
            if (entity instanceof LivingEntity) {
                final AxisAlignedBB bb = new AxisAlignedBB(
                        l.getX() - 0.3, l.getY() - 0.1, l.getZ() - 0.3,
                        l.getX() + 0.3, l.getY() + 1.9, l.getZ() + 0.3
                );
                final MovingObjectPosition result =
                        RayTrace.rayCast(l.getYaw(),
                                location.getPitch(),
                                bb, fromVec,
                                speed + 0.1, BuildSpeed.FAST);
                if (result == null) continue;
                final double dist = fromVec.distanceTo(result.hitVec);
                if (dist < closest) {
                    closest = dist;
                    closestEntity = (LivingEntity) entity;
                    closestHit = result.hitVec;
                }
            }
        }
        if (closestEntity != null) {
            final Location hitLoc = new Location(world, closestHit.xCoord, closestHit.yCoord, closestHit.zCoord);
            final LivingEntity finalClosestEntity = closestEntity;
            shockwave(hitLoc, entities);
            Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                finalClosestEntity.damage(damage);
                if (potionEffect != null) finalClosestEntity.addPotionEffects(potionEffect);
                world.spawnParticle(hitParticle, hitLoc, hitCount);
                if (explosion > 0) world.createExplosion(hitLoc, (float) explosion);
            });
        }
        return false;
    }

    public boolean checkBlockCollision(final Location from) {
        final Set<AxisAlignedBB> boxes = new HashSet<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    final Location l = location.clone().add(x, y, z);
                    final Block block = world.getBlockAt(l);
                    if (!block.getType().name().equals("AIR")) {
                        final Location b = new Location(world,
                                block.getX() + 0.5,
                                block.getY() + 0.5,
                                block.getZ() + 0.5);
                        boxes.add(new AxisAlignedBB(
                                b.getX() - BOX_SIZE, b.getY() - BOX_SIZE, b.getZ() - BOX_SIZE,
                                b.getX() + BOX_SIZE, b.getY() + BOX_SIZE, b.getZ() + BOX_SIZE
                        ));
                    }
                }
            }
        }
        if (boxes.isEmpty()) return false;
        { // conclusion
            final Vec3 fromVec = new Vec3(from.getX(), from.getY(), from.getZ());
            double closest = 999;
            Vec3 closestDist = new Vec3(0, 0, 0);
            for (final AxisAlignedBB bb : boxes) {
                final MovingObjectPosition result =
                        RayTrace.rayCast(location.getYaw(),
                                location.getPitch(),
                                bb, fromVec,
                                speed + 0.3, BuildSpeed.FAST);
                if (result == null) continue;
                final double dist = fromVec.distanceTo(result.hitVec);
                if (dist < closest) {
                    closest = dist;
                    closestDist = result.hitVec;
                }
            }
            if (closest < 100) {
                final Location hitLoc = new Location(world, closestDist.xCoord, closestDist.yCoord, closestDist.zCoord);
                shockwave(hitLoc, null);
                Bukkit.getScheduler().runTask(OpenCreative.getPlugin(), () -> {
                    world.spawnParticle(hitParticle, hitLoc, hitCount);
                    if (explosion > 0) world.createExplosion(hitLoc, (float) explosion);
                });
                return true;
            }
        }
        return false;
    }

    private void shockwave(final Location to, final List<Entity> saved) {
        final List<Entity> entities = (saved != null) ? saved : getEntitiesAroundPoint(to, shockwaveRadius + 0.3);
        if (shockwaveRadius < 1e-7 || shockwavePower < 1e-7) return;
        for (final Entity entity : entities) {
            final Location l = entity.getLocation();
            if (entity instanceof LivingEntity) {
                double calculateRealisticVertical;
                { // ease 1
                    double delta = l.getY() - to.getY();
                    calculateRealisticVertical = (delta >= 1.0) ? 0 :
                            Interpolation.interpolate(0.5, 1,
                                    delta, Interpolation.Type.BACK, Interpolation.Ease.OUT);
                }
                final Vec2 vec = Euler.calculateVec2Vec(
                        new Vec3(to.toVector()),
                        new Vec3(l.clone().add(0, calculateRealisticVertical, 0).toVector()));
                final Vector velo = new Vector(
                        -GeneralMath.sin((float) Math.toRadians(vec.getX()), BuildSpeed.FAST),
                        -GeneralMath.sin((float) Math.toRadians(vec.getY()), BuildSpeed.FAST),
                        GeneralMath.cos((float) Math.toRadians(vec.getX()), BuildSpeed.FAST))
                        .multiply((shockwavePower + 1) / 5);
                double interpolatePitch = 1 - ((Math.abs(vec.getY())) / 90);
                velo.setX(velo.getX() * 3 * interpolatePitch);
                velo.setZ(velo.getZ() * 3 * interpolatePitch);
                { // ease 2
                    double delta = l.distance(to) / shockwaveRadius;
                    double calculateRealisticHorizontal = Interpolation.interpolate(1, 0.25,
                            delta, Interpolation.Type.BACK, Interpolation.Ease.OUT);
                    velo.setX(velo.getX() * calculateRealisticHorizontal);
                    velo.setZ(velo.getZ() * calculateRealisticHorizontal);
                }
                entity.setVelocity(velo);
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PhysObject physObject)) {
            return false;
        }
        return timeExist == physObject.timeExist &&
                living == physObject.living &&
                Objects.equals(world, physObject.world) &&
                Objects.equals(location, physObject.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, location, living, timeExist);
    }

}
