package com.fleeksoft.net

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
class UrlResolveTest {
    @Test
    fun resolvesRelativeUrls() {
        assertEquals("http://example.com/g", URIResolver.resolve("http://example.com/b/c/d;p?q", "../../../../g"))
        assertEquals("", URIResolver.resolve("wrong", "also wrong"))
        assertEquals("https://example.com/one", URIResolver.resolve("https://example.com/one", ""))
        assertEquals("http://example.com/one/two?three", URIResolver.resolve("http://example.com", "./one/two?three"))
        assertEquals("http://example.com/one/two?three", URIResolver.resolve("http://example.com?one", "./one/two?three"))
        assertEquals("http://example.com/one/two?three#four", URIResolver.resolve("http://example.com", "./one/two?three#four"))
        assertEquals("https://example.com/one", URIResolver.resolve("http://example.com/", "https://example.com/one"))
        assertEquals("http://example.com/one/two.html", URIResolver.resolve("http://example.com/two/", "../one/two.html"))
        assertEquals("https://example2.com/one", URIResolver.resolve("https://example.com/", "//example2.com/one"))
        assertEquals("https://example.com:8080/one", URIResolver.resolve("https://example.com:8080", "./one"))
        assertEquals("https://example2.com/one", URIResolver.resolve("http://example.com/", "https://example2.com/one"))
        assertEquals("https://example.com/one", URIResolver.resolve("wrong", "https://example.com/one"))
        assertEquals("https://example.com/one/two.c", URIResolver.resolve("https://example.com/one/two/", "../two.c"))
        assertEquals("https://example.com/two.c", URIResolver.resolve("https://example.com/one/two", "../two.c"))
        assertEquals("ftp://example.com/one", URIResolver.resolve("ftp://example.com/two/", "../one"))
        assertEquals("ftp://example.com/one/two.c", URIResolver.resolve("ftp://example.com/one/", "./two.c"))
        assertEquals("ftp://example.com/one/two.c", URIResolver.resolve("ftp://example.com/one/", "two.c"))
        // examples taken from rfc3986 section 5.4.2
        assertEquals("http://example.com/g", URIResolver.resolve("http://example.com/b/c/d;p?q", "../../../g"))

        assertEquals("http://example.com/g", URIResolver.resolve("http://example.com/b/c/d;p?q", "/./g"))
        assertEquals("http://example.com/g", URIResolver.resolve("http://example.com/b/c/d;p?q", "/../g"))
        assertEquals("http://example.com/b/c/g.", URIResolver.resolve("http://example.com/b/c/d;p?q", "g."))
        assertEquals("http://example.com/b/c/.g", URIResolver.resolve("http://example.com/b/c/d;p?q", ".g"))
        assertEquals("http://example.com/b/c/g..", URIResolver.resolve("http://example.com/b/c/d;p?q", "g.."))
        assertEquals("http://example.com/b/c/..g", URIResolver.resolve("http://example.com/b/c/d;p?q", "..g"))
        assertEquals("http://example.com/b/g", URIResolver.resolve("http://example.com/b/c/d;p?q", "./../g"))
        assertEquals("http://example.com/b/c/g/", URIResolver.resolve("http://example.com/b/c/d;p?q", "./g/."))
        assertEquals("http://example.com/b/c/g/h", URIResolver.resolve("http://example.com/b/c/d;p?q", "g/./h"))
        assertEquals("http://example.com/b/c/h", URIResolver.resolve("http://example.com/b/c/d;p?q", "g/../h"))
        assertEquals("http://example.com/b/c/g;x=1/y", URIResolver.resolve("http://example.com/b/c/d;p?q", "g;x=1/./y"))
        assertEquals("http://example.com/b/c/y", URIResolver.resolve("http://example.com/b/c/d;p?q", "g;x=1/../y"))
        assertEquals("http://example.com/b/c/g?y/./x", URIResolver.resolve("http://example.com/b/c/d;p?q", "g?y/./x"))
        assertEquals("http://example.com/b/c/g?y/../x", URIResolver.resolve("http://example.com/b/c/d;p?q", "g?y/../x"))
        assertEquals("http://example.com/b/c/g#s/./x", URIResolver.resolve("http://example.com/b/c/d;p?q", "g#s/./x"))
        assertEquals("http://example.com/b/c/g#s/../x", URIResolver.resolve("http://example.com/b/c/d;p?q", "g#s/../x"))
    }

    @Test
    fun stripsControlCharsFromUrls() {
        assertEquals("foo:bar", URIResolver.resolve("\nhttps://\texample.com/", "\r\nfo\to:ba\br"))
    }

    @Test
    fun allowsSpaceInUrl() {
        assertEquals("https://example.com/foo bar/", URIResolver.resolve("https://example.com/example/", "../foo bar/"))
    }
}