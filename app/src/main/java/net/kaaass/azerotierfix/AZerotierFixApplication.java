package net.kaaass.azerotierfix;

import android.util.Log;

import androidx.multidex.MultiDexApplication;

import net.kaaass.azerotierfix.model.DaoMaster;
import net.kaaass.azerotierfix.model.DaoSession;
import net.kaaass.azerotierfix.model.ZTOpenHelper;

/**
 * 主程序入口
 *
 * @author kaaass
 */
public class AZerotierFixApplication extends MultiDexApplication {
    private DaoSession mDaoSession;

    public void onCreate() {
        super.onCreate();
        Log.i("Application", "Starting Application");
        // 创建 DAO 会话
        this.mDaoSession = new DaoMaster(
                new ZTOpenHelper(this, "ztfixdb", null)
                        .getWritableDatabase()
        ).newSession();
    }

    public DaoSession getDaoSession() {
        return this.mDaoSession;
    }
}
