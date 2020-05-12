package com.hs.face.impl;

import android.util.Log;

import androidx.annotation.Nullable;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;

public class StreamHandlerImpl implements EventChannel.StreamHandler {
    private static final String TAG = "EventSinkMessenger";

    @Nullable
    private EventChannel.EventSink eventSink;

    public StreamHandlerImpl(BinaryMessenger messenger, String eventChannelId) {

        Log.i(TAG, "设置监听通道："+eventChannelId);
        EventChannel eventChannel = new EventChannel(messenger, eventChannelId);
        eventChannel.setStreamHandler(this);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
    }

    /**
     * event发送数据
     * @param data
     */
    public void eventSinkSuccess(Object data) {
        if (eventSink != null) {
            eventSink.success(data);
        } else {
            Log.e(TAG, "===== FlutterEventChannel.eventSink 为空 需要检查一下 =====");
        }
    }

    public void eventSinkError(String errCode, String errMsg) {
        if (eventSink != null) {
            eventSink.error(errCode, errMsg, null);
        } else {
            Log.e(TAG, "===== FlutterEventChannel.eventSink 为空 需要检查一下 =====");
        }
    }
}
