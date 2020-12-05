package net.soluspay.cashq;

import android.content.Intent;
import android.os.IBinder;
import android.service.quicksettings.TileService;

public class MyTileService extends TileService {


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();

        Intent qr = new Intent(this.getApplicationContext(), QRPayActivity.class);
        qr.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(qr);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
