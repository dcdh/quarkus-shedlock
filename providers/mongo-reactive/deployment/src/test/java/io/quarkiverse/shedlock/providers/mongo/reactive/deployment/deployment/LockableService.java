package io.quarkiverse.shedlock.providers.mongo.reactive.deployment.deployment;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkiverse.shedlock.providers.mongo.reactive.runtime.runtime.MongoReactiveSchedulerLock;

@ApplicationScoped
public class LockableService {
    @MongoReactiveSchedulerLock
    void execute() {
    }
}
