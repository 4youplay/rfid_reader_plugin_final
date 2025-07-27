package com.example.rfid_reader_plugin;

import android.content.Context;
import androidx.annotation.NonNull;

import com.rguhf.ILcUhfProduct;
import com.rguhf.InventoryTagMap;
import com.rguhf.LcModule;
import com.rguhf.PowerUtil;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class RfidReaderPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {
  private MethodChannel channel;
  private Context context;
  private ILcUhfProduct reader;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "rfid_reader_plugin");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
    switch (call.method) {
      case "init":
        result.success(initReader());
        break;
      case "startReading":
        if (reader != null) {
          reader.SetCallBack(tag -> {
            // Tags recebidas via callback (não usadas diretamente aqui)
          });
          reader.StartRead();
          result.success(true);
        } else {
          result.error("READER_NULL", "Reader is not initialized", null);
        }
        break;
      case "stopReading":
        if (reader != null) {
          reader.StopRead();
          result.success(true);
        } else {
          result.error("READER_NULL", "Reader is not initialized", null);
        }
        break;
      case "getTags":
        if (reader != null) {
          List<InventoryTagMap> list = reader.getInventoryTagMapList();
          List<String> epcs = new ArrayList<>();
          for (InventoryTagMap tag : list) {
            epcs.add(tag.EPC);
          }
          result.success(epcs);
        } else {
          result.success(new ArrayList<>());
        }
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private boolean initReader() {
    try {
      PowerUtil.power("1"); // Power on
      new LcModule(context).Connect("/dev/ttyS3", 115200);
      reader = new LcModule(context).createProduct();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}

