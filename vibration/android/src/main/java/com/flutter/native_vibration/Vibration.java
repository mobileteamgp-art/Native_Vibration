package com.flutter.native_vibration;

import android.os.Build;
import android.os.VibrationAttributes;
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
        if (!vibrator.hasVibrator()) return;

        // Use VibrationAttributes ONLY on Android 13+ (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            int finalAmplitude = vibrator.hasAmplitudeControl()
                    ? amplitude
                    : VibrationEffect.DEFAULT_AMPLITUDE;

            vibrator.vibrate(
                    VibrationEffect.createOneShot(duration, finalAmplitude),
                    new VibrationAttributes.Builder()
                            .setUsage(VibrationAttributes.USAGE_TOUCH)
                            .build()
            );
        }
        // Android 8 → Android 12L (API 26–32)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hapticEnabled = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.HAPTIC_FEEDBACK_ENABLED,
                    1
            ) == 1;

            if (!hapticEnabled) return;

            int finalAmplitude = vibrator.hasAmplitudeControl()
                    ? amplitude
                    : VibrationEffect.DEFAULT_AMPLITUDE;

            vibrator.vibrate(
                    VibrationEffect.createOneShot(duration, finalAmplitude),
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .build()
            );
        }
        // Below Android 8
        else {
            boolean hapticEnabled = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.HAPTIC_FEEDBACK_ENABLED,
                    1
            ) == 1;

            if (hapticEnabled) {
                vibrator.vibrate(duration);
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