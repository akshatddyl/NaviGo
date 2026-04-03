package com.sensecode.navigo.di;

import android.content.Context;
import android.hardware.SensorManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class SensorModule_ProvideSensorManagerFactory implements Factory<SensorManager> {
  private final Provider<Context> contextProvider;

  public SensorModule_ProvideSensorManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SensorManager get() {
    return provideSensorManager(contextProvider.get());
  }

  public static SensorModule_ProvideSensorManagerFactory create(Provider<Context> contextProvider) {
    return new SensorModule_ProvideSensorManagerFactory(contextProvider);
  }

  public static SensorManager provideSensorManager(Context context) {
    return Preconditions.checkNotNullFromProvides(SensorModule.INSTANCE.provideSensorManager(context));
  }
}
