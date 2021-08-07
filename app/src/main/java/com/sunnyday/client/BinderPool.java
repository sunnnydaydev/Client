package com.sunnyday.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.sunnyday.binderpool.IBinderPool;
import java.util.concurrent.CountDownLatch;

/**
 * Create by SunnyDay on 11:56 2021/08/07
 */
class BinderPool {

    private static final String TAG = "BinderPool";
    private static volatile BinderPool INSTANCE = null;

    public static final int BINDER_NONE = -1;
    public static final int BINDER_MUSIC_MANAGER = 0;
    public static final int BINDER_AUDIO_MANAGER = 1;

    private final Context mContext;
    private CountDownLatch mCountDownLatch;
    private IBinderPool mIBinderPool;


    private BinderPool(Context context) {
        //use application context avoid memory leak.
        this.mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BinderPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BinderPool(context);
                }
            }
        }
        return INSTANCE;
    }

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        //死亡回调
        @Override
        public void binderDied() {
            if (mIBinderPool!=null){
                mIBinderPool.asBinder().unlinkToDeath(mDeathRecipient,0);
                mIBinderPool=null;
                connectBinderPoolService();
            }
        }
    };

    private final ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"onServiceConnected");
            mIBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                //设置死亡代理
                mIBinderPool.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected");
            mIBinderPool = null;
        }
    };



    public IBinder getBinder(int bindType) {
        Log.i(TAG, "getBinder");
        IBinder binder = null;

        try {
            if (mIBinderPool != null) {
                binder = mIBinderPool.getBinderByType(bindType);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private void connectBinderPoolService() {
        Log.i(TAG, "connectBinderPoolService");
        mCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent();
        intent.setAction("com.sunnyday.binderpool.BinderPoolService");
        intent.setPackage("com.sunnyday.binderpool");
        mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        try {
            mCountDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
