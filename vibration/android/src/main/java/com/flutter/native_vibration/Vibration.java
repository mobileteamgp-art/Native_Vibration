package com.flutter.native_vibration;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.media.AudioAttributes;
import android.provider.Settings;
import android.content.Context;
import java.util.List;

public class Vibration {
    private final Vibrator vibrator;
    private final Context context;

    Vibration(Vibrator vibrator,Context context) {

        this.vibrator = vibrator;
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    void vibrate(long duration, int amplitude) {
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12 and above (API 31+)
                if (vibrator.hasAmplitudeControl()) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude), new android.os.VibrationAttributes.Builder()
                            .setUsage(android.os.VibrationAttributes.USAGE_TOUCH)
                            .build());
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE), new android.os.VibrationAttributes.Builder()
                            .setUsage(android.os.VibrationAttributes.USAGE_TOUCH)
                            .build());
                }
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                // Respect system haptic feedback setting for Android 8 → 11
                boolean hapticEnabled = Settings.System.getInt(
                        context.getContentResolver(),
                        Settings.System.HAPTIC_FEEDBACK_ENABLED,
                        1
                ) == 1;
                if(hapticEnabled) {
                    if (vibrator.hasAmplitudeControl()) {
                        vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude), new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build());
                    } else {
                        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE), new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build());
                    }
                }
            } else {
                // Respect system haptic feedback setting for below Android 8
                boolean hapticEnabled = Settings.System.getInt(
                        context.getContentResolver(),
                        Settings.System.HAPTIC_FEEDBACK_ENABLED,
                        1
                ) == 1;
                if(hapticEnabled) {
                    vibrator.vibrate(duration);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    void vibrate(List<Integer> pattern, int repeat) {
        long[] patternLong = new long[pattern.size()];

        for (int i = 0; i < patternLong.length; i++) {
            patternLong[i] = pattern.get(i);
        }

        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(patternLong, repeat), new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build());
            } else {
                vibrator.vibrate(patternLong, repeat);
            }
        }
    }

    @SuppressWarnings("deprecation")
    void vibrate(List<Integer> pattern, int repeat, List<Integer> intensities) {
        long[] patternLong = new long[pattern.size()];
        int[] intensitiesArray = new int[intensities.size()];

        for (int i = 0; i < patternLong.length; i++) {
            patternLong[i] = pattern.get(i);
        }

        for (int i = 0; i < intensitiesArray.length; i++) {
            intensitiesArray[i] = intensities.get(i);
        }

        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (vibrator.hasAmplitudeControl()) {
                    vibrator.vibrate(VibrationEffect.createWaveform(patternLong, intensitiesArray, repeat), new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
                } else {
                    vibrator.vibrate(VibrationEffect.createWaveform(patternLong, repeat), new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
                }
            } else {
                vibrator.vibrate(patternLong, repeat);
            }
        }
    }

    Vibrator getVibrator() {
        return this.vibrator;
    }
}