package main

import (
	"fmt"
	"context"
	"golang.org/x/sync/semaphore"
	"math/rand"
	"time"
)

func main() {
	rand.Seed(time.Now().UnixNano())

	max_followers := 10
	max_leaders := 10

	followers := 0
	leaders := 0

	ctx := context.TODO()

	mutex := semaphore.NewWeighted(1)
	leaderQ := semaphore.NewWeighted(1)
	leaderQ.Acquire(ctx, 1)
	followerQ := semaphore.NewWeighted(1)
	followerQ.Acquire(ctx, 1)
	rendezvous := semaphore.NewWeighted(1)
	rendezvous.Acquire(ctx, 1)

	done := make(chan int, max_followers+max_leaders)

	i := 0;	j := 0
	for (i < max_followers) || (j < max_leaders) {
		if rand.Intn(2) == 1 {
			if i == max_followers {
				continue
			}

			i += 1
			// Leader code
			go func(ind int) {
				mutex.Acquire(ctx, 1)
				if followers > 0 {
					followers -= 1
					followerQ.Release(1)
				} else {
					leaders += 1
					mutex.Release(1)
					leaderQ.Acquire(ctx, 1)
				}
				fmt.Printf("Leader %v\n", ind)
				rendezvous.Acquire(ctx, 1)
				mutex.Release(1)
				done <- 1
			}(i)

		} else {
			if j == max_leaders {
				continue
			}
			j += 1
			// Follower code
			go func(ind int) {
				mutex.Acquire(ctx, 1)
				if leaders > 0 {
					leaders -= 1
					leaderQ.Release(1)
				} else {
					followers += 1
					mutex.Release(1)
					followerQ.Acquire(ctx, 1)
				}
				fmt.Printf("Follower %v\n", ind)
				rendezvous.Release(1)
				done <- 1
			}(j)
		}
	}

	for i := 0; i < (max_followers + max_leaders); i++ {
		<-done
	}
}
