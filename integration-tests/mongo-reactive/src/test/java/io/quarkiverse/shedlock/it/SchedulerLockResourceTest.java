package io.quarkiverse.shedlock.it;

import static com.mongodb.client.model.Filters.eq;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.mongodb.MongoClientName;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SchedulerLockResourceTest {
    @Inject
    ReactiveMongoClient defaultMongoClient;
    @Inject
    @MongoClientName("cluster1reactive")
    ReactiveMongoClient clusterOnereactiveMongoClient;

    @Test
    void testShedlockEndpoint() {
        given()
                .when().post("/shedlock")
                .then()
                .statusCode(204);

        assertThat(defaultMongoClient.getDatabase("shedLock")
                .getCollection("shedLock")
                .countDocuments(eq("_id", "io.quarkiverse.shedlock.it.SchedulerLockResource_runUsingLock"))
                .await().indefinitely())
                .isEqualTo(1L);
    }

    @Test
    void testShedlockEndpointOnClusterOneReactive() {
        given()
                .when().post("/shedlock/clusterOneReactive")
                .then()
                .statusCode(204);

        assertThat(clusterOnereactiveMongoClient.getDatabase("customDatabase")
                .getCollection("shedLock")
                .countDocuments(eq("_id", "io.quarkiverse.shedlock.it.SchedulerLockResource_runUsingLockOnClusterOne"))
                .await().indefinitely())
                .isEqualTo(1L);
    }

    @BeforeEach
    @AfterEach
    void drop() {
        defaultMongoClient.getDatabase("shedLock").drop().await().indefinitely();
        clusterOnereactiveMongoClient.getDatabase("customDatabase").drop().await().indefinitely();
    }
}
