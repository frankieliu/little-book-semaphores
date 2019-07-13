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
	ctx := context.TODO()
	barrier := semaphore.NewWeighted(int64(1))
	barrier.Acquire(ctx, 1)
	count := Counter{val: 0}
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
