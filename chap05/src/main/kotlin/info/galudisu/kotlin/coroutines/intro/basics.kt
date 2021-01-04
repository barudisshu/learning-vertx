package info.galudisu.kotlin.coroutines.intro

import kotlinx.coroutines.*

// This function can be suspended.
suspend fun hello(): String {
  // This function is suspending and will not block the caller thread.
  delay(1000)
  return "Hello!"
}

fun main() {
  // This allows waiting for coroutines code to complete.
  runBlocking {
    println(hello())
  }
}
