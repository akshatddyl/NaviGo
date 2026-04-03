package com.sensecode.navigo.audio;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class TtsManager_Factory implements Factory<TtsManager> {
  @Override
  public TtsManager get() {
    return newInstance();
  }

  public static TtsManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TtsManager newInstance() {
    return new TtsManager();
  }

  private static final class InstanceHolder {
    private static final TtsManager_Factory INSTANCE = new TtsManager_Factory();
  }
}
