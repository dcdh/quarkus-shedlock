package io.quarkiverse.shedlock.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import io.quarkiverse.shedlock.providers.inmemory.runtime.InMemorySchedulerLock;
import io.quarkiverse.shedlock.providers.jdbc.runtime.JdbcSchedulerLock;
import io.quarkiverse.shedlock.providers.mongo.runtime.MongoSchedulerLock;

@Path("/shedlock")
@ApplicationScoped
public class SchedulerLockResource {
    @POST
    @Path("/in-memory")
    @InMemorySchedulerLock
    public void runUsingInMemoryLock() {
    }

    @POST
    @Path("/jdbc")
    @JdbcSchedulerLock
    public void runUsingJdbcLock() {
    }

    @POST
    @Path("/mongo")
    @MongoSchedulerLock
    public void runUsingMongoLock() {
    }
}
