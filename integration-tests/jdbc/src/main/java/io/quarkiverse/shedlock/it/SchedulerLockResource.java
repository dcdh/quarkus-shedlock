package io.quarkiverse.shedlock.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import io.quarkiverse.shedlock.providers.jdbc.runtime.JdbcSchedulerLock;

@Path("/shedlock")
@ApplicationScoped
public class SchedulerLockResource {
    @POST
    @JdbcSchedulerLock
    public void runUsingLock() {
    }

    @POST
    @Path("/master")
    @JdbcSchedulerLock(dataSourceName = "master")
    public void runUsingLockOnMaster() {
    }
}
