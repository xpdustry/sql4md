// SPDX-License-Identifier: MIT
package com.xpdustry.sql4md.base;

import arc.util.Log;
import java.util.Objects;
import java.util.Properties;
import mindustry.mod.Plugin;

@SuppressWarnings("unused")
public final class DriverLoader extends Plugin {

    @Override
    public void init() {
        final String identifier;
        final String driver;
        try (final var stream = Objects.requireNonNull(
                this.getClass().getResourceAsStream("/sql4md.properties"), "Missing info file")) {
            final var properties = new Properties();
            properties.load(stream);
            identifier = Objects.requireNonNull(properties.getProperty("sql4md.implementation.identifier"));
            driver = Objects.requireNonNull(properties.getProperty("sql4md.implementation.driver"));
        } catch (final Exception e) {
            throw new RuntimeException("Failed to read the info file", e);
        }

        try {
            Class.forName(driver);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("Failed to load the " + driver + " JDBC driver", e);
        }

        final var message = "Loaded " + identifier + " JDBC driver";
        try {
            final var logger = Class.forName("org.slf4j.LoggerFactory")
                    .getMethod("getLogger", Class.class)
                    .invoke(null, this.getClass());
            Class.forName("org.slf4j.Logger").getMethod("info", String.class).invoke(logger, message);
        } catch (final ReflectiveOperationException e) {
            Log.info(message);
        }
    }
}
