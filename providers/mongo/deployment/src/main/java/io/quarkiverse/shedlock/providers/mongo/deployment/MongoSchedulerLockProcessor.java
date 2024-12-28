package io.quarkiverse.shedlock.providers.mongo.deployment;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.quarkus.mongodb.runtime.MongoClientBeanUtil;
import jakarta.inject.Singleton;

import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;

import io.quarkiverse.shedlock.common.runtime.InstantProvider;
import io.quarkiverse.shedlock.common.runtime.SchedulerLockExecutor;
import io.quarkiverse.shedlock.common.runtime.ShedLockConfiguration;
import io.quarkiverse.shedlock.providers.mongo.runtime.MongoConfig;
import io.quarkiverse.shedlock.providers.mongo.runtime.MongoSchedulerLockExecutorRecorder;
import io.quarkiverse.shedlock.providers.mongo.runtime.MongoSchedulerLockInterceptor;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.mongodb.MongoClientName;

public class MongoSchedulerLockProcessor {

    private static final String FEATURE = "mongoSchedulerLock";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void produceBeans(final BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        additionalBeanBuildItemProducer.produce(
                AdditionalBeanBuildItem.builder()
                        .setUnremovable()
                        .addBeanClasses(MongoSchedulerLockInterceptor.class)
                        .build());
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    List<SyntheticBeanBuildItem> registerJdbcSchedulerLockExecutors(
            final CombinedIndexBuildItem combinedIndexBuildItem,
            final ShedLockConfiguration shedLockConfiguration,
            final MongoConfig mongoConfig,
            final MongoSchedulerLockExecutorRecorder mongoSchedulerLockExecutorRecorder) {
        final Set<String> mongoClientsName = combinedIndexBuildItem.getComputingIndex()
                .getAnnotations(MongoClientName.class)
                .stream()
                .map(name -> name.value() != null ? name.value().asString() : MongoClientBeanUtil.DEFAULT_MONGOCLIENT_NAME)
                .collect(Collectors.toSet());

        return mongoClientsName.stream()
                .map(mongoClientName -> SyntheticBeanBuildItem.configure(SchedulerLockExecutor.class)
                        .types(SchedulerLockExecutor.class)
                        .scope(Singleton.class)
                        .createWith(mongoSchedulerLockExecutorRecorder.schedulerLockExecutorSupplier(shedLockConfiguration,
                                mongoConfig, mongoClientName))
                        .addQualifier().annotation(MongoClientName.class).addValue("value", mongoClientName).done()
                        .addInjectionPoint(ClassType.create(DotName.createSimple(InstantProvider.class)))
                        .unremovable()
                        .setRuntimeInit()
                        .done())
                .toList();
    }
}
