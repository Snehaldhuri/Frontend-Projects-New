# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.diipl.moviebeam.data.dto.** {*;}
-keep class com.diipl.moviebeam.data.kaping.** {*;}

-keepnames class com.google.common.** { *; }

#-dontwarn okio.**
#-dontwarn okhttp3.**
#-dontwarn retrofit2.**
#
#
### keep Enum in Response Objects
#-keepclassmembers enum com.android.services.** { *; }
#
#
### Note not be needed unless some model classes don't implement Serializable interface
### Keep model classes used by ORMlite
#-keep class com.android.model.**
#
#
### keep classes and class members that implement java.io.Serializable from being removed or renamed
### Fixes "Class class com.twinpeek.android.model.User does not have an id field" execption
#-keep class * implements java.io.Serializable {
#    *;
#}
#
### Rules for Retrofit2
#-keepclasseswithmembers class * {
#    @retrofit2.http.* <methods>;
#}
## Platform calls Class.forName on types which do not exist on Android to determine platform.
#-dontnote retrofit2.Platform
## Platform used when running on RoboVM on iOS. Will not be used at runtime.
#-dontnote retrofit2.Platform$IOS$MainThreadExecutor
## Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
## Retain generic type information for use by reflection by converters and adapters.
#-keepattributes Signature
## Retain declared checked exceptions for use by a Proxy instance.
#-keepattributes Exceptions
#
#
### Rules for Gson
## For using GSON @Expose annotation
#-keepattributes *Annotation*
## Gson specific classes
#-keep class com.google.gson.stream.** { *; }
#
## Prevent proguard from stripping interface information from TypeAdapterFactory,
## JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
#-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer