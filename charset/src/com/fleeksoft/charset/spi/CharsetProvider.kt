package com.fleeksoft.charset.spi

import com.fleeksoft.charset.Charset

abstract class CharsetProvider {

    /**
     * Retrieves a charset for the given charset name.
     *
     * @param  charsetName
     * The name of the requested charset; may be either
     * a canonical name or an alias
     *
     * @return  A charset object for the named charset,
     * or `null` if the named charset
     * is not supported by this provider
     */
    abstract fun charsetForName(charsetName: String): Charset?
}