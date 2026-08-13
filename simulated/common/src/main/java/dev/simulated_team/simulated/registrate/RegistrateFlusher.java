package dev.simulated_team.simulated.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RegistrateFlusher {
	private static final Logger LOGGER = LoggerFactory.getLogger("simulated/registrate-flush");

	private RegistrateFlusher() {
	}

	public static void flush(final AbstractRegistrate<?> registrate) {
		try {
			final Field registrationsField = findField(AbstractRegistrate.class, "registrations");
			registrationsField.setAccessible(true);
			final Object table = registrationsField.get(registrate);
			final Map<?, Map<?, ?>> rowMap = rowMap(table);

			for (final Registry<?> registry : BuiltInRegistries.REGISTRY) {
				final Map<?, ?> row = rowMap.get(registry.key());
				if (row == null || row.isEmpty()) {
					continue;
				}
				for (final Object registration : row.values()) {
					flushRegistration(registry, registration);
				}
				runAfterCallbacks(registrate, registry.key());
				markCompleted(registrate, registry.key());
			}
		} catch (final ReflectiveOperationException e) {
			throw new RuntimeException("Failed to flush CreateRegistrate entries on Fabric", e);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void flushRegistration(final Registry registry, final Object registration) throws ReflectiveOperationException {
		final ResourceLocation name = (ResourceLocation) registration.getClass().getMethod("getName").invoke(registration);
		final NonNullSupplier<?> creator = (NonNullSupplier<?>) registration.getClass().getMethod("getCreator").invoke(registration);
		final RegistryEntry<?, ?> delegate = (RegistryEntry<?, ?>) registration.getClass().getMethod("getDelegate").invoke(registration);

		if (name == null || creator == null) {
			LOGGER.warn("Skipping unreadable registrate entry {}", registration);
			return;
		}

		final Object value;
		if (registry.containsKey(name)) {
			value = registry.get(name);
			bind(delegate);
		} else {
			value = creator.get();
			Registry.register(registry, name, value);
			bind(delegate);
		}

		fireCallbacks(registration, value);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void fireCallbacks(final Object registration, final Object value) throws ReflectiveOperationException {
		final Field callbacksField = findField(registration.getClass(), "callbacks");
		callbacksField.setAccessible(true);
		final List callbacks = (List) callbacksField.get(registration);
		for (final Object callback : new ArrayList<>(callbacks)) {
			((NonNullConsumer) callback).accept(value);
		}
		callbacks.clear();
	}

	@SuppressWarnings("unchecked")
	private static void runAfterCallbacks(final AbstractRegistrate<?> registrate, final ResourceKey<?> registryKey) throws ReflectiveOperationException {
		final Field field = findField(AbstractRegistrate.class, "afterRegisterCallbacks");
		field.setAccessible(true);
		final Object multimap = field.get(registrate);
		final Collection<Runnable> callbacks = (Collection<Runnable>) multimap.getClass()
				.getMethod("get", Object.class)
				.invoke(multimap, registryKey);
		for (final Runnable callback : new ArrayList<>(callbacks)) {
			callback.run();
		}
		callbacks.clear();
	}

	@SuppressWarnings("unchecked")
	private static void markCompleted(final AbstractRegistrate<?> registrate, final ResourceKey<?> registryKey) throws ReflectiveOperationException {
		final Field field = findField(AbstractRegistrate.class, "completedRegistrations");
		field.setAccessible(true);
		((Set<ResourceKey<?>>) field.get(registrate)).add(registryKey);
	}

	private static void bind(final Object entry) {
		if (entry == null) {
			return;
		}
		try {
			final Method bind = findMethod(entry.getClass(), "bind", boolean.class);
			bind.setAccessible(true);
			bind.invoke(entry, false);
		} catch (final ReflectiveOperationException ignored) {
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<?, Map<?, ?>> rowMap(final Object table) throws ReflectiveOperationException {
		return (Map<?, Map<?, ?>>) table.getClass().getMethod("rowMap").invoke(table);
	}

	private static Field findField(final Class<?> type, final String name) throws NoSuchFieldException {
		Class<?> current = type;
		while (current != null) {
			try {
				return current.getDeclaredField(name);
			} catch (final NoSuchFieldException ignored) {
				current = current.getSuperclass();
			}
		}
		throw new NoSuchFieldException(name);
	}

	private static Method findMethod(final Class<?> type, final String name, final Class<?>... params) throws NoSuchMethodException {
		Class<?> current = type;
		while (current != null) {
			try {
				return current.getDeclaredMethod(name, params);
			} catch (final NoSuchMethodException ignored) {
				current = current.getSuperclass();
			}
		}
		throw new NoSuchMethodException(name);
	}
}
