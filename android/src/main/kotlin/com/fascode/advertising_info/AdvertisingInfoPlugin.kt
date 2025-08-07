package com.fascode.advertising_info

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.concurrent.thread

/** AdvertisingInfoPlugin */
class AdvertisingInfoPlugin : FlutterPlugin, MethodChannel.MethodCallHandler {

    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "advertising_info")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (call.method == "getAdvertisingInfo") {
            try {
                Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient")
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    result.error("-1", "Internal Error", "AdvertisingIdClient class not found")
                }
                return
            }

            thread {
                try {
                    val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                    Handler(Looper.getMainLooper()).post {
                        result.success(
                            mapOf(
                                "id" to adInfo.id,
                                "isLimitAdTrackingEnabled" to adInfo.isLimitAdTrackingEnabled
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Handler(Looper.getMainLooper()).post {
                        result.error(
                            "-1",
                            "Internal Error",
                            "Failed to fetch Advertising ID. Please try again later."
                        )
                    }
                }
            }
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
