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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ua.mcchickenstudio.opencreative.utils.ErrorUtils;

import java.lang.reflect.Field;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * <h1>AsyncScheduler</h1>
 * This class represents a scheduler, that executes
 * runnables by asynchronous way.
 *
 * @author kireikosasha
 * @since 5.0
 */
public class AsyncScheduler {

    private static final char INNER_CLASS_SEPARATOR_CHAR = '$';
    private static final int STOP_WATCH_TIME_MILLIS = 750;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(36,
            new ThreadFactoryBuilder().setNameFormat("opencreative-thread-%d").build());

    private AsyncScheduler() {
    }

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static void shutdown(ScheduledExecutorService schedulerCustom) {
        TryIgnore.ignore(schedulerCustom::shutdownNow);
    }

    public static Future<?> run(Runnable runnable, ScheduledExecutorService schedulerCustom) {
        return schedulerCustom.submit(new DecoratedRunnable(runnable));
    }

    public static <T> Future<T> run(Callable<T> callable, ScheduledExecutorService schedulerCustom) {
        return schedulerCustom.submit(new DecoratedCallable<>(callable));
    }

    public static ScheduledFuture<?> later(Runnable runnable, ScheduledExecutorService schedulerCustom, long delay, TimeUnit time) {
        return schedulerCustom.schedule(new DecoratedRunnable(runnable), delay, time);
    }

    public static ScheduledFuture<?> timer(Runnable runnable, ScheduledExecutorService schedulerCustom, long delay, long period, TimeUnit time) {
        return schedulerCustom.scheduleAtFixedRate(new DecoratedRunnable(runnable), delay, period, time);
    }

    public static void cancel(ScheduledFuture<?> timer) {
        try {
            if (timer != null) {
                timer.cancel(true);
            }
        } catch (Exception ignored) {
        }
    }

    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        Class<?> clazz = object.getClass();
        StringBuilder sb = new StringBuilder(clazz.getSimpleName() + "{");
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                if (field.getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) != -1) {
                    sb.append(field.getName()).append("=");
                    Object value = field.get(object);
                    sb.append(value == null ? "null" : value.toString());
                }
            } catch (IllegalAccessException e) {
                sb.append(field.getName()).append("=<access denied>");
            }
            if (i < fields.length - 1) {
                sb.append(", ");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    public static class DecoratedRunnable implements Runnable {

        private static final Function<Runnable, Runnable> hotfixDecorator = runnable -> runnable;

        private final Runnable originalRunnable;
        private final Runnable decoratedRunnable;

        public DecoratedRunnable(Runnable originalRunnable) {
            this.originalRunnable = originalRunnable;
            this.decoratedRunnable = hotfixDecorator.apply(originalRunnable);
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            try {
                decoratedRunnable.run();
            } catch (Throwable throwable) {
                ErrorUtils.sendCriticalErrorMessage("Asynchronous task error " + AsyncScheduler.toString(originalRunnable), new Exception(throwable));
            } finally {
                long after = System.currentTimeMillis() - start;
                if (after > STOP_WATCH_TIME_MILLIS) {
                    ErrorUtils.sendCriticalErrorMessage("Asynchronous task took longer time (" + after + " ms) than expected. " + AsyncScheduler.toString(originalRunnable));
                }
            }
        }
    }

    public static class DecoratedCallable<T> implements Callable<T> {

        private final static Function<Callable<?>, Callable<?>> hotfixDecorator = callable -> callable;

        private final Callable<T> originalCallable;
        private final Callable<T> decoratedCallable;

        @SuppressWarnings("unchecked")
        public DecoratedCallable(Callable<T> originalCallable) {
            this.originalCallable = originalCallable;
            this.decoratedCallable = (Callable<T>) hotfixDecorator.apply(originalCallable);
        }

        @Override
        public T call() throws Exception {
            long start = System.currentTimeMillis();
            try {
                return decoratedCallable.call();
            } catch (Throwable throwable) {
                ErrorUtils.sendCriticalErrorMessage("Asynchronous task error " + AsyncScheduler.toString(decoratedCallable), new Exception(throwable));
                throw throwable;
            } finally {
                long after = System.currentTimeMillis() - start;
                if (after > STOP_WATCH_TIME_MILLIS) {
                    ErrorUtils.sendCriticalErrorMessage("Asynchronous task took longer time (" + after + " ms) than expected. " + AsyncScheduler.toString(decoratedCallable));
                }
            }
        }
    }
}
