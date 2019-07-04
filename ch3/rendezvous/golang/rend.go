package main
import (
	//	"context"
	"fmt"
	//	"log"
	//	"runtime"
	//	"golang.org/x/sync/semaphore"
	"time"
)

func main () {

	//ctx := context.TODO()
	//aAcquired := semaphore.NewWeighted(int64(2))
	//bAcquired := semaphore.NewWeighted(int64(2))
	aAcquired := make(chan bool, 1)
	bAcquired := make(chan bool, 2)

	fmt.Println("Starting")
	done := make(chan bool)

	r := 100
	
	go func() {
		time.Sleep(time.Duration(r) * time.Microsecond)
		fmt.Println("a1")
		aAcquired <- true
		<- bAcquired
		fmt.Println("a2")
		done <- true
	}()

	go func() {
		time.Sleep(time.Duration(r) * time.Microsecond)
		fmt.Println("b1")
		bAcquired <- true
		<- aAcquired
		fmt.Println("b2")
		done <- true
	}()

	for i:=0; i<2; i++ {
		<- done
	}
	fmt.Println("End")
}
