package com.sensecode.navigo.di;

import com.sensecode.navigo.data.local.NaviGoDatabase;
import com.sensecode.navigo.data.local.dao.EdgeDao;
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
public final class DatabaseModule_ProvideEdgeDaoFactory implements Factory<EdgeDao> {
  private final Provider<NaviGoDatabase> dbProvider;

  public DatabaseModule_ProvideEdgeDaoFactory(Provider<NaviGoDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public EdgeDao get() {
    return provideEdgeDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideEdgeDaoFactory create(Provider<NaviGoDatabase> dbProvider) {
    return new DatabaseModule_ProvideEdgeDaoFactory(dbProvider);
  }

  public static EdgeDao provideEdgeDao(NaviGoDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideEdgeDao(db));
  }
}
