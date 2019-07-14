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

type Barrier struct {
	n int
	n64 int64
	count int
	m sync.Mutex
	turnstile1 *semaphore.Weighted
	turnstile2 *semaphore.Weighted
	ctx context.Context
}

func (b *Barrier) Init() {
	b.n64 = int64(b.n)
	b.ctx = context.TODO()
	b.turnstile1 = semaphore.NewWeighted(b.n64)
	b.turnstile1.Acquire(b.ctx, b.n64)
	b.turnstile2 = semaphore.NewWeighted(b.n64)
	b.turnstile2.Acquire(b.ctx, b.n64)
}

func (b *Barrier) Phase1 () {
	b.m.Lock()
	b.count += 1
	if b.count == b.n {
		b.turnstile1.Release(b.n64)
	}
	b.m.Unlock()
	b.turnstile1.Acquire(b.ctx, 1)
}

func (b *Barrier) Phase2 () {
	b.m.Lock()
	b.count -= 1
	if b.count == 0 {
		b.turnstile2.Release(b.n64)
	}
	b.m.Unlock()
	b.turnstile2.Acquire(b.ctx, 1)
}

func (b *Barrier) Wait() {
	b.Phase1()
	b.Phase2()
}

func main() {
	numThreads := 10
	b := Barrier{n: numThreads}
	b.Init()
	done := make(chan int, numThreads)
	for i := 0; i < numThreads; i++ {
		go func() {
			fmt.Println("rendezvous1")
			b.Phase1()
			fmt.Println("critical point1")
			b.Phase2()
			fmt.Println("rendezvous2")
			b.Phase1()
			fmt.Println("critical point2")
			b.Phase2()
			fmt.Println("rendezvous3")
			b.Wait()
			fmt.Println("critical point3")
			b.Wait()
			fmt.Println("rendezvous4")
			b.Wait()
			fmt.Println("critical point4")
			b.Wait()
			done <- 1
		}()
	}

	for i := 0; i < numThreads; i++ {
		<-done
	}
}
