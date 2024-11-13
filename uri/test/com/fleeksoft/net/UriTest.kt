package com.fleeksoft.net

import kotlin.test.Test

class UriTest {

    @Test
    fun rfc2396() {
        UriTestHelper.rfc2396()
    }

    @Test
    fun ip() {
        UriTestHelper.ip()
    }

    @Test
    fun misc() {
        UriTestHelper.misc()
    }

    @Test
    fun chars() {
        UriTestHelper.chars()
    }

    @Test
    fun eqHashComp() {
        UriTestHelper.eqHashComp()
    }

    @Test
    fun urls() {
        UriTestHelper.urls()
    }

    @Test
    fun bugs() {
        UriTestHelper.bugs()
    }
}