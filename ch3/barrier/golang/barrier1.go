package main

import (
	"fmt"
	"sync"
	"context"
	"golang.org/x/sync/semaphore"
	"time"
)

type Counter struct {
	count int
	m sync.Mutex
}

func main() {
	numThreads = 10
	count := Counter{count: numThreads}
	for i := 0; i < numThreads; i++ {
		go func() {
			fmt.Println("rendezvous");
			fmt.Println("critical point");
		}
	}
}
