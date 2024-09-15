package com.mr.x.core.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mr.x.core.ui.components.VisibilityState.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay

@Stable
class AwaitedEnterTransition(
	val duration: Int,
	val convert: (Int) -> EnterTransition
) {
	val transition: EnterTransition = convert(duration)
}

operator fun AwaitedEnterTransition.times(value: Int): AwaitedEnterTransition {
	val newDuration = duration * value
	return AwaitedEnterTransition(newDuration, convert)
}

@Stable
class AwaitedExitTransition(
	val duration: Int,
	val convert: (Int) -> ExitTransition
) {
	val transition: ExitTransition = convert(duration)
}

operator fun AwaitedExitTransition.times(value: Int): AwaitedExitTransition {
	val newDuration = duration * value
	return AwaitedExitTransition(newDuration, convert)
}

private const val DEFAULT_DURATION = 300

val defaultExitTransition =
	AwaitedExitTransition(DEFAULT_DURATION) { shrinkVertically(tween(it)) + fadeOut(tween(it)) }

val defaultEnterTransition =
	AwaitedEnterTransition(DEFAULT_DURATION) { expandVertically(tween(it)) + fadeIn(tween(it)) }


@Stable
private data class WithVisibility<T>(
	val data: T,
	val state: VisibilityState = Initial
)

/**
 * The state of each widget in an [AnimatedColumn].
 * @property Initial the state of all widgets that are passed to the column at the first composition.
 * @property ToPopUp the state that is applied to all widgets that are new compared to the previous version of the list.
 * @property ToClose the state of all widgets that are missing in the new version of the list.
 * When marked with this state, a closing animation will be launched for its widget.
 * After the animation is complete, the item of this widget is deleted.
 */
@Stable
enum class VisibilityState {
	Initial,
	ToPopUp,
	ToClose;

	fun toInitialState(): Boolean = when (this) {
		Initial -> true
		ToClose -> true
		ToPopUp -> false
	}

	fun toTargetState(): Boolean = when (this) {
		Initial -> true
		ToClose -> false
		ToPopUp -> true
	}
}

@Stable
data class ColumnChange<T>(
	val removed: Iterable<IndexedValue<T>>,
	val added: Iterable<IndexedValue<T>>
)

private fun <T> findDifference(
	initial: List<WithVisibility<T>>,
	changed: List<T>,
	keyProvider: (T) -> String,
): ColumnChange<T> {
	val initialTransformed = initial.map { it.data }
	val removedIndices = processChanges(changed, keyProvider, initialTransformed)
	val addedIndices = processChanges(initialTransformed, keyProvider, changed)
	return ColumnChange(removedIndices, addedIndices)
}

private fun <T> processChanges(
	changed: List<T>,
	keyProvider: (T) -> String,
	initialTransformed: List<T>
): ImmutableList<IndexedValue<T>> {
	val removedIndices = mutableListOf<IndexedValue<T>>()
	val mapChanged = changed.withIndex().associate { keyProvider(it.value) to it.index }
	initialTransformed.forEachIndexed { index, value ->
		if (mapChanged[keyProvider(value)] == null) {
			removedIndices.add(IndexedValue(index, value))
		}
	}
	return removedIndices.toPersistentList()
}


private fun <T> List<T>.toVisibleItems() = map { WithVisibility(it) }


/**
 *  A non-scrollable column with items addition and deletion animations.
 *  When a new list of items is fed into this composable, it detects new items and runs appearance animations for them.
 *  It also detects deleted items and removes them dynamically with animation.
 *  Mind that this column is not lazy.
 *
 *  @param items a list of items of any type ([T]).
 *  @param keyProvider a lambda that outputs a key that an item can be uniquely identified with.
 *  Duplicate keys will not crash your application as with an [androidx.compose.foundation.lazy.LazyColumn], but may produce incorrect behaviour.
 *  @param modifier the [Modifier] to be applied to the column.
 *  @param enterTransition the [EnterTransition] to be applied to each entering widget.
 *  @param exitTransition the [ExitTransition] to be applied to each exiting widget.
 *  @param itemContent an individual widget for an item of type [T]
 */
@Composable
fun <T : Any> AnimatedColumn(
	items: ImmutableList<T>,
	keyProvider: (T) -> String,
	modifier: Modifier = Modifier,
	enterTransition: AwaitedEnterTransition = defaultEnterTransition,
	exitTransition: AwaitedExitTransition = defaultExitTransition,
	itemContent: @Composable AnimatedVisibilityScope.(Int, T) -> Unit
) = AnimatedColumnImpl(
	items = items,
	keyProvider = keyProvider,
	modifier = modifier,
	enterTransition = enterTransition,
	exitTransition = exitTransition,
	isLazy = false
) { index, item ->
	itemContent(index, item)
}


/**
 *  Absolutely the same as [AnimatedColumn], but backed by a lazy column.
 *
 *  @param items a list of items of any type ([T]). The items should be wrapped in a [State] to keep them a stable argument.
 *  @param keyProvider a lambda that outputs a key that an item can be uniquely identified with.
 *  @param modifier the [Modifier] to be applied to the column.
 *  @param enterTransition the [EnterTransition] to be applied to each entering widget.
 *  @param exitTransition the [ExitTransition] to be applied to each exiting widget.
 *  @param itemContent an individual widget for an item of type [T]
 */
@Composable
fun <T : Any> LazyAnimatedColumn(
	items: ImmutableList<T>,
	keyProvider: (T) -> String,
	modifier: Modifier = Modifier,
	lazyModifier: LazyItemScope.() -> Modifier = { Modifier },
	contentPadding: PaddingValues = PaddingValues(0.dp),
	horizontalAlignment: Alignment.Horizontal = Alignment.Start,
	state: LazyListState = rememberLazyListState(),
	enterTransition: AwaitedEnterTransition = defaultEnterTransition,
	exitTransition: AwaitedExitTransition = defaultExitTransition,
	itemContent: @Composable AnimatedVisibilityScope.(Int, T) -> Unit
) = AnimatedColumnImpl(
	items = items,
	keyProvider = keyProvider,
	modifier = modifier,
	lazyModifier = lazyModifier,
	state = state,
	enterTransition = enterTransition,
	exitTransition = exitTransition,
	isLazy = true,
	horizontalAlignment = horizontalAlignment,
	contentPadding = contentPadding
) { index, item ->
	itemContent(index, item)
}

@Composable
private fun <T> AnimatedColumnImpl(
	items: ImmutableList<T>,
	keyProvider: (T) -> String,
	modifier: Modifier = Modifier,
	lazyModifier: LazyItemScope.() -> Modifier = { Modifier },
	contentPadding: PaddingValues = PaddingValues(0.dp),
	horizontalAlignment: Alignment.Horizontal = Alignment.Start,
	state: LazyListState = rememberLazyListState(),
	enterTransition: AwaitedEnterTransition = defaultEnterTransition,
	exitTransition: AwaitedExitTransition = defaultExitTransition,
	isLazy: Boolean = false,
	itemContent: @Composable AnimatedVisibilityScope.(Int, T) -> Unit
) {
	val internalList = remember {
		items.toVisibleItems().toMutableStateList()
	}

	LaunchedEffect(items) {
		val (removedItems, addedItems) = findDifference(internalList, items, keyProvider)
		val total = removedItems + addedItems
		if (total.isNotEmpty()) {
			internalList.run {
				for (removed in removedItems) {
					this[removed.index] = this[removed.index].copy(state = ToClose)
				}
				for (added in addedItems) {
					add(added.index, WithVisibility(added.value, ToPopUp))
				}
			}
			if (addedItems.toPersistentList().size == 1) {
				state.animateScrollToItem(addedItems.first().index, scrollOffset = -500)
			}
			delay(maxOf(enterTransition.duration, exitTransition.duration).toLong())
			internalList.clearUpdate(items)
		}
		else {
			internalList.clearUpdate(items)
		}
	}

	if (isLazy) {
		LazyColumn(
			modifier = modifier,
			state = state,
			horizontalAlignment = horizontalAlignment,
			contentPadding = contentPadding
		) {
			items(
				count = internalList.size,
				key = { keyProvider(internalList[it].data) }
			) { index ->
				val internalLazyModifier = lazyModifier()
				val currentItem = internalList[index]
				AnimatedRemovable(
					key = keyProvider(currentItem.data),
					state = currentItem.state,
					modifier = internalLazyModifier,
					enterTransition = enterTransition.transition,
					exitTransition = exitTransition.transition
				) {
					itemContent(index, currentItem.data)
				}
			}
			item { Spacer(Modifier.height(2.dp)) }
		}
	}
	else {
		Column(modifier) {
			internalList.forEachIndexed { index, item ->
				AnimatedRemovable(
					key = keyProvider(item.data),
					state = item.state,
					enterTransition = enterTransition.transition,
					exitTransition = exitTransition.transition
				) {
					itemContent(index, item.data)
				}
			}
		}
	}
}

private fun <T> SnapshotStateList<WithVisibility<T>>.clearUpdate(
	items: List<T>
) {
	clear()
	addAll(items.toVisibleItems())
}

@Composable
internal fun AnimatedRemovable(
	key: String,
	state: VisibilityState,
	modifier: Modifier = Modifier,
	enterTransition: EnterTransition = defaultEnterTransition.transition,
	exitTransition: ExitTransition = defaultExitTransition.transition,
	content: @Composable AnimatedVisibilityScope.() -> Unit
) {
	val internalVisible = remember(key) {
		MutableTransitionState(state.toInitialState())
	}
	LaunchedEffect(state) {
		internalVisible.targetState = state.toTargetState()
	}
	AnimatedVisibility(
		visibleState = internalVisible,
		enter = enterTransition,
		exit = exitTransition,
		modifier = modifier
	) {
		Column {
			content()
		}
	}
}
