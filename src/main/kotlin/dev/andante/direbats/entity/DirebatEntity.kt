package dev.andante.direbats.entity

import dev.andante.direbats.entity.DirebatEntity.Companion.MAX_EATING_TIME
import dev.andante.direbats.item.DirebatsItems
import dev.andante.direbats.sound.DirebatsSoundEvents
import dev.andante.direbats.tag.DirebatsGameEventTags
import dev.andante.direbats.tag.DirebatsItemTags
import dev.andante.direbats.world.DirebatsGameRules
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FallingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.AboveGroundTargeting
import net.minecraft.entity.ai.NoPenaltySolidTargeting
import net.minecraft.entity.ai.control.FlightMoveControl
import net.minecraft.entity.ai.goal.ActiveTargetGoal
import net.minecraft.entity.ai.goal.AttackGoal
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.ai.goal.RevengeGoal
import net.minecraft.entity.ai.pathing.BirdNavigation
import net.minecraft.entity.ai.pathing.EntityNavigation
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.passive.BatEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.ItemTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.Difficulty
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import net.minecraft.world.WorldView
import net.minecraft.world.event.EntityPositionSource
import net.minecraft.world.event.GameEvent
import net.minecraft.world.event.PositionSource
import net.minecraft.world.event.Vibrations
import net.minecraft.world.event.listener.EntityGameEventHandler
import java.util.EnumSet
import java.util.function.BiConsumer
import java.util.function.Predicate

/**
 * Represents a Direbat entity.
 * @see [DirebatsEntityTypes.DIREBAT]
 */
class DirebatEntity(entityType: EntityType<out PathAwareEntity>, world: World) : PathAwareEntity(entityType, world), Vibrations {
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
    val gameEventHandler = EntityGameEventHandler(Vibrations.VibrationListener(this))

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
            return canHangAt(blockPos, world, this, avoidsFallingBlocks)
        }

    /**
     * Whether a Direbat is currently hanging.
     */
    var hanging: Boolean
        get() = dataTracker.get(HANGING)
        set(value) = dataTracker.set(HANGING, value)

    /**
     * How long a Direbat has been eating for.
     */
    var eatingTime: Int
        get() = dataTracker.get(EATING_TIME)
        set(value) = dataTracker.set(EATING_TIME, value)

    private var vibrationData = Vibrations.ListenerData()

    init {
        // movement and pathfinding
        moveControl = FlightMoveControl(this, 20, true)
        setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0f)
        setPathfindingPenalty(PathNodeType.WATER, -1.0f)
        setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0f)
        setPathfindingPenalty(PathNodeType.STICKY_HONEY, -1.0f)
        setPathfindingPenalty(PathNodeType.COCOA, -1.0f)
        setPathfindingPenalty(PathNodeType.FENCE, -1.0f)

        // loot
        setCanPickUpLoot(true)
        experiencePoints = 5
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
        builder.add(HANGING, false)
        builder.add(EATING_TIME, -1)
    }

    override fun initGoals() {
        targetSelector.add(0, RevengeGoal(this))
        targetSelector.add(1, DirebatTargetGoal(this, PlayerEntity::class.java))

        goalSelector.add(1, AttackGoal(this))
        goalSelector.add(2, DirebatItemPickupGoal(this))
        goalSelector.add(3, DirebatWanderGoal(this))
    }

    override fun createNavigation(world: World): EntityNavigation {
        val navigation = object : BirdNavigation(this, world) {
            override fun isValidPosition(pos: BlockPos): Boolean {
                return !world.getBlockState(pos.down()).isAir
            }
        }

        navigation.setCanSwim(false)
        navigation.setCanOpenDoors(true)

        return navigation
    }

    override fun isDisallowedInPeaceful(): Boolean {
        return true
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
    override fun pushAwayFrom(entity: Entity) {
        if (hanging) {
            super.pushAwayFrom(entity)
        }
    }

    /**
     * Cancels ground effects.
     */
    override fun fall(heightDifference: Double, onGround: Boolean, state: BlockState, landedPosition: BlockPos) {
    }

    /**
     * Makes so that the Direbat cannot despawn if it is holding an item.
     */
    override fun cannotDespawn(): Boolean {
        return super.cannotDespawn() || !mainHandStack.isEmpty
    }

    /**
     * Always drop the held stack.
     */
    override fun dropInventory(world: ServerWorld) {
        super.dropInventory(world)

        val stack = mainHandStack
        if (!stack.isEmpty) {
            dropStack(world, stack)
            setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY)
        }
    }

    /**
     * Handles picking up of items.
     */
    override fun loot(world: ServerWorld, entity: ItemEntity) {
        if (!world.gameRules.getBoolean(DirebatsGameRules.DO_DIREBAT_ITEM_PICKUP)) {
            return
        }

        if (target != null) {
            return
        }

        if (mainHandStack.isEmpty && !hanging && PICKABLE_DROP_FILTER.test(entity)) {
            val stack: ItemStack = entity.stack
            equipLootStack(EquipmentSlot.MAINHAND, stack)
            sendPickup(entity, stack.count)
            triggerItemPickedUpByEntityCriteria(entity)
            entity.discard()
        }
    }

    /**
     * Handles damage.
     */
    override fun damage(world: ServerWorld, source: DamageSource, amount: Float): Boolean {
        return if (isInvulnerableTo(world, source)) {
            false
        } else {
            if (!world.isClient && hanging) {
                hanging = false
            }

            dropInventory(world)
            super.damage(world, source, amount)
        }
    }

    /**
     * Handles Direbat attacking.
     */
    override fun tryAttack(world: ServerWorld, target: Entity): Boolean {
        return if (super.tryAttack(world, target)) {
            if (target is LivingEntity) {
                val duration = (if (world.difficulty == Difficulty.HARD) 10 else 5) * 20
                val statusEffect = StatusEffectInstance(StatusEffects.BLINDNESS, duration, 0)
                target.addStatusEffect(statusEffect)
            }

            playSound(attackSound, 1.0f, 1.0f)
            true
        } else false
    }

    override fun travel(movementInput: Vec3d) {
        if (canMoveVoluntarily()) {
            updateVelocity(0.1f, movementInput)
            move(MovementType.SELF, velocity)
            velocity = velocity.multiply(0.9)
        }
    }

    override fun getPathfindingFavor(pos: BlockPos, world: WorldView): Float {
        if (world.getBlockState(pos).isAir) {
            var offset = 0.0f

            if (!world.getBlockState(pos.down()).isAir) {
                offset -= 2.0f
            }

            if (world is World && canHangAt(pos.up(), world, this, avoidsFallingBlocks)) {
                offset += 2.0f
            }

            @Suppress("DEPRECATION")
            return 1.0f / (world.getBrightness(pos)) + offset
        }

        return 0.0f
    }

    @Environment(EnvType.CLIENT)
    override fun handleStatus(status: Byte) {
        when (status) {
            EntityStatuses.CREATE_EATING_PARTICLES -> {
                val stack = getEquippedStack(EquipmentSlot.MAINHAND)
                if (!stack.isEmpty) {
                    for (i in 1..8) {
                        val velocity = Vec3d((random.nextFloat().toDouble() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                            .rotateX(-pitch * (Math.PI.toFloat() / 180))
                            .rotateY(-yaw * (Math.PI.toFloat() / 180))
                        world.addParticleClient(
                            ItemStackParticleEffect(ParticleTypes.ITEM, stack),
                            this.x + this.rotationVector.x / 2.0, this.y, this.z + this.rotationVector.z / 2.0,
                            velocity.x, velocity.y + 0.05, velocity.z
                        )
                    }
                }
            }

            else -> super.handleStatus(status)
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
        val world = world
        if (world is ServerWorld) {
            Vibrations.Ticker.tick(world, vibrationData, vibrationCallback)
        }

        super.tick()

        // prevent getting stuck below the world
        if (y <= world.bottomY) {
            val velocityCache = velocity
            setVelocity(velocityCache.x, 0.2, velocityCache.z)
        } else {
            velocity = if (hanging) Vec3d.ZERO else velocity.multiply(1.05)
        }
    }

    @Suppress("DEPRECATION")
    override fun mobTick(world: ServerWorld) {
        val targetCache = target
        if (targetCache is PlayerEntity && !isValidTarget(targetCache)) {
            target = null
        }

        super.mobTick(world)

        // tick pickup cooldown
        if (pickupCooldown > 0) {
            pickupCooldown--
        }

        if (!world.isClient) {
            if (hanging) {
                // if cannot hang, stop hanging
                if (!canHang) {
                    hanging = false

                    if (!isSilent) {
                        world.syncWorldEvent(null, WorldEvents.BAT_TAKES_OFF, blockPos, 0)
                    }
                }

                // eat held item
                val stack = getEquippedStack(EquipmentSlot.MAINHAND)
                if (!stack.isEmpty && (stack.components.contains(DataComponentTypes.FOOD) || stack.isIn(ItemTags.ARROWS))) {
                    if (eatingTime > MAX_EATING_TIME) { // finish eating
                        val finishedStack = stack.finishUsing(world, this)
                        if (stack.isIn(ItemTags.ARROWS)) { // if arrow, convert to direbat fang arrow
                            val arrowStack = stack.copy()
                            arrowStack.item = DirebatsItems.DIREBAT_FANG_ARROW
                            equipStack(EquipmentSlot.MAINHAND, arrowStack)
                        } else if (!finishedStack.isEmpty) { // else, equip finished stack
                            equipStack(EquipmentSlot.MAINHAND, finishedStack)
                        }
                        eatingTime = -1
                    } else { // during consumption
                        if (eatingTime > EATING_EFFECTS_TIME) {
                            // randomly play eating effects
                            if (random.nextFloat() < 0.1F) {
                                val consumableComponent = stack.get(DataComponentTypes.CONSUMABLE)
                                consumableComponent?.sound?.let {
                                    playSound(it.value(), soundVolume, soundPitch)
                                }

                                world.sendEntityStatus(this, EntityStatuses.CREATE_EATING_PARTICLES)
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
        val ceilingPos = blockPos.up()
        val ceilingState = world.getBlockState(ceilingPos)
        if (ceilingState.block is FallingBlock) {
            avoidsFallingBlocks = true
        }
        ceilingState.updateNeighbors(world, ceilingPos, Block.NOTIFY_ALL)
    }

    @Suppress("DEPRECATION")
    override fun tickMovement() {
        // skylight fire damage
        if (isAlive && isAffectedByDaylight) {
            // head protection
            val stack = getEquippedStack(EquipmentSlot.HEAD)
            if (!stack.isEmpty) {
                if (stack.isDamageable) {
                    stack.damage = stack.damage + random.nextInt(2)
                    if (stack.damage >= stack.maxDamage) {
                        sendEquipmentBreakStatus(stack.item, EquipmentSlot.HEAD)
                        equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY)
                    }
                }
            } else {
                setOnFireFor(8.0f)
            }
        }

        super.tickMovement()
    }

    /**
     * Accepts vibration events.
     */
    override fun updateEventHandler(callback: BiConsumer<EntityGameEventHandler<*>, ServerWorld>) {
        val world = world
        if (world is ServerWorld) {
            callback.accept(gameEventHandler, world)
        }
    }

    /**
     * @return the first available item to pick up
     */
    fun findItemEntityToPickUp(): ItemEntity? {
        val list = world.getEntitiesByClass(ItemEntity::class.java, boundingBox.expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER)
        return if (list.isNotEmpty()) list[random.nextInt(list.size)] else null
    }

    /**
     * @return whether a player should wake up a Direbat
     */
    fun isValidTarget(player: PlayerEntity): Boolean {
        if (world !== player.world) return false
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(player)) return false
        if (isTeammate(player)) return false
        if (player.isInvulnerable) return false
        if (player.isDead) return false
        return world.worldBorder.contains(player.boundingBox)
    }

    /* NBT */

    override fun writeCustomData(view: WriteView) {
        super.writeCustomData(view)

        view.put(LISTENER_KEY, Vibrations.ListenerData.CODEC, vibrationListenerData)

        view.putBoolean(HANGING_KEY, hanging)
        view.putBoolean(AVOIDS_FALLING_BLOCKS_KEY, avoidsFallingBlocks)
        view.putInt(EATING_TIME_KEY, eatingTime)
        view.putInt(HANGING_COOLDOWN_KEY, hangingCooldown)
        view.putInt(PICKUP_COOLDOWN_KEY, pickupCooldown)
    }

    override fun readCustomData(view: ReadView) {
        super.readCustomData(view)

        vibrationData = view.read<Vibrations.ListenerData>("listener", Vibrations.ListenerData.CODEC).orElseGet { Vibrations.ListenerData() }

        hanging = view.getBoolean(HANGING_KEY, false)
        avoidsFallingBlocks = view.getBoolean(AVOIDS_FALLING_BLOCKS_KEY, false)
        eatingTime = view.getInt(EATING_TIME_KEY, -1)
        hangingCooldown = view.getInt(HANGING_COOLDOWN_KEY, 0)
        pickupCooldown = view.getInt(PICKUP_COOLDOWN_KEY, 0)
    }

    override fun getVibrationListenerData(): Vibrations.ListenerData {
        return vibrationData
    }

    override fun getVibrationCallback(): Vibrations.Callback {
        return vibrationListenerCallback
    }

    companion object {
        val PICKABLE_DROP_FILTER = Predicate<ItemEntity> { entity -> entity != null &&
                !entity.cannotPickup() && entity.stack.isIn(DirebatsItemTags.PICKED_UP_BY_DIREBAT)
        }

        val HANGING: TrackedData<Boolean> = DataTracker.registerData(DirebatEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        val EATING_TIME: TrackedData<Int> = DataTracker.registerData(DirebatEntity::class.java, TrackedDataHandlerRegistry.INTEGER)

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
            world: ServerWorldAccess,
            spawnReason: SpawnReason,
            pos: BlockPos,
            random: Random
        ): Boolean {
            world as ServerWorld

            return if (pos.y >= world.seaLevel) {
                world.isNight && world.moonSize == 1.0f
            } else {
                val worldLight = world.getLightLevel(pos)
                val maximumLight = if (BatEntity.isTodayAroundHalloween()) 7 else 4
                worldLight <= random.nextInt(maximumLight) && canMobSpawn(type, world, spawnReason, pos, random)
            }
        }

        /**
         * @return the default Direbat attribute container
         */
        fun createDirebatAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 16.0)
                .add(EntityAttributes.FLYING_SPEED, 0.22)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.22)
                .add(EntityAttributes.ATTACK_DAMAGE, 4.0)
        }

        /**
         * @return whether a Direbat can hang at a given position
         */
        fun canHangAt(pos: BlockPos, world: World, entity: Entity, avoidsFallingBlocks: Boolean): Boolean {
            // if learnt about falling blocks, avoid falling blocks
            val ceilingPos = pos.up()
            val ceilingState = world.getBlockState(ceilingPos)
            if (avoidsFallingBlocks && ceilingState.block is FallingBlock) {
                return false
            }

            // check is cube
            if (!ceilingState.isFullCube(world, ceilingPos)) {
                return false
            }

            // check hanging position for already present direbats
            val mutable = pos.mutableCopy()
            for (i in 1..2) {
                val box = Box.of(Vec3d.of(mutable), 1.0, 1.0, 1.0)
                if (world.getEntitiesByClass(DirebatEntity::class.java, box) { direbatEntity -> direbatEntity !== entity && direbatEntity.hanging }.isNotEmpty()) {
                    return false
                }

                if (!world.getBlockState(mutable).getCollisionShape(world, mutable, ShapeContext.of(entity)).isEmpty) {
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
    class DirebatTargetGoal<T : LivingEntity>(mob: DirebatEntity, classTarget: Class<T>) : ActiveTargetGoal<T>(mob, classTarget, true) {
        override fun canStart(): Boolean {
            @Suppress("DEPRECATION")
            return mob.brightnessAtEyes >= 0.5 && !super.canStart()
        }
    }

    /**
     * Goal for a Direbat wandering.
     */
    class DirebatWanderGoal(val mob: DirebatEntity) : Goal() {
        init {
            controls = EnumSet.of(Control.MOVE)
        }

        override fun canStart(): Boolean {
            return mob.navigation.isIdle && !mob.hanging && mob.random.nextInt(5) == 0
        }

        override fun shouldContinue(): Boolean {
            return mob.navigation.isFollowingPath
        }

        override fun start() {
            calculateRandomLocation()?.run {
                val navigation = mob.navigation
                navigation.startMovingAlong(navigation.findPathTo(x, y, z, 1), 1.0)
            }
        }

        fun calculateRandomLocation(): Vec3d? {
            val rotation = if (mob.hasPositionTarget() && !mob.positionTarget.isWithinDistance(mob.pos, 22.0)) {
                Vec3d.ofCenter(mob.positionTarget).subtract(mob.pos).normalize()
            } else {
                mob.getRotationVec(0.0f)
            }

            return AboveGroundTargeting.find(
                mob, 8, 7, rotation.x, rotation.z,
                Math.PI.toFloat() / 2f, 2, 1
            ) ?: NoPenaltySolidTargeting.find(
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
            controls = EnumSet.of(Control.MOVE)
        }

        override fun canStart(): Boolean {
            // can't start if cooling down
            if (direbat.pickupCooldown > 0) {
                return false
            }

            // can't start if already has item
            if (!direbat.mainHandStack.isEmpty) {
                return false
            }

            // can't start if attacking
            if (direbat.target != null) {
                return false
            }

            // can't start if configured not to
            val world = direbat.world
            if (world is ServerWorld) {
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
                direbat.navigation.startMovingTo(this, 1.2)
            }

            // create cooldown
            direbat.pickupCooldown = 20 * 3
        }

        override fun tick() {
            // continue moving to
            itemEntity?.run {
                direbat.navigation.startMovingTo(this, 1.2)
            }
        }

        override fun shouldContinue(): Boolean {
            return direbat.navigation.isFollowingPath && direbat.mainHandStack.isEmpty
        }
    }

    inner class VibrationCallback : Vibrations.Callback {
        val positionSource = EntityPositionSource(this@DirebatEntity, standingEyeHeight)

        override fun getRange(): Int {
            return 8
        }

        override fun getPositionSource(): PositionSource {
            return positionSource
        }

        override fun accepts(world: ServerWorld, pos: BlockPos, event: RegistryEntry<GameEvent>, emitter: GameEvent.Emitter): Boolean {
            if (isAiDisabled || dead || !world.worldBorder.contains(pos)) {
                return false
            }

            if (target != null || !hanging) {
                return false
            }

            val entity = emitter.sourceEntity
            return entity is PlayerEntity && isValidTarget(entity)
        }

        override fun accept(world: ServerWorld, pos: BlockPos, event: RegistryEntry<GameEvent>, sourceEntity: Entity?, entity: Entity?, distance: Float) {
            if (dead) {
                return
            }

            if (sourceEntity is PlayerEntity) {
                target = sourceEntity

                val pitch = soundPitch * 1.4f
                ambientSound?.let { playSound(it, 1.0f, pitch) }
                playSound(attackSound, 1.0f, pitch)
            }
        }

        override fun getTag(): TagKey<GameEvent> {
            return DirebatsGameEventTags.DIREBAT_CAN_LISTEN
        }
    }
}
