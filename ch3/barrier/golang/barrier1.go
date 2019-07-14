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

	// Barrier semaphore (don't allow it to go above 1)
	// Initially set it to barrier to 0
	barrier := semaphore.NewWeighted(int64(1))
	barrier.Acquire(ctx, 1)

	done := make(chan int, numThreads)
	for i := 0; i < numThreads; i++ {
		go func() {
			fmt.Println("rendezvous")
			count.m.Lock()
			count.val += 1
			count.m.Unlock()
			if count.val == numThreads {
				barrier.Release(1)
			}
			barrier.Acquire(ctx, 1)
			barrier.Release(1)
			fmt.Println("critical point")
			done <- 1
		}()
	}

	for i := 0; i < numThreads; i++ {
		<-done
	}
}
