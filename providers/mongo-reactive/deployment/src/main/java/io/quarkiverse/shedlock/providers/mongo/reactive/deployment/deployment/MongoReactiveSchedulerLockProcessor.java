package io.quarkiverse.shedlock.providers.mongo.reactive.deployment.deployment;

import io.quarkiverse.shedlock.common.runtime.InstantProvider;
import io.quarkiverse.shedlock.common.runtime.SchedulerLockExecutor;
import io.quarkiverse.shedlock.common.runtime.ShedLockConfiguration;
import io.quarkiverse.shedlock.providers.mongo.reactive.runtime.runtime.MongoReactiveConfig;
import io.quarkiverse.shedlock.providers.mongo.reactive.runtime.runtime.MongoReactiveSchedulerLockExecutorRecorder;
import io.quarkiverse.shedlock.providers.mongo.reactive.runtime.runtime.MongoReactiveSchedulerLockInterceptor;
import io.quarkiverse.shedlock.providers.mongo.reactive.runtime.runtime.Reactive;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.mongodb.MongoClientName;
import io.quarkus.mongodb.runtime.MongoClientBeanUtil;
import jakarta.inject.Singleton;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MongoReactiveSchedulerLockProcessor {

    private static final String FEATURE = "mongoReactiveSchedulerLock";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void produceBeans(final BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        additionalBeanBuildItemProducer.produce(
                AdditionalBeanBuildItem.builder()
                        .setUnremovable()
                        .addBeanClasses(MongoReactiveSchedulerLockInterceptor.class)
                        .build());
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    List<SyntheticBeanBuildItem> registerJdbcSchedulerLockExecutors(
            final CombinedIndexBuildItem combinedIndexBuildItem,
            final ShedLockConfiguration shedLockConfiguration,
            final MongoReactiveConfig mongoReactiveConfig,
            final MongoReactiveSchedulerLockExecutorRecorder mongoReactiveSchedulerLockExecutorRecorder) {
        final Set<String> mongoClientsName = combinedIndexBuildItem.getComputingIndex()
                .getAnnotations(MongoClientName.class)
                .stream()
                .map(name -> name.value() != null ? name.value().asString() : MongoClientBeanUtil.DEFAULT_MONGOCLIENT_NAME)
                .collect(Collectors.toSet());

        return mongoClientsName.stream()
                .map(mongoClientName -> SyntheticBeanBuildItem.configure(SchedulerLockExecutor.class)
                        .scope(Singleton.class)
                        .createWith(mongoReactiveSchedulerLockExecutorRecorder.schedulerLockExecutorSupplier(shedLockConfiguration, mongoReactiveConfig, mongoClientName))
                        .addQualifier().annotation(MongoClientName.class).addValue("value", mongoClientName).done()
                        .addQualifier().annotation(Reactive.class).done()
                        .addInjectionPoint(ClassType.create(DotName.createSimple(InstantProvider.class)))
                        .unremovable()
                        .setRuntimeInit()
                        .done())
                .toList();
    }
}
