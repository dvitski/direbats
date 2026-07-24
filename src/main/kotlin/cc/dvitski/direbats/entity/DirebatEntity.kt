package cc.dvitski.direbats.entity

import cc.dvitski.direbats.item.DirebatsItems
import cc.dvitski.direbats.sound.DirebatsSoundEvents
import cc.dvitski.direbats.tag.DirebatsGameEventTags
import cc.dvitski.direbats.tag.DirebatsItemTags
import cc.dvitski.direbats.world.DirebatsGameRules
import com.mojang.serialization.Dynamic
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityEvent
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos
import net.minecraft.world.entity.ai.util.HoverRandomPos
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FallingBlock
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.DynamicGameEventListener
import net.minecraft.world.level.gameevent.EntityPositionSource
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.PositionSource
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem
import net.minecraft.world.level.levelgen.Heightmap.Types
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import java.util.EnumSet
import java.util.function.BiConsumer
import java.util.function.Predicate

/**
 * Represents a Direbat entity.
 * @see [DirebatsEntityTypes.DIREBAT]
 */
class DirebatEntity(entityType: EntityType<out PathfinderMob>, world: Level) : PathfinderMob(entityType, world),
    VibrationSystem {
    /**
     * Cooldown for a Direbat picking up items.
     */
    var pickupCooldown: Int = 0

    /**
     * Cooldown for a Direbat returning to hang.
     */
    var hangingCooldown: Int = 0

    /**
     * Whether a Direbat has learnt to avoid falling blocks.
     */
    var avoidsFallingBlocks: Boolean = false

    /**
     * Vibration listener callback.
     */
    val vibrationListenerCallback = VibrationCallback()

    /**
     * Vibration listener.
     */
    val gameEventHandler = DynamicGameEventListener(VibrationSystem.Listener(this))

    /**
     * Whether a Direbat can hang in its current context.
     */
    val canHang: Boolean
        get() {
            // if cooling down and not hanging, can't hang
            if (hangingCooldown > 0 && !hanging) {
                return false
            }

            // if targetting anything or annoyable, can't hang
            if (target != null) {
                return false
            }

            // check position
            return canHangAt(blockPosition(), level(), this, avoidsFallingBlocks)
        }

    /**
     * Whether a Direbat is currently hanging.
     */
    var hanging: Boolean
        get() = entityData.get(HANGING)
        set(value) = entityData.set(HANGING, value)

    /**
     * How long a Direbat has been eating for.
     */
    var eatingTime: Int
        get() = entityData.get(EATING_TIME)
        set(value) = entityData.set(EATING_TIME, value)

    private var vibrationData = VibrationSystem.Data()

    init {
        // movement and pathfinding
        moveControl = FlyingMoveControl(this, 20, true)
        setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0f)
        setPathfindingMalus(PathType.WATER, -1.0f)
        setPathfindingMalus(PathType.WATER_BORDER, 16.0f)
        setPathfindingMalus(PathType.STICKY_HONEY, -1.0f)
        setPathfindingMalus(PathType.COCOA, -1.0f)
        setPathfindingMalus(PathType.FENCE, -1.0f)

        // loot
        setCanPickUpLoot(true)
        xpReward = 5
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(HANGING, false)
        builder.define(EATING_TIME, -1)
    }

    override fun registerGoals() {
        targetSelector.addGoal(0, HurtByTargetGoal(this))
        targetSelector.addGoal(1, DirebatTargetGoal(this, Player::class.java))

        goalSelector.addGoal(1, OcelotAttackGoal(this))
        goalSelector.addGoal(2, DirebatItemPickupGoal(this))
        goalSelector.addGoal(3, DirebatWanderGoal(this))
    }

    override fun createNavigation(world: Level): PathNavigation {
        val navigation = object : FlyingPathNavigation(this, world) {
            override fun isStableDestination(pos: BlockPos): Boolean {
                return !world.getBlockState(pos.below()).isAir
            }
        }

        navigation.setCanFloat(false)
        navigation.setCanOpenDoors(true)

        return navigation
    }

    /**
     * Makes it so that collision pushing only happens when hanging.
     */
    override fun isPushable(): Boolean {
        return super.isPushable() && hanging
    }

    /**
     * Makes it so that push away collision only happens when hanging.
     */
    override fun push(entity: Entity) {
        if (hanging) {
            super.push(entity)
        }
    }

    /**
     * Cancels ground effects.
     */
    override fun checkFallDamage(heightDifference: Double, onGround: Boolean, state: BlockState, landedPosition: BlockPos) {
    }

    /**
     * Makes so that the Direbat cannot despawn if it is holding an item.
     */
    override fun requiresCustomPersistence(): Boolean {
        return super.requiresCustomPersistence() || !mainHandItem.isEmpty
    }

    /**
     * Always drop the held stack.
     */
    override fun dropEquipment() {
        super.dropEquipment()

        val stack = mainHandItem
        if (!stack.isEmpty) {
            spawnAtLocation(stack)
            setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
        }
    }

    /**
     * Handles picking up of items.
     */
    override fun pickUpItem(entity: ItemEntity) {
        val world = level()
        if (world is ServerLevel && !world.gameRules.getBoolean(DirebatsGameRules.DO_DIREBAT_ITEM_PICKUP)) {
            return
        }

        if (target != null) {
            return
        }

        if (mainHandItem.isEmpty && !hanging && PICKABLE_DROP_FILTER.test(entity)) {
            val stack: ItemStack = entity.item
            setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, stack)
            take(entity, stack.count)
            onItemPickup(entity)
            entity.discard()
        }
    }

    /**
     * Handles damage.
     */
    override fun hurt(source: DamageSource, amount: Float): Boolean {
        return if (isInvulnerableTo(source)) {
            false
        } else {
            if (!level().isClientSide && hanging) {
                hanging = false
            }

            dropEquipment()
            super.hurt(source, amount)
        }
    }

    /**
     * Handles Direbat attacking.
     */
    override fun doHurtTarget(target: Entity): Boolean {
        return if (super.doHurtTarget(target)) {
            if (target is LivingEntity) {
                val duration = (if (level().difficulty == Difficulty.HARD) 10 else 5) * 20
                val statusEffect = MobEffectInstance(MobEffects.BLINDNESS, duration, 0)
                target.addEffect(statusEffect)
            }

            playSound(attackSound, 1.0f, 1.0f)
            true
        } else false
    }

    override fun travel(movementInput: Vec3) {
        if (isEffectiveAi || isControlledByLocalInstance) {
            moveRelative(0.1f, movementInput)
            move(MoverType.SELF, deltaMovement)
            setDeltaMovement(deltaMovement.scale(0.9))
        }
    }

    override fun getWalkTargetValue(pos: BlockPos, world: LevelReader): Float {
        if (world.getBlockState(pos).isAir) {
            var offset = 0.0f

            if (!world.getBlockState(pos.below()).isAir) {
                offset -= 2.0f
            }

            if (world is Level && canHangAt(pos.above(), world, this, avoidsFallingBlocks)) {
                offset += 2.0f
            }

            @Suppress("DEPRECATION")
            return 1.0f / (world.getLightLevelDependentMagicValue(pos)) + offset
        }

        return 0.0f
    }

    @Environment(EnvType.CLIENT)
    override fun handleEntityEvent(status: Byte) {
        when (status) {
            EntityEvent.FOX_EAT -> {
                val stack = getItemBySlot(EquipmentSlot.MAINHAND)
                if (!stack.isEmpty) {
                    for (i in 1..8) {
                        val velocity = Vec3((random.nextFloat().toDouble() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                            .xRot(-xRot * (Math.PI.toFloat() / 180))
                            .yRot(-yRot * (Math.PI.toFloat() / 180))
                        level().addParticle(
                            ItemParticleOption(ParticleTypes.ITEM, stack),
                            this.x + this.lookAngle.x / 2.0, this.y, this.z + this.lookAngle.z / 2.0,
                            velocity.x, velocity.y + 0.05, velocity.z
                        )
                    }
                }
            }

            else -> super.handleEntityEvent(status)
        }
    }

    /* Sounds */

    override fun getSoundVolume(): Float {
        return 0.4F
    }

    override fun getAmbientSound(): SoundEvent? {
        return if (hanging && random.nextInt(4) != 0) null else DirebatsSoundEvents.ENTITY_DIREBAT_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return DirebatsSoundEvents.ENTITY_DIREBAT_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return DirebatsSoundEvents.ENTITY_DIREBAT_DEATH
    }

    val attackSound: SoundEvent = DirebatsSoundEvents.ENTITY_DIREBAT_ATTACK

    /* Ticks */

    override fun tick() {
        val world = level()
        if (world is ServerLevel) {
            VibrationSystem.Ticker.tick(world, vibrationData, vibrationListenerCallback)
        }

        super.tick()

        // prevent getting stuck below the world
        if (y <= world.minBuildHeight) {
            val velocityCache = deltaMovement
            setDeltaMovement(velocityCache.x, 0.2, velocityCache.z)
        } else {
            setDeltaMovement(if (hanging) Vec3.ZERO else deltaMovement.scale(1.05))
        }
    }

    @Suppress("DEPRECATION")
    override fun customServerAiStep() {
        val world = level()
        val targetCache = target
        if (targetCache is Player && !isValidTarget(targetCache)) {
            target = null
        }

        super.customServerAiStep()

        // tick pickup cooldown
        if (pickupCooldown > 0) {
            pickupCooldown--
        }

        if (!world.isClientSide) {
            if (hanging) {
                // if cannot hang, stop hanging
                if (!canHang) {
                    hanging = false

                    if (!isSilent) {
                        world.levelEvent(null, LevelEvent.SOUND_BAT_LIFTOFF, blockPosition(), 0)
                    }
                }

                // eat held item
                val stack = getItemBySlot(EquipmentSlot.MAINHAND)
                if (!stack.isEmpty && (stack.components.has(DataComponents.FOOD) || stack.`is`(ItemTags.ARROWS))) {
                    if (eatingTime > MAX_EATING_TIME) { // finish eating
                        val finishedStack = stack.finishUsingItem(world, this)
                        if (stack.`is`(ItemTags.ARROWS)) { // if arrow, convert to direbat fang arrow
                            val arrowStack = stack.transmuteCopy(DirebatsItems.DIREBAT_FANG_ARROW)
                            setItemSlot(EquipmentSlot.MAINHAND, arrowStack)
                        } else if (!finishedStack.isEmpty) { // else, equip finished stack
                            setItemSlot(EquipmentSlot.MAINHAND, finishedStack)
                        }
                        eatingTime = -1
                    } else { // during consumption
                        if (eatingTime > EATING_EFFECTS_TIME) {
                            // randomly play eating effects
                            if (random.nextFloat() < 0.1F) {
                                playSound(SoundEvents.GENERIC_EAT, soundVolume, voicePitch)
                                world.broadcastEntityEvent(this, EntityEvent.FOX_EAT)
                            }
                        }

                        // tick eating time
                        eatingTime++
                    }
                }
            } else {
                // tick hanging cooldown
                if (hangingCooldown > 0) {
                    hangingCooldown--
                }

                if (random.nextFloat() <= 0.5f && canHang) {
                    // set hanging
                    hanging = true
                    hangingCooldown = 20 * 10

                    // stop navigation
                    navigation.stop()

                    // update above block
                    updateCeiling()
                }
            }
        }
    }

    /**
     * Updates the block above the Direbat's head.
     *
     * If there is a falling block above a Direbat's head, the
     * block will fall and it will learn about the dangers.
     */
    fun updateCeiling() {
        val ceilingPos = blockPosition().above()
        val ceilingState = level().getBlockState(ceilingPos)
        if (ceilingState.block is FallingBlock) {
            avoidsFallingBlocks = true
        }
        ceilingState.updateNeighbourShapes(level(), ceilingPos, Block.UPDATE_ALL)
    }

    // TODO BURN_IN_DAYLIGHT

    /**
     * Accepts vibration events.
     */
    override fun updateDynamicGameEventListener(callback: BiConsumer<DynamicGameEventListener<*>, ServerLevel>) {
        val world = level()
        if (world is ServerLevel) {
            callback.accept(gameEventHandler, world)
        }
    }

    /**
     * @return the first available item to pick up
     */
    fun findItemEntityToPickUp(): ItemEntity? {
        val list = level().getEntitiesOfClass(ItemEntity::class.java, boundingBox.inflate(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER)
        return if (list.isNotEmpty()) list[random.nextInt(list.size)] else null
    }

    /**
     * @return whether a player should wake up a Direbat
     */
    fun isValidTarget(player: Player): Boolean {
        if (level() !== player.level()) return false
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) return false
        if (isAlliedTo(player)) return false
        if (player.isInvulnerable) return false
        if (player.isDeadOrDying) return false
        return level().worldBorder.isWithinBounds(player.boundingBox)
    }

    /* NBT */

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        super.addAdditionalSaveData(nbt)

        VibrationSystem.Data.CODEC.encodeStart(NbtOps.INSTANCE, vibrationData)
            .result()
            .ifPresent { nbt.put(LISTENER_KEY, it) }

        nbt.putBoolean(HANGING_KEY, hanging)
        nbt.putBoolean(AVOIDS_FALLING_BLOCKS_KEY, avoidsFallingBlocks)
        nbt.putInt(EATING_TIME_KEY, eatingTime)
        nbt.putInt(HANGING_COOLDOWN_KEY, hangingCooldown)
        nbt.putInt(PICKUP_COOLDOWN_KEY, pickupCooldown)
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        super.readAdditionalSaveData(nbt)

        if (nbt.contains(LISTENER_KEY, 10)) {
            VibrationSystem.Data.CODEC.parse(Dynamic(NbtOps.INSTANCE, nbt.getCompound(LISTENER_KEY)))
                .result()
                .ifPresent { vibrationData = it }
        }

        hanging = nbt.getBoolean(HANGING_KEY)
        avoidsFallingBlocks = nbt.getBoolean(AVOIDS_FALLING_BLOCKS_KEY)
        eatingTime = if (nbt.contains(EATING_TIME_KEY)) nbt.getInt(EATING_TIME_KEY) else -1
        hangingCooldown = nbt.getInt(HANGING_COOLDOWN_KEY)
        pickupCooldown = nbt.getInt(PICKUP_COOLDOWN_KEY)
    }

    override fun getVibrationData(): VibrationSystem.Data {
        return vibrationData
    }

    override fun getVibrationUser(): VibrationSystem.User {
        return vibrationListenerCallback
    }

    companion object {
        val PICKABLE_DROP_FILTER = Predicate<ItemEntity> { entity -> entity != null &&
                !entity.hasPickUpDelay() && entity.item.`is`(DirebatsItemTags.PICKED_UP_BY_DIREBAT)
        }

        val HANGING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(DirebatEntity::class.java, EntityDataSerializers.BOOLEAN)
        val EATING_TIME: EntityDataAccessor<Int> = SynchedEntityData.defineId(DirebatEntity::class.java, EntityDataSerializers.INT)

        const val HANGING_KEY = "Hanging"
        const val AVOIDS_FALLING_BLOCKS_KEY = "AvoidsFallingBlocks"
        const val EATING_TIME_KEY = "EatingTime"
        const val HANGING_COOLDOWN_KEY = "HangingCooldown"
        const val PICKUP_COOLDOWN_KEY = "PickupCooldown"
        const val LISTENER_KEY = "listener"

        /**
         * How long it takes for a Direbat to eat an item.
         */
        const val MAX_EATING_TIME = 30 * 20

        /**
         * At what time during [MAX_EATING_TIME] that the
         * Direbat starts making eating effects.
         */
        const val EATING_EFFECTS_TIME = MAX_EATING_TIME - (2 * 20)

        /**
         * @return whether a Direbat can spawn under the given conditions
         */
        fun canSpawn(
            type: EntityType<DirebatEntity>,
            world: ServerLevelAccessor,
            spawnReason: MobSpawnType,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            if (world !is ServerLevel) return false

            if (pos.y >= world.getHeightmapPos(Types.WORLD_SURFACE, pos).y) {
                return false
            }

            val chance = if (world.getMoonBrightness() == 1.0f) 2 else 4
            if (world.getMaxLocalRawBrightness(pos) > random.nextInt(chance)) {
                return false
            }

            if (!checkMobSpawnRules(type, world, spawnReason, pos, random)) {
                return false
            }

            return true
        }

        /**
         * @return the default Direbat attribute container
         */
        fun createDirebatAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.FLYING_SPEED, 0.22)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
        }

        /**
         * @return whether a Direbat can hang at a given position
         */
        fun canHangAt(pos: BlockPos, world: Level, entity: Entity, avoidsFallingBlocks: Boolean): Boolean {
            // if learnt about falling blocks, avoid falling blocks
            val ceilingPos = pos.above()
            val ceilingState = world.getBlockState(ceilingPos)
            if (avoidsFallingBlocks && ceilingState.block is FallingBlock) {
                return false
            }

            // check is cube
            if (!ceilingState.isCollisionShapeFullBlock(world, ceilingPos)) {
                return false
            }

            // check hanging position for already present direbats
            val mutable = pos.mutable()
            for (i in 1..2) {
                val box = AABB.ofSize(Vec3.atLowerCornerOf(mutable), 1.0, 1.0, 1.0)
                if (world.getEntitiesOfClass(DirebatEntity::class.java, box) { direbatEntity -> direbatEntity !== entity && direbatEntity.hanging }.isNotEmpty()) {
                    return false
                }

                if (!world.getBlockState(mutable).getCollisionShape(world, mutable, CollisionContext.of(entity)).isEmpty) {
                    return false
                }

                mutable.move(Direction.DOWN)
            }

            return true
        }
    }

    /**
     * A customised [ActiveTargetGoal], for Direbats, based on light level.
     */
    class DirebatTargetGoal<T : LivingEntity>(mob: DirebatEntity, classTarget: Class<T>) : NearestAttackableTargetGoal<T>(mob, classTarget, true) {
        override fun canUse(): Boolean {
            @Suppress("DEPRECATION")
            return mob.lightLevelDependentMagicValue >= 0.5 && !super.canUse()
        }
    }

    /**
     * Goal for a Direbat wandering.
     */
    class DirebatWanderGoal(val mob: DirebatEntity) : Goal() {
        init {
            setFlags(EnumSet.of(Flag.MOVE))
        }

        override fun canUse(): Boolean {
            return mob.navigation.isDone && !mob.hanging && mob.random.nextInt(5) == 0
        }

        override fun canContinueToUse(): Boolean {
            return mob.navigation.isInProgress
        }

        override fun start() {
            calculateRandomLocation()?.run {
                val navigation = mob.navigation
                navigation.moveTo(navigation.createPath(x, y, z, 1), 1.0)
            }
        }

        fun calculateRandomLocation(): Vec3? {
            val rotation = if (mob.hasRestriction() && !mob.restrictCenter.closerToCenterThan(mob.position(), 22.0)) {
                Vec3.atCenterOf(mob.restrictCenter).subtract(mob.position()).normalize()
            } else {
                mob.getViewVector(0.0f)
            }

            return HoverRandomPos.getPos(
                mob, 8, 7, rotation.x, rotation.z,
                Math.PI.toFloat() / 2f, 2, 1
            ) ?: AirAndWaterRandomPos.getPos(
                mob,
                8,
                4,
                -2,
                rotation.x,
                rotation.z,
                (Math.PI.toFloat() / 2f).toDouble()
            )
        }
    }

    /**
     * Goal for a Direbat picking up items.
     */
    class DirebatItemPickupGoal(val direbat: DirebatEntity) : Goal() {
        private var itemEntity: ItemEntity? = null

        init {
            setFlags(EnumSet.of(Flag.MOVE))
        }

        override fun canUse(): Boolean {
            // can't start if cooling down
            if (direbat.pickupCooldown > 0) {
                return false
            }

            // can't start if already has item
            if (!direbat.mainHandItem.isEmpty) {
                return false
            }

            // can't start if attacking
            if (direbat.target != null) {
                return false
            }

            // can't start if configured not to
            val world = direbat.level()
            if (world is ServerLevel) {
                if (!world.gameRules.getBoolean(DirebatsGameRules.DO_DIREBAT_ITEM_PICKUP)) {
                    return false
                }
            }

            // find item and start
            direbat.findItemEntityToPickUp()?.run {
                itemEntity = this
                return true
            }

            return false
        }

        override fun start() {
            // start moving to
            itemEntity?.run {
                direbat.navigation.moveTo(this, 1.2)
            }

            // create cooldown
            direbat.pickupCooldown = 20 * 3
        }

        override fun tick() {
            // continue moving to
            itemEntity?.run {
                direbat.navigation.moveTo(this, 1.2)
            }
        }

        override fun canContinueToUse(): Boolean {
            return direbat.navigation.isInProgress && direbat.mainHandItem.isEmpty
        }
    }

    inner class VibrationCallback : VibrationSystem.User {
        val positionSource = EntityPositionSource(this@DirebatEntity, eyeHeight)

        override fun getListenerRadius(): Int {
            return 8
        }

        override fun getPositionSource(): PositionSource {
            return positionSource
        }

        override fun canReceiveVibration(world: ServerLevel, pos: BlockPos, event: Holder<GameEvent>, emitter: GameEvent.Context): Boolean {
            if (isNoAi || dead || !world.worldBorder.isWithinBounds(pos)) {
                return false
            }

            if (target != null || !hanging) {
                return false
            }

            val entity = emitter.sourceEntity
            return entity is Player && isValidTarget(entity)
        }

        override fun onReceiveVibration(world: ServerLevel, pos: BlockPos, event: Holder<GameEvent>, sourceEntity: Entity?, entity: Entity?, distance: Float) {
            if (dead) {
                return
            }

            if (sourceEntity is Player) {
                target = sourceEntity

                val pitch = voicePitch * 1.4f
                ambientSound?.let { playSound(it, 1.0f, pitch) }
                playSound(attackSound, 1.0f, pitch)
            }
        }

        override fun getListenableEvents(): TagKey<GameEvent> {
            return DirebatsGameEventTags.DIREBAT_CAN_LISTEN
        }
    }
}
