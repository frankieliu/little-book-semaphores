package main

import (
	"fmt"
	"sync"
	"context"
	"golang.org/x/sync/semaphore"
	// "time"
)

type Counter struct {
	val int
	m sync.Mutex
}

func main() {
	numThreads := 10      // Number of threads to create
	count := Counter{val: 0}
	ctx := context.TODO() // Context req for golang's semaphores

	turnstile1 := semaphore.NewWeighted(int64(1))
	turnstile2 := semaphore.NewWeighted(int64(1))
	// Turnstile2 : bomb needs to be detonated
	turnstile1.Acquire(ctx, 1)

	done := make(chan int, numThreads)
	for i := 0; i < numThreads; i++ {
		go func() {

			//--------------------------------------------------------
			fmt.Println("rendezvous1")

			// First barrier
			count.m.Lock()
			count.val += 1
			if count.val == numThreads {
				turnstile2.Acquire(ctx,1)  // diffuse bomb2
				turnstile1.Release(1)      // activate bomb1
			}
			count.m.Unlock()
			turnstile1.Acquire(ctx, 1)
			turnstile1.Release(1)

			//--------------------------------------------------------
			fmt.Println("critical point1")

			// Second barrier
			count.m.Lock()
			count.val -= 1
			if count.val == 0 {
				turnstile1.Acquire(ctx,1)  // diffuse bomb1
				turnstile2.Release(1)      // activate bomb2
			}
			count.m.Unlock()
			turnstile2.Acquire(ctx, 1)
			turnstile2.Release(1)

			//--------------------------------------------------------
			fmt.Println("rendezvous2")

			// First barrier
			count.m.Lock()
			count.val += 1
			if count.val == numThreads {
				turnstile2.Acquire(ctx,1)  // diffuse bomb2
				turnstile1.Release(1)      // activate bomb1
			}
			count.m.Unlock()
			turnstile1.Acquire(ctx, 1)
			turnstile1.Release(1)

			//--------------------------------------------------------
			fmt.Println("critical point2")

			// Second barrier
			count.m.Lock()
			count.val -= 1
			if count.val == 0 {
				turnstile1.Acquire(ctx,1)  // diffuse bomb1
				turnstile2.Release(1)      // activate bomb2
			}
			count.m.Unlock()
			turnstile2.Acquire(ctx, 1)
			turnstile2.Release(1)

			done <- 1
		}()
	}

	for i := 0; i < numThreads; i++ {
		<-done
	}
}
