package net.soluspay.cashq.api;


import net.soluspay.cashq.model.Transaction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Mohamed Jaafar on 16/02/19.
 */

public interface TransactionClient {

    @POST("workingKey")
    Call<Transaction> workingKey(@Body Transaction transaction);

    @POST("purchase")
    Call<Transaction> purchase(@Body Transaction transaction);

    @POST("changePin")
    Call<Transaction> changePin(@Body Transaction transaction);

    @POST("cardTransfer")
    Call<Transaction> cardTransfer(@Body Transaction transaction);

}
