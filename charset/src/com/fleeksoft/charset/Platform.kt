package com.fleeksoft.charset

enum class PlatformType {
    ANDROID,
    ANDROID_NATIVE,
    JVM,
    IOS,
    APPLE,
    LINUX,
    JS,
    MAC,
    WINDOWS,
    WASM_JS,
    WASM_WASI,
}

expect object Platform {
    val current: PlatformType
}


fun Platform.isApple(): Boolean = this.current == PlatformType.APPLE || this.current == PlatformType.IOS || this.current == PlatformType.MAC

fun Platform.isWindows(): Boolean = this.current == PlatformType.WINDOWS

fun Platform.isLinux(): Boolean = this.current == PlatformType.LINUX

fun Platform.isJvmOrAndroid(): Boolean = this.current == PlatformType.JVM || this.current == PlatformType.ANDROID

fun Platform.isJvm(): Boolean = this.current == PlatformType.JVM

fun Platform.isJsOrWasmJs(): Boolean = this.current == PlatformType.JS || this.current == PlatformType.WASM_JS

fun Platform.isWasmJs(): Boolean = this.current == PlatformType.WASM_JS
fun Platform.isWasmWasi(): Boolean = this.current == PlatformType.WASM_WASI