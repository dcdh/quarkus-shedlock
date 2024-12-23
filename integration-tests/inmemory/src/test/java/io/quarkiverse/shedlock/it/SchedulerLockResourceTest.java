package io.quarkiverse.shedlock.it;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SchedulerLockResourceTest {
    @Test
    void testShedlockEndpoint() {
        given()
                .when().get("/shedlock")
                .then()
                .statusCode(204);
    }
}
