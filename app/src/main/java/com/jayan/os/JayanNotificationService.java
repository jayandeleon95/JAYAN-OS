package com.jayan.os;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.Notification;
import org.json.JSONArray;
import org.json.JSONObject;

public class JayanNotificationService extends NotificationListenerService {
    private static volatile JayanNotificationService instance;
    @Override public void onListenerConnected(){ instance=this; }
    @Override public void onListenerDisconnected(){ instance=null; }

    public static String snapshot(){
        JSONArray arr=new JSONArray();
        try{
            if(instance==null)return arr.toString();
            StatusBarNotification[] ns=instance.getActiveNotifications();
            if(ns==null)return arr.toString();
            for(StatusBarNotification sbn:ns){
                Notification n=sbn.getNotification();
                CharSequence title=n.extras.getCharSequence(Notification.EXTRA_TITLE);
                CharSequence text=n.extras.getCharSequence(Notification.EXTRA_TEXT);
                JSONObject o=new JSONObject();
                o.put("app",sbn.getPackageName());
                o.put("title",title==null?"":title.toString());
                o.put("text",text==null?"":text.toString());
                arr.put(o);
            }
        }catch(Exception ignored){}
        return arr.toString();
    }
}
