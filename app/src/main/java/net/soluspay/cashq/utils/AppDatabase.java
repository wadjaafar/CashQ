package net.soluspay.cashq.utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CardsData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CardsDataDao userDao();
}
