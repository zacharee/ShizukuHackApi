package rikka.shizuku;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;
import java.util.Objects;

/**
 * Binder wrapper to use ShizukuService#transactRemote more conveniently.
 * <p>
 * example:
 * <br><code>IPackageManager pm = IPackageManager.Stub.asInterface(new ShizukuBinder(SystemServiceHelper.getSystemService("package")));
 * <br>pm.getInstalledPackages(0, 0);</code>
 */
public class ShizukuBinderWrapper implements IBinder {

    private final IBinder original;

    public ShizukuBinderWrapper(@NonNull IBinder original) {
        this.original = Objects.requireNonNull(original);
    }

    @Override
    public boolean transact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        boolean atLeast13 = !Shizuku.isPreV11() && Shizuku.getVersion() >= 13;

        Parcel newData = Parcel.obtain();
        try {
            newData.writeInterfaceToken(ShizukuApiConstants.BINDER_DESCRIPTOR);
            newData.writeStrongBinder(original);
            newData.writeInt(code);
            if (atLeast13) {
                newData.writeInt(flags);
            }
            newData.appendFrom(data, 0, data.dataSize());
            if (atLeast13) {
                Shizuku.transactRemote(newData, reply, 0);
            } else {
                Shizuku.transactRemote(newData, reply, flags);
            }
        } finally {
            newData.recycle();
        }
        return true;
    }

    @Nullable
    @Override
    public String getInterfaceDescriptor() throws RemoteException {
        return original.getInterfaceDescriptor();
    }

    @Override
    public boolean pingBinder() {
        return original.pingBinder();
    }

    @Override
    public boolean isBinderAlive() {
        return original.isBinderAlive();
    }

    @Nullable
    @Override
    public IInterface queryLocalInterface(@NonNull String descriptor) {
        return null;
    }

    @Override
    public void dump(@NonNull FileDescriptor fd, @Nullable String[] args) throws RemoteException {
        original.dump(fd, args);
    }

    @Override
    public void dumpAsync(@NonNull FileDescriptor fd, @Nullable String[] args) throws RemoteException {
        original.dumpAsync(fd, args);
    }

    @Override
    public void linkToDeath(@NonNull DeathRecipient recipient, int flags) throws RemoteException {
        original.linkToDeath(recipient, flags);
    }

    @Override
    public boolean unlinkToDeath(@NonNull DeathRecipient recipient, int flags) {
        return original.unlinkToDeath(recipient, flags);
    }

    @Override
    public void shellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
        original.shellCommand(in, out, err, args, shellCallback, resultReceiver);
    }
}
