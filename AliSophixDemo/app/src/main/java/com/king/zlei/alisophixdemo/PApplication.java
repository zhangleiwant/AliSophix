package com.king.zlei.alisophixdemo;

import android.app.Application;
import android.content.pm.PackageManager;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * <b>Create Date:</b> 2017/8/10<br>
 * <b>Author:</b> Zhanglei<br>
 * <b>Description:</b> <br>
 */

public class PApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSophia();
    }

    private void initSophia() {
        String  version = "";

        try {
            version = this.getPackageManager().getPackageInfo(this.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SophixManager.getInstance().setContext(this)
                .setAppVersion(version)
                .setAesKey(null)
                .setEnableDebug(false)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀，以此加快应用补丁
                            // 建议调用killProcessSafely，详见1.3.2.3
                            // SophixManager.getInstance().killProcessSafely();
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            // SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
// queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();
    }
}
