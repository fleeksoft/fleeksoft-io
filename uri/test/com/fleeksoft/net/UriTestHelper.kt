@file:OptIn(ExperimentalStdlibApi::class)

package com.fleeksoft.net

import com.fleeksoft.charset.Platform
import com.fleeksoft.charset.isJvmOrAndroid
import com.fleeksoft.io.exception.URISyntaxException

class UriTestHelper {

    var input: String? = null
    var _uri: URI? = null
    var originalURI: URI?
    var base: URI? = null // Base for resolution/relativization
    var op: String? = null // Op performed if uri != originalURI
    var checked: Int = 0 // Mask for checked properties
    var failed: Int = 0 // Mask for failed properties
    var exc: Exception? = null

    private constructor(s: String) {
        testCount++
        input = s
        try {
            _uri = URI(s)
        } catch (x: URISyntaxException) {
            exc = x
        }
        originalURI = _uri
    }

    private constructor(
        s: String?, u: String?, h: String?, n: Int,
        p: String?, q: String?, f: String?
    ) {
        testCount++
        try {
            _uri = URI(s, u, h, n, p, q, f)
        } catch (x: URISyntaxException) {
            exc = x
            input = x.getInput()
        }
        if (_uri != null) input = _uri!!.toString()
        originalURI = _uri
    }

    private constructor(
        s: String?, a: String?,
        p: String?, q: String?, f: String?
    ) {
        testCount++
        try {
            _uri = URI(s, a, p, q, f)
        } catch (x: URISyntaxException) {
            exc = x
            input = x.getInput()
        }
        if (_uri != null) input = _uri!!.toString()
        originalURI = _uri
    }

    private constructor(s: String?, h: String?, p: String?, f: String?) {
        testCount++
        try {
            _uri = URI(s, h, p, f)
        } catch (x: URISyntaxException) {
            exc = x
            input = x.getInput()
        }
        if (_uri != null) input = _uri!!.toString()
        originalURI = _uri
    }

    private constructor(s: String?, ssp: String?, f: String?) {
        testCount++
        try {
            _uri = URI(s, ssp, f)
        } catch (x: URISyntaxException) {
            exc = x
            input = x.getInput()
        }
        if (_uri != null) input = _uri!!.toString()
        originalURI = _uri
    }

    private constructor(s: String, xxx: Boolean) {
        testCount++
        try {
            _uri = URIFactory.create(s)
        } catch (x: IllegalArgumentException) {
            exc = x
        }
        if (_uri != null) input = _uri!!.toString()
        originalURI = _uri
    }

    fun parsed(): Boolean {
        return _uri != null
    }

    fun resolved(): Boolean {
        return base != null
    }

    fun uri(): URI {
        return _uri!!
    }


    // Operations on Test instances
    //
    // These are short so as to make test cases compact.
    //
    //    s      Scheme
    //    sp     Scheme-specific part
    //    spd    Scheme-specific part, decoded
    //    o      Opaque part (isOpaque() && ssp matches)
    //    g      reGistry (authority matches, and host is not defined)
    //    gd     reGistry, decoded
    //    u      User info
    //    ud     User info, decoded
    //    h      Host
    //    n      port Number
    //    p      Path
    //    pd     Path, decoded
    //    q      Query
    //    qd     Query, decoded
    //    f      Fragment
    //    fd     Fragment, decoded
    //
    //    rslv   Resolve against given base
    //    rtvz   Relativize
    //    psa    Parse server Authority
    //    norm   Normalize
    //    ta     ASCII form
    //
    //    x      Check that parse failed as expected
    //    z      End -- ensure that unchecked components are null
    private fun check1(prop: Int): Boolean {
        checked = checked or prop
        if (!parsed()) {
            failed = failed or prop
            return false
        }
        return true
    }

    private fun check2(s: String?, ans: String?, prop: Int) {
        if ((s == null) || s != ans) failed = failed or prop
    }

    fun s(s: String?): UriTestHelper {
        if (check1(SCHEME)) check2(_uri!!.getScheme(), s, SCHEME)
        return this
    }

    fun u(s: String?): UriTestHelper {
        if (check1(USERINFO)) check2(_uri!!.getRawUserInfo(), s, USERINFO)
        return this
    }

    fun ud(s: String?): UriTestHelper {
        if (check1(USERINFO_D)) {
            check2(_uri!!.getUserInfo(), s, USERINFO_D)
        }
        return this
    }

    fun h(s: String?): UriTestHelper {
        if (check1(HOST)) check2(_uri!!.getHost(), s, HOST)
        return this
    }

    fun g(s: String?): UriTestHelper {
        if (check1(REGISTRY)) {
            if (_uri!!.getHost() != null) failed = failed or REGISTRY
            else check2(_uri!!.getRawAuthority(), s, REGISTRY)
        }
        return this
    }

    fun gd(s: String?): UriTestHelper {
        if (check1(REGISTRY_D)) {
            if (_uri!!.getHost() != null) failed = failed or REGISTRY_D
            else check2(_uri!!.getAuthority(), s, REGISTRY_D)
        }
        return this
    }

    fun n(n: Int): UriTestHelper {
        checked = checked or PORT
        if (!parsed() || (_uri!!.getPort() != n)) failed = failed or PORT
        return this
    }

    fun p(s: String?): UriTestHelper {
        if (check1(PATH)) check2(_uri!!.getRawPath(), s, PATH)
        return this
    }

    fun pd(s: String?): UriTestHelper {
        if (check1(PATH_D)) check2(_uri!!.getPath(), s, PATH_D)
        return this
    }

    fun o(s: String?): UriTestHelper {
        if (check1(OPAQUEPART)) {
            if (!_uri!!.isOpaque()) failed = failed or OPAQUEPART
            else check2(_uri!!.getSchemeSpecificPart(), s, OPAQUEPART)
        }
        return this
    }

    fun sp(s: String?): UriTestHelper {
        if (check1(SSP)) check2(_uri!!.getRawSchemeSpecificPart(), s, SSP)
        return this
    }

    fun spd(s: String?): UriTestHelper {
        if (check1(SSP_D)) check2(_uri!!.getSchemeSpecificPart(), s, SSP_D)
        return this
    }

    fun q(s: String?): UriTestHelper {
        if (check1(QUERY)) check2(_uri!!.getRawQuery(), s, QUERY)
        return this
    }

    fun qd(s: String?): UriTestHelper {
        if (check1(QUERY_D)) check2(_uri!!.getQuery(), s, QUERY_D)
        return this
    }

    fun f(s: String?): UriTestHelper {
        if (check1(FRAGMENT)) check2(_uri!!.getRawFragment(), s, FRAGMENT)
        return this
    }

    fun fd(s: String?): UriTestHelper {
        if (check1(FRAGMENT_D)) check2(_uri!!.getFragment(), s, FRAGMENT_D)
        return this
    }

    fun ts(s: String?): UriTestHelper {
        if (check1(TOSTRING)) check2(_uri!!.toString(), s, TOSTRING)
        return this
    }

    fun x(): UriTestHelper {
        checked = checked or PARSEFAIL
        if (parsed()) failed = failed or PARSEFAIL
        return this
    }

    fun rslv(base: URI): UriTestHelper {
        if (!parsed()) return this
        this.base = base
        op = "rslv"
        val u = _uri!!
        _uri = null
        try {
            this._uri = base.resolve(u)
        } catch (x: IllegalArgumentException) {
            exc = x
        }
        checked = 0
        failed = 0
        return this
    }

    fun norm(): UriTestHelper {
        if (!parsed()) return this
        op = "norm"
        _uri = _uri!!.normalize()
        return this
    }

    fun rtvz(base: URI): UriTestHelper {
        if (!parsed()) return this
        this.base = base
        op = "rtvz"
        _uri = base.relativize(_uri!!)
        checked = 0
        failed = 0
        return this
    }

    fun psa(): UriTestHelper {
        try {
            _uri!!.parseServerAuthority()
        } catch (x: URISyntaxException) {
            exc = x
            _uri = null
        }
        checked = 0
        failed = 0
        return this
    }

    private fun checkEmpty(s: String?, prop: Int) {
        if (((checked and prop) == 0) && (s != null)) failed = failed or prop
    }

    // Check identity for the seven-argument URI constructor
    //
    fun checkURI7() {
        // Only works on hierarchical URIs
        if (_uri!!.isOpaque()) return
        // Only works with server-based authorities
        if ((_uri!!.getAuthority() == null)
            != ((_uri!!.getUserInfo() == null) && (_uri!!.getHost() == null))
        ) return
        // Not true if non-US-ASCII chars are encoded unnecessarily
        if (_uri!!.getPath()!!.indexOf('\u20AC') >= 0) return
        try {
            val u2 = URI(
                _uri!!.getScheme(), _uri!!.getUserInfo(),
                _uri!!.getHost(), _uri!!.getPort(), _uri!!.getPath(),
                _uri!!.getQuery(), _uri!!.getFragment()
            )
            if (_uri!! != u2) failed = failed or IDENT_URI7
        } catch (x: URISyntaxException) {
            failed = failed or IDENT_URI7
        }
    }

    // Check identity for the five-argument URI constructor
    //
    fun checkURI5() {
        // Only works on hierarchical URIs
        if (_uri!!.isOpaque()) return
        try {
            val u2 = URI(
                _uri!!.getScheme(), _uri!!.getAuthority(),
                _uri!!.getPath(), _uri!!.getQuery(), _uri!!.getFragment()
            )
            if (_uri!! != u2) failed = failed or IDENT_URI5
        } catch (x: URISyntaxException) {
            failed = failed or IDENT_URI5
        }
    }

    // Check identity for the three-argument URI constructor
    //
    fun checkURI3() {
        try {
            val u2 = URI(
                _uri!!.getScheme(),
                _uri!!.getSchemeSpecificPart(),
                _uri!!.getFragment()
            )
            if (_uri!! != u2) failed = failed or IDENT_URI3
        } catch (x: URISyntaxException) {
            failed = failed or IDENT_URI3
        }
    }

    // Check all identities mentioned in the URI class specification
    //
    fun checkIdentities() {
        if (input != null) {
            if (_uri!!.toString() != input) failed = failed or IDENT_STR
        }
        try {
            if ((URI(_uri!!.toString())) != _uri) failed = failed or IDENT_URI1
        } catch (x: URISyntaxException) {
            failed = failed or IDENT_URI1
        }

        // Remaining identities fail if "//" given but authority is undefined
        if ((_uri!!.getAuthority() == null)
            && (_uri!!.getSchemeSpecificPart() != null)
            && (_uri!!.getSchemeSpecificPart()!!.startsWith("///")
                    || _uri!!.getSchemeSpecificPart()!!.startsWith("//?")
                    || _uri!!.getSchemeSpecificPart().equals("//"))
        ) return

        // Remaining identities fail if ":" given but port is undefined
        if ((_uri!!.getHost() != null)
            && (_uri!!.getAuthority() != null)
            && (_uri!!.getAuthority().equals(_uri!!.getHost() + ":"))
        ) return

        // Remaining identities fail if non-US-ASCII chars are encoded
        // unnecessarily
        if ((_uri!!.getPath() != null) && _uri!!.getPath()!!.indexOf('\u20AC') >= 0) return

        checkURI3()
        checkURI5()
        checkURI7()
    }

    // Check identities, check that unchecked component properties are not
    // defined, and report any failures
    //
    fun z(): UriTestHelper {
        if (!parsed()) {
            report()
            return this
        }

        if (op == null) checkIdentities()

        // Check that unchecked components are undefined
        checkEmpty(_uri!!.getScheme(), SCHEME)
        checkEmpty(_uri!!.getUserInfo(), USERINFO)
        checkEmpty(_uri!!.getHost(), HOST)
        if (((checked and PORT) == 0) && (_uri!!.getPort() != -1)) failed = failed or PORT
        checkEmpty(_uri!!.getPath(), PATH)
        checkEmpty(_uri!!.getQuery(), QUERY)
        checkEmpty(_uri!!.getFragment(), FRAGMENT)

        // Report failures
        report()
        return this
    }


    private fun summarize() {
        val sb = StringBuilder() // TODO: replace StringBuilder with StringBuffer when StringBuffer available
        if (input!!.isEmpty()) sb.append("\"\"")
        else sb.append(input)
        if (base != null) {
            sb.append(" ")
            sb.append(base)
        }
        if (!parsed()) {
            val s = (if ((checked and PARSEFAIL) != 0) "Correct exception" else "UNEXPECTED EXCEPTION")
            if (exc is URISyntaxException) show(s, exc as URISyntaxException)
            else {
                println(uquote(sb.toString()))
                print("$s: ")
                exc!!.printStackTrace()
            }
        } else {
            if (_uri != originalURI) {
                sb.append(" ")
                sb.append(op)
                sb.append(" --> ")
                sb.append(_uri)
            }
            println(uquote(sb.toString()))
        }
    }

    private fun report() {
        summarize()
        if (failed == 0) return
        val sb = StringBuilder() // TODO: replace StringBuilder with StringBuffer when StringBuffer available
        sb.append("FAIL:")
        if ((failed and PARSEFAIL) != 0) sb.append(" parsefail")
        if ((failed and SCHEME) != 0) sb.append(" scheme")
        if ((failed and SSP) != 0) sb.append(" ssp")
        if ((failed and OPAQUEPART) != 0) sb.append(" opaquepart")
        if ((failed and USERINFO) != 0) sb.append(" userinfo")
        if ((failed and USERINFO_D) != 0) sb.append(" userinfod")
        if ((failed and HOST) != 0) sb.append(" host")
        if ((failed and PORT) != 0) sb.append(" port")
        if ((failed and REGISTRY) != 0) sb.append(" registry")
        if ((failed and PATH) != 0) sb.append(" path")
        if ((failed and PATH_D) != 0) sb.append(" pathd")
        if ((failed and QUERY) != 0) sb.append(" query")
        if ((failed and QUERY_D) != 0) sb.append(" queryd")
        if ((failed and FRAGMENT) != 0) sb.append(" fragment")
        if ((failed and FRAGMENT_D) != 0) sb.append(" fragmentd")
        if ((failed and TOASCII) != 0) sb.append(" toascii")
        if ((failed and IDENT_STR) != 0) sb.append(" ident-str")
        if ((failed and IDENT_URI1) != 0) sb.append(" ident-uri1")
        if ((failed and IDENT_URI3) != 0) sb.append(" ident-uri3")
        if ((failed and IDENT_URI5) != 0) sb.append(" ident-uri5")
        if ((failed and IDENT_URI7) != 0) sb.append(" ident-uri7")
        if ((failed and TOSTRING) != 0) sb.append(" tostring")
        println(sb.toString())
        if (_uri != null) show(_uri!!)
        throw RuntimeException("Test failed")
    }

    companion object {
        var testCount: Int = 0

        // Properties that we check
        val PARSEFAIL: Int = 1 shl 0
        val SCHEME: Int = 1 shl 1
        val SSP: Int = 1 shl 2
        val SSP_D: Int = 1 shl 3 // Decoded form
        val OPAQUEPART: Int = 1 shl 4 // SSP, and URI is opaque
        val USERINFO: Int = 1 shl 5
        val USERINFO_D: Int = 1 shl 6 // Decoded form
        val HOST: Int = 1 shl 7
        val PORT: Int = 1 shl 8
        val REGISTRY: Int = 1 shl 9
        val REGISTRY_D: Int = 1 shl 10 // Decoded form
        val PATH: Int = 1 shl 11
        val PATH_D: Int = 1 shl 12 // Decoded form
        val QUERY: Int = 1 shl 13
        val QUERY_D: Int = 1 shl 14 // Decoded form
        val FRAGMENT: Int = 1 shl 15
        val FRAGMENT_D: Int = 1 shl 16 // Decoded form
        val TOASCII: Int = 1 shl 17
        val IDENT_STR: Int = 1 shl 18 // Identities
        val IDENT_URI1: Int = 1 shl 19
        val IDENT_URI3: Int = 1 shl 20
        val IDENT_URI5: Int = 1 shl 21
        val IDENT_URI7: Int = 1 shl 22
        val TOSTRING: Int = 1 shl 23

        fun test(s: String): UriTestHelper {
            return UriTestHelper(s)
        }

        fun test(
            s: String?, u: String?, h: String?, n: Int,
            p: String?, q: String?, f: String?
        ): UriTestHelper {
            return UriTestHelper(s, u, h, n, p, q, f)
        }

        fun test(
            s: String?, a: String?,
            p: String?, q: String?, f: String?
        ): UriTestHelper {
            return UriTestHelper(s, a, p, q, f)
        }

        fun test(s: String?, h: String?, p: String?, f: String?): UriTestHelper {
            return UriTestHelper(s, h, p, f)
        }

        fun test(s: String?, ssp: String?, f: String?): UriTestHelper {
            return UriTestHelper(s, ssp, f)
        }

        fun testCreate(s: String): UriTestHelper {
            return UriTestHelper(s, false)
        }

        // Summarization and reporting
        fun header(s: String?) {
            println()
            println()
            println("-- $s --")
        }

        fun show(prefix: String?, x: URISyntaxException) {
            println(uquote(x.getInput()))
            if (x.getIndex() >= 0) {
                for (i in 0..<x.getIndex()) {
                    if (x.getInput()[i] >= '\u0080') print("      ") // Skip over \u1234
                    else print(" ")
                }
                println("^")
            }
            println(prefix.toString() + ": " + x.getReason())
        }

        fun uquote(str: String?): String? {
            if (str == null) return str
            val sb = StringBuilder() // TODO: replace StringBuilder with StringBuffer when available
            val n: Int = str.length
            for (i in 0..<n) {
                val c: Char = str[i]
                if ((c >= ' ') && (c.code < 0x7f)) {
                    sb.append(c)
                    continue
                }
                sb.append("\\u")
                var s: String = c.code.toString(16).uppercase()
                while (s.length < 4) s = "0$s"
                sb.append(s)
            }
            return sb.toString()
        }

        fun show(n: String, v: String?) {
            println(
                ("  $n${"          = ".substring(n.length)}${uquote(v)}")
            )
        }

        fun show(n: String, v: String?, vd: String?) {
            if ((v == null) || v == vd) show(n, v)
            else {
                println(
                    ("  $n${"          = ".substring(n.length)}${uquote(v)} = ${uquote(vd)}")
                )
            }
        }

        fun show(u: URI) {
            show("opaque", "" + u.isOpaque())
            show("scheme", u.getScheme())
            show("ssp", u.getRawSchemeSpecificPart(), u.getSchemeSpecificPart())
            show("authority", u.getRawAuthority(), u.getAuthority())
            show("userinfo", u.getRawUserInfo(), u.getUserInfo())
            show("host", u.getHost())
            show("port", "" + u.getPort())
            show("path", u.getRawPath(), u.getPath())
            show("query", u.getRawQuery(), u.getQuery())
            show("fragment", u.getRawFragment(), u.getFragment())
        }

        // -- Tests --
        fun rfc2396() {
            header("RFC2396: Basic examples")

            test("ftp://ftp.is.co.za/rfc/rfc1808.txt")
                .s("ftp").h("ftp.is.co.za").p("/rfc/rfc1808.txt").z()

            test("http://www.math.uio.no/faq/compression-faq/part1.html")
                .s("http").h("www.math.uio.no").p("/faq/compression-faq/part1.html").z()

            test("mailto:mduerst@ifi.unizh.ch")
                .s("mailto").o("mduerst@ifi.unizh.ch").z()

            test("news:comp.infosystems.www.servers.unix")
                .s("news").o("comp.infosystems.www.servers.unix").z()

            test("telnet://melvyl.ucop.edu/")
                .s("telnet").h("melvyl.ucop.edu").p("/").z()

            test("http://www.w3.org/Addressing/")
                .s("http").h("www.w3.org").p("/Addressing/").z()

            test("ftp://ds.internic.net/rfc/")
                .s("ftp").h("ds.internic.net").p("/rfc/").z()

            test("http://www.ics.uci.edu/pub/ietf/uri/historical.html#WARNING")
                .s("http").h("www.ics.uci.edu").p("/pub/ietf/uri/historical.html")
                .f("WARNING").z()

            test("http://www.ics.uci.edu/pub/ietf/uri/#Related")
                .s("http").h("www.ics.uci.edu").p("/pub/ietf/uri/")
                .f("Related").z()


            header("RFC2396: Normal relative-URI examples (appendix C)")

            val base = (test("http://a/b/c/d;p?q")
                .s("http").h("a").p("/b/c/d;p").q("q").z().uri())

            // g:h       g:h
            test("g:h")
                .s("g").o("h").z()
                .rslv(base).s("g").o("h").z()

            // g         http://a/b/c/g
            test("g")
                .p("g").z()
                .rslv(base).s("http").h("a").p("/b/c/g").z()

            // ./g       http://a/b/c/g
            test("./g")
                .p("./g").z()
                .rslv(base).s("http").h("a").p("/b/c/g").z()

            // g/        http://a/b/c/g/
            test("g/")
                .p("g/").z()
                .rslv(base).s("http").h("a").p("/b/c/g/").z()

            // /g        http://a/g
            test("/g")
                .p("/g").z()
                .rslv(base).s("http").h("a").p("/g").z()

            // //g       http://g
            test("//g")
                .h("g").p("").z()
                .rslv(base).s("http").h("g").p("").z()

            // ?y        http://a/b/c/?y
            test("?y")
                .p("").q("y").z()
                .rslv(base).s("http").h("a").p("/b/c/").q("y").z()

            // g?y       http://a/b/c/g?y
            test("g?y")
                .p("g").q("y").z()
                .rslv(base).s("http").h("a").p("/b/c/g").q("y").z()

            // #s        (current document)#s
            // DEVIATION: Lone fragment parses as relative URI with empty path
            test("#s")
                .p("").f("s").z()
                .rslv(base).s("http").h("a").p("/b/c/d;p").f("s").q("q").z()

            // g#s       http://a/b/c/g#s
            test("g#s")
                .p("g").f("s").z()
                .rslv(base).s("http").h("a").p("/b/c/g").f("s").z()

            // g?y#s     http://a/b/c/g?y#s
            test("g?y#s")
                .p("g").q("y").f("s").z()
                .rslv(base).s("http").h("a").p("/b/c/g").q("y").f("s").z()

            // ;x        http://a/b/c/;x
            test(";x")
                .p(";x").z()
                .rslv(base).s("http").h("a").p("/b/c/;x").z()

            // g;x       http://a/b/c/g;x
            test("g;x")
                .p("g;x").z()
                .rslv(base).s("http").h("a").p("/b/c/g;x").z()

            // g;x?y#s   http://a/b/c/g;x?y#s
            test("g;x?y#s")
                .p("g;x").q("y").f("s").z()
                .rslv(base).s("http").h("a").p("/b/c/g;x").q("y").f("s").z()

            // .         http://a/b/c/
            test(".")
                .p(".").z()
                .rslv(base).s("http").h("a").p("/b/c/").z()

            // ./        http://a/b/c/
            test("./")
                .p("./").z()
                .rslv(base).s("http").h("a").p("/b/c/").z()

            // ..        http://a/b/
            test("..")
                .p("..").z()
                .rslv(base).s("http").h("a").p("/b/").z()

            // ../       http://a/b/
            test("../")
                .p("../").z()
                .rslv(base).s("http").h("a").p("/b/").z()

            // ../g      http://a/b/g
            test("../g")
                .p("../g").z()
                .rslv(base).s("http").h("a").p("/b/g").z()

            // ../..     http://a/
            test("../..")
                .p("../..").z()
                .rslv(base).s("http").h("a").p("/").z()

            // ../../    http://a/
            test("../../")
                .p("../../").z()
                .rslv(base).s("http").h("a").p("/").z()

            // ../../g   http://a/g
            test("../../g")
                .p("../../g").z()
                .rslv(base).s("http").h("a").p("/g").z()


            header("RFC2396: Abnormal relative-URI examples (appendix C)")

            // ../../../g    =  http://a/../g
            test("../../../g")
                .p("../../../g").z()
                .rslv(base).s("http").h("a").p("/../g").z()

            // ../../../../g =  http://a/../../g
            test("../../../../g")
                .p("../../../../g").z()
                .rslv(base).s("http").h("a").p("/../../g").z()


            // /./g          =  http://a/./g
            test("/./g")
                .p("/./g").z()
                .rslv(base).s("http").h("a").p("/./g").z()

            // /../g         =  http://a/../g
            test("/../g")
                .p("/../g").z()
                .rslv(base).s("http").h("a").p("/../g").z()

            // g.            =  http://a/b/c/g.
            test("g.")
                .p("g.").z()
                .rslv(base).s("http").h("a").p("/b/c/g.").z()

            // .g            =  http://a/b/c/.g
            test(".g")
                .p(".g").z()
                .rslv(base).s("http").h("a").p("/b/c/.g").z()

            // g..           =  http://a/b/c/g..
            test("g..")
                .p("g..").z()
                .rslv(base).s("http").h("a").p("/b/c/g..").z()

            // ..g           =  http://a/b/c/..g
            test("..g")
                .p("..g").z()
                .rslv(base).s("http").h("a").p("/b/c/..g").z()

            // ./../g        =  http://a/b/g
            test("./../g")
                .p("./../g").z()
                .rslv(base).s("http").h("a").p("/b/g").z()

            // ./g/.         =  http://a/b/c/g/
            test("./g/.")
                .p("./g/.").z()
                .rslv(base).s("http").h("a").p("/b/c/g/").z()

            // g/./h         =  http://a/b/c/g/h
            test("g/./h")
                .p("g/./h").z()
                .rslv(base).s("http").h("a").p("/b/c/g/h").z()

            // g/../h        =  http://a/b/c/h
            test("g/../h")
                .p("g/../h").z()
                .rslv(base).s("http").h("a").p("/b/c/h").z()

            // g;x=1/./y     =  http://a/b/c/g;x=1/y
            test("g;x=1/./y")
                .p("g;x=1/./y").z()
                .rslv(base).s("http").h("a").p("/b/c/g;x=1/y").z()

            // g;x=1/../y    =  http://a/b/c/y
            test("g;x=1/../y")
                .p("g;x=1/../y").z()
                .rslv(base).s("http").h("a").p("/b/c/y").z()

            // g?y/./x       =  http://a/b/c/g?y/./x
            test("g?y/./x")
                .p("g").q("y/./x").z()
                .rslv(base).s("http").h("a").p("/b/c/g").q("y/./x").z()

            // g?y/../x      =  http://a/b/c/g?y/../x
            test("g?y/../x")
                .p("g").q("y/../x").z()
                .rslv(base).s("http").h("a").p("/b/c/g").q("y/../x").z()

            // g#s/./x       =  http://a/b/c/g#s/./x
            test("g#s/./x")
                .p("g").f("s/./x").z()
                .rslv(base).s("http").h("a").p("/b/c/g").f("s/./x").z()

            // g#s/../x      =  http://a/b/c/g#s/../x
            test("g#s/../x")
                .p("g").f("s/../x").z()
                .rslv(base).s("http").h("a").p("/b/c/g").f("s/../x").z()

            // http:g        =  http:g
            test("http:g")
                .s("http").o("g").z()
                .rslv(base).s("http").o("g").z()
        }


        fun ip() {
            header("IP addresses")

            test("http://1.2.3.4:5")
                .s("http").h("1.2.3.4").n(5).p("").z()

            // From RFC2732
            test("http://[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]:80/index.html")
                .s("http").h("[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]")
                .n(80).p("/index.html").z()

            test("http://[FEDC:BA98:7654:3210:FEDC:BA98:7654:10%12]:80/index.html")
                .s("http").h("[FEDC:BA98:7654:3210:FEDC:BA98:7654:10%12]")
                .n(80).p("/index.html").z()

            test("http://[1080:0:0:0:8:800:200C:417A]/index.html")
                .s("http").h("[1080:0:0:0:8:800:200C:417A]").p("/index.html").z()

            test("http://[1080:0:0:0:8:800:200C:417A%1]/index.html")
                .s("http").h("[1080:0:0:0:8:800:200C:417A%1]").p("/index.html").z()

            test("http://[3ffe:2a00:100:7031::1]")
                .s("http").h("[3ffe:2a00:100:7031::1]").p("").z()

            test("http://[1080::8:800:200C:417A]/foo")
                .s("http").h("[1080::8:800:200C:417A]").p("/foo").z()

            test("http://[::192.9.5.5]/ipng")
                .s("http").h("[::192.9.5.5]").p("/ipng").z()

            test("http://[::192.9.5.5%interface]/ipng")
                .s("http").h("[::192.9.5.5%interface]").p("/ipng").z()

            test("http://[::FFFF:129.144.52.38]:80/index.html")
                .s("http").h("[::FFFF:129.144.52.38]").n(80).p("/index.html").z()

            test("http://[2010:836B:4179::836B:4179]")
                .s("http").h("[2010:836B:4179::836B:4179]").p("").z()

            // From RFC2373
            test("http://[FF01::101]")
                .s("http").h("[FF01::101]").p("").z()

            test("http://[::1]")
                .s("http").h("[::1]").p("").z()

            test("http://[::]")
                .s("http").h("[::]").p("").z()

            test("http://[::%hme0]")
                .s("http").h("[::%hme0]").p("").z()

            test("http://[0:0:0:0:0:0:13.1.68.3]")
                .s("http").h("[0:0:0:0:0:0:13.1.68.3]").p("").z()

            test("http://[0:0:0:0:0:FFFF:129.144.52.38]")
                .s("http").h("[0:0:0:0:0:FFFF:129.144.52.38]").p("").z()

            test("http://[0:0:0:0:0:FFFF:129.144.52.38%33]")
                .s("http").h("[0:0:0:0:0:FFFF:129.144.52.38%33]").p("").z()

            test("http://[0:0:0:0:0:ffff:1.2.3.4]")
                .s("http").h("[0:0:0:0:0:ffff:1.2.3.4]").p("").z()

            test("http://[::13.1.68.3]")
                .s("http").h("[::13.1.68.3]").p("").z()

            // Optional IPv6 brackets in constructors
            test("s", null, "1:2:3:4:5:6:7:8", -1, null, null, null)
                .s("s").h("[1:2:3:4:5:6:7:8]").p("").z()

            test("s", null, "[1:2:3:4:5:6:7:8]", -1, null, null, null)
                .s("s").h("[1:2:3:4:5:6:7:8]").p("").z()

            test("s", null, "[1:2:3:4:5:6:7:8]", -1, null, null, null)
                .s("s").h("[1:2:3:4:5:6:7:8]").p("").z()

            test("s", "1:2:3:4:5:6:7:8", null, null)
                .s("s").h("[1:2:3:4:5:6:7:8]").p("").z()

            test("s", "1:2:3:4:5:6:7:8%hme0", null, null)
                .s("s").h("[1:2:3:4:5:6:7:8%hme0]").p("").z()

            test("s", "1:2:3:4:5:6:7:8%1", null, null)
                .s("s").h("[1:2:3:4:5:6:7:8%1]").p("").z()

            test("s", "[1:2:3:4:5:6:7:8]", null, null)
                .s("s").h("[1:2:3:4:5:6:7:8]").p("").z()

            test("s", "[1:2:3:4:5:6:7:8]", null, null, null)
                .s("s").h("[1:2:3:4:5:6:7:8]").p("").z()

            test("s", "1:2:3:4:5:6:7:8", null, null, null)
                .s("s").g("1:2:3:4:5:6:7:8").p("").z()

            // Error cases
            test("http://[ff01:234/foo").x().z()
            test("http://[ff01:234:zzz]/foo").x().z()
            test("http://[foo]").x().z()
            test("http://[]").x().z()
            test("http://[129.33.44.55]").x().z()
            test("http://[ff:ee:dd:cc:bb::aa:9:8]").x().z()
            test("http://[fffff::1]").x().z()
            test("http://[ff::ee::8]").x().z()
            test("http://[1:2:3:4::5:6:7:8]").x().z()
            test("http://[1:2]").x().z()
            test("http://[1:2:3:4:5:6:7:8:9]").x().z()
            test("http://[1:2:3:4:5:6:7:8%]").x().z()
            test("http://[1:2:3:4:5:6:7:8%!/]").x().z()
            test("http://[::1.2.3.300]").x().z()
            test("http://1.2.3").psa().x().z()
            test("http://1.2.3.300").psa().x().z()
            test("http://1.2.3.4.5").psa().x().z()
            test("http://[1.2.3.4:5]").x().z()
            test("http://1:2:3:4:5:6:7:8").psa().x().z()
            test("http://[1.2.3.4]/").x().z()
            test("http://[1.2.3.4/").x().z()
            test("http://[foo]/").x().z()
            test("http://[foo/").x().z()
            test("s", "[foo]", "/", null, null).x().z()
            test("s", "[foo", "/", null, null).x().z()
            test("s", "[::foo", "/", null, null).x().z()

            // Test hostnames that might initially look like IPv4 addresses
            test("s://1.2.3.com").psa().s("s").h("1.2.3.com").p("").z()
            test("s://1.2.3.4me.com").psa().s("s").h("1.2.3.4me.com").p("").z()

            test("s://7up.com").psa().s("s").h("7up.com").p("").z()
            test("s://7up.com/p").psa().s("s").h("7up.com").p("/p").z()
            test("s://7up").psa().s("s").h("7up").p("").z()
            test("s://7up/p").psa().s("s").h("7up").p("/p").z()
            test("s://7up.").psa().s("s").h("7up.").p("").z()
            test("s://7up./p").psa().s("s").h("7up.").p("/p").z()
        }


        fun misc() {
            val base = URI("s://h/a/b")
            val rbase = URI("a/b/c/d")


            header("Corner cases")

            // The empty URI parses as a relative URI with an empty path
            test("").p("").z()
                .rslv(base).s("s").h("h").p("/a/").z()

            // Resolving solo queries and fragments
            test("#f").p("").f("f").z()
                .rslv(base).s("s").h("h").p("/a/b").f("f").z()
            test("?q").p("").q("q").z()
                .rslv(base).s("s").h("h").p("/a/").q("q").z()

            // Fragment is not part of ssp
            test("p#f").p("p").f("f").sp("p").z()
            test("s:p#f").s("s").o("p").f("f").z()
            test("p#f")
                .rslv(base).s("s").h("h").p("/a/p").f("f").sp("//h/a/p").z()
            test("").p("").sp("").z()



            header("Emptiness")

            // Components that may be empty
            test("///p").p("/p").z() // Authority (w/ path)
            test("//@h/p").u("").h("h").p("/p").z() // User info
            test("//h:/p").h("h").p("/p").z() // Port
            test("//h").h("h").p("").z() // Path
            test("//h?q").h("h").p("").q("q").z() // Path (w/query)
            test("//?q").p("").q("q").z() // Authority (w/query)
            test("//#f").p("").f("f").z() // Authority (w/fragment)
            test("p?#").p("p").q("").f("").z() // Query & fragment

            // Components that may not be empty
            test(":").x().z() // Scheme
            test("x:").x().z() // Hier/opaque
            test("//").x().z() // Authority (w/o path)


            header("Resolution, normalization, and relativization")

            // Resolving relative paths
            test("../e/f").p("../e/f").z()
                .rslv(rbase).p("a/b/e/f").z()
            test("../../../../d").p("../../../../d").z()
                .rslv(rbase).p("../d").z()
            test("../../../d:e").p("../../../d:e").z()
                .rslv(rbase).p("./d:e").z()
            test("../../../d:e/f").p("../../../d:e/f").z()
                .rslv(rbase).p("./d:e/f").z()

            // Normalization
            test("a/./c/../d/f").p("a/./c/../d/f").z()
                .norm().p("a/d/f").z()
            test("http://a/./b/c/../d?q#f")
                .s("http").h("a").p("/./b/c/../d").q("q").f("f").z()
                .norm().s("http").h("a").p("/b/d").q("q").f("f").z()
            test("a/../b").p("a/../b").z().norm().p("b")
            test("a/../b:c").p("a/../b:c").z()
                .norm().p("./b:c").z()

            // Normalization of already normalized URI should yield the
            // same URI
            val u1: URI = URIFactory.create("s://h/../p")
            val u2: URI = u1.normalize()
            eq(u1, u2)
            eqeq(u1, u2)

            // Relativization
            test("/a/b").p("/a/b").z()
                .rtvz(URI("/a")).p("b").z()
            test("/a/b").p("/a/b").z()
                .rtvz(URI("/a/")).p("b").z()
            test("a/b").p("a/b").z()
                .rtvz(URI("a")).p("b").z()
            test("/a/b").p("/a/b").z()
                .rtvz(URI("/a/b")).p("").z() // Result is empty path
            test("a/../b:c/d").p("a/../b:c/d").z()
                .rtvz(URI("./b:c/")).p("d").z()

            test("http://a/b/d/e?q#f")
                .s("http").h("a").p("/b/d/e").q("q").f("f").z()
                .rtvz(URI("http://a/b/?r#g"))
                .p("d/e").q("q").f("f").z()

            // parseServerAuthority
            test("/a/b").psa().p("/a/b").z()
            test("s://u@h:1/p")
                .psa().s("s").u("u").h("h").n(1).p("/p").z()
            test("s://u@h:-foo/p").s("s").g("u@h:-foo").p("/p").z()
                .psa().x().z()
            test("s://h:999999999999999999999999").psa().x().z()
            test("s://:/b").psa().x().z()


            header("Constructors and factories")

            test("s", null, null, -1, "p", null, null).x().z()
            test(null, null, null, -1, null, null, null).p("").z()
            test(null, null, null, -1, "p", null, null).p("p").z()
            test(null, null, "foo%20bar", -1, null, null, null).x().z()
            test(null, null, "foo", -100, null, null, null).x().z()
            test("s", null, null, -1, "", null, null).x().z()
            test("s", null, null, -1, "/p", null, null).s("s").p("/p").z()
            test("s", "u", "h", 10, "/p", "q", "f")
                .s("s").u("u").h("h").n(10).p("/p").q("q").f("f").z()
            test("s", "a:b", "/p", "q", "f")
                .s("s").g("a:b").p("/p").q("q").f("f").z()
            test("s", "h", "/p", "f")
                .s("s").h("h").p("/p").f("f").z()
            test("s", "p", "f").s("s").o("p").f("f").z()
            test("s", "/p", "f").s("s").p("/p").f("f").z()
            testCreate("s://u@h/p?q#f")
                .s("s").u("u").h("h").p("/p").q("q").f("f").z()
        }

        fun chars() {
            header("Escapes and non-US-ASCII characters")

            // Escape pairs
            test("%0a%0A%0f%0F%01%09zz")
                .p("%0a%0A%0f%0F%01%09zz").z()
            test("foo%1").x().z()
            test("foo%z").x().z()
            test("foo%9z").x().z()

            // Escapes not permitted in scheme, host
            test("s%20t://a").x().z()
            test("//a%20b").g("a%20b").p("").z() // Parses as registry

            // Escapes permitted in opaque part, userInfo, registry, path,
            // query, and fragment
            test("//u%20v@a").u("u%20v").h("a").p("").z()
            test("/p%20q").p("/p%20q").z()
            test("/p?q%20").p("/p").q("q%20").z()
            test("/p#%20f").p("/p").f("%20f").z()

            // Non-US-ASCII chars
            test("s\u00a7t://a").x().z()
            test("//\u00a7/b").g("\u00a7").p("/b").z() // Parses as registry
            test("//u\u00a7v@a").u("u\u00a7v").h("a").p("").z()
            test("/p\u00a7q").p("/p\u00a7q").z()
            test("/p?q\u00a7").p("/p").q("q\u00a7").z()
            test("/p#\u00a7f").p("/p").f("\u00a7f").z()

            // 4648111 - Escapes quoted by toString after resolution
            val uri = URI("http://a/b/c/d;p?q")
            test("/p%20p")
                .rslv(uri).s("http").h("a").p("/p%20p").ts("http://a/p%20p").z()

            // 4464135: Forbid unwise characters throughout opaque part
            test("foo:x{bar").x().z()
            test("foo:{bar").x().z()

            // 4438319: Single-argument constructor requires quotation,
            //          preserves escapes
            test("//u%01@h/a/b/%02/c?q%03#f%04")
                .u("u%01").ud("u\u0001")
                .h("h")
                .p("/a/b/%02/c").pd("/a/b/\u0002/c")
                .q("q%03").qd("q\u0003")
                .f("f%04").fd("f\u0004")
                .z()
            test("/a/b c").x().z()

            // 4438319: Multi-argument constructors quote illegal chars and
            //          preserve legal non-ASCII chars
            // \uA001-\uA009 are visible characters, \u2000 is a space character
            test(
                null, "u\uA001\u0001", "h", -1,
                "/p% \uA002\u0002\u2000",
                "q% \uA003\u0003\u2000",
                "f% \uA004\u0004\u2000"
            )
                .u("u\uA001%01").h("h")
                .p("/p%25%20\uA002%02%E2%80%80").pd("/p% \uA002\u0002\u2000")
                .q("q%25%20\uA003%03%E2%80%80").qd("q% \uA003\u0003\u2000")
                .f("f%25%20\uA004%04%E2%80%80").fd("f% \uA004\u0004\u2000").z()
            test(
                null, "g\uA001\u0001",
                "/p% \uA002\u0002\u2000",
                "q% \uA003\u0003\u2000",
                "f% \uA004\u0004\u2000"
            )
                .g("g\uA001%01")
                .p("/p%25%20\uA002%02%E2%80%80").pd("/p% \uA002\u0002\u2000")
                .q("q%25%20\uA003%03%E2%80%80").qd("q% \uA003\u0003\u2000")
                .f("f%25%20\uA004%04%E2%80%80").fd("f% \uA004\u0004\u2000").z()
            test(null, null, "/p% \uA002\u0002\u2000", "f% \uA004\u0004\u2000")
                .p("/p%25%20\uA002%02%E2%80%80").pd("/p% \uA002\u0002\u2000")
                .f("f%25%20\uA004%04%E2%80%80").fd("f% \uA004\u0004\u2000").z()
            test(null, "/sp% \uA001\u0001\u2000", "f% \uA004\u0004\u2000")
                .sp("/sp%25%20\uA001%01%E2%80%80").spd("/sp% \uA001\u0001\u2000")
                .p("/sp%25%20\uA001%01%E2%80%80").pd("/sp% \uA001\u0001\u2000")
                .f("f%25%20\uA004%04%E2%80%80").fd("f% \uA004\u0004\u2000").z()

            // 4438319: Non-raw accessors decode all escaped octets
            test("/%25%20%E2%82%AC%E2%80%80")
                .p("/%25%20%E2%82%AC%E2%80%80").pd("/% \u20Ac\u2000").z()

            // 4438319: toASCIIString
            test("/\uCAFE\uBABE")
                .p("/\uCAFE\uBABE").z()

            // 4991359 and 4866303: bad quoting by defineSchemeSpecificPart()
            val base = URI("http://host/foo%20bar/a/b/c/d")
            test("resolve")
                .rslv(base).spd("//host/foo bar/a/b/c/resolve")
                .sp("//host/foo%20bar/a/b/c/resolve").s("http")
                .pd("/foo bar/a/b/c/resolve").h("host")
                .p("/foo%20bar/a/b/c/resolve").z()

            // 6773270: java.net.URI fails to escape u0000
            test("s", "a", "/\u0000", null)
                .s("s").p("/%00").h("a").z()
        }


        @Throws(URISyntaxException::class)
        fun eq0(u: URI, v: URI) {
            testCount++
            if (u != v) throw RuntimeException("Not equal: $u $v")
            val uh = u.hashCode()
            val vh = v.hashCode()
            if (uh != vh) throw RuntimeException("Hash codes not equal: $u ${uh.toHexString()} $v ${vh.toHexString()}")
            println()
            println("$u == $v  [${uh.toHexString()}]")
        }

        @Throws(URISyntaxException::class)
        fun cmp0(u: URI, v: URI?, same: Boolean) {
            val c = u.compareTo(v!!)
            if ((c == 0) != same) throw RuntimeException(
                ("Comparison inconsistent: " + u + " " + v
                        + " " + c)
            )
        }

        @Throws(URISyntaxException::class)
        fun eq(u: URI, v: URI) {
            eq0(u, v)
            cmp0(u, v, true)
        }

        fun eq(expected: String?, actual: String?) {
            testCount++
            if (expected == null && actual == null) {
                return
            }
            if (expected != null && expected == actual) {
                return
            }
            throw AssertionError("Strings are not equal: '$expected', '$actual'")
        }

        fun eqeq(u: URI?, v: URI?) {
            testCount++
            if (u !== v) throw RuntimeException("Not ==: $u $v")
        }

        @Throws(URISyntaxException::class)
        fun ne0(u: URI, v: URI) {
            testCount++
            if (u == v) throw RuntimeException("Equal: $u $v")
            println()
            println("$u != $v  [${u.hashCode().toHexString()} ${v.hashCode().toHexString()}]")
        }

        @Throws(URISyntaxException::class)
        fun ne(u: URI, v: URI) {
            ne0(u, v)
            cmp0(u, v, false)
        }

        @Throws(URISyntaxException::class)
        fun lt(u: URI, v: URI) {
            ne0(u, v)
            val c = u.compareTo(v)
            if (c >= 0) {
                show(u)
                show(v)
                throw RuntimeException(
                    ("Not less than: " + u + " " + v
                            + " " + c)
                )
            }
            println("$u < $v")
        }

        fun lt(s: String, t: String) {
            lt(URI(s), URI(t))
        }

        fun gt0(u: URI, v: URI) {
            ne0(u, v)
            val c = u.compareTo(v)
            if (c <= 0) {
                show(u)
                show(v)
                throw RuntimeException("Not greater than: $u $v $c")
            }
            println("$u < $v")
        }

        @Throws(URISyntaxException::class)
        fun gt(u: URI, v: URI) {
            lt(v, u)
        }

        @Throws(URISyntaxException::class)
        fun eqHashComp() {
            header("Equality, hashing, and comparison")

            val o = URI("mailto:foo@bar.com")
            val r = URI("reg://some%20registry/b/c/d?q#f")
            val s = URI("http://jag:cafebabe@java.sun.com:94/b/c/d?q#f")
            val t = URI("http://example.com/%5bsegment%5d")
            eq(o, o)
            lt(o, r)
            lt(s, o)
            lt(s, r)

            eq(o, URI("MaILto:foo@bar.com"))
            gt(o, URI("mailto:foo@bar.COM"))
            eq(r, URI("rEg://some%20registry/b/c/d?q#f"))
            gt(r, URI("reg://Some%20Registry/b/c/d?q#f"))
            gt(r, URI("reg://some%20registry/b/c/D?q#f"))
            eq(s, URI("hTtP://jag:cafebabe@Java.Sun.COM:94/b/c/d?q#f"))
            gt(s, URI("http://jag:CafeBabe@java.sun.com:94/b/c/d?q#f"))
            lt(s, URI("http://jag:cafebabe@java.sun.com:94/b/c/d?r#f"))
            lt(s, URI("http://jag:cafebabe@java.sun.com:94/b/c/d?q#g"))
            cmp0(t, URI("http://example.com/%5Bsegment%5D"), true)
            gt0(t, URI("http://example.com/%5BSegment%5D"))
            lt(URI("http://example.com/%5Asegment%5D"), URI("http://example.com/%5Bsegment%5D"))
            eq(URI("http://host/a%00bcd"), URI("http://host/a%00bcd"))
            ne(URI("http://host/a%00bcd"), URI("http://host/aZ00bcd"))
            eq0(
                URI("http://host/abc%e2def%C3ghi"),
                URI("http://host/abc%E2def%c3ghi")
            )

            lt("p", "s:p")
            lt("s:p", "T:p")
            lt("S:p", "t:p")
            lt("s:/p", "s:p")
            lt("s:p", "s:q")
            lt("s:p#f", "s:p#g")
            lt("s://u@h:1", "s://v@h:1")
            lt("s://u@h:1", "s://u@i:1")
            lt("s://u@h:1", "s://v@h:2")
            lt("s://a%20b", "s://a%20c")
            lt("s://a%20b", "s://aab")
            lt("s://AA", "s://A_")
            lt("s:/p", "s:/q")
            lt("s:/p?q", "s:/p?r")
            lt("s:/p#f", "s:/p#g")

            lt("s://h", "s://h/p")
            lt("s://h/p", "s://h/p?q")
        }

        fun urls() {
            // TODO: add URL
            /*header("URLs")
            var url: URL
            var caught = false

            println()
            var uri = URI("http://a/p?q#f")
            try {
                url = uri!!.toURL()
            } catch (x: MalformedURLException) {
                throw RuntimeException(x.toString())
            }
            if (url.toString() != "http://a/p?q#f") throw RuntimeException("Incorrect URL: $url")
            println(uri!!.toString() + " url --> " + url)

            println()
            uri = URI("a/b")
            try {
                println(uri!!.toString() + " url --> ")
                url = uri!!.toURL()
            } catch (x: IllegalArgumentException) {
                caught = true
                println("Correct exception: $x")
            } catch (x: MalformedURLException) {
                caught = true
                throw RuntimeException("Incorrect exception: $x")
            }
            if (!caught) throw RuntimeException("Incorrect URL: $url")

            println()
            uri = URI("foo://bar/baz")
            caught = false
            try {
                println(uri!!.toString() + " url --> ")
                url = uri!!.toURL()
            } catch (x: MalformedURLException) {
                caught = true
                println("Correct exception: $x")
            } catch (x: IllegalArgumentException) {
                caught = true
                throw RuntimeException("Incorrect exception: $x")
            }
            if (!caught) throw RuntimeException("Incorrect URL: $url")

            */
            testCount += 3
        }


        // miscellaneous bugs/rfes that don't fit in with the test framework
        fun bugs() {
            header("Bugs")
            b6339649()
            b6933879()
            b8037396()
            b8051627()
            b8272072()
            b8297687()
        }

        private fun b8297687() {
            // constructors that take a hostname should fail
            test("ftps", "p.e.local|SIT@p.e.local", "/path", null)
                .x().z()
            test("ftps", null, "p.e.local|SIT@p.e.local", -1, "/path", null, null)
                .x().z()
            // constructors that take an authority component should succeed
            test("ftps", "p.e.local|SIT@p.e.local", "/path", null, null)
                .s("ftps")
                .sp("//p.e.local%7CSIT@p.e.local/path")
                .spd("//p.e.local|SIT@p.e.local/path")
                .u("p.e.local%7CSIT")
                .ud("p.e.local|SIT")
                .h("p.e.local")
                .n(-1)
                .p("/path")
                .pd("/path")
                .z()

            // check index in exception for constructors that should fail
            try {
                val uri = URI("ftps", "p.e.local|SIT@p.e.local", "/path", null)
                throw AssertionError("Expected URISyntaxException not thrown for $uri")
            } catch (ex: URISyntaxException) {
                if ((Platform.isJvmOrAndroid() && ex.message!!.contains("at index 7")) || ex.message!!.contains("at index 16")) {
                    println("Got expected exception: $ex")
                } else {
                    throw AssertionError("Exception does not point at index 16", ex)
                }
            }
            testCount++

            // check index in exception for constructors that should fail
            try {
                val uri = URI("ftps", null, "p.e.local|SIT@p.e.local", -1, "/path", null, null)
                throw AssertionError("Expected URISyntaxException not thrown for $uri")
            } catch (ex: URISyntaxException) {
                if ((Platform.isJvmOrAndroid() && ex.message!!.contains("at index 7")) || ex.message!!.contains("at index 16")) {
                    println("Got expected exception: $ex")
                } else {
                    throw AssertionError("Exception does not point at index 16", ex)
                }
            }
            testCount++
        }

        // 6339649 - include detail message from nested exception
        private fun b6339649() {
            try {
                val uri: URI? = URIFactory.create("http://nowhere.net/should not be permitted")
            } catch (e: IllegalArgumentException) {
                if ("" == e.message || e.message == null) {
                    throw RuntimeException("No detail message")
                }
            }
            testCount++
        }

        // 6933879 - check that "." and "_" characters are allowed in IPv6 scope_id.
        private fun b6933879() {
            val HOST = "fe80::c00:16fe:cebe:3214%eth1.12_55"
            val uri: URI?
            try {
                uri = URI("http", null, HOST, 10, "/", null, null)
            } catch (ex: URISyntaxException) {
                throw AssertionError("Should not happen", ex)
            }
            eq("[$HOST]", uri.getHost())
        }

        private fun b8037396() {
            // primary checks:

            var u: URI?
            try {
                u = URI("http", "example.org", "/[a b]", "[a b]", "[a b]")
            } catch (e: URISyntaxException) {
                throw AssertionError("shouldn't ever happen", e)
            }
            eq("/[a b]", u.getPath())
            eq("[a b]", u.getQuery())
            eq("[a b]", u.getFragment())

            // additional checks:
            //  *   '%' symbols are still decoded outside square brackets
            //  *   the getRawXXX() functionality left intact
            try {
                u = URI("http", "example.org", "/a b[c d]", "a b[c d]", "a b[c d]")
            } catch (e: URISyntaxException) {
                throw AssertionError("shouldn't ever happen", e)
            }

            eq("/a b[c d]", u.getPath())
            eq("a b[c d]", u.getQuery())
            eq("a b[c d]", u.getFragment())

            eq("/a%20b%5Bc%20d%5D", u.getRawPath())
            eq("a%20b[c%20d]", u.getRawQuery())
            eq("a%20b[c%20d]", u.getRawFragment())
        }

        // 8051627 - Invariants about java.net.URI resolve and relativize are wrong
        private fun b8051627() {
            try {
                // Let u be a normalized absolute URI u which ends with "/" and
                // v be a normalized relative URI v which does not start with "." or "/", then
                // u.relativize(u.resolve(v)).equals(v) should be true
                reltivizeAfterResolveTest("http://a/b/", "c/d", "c/d")
                reltivizeAfterResolveTest("http://a/b/", "g;x?y#s", "g;x?y#s")

                // when the URI condition is not met, u.relativize(u.resolve(v)).equals(v) may be false
                // In the following examples, that should be false
                reltivizeAfterResolveTest("http://a/b", "c/d", "http://a/c/d")
                reltivizeAfterResolveTest("http://a/b/", "../c/d", "http://a/c/d")
                reltivizeAfterResolveTest("http://a/b/", "/c/d", "http://a/c/d")
                reltivizeAfterResolveTest("http://a/b/", "http://a/b/c/d", "c/d")

                // Let u be a normalized absolute URI u which ends with "/" and
                // v be a normalized absolute URI v, then
                // u.resolve(u.relativize(v)).equals(v) should be true
                resolveAfterRelativizeTest("http://a/b/", "http://a/b/c/d", "http://a/b/c/d")
                resolveAfterRelativizeTest("http://a/b/", "http://a/b/c/g;x?y#s", "http://a/b/c/g;x?y#s")

                // when the URI condition is not met, u.resolve(u.relativize(v)).equals(v) may be false
                // In the following examples, that should be false
                resolveAfterRelativizeTest("http://a/b", "http://a/b/c/d", "http://a/c/d")
                resolveAfterRelativizeTest("http://a/b/", "c/d", "http://a/b/c/d")
            } catch (e: URISyntaxException) {
                throw AssertionError("shouldn't ever happen", e)
            }
        }

        private fun reltivizeAfterResolveTest(base: String, target: String, expected: String) {
            val baseURI: URI = URIFactory.create(base)
            val targetURI: URI = URIFactory.create(target)
            eq(URIFactory.create(expected), baseURI.relativize(baseURI.resolve(targetURI)))
        }

        private fun resolveAfterRelativizeTest(base: String, target: String, expected: String) {
            val baseURI: URI = URIFactory.create(base)
            val targetURI: URI = URIFactory.create(target)
            eq(URIFactory.create(expected), baseURI.resolve(baseURI.relativize(targetURI)))
        }

        // 8272072 - Resolving URI relative path with no "/" may lead to incorrect toString
        private fun b8272072() {
            try {
                var baseURI = URI("http://example.com")
                val relativeURI = URI("test")
                var resolvedURI: URI = baseURI.resolve(relativeURI)

                eq(URI("http://example.com/test"), resolvedURI)

                baseURI = URI("relativeBase")
                resolvedURI = baseURI.resolve(relativeURI)

                eq(URI("test"), resolvedURI)
            } catch (e: URISyntaxException) {
                throw AssertionError("shouldn't ever happen", e)
            }
        }
    }
}
