package com.sensecode.navigo.data.remote.firebase;

import com.google.firebase.firestore.FirebaseFirestore;
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
public final class FirestoreVenueService_Factory implements Factory<FirestoreVenueService> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  public FirestoreVenueService_Factory(Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public FirestoreVenueService get() {
    return newInstance(firestoreProvider.get());
  }

  public static FirestoreVenueService_Factory create(
      Provider<FirebaseFirestore> firestoreProvider) {
    return new FirestoreVenueService_Factory(firestoreProvider);
  }

  public static FirestoreVenueService newInstance(FirebaseFirestore firestore) {
    return new FirestoreVenueService(firestore);
  }
}
