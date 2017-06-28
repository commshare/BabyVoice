package com.lihb.babyvoice.upgrade;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.allenliu.versionchecklib.AVersionService;
import com.lihb.babyvoice.R;

public class UpgradeService extends AVersionService {
    private int mFrom;
    public UpgradeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        mFrom = intent.getIntExtra(UpgradeUtil.FROM, UpgradeUtil.FROM_MAIN_ACTIVITY);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {
        Log.e("UpgradeService", response);
        service.showVersionDialog("http://www.apk3.com/uploads/soft/guiguangbao/UCllq.apk", getString(R.string.checked_new_version), getString(R.string.updatecontent),true);
//        UpdateInfo info = GsonHelper.jsonToObject(response, UpdateInfo.class);
//        if(Float.parseFloat(info.version) > Float.parseFloat(UpgradeUtil.getVersionName(service))){
//            service.showVersionDialog(info.downloadUrl,getString(R.string.checked_new_version), info.description);
//        }else {
//            if (mFrom == UpgradeUtil.FROM_ME_FRAGMENT) {
//                CommonToast.showShortToast(R.string.already_newest_version);
//            }
//        }
    }
}
