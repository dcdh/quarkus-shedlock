package io.quarkiverse.shedlock.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import io.quarkiverse.shedlock.providers.mongo.reactive.runtime.runtime.MongoReactiveSchedulerLock;

@Path("/shedlock")
@ApplicationScoped
public class SchedulerLockResource {
    @GET
    @MongoReactiveSchedulerLock
    public void runUsingLock() {
    }

    @GET
    @Path("/clusterOneReactive")
    @MongoReactiveSchedulerLock(mongoClientName = "cluster1reactive")
    public void runUsingLockOnClusterOne() {
    }
}
