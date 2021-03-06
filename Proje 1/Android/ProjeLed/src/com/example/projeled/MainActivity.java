package com.example.projeled;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class MainActivity extends ActionBarActivity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private PendingIntent mPermissionIntent;
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private boolean mPermissionRequestPending;
	private UsbManager mUsbManager;
	private UsbAccessory mAccessory;
	private ParcelFileDescriptor mFileDescriptor;
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;
	private static final byte COMMAND_LED = 0x2;
	private static final byte TARGET_PIN_2 = 0x2;
	private static final byte VALUE_ON = 0x1;
	private static final byte VALUE_OFF = 0x0;
	private ToggleButton ledToggleButton;
	         
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
		ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
		
		setContentView(R.layout.activity_main);
		ledToggleButton = (ToggleButton) findViewById(R.id.led_toggle_button);
		ledToggleButton.setOnCheckedChangeListener(toggleButtonCheckedListener);
		}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mInputStream != null && mOutputStream != null) {
		return;
		}
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
		if (mUsbManager.hasPermission(accessory)) {
		openAccessory(accessory);
		} else {
		synchronized (mUsbReceiver) {
		if (!mPermissionRequestPending) {
		mUsbManager.requestPermission(accessory, mPermissionIntent);
		mPermissionRequestPending = true;
		        }
		      }
		   }
		} 
		else 
		 {
		   Log.d(TAG, "mAccessory is null");
		   }
		 }
	
	/** Sistem tarafından Activity durduruldugunda çagrılır. */
	@Override
	public void onPause() {
	super.onPause();
	closeAccessory();
	  }
	/**
	* 
	* Activity bittiginde çagrılır.
	*/
	@Override
	public void onDestroy() {
	super.onDestroy();
	unregisterReceiver(mUsbReceiver);
	}
	
	OnCheckedChangeListener toggleButtonCheckedListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.led_toggle_button) {
		new AsyncTask<Boolean, Void, Void>() {
		@Override
		protected Void doInBackground(Boolean... params) {
			
			sendLedSwitchCommand(TARGET_PIN_2, params[0]);
			return null;
		}
		}.execute(isChecked);
		}
		}
	};
		
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (ACTION_USB_PERMISSION.equals(action)) {
		synchronized (this) {
		UsbAccessory accessory = UsbManager.getAccessory(intent);
		if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
		openAccessory(accessory);
		} else {
		Log.d(TAG, "Aksesuar'a izin verilmedi " + accessory);
		}
		mPermissionRequestPending = false;
		}
		} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
		UsbAccessory accessory = UsbManager.getAccessory(intent);
		if (accessory != null && accessory.equals(mAccessory)) {
		closeAccessory();
		}
		}
		}
		};
		
		private void openAccessory(UsbAccessory accessory) {
			mFileDescriptor = mUsbManager.openAccessory(accessory);
			if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Log.d(TAG, "Aksesuar açıldı");
			} else {
			Log.d(TAG, "Aksesuar açılamadı");
			}
			}
		
		private void closeAccessory() {
			try {
			if (mFileDescriptor != null) {
			mFileDescriptor.close();
			}
			} catch (IOException e) {
			} finally {
			mFileDescriptor = null;
			mAccessory = null;
			}
		}
		
		public void sendLedSwitchCommand(byte target, boolean isSwitchedOn) {
			byte[] buffer = new byte[3];
			buffer[0] = COMMAND_LED;
			buffer[1] = target;
			if (isSwitchedOn) {
			buffer[2] = VALUE_ON;
			} else {
			buffer[2] = VALUE_OFF;
			}
			if (mOutputStream != null) {
			try {
			mOutputStream.write(buffer);
			} catch (IOException e) {
			Log.e(TAG, "Yazma başarısız oldu", e);
			}
			}
			}
		
		
}					
	
		

	

	
