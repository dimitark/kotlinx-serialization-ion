package com.github.dimitark.kotlix.serialization.ion

import java.io.File

fun tempFile(): File = File.createTempFile("ion", "serialization")
