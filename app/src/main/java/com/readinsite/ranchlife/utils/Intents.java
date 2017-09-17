package com.readinsite.ranchlife.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * @author mbelsky 07.04.16
 */
public class Intents {
    public static final String ACTION_SHARE_REQUEST_PERMISSIONS_RESULT = "com.mbelsky.ibis.android.utils.Intents.SHARE_REQUEST_PERMISSIONS_RESULT";
    public static final String EXTRA_RPR_CODE = "com.mbelsky.ibis.android.utils.Intents.RPR_CODE";
    public static final String EXTRA_RPR_PERMISSIONS = "com.mbelsky.ibis.android.utils.Intents.RPR_PERMISSIONS";
    public static final String EXTRA_RPR_RESULTS = "com.mbelsky.ibis.android.utils.Intents.RPR_RESULTS";

    //MARK: Properties to handle firebase message notifications
    public static final String FMN_ACTION_NEW_ASSIGNMENT = "new_assignment";
    public static final String FMN_ACTION_NEW_TASK_NOTE = "new_task_note";
    public static final String EXTRA_FMN_ACTION_TITLE = "com.mbelsky.ibis.android.utils.Intents.FMN_ACTION_TITLE";
    public static final String EXTRA_FMN_SITE_ID = "com.mbelsky.ibis.android.utils.Intents.FMN_SITE_ID";
    public static final String EXTRA_FMN_TASK_ID = "com.mbelsky.ibis.android.utils.Intents.FMN_TASK_ID";

    public static Intent buildShareRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Intent intent = new Intent(ACTION_SHARE_REQUEST_PERMISSIONS_RESULT);
        intent.putExtra(EXTRA_RPR_CODE, requestCode);
        intent.putExtra(EXTRA_RPR_PERMISSIONS, permissions);
        intent.putExtra(EXTRA_RPR_RESULTS, grantResults);
        return intent;
    }

    public static Intent buildNotificationNewAssignmentIntent(Context context, Class<? extends Context> clazz, long siteId, long taskId) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(EXTRA_FMN_ACTION_TITLE, FMN_ACTION_NEW_ASSIGNMENT);

        Bundle extras = new Bundle(3);
        extras.putLong(EXTRA_FMN_SITE_ID, siteId);
        extras.putLong(EXTRA_FMN_TASK_ID,taskId);
        extras.putString(EXTRA_FMN_ACTION_TITLE, FMN_ACTION_NEW_ASSIGNMENT);
        intent.putExtras(extras);
        return intent;
    }
}
