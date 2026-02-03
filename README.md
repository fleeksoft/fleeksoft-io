# Kotlin Multiplatform IO Library

A Kotlin Multiplatform (KMP) port of Java’s IO classes, bringing familiar IO operations to multiplatform projects—JVM, Android, iOS, macOS, Linux, Windows, Web, and beyond.


[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
![License](https://img.shields.io/badge/license-Apache--2.0-blue)
[![Maven Central](https://img.shields.io/maven-central/v/com.fleeksoft.io/io.svg)](https://central.sonatype.com/artifact/com.fleeksoft.io/io)
---
## 🌐 Supported Platforms
![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)
![badge-mac](http://img.shields.io/badge/platform-macos-111111.svg?style=flat)
![badge-tvos](http://img.shields.io/badge/platform-tvos-808080.svg?style=flat)
![badge-tvos](http://img.shields.io/badge/platform-watchos-808080.svg?style=flat)
![badge-linux](http://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat)
![badge-windows](http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat)
![badge-js](https://img.shields.io/badge/platform-js-F8DB5D.svg?style=flat)
![badge-wasm](https://img.shields.io/badge/platform-wasm-F8DB5D.svg?style=flat)

---

## ✨ Features

This library is organized into modular components for flexibility and clarity.

> **Note**
> Direct file read/write APIs are not included. For file operations, use this library in combination with `kotlinx-io` or `okio`, which support stream conversions.

---
### 🧱 Core Module (`com.fleeksoft.io:io-core`)

Core buffers plus the main stream/reader implementations and extensions.

- `Buffer`: Base container (like `java.nio.Buffer`)
- `ByteBuffer`, `CharBuffer`
- `ByteBufferFactory`, `CharBufferFactory`: Helper utilities to create buffers
- `InputStream`, `OutputStream`, `Reader`, `BufferedReader`
- `ByteArrayInputStream`, `ByteArrayOutputStream`, `BufferedInputStream`, `BufferedOutputStream`
- `FilterInputStream`, `FilterOutputStream`, `FilterReader`, `PushbackReader`
- `StringReader`, `CharArrayReader`
- `Closeable`, `Readable`, `Flushable`: Interfaces for IO components
- **Core Extensions**
  - `"Hello".byteInputStream()`, `"Hello".reader()`
  - `byteArray.inputStream()`
  - `reader.buffered()`, `reader.readLines()`, `reader.readString(count)`

---

### 📦 IO Module (`com.fleeksoft.io:io`)

Charset-aware reader adapter and `InputStream` extensions.

- `InputStreamReader`
- **Charset Support**  [CharsetsReadme.md](CharsetsReadme.md)
- **Stream Extensions**
  ```kotlin
  val byteArrayInputStream: ByteArrayInputStream = "...".byteInputStream(Charsets.UTF8) 
  val inputStreamReader: InputStreamReader = inputStream.reader(Charsets.UTF8) // return InputStreamReader
  val bufferReader: BufferedReader = inputStream.bufferedReader(Charsets.UTF8) // return BufferedReader
  ```

---

---

### 🌐 URI Module (`com.fleeksoft.io:uri`)

Multiplatform-safe version of `java.net.URI`.

- `URI`: Parse, resolve, normalize URIs
- `URIFactory`: Use this instead of `URI.create()`

---

### 🔌 kotlinx-io Integration (`com.fleeksoft.io:kotlinx-io`)

Interop for working with `kotlinx-io` streams:

- `Source.asInputStream(): InputStream`
- `RawSource.asInputStream(): InputStream`
- `InputStream.asSource(): RawSource`

---

### 🔌 Okio Integration (`com.fleeksoft.io:okio`)

Interop for Okio-powered IO:

- `Source.asInputStream(): InputStream`
- `InputStream.asSource(): Source`

---

### 🧬 Charset Modules

Support for standard and extended character sets:

- Standard: `com.fleeksoft.charset:charset:<version>`
- Extended: `com.fleeksoft.charset:charset-ext:<version>`

➡️ [More info](CharsetsReadme.md)

---

## 🛠 Installation

Add dependencies in your `build.gradle.kts`:

```kotlin
commonMain.dependencies {
    implementation("com.fleeksoft.io:io-core:<version>")
    implementation("com.fleeksoft.io:io:<version>")

    // Optional integrations
    implementation("com.fleeksoft.io:kotlinx-io:<version>")
    implementation("com.fleeksoft.io:okio:<version>")
}
```

Find the latest version here:
[![Maven Central](https://img.shields.io/maven-central/v/com.fleeksoft.io/io.svg)](https://central.sonatype.com/artifact/com.fleeksoft.io/io)
---

## 🚀 Usage Example

```kotlin
val str = "Hello, World!"
val byteArray = ByteArray(10)
val charArray = CharArray(10)

val byteArrayInputStream = str.byteInputStream()
val stringReader = str.reader()

val bufferedReader = stringReader.buffered()
val bufferedReader2 = byteArrayInputStream.bufferedReader()

val byteBuffer = ByteBufferFactory.wrap(byteArray)
val charBuffer = CharBufferFactory.wrap(charArray)
```

---

## 🤝 Contributing

Contributions are welcome!
Open an issue or submit a pull request to improve features, fix bugs, or enhance documentation.

## 📄 License

Licensed under the Apache License 2.0.
See [LICENSE](LICENSE.md) for full details.
