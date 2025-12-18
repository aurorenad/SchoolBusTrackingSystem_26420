package auca.ac.rw.transportManagementSystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Fixes legacy DB check constraints that don't auto-update when enums change.
 *
 * Problem seen in logs:
 * - ERROR: new row for relation "users" violates check constraint "users_role_check"
 *
 * This happens after adding new Role enum values (e.g. DRIVER) while using ddl-auto=update.
 */
@Component
public class DatabaseSchemaFixer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaFixer.class);

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate tx;

    public DatabaseSchemaFixer(JdbcTemplate jdbcTemplate, PlatformTransactionManager txManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.tx = new TransactionTemplate(txManager);
    }

    @Override
    public void run(String... args) {
        fixUsersRoleCheckConstraint();
    }

    private void fixUsersRoleCheckConstraint() {
        try {
            tx.execute(status -> {
                try {
                    Integer exists = jdbcTemplate.queryForObject(
                            "select 1 " +
                                    "from information_schema.table_constraints " +
                                    "where table_schema = 'public' and table_name = 'users' and constraint_name = 'users_role_check'",
                            Integer.class
                    );

                    if (exists == null) {
                        return null;
                    }

                    log.warn("Updating legacy constraint users_role_check to include current Role enum values...");

                    jdbcTemplate.execute("alter table users drop constraint if exists users_role_check");

                    // IMPORTANT: "role" is a reserved keyword in Postgres, so quote the column name.
                    jdbcTemplate.execute(
                            "alter table users add constraint users_role_check " +
                                    "check (\"role\" in ('PARENT','ADMIN','TEACHER','DRIVER','STUDENT'))"
                    );
                } catch (DataAccessException e) {
                    status.setRollbackOnly();
                    throw e;
                }
                return null;
            });
        } catch (Exception e) {
            // Don't break app startup if constraint doesn't exist / schema differs.
            log.warn("DatabaseSchemaFixer: could not update users_role_check: {}", e.getMessage());
        }
    }
}


