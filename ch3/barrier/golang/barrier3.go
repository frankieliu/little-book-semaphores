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
	numThreads := 10
	num64 := int64(numThreads)
	count := Counter{val: 0}
	ctx := context.TODO()

	// Pre-loadable bombs

	// NOTE: it is necessary to allocate more than
	// enough semaphores, because might be releasing
	// many of them at once

	turnstile1 := semaphore.NewWeighted(num64)
	turnstile1.Acquire(ctx, num64)
	turnstile2 := semaphore.NewWeighted(num64)
	turnstile2.Acquire(ctx, num64)

	done := make(chan int, numThreads)
	for i := 0; i < numThreads; i++ {
		go func() {

			//--------------------------------------------------------
			fmt.Println("rendezvous1")

			// First barrier
			count.m.Lock()
			count.val += 1
			if count.val == numThreads {
				turnstile1.Release(num64)
			}
			count.m.Unlock()
			turnstile1.Acquire(ctx, 1)

			//--------------------------------------------------------
			fmt.Println("critical point1")

			// Second barrier
			count.m.Lock()
			count.val -= 1
			if count.val == 0 {
				turnstile2.Release(num64)
			}
			count.m.Unlock()
			turnstile2.Acquire(ctx, 1)

			//--------------------------------------------------------
			fmt.Println("rendezvous2")

			// First barrier
			count.m.Lock()
			count.val += 1
			if count.val == numThreads {
				turnstile1.Release(num64)
			}
			count.m.Unlock()
			turnstile1.Acquire(ctx, 1)

			//--------------------------------------------------------
			fmt.Println("critical point2")

			// Second barrier
			count.m.Lock()
			count.val -= 1
			if count.val == 0 {
				turnstile2.Release(num64)
			}
			count.m.Unlock()
			turnstile2.Acquire(ctx, 1)

			done <- 1
		}()
	}

	for i := 0; i < numThreads; i++ {
		<-done
	}
}
