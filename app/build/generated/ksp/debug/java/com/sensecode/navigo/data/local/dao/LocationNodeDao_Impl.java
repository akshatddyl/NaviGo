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
import com.sensecode.navigo.data.local.entity.LocationNodeEntity;
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
public final class LocationNodeDao_Impl implements LocationNodeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LocationNodeEntity> __insertionAdapterOfLocationNodeEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteNodesByVenue;

  public LocationNodeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLocationNodeEntity = new EntityInsertionAdapter<LocationNodeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `location_nodes` (`id`,`name`,`floor`,`venueId`,`accessible`,`type`,`relativeX`,`relativeY`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LocationNodeEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getFloor());
        statement.bindString(4, entity.getVenueId());
        final int _tmp = entity.getAccessible() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindString(6, entity.getType());
        statement.bindDouble(7, entity.getRelativeX());
        statement.bindDouble(8, entity.getRelativeY());
      }
    };
    this.__preparedStmtOfDeleteNodesByVenue = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM location_nodes WHERE venueId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertNode(final LocationNodeEntity node,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLocationNodeEntity.insert(node);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllNodes(final List<LocationNodeEntity> nodes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLocationNodeEntity.insert(nodes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteNodesByVenue(final String venueId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteNodesByVenue.acquire();
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
          __preparedStmtOfDeleteNodesByVenue.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getNodesByVenue(final String venueId,
      final Continuation<? super List<LocationNodeEntity>> $completion) {
    final String _sql = "SELECT * FROM location_nodes WHERE venueId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, venueId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LocationNodeEntity>>() {
      @Override
      @NonNull
      public List<LocationNodeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfAccessible = CursorUtil.getColumnIndexOrThrow(_cursor, "accessible");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfRelativeX = CursorUtil.getColumnIndexOrThrow(_cursor, "relativeX");
          final int _cursorIndexOfRelativeY = CursorUtil.getColumnIndexOrThrow(_cursor, "relativeY");
          final List<LocationNodeEntity> _result = new ArrayList<LocationNodeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationNodeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpFloor;
            _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final boolean _tmpAccessible;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAccessible);
            _tmpAccessible = _tmp != 0;
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final float _tmpRelativeX;
            _tmpRelativeX = _cursor.getFloat(_cursorIndexOfRelativeX);
            final float _tmpRelativeY;
            _tmpRelativeY = _cursor.getFloat(_cursorIndexOfRelativeY);
            _item = new LocationNodeEntity(_tmpId,_tmpName,_tmpFloor,_tmpVenueId,_tmpAccessible,_tmpType,_tmpRelativeX,_tmpRelativeY);
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

  @Override
  public Object getNodesByFloor(final String venueId, final int floor,
      final Continuation<? super List<LocationNodeEntity>> $completion) {
    final String _sql = "SELECT * FROM location_nodes WHERE venueId = ? AND floor = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, venueId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, floor);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LocationNodeEntity>>() {
      @Override
      @NonNull
      public List<LocationNodeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfAccessible = CursorUtil.getColumnIndexOrThrow(_cursor, "accessible");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfRelativeX = CursorUtil.getColumnIndexOrThrow(_cursor, "relativeX");
          final int _cursorIndexOfRelativeY = CursorUtil.getColumnIndexOrThrow(_cursor, "relativeY");
          final List<LocationNodeEntity> _result = new ArrayList<LocationNodeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationNodeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpFloor;
            _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final boolean _tmpAccessible;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAccessible);
            _tmpAccessible = _tmp != 0;
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final float _tmpRelativeX;
            _tmpRelativeX = _cursor.getFloat(_cursorIndexOfRelativeX);
            final float _tmpRelativeY;
            _tmpRelativeY = _cursor.getFloat(_cursorIndexOfRelativeY);
            _item = new LocationNodeEntity(_tmpId,_tmpName,_tmpFloor,_tmpVenueId,_tmpAccessible,_tmpType,_tmpRelativeX,_tmpRelativeY);
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

  @Override
  public Object getNodeById(final String id,
      final Continuation<? super LocationNodeEntity> $completion) {
    final String _sql = "SELECT * FROM location_nodes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LocationNodeEntity>() {
      @Override
      @Nullable
      public LocationNodeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfAccessible = CursorUtil.getColumnIndexOrThrow(_cursor, "accessible");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfRelativeX = CursorUtil.getColumnIndexOrThrow(_cursor, "relativeX");
          final int _cursorIndexOfRelativeY = CursorUtil.getColumnIndexOrThrow(_cursor, "relativeY");
          final LocationNodeEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpFloor;
            _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final boolean _tmpAccessible;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAccessible);
            _tmpAccessible = _tmp != 0;
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final float _tmpRelativeX;
            _tmpRelativeX = _cursor.getFloat(_cursorIndexOfRelativeX);
            final float _tmpRelativeY;
            _tmpRelativeY = _cursor.getFloat(_cursorIndexOfRelativeY);
            _result = new LocationNodeEntity(_tmpId,_tmpName,_tmpFloor,_tmpVenueId,_tmpAccessible,_tmpType,_tmpRelativeX,_tmpRelativeY);
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
  public Object searchNodesByName(final String venueId, final String query,
      final Continuation<? super List<LocationNodeEntity>> $completion) {
    final String _sql = "SELECT * FROM location_nodes WHERE venueId = ? AND name LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, venueId);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LocationNodeEntity>>() {
      @Override
      @NonNull
      public List<LocationNodeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
          final int _cursorIndexOfVenueId = CursorUtil.getColumnIndexOrThrow(_cursor, "venueId");
          final int _cursorIndexOfAccessible = CursorUtil.getColumnIndexOrThrow(_cursor, "accessible");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfRelativeX = CursorUtil.getColumnIndexOrThrow(_cursor, "relativeX");
          final int _cursorIndexOfRelativeY = CursorUtil.getColumnIndexOrThrow(_cursor, "relativeY");
          final List<LocationNodeEntity> _result = new ArrayList<LocationNodeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationNodeEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpFloor;
            _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
            final String _tmpVenueId;
            _tmpVenueId = _cursor.getString(_cursorIndexOfVenueId);
            final boolean _tmpAccessible;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAccessible);
            _tmpAccessible = _tmp != 0;
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final float _tmpRelativeX;
            _tmpRelativeX = _cursor.getFloat(_cursorIndexOfRelativeX);
            final float _tmpRelativeY;
            _tmpRelativeY = _cursor.getFloat(_cursorIndexOfRelativeY);
            _item = new LocationNodeEntity(_tmpId,_tmpName,_tmpFloor,_tmpVenueId,_tmpAccessible,_tmpType,_tmpRelativeX,_tmpRelativeY);
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

  @Override
  public Object getDistinctVenueIds(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT venueId FROM location_nodes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
