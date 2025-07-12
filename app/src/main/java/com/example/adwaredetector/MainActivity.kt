package com.example.adwaredetector

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var malwareDetector: MalwareDetector
    private lateinit var resultTextView: TextView
    private lateinit var scanButton: Button

    private val TAG = "ADWARE_TEST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.result_text)
        scanButton = findViewById(R.id.scan_button)

        try {
            malwareDetector = MalwareDetector(this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize model", e)
            resultTextView.text = "❌ Model failed to load.\n${e.localizedMessage}"
            scanButton.isEnabled = false
            return
        }

        scanButton.setOnClickListener {
            scanButton.isEnabled = false
            resultTextView.text = "Scanning installed apps..."
            CoroutineScope(Dispatchers.Main).launch {
                val result = withContext(Dispatchers.Default) {
                    scanAndPredictAllApps()
                }
                resultTextView.text = result
                scanButton.isEnabled = true
            }
        }
    }

    private fun scanAndPredictAllApps(): String {
        val pm = packageManager
        val installedApps = pm.getInstalledApplications(0)

        val suspiciousApps = mutableListOf<String>()

        for (appInfo in installedApps) {
            // Skip system apps and your own app
            if (isSystemApp(appInfo) || appInfo.packageName == packageName) {
                continue
            }

            val features = extractFeatures(pm, appInfo.packageName)

            Log.d(TAG, "Extracted features for ${appInfo.packageName}: ${features.joinToString(",")}")

            val score = try {
                malwareDetector.predict(features)
            } catch (e: Exception) {
                Log.e(TAG, "Prediction failed for ${appInfo.packageName}", e)
                0f
            }

            Log.i(TAG, "Prediction score for ${appInfo.packageName}: $score")

            if (score > 0.5f) {
                suspiciousApps.add("${appInfo.packageName} (Score: %.2f)".format(score))
            }
        }

        return if (suspiciousApps.isEmpty()) {
            "✅ No adware detected among ${installedApps.size} scanned apps."
        } else {
            "⚠️ Adware Detected in ${suspiciousApps.size} apps:\n" + suspiciousApps.joinToString("\n")
        }
    }

    private fun isSystemApp(appInfo: ApplicationInfo): Boolean {
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }

    private fun extractFeatures(pm: PackageManager, packageName: String): FloatArray {
        val features = FloatArray(8) { 0f }
        try {
            val packageInfo = pm.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS or
                        PackageManager.GET_ACTIVITIES or
                        PackageManager.GET_SERVICES
            )

            // 1. Total permissions requested
            val permissions = packageInfo.requestedPermissions ?: emptyArray()
            features[0] = permissions.size.toFloat()

            // 2. Dangerous permissions count
            val dangerousPermissionsCount = permissions.count { permission ->
                try {
                    val permInfo = pm.getPermissionInfo(permission, 0)
                    (permInfo.protectionLevel and android.content.pm.PermissionInfo.PROTECTION_DANGEROUS) != 0
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }
            }
            features[1] = dangerousPermissionsCount.toFloat()

            // 3. Has INTERNET permission
            features[2] = if (permissions.contains(android.Manifest.permission.INTERNET)) 1f else 0f

            // 4. Has SMS permission (SEND_SMS or RECEIVE_SMS)
            val smsPermissions = listOf(
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS
            )
            features[3] = if (permissions.any { smsPermissions.contains(it) }) 1f else 0f

            // 5. Number of activities
            features[4] = packageInfo.activities?.size?.toFloat() ?: 0f

            // 6. Number of services
            features[5] = packageInfo.services?.size?.toFloat() ?: 0f

            // 7. Is system app (1 if system app, else 0)
            val appInfo = packageInfo.applicationInfo
            features[6] = if ((appInfo?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM != 0) 1f else 0f

            // 8. APK size in MB (approximate)
            val apkFile = appInfo?.sourceDir?.let { java.io.File(it) }
            features[7] = if (apkFile != null) apkFile.length().toFloat() / (1024 * 1024) else 0f

            Log.d(TAG, "Features extracted for $packageName: ${features.joinToString(",")}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract features for $packageName", e)
        }
        return features
    }
}
