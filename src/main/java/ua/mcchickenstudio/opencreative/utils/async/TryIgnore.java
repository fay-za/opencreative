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

package ua.mcchickenstudio.opencreative.utils.async;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class TryIgnore {

    public static final ThrowableHandler throwableHandler = Throwable::printStackTrace;

    public static <T> T unchecked(SupplierThrows<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            doThrow0(e);
            throw new AssertionError();
        }
    }

    public static void unchecked(RunnableThrows runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            doThrow0(e);
            throw new AssertionError(); // до сюда код не дойдет
        }
    }

    public static <T> Predicate<T> unchecked(PredicateThrows<T> predicate) {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Exception e) {
                doThrow0(e);
                throw new AssertionError(); // до сюда код не дойдет
            }
        };
    }

    public static <T> T ignore(SupplierThrows<T> supplier, T def) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            throwableHandler.handle(e);
            return def;
        }
    }

    public static Optional<Throwable> ignore(RunnableThrows runnable) {
        try {
            runnable.run();
        } catch (Throwable e) {
            throwableHandler.handle(e);
            return Optional.of(e);
        }
        return Optional.empty();
    }

    public static Optional<Throwable> ignore(RunnableThrows runnable, Consumer<Throwable> consumer) {
        try {
            runnable.run();
        } catch (Throwable e) {
            consumer.accept(e);
            throwableHandler.handle(e);
            return Optional.of(e);
        }
        return Optional.empty();
    }

    public static <T> Predicate<T> ignore(PredicateThrows<T> predicate, boolean def) {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Throwable e) {
                throwableHandler.handle(e);
                return def;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void doThrow0(Throwable e) throws E {
        throw (E) e;
    }

    public interface SupplierThrows<T> {

        T get();
    }

    public interface RunnableThrows {

        void run();
    }

    public interface PredicateThrows<T> {

        boolean test(T val);
    }

    public interface ThrowableHandler {

        void handle(Throwable throwable);
    }
}