package net.soluspay.cashq.utils;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CardsDataDao {
    @Query("SELECT * FROM cardsdata")
    List<CardsData> getAll();

    @Query("SELECT * FROM cardsdata WHERE _ID IN (:userIds)")
    List<CardsData> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM cardsdata WHERE pan LIKE :first AND " +
            "exp_date LIKE :last LIMIT 1")
    CardsData findByName(String first, String last);

    @Insert
    void insertAll(CardsData... cards);

    @Delete
    void delete(CardsData cards);
}


