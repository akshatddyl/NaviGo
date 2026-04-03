package com.sensecode.navigo.di;

import com.sensecode.navigo.data.local.NaviGoDatabase;
import com.sensecode.navigo.data.local.dao.LocationNodeDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DatabaseModule_ProvideNodeDaoFactory implements Factory<LocationNodeDao> {
  private final Provider<NaviGoDatabase> dbProvider;

  public DatabaseModule_ProvideNodeDaoFactory(Provider<NaviGoDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public LocationNodeDao get() {
    return provideNodeDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideNodeDaoFactory create(Provider<NaviGoDatabase> dbProvider) {
    return new DatabaseModule_ProvideNodeDaoFactory(dbProvider);
  }

  public static LocationNodeDao provideNodeDao(NaviGoDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideNodeDao(db));
  }
}
