package com.fleeksoft.net

import com.fleeksoft.io.exception.URISyntaxException

object URIResolver {
    private val validUriScheme = Regex("^[a-zA-Z][a-zA-Z0-9+-.]*:")
//    private val extraDotSegmentsRegex = Regex("/\\.\\./")
    private val extraDotSegmentsRegex = Regex("^/(?>(?>\\.\\.?/)+)")

    fun resolve(base: URI, relUrl: String): URI {
        // Strip control characters from relUrl
        var sanitizedRelUrl = stripControlChars(relUrl)

        // Fix relUrl if it starts with "?"
        if (sanitizedRelUrl.startsWith("?")) {
            sanitizedRelUrl = base.getPath() + sanitizedRelUrl
        }

        // Resolve the relative URI against the base URI
        val resolvedUri = base.resolve(sanitizedRelUrl)

        // Fix extra dot segments in the resolved URI's path
        val fixedPath = extraDotSegmentsRegex.replaceFirst(resolvedUri.getPath() ?: "", "/")

        // Build the final URI with the fixed path
        return URI(
            resolvedUri.getScheme(),
            resolvedUri.getAuthority(),
            fixedPath,
            resolvedUri.getQuery(),
            resolvedUri.getFragment()
        )
    }

    private fun toExternalForm(u: URI): String {
        val scheme = u.getScheme() ?: ""
        val authority = u.getAuthority()?.let { if (it.isNotEmpty()) "//$it" else "" } ?: ""
        val path = u.getPath() ?: ""
        val query = u.getQuery()?.let { "?$it" } ?: ""
        val fragment = u.getFragment()?.let { "#$it" } ?: ""
        return "$scheme:$authority$path$query$fragment"
    }

    fun resolve(baseUrl: String, relUrl: String): String {
        // Normalize URLs by stripping control characters
        val sanitizedBaseUrl = stripControlChars(baseUrl)
        val sanitizedRelUrl = stripControlChars(relUrl)
        if (sanitizedRelUrl.isEmpty()) return baseUrl

        return try {
            val base = try {
                URI(sanitizedBaseUrl)
            } catch (e: URISyntaxException) {
                // If the base is invalid, check if relUrl is absolute on its own
                val abs = URI(sanitizedRelUrl)
                if (abs.isAbsolute()) return abs.toString()
                throw e // If relUrl is not absolute, rethrow the exception
            }

            // Resolve relative URL against the base URI
            toExternalForm(resolve(base, sanitizedRelUrl))
        } catch (e: URISyntaxException) {
            // Check if relUrl has a valid scheme as a fallback
            if (validUriScheme.containsMatchIn(sanitizedRelUrl)) sanitizedRelUrl else ""
        } catch (e: IllegalArgumentException) {
            // Check if relUrl has a valid scheme as a fallback
            if (validUriScheme.containsMatchIn(sanitizedRelUrl)) sanitizedRelUrl else ""
        }
    }

    private fun stripControlChars(input: String): String {
        return input.filter { it.code >= 32 }
    }
}