
package io.collapp.common;

import org.springframework.context.ApplicationEvent;

public class DatabaseMigrationDoneEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4359436382324341606L;

    public DatabaseMigrationDoneEvent(Object source) {
        super(source);
    }
}
