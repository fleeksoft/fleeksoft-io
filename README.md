# Kotlin Multiplatform IO Library

This library is a port of JDK's IO classes to Kotlin Multiplatform (KMP). It allows you to work with common Java-style IO operations on Kotlin code that runs across multiple platforms, including JVM, Android, iOS, and more.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
![License: GPL v2](https://img.shields.io/badge/License-GPLv2-blue.svg)
[![Maven Central](https://img.shields.io/maven-central/v/com.fleeksoft.io/io.svg)](https://central.sonatype.com/artifact/com.fleeksoft.io/io)

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

## Features

This library is split into modules, each covering specific sets of classes to support Java-style IO in Kotlin Multiplatform. Key features include:

### Core IO Module (`com.fleeksoft.io:core-io`)
Provides foundational classes for buffer and charset management.

- **Buffer**: Base container for data, compatible with `java.nio.Buffer`.
- **ByteBuffer**: Buffer specifically for byte data.
- **CharBuffer**: Buffer for character data.
- **CharBufferFactory**: Utility for creating `CharBuffer` and `ByteBuffer` instances.
- **Closeable**: Interface for resources that require closing after usage.
- **Readable**: Interface for sources that can be read into a `CharBuffer`.
### Main IO Module (`com.fleeksoft.io:io`)
Includes core IO classes for reading character and byte streams.

- **Reader**: Abstract class for reading character streams.
- **InputStream**: Abstract class for reading raw byte streams.
- **ByteArrayInputStream**: Creates an `InputStream` from a `ByteArray`.
- **InputStreamReader**: Bridges byte streams to character streams.
- **BufferedReader**: Efficient reader that wraps character streams.
- **StringReader**: Reads from a string as if it were a character stream.
- **CharArrayReader**: Reads characters from an array.
- **FilterInputStream** and **FilterReader**: Wraps `InputStream` and `Reader` for additional functionality.
- **PushbackReader**: Enables pushing back characters into the stream for re-reading.
- **Charset**: Encodes and decodes text with support from [`com.fleeksoft.charset`](https://github.com/fleeksoft/charset).
  - `Extensions Functions`: InputStream and Reader extension functions:
      - `String.byteInputStream(charset: Charset = Charsets.UTF8): ByteArrayInputStream`
      - `ByteArray.inputStream(): ByteArrayInputStream`
      - `ByteArray.inputStream(off: Int, len: Int): ByteArrayInputStream`
      - `InputStream.reader(charset: Charset = Charsets.UTF8): InputStreamReader`
      - `Reader.buffered(bufferSize: Int = Constants.DEFAULT_BYTE_BUFFER_SIZE): BufferedReader`
      - `InputStream.bufferedReader(charset: Charset = Charsets.UTF8): BufferedReader`
      - `Reader.forEachLine(action: (String) -> Unit): Unit`
      - `Reader.readLines(): List<String>`
      - `String.reader(): StringReader`
      - `BufferedReader.lineSequence(): Sequence<String>`
      - `BufferedReader.readString(count: Int): String`
### kotlinx-io Integration (`com.fleeksoft.io:kotlinx-io`)
Provides seamless interoperation with kotlinx-io, extending its functionality with InputStream and Source conversions.
- **Source.asInputStream(): InputStream**: Converts a Source into an InputStream.
- **RawSource.asInputStream(): InputStream**: Converts a RawSource into an InputStream.
- **InputStream.asSource(): RawSource**: Converts an InputStream into a RawSource.

### Okio Integration (`com.fleeksoft.io:okio`)
Provides seamless interoperation with Okio, extending its functionality with InputStream and Source conversions.
- **Source.asInputStream(): InputStream**: Converts a Source into an InputStream.
- **InputStream.asSource(): Source**: Converts an InputStream into a Source.


## Installation
###### Latest Version
[![Maven Central](https://img.shields.io/maven-central/v/com.fleeksoft.io/io.svg)](https://central.sonatype.com/artifact/com.fleeksoft.io/io)

To integrate this library into your Kotlin Multiplatform project, add the relevant dependencies in your `build.gradle.kts`:
```kotlin
commonMain.dependencies {
  implementation("com.fleeksoft.io:core-io:<version>")
  implementation("com.fleeksoft.io:io:<version>")
  // Optional: kotlinx-io integration
  implementation("com.fleeksoft.io:kotlinx-io:<version>")
}
```

## Usage Examples
Here’s a basic usage example:
```kotlin
val str = "Hello, World!"
val byteArray = ByteArray(10)
val charArray = CharArray(10)

val byteArrayInputStream: ByteArrayInputStream = str.byteInputStream()
val stringReader: StringReader = str.reader()

val byteArrayInputStream2 = byteArray.inputStream()

val bufferedReader: BufferedReader = stringReader.buffered()
val bufferedReader2: BufferedReader = byteArrayInputStream.bufferedReader()

val byteBuffer: ByteBuffer = ByteBufferFactory.wrap(byteArray)
val charBuffer: CharBuffer = CharBufferFactory.wrap(charArray)
```
## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License
This project is licensed under the GNU General Public License v2.0. See the [LICENSE](LICENSE.md) file for more details.