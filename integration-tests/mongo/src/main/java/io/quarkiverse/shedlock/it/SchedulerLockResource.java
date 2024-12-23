package io.quarkiverse.shedlock.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import io.quarkiverse.shedlock.providers.mongo.runtime.MongoSchedulerLock;

@Path("/shedlock")
@ApplicationScoped
public class SchedulerLockResource {
    @POST
    @MongoSchedulerLock
    public void runUsingLock() {
    }

    @POST
    @Path("/clusterOne")
    @MongoSchedulerLock(mongoClientName = "cluster1")
    public void runUsingLockOnClusterOne() {
    }
}
