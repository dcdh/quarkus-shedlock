package io.quarkiverse.shedlock.providers.mongo.reactive.deployment.deployment;

import static com.mongodb.client.model.Filters.eq;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.builder.Version;
import io.quarkus.maven.dependency.Dependency;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.test.QuarkusUnitTest;

class MongoReactiveSchedulerLockTest {
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(LockableService.class)
                    .addAsResource(new StringAsset("quarkus.shedlock.defaults-lock-at-most-for=PT30S"),
                            "application.properties"))
            .setForcedDependencies(List.of(
                    Dependency.of("io.quarkus", "quarkus-mongodb-client", Version.getVersion())));

    @Inject
    LockableService lockableService;

    @Inject
    ReactiveMongoClient reactiveMongoClient;

    @Test
    void shouldUseDefaultDatabaseName() {
        lockableService.execute();

        final List<String> databaseNames = reactiveMongoClient.listDatabaseNames().collect().asList().await().indefinitely();
        assertThat(databaseNames).contains("shedLock");
    }

    @Test
    void shouldCreateALock() {
        lockableService.execute();

        final Long documents = reactiveMongoClient.getDatabase("shedLock").getCollection("shedLock")
                .countDocuments(eq("_id",
                        "io.quarkiverse.shedlock.providers.mongo.reactive.deployment.deployment.LockableService_execute"))
                .await().indefinitely();
        assertThat(documents).isEqualTo(1L);
    }

    @BeforeEach
    @AfterEach
    void dropCollections() {
        reactiveMongoClient.getDatabase("shedLock").drop().await().indefinitely();
    }
}