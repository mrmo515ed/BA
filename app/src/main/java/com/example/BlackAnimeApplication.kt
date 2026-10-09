package com.example

import android.app.Application
import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.google.firebase.FirebaseApp
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BlackAnimeApplication : Application(), ImageLoaderFactory {

    companion object {
        private const val TAG = "BlackAnimeApp"
        private const val CRASH_FILE_NAME = "black_anime_crash_log.txt"

        fun getLastCrashReport(context: Context): String? {
            return try {
                val file = File(context.filesDir, CRASH_FILE_NAME)
                if (file.exists() && file.length() > 0) {
                    file.readText()
                } else null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read crash report: ${e.message}")
                null
            }
        }

        fun clearCrashReport(context: Context) {
            try {
                val file = File(context.filesDir, CRASH_FILE_NAME)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete crash report: ${e.message}")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 1. Install Global UncaughtExceptionHandler to capture and log any startup crashes to Logcat
        setupUncaughtExceptionHandler()

        // 2. Safely ensure FirebaseApp is initialized before any components start
        initializeFirebaseSafely()
    }

    private fun setupUncaughtExceptionHandler() {
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
            val stringWriter = StringWriter()
            throwable.printStackTrace(PrintWriter(stringWriter))
            val stackTraceString = stringWriter.toString()

            val crashReport = buildString {
                appendLine("================ FATAL EXCEPTION DETECTED ================")
                appendLine("Timestamp: $timestamp")
                appendLine("Thread: ${thread.name} (id: ${thread.id})")
                appendLine("Exception: ${throwable.javaClass.name}")
                appendLine("Message: ${throwable.message}")
                appendLine("Stack Trace:")
                appendLine(stackTraceString)
                appendLine("==========================================================")
            }

            // Log extensively to Logcat with dedicated high-visibility tags
            Log.e(TAG, crashReport)
            Log.e("BlackAnimeCrash", "FATAL CRASH in thread ${thread.name}: ${throwable.message}", throwable)

            // Save crash report to internal storage so it can be retrieved for diagnostics
            try {
                val crashFile = File(filesDir, CRASH_FILE_NAME)
                crashFile.writeText(crashReport)
                Log.i(TAG, "Crash report successfully written to: ${crashFile.absolutePath}")
            } catch (ioe: Exception) {
                Log.e(TAG, "Failed to persist crash report: ${ioe.message}", ioe)
            }

            // Delegate to system/default handler to let Android platform handle process death cleanly
            originalHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun initializeFirebaseSafely() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val app = FirebaseApp.initializeApp(this)
                Log.i(TAG, "Firebase initialized successfully: ${app?.name ?: "default"}")
            } else {
                Log.i(TAG, "Firebase already initialized. Active apps: ${FirebaseApp.getApps(this).size}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase in Application.onCreate: ${e.message}", e)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(150L * 1024 * 1024) // 150 MB disk cache for offline & weak internet
                    .build()
            }
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false) // Ensures images stay cached even on bad connections
            .crossfade(true)
            .crossfade(300)
            .build()
    }
}
