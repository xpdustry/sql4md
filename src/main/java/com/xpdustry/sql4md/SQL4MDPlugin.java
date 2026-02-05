// SPDX-License-Identifier: MIT
package com.xpdustry.sql4md;

import mindustry.mod.Plugin;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public final class SQL4MDPlugin extends Plugin {

    @Override
    public void init() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Class.forName("org.sqlite.JDBC");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("org.h2.Driver");
            Class.forName("org.postgresql.Driver");
            LoggerFactory.getLogger(SQL4MDPlugin.class)
                    .info("Loaded JDBC drivers ({}, {}, {}, {}, {})", "MariaDB", "SQLite", "MySQL", "H2", "PostGres");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JDBC driver", e);
        }
    }
}
