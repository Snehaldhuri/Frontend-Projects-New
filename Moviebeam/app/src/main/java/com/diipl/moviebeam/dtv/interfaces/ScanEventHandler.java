package com.diipl.moviebeam.dtv.interfaces;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ScanEventHandler extends IInterface {

    public static class Default implements ScanEventHandler {
        public IBinder asBinder() {
            return null;
        }

        public void onEvent(String str, String str2) throws RemoteException {
        }
    }

    void onEvent(String str, String str2) throws RemoteException;

    public static abstract class Stub extends Binder implements ScanEventHandler {
        private static final String DESCRIPTOR = "nes.scan.ScanEventHandler";
        static final int TRANSACTION_onEvent = 1;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ScanEventHandler asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ScanEventHandler)) {
                return new Proxy(iBinder);
            }
            return (ScanEventHandler) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                onEvent(parcel.readString(), parcel.readString());
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements ScanEventHandler {
            public static ScanEventHandler sDefaultImpl;
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onEvent(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().onEvent(str, str2);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ScanEventHandler scanEventHandler) {
            if (Proxy.sDefaultImpl != null || scanEventHandler == null) {
                return false;
            }
            Proxy.sDefaultImpl = scanEventHandler;
            return true;
        }

        public static ScanEventHandler getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
