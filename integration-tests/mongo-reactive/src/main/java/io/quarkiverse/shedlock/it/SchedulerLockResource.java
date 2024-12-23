package io.quarkiverse.shedlock.it;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import io.quarkiverse.shedlock.providers.mongo.reactive.runtime.runtime.MongoReactiveSchedulerLock;

@Path("/shedlock")
@ApplicationScoped
public class SchedulerLockResource {
    @POST
    @MongoReactiveSchedulerLock
    public void runUsingLock() {
    }

    @POST
    @Path("/clusterOneReactive")
    @MongoReactiveSchedulerLock(mongoClientName = "cluster1reactive")
    public void runUsingLockOnClusterOne() {
    }
}
