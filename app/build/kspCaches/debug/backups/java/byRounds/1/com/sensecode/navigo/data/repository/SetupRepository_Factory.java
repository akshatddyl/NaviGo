package com.sensecode.navigo.data.repository;

import com.sensecode.navigo.data.local.dao.EdgeDao;
import com.sensecode.navigo.data.local.dao.LocationNodeDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SetupRepository_Factory implements Factory<SetupRepository> {
  private final Provider<LocationNodeDao> nodeDaoProvider;

  private final Provider<EdgeDao> edgeDaoProvider;

  public SetupRepository_Factory(Provider<LocationNodeDao> nodeDaoProvider,
      Provider<EdgeDao> edgeDaoProvider) {
    this.nodeDaoProvider = nodeDaoProvider;
    this.edgeDaoProvider = edgeDaoProvider;
  }

  @Override
  public SetupRepository get() {
    return newInstance(nodeDaoProvider.get(), edgeDaoProvider.get());
  }

  public static SetupRepository_Factory create(Provider<LocationNodeDao> nodeDaoProvider,
      Provider<EdgeDao> edgeDaoProvider) {
    return new SetupRepository_Factory(nodeDaoProvider, edgeDaoProvider);
  }

  public static SetupRepository newInstance(LocationNodeDao nodeDao, EdgeDao edgeDao) {
    return new SetupRepository(nodeDao, edgeDao);
  }
}
