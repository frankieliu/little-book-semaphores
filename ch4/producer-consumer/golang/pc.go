package main

import (
	"fmt"
	// "sync"
	"time"
	"math/rand"
)

func main() {

	q := make(int,0)
	done := make(chan int, 0)
	m := &sync.Mutex{}

	fmt.Println("Hello")

	// Producer
	go func(id int) {
		time.Sleep(time.Duration(rand.Intn(5) * 100) * time.Millisecond)
		m.Lock()
		q.append(id)
		m.Unlock()
		done <- 1
	}(1)

	// Producer
	go func(id int) {
		time.Sleep(time.Duration(rand.Intn(10) * 100) * time.Millisecond)
		m.Lock()
		q.append(id)
		m.Unlock()
		done <- 1
	}(2)

	for i:=0; i<2; i++ {
		<- done
	}
}
