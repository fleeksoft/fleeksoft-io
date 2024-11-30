/*
 * Copyright (c) 2000, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.fleeksoft.net

import com.fleeksoft.charset.CharsetEncoder
import com.fleeksoft.charset.Charsets
import com.fleeksoft.charset.CodingErrorActionValue
import com.fleeksoft.io.ByteBuffer
import com.fleeksoft.io.ByteBufferFactory
import com.fleeksoft.io.CharBufferFactory
import com.fleeksoft.io.clearExt
import com.fleeksoft.io.exception.URISyntaxException
import com.fleeksoft.io.flipExt
import com.fleeksoft.io.internal.assert
import com.fleeksoft.lang.Character
import com.fleeksoft.lang.isSpaceChar
import kotlin.concurrent.Volatile
import kotlin.jvm.Transient
import kotlin.math.min

actual class URI : Comparable<URI> {
    /**
     * Returns the scheme component of this URI.
     *
     *
     *  The scheme component of a URI, if defined, only contains characters
     * in the *alphanum* category and in the string `"-.+"`.  A
     * scheme always starts with an *alpha* character.
     *
     *
     *
     * The scheme component of a URI cannot contain escaped octets, hence this
     * method does not perform any decoding.
     *
     * @return  The scheme component of this URI,
     * or `null` if the scheme is undefined
     */
    // -- Properties and components of this instance --
    // Components of all URIs: [<scheme>:]<scheme-specific-part>[#<fragment>]
    @Transient
    var _scheme: String? = null // null ==> relative URI
        private set

    /**
     * Returns the raw fragment component of this URI.
     *
     *
     *  The fragment component of a URI, if defined, only contains legal URI
     * characters.
     *
     * @return  The raw fragment component of this URI,
     * or `null` if the fragment is undefined
     */
    @Transient
    var _fragment: String? = null
        private set

    /**
     * Returns the raw authority component of this URI.
     *
     *
     *  The authority component of a URI, if defined, only contains the
     * commercial-at character (`'@'`) and characters in the
     * *unreserved*, *punct*, *escaped*, and *other*
     * categories.  If the authority is server-based then it is further
     * constrained to have valid user-information, host, and port
     * components.
     *
     * @return  The raw authority component of this URI,
     * or `null` if the authority is undefined
     */
    // Hierarchical URI components: [//<authority>]<path>[?<query>]
    @Transient
    var _authoriy: String? = null // Registry or server
        private set

    /**
     * Returns the raw user-information component of this URI.
     *
     *
     *  The user-information component of a URI, if defined, only contains
     * characters in the *unreserved*, *punct*, *escaped*, and
     * *other* categories.
     *
     * @return  The raw user-information component of this URI,
     * or `null` if the user information is undefined
     */
    // Server-based authority: [<userInfo>@]<host>[:<port>]
    @Transient
    var _userInfo: String? = null
        private set

    /**
     * Returns the host component of this URI.
     *
     *
     *  The host component of a URI, if defined, will have one of the
     * following forms:
     *
     *
     *
     *  *
     *
     * A domain name consisting of one or more *labels*
     * separated by period characters (`'.'`), optionally followed by
     * a period character.  Each label consists of *alphanum* characters
     * as well as hyphen characters (`'-'`), though hyphens never
     * occur as the first or last characters in a label. The rightmost
     * label of a domain name consisting of two or more labels, begins
     * with an *alpha* character.
     *
     *  *
     *
     * A dotted-quad IPv4 address of the form
     * *digit*`+.`*digit*`+.`*digit*`+.`*digit*`+`,
     * where no *digit* sequence is longer than three characters and no
     * sequence has a value larger than 255.
     *
     *  *
     *
     * An IPv6 address enclosed in square brackets (`'['` and
     * `']'`) and consisting of hexadecimal digits, colon characters
     * (`':'`), and possibly an embedded IPv4 address.  The full
     * syntax of IPv6 addresses is specified in [*RFC&nbsp;2373: IPv6
 * Addressing Architecture*](http://www.ietf.org/rfc/rfc2373.txt).
     *
     *
     *
     * The host component of a URI cannot contain escaped octets, hence this
     * method does not perform any decoding.
     *
     * @return  The host component of this URI,
     * or `null` if the host is undefined
     * @spec https://www.rfc-editor.org/info/rfc2373
     * RFC 2373: IP Version 6 Addressing Architecture
     */
    @Transient
    var _host: String? = null // null ==> registry-based
        private set

    /**
     * Returns the port number of this URI.
     *
     *
     *  The port component of a URI, if defined, is a non-negative
     * integer.
     *
     * @return  The port component of this URI,
     * or `-1` if the port is undefined
     */
    @Transient
    var _port: Int = -1 // -1 ==> undefined
        private set

    /**
     * Returns the raw path component of this URI.
     *
     *
     *  The path component of a URI, if defined, only contains the slash
     * character (`'/'`), the commercial-at character (`'@'`),
     * and characters in the *unreserved*, *punct*, *escaped*,
     * and *other* categories.
     *
     * @return  The path component of this URI,
     * or `null` if the path is undefined
     */
    // Remaining components of hierarchical URIs
    @Transient
    var _path: String? = null // null ==> opaque
        private set

    /**
     * Returns the raw query component of this URI.
     *
     *
     *  The query component of a URI, if defined, only contains legal URI
     * characters.
     *
     * @return  The raw query component of this URI,
     * or `null` if the query is undefined
     */
    @Transient
    var _query: String? = null
        private set

    // The remaining fields may be computed on demand, which is safe even in
    // the face of multiple threads racing to initialize them
    @Transient
    private var schemeSpecificPart: String? = null

    @Transient
    private var hash = 0 // Zero ==> undefined

    @Transient
    private var decodedUserInfo: String? = null

    @Transient
    private var decodedAuthority: String? = null

    @Transient
    private var decodedPath: String? = null

    @Transient
    private var decodedQuery: String? = null

    @Transient
    private var decodedFragment: String? = null

    @Transient
    private var decodedSchemeSpecificPart: String? = null

    /**
     * The string form of this URI.
     *
     * @serial
     */
    @Volatile
    private var string: String? = null // The only serializable field


    // -- Constructors and factories --
    private constructor() // Used internally

    /**
     * Constructs a URI by parsing the given string.
     *
     *
     *  This constructor parses the given string exactly as specified by the
     * grammar in [RFC&nbsp;2396](http://www.ietf.org/rfc/rfc2396.txt),
     * Appendix&nbsp;A, ***except for the following deviations:***
     *
     *
     *
     *  *
     *
     * An empty authority component is permitted as long as it is
     * followed by a non-empty path, a query component, or a fragment
     * component.  This allows the parsing of URIs such as
     * `"file:///foo/bar"`, which seems to be the intent of
     * RFC&nbsp;2396 although the grammar does not permit it.  If the
     * authority component is empty then the user-information, host, and port
     * components are undefined.
     *
     *  *
     *
     * Empty relative paths are permitted; this seems to be the
     * intent of RFC&nbsp;2396 although the grammar does not permit it.  The
     * primary consequence of this deviation is that a standalone fragment
     * such as `"#foo"` parses as a relative URI with an empty path
     * and the given fragment, and can be usefully [resolved](#resolve-frag) against a base URI.
     *
     *  *
     *
     * IPv4 addresses in host components are parsed rigorously, as
     * specified by [RFC&nbsp;2732](http://www.ietf.org/rfc/rfc2732.txt): Each
     * element of a dotted-quad address must contain no more than three
     * decimal digits.  Each element is further constrained to have a value
     * no greater than 255.
     *
     *  *
     *
     * Hostnames in host components that comprise only a single
     * domain label are permitted to start with an *alphanum*
     * character. This seems to be the intent of [RFC&nbsp;2396](http://www.ietf.org/rfc/rfc2396.txt)
     * section&nbsp;3.2.2 although the grammar does not permit it. The
     * consequence of this deviation is that the authority component of a
     * hierarchical URI such as `s://123`, will parse as a server-based
     * authority.
     *
     *  *
     *
     * IPv6 addresses are permitted for the host component.  An IPv6
     * address must be enclosed in square brackets (`'['` and
     * `']'`) as specified by [RFC&nbsp;2732](http://www.ietf.org/rfc/rfc2732.txt).  The
     * IPv6 address itself must parse according to [RFC&nbsp;2373](http://www.ietf.org/rfc/rfc2373.txt).  IPv6
     * addresses are further constrained to describe no more than sixteen
     * bytes of address information, a constraint implicit in RFC&nbsp;2373
     * but not expressible in the grammar.
     *
     *  *
     *
     * Characters in the *other* category are permitted wherever
     * RFC&nbsp;2396 permits *escaped* octets, that is, in the
     * user-information, path, query, and fragment components, as well as in
     * the authority component if the authority is registry-based.  This
     * allows URIs to contain Unicode characters beyond those in the US-ASCII
     * character set.
     *
     *
     *
     * @param  str   The string to be parsed into a URI
     *
     * @throws  NullPointerException
     * If `str` is `null`
     *
     * @throws URISyntaxException
     * If the given string violates RFC&nbsp;2396, as augmented
     * by the above deviations
     * @spec https://www.rfc-editor.org/info/rfc2373
     * RFC 2373: IP Version 6 Addressing Architecture
     * @spec https://www.rfc-editor.org/info/rfc2396
     * RFC 2396: Uniform Resource Identifiers (URI): Generic Syntax
     * @spec https://www.rfc-editor.org/info/rfc2732
     * RFC 2732: Format for Literal IPv6 Addresses in URL's
     */
    actual constructor(str: String) {
        Parser(str).parse(false)
    }

    /**
     * Constructs a hierarchical URI from the given components.
     *
     *
     *  If a scheme is given then the path, if also given, must either be
     * empty or begin with a slash character (`'/'`).  Otherwise a
     * component of the new URI may be left undefined by passing `null`
     * for the corresponding parameter or, in the case of the `port`
     * parameter, by passing `-1`.
     *
     *
     *  This constructor first builds a URI string from the given components
     * according to the rules specified in [RFC&nbsp;2396](http://www.ietf.org/rfc/rfc2396.txt),
     * section&nbsp;5.2, step&nbsp;7:
     *
     *
     *
     *  1.
     *
     * Initially, the result string is empty.
     *
     *  1.
     *
     * If a scheme is given then it is appended to the result,
     * followed by a colon character (`':'`).
     *
     *  1.
     *
     * If user information, a host, or a port are given then the
     * string `"//"` is appended.
     *
     *  1.
     *
     * If user information is given then it is appended, followed by
     * a commercial-at character (`'@'`).  Any character not in the
     * *unreserved*, *punct*, *escaped*, or *other*
     * categories is [quoted](#quote).
     *
     *  1.
     *
     * If a host is given then it is appended.  If the host is a
     * literal IPv6 address but is not enclosed in square brackets
     * (`'['` and `']'`) then the square brackets are added.
     *
     *
     *  1.
     *
     * If a port number is given then a colon character
     * (`':'`) is appended, followed by the port number in decimal.
     *
     *
     *  1.
     *
     * If a path is given then it is appended.  Any character not in
     * the *unreserved*, *punct*, *escaped*, or *other*
     * categories, and not equal to the slash character (`'/'`) or the
     * commercial-at character (`'@'`), is quoted.
     *
     *  1.
     *
     * If a query is given then a question-mark character
     * (`'?'`) is appended, followed by the query.  Any character that
     * is not a [legal URI character](#legal-chars) is quoted.
     *
     *
     *  1.
     *
     * Finally, if a fragment is given then a hash character
     * (`'#'`) is appended, followed by the fragment.  Any character
     * that is not a legal URI character is quoted.
     *
     *
     *
     *
     *  The resulting URI string is then parsed as if by invoking the [ ][.URI] constructor and then invoking the [ ][.parseServerAuthority] method upon the result; this may cause a [ ] to be thrown.
     *
     * @param   scheme    Scheme name
     * @param   userInfo  User name and authorization information
     * @param   host      Host name
     * @param   port      Port number
     * @param   path      Path
     * @param   query     Query
     * @param   fragment  Fragment
     *
     * @throws URISyntaxException
     * If both a scheme and a path are given but the path is relative,
     * if the URI string constructed from the given components violates
     * RFC&nbsp;2396, or if the authority component of the string is
     * present but cannot be parsed as a server-based authority
     * @spec https://www.rfc-editor.org/info/rfc2396
     * RFC 2396: Uniform Resource Identifiers (URI): Generic Syntax
     */
    actual constructor(scheme: String?, userInfo: String?, host: String?, port: Int, path: String?, query: String?, fragment: String?) {
        val s = toString(scheme, null, null, userInfo, host, port, path, query, fragment)
        checkPath(s, scheme, path)
        Parser(s).parse(true)
    }

    /**
     * Constructs a hierarchical URI from the given components.
     *
     *
     *  If a scheme is given then the path, if also given, must either be
     * empty or begin with a slash character (`'/'`).  Otherwise a
     * component of the new URI may be left undefined by passing `null`
     * for the corresponding parameter.
     *
     *
     *  This constructor first builds a URI string from the given components
     * according to the rules specified in [RFC&nbsp;2396](http://www.ietf.org/rfc/rfc2396.txt),
     * section&nbsp;5.2, step&nbsp;7:
     *
     *
     *
     *  1.
     *
     * Initially, the result string is empty.
     *
     *  1.
     *
     * If a scheme is given then it is appended to the result,
     * followed by a colon character (`':'`).
     *
     *  1.
     *
     * If an authority is given then the string `"//"` is
     * appended, followed by the authority.  If the authority contains a
     * literal IPv6 address then the address must be enclosed in square
     * brackets (`'['` and `']'`).  Any character not in the
     * *unreserved*, *punct*, *escaped*, or *other*
     * categories, and not equal to the commercial-at character
     * (`'@'`), is [quoted](#quote).
     *
     *  1.
     *
     * If a path is given then it is appended.  Any character not in
     * the *unreserved*, *punct*, *escaped*, or *other*
     * categories, and not equal to the slash character (`'/'`) or the
     * commercial-at character (`'@'`), is quoted.
     *
     *  1.
     *
     * If a query is given then a question-mark character
     * (`'?'`) is appended, followed by the query.  Any character that
     * is not a [legal URI character](#legal-chars) is quoted.
     *
     *
     *  1.
     *
     * Finally, if a fragment is given then a hash character
     * (`'#'`) is appended, followed by the fragment.  Any character
     * that is not a legal URI character is quoted.
     *
     *
     *
     *
     *  The resulting URI string is then parsed as if by invoking the [ ][.URI] constructor and then invoking the [ ][.parseServerAuthority] method upon the result; this may cause a [ ] to be thrown.
     *
     * @param   scheme     Scheme name
     * @param   authority  Authority
     * @param   path       Path
     * @param   query      Query
     * @param   fragment   Fragment
     *
     * @throws URISyntaxException
     * If both a scheme and a path are given but the path is relative,
     * if the URI string constructed from the given components violates
     * RFC&nbsp;2396, or if the authority component of the string is
     * present but cannot be parsed as a server-based authority
     * @spec https://www.rfc-editor.org/info/rfc2396
     * RFC 2396: Uniform Resource Identifiers (URI): Generic Syntax
     */
    actual constructor(scheme: String?, authority: String?, path: String?, query: String?, fragment: String?) {
        val s = toString(scheme, null, authority, null, null, -1, path, query, fragment)
        checkPath(s, scheme, path)
        Parser(s).parse(false)
    }

    /**
     * Constructs a hierarchical URI from the given components.
     *
     *
     *  A component may be left undefined by passing `null`.
     *
     *
     *  This convenience constructor works as if by invoking the
     * seven-argument constructor as follows:
     *
     * <blockquote>
     * `new` [ URI][.URI]`(scheme, null, host, -1, path, null, fragment);`
    </blockquote> *
     *
     * @param   scheme    Scheme name
     * @param   host      Host name
     * @param   path      Path
     * @param   fragment  Fragment
     *
     * @throws  URISyntaxException
     * If the URI string constructed from the given components
     * violates RFC&nbsp;2396
     */
    actual constructor(scheme: String?, host: String?, path: String?, fragment: String?) : this(scheme, null, host, -1, path, null, fragment)

    /**
     * Constructs a URI from the given components.
     *
     *
     *  A component may be left undefined by passing `null`.
     *
     *
     *  This constructor first builds a URI in string form using the given
     * components as follows:
     *
     *
     *
     *  1.
     *
     * Initially, the result string is empty.
     *
     *  1.
     *
     * If a scheme is given then it is appended to the result,
     * followed by a colon character (`':'`).
     *
     *  1.
     *
     * If a scheme-specific part is given then it is appended.  Any
     * character that is not a [legal URI character](#legal-chars)
     * is [quoted](#quote).
     *
     *  1.
     *
     * Finally, if a fragment is given then a hash character
     * (`'#'`) is appended to the string, followed by the fragment.
     * Any character that is not a legal URI character is quoted.
     *
     *
     *
     *
     *  The resulting URI string is then parsed in order to create the new
     * URI instance as if by invoking the [.URI] constructor;
     * this may cause a [URISyntaxException] to be thrown.
     *
     * @param   scheme    Scheme name
     * @param   ssp       Scheme-specific part
     * @param   fragment  Fragment
     *
     * @throws  URISyntaxException
     * If the URI string constructed from the given components
     * violates RFC&nbsp;2396
     */
    actual constructor(scheme: String?, ssp: String?, fragment: String?) {
        Parser(toString(scheme, ssp, null, null, null, -1, null, null, fragment))
            .parse(false)
    }

    /**
     * Constructs a simple URI consisting of only a scheme and a pre-validated
     * path. Provides a fast-path for some internal cases.
     */
    internal constructor(scheme: String, path: String) {
        assert(validSchemeAndPath(scheme, path))
        this._scheme = scheme
        this._path = path
    }

    // -- Operations --
    /**
     * Attempts to parse this URI's authority component, if defined, into
     * user-information, host, and port components.
     *
     *
     *  If this URI's authority component has already been recognized as
     * being server-based then it will already have been parsed into
     * user-information, host, and port components.  In this case, or if this
     * URI has no authority component, this method simply returns this URI.
     *
     *
     *  Otherwise this method attempts once more to parse the authority
     * component into user-information, host, and port components, and throws
     * an exception describing why the authority component could not be parsed
     * in that way.
     *
     *
     *  This method is provided because the generic URI syntax specified in
     * [RFC&nbsp;2396](http://www.ietf.org/rfc/rfc2396.txt)
     * cannot always distinguish a malformed server-based authority from a
     * legitimate registry-based authority.  It must therefore treat some
     * instances of the former as instances of the latter.  The authority
     * component in the URI string `"//foo:bar"`, for example, is not a
     * legal server-based authority but it is legal as a registry-based
     * authority.
     *
     *
     *  In many common situations, for example when working URIs that are
     * known to be either URNs or URLs, the hierarchical URIs being used will
     * always be server-based.  They therefore must either be parsed as such or
     * treated as an error.  In these cases a statement such as
     *
     * <blockquote>
     * `URI `*u*`= new URI(str).parseServerAuthority();`
    </blockquote> *
     *
     *
     *  can be used to ensure that *u* always refers to a URI that, if
     * it has an authority component, has a server-based authority with proper
     * user-information, host, and port components.  Invoking this method also
     * ensures that if the authority could not be parsed in that way then an
     * appropriate diagnostic message can be issued based upon the exception
     * that is thrown.
     *
     * @return  A URI whose authority field has been parsed
     * as a server-based authority
     *
     * @throws  URISyntaxException
     * If the authority component of this URI is defined
     * but cannot be parsed as a server-based authority
     * according to RFC&nbsp;2396
     *
     * @spec https://www.rfc-editor.org/info/rfc2396
     * RFC 2396: Uniform Resource Identifiers (URI): Generic Syntax
     */
    actual fun parseServerAuthority(): URI {
        // We could be clever and cache the error message and index from the
        // exception thrown during the original parse, but that would require
        // either more fields or a more-obscure representation.
        if ((_host != null) || (this._authoriy == null)) return this
        Parser(toString()).parse(true)
        return this
    }

    /**
     * Normalizes this URI's path.
     *
     *
     *  If this URI is opaque, or if its path is already in normal form,
     * then this URI is returned.  Otherwise a new URI is constructed that is
     * identical to this URI except that its path is computed by normalizing
     * this URI's path in a manner consistent with [RFC&nbsp;2396](http://www.ietf.org/rfc/rfc2396.txt),
     * section&nbsp;5.2, step&nbsp;6, sub-steps&nbsp;c through&nbsp;f; that is:
     *
     *
     *
     *
     *  1.
     *
     * All `"."` segments are removed.
     *
     *  1.
     *
     * If a `".."` segment is preceded by a non-`".."`
     * segment then both of these segments are removed.  This step is
     * repeated until it is no longer applicable.
     *
     *  1.
     *
     * If the path is relative, and if its first segment contains a
     * colon character (`':'`), then a `"."` segment is
     * prepended.  This prevents a relative URI with a path such as
     * `"a:b/c/d"` from later being re-parsed as an opaque URI with a
     * scheme of `"a"` and a scheme-specific part of `"b/c/d"`.
     * ***(Deviation from RFC&nbsp;2396)***
     *
     *
     *
     *
     *  A normalized path will begin with one or more `".."` segments
     * if there were insufficient non-`".."` segments preceding them to
     * allow their removal.  A normalized path will begin with a `"."`
     * segment if one was inserted by step 3 above.  Otherwise, a normalized
     * path will not contain any `"."` or `".."` segments.
     *
     * @return  A URI equivalent to this URI,
     * but whose path is in normal form
     * @spec https://www.rfc-editor.org/info/rfc2396
     * RFC 2396: Uniform Resource Identifiers (URI): Generic Syntax
     */
    actual fun normalize(): URI {
        return normalize(this)
    }

    /**
     * Resolves the given URI against this URI.
     *
     *
     *  If the given URI is already absolute, or if this URI is opaque, then
     * the given URI is returned.
     *
     *
     * <a id="resolve-frag"></a> If the given URI's fragment component is
     * defined, its path component is empty, and its scheme, authority, and
     * query components are undefined, then a URI with the given fragment but
     * with all other components equal to those of this URI is returned.  This
     * allows a URI representing a standalone fragment reference, such as
     * `"#foo"`, to be usefully resolved against a base URI.
     *
     *
     *  Otherwise this method constructs a new hierarchical URI in a manner
     * consistent with [RFC&nbsp;2396](http://www.ietf.org/rfc/rfc2396.txt),
     * section&nbsp;5.2; that is:
     *
     *
     *
     *  1.
     *
     * A new URI is constructed with this URI's scheme and the given
     * URI's query and fragment components.
     *
     *  1.
     *
     * If the given URI has an authority component then the new URI's
     * authority and path are taken from the given URI.
     *
     *  1.
     *
     * Otherwise the new URI's authority component is copied from
     * this URI, and its path is computed as follows:
     *
     *
     *
     *  1.
     *
     * If the given URI's path is absolute then the new URI's path
     * is taken from the given URI.
     *
     *  1.
     *
     * Otherwise the given URI's path is relative, and so the new
     * URI's path is computed by resolving the path of the given URI
     * against the path of this URI.  This is done by concatenating all but
     * the last segment of this URI's path, if any, with the given URI's
     * path and then normalizing the result as if by invoking the [     ][.normalize] method.
     *
     *
     *
     *
     *
     *
     *  The result of this method is absolute if, and only if, either this
     * URI is absolute or the given URI is absolute.
     *
     * @param  uri  The URI to be resolved against this URI
     * @return The resulting URI
     *
     * @throws  NullPointerException
     * If `uri` is `null`
     * @spec https://www.rfc-editor.org/info/rfc2396
     * RFC 2396: Uniform Resource Identifiers (URI): Generic Syntax
     */
    actual fun resolve(uri: URI): URI {
        return resolve(this, uri)
    }

    /**
     * Constructs a new URI by parsing the given string and then resolving it
     * against this URI.
     *
     *
     *  This convenience method works as if invoking it were equivalent to
     * evaluating the expression [ resolve][.resolve]`(URI.`[create][.create]`(str))`.
     *
     * @param  str   The string to be parsed into a URI
     * @return The resulting URI
     *
     * @throws  NullPointerException
     * If `str` is `null`
     *
     * @throws  IllegalArgumentException
     * If the given string violates RFC&nbsp;2396
     */
    actual fun resolve(str: String): URI {
        return resolve(create(str))
    }

    /**
     * Relativizes the given URI against this URI.
     *
     *
     *  The relativization of the given URI against this URI is computed as
     * follows:
     *
     *
     *
     *  1.
     *
     * If either this URI or the given URI are opaque, or if the
     * scheme and authority components of the two URIs are not identical, or
     * if the path of this URI is not a prefix of the path of the given URI,
     * then the given URI is returned.
     *
     *  1.
     *
     * Otherwise a new relative hierarchical URI is constructed with
     * query and fragment components taken from the given URI and with a path
     * component computed by removing this URI's path from the beginning of
     * the given URI's path.
     *
     *
     *
     * @param  uri  The URI to be relativized against this URI
     * @return The resulting URI
     *
     * @throws  NullPointerException
     * If `uri` is `null`
     */
    actual fun relativize(uri: URI): URI {
        return relativize(this, uri)
    }

    /**
     * Constructs a URL from this URI.
     *
     *
     *  This convenience method works as if invoking it were equivalent to
     * evaluating the expression `new URL(this.toString())` after
     * first checking that this URI is absolute.
     *
     * @return  A URL constructed from this URI
     *
     * @throws  IllegalArgumentException
     * If this URL is not absolute
     *
     * @throws MalformedURLException
     * If a protocol handler for the URL could not be found,
     * or if some other error occurred while constructing the URL
     */
    /*fun toURL(): URL {
        return URL.of(this, null)
    }*/

    // -- Component access methods --

    actual fun isAbsolute(): Boolean = _scheme != null

    actual fun isOpaque(): Boolean = this._path == null

    actual fun getRawSchemeSpecificPart(): String? {
        var part = schemeSpecificPart
        if (part != null) {
            return part
        }

        val s = string
        if (s != null) {
            // if string is defined, components will have been parsed
            var start = 0
            var end = s.length
            if (_scheme != null) {
                start = _scheme!!.length + 1
            }
            if (this._fragment != null) {
                end -= _fragment!!.length + 1
            }
            part = if (this._path != null && _path!!.length == end - start) {
                this._path
            } else {
                s.substring(start, end)
            }
        } else {
            val sb = StringBuilder()
            appendSchemeSpecificPart(
                sb, null, getAuthority(), getUserInfo(),
                _host, _port, getPath(), getQuery()
            )
            part = sb.toString()
        }
        return part.also { schemeSpecificPart = it }
    }

    /**
     * Returns the decoded scheme-specific part of this URI.
     *
     *
     *  The string returned by this method is equal to that returned by the
     * [getRawSchemeSpecificPart][.getRawSchemeSpecificPart] method
     * except that all sequences of escaped octets are [decoded](#decode).
     *
     * @return  The decoded scheme-specific part of this URI
     * (never `null`)
     */
    actual fun getSchemeSpecificPart(): String? {
        var part = decodedSchemeSpecificPart
        if (part == null) {
            part = decode(this.getRawSchemeSpecificPart())
            decodedSchemeSpecificPart = part
        }
        return part
    }

    /**
     * Returns the decoded authority component of this URI.
     *
     *
     *  The string returned by this method is equal to that returned by the
     * [getRawAuthority][.getRawAuthority] method except that all
     * sequences of escaped octets are [decoded](#decode).
     *
     * @return  The decoded authority component of this URI,
     * or `null` if the authority is undefined
     */
    actual fun getAuthority(): String? {
        var auth = decodedAuthority
        if ((auth == null) && (this._authoriy != null)) {
            auth = decode(this._authoriy)
            decodedAuthority = auth
        }
        return auth
    }

    actual fun getRawAuthority(): String? = _authoriy

    /**
     * Returns the decoded user-information component of this URI.
     *
     *
     *  The string returned by this method is equal to that returned by the
     * [getRawUserInfo][.getRawUserInfo] method except that all
     * sequences of escaped octets are [decoded](#decode).
     *
     * @return  The decoded user-information component of this URI,
     * or `null` if the user information is undefined
     */
    actual fun getUserInfo(): String? {
        var user = decodedUserInfo
        if ((user == null) && (this._userInfo != null)) {
            user = decode(this._userInfo)
            decodedUserInfo = user
        }
        return user
    }

    actual fun getRawUserInfo(): String? = _userInfo

    /**
     * Returns the decoded path component of this URI.
     *
     *
     *  The string returned by this method is equal to that returned by the
     * [getRawPath][.getRawPath] method except that all sequences of
     * escaped octets are [decoded](#decode).
     *
     * @return  The decoded path component of this URI,
     * or `null` if the path is undefined
     */
    actual fun getPath(): String? {
        var decoded = decodedPath
        if ((decoded == null) && (this._path != null)) {
            decoded = decode(this._path)
            decodedPath = decoded
        }
        return decoded
    }

    actual fun getRawPath(): String? = _path

    /**
     * Returns the decoded query component of this URI.
     *
     *
     *  The string returned by this method is equal to that returned by the
     * [getRawQuery][.getRawQuery] method except that all sequences of
     * escaped octets are [decoded](#decode).
     *
     * @return  The decoded query component of this URI,
     * or `null` if the query is undefined
     */
    actual fun getQuery(): String? {
        var decoded = decodedQuery
        if ((decoded == null) && (this._query != null)) {
            decoded = decode(this._query, false)
            decodedQuery = decoded
        }
        return decoded
    }

    actual fun getRawQuery(): String? = _query

    /**
     * Returns the decoded fragment component of this URI.
     *
     *
     *  The string returned by this method is equal to that returned by the
     * [getRawFragment][.getRawFragment] method except that all
     * sequences of escaped octets are [decoded](#decode).
     *
     * @return  The decoded fragment component of this URI,
     * or `null` if the fragment is undefined
     */
    actual fun getFragment(): String? {
        var decoded = decodedFragment
        if ((decoded == null) && (this._fragment != null)) {
            decoded = decode(this._fragment, false)
            decodedFragment = decoded
        }
        return decoded
    }

    actual fun getRawFragment(): String? = _fragment

    actual fun getScheme(): String? = _scheme
    actual fun getHost(): String? = _host
    actual fun getPort(): Int = _port


    // -- Equality, comparison, hash code, toString, and serialization --
    /**
     * Tests this URI for equality with another object.
     *
     *
     *  If the given object is not a URI then this method immediately
     * returns `false`.
     *
     *
     *  For two URIs to be considered equal requires that either both are
     * opaque or both are hierarchical.  Their schemes must either both be
     * undefined or else be equal without regard to case. Their fragments
     * must either both be undefined or else be equal.
     *
     *
     *  For two opaque URIs to be considered equal, their scheme-specific
     * parts must be equal.
     *
     *
     *  For two hierarchical URIs to be considered equal, their paths must
     * be equal and their queries must either both be undefined or else be
     * equal.  Their authorities must either both be undefined, or both be
     * registry-based, or both be server-based.  If their authorities are
     * defined and are registry-based, then they must be equal.  If their
     * authorities are defined and are server-based, then their hosts must be
     * equal without regard to case, their port numbers must be equal, and
     * their user-information components must be equal.
     *
     *
     *  When testing the user-information, path, query, fragment, authority,
     * or scheme-specific parts of two URIs for equality, the raw forms rather
     * than the encoded forms of these components are compared and the
     * hexadecimal digits of escaped octets are compared without regard to
     * case.
     *
     *
     *  This method satisfies the general contract of the [ ][Object.equals] method.
     *
     * @param   other   The object to which this object is to be compared
     *
     * @return  `true` if, and only if, the given object is a URI that
     * is identical to this URI
     */


    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is URI) return false
        if (this.isOpaque() != other.isOpaque()) return false
        if (!equalIgnoringCase(this._scheme, other._scheme)) return false
        if (!equal(this._fragment, other._fragment)) return false

        // Opaque
        if (this.isOpaque()) return equal(this.schemeSpecificPart, other.schemeSpecificPart)

        // Hierarchical
        if (!equal(this._path, other._path)) return false
        if (!equal(this._query, other._query)) return false

        // Authorities
        if (this._authoriy === other._authoriy) return true
        if (this._host != null) {
            // Server-based
            if (!equal(this._userInfo, other._userInfo)) return false
            if (!equalIgnoringCase(this._host, other._host)) return false
            if (this._port != other._port) return false
        } else if (this._authoriy != null) {
            // Registry-based
            if (!equal(this._authoriy, other._authoriy)) return false
        } else if (this._authoriy !== other._authoriy) {
            return false
        }

        return true
    }

    /**
     * Returns a hash-code value for this URI.  The hash code is based upon all
     * of the URI's components, and satisfies the general contract of the
     * [Object.hashCode] method.
     *
     * @return  A hash-code value for this URI
     */
    override fun hashCode(): Int {
        var h = hash
        if (h == 0) {
            h = hashIgnoringCase(0, _scheme)
            h = hash(h, this._fragment)
            if (this.isOpaque()) {
                h = hash(h, schemeSpecificPart)
            } else {
                h = hash(h, this._path)
                h = hash(h, this._query)
                if (_host != null) {
                    h = hash(h, this._userInfo)
                    h = hashIgnoringCase(h, _host)
                    h += 1949 * _port
                } else {
                    h = hash(h, this._authoriy)
                }
            }
            if (h != 0) {
                hash = h
            }
        }
        return h
    }

    /**
     * Compares this URI to another object, which must be a URI.
     *
     *
     *  When comparing corresponding components of two URIs, if one
     * component is undefined but the other is defined then the first is
     * considered to be less than the second.  Unless otherwise noted, string
     * components are ordered according to their natural, case-sensitive
     * ordering as defined by the [ String.compareTo][String.compareTo] method.  String components that are subject to
     * encoding are compared by comparing their raw forms rather than their
     * encoded forms and the hexadecimal digits of escaped octets are compared
     * without regard to case.
     *
     *
     *  The ordering of URIs is defined as follows:
     *
     *
     *
     *  *
     *
     * Two URIs with different schemes are ordered according the
     * ordering of their schemes, without regard to case.
     *
     *  *
     *
     * A hierarchical URI is considered to be less than an opaque URI
     * with an identical scheme.
     *
     *  *
     *
     * Two opaque URIs with identical schemes are ordered according
     * to the ordering of their scheme-specific parts.
     *
     *  *
     *
     * Two opaque URIs with identical schemes and scheme-specific
     * parts are ordered according to the ordering of their
     * fragments.
     *
     *  *
     *
     * Two hierarchical URIs with identical schemes are ordered
     * according to the ordering of their authority components:
     *
     *
     *
     *  *
     *
     * If both authority components are server-based then the URIs
     * are ordered according to their user-information components; if these
     * components are identical then the URIs are ordered according to the
     * ordering of their hosts, without regard to case; if the hosts are
     * identical then the URIs are ordered according to the ordering of
     * their ports.
     *
     *  *
     *
     * If one or both authority components are registry-based then
     * the URIs are ordered according to the ordering of their authority
     * components.
     *
     *
     *
     *  *
     *
     * Finally, two hierarchical URIs with identical schemes and
     * authority components are ordered according to the ordering of their
     * paths; if their paths are identical then they are ordered according to
     * the ordering of their queries; if the queries are identical then they
     * are ordered according to the order of their fragments.
     *
     *
     *
     *
     *  This method satisfies the general contract of the [ ][Comparable.compareTo]
     * method.
     *
     * @param   that
     * The object to which this URI is to be compared
     *
     * @return  A negative integer, zero, or a positive integer as this URI is
     * less than, equal to, or greater than the given URI
     *
     * @throws  ClassCastException
     * If the given object is not a URI
     */
    actual override fun compareTo(that: URI): Int {
        var c: Int

        if ((compareIgnoringCase(this._scheme, that._scheme).also { c = it }) != 0) return c

        if (this.isOpaque()) {
            if (that.isOpaque()) {
                // Both opaque
                if ((compare(
                        this.schemeSpecificPart,
                        that.schemeSpecificPart
                    ).also { c = it }) != 0
                ) return c
                return compare(this._fragment, that._fragment)
            }
            return +1 // Opaque > hierarchical
        } else if (that.isOpaque()) {
            return -1 // Hierarchical < opaque
        }

        // Hierarchical
        if ((this._host != null) && (that?._host != null)) {
            // Both server-based
            if ((compare(this._userInfo, that._userInfo).also { c = it }) != 0) return c
            if ((compareIgnoringCase(this._host, that._host).also { c = it }) != 0) return c
            if (((this._port - that._port).also { c = it }) != 0) return c
        } else {
            // If one or both authorities are registry-based then we simply
            // compare them in the usual, case-sensitive way.  If one is
            // registry-based and one is server-based then the strings are
            // guaranteed to be unequal, hence the comparison will never return
            // zero and the compareTo and equals methods will remain
            // consistent.
            if ((compare(this._authoriy, that?._authoriy).also { c = it }) != 0) return c
        }

        if ((compare(this._path, that?._path).also { c = it }) != 0) return c
        if ((compare(this._query, that?._query).also { c = it }) != 0) return c
        return compare(this._fragment, that?._fragment)
    }

    /**
     * Returns the content of this URI as a string.
     *
     *
     *  If this URI was created by invoking one of the constructors in this
     * class then a string equivalent to the original input string, or to the
     * string computed from the originally-given components, as appropriate, is
     * returned.  Otherwise this URI was created by normalization, resolution,
     * or relativization, and so a string is constructed from this URI's
     * components according to the rules specified in [RFC&nbsp;2396](http://www.ietf.org/rfc/rfc2396.txt),
     * section&nbsp;5.2, step&nbsp;7.
     *
     * @return  The string form of this URI
     * @spec https://www.rfc-editor.org/info/rfc2396
     * RFC 2396: Uniform Resource Identifiers (URI): Generic Syntax
     */
    override fun toString(): String {
        var s = string
        if (s == null) {
            s = defineString()
        }
        return s
    }

    private fun defineString(): String {
        val s = string
        if (s != null) {
            return s
        }

        val sb = StringBuilder()
        if (_scheme != null) {
            sb.append(_scheme)
            sb.append(':')
        }
        if (this.isOpaque()) {
            sb.append(schemeSpecificPart)
        } else {
            if (_host != null) {
                sb.append("//")
                if (this._userInfo != null) {
                    sb.append(this._userInfo)
                    sb.append('@')
                }
                val needBrackets = ((_host!!.indexOf(':') >= 0)
                        && !_host!!.startsWith("[") && !_host!!.endsWith("]"))
                if (needBrackets) sb.append('[')
                sb.append(_host)
                if (needBrackets) sb.append(']')
                if (_port != -1) {
                    sb.append(':')
                    sb.append(_port)
                }
            } else if (this._authoriy != null) {
                sb.append("//")
                sb.append(this._authoriy)
            }
            if (this._path != null) sb.append(this._path)
            if (this._query != null) {
                sb.append('?')
                sb.append(this._query)
            }
        }
        if (this._fragment != null) {
            sb.append('#')
            sb.append(this._fragment)
        }
        return sb.toString().also { string = it }
    }

    private fun appendAuthority(sb: StringBuilder, authority: String?, userInfo: String?, host: String?, port: Int) {
        if (host != null) {
            sb.append("//")
            if (userInfo != null) {
                sb.append(quote(userInfo, L_USERINFO, H_USERINFO))
                sb.append('@')
            }
            val needBrackets = ((host.indexOf(':') >= 0)
                    && !host.startsWith("[") && !host.endsWith("]"))
            if (needBrackets) sb.append('[')
            sb.append(host)
            if (needBrackets) sb.append(']')
            if (port != -1) {
                sb.append(':')
                sb.append(port)
            }
        } else if (authority != null) {
            sb.append("//")
            if (authority.startsWith("[")) {
                // authority should (but may not) contain an embedded IPv6 address
                val end = authority.indexOf(']')
                var doquote: String? = authority
                if (end != -1 && authority.indexOf(':') != -1) {
                    // the authority contains an IPv6 address
                    sb.append(authority, 0, end + 1)
                    doquote = authority.substring(end + 1)
                }
                sb.append(
                    quote(
                        doquote!!,
                        L_REG_NAME or L_SERVER,
                        H_REG_NAME or H_SERVER
                    )
                )
            } else {
                sb.append(
                    quote(
                        authority,
                        L_REG_NAME or L_SERVER,
                        H_REG_NAME or H_SERVER
                    )
                )
            }
        }
    }

    private fun appendSchemeSpecificPart(
        sb: StringBuilder,
        opaquePart: String?,
        authority: String?,
        userInfo: String?,
        host: String?,
        port: Int,
        path: String?,
        query: String?
    ) {
        if (opaquePart != null) {
            /* check if SSP begins with an IPv6 address
             * because we must not quote a literal IPv6 address
             */
            if (opaquePart.startsWith("//[")) {
                val end = opaquePart.indexOf(']')
                if (end != -1 && opaquePart.indexOf(':') != -1) {
                    val doquote = opaquePart.substring(end + 1)
                    sb.append(opaquePart, 0, end + 1)
                    sb.append(quote(doquote, L_URIC, H_URIC))
                }
            } else {
                sb.append(quote(opaquePart, L_URIC, H_URIC))
            }
        } else {
            appendAuthority(sb, authority, userInfo, host, port)
            if (path != null) sb.append(quote(path, L_PATH, H_PATH))
            if (query != null) {
                sb.append('?')
                sb.append(quote(query, L_URIC, H_URIC))
            }
        }
    }

    private fun appendFragment(sb: StringBuilder, fragment: String?) {
        if (fragment != null) {
            sb.append('#')
            sb.append(quote(fragment, L_URIC, H_URIC))
        }
    }

    private fun toString(
        scheme: String?,
        opaquePart: String?,
        authority: String?,
        userInfo: String?,
        host: String?,
        port: Int,
        path: String?,
        query: String?,
        fragment: String?
    ): String {
        val sb = StringBuilder()
        if (scheme != null) {
            sb.append(scheme)
            sb.append(':')
        }
        appendSchemeSpecificPart(
            sb, opaquePart,
            authority, userInfo, host, port,
            path, query
        )
        appendFragment(sb, fragment)
        return sb.toString()
    }

    // -- Parsing --
    // For convenience we wrap the input URI string in a new instance of the
    // following internal class.  This saves always having to pass the input
    // string as an argument to each internal scan/parse method.
    private inner class Parser(// URI input string
        private val input: String
    ) {
        private var requireServerAuthority = false

        // -- Methods for throwing URISyntaxException in various ways --
        fun fail(reason: String) {
            throw URISyntaxException(input, reason)
        }

        fun fail(reason: String, p: Int) {
            throw URISyntaxException(input, reason, p)
        }

        fun failExpecting(expected: String?, p: Int) {
            fail("Expected $expected", p)
        }


        // -- Simple access to the input string --
        // Tells whether start < end and, if so, whether charAt(start) == c
        //
        fun at(start: Int, end: Int, c: Char): Boolean {
            return (start < end) && (input[start] == c)
        }

        // Tells whether start + s.length() < end and, if so,
        // whether the chars at the start position match s exactly
        //
        fun at(start: Int, end: Int, s: String): Boolean {
            var p = start
            val sn = s.length
            if (sn > end - p) return false
            var i = 0
            while (i < sn) {
                if (input[p++] != s[i]) {
                    break
                }
                i++
            }
            return (i == sn)
        }


        // -- Scanning --
        // The various scan and parse methods that follow use a uniform
        // convention of taking the current start position and end index as
        // their first two arguments.  The start is inclusive while the end is
        // exclusive, just as in the String class, i.e., a start/end pair
        // denotes the left-open interval [start, end) of the input string.
        //
        // These methods never proceed past the end position.  They may return
        // -1 to indicate outright failure, but more often they simply return
        // the position of the first char after the last char scanned.  Thus
        // a typical idiom is
        //
        //     int p = start;
        //     int q = scan(p, end, ...);
        //     if (q > p)
        //         // We scanned something
        //         ...;
        //     else if (q == p)
        //         // We scanned nothing
        //         ...;
        //     else if (q == -1)
        //         // Something went wrong
        //         ...;
        // Scan a specific char: If the char at the given start position is
        // equal to c, return the index of the next char; otherwise, return the
        // start position.
        //
        fun scan(start: Int, end: Int, c: Char): Int {
            if ((start < end) && (input[start] == c)) return start + 1
            return start
        }

        // Scan forward from the given start position.  Stop at the first char
        // in the err string (in which case -1 is returned), or the first char
        // in the stop string (in which case the index of the preceding char is
        // returned), or the end of the input string (in which case the length
        // of the input string is returned).  May return the start position if
        // nothing matches.
        //
        fun scan(start: Int, end: Int, err: String, stop: String): Int {
            var p = start
            while (p < end) {
                val c = input[p]
                if (err.indexOf(c) >= 0) return -1
                if (stop.indexOf(c) >= 0) break
                p++
            }
            return p
        }

        // Scan forward from the given start position.  Stop at the first char
        // in the stop string (in which case the index of the preceding char is
        // returned), or the end of the input string (in which case the length
        // of the input string is returned).  May return the start position if
        // nothing matches.
        //
        fun scan(start: Int, end: Int, stop: String): Int {
            var p = start
            while (p < end) {
                val c = input[p]
                if (stop.indexOf(c) >= 0) break
                p++
            }
            return p
        }

        // Scan a potential escape sequence, starting at the given position,
        // with the given first char (i.e., charAt(start) == c).
        //
        // This method assumes that if escapes are allowed then visible
        // non-US-ASCII chars are also allowed.
        //
        fun scanEscape(start: Int, n: Int, first: Char): Int {
            val p = start
            val c: Char = first
            if (c == '%') {
                // Process escape pair
                if ((p + 3 <= n)
                    && match(input[p + 1], L_HEX, H_HEX)
                    && match(input[p + 2], L_HEX, H_HEX)
                ) {
                    return p + 3
                }
                fail("Malformed escape pair", p)
            } else if ((c.code > 128)
                && !Character.isSpaceChar(c.code) && !c.isISOControl()
            ) {
                // Allow unescaped but visible non-US-ASCII chars
                return p + 1
            }
            return p
        }

        // Scan chars that match the given mask pair
        fun scan(start: Int, n: Int, lowMask: Long, highMask: Long): Int {
            var p = start
            while (p < n) {
                val c = input[p]
                if (match(c, lowMask, highMask)) {
                    p++
                    continue
                }
                if ((lowMask and L_ESCAPED) != 0L) {
                    val q = scanEscape(p, n, c)
                    if (q > p) {
                        p = q
                        continue
                    }
                }
                break
            }
            return p
        }

        // Check that each of the chars in [start, end) matches the given mask
        fun checkChars(start: Int, end: Int, lowMask: Long, highMask: Long, what: String?) {
            val p = scan(start, end, lowMask, highMask)
            if (p < end) fail("Illegal character in $what", p)
        }

        // Check that the char at position p matches the given mask
        //
        fun checkChar(p: Int, lowMask: Long, highMask: Long, what: String?) {
            checkChars(p, p + 1, lowMask, highMask, what)
        }


        // -- Parsing --
        // [<scheme>:]<scheme-specific-part>[#<fragment>]
        //
        fun parse(rsa: Boolean) {
            requireServerAuthority = rsa
            val n = input.length
            var p = scan(0, n, "/?#", ":")
            if ((p >= 0) && at(p, n, ':')) {
                if (p == 0) failExpecting("scheme name", 0)
                checkChar(0, L_ALPHA, H_ALPHA, "scheme name")
                checkChars(1, p, L_SCHEME, H_SCHEME, "scheme name")
                _scheme = input.substring(0, p)
                p++ // Skip ':'
                if (at(p, n, '/')) {
                    p = parseHierarchical(p, n)
                } else {
                    // opaque; need to create the schemeSpecificPart
                    val q = scan(p, n, "#")
                    if (q <= p) failExpecting("scheme-specific part", p)
                    checkChars(p, q, L_URIC, H_URIC, "opaque part")
                    schemeSpecificPart = input.substring(p, q)
                    p = q
                }
            } else {
                p = parseHierarchical(0, n)
            }
            if (at(p, n, '#')) {
                checkChars(p + 1, n, L_URIC, H_URIC, "fragment")
                _fragment = input.substring(p + 1, n)
                p = n
            }
            if (p < n) fail("end of URI", p)
        }

        // [//authority]<path>[?<query>]
        //
        // DEVIATION from RFC2396: We allow an empty authority component as
        // long as it's followed by a non-empty path, query component, or
        // fragment component.  This is so that URIs such as "file:///foo/bar"
        // will parse.  This seems to be the intent of RFC2396, though the
        // grammar does not permit it.  If the authority is empty then the
        // userInfo, host, and port components are undefined.
        //
        // DEVIATION from RFC2396: We allow empty relative paths.  This seems
        // to be the intent of RFC2396, but the grammar does not permit it.
        // The primary consequence of this deviation is that "#f" parses as a
        // relative URI with an empty path.
        //
        @Throws(URISyntaxException::class)
        fun parseHierarchical(start: Int, n: Int): Int {
            var p = start
            if (at(p, n, '/') && at(p + 1, n, '/')) {
                p += 2
                val q = scan(p, n, "/?#")
                if (q > p) {
                    p = parseAuthority(p, q)
                } else if (q < n) {
                    // DEVIATION: Allow empty authority prior to non-empty
                    // path, query component or fragment identifier
                } else failExpecting("authority", p)
            }
            var q = scan(p, n, "?#") // DEVIATION: May be empty
            checkChars(p, q, L_PATH, H_PATH, "path")
            _path = input.substring(p, q)
            p = q
            if (at(p, n, '?')) {
                p++
                q = scan(p, n, "#")
                checkChars(p, q, L_URIC, H_URIC, "query")
                _query = input.substring(p, q)
                p = q
            }
            return p
        }

        // authority     = server | reg_name
        //
        // Ambiguity: An authority that is a registry name rather than a server
        // might have a prefix that parses as a server.  We use the fact that
        // the authority component is always followed by '/' or the end of the
        // input string to resolve this: If the complete authority did not
        // parse as a server then we try to parse it as a registry name.
        //
        @Throws(URISyntaxException::class)
        fun parseAuthority(start: Int, n: Int): Int {
            val p = start
            var q = p
            var qreg = p
            var ex: URISyntaxException? = null

            val serverChars: Boolean
            val regChars: Boolean
            val skipParseException: Boolean

            serverChars = if (scan(p, n, "]") > p) {
                // contains a literal IPv6 address, therefore % is allowed
                (scan(p, n, L_SERVER_PERCENT, H_SERVER_PERCENT) == n)
            } else {
                (scan(p, n, L_SERVER, H_SERVER) == n)
            }
            regChars = ((scan(p, n, L_REG_NAME, H_REG_NAME).also { qreg = it }) == n)

            if (regChars && !serverChars) {
                // Must be a registry-based authority
                _authoriy = input.substring(p, n)
                return n
            }

            // When parsing a URI, skip creating exception objects if the server-based
            // authority is not required and the registry parse is successful.
            //
            skipParseException = (!requireServerAuthority && regChars)
            if (serverChars) {
                // Might be (probably is) a server-based authority, so attempt
                // to parse it as such.  If the attempt fails, try to treat it
                // as a registry-based authority.
                try {
                    q = parseServer(p, n, skipParseException)
                    if (q < n) {
                        if (skipParseException) {
                            _userInfo = null
                            _host = null
                            _port = -1
                            q = p
                        } else {
                            failExpecting("end of authority", q)
                        }
                    } else {
                        _authoriy = input.substring(p, n)
                    }
                } catch (x: URISyntaxException) {
                    // Undo results of failed parse
                    _userInfo = null
                    _host = null
                    _port = -1
                    if (requireServerAuthority) {
                        // If we're insisting upon a server-based authority,
                        // then just re-throw the exception
                        throw x
                    } else {
                        // Save the exception in case it doesn't parse as a
                        // registry either
                        ex = x
                        q = p
                    }
                }
            }

            if (q < n) {
                if (regChars) {
                    // Registry-based authority
                    _authoriy = input.substring(p, n)
                } else if (ex != null) {
                    // Re-throw exception; it was probably due to
                    // a malformed IPv6 address
                    throw ex
                } else {
                    fail("Illegal character in authority", if (serverChars) q else qreg)
                }
            }

            return n
        }


        // [<userinfo>@]<host>[:<port>]
        //
        fun parseServer(start: Int, n: Int, skipParseException: Boolean): Int {
            var p = start
            var q: Int

            // userinfo
            q = scan(p, n, "/?#", "@")
            if ((q >= p) && at(q, n, '@')) {
                checkChars(p, q, L_USERINFO, H_USERINFO, "user info")
                _userInfo = input.substring(p, q)
                p = q + 1 // Skip '@'
            }

            // hostname, IPv4 address, or IPv6 address
            if (at(p, n, '[')) {
                // DEVIATION from RFC2396: Support IPv6 addresses, per RFC2732
                p++
                q = scan(p, n, "/?#", "]")
                if ((q > p) && at(q, n, ']')) {
                    // look for a "%" scope id
                    val r = scan(p, q, "%")
                    if (r > p) {
                        parseIPv6Reference(p, r)
                        if (r + 1 == q) {
                            fail("scope id expected")
                        }
                        checkChars(
                            r + 1, q, L_SCOPE_ID, H_SCOPE_ID,
                            "scope id"
                        )
                    } else {
                        parseIPv6Reference(p, q)
                    }
                    _host = input.substring(p - 1, q + 1)
                    p = q + 1
                } else {
                    failExpecting("closing bracket for IPv6 address", q)
                }
            } else {
                q = parseIPv4Address(p, n)
                if (q <= p) q = parseHostname(p, n, skipParseException)
                p = q
            }

            // port
            if (at(p, n, ':')) {
                p++
                q = scan(p, n, "/")
                if (q > p) {
                    checkChars(p, q, L_DIGIT, H_DIGIT, "port number")
                    try {
                        _port = input.substring(p, q).toInt(10)
                    } catch (x: NumberFormatException) {
                        fail("Malformed port number", p)
                    }
                    p = q
                }
            } else if (p < n && skipParseException) {
                return p
            }

            if (p < n) failExpecting("port number", p)

            return p
        }

        // Scan a string of decimal digits whose value fits in a byte
        //
        fun scanByte(start: Int, n: Int): Int {
            val p = start
            val q: Int = scan(p, n, L_DIGIT, H_DIGIT)
            if (q <= p) return q
            if (input.substring(p, q).toInt(10) > 255) return p
            return q
        }

        // Scan an IPv4 address.
        //
        // If the strict argument is true then we require that the given
        // interval contain nothing besides an IPv4 address; if it is false
        // then we only require that it start with an IPv4 address.
        //
        // If the interval does not contain or start with (depending upon the
        // strict argument) a legal IPv4 address characters then we return -1
        // immediately; otherwise we insist that these characters parse as a
        // legal IPv4 address and throw an exception on failure.
        //
        // We assume that any string of decimal digits and dots must be an IPv4
        // address.  It won't parse as a hostname anyway, so making that
        // assumption here allows more meaningful exceptions to be thrown.
        //
        @Throws(URISyntaxException::class)
        fun scanIPv4Address(start: Int, n: Int, strict: Boolean): Int {
            var p = start
            var q: Int
            val m: Int = scan(p, n, L_DIGIT or L_DOT, H_DIGIT or H_DOT)
            if ((m <= p) || (strict && (m != n))) return -1
            while (true) {
                // Per RFC2732: At most three digits per byte
                // Further constraint: Each element fits in a byte
                if ((scanByte(p, m).also { q = it }) <= p) break
                p = q
                if ((scan(p, m, '.').also { q = it }) <= p) break
                p = q
                if ((scanByte(p, m).also { q = it }) <= p) break
                p = q
                if ((scan(p, m, '.').also { q = it }) <= p) break
                p = q
                if ((scanByte(p, m).also { q = it }) <= p) break
                p = q
                if ((scan(p, m, '.').also { q = it }) <= p) break
                p = q
                if ((scanByte(p, m).also { q = it }) <= p) break
                p = q
                if (q < m) break
                return q
            }
            fail("Malformed IPv4 address", q)
            return -1
        }

        // Take an IPv4 address: Throw an exception if the given interval
        // contains anything except an IPv4 address
        //
        @Throws(URISyntaxException::class)
        fun takeIPv4Address(start: Int, n: Int, expected: String?): Int {
            val p = scanIPv4Address(start, n, true)
            if (p <= start) failExpecting(expected, start)
            return p
        }

        // Attempt to parse an IPv4 address, returning -1 on failure but
        // allowing the given interval to contain [:<characters>] after
        // the IPv4 address.
        //
        fun parseIPv4Address(start: Int, n: Int): Int {
            var p: Int

            try {
                p = scanIPv4Address(start, n, false)
            } catch (x: URISyntaxException) {
                return -1
            } catch (x: NumberFormatException) {
                return -1
            }

            if (p > start && p < n) {
                // IPv4 address is followed by something - check that
                // it's a ":" as this is the only valid character to
                // follow an address.
                if (input[p] != ':') {
                    p = -1
                }
            }

            if (p > start) _host = input.substring(start, p)

            return p
        }

        // hostname      = domainlabel [ "." ] | 1*( domainlabel "." ) toplabel [ "." ]
        // domainlabel   = alphanum | alphanum *( alphanum | "-" ) alphanum
        // toplabel      = alpha | alpha *( alphanum | "-" ) alphanum
        //
        @Throws(URISyntaxException::class)
        fun parseHostname(start: Int, n: Int, skipParseException: Boolean): Int {
            var p = start
            var q: Int
            var l = -1 // Start of last parsed label

            do {
                // domainlabel = alphanum [ *( alphanum | "-" ) alphanum ]
                q = scan(p, n, L_ALPHANUM, H_ALPHANUM)
                if (q <= p) break
                l = p
                p = q
                q = scan(p, n, L_ALPHANUM or L_DASH, H_ALPHANUM or H_DASH)
                if (q > p) {
                    if (input[q - 1] == '-') fail("Illegal character in hostname", q - 1)
                    p = q
                }
                q = scan(p, n, '.')
                if (q <= p) break
                p = q
            } while (p < n)

            if ((p < n) && !at(p, n, ':')) {
                if (skipParseException) {
                    return p
                }
                fail("Illegal character in hostname", p)
            }
            if (l < 0) failExpecting("hostname", start)

            // for a fully qualified hostname check that the rightmost
            // label starts with an alpha character.
            if (l > start && !match(input[l], L_ALPHA, H_ALPHA)) {
                fail("Illegal character in hostname", l)
            }

            _host = input.substring(start, p)
            return p
        }


        // IPv6 address parsing, from RFC2373: IPv6 Addressing Architecture
        //
        // Bug: The grammar in RFC2373 Appendix B does not allow addresses of
        // the form ::12.34.56.78, which are clearly shown in the examples
        // earlier in the document.  Here is the original grammar:
        //
        //   IPv6address = hexpart [ ":" IPv4address ]
        //   hexpart     = hexseq | hexseq "::" [ hexseq ] | "::" [ hexseq ]
        //   hexseq      = hex4 *( ":" hex4)
        //   hex4        = 1*4HEXDIG
        //
        // We therefore use the following revised grammar:
        //
        //   IPv6address = hexseq [ ":" IPv4address ]
        //                 | hexseq [ "::" [ hexpost ] ]
        //                 | "::" [ hexpost ]
        //   hexpost     = hexseq | hexseq ":" IPv4address | IPv4address
        //   hexseq      = hex4 *( ":" hex4)
        //   hex4        = 1*4HEXDIG
        //
        // This covers all and only the following cases:
        //
        //   hexseq
        //   hexseq : IPv4address
        //   hexseq ::
        //   hexseq :: hexseq
        //   hexseq :: hexseq : IPv4address
        //   hexseq :: IPv4address
        //   :: hexseq
        //   :: hexseq : IPv4address
        //   :: IPv4address
        //   ::
        //
        // Additionally we constrain the IPv6 address as follows :-
        //
        //  i.  IPv6 addresses without compressed zeros should contain
        //      exactly 16 bytes.
        //
        //  ii. IPv6 addresses with compressed zeros should contain
        //      less than 16 bytes.
        private var ipv6byteCount = 0

        init {
            string = input
        }

        @Throws(URISyntaxException::class)
        fun parseIPv6Reference(start: Int, n: Int): Int {
            var p = start
            val q: Int
            var compressedZeros = false

            q = scanHexSeq(p, n)

            if (q > p) {
                p = q
                if (at(p, n, "::")) {
                    compressedZeros = true
                    p = scanHexPost(p + 2, n)
                } else if (at(p, n, ':')) {
                    p = takeIPv4Address(p + 1, n, "IPv4 address")
                    ipv6byteCount += 4
                }
            } else if (at(p, n, "::")) {
                compressedZeros = true
                p = scanHexPost(p + 2, n)
            }
            if (p < n) fail("Malformed IPv6 address", start)
            if (ipv6byteCount > 16) fail("IPv6 address too long", start)
            if (!compressedZeros && ipv6byteCount < 16) fail("IPv6 address too short", start)
            if (compressedZeros && ipv6byteCount == 16) fail("Malformed IPv6 address", start)

            return p
        }

        @Throws(URISyntaxException::class)
        fun scanHexPost(start: Int, n: Int): Int {
            var p = start
            val q: Int

            if (p == n) return p

            q = scanHexSeq(p, n)
            if (q > p) {
                p = q
                if (at(p, n, ':')) {
                    p++
                    p = takeIPv4Address(p, n, "hex digits or IPv4 address")
                    ipv6byteCount += 4
                }
            } else {
                p = takeIPv4Address(p, n, "hex digits or IPv4 address")
                ipv6byteCount += 4
            }
            return p
        }

        // Scan a hex sequence; return -1 if one could not be scanned
        //
        @Throws(URISyntaxException::class)
        fun scanHexSeq(start: Int, n: Int): Int {
            var p = start
            var q: Int

            q = scan(p, n, L_HEX, H_HEX)
            if (q <= p) return -1
            if (at(q, n, '.'))  // Beginning of IPv4 address
                return -1
            if (q > p + 4) fail("IPv6 hexadecimal digit sequence too long", p)
            ipv6byteCount += 2
            p = q
            while (p < n) {
                if (!at(p, n, ':')) break
                if (at(p + 1, n, ':')) break // "::"

                p++
                q = scan(p, n, L_HEX, H_HEX)
                if (q <= p) failExpecting("digits for an IPv6 address", p)
                if (at(q, n, '.')) {    // Beginning of IPv4 address
                    p--
                    break
                }
                if (q > p + 4) fail("IPv6 hexadecimal digit sequence too long", p)
                ipv6byteCount += 2
                p = q
            }

            return p
        }
    }

    companion object {


        private fun validSchemeAndPath(scheme: String, path: String): Boolean {
            try {
                val u = URI("$scheme:$path")
                return scheme == u._scheme && path == u._path
            } catch (e: URISyntaxException) {
                return false
            }
        }

        /**
         * Creates a URI by parsing the given string.
         *
         *
         *  This convenience factory method works as if by invoking the [ ][.URI] constructor; any [URISyntaxException] thrown by the
         * constructor is caught and wrapped in a new [ ] object, which is then thrown.
         *
         *
         *  This method is provided for use in situations where it is known that
         * the given string is a legal URI, for example for URI constants declared
         * within a program, and so it would be considered a programming error
         * for the string not to parse as such.  The constructors, which throw
         * [URISyntaxException] directly, should be used in situations where a
         * URI is being constructed from user input or from some other source that
         * may be prone to errors.
         *
         * @param  str   The string to be parsed into a URI
         * @return The new URI
         *
         * @throws  NullPointerException
         * If `str` is `null`
         *
         * @throws  IllegalArgumentException
         * If the given string violates RFC&nbsp;2396
         */
        fun create(str: String): URI {
            try {
                return URI(str)
            } catch (x: URISyntaxException) {
                throw IllegalArgumentException(x.message, x)
            }
        }


        // -- End of public methods --
        // -- Utility methods for string-field comparison and hashing --
        // These methods return appropriate values for null string arguments,
        // thereby simplifying the equals, hashCode, and compareTo methods.
        //
        // The case-ignoring methods should only be applied to strings whose
        // characters are all known to be US-ASCII.  Because of this restriction,
        // these methods are faster than the similar methods in the String class.
        // US-ASCII only
        private fun toLower(c: Char): Int {
            if ((c >= 'A') && (c <= 'Z')) return c.code + ('a'.code - 'A'.code)
            return c.code
        }

        // US-ASCII only
        private fun toUpper(c: Char): Int {
            if ((c >= 'a') && (c <= 'z')) return c.code - ('a'.code - 'A'.code)
            return c.code
        }

        private fun equal(s: String?, t: String?): Boolean {
            val testForEquality = true
            val result: Int = percentNormalizedComparison(s, t, testForEquality)
            return result == 0
        }

        // US-ASCII only
        private fun equalIgnoringCase(s: String?, t: String?): Boolean {
            if (s === t) return true
            if ((s != null) && (t != null)) {
                val n = s.length
                if (t.length != n) return false
                for (i in 0..<n) {
                    if (toLower(s[i]) != toLower(t[i])) return false
                }
                return true
            }
            return false
        }

        private fun hash(hash: Int, s: String?): Int {
            if (s == null) return hash
            return if (s.indexOf('%') < 0)
                hash * 127 + s.hashCode()
            else
                normalizedHash(hash, s)
        }


        private fun normalizedHash(hash: Int, s: String): Int {
            var h = 0
            var index = 0
            while (index < s.length) {
                val ch = s[index]
                h = 31 * h + ch.code
                if (ch == '%') {
                    /*
                 * Process the next two encoded characters
                 */
                    for (i in index + 1..<index + 3) h = 31 * h + toUpper(s[i])
                    index += 2
                }
                index++
            }
            return hash * 127 + h
        }

        // US-ASCII only
        private fun hashIgnoringCase(hash: Int, s: String?): Int {
            if (s == null) return hash
            var h = hash
            val n = s.length
            for (i in 0..<n) h = 31 * h + toLower(s[i])
            return h
        }

        private fun compare(s: String?, t: String?): Int {
            val testForEquality = false
            val result: Int = percentNormalizedComparison(s, t, testForEquality)
            return result
        }

        // The percentNormalizedComparison method does not verify two
        // characters that follow the % sign are hexadecimal digits.
        // Reason being:
        // 1) percentNormalizedComparison method is not called with
        // 'decoded' strings
        // 2) The only place where a percent can be followed by anything
        // other than hexadecimal digits is in the authority component
        // (for a IPv6 scope) and the whole authority component is case
        // insensitive.
        private fun percentNormalizedComparison(
            s: String?, t: String?,
            testForEquality: Boolean
        ): Int {
            if (s === t) return 0
            if (s != null) {
                if (t != null) {
                    if (s.indexOf('%') < 0) {
                        return s.compareTo(t)
                    }
                    val sn = s.length
                    val tn = t.length
                    if ((sn != tn) && testForEquality) return sn - tn
                    var `val` = 0
                    val n = min(sn.toDouble(), tn.toDouble()).toInt()
                    var i = 0
                    while (i < n) {
                        val c = s[i]
                        val d = t[i]
                        `val` = c.code - d.code
                        if (c != '%') {
                            if (`val` != 0) return `val`
                            i++
                            continue
                        }
                        if (d != '%') {
                            if (`val` != 0) return `val`
                        }
                        i++
                        `val` = toLower(s[i]) - toLower(t[i])
                        if (`val` != 0) return `val`
                        i++
                        `val` = toLower(s[i]) - toLower(t[i])
                        if (`val` != 0) return `val`
                        i++
                    }
                    return sn - tn
                } else return +1
            } else {
                return -1
            }
        }

        // US-ASCII only
        private fun compareIgnoringCase(s: String?, t: String?): Int {
            if (s === t) return 0
            if (s != null) {
                if (t != null) {
                    val sn = s.length
                    val tn = t.length
                    val n = if (sn < tn) sn else tn
                    for (i in 0..<n) {
                        val c: Int = toLower(s[i]) - toLower(t[i])
                        if (c != 0) return c
                    }
                    return sn - tn
                }
                return +1
            } else {
                return -1
            }
        }


        // -- String construction --
        // If a scheme is given then the path, if given, must be absolute
        private fun checkPath(s: String, scheme: String?, path: String?) {
            if (scheme != null) {
                if (path != null && !path.isEmpty() && path[0] != '/') throw URISyntaxException(s, "Relative path in absolute URI")
            }
        }

        // -- Normalization, resolution, and relativization --
        // RFC2396 5.2 (6)
        private fun resolvePath(base: String, child: String, absolute: Boolean): String {
            val i = base.lastIndexOf('/')
            val cn = child.length
            var path = ""

            if (cn == 0) {
                // 5.2 (6a)
                if (i >= 0) path = base.substring(0, i + 1)
            } else {
                // 5.2 (6a-b)
                path = if (i >= 0 || !absolute) {
                    base.substring(0, i + 1) + child
                } else {
                    "/$child"
                }
            }

            // 5.2 (6c-f)
            val np: String = normalize(path)

            // 5.2 (6g): If the result is absolute but the path begins with "../",
            // then we simply leave the path as-is
            return np
        }

        // RFC2396 5.2
        private fun resolve(base: URI, child: URI): URI {
            // check if child if opaque first so that NPE is thrown
            // if child is null.
            if (child.isOpaque() || base.isOpaque()) return child

            // 5.2 (2): Reference to current document (lone fragment)
            if ((child._scheme == null) && (child._authoriy == null)
                && child._path!!.isEmpty() && (child._fragment != null)
                && (child._query == null)
            ) {
                if ((base._fragment != null)
                    && child._fragment == base._fragment
                ) {
                    return base
                }
                val ru = URI()
                ru._scheme = base._scheme
                ru._authoriy = base._authoriy
                ru._userInfo = base._userInfo
                ru._host = base._host
                ru._port = base._port
                ru._path = base._path
                ru._fragment = child._fragment
                ru._query = base._query
                return ru
            }

            // 5.2 (3): Child is absolute
            if (child._scheme != null) return child

            val ru = URI() // Resolved URI
            ru._scheme = base._scheme
            ru._query = child._query
            ru._fragment = child._fragment

            // 5.2 (4): Authority
            if (child._authoriy == null) {
                ru._authoriy = base._authoriy
                ru._host = base._host
                ru._userInfo = base._userInfo
                ru._port = base._port

                val cp = child._path
                if (!cp!!.isEmpty() && cp[0] == '/') {
                    // 5.2 (5): Child path is absolute
                    ru._path = child._path
                } else {
                    // 5.2 (6): Resolve relative path
                    ru._path = resolvePath(base._path!!, cp, base.isAbsolute())
                }
            } else {
                ru._authoriy = child._authoriy
                ru._host = child._host
                ru._userInfo = child._userInfo
                ru._port = child._port
                ru._path = child._path
            }

            // 5.2 (7): Recombine (nothing to do here)
            return ru
        }

        // If the given URI's path is normal then return the URI;
        // o.w., return a new URI containing the normalized path.
        //
        private fun normalize(u: URI): URI {
            if (u.isOpaque() || u._path == null || u._path!!.isEmpty()) return u

            val np: String = normalize(u._path!!)
            if (np === u._path) return u

            val v = URI()
            v._scheme = u._scheme
            v._fragment = u._fragment
            v._authoriy = u._authoriy
            v._userInfo = u._userInfo
            v._host = u._host
            v._port = u._port
            v._path = np
            v._query = u._query
            return v
        }

        // If both URIs are hierarchical, their scheme and authority components are
        // identical, and the base path is a prefix of the child's path, then
        // return a relative URI that, when resolved against the base, yields the
        // child; otherwise, return the child.
        //
        private fun relativize(base: URI, child: URI): URI {
            // check if child if opaque first so that NPE is thrown
            // if child is null.
            if (child.isOpaque() || base.isOpaque()) return child
            if (!equalIgnoringCase(base._scheme, child._scheme)
                || !equal(base._authoriy, child._authoriy)
            ) return child

            var bp: String = normalize(base._path!!)
            val cp: String = normalize(child._path!!)
            if (bp != cp) {
                if (!bp.endsWith("/")) bp = "$bp/"
                if (!cp.startsWith(bp)) return child
            }

            val v = URI()
            v._path = cp.substring(bp.length)
            v._query = child._query
            v._fragment = child._fragment
            return v
        }


        // -- Path normalization --
        // The following algorithm for path normalization avoids the creation of a
        // string object for each segment, as well as the use of a string buffer to
        // compute the final result, by using a single char array and editing it in
        // place.  The array is first split into segments, replacing each slash
        // with '\0' and creating a segment-index array, each element of which is
        // the index of the first char in the corresponding segment.  We then walk
        // through both arrays, removing ".", "..", and other segments as necessary
        // by setting their entries in the index array to -1.  Finally, the two
        // arrays are used to rejoin the segments and compute the final result.
        //
        // This code is based upon src/solaris/native/java/io/canonicalize_md.c
        // Check the given path to see if it might need normalization.  A path
        // might need normalization if it contains duplicate slashes, a "."
        // segment, or a ".." segment.  Return -1 if no further normalization is
        // possible, otherwise return the number of segments found.
        //
        // This method takes a string argument rather than a char array so that
        // this test can be performed without invoking path.toCharArray().
        //
        private fun needsNormalization(path: String): Int {
            var normal = true
            var ns = 0 // Number of segments
            val end = path.length - 1 // Index of last char in path
            var p = 0 // Index of next char in path

            // Skip initial slashes
            while (p <= end) {
                if (path[p] != '/') break
                p++
            }
            if (p > 1) normal = false

            // Scan segments
            while (p <= end) {
                // Looking at "." or ".." ?

                if ((path[p] == '.')
                    && ((p == end)
                            || ((path[p + 1] == '/')
                            || ((path[p + 1] == '.')
                            && ((p + 1 == end)
                            || (path[p + 2] == '/')))))
                ) {
                    normal = false
                }
                ns++

                // Find beginning of next segment
                while (p <= end) {
                    if (path[p++] != '/') continue

                    // Skip redundant slashes
                    while (p <= end) {
                        if (path[p] != '/') break
                        normal = false
                        p++
                    }

                    break
                }
            }

            return if (normal) -1 else ns
        }


        // Split the given path into segments, replacing slashes with nulls and
        // filling in the given segment-index array.
        //
        // Preconditions:
        //   segs.length == Number of segments in path
        //
        // Postconditions:
        //   All slashes in path replaced by '\0'
        //   segs[i] == Index of first char in segment i (0 <= i < segs.length)
        //
        private fun split(path: CharArray, segs: IntArray) {
            val end = path.size - 1 // Index of last char in path
            var p = 0 // Index of next char in path
            var i = 0 // Index of current segment

            // Skip initial slashes
            while (p <= end) {
                if (path[p] != '/') break
                path[p] = '\u0000'
                p++
            }

            while (p <= end) {
                // Note start of segment

                segs[i++] = p++

                // Find beginning of next segment
                while (p <= end) {
                    if (path[p++] != '/') continue
                    path[p - 1] = '\u0000'

                    // Skip redundant slashes
                    while (p <= end) {
                        if (path[p] != '/') break
                        path[p++] = '\u0000'
                    }
                    break
                }
            }

            if (i != segs.size) throw Exception("Internal error") // ASSERT
        }


        // Join the segments in the given path according to the given segment-index
        // array, ignoring those segments whose index entries have been set to -1,
        // and inserting slashes as needed.  Return the length of the resulting
        // path.
        //
        // Preconditions:
        //   segs[i] == -1 implies segment i is to be ignored
        //   path computed by split, as above, with '\0' having replaced '/'
        //
        // Postconditions:
        //   path[0] .. path[return value] == Resulting path
        //
        private fun join(path: CharArray, segs: IntArray): Int {
            val ns = segs.size // Number of segments
            val end = path.size - 1 // Index of last char in path
            var p = 0 // Index of next path char to write

            if (path[p] == '\u0000') {
                // Restore initial slash for absolute paths
                path[p++] = '/'
            }

            for (i in 0..<ns) {
                var q = segs[i] // Current segment
                if (q == -1)  // Ignore this segment
                    continue

                if (p == q) {
                    // We're already at this segment, so just skip to its end
                    while ((p <= end) && (path[p] != '\u0000')) p++
                    if (p <= end) {
                        // Preserve trailing slash
                        path[p++] = '/'
                    }
                } else if (p < q) {
                    // Copy q down to p
                    while ((q <= end) && (path[q] != '\u0000')) path[p++] = path[q++]
                    if (q <= end) {
                        // Preserve trailing slash
                        path[p++] = '/'
                    }
                } else throw Exception("Internal error") // ASSERT false
            }

            return p
        }


        // Remove "." segments from the given path, and remove segment pairs
        // consisting of a non-".." segment followed by a ".." segment.
        //
        private fun removeDots(path: CharArray, segs: IntArray) {
            val ns = segs.size
            val end = path.size - 1

            var i = 0
            while (i < ns) {
                var dots = 0 // Number of dots found (0, 1, or 2)

                // Find next occurrence of "." or ".."
                do {
                    val p = segs[i]
                    if (path[p] == '.') {
                        if (p == end) {
                            dots = 1
                            break
                        } else if (path[p + 1] == '\u0000') {
                            dots = 1
                            break
                        } else if ((path[p + 1] == '.')
                            && ((p + 1 == end)
                                    || (path[p + 2] == '\u0000'))
                        ) {
                            dots = 2
                            break
                        }
                    }
                    i++
                } while (i < ns)
                if ((i > ns) || (dots == 0)) break

                if (dots == 1) {
                    // Remove this occurrence of "."
                    segs[i] = -1
                } else {
                    // If there is a preceding non-".." segment, remove both that
                    // segment and this occurrence of ".."; otherwise, leave this
                    // ".." segment as-is.
                    var j: Int
                    j = i - 1
                    while (j >= 0) {
                        if (segs[j] != -1) break
                        j--
                    }
                    if (j >= 0) {
                        val q = segs[j]
                        if (!((path[q] == '.')
                                    && (path[q + 1] == '.')
                                    && (path[q + 2] == '\u0000'))
                        ) {
                            segs[i] = -1
                            segs[j] = -1
                        }
                    }
                }
                i++
            }
        }


        // DEVIATION: If the normalized path is relative, and if the first
        // segment could be parsed as a scheme name, then prepend a "." segment
        //
        private fun maybeAddLeadingDot(path: CharArray, segs: IntArray) {
            if (path[0] == '\u0000')  // The path is absolute
                return

            val ns = segs.size
            var f = 0 // Index of first segment
            while (f < ns) {
                if (segs[f] >= 0) break
                f++
            }
            if ((f >= ns) || (f == 0))  // The path is empty, or else the original first segment survived,
            // in which case we already know that no leading "." is needed
                return

            var p = segs[f]
            while ((p < path.size) && (path[p] != ':') && (path[p] != '\u0000')) p++
            if (p >= path.size || path[p] == '\u0000')  // No colon in first segment, so no "." needed
                return

            // At this point we know that the first segment is unused,
            // hence we can insert a "." segment at that position
            path[0] = '.'
            path[1] = '\u0000'
            segs[0] = 0
        }


        // Normalize the given path string.  A normal path string has no empty
        // segments (i.e., occurrences of "//"), no segments equal to ".", and no
        // segments equal to ".." that are preceded by a segment not equal to "..".
        // In contrast to Unix-style pathname normalization, for URI paths we
        // always retain trailing slashes.
        //
        private fun normalize(ps: String): String {
            // Does this path need normalization?

            val ns: Int = needsNormalization(ps) // Number of segments
            if (ns < 0)  // Nope -- just return it
                return ps

            val path = ps.toCharArray() // Path in char-array form

            // Split path into segments
            val segs = IntArray(ns) // Segment-index array
            split(path, segs)

            // Remove dots
            removeDots(path, segs)

            // Prevent scheme-name confusion
            maybeAddLeadingDot(path, segs)

            // Join the remaining segments and return the result
            val s = path.concatToString(0, join(path, segs))
            if (s == ps) {
                // string was already normalized
                return ps
            }
            return s
        }


        // -- Character classes for parsing --
        // RFC2396 precisely specifies which characters in the US-ASCII charset are
        // permissible in the various components of a URI reference.  We here
        // define a set of mask pairs to aid in enforcing these restrictions.  Each
        // mask pair consists of two longs, a low mask and a high mask.  Taken
        // together they represent a 128-bit mask, where bit i is set iff the
        // character with value i is permitted.
        //
        // This approach is more efficient than sequentially searching arrays of
        // permitted characters.  It could be made still more efficient by
        // precompiling the mask information so that a character's presence in a
        // given mask could be determined by a single table lookup.
        // To save startup time, we manually calculate the low-/highMask constants.
        // For reference, the following methods were used to calculate the values:
        // Compute the low-order mask for the characters in the given string
        //     private static long lowMask(String chars) {
        //        int n = chars.length();
        //        long m = 0;
        //        for (int i = 0; i < n; i++) {
        //            char c = chars.charAt(i);
        //            if (c < 64)
        //                m |= (1L << c);
        //        }
        //        return m;
        //    }
        // Compute the high-order mask for the characters in the given string
        //    private static long highMask(String chars) {
        //        int n = chars.length();
        //        long m = 0;
        //        for (int i = 0; i < n; i++) {
        //            char c = chars.charAt(i);
        //            if ((c >= 64) && (c < 128))
        //                m |= (1L << (c - 64));
        //        }
        //        return m;
        //    }
        // Compute a low-order mask for the characters
        // between first and last, inclusive
        //    private static long lowMask(char first, char last) {
        //        long m = 0;
        //        int f = Math.max(Math.min(first, 63), 0);
        //        int l = Math.max(Math.min(last, 63), 0);
        //        for (int i = f; i <= l; i++)
        //            m |= 1L << i;
        //        return m;
        //    }
        // Compute a high-order mask for the characters
        // between first and last, inclusive
        //    private static long highMask(char first, char last) {
        //        long m = 0;
        //        int f = Math.max(Math.min(first, 127), 64) - 64;
        //        int l = Math.max(Math.min(last, 127), 64) - 64;
        //        for (int i = f; i <= l; i++)
        //            m |= 1L << i;
        //        return m;
        //    }
        // Tell whether the given character is permitted by the given mask pair
        private fun match(c: Char, lowMask: Long, highMask: Long): Boolean {
            if (c.code == 0)  // 0 doesn't have a slot in the mask. So, it never matches.
                return false
            if (c.code < 64) return ((1L shl c.code) and lowMask) != 0L
            if (c.code < 128) return ((1L shl (c.code - 64)) and highMask) != 0L
            return false
        }

        // Character-class masks, in reverse order from RFC2396 because
        // initializers for static fields cannot make forward references.
        // digit    = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" |
        //            "8" | "9"
        private const val L_DIGIT = 0x3FF000000000000L // lowMask('0', '9');
        private const val H_DIGIT = 0L

        // upalpha  = "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" |
        //            "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" |
        //            "S" | "T" | "U" | "V" | "W" | "X" | "Y" | "Z"
        private const val L_UPALPHA = 0L
        private const val H_UPALPHA = 0x7FFFFFEL // highMask('A', 'Z');

        // lowalpha = "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" |
        //            "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" |
        //            "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z"
        private const val L_LOWALPHA = 0L
        private const val H_LOWALPHA = 0x7FFFFFE00000000L // highMask('a', 'z');

        // alpha         = lowalpha | upalpha
        private val L_ALPHA: Long = L_LOWALPHA or L_UPALPHA
        private val H_ALPHA: Long = H_LOWALPHA or H_UPALPHA

        // alphanum      = alpha | digit
        private val L_ALPHANUM: Long = L_DIGIT or L_ALPHA
        private val H_ALPHANUM: Long = H_DIGIT or H_ALPHA

        // hex           = digit | "A" | "B" | "C" | "D" | "E" | "F" |
        //                         "a" | "b" | "c" | "d" | "e" | "f"
        private val L_HEX: Long = L_DIGIT
        private const val H_HEX = 0x7E0000007EL // highMask('A', 'F') | highMask('a', 'f');

        // mark          = "-" | "_" | "." | "!" | "~" | "*" | "'" |
        //                 "(" | ")"
        private const val L_MARK = 0x678200000000L // lowMask("-_.!~*'()");
        private const val H_MARK = 0x4000000080000000L // highMask("-_.!~*'()");

        // unreserved    = alphanum | mark
        private val L_UNRESERVED: Long = L_ALPHANUM or L_MARK
        private val H_UNRESERVED: Long = H_ALPHANUM or H_MARK

        // reserved      = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" |
        //                 "$" | "," | "[" | "]"
        // Added per RFC2732: "[", "]"
        private const val L_RESERVED = -0x53ff67b000000000L // lowMask(";/?:@&=+$,[]");
        private const val H_RESERVED = 0x28000001L // highMask(";/?:@&=+$,[]");

        // The zero'th bit is used to indicate that escape pairs and non-US-ASCII
        // characters are allowed; this is handled by the scanEscape method below.
        private const val L_ESCAPED = 1L
        private const val H_ESCAPED = 0L

        // uric          = reserved | unreserved | escaped
        private val L_URIC: Long = L_RESERVED or L_UNRESERVED or L_ESCAPED
        private val H_URIC: Long = H_RESERVED or H_UNRESERVED or H_ESCAPED

        // pchar         = unreserved | escaped |
        //                 ":" | "@" | "&" | "=" | "+" | "$" | ","
        private val L_PCHAR
                : Long = L_UNRESERVED or L_ESCAPED or 0x2400185000000000L // lowMask(":@&=+$,");
        private val H_PCHAR
                : Long = H_UNRESERVED or H_ESCAPED or 0x1L // highMask(":@&=+$,");

        // All valid path characters
        private val L_PATH: Long = L_PCHAR or 0x800800000000000L // lowMask(";/");
        private val H_PATH: Long = H_PCHAR // highMask(";/") == 0x0L;

        // Dash, for use in domainlabel and toplabel
        private const val L_DASH = 0x200000000000L // lowMask("-");
        private const val H_DASH = 0x0L // highMask("-");

        // Dot, for use in hostnames
        private const val L_DOT = 0x400000000000L // lowMask(".");
        private const val H_DOT = 0x0L // highMask(".");

        // userinfo      = *( unreserved | escaped |
        //                    ";" | ":" | "&" | "=" | "+" | "$" | "," )
        private val L_USERINFO
                : Long = L_UNRESERVED or L_ESCAPED or 0x2C00185000000000L // lowMask(";:&=+$,");
        private val H_USERINFO
                : Long = H_UNRESERVED or H_ESCAPED // | highMask(";:&=+$,") == 0L;

        // reg_name      = 1*( unreserved | escaped | "$" | "," |
        //                     ";" | ":" | "@" | "&" | "=" | "+" )
        private val L_REG_NAME
                : Long = L_UNRESERVED or L_ESCAPED or 0x2C00185000000000L // lowMask("$,;:@&=+");
        private val H_REG_NAME
                : Long = H_UNRESERVED or H_ESCAPED or 0x1L // highMask("$,;:@&=+");

        // All valid characters for server-based authorities
        private val L_SERVER
                : Long = L_USERINFO or L_ALPHANUM or L_DASH or 0x400400000000000L // lowMask(".:@[]");
        private val H_SERVER
                : Long = H_USERINFO or H_ALPHANUM or H_DASH or 0x28000001L // highMask(".:@[]");

        // Special case of server authority that represents an IPv6 address
        // In this case, a % does not signify an escape sequence
        private val L_SERVER_PERCENT
                : Long = L_SERVER or 0x2000000000L // lowMask("%");
        private val H_SERVER_PERCENT
                : Long = H_SERVER // | highMask("%") == 0L;

        // scheme        = alpha *( alpha | digit | "+" | "-" | "." )
        private val L_SCHEME: Long = L_ALPHA or L_DIGIT or 0x680000000000L // lowMask("+-.");
        private val H_SCHEME: Long = H_ALPHA or H_DIGIT // | highMask("+-.") == 0L

        // scope_id = alpha | digit | "_" | "."
        private val L_SCOPE_ID
                : Long = L_ALPHANUM or 0x400000000000L // lowMask("_.");
        private val H_SCOPE_ID
                : Long = H_ALPHANUM or 0x80000000L // highMask("_.");

        // -- Escaping and encoding --
        private val hexDigits = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        )

        private fun appendEscape(sb: StringBuilder, b: Byte) {
            sb.append('%')
            sb.append(hexDigits[(b.toInt() shr 4) and 0x0f])
            sb.append(hexDigits[(b.toInt() shr 0) and 0x0f])
        }

        private fun appendEncoded(encoder: CharsetEncoder, sb: StringBuilder, c: Char) {
            var bb: ByteBuffer? = null
            try {
                bb = encoder.encode(CharBufferFactory.wrap(charArrayOf(c)))
            } catch (x: CharacterCodingException) {
                assert(false)
            }
            while (bb!!.hasRemaining()) {
                val b = bb.get().toInt() and 0xff
                if (b >= 0x80) appendEscape(sb, b.toByte())
                else sb.append(b.toChar())
            }
        }

        // Quote any characters in s that are not permitted
        // by the given mask pair
        //
        private fun quote(s: String, lowMask: Long, highMask: Long): String {
            var sb: StringBuilder? = null
            var encoder: CharsetEncoder? = null
            val allowNonASCII = ((lowMask and L_ESCAPED) != 0L)
            for (i in 0..<s.length) {
                val c: Char = s[i]
                if (c < '\u0080') {
                    if (!match(c, lowMask, highMask)) {
                        if (sb == null) {
                            sb = StringBuilder()
                            sb.append(s, 0, i)
                        }
                        appendEscape(sb, c.code.toByte())
                    } else {
                        sb?.append(c)
                    }
                } else if (allowNonASCII
                    && (Character.isSpaceChar(c.code)
                            || c.isISOControl())
                ) {
                    if (encoder == null) encoder = Charsets.UTF8.newEncoder()
                    if (sb == null) {
                        sb = StringBuilder()
                        sb.append(s, 0, i)
                    }
                    appendEncoded(encoder, sb, c)
                } else {
                    sb?.append(c)
                }
            }
            return sb?.toString() ?: s
        }

        private fun decode(c: Char): Int {
            if ((c >= '0') && (c <= '9')) return c.code - '0'.code
            if ((c >= 'a') && (c <= 'f')) return c.code - 'a'.code + 10
            if ((c >= 'A') && (c <= 'F')) return c.code - 'A'.code + 10
            assert(false)
            return -1
        }

        private fun decode(c1: Char, c2: Char): Byte {
            return (((decode(c1) and 0xf) shl 4)
                    or ((decode(c2) and 0xf) shl 0)).toByte()
        }

        // This method was introduced as a generalization of URI.decode method
        // to provide a fix for JDK-8037396
        // Evaluates all escapes in s, applying UTF-8 decoding if needed.  Assumes
        // that escapes are well-formed syntactically, i.e., of the form %XX.  If a
        // sequence of escaped octets is not valid UTF-8 then the erroneous octets
        // are replaced with '\uFFFD'.
        // Exception: any "%" found between "[]" is left alone. It is an IPv6 literal
        //            with a scope_id
        //
        private fun decode(s: String?, ignorePercentInBrackets: Boolean = true): String? {
            if (s == null) return s
            val n = s.length
            if (n == 0) return s
            if (s.indexOf('%') < 0) return s

            val sb = StringBuilder(n)
            val bb = ByteBufferFactory.allocate(n)
            val cb = CharBufferFactory.allocate(n)
            val dec = Charsets.UTF8.newDecoder()
                .onMalformedInput(CodingErrorActionValue.REPLACE)
                .onUnmappableCharacter(CodingErrorActionValue.REPLACE)

            // This is not horribly efficient, but it will do for now
            var c = s[0]
            var betweenBrackets = false

            var i = 0
            while (i < n) {
                assert(
                    c == s[i] // Loop invariant
                )
                if (c == '[') {
                    betweenBrackets = true
                } else if (betweenBrackets && c == ']') {
                    betweenBrackets = false
                }
                if (c != '%' || (betweenBrackets && ignorePercentInBrackets)) {
                    sb.append(c)
                    if (++i >= n) break
                    c = s[i]
                    continue
                }
                bb.clearExt()
                while (true) {
                    assert(n - i >= 2)
                    bb.put(decode(s[++i], s[++i]))
                    if (++i >= n) break
                    c = s[i]
                    if (c != '%') break
                }
                bb.flipExt()
                cb.clearExt()
                dec.reset()
                var cr = dec.decode(bb, cb, true)
                assert(cr.isUnderflow())
                cr = dec.flush(cb)
                assert(cr.isUnderflow())
                sb.append(cb.flipExt().toString())
            }

            return sb.toString()
        }
    }
}