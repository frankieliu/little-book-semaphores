/*

In this version:
- try to get both
- if can't get both drop any I am holding

*/

package main

import (
	"fmt"
	"sync"
)

func main() {
	var wg sync.WaitGroup

	var fork []int
	var mux []sync.Mutex

	fork = make([]int, 5)
	mux = make([]sync.Mutex, 5)
	for i:= 0; i<5; i++ {
		print(fork[i])
	}
	wg.Add(5)

	fmt.Println("hello")
	
	phil := func(id int) {
		defer wg.Done()
		left := false
		right := false
		
		for i:=0; i<20; i++ {
			fmt.Println(id, "thinking")

			mux[id].Lock()
			if fork[id] == 0 {
				fork[id] = 1
				fmt.Println(id, "got left")
				left = true
			}
			mux[id].Unlock()

			mux[(id+1)%5].Lock()
			if fork[(id+1)%5] == 0 {
				fork[(id+1)%5] = 1
				fmt.Println(id, "got right")
				right = true
			}
			mux[(id+1)%5].Unlock()

			if left && right {
				fmt.Println(id, "eating")
			}
			if left {
				mux[id].Lock()
				fork[id] = 0
				fmt.Println(id, "let got left")
				left = false
				mux[id].Unlock()
			}
			if right {
				mux[(id+1)%5].Lock()
				fork[(id+1)%5] = 0
				fmt.Println(id, "let got right")
				right = false
				mux[(id+1)%5].Unlock()
			}
		}
	}

	for i := 0 ; i<5; i++ {
		go phil(i)
	}
	
	wg.Wait()
}
