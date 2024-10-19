# Kotlin Multiplatform IO Library

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blueviolet)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE.md)

This library is a port of JDK's IO classes to Kotlin Multiplatform (KMP). It allows you to work with common Java-style IO operations on Kotlin code that runs across multiple platforms, including JVM, Android, iOS, and more.

## Features

The library includes Kotlin Multiplatform-compatible implementations of the following classes:

- `Reader`: An abstract class for reading character streams.
- `InputStream`: An abstract class for reading raw byte streams.
- `InputStreamReader`: A bridge from byte streams to character streams.
- `BufferedReader`: A wrapper for efficient reading of characters, arrays, and lines from a character stream.
- `StreamDecoder`: A class that handles decoding of bytes into characters using a specified charset.
- `StringReader`: A reader to parse strings as input streams.
- `Charset`: A class from [`com.fleeksoft.charset`](https://github.com/fleeksoft/charset) to manage character sets and encodings.

## Installation

To use this library in your Kotlin Multiplatform project, add the following dependencies to your `build.gradle.kts` file:

```kotlin
commonMain.dependencies {
    implementation("com.fleeksoft.io:io:<version>")
}
```

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE.md) file for more details.