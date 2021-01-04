package info.galudisu.kotlin.coroutines.intro

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Coroutines example
fun main() = runBlocking {
  // Starts a job
  val job1 = launch { delay(500) }

  fun fib(n: Long): Long = if (n < 2) n else fib(n - 1) + fib(n - 2)

  // Starts a job that returns a value
  val job2 = async { fib(42) }

  // Waits for the job to complete
  job1.join()
  println("job1 has completed")
  // Gets the value when the job completes
  println("job2 fib(42) = ${job2.await()}")
}
