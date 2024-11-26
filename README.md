failed
Download info
:app:mergeDebugResources
:app:checkDebugAarMetadata
:app:mergeExtDexDebug
org.gradle.workers.internal.DefaultWorkerExecutor$WorkExecutionException: A failure occurred while executing com.android.build.gradle.internal.tasks.CheckAarMetadataWorkAction
java.lang.RuntimeException: 2 issues were found when checking AAR metadata:
org.gradle.workers.internal.DefaultWorkerExecutor$WorkExecutionException: A failure occurred while executing com.android.build.gradle.internal.res.ResourceCompilerRunnable
com.android.aaptcompiler.ResourceCompilationException: Resource compilation failed (Failed to compile values resource file C:\Users\Protech\AndroidStudioProjects\QRVideoRecorder\app\build\intermediates\incremental\debug\mergeDebugResources\merged.dir\values\values.xml. Cause: java.lang.IllegalStateException: Can not extract resource from com.android.aaptcompiler.ParsedResource@5403a00f.). Check logs for more details.
com.android.aaptcompiler.ResourceCompilationException: Failed to compile values resource file C:\Users\Protech\AndroidStudioProjects\QRVideoRecorder\app\build\intermediates\incremental\debug\mergeDebugResources\merged.dir\values\values.xml
java.lang.IllegalStateException: Can not extract resource from com.android.aaptcompiler.ParsedResource@5403a00f.
Aar Dependency compatibility issues
