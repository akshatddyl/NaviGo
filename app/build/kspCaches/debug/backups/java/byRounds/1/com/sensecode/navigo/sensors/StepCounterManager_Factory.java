package com.sensecode.navigo.sensors;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class StepCounterManager_Factory implements Factory<StepCounterManager> {
  private final Provider<Context> contextProvider;

  public StepCounterManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public StepCounterManager get() {
    return newInstance(contextProvider.get());
  }

  public static StepCounterManager_Factory create(Provider<Context> contextProvider) {
    return new StepCounterManager_Factory(contextProvider);
  }

  public static StepCounterManager newInstance(Context context) {
    return new StepCounterManager(context);
  }
}
