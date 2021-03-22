package com.github.nanodeath

import java.nio.ByteBuffer
import java.security.MessageDigest

internal fun ByteArray.toLong(): Long = ByteBuffer.wrap(this).long
internal fun String.sha1(): ByteArray = MessageDigest.getInstance("SHA-1").digest(toByteArray())