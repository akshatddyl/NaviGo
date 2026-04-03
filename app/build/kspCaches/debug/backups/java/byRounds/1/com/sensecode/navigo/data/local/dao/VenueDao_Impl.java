package com.sensecode.navigo.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sensecode.navigo.data.local.entity.VenueEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VenueDao_Impl implements VenueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VenueEntity> __insertionAdapterOfVenueEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteVenue;

  public VenueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVenueEntity = new EntityInsertionAdapter<VenueEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `venues` (`venueId`,`name`,`address`,`orgName`,`floors`,`nodeCount`,`publisherId`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VenueEntity entity) {
        statement.bindString(1, entity.getVenueId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getAddress());
        statement.bindString(4, entity.getOrgName());
        statement.bindLong(5, entity.getFloors());
        statement.bindLong(6, entity.getNodeCount());
        statement.bindString(7, entity.getPublisherId());
      }
    };
    this.__preparedStmtOfDeleteVenue = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM venues WHERE venueId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertVenue(final VenueEntity venue, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVenueEntity.insert(venue);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteVenue(final String venueId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteVenue.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, venueId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteVenue.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getVenueById(final String venueId,
      final Continuation<? super VenueEntity> $completion) {
    final String _sql = "SELECT * FROM venues WHERE venueId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, venueId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VenueEntity>() {
      @Override
      @Nullable
      public VenueEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfOrgName = CursorUtil.getColumnIndexOrThrow(_cursor, "orgName");
          final int _cursorIndexOfFloors = CursorUtil.getColumnIndexOrThrow(_cursor, "floors");
          final int _cursorIndexOfNodeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeCount");
          final int _cursorIndexOfPublisherId = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherId");
          final VenueEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpOrgName;
            _tmpOrgName = _cursor.getString(_cursorIndexOfOrgName);
            final int _tmpFloors;
            _tmpFloors = _cursor.getInt(_cursorIndexOfFloors);
            final int _tmpNodeCount;
            _tmpNodeCount = _cursor.getInt(_cursorIndexOfNodeCount);
            final String _tmpPublisherId;
            _tmpPublisherId = _cursor.getString(_cursorIndexOfPublisherId);
            _result = new VenueEntity(_tmpVenueId,_tmpName,_tmpAddress,_tmpOrgName,_tmpFloors,_tmpNodeCount,_tmpPublisherId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllVenues(final Continuation<? super List<VenueEntity>> $completion) {
    final String _sql = "SELECT * FROM venues";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VenueEntity>>() {
      @Override
      @NonNull
      public List<VenueEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfOrgName = CursorUtil.getColumnIndexOrThrow(_cursor, "orgName");
          final int _cursorIndexOfFloors = CursorUtil.getColumnIndexOrThrow(_cursor, "floors");
          final int _cursorIndexOfNodeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "nodeCount");
          final int _cursorIndexOfPublisherId = CursorUtil.getColumnIndexOrThrow(_cursor, "publisherId");
          final List<VenueEntity> _result = new ArrayList<VenueEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VenueEntity _item;
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpOrgName;
            _tmpOrgName = _cursor.getString(_cursorIndexOfOrgName);
            final int _tmpFloors;
            _tmpFloors = _cursor.getInt(_cursorIndexOfFloors);
            final int _tmpNodeCount;
            _tmpNodeCount = _cursor.getInt(_cursorIndexOfNodeCount);
            final String _tmpPublisherId;
            _tmpPublisherId = _cursor.getString(_cursorIndexOfPublisherId);
            _item = new VenueEntity(_tmpVenueId,_tmpName,_tmpAddress,_tmpOrgName,_tmpFloors,_tmpNodeCount,_tmpPublisherId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
